"""
FastAPI Microservice para Predição de Churn (ChurnInsight)

- Carrega pipeline .joblib (sklearn) treinado
- Normaliza entradas PT -> EN (compatível com Telco Churn dataset)
- Expõe endpoints: /, /health, /predict
"""

from __future__ import annotations

import logging
import os
from pathlib import Path
from typing import Any, Dict, Optional

import joblib
import pandas as pd
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# -----------------------------------------------------------------------------
# Logging
# -----------------------------------------------------------------------------
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("churn_ds")

# -----------------------------------------------------------------------------
# App
# -----------------------------------------------------------------------------
app = FastAPI(
    title="ChurnInsight - Microserviço de Predição",
    description="Microserviço Python que expõe o modelo de predição de churn",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -----------------------------------------------------------------------------
# DTOs
# -----------------------------------------------------------------------------
class ChurnPredictRequest(BaseModel):
    gender: str
    SeniorCitizen: int = Field(..., ge=0, le=1)
    Partner: str
    Dependents: str
    tenure: int = Field(..., ge=0, le=72)
    PhoneService: str
    MultipleLines: str
    InternetService: str
    OnlineSecurity: str
    OnlineBackup: str
    DeviceProtection: str
    TechSupport: str
    StreamingTV: str
    StreamingMovies: str
    Contract: str
    PaperlessBilling: str
    PaymentMethod: str
    MonthlyCharges: float = Field(..., ge=0)
    TotalCharges: float = Field(..., ge=0)


class ChurnPredictResponse(BaseModel):
    previsao: str
    probabilidade: float = Field(..., ge=0, le=1)
    confianca: float = Field(..., ge=0, le=1)


class HealthResponse(BaseModel):
    # Evita warning do Pydantic v2: "model_loaded" conflita com namespace "model_"
    model_config = {"protected_namespaces": ()}

    status: str
    model_loaded: bool
    service_version: str
    threshold: float
    modelo_path: str


# -----------------------------------------------------------------------------
# Helpers
# -----------------------------------------------------------------------------
def _to_dict(model: BaseModel) -> Dict[str, Any]:
    # compatível com pydantic v1/v2
    if hasattr(model, "model_dump"):
        return model.model_dump()
    return model.dict()


def _as_float(value: str, default: float) -> float:
    try:
        return float(str(value).replace(",", "."))
    except Exception:
        return default


def _looks_like_lfs_pointer(path: Path) -> bool:
    """
    Detecta arquivo 'ponteiro' do Git LFS (texto começando com 'version https://git-lfs...')
    """
    try:
        with path.open("rb") as f:
            head = f.read(80)
        return b"version https://git-lfs.github.com/spec/v1" in head
    except Exception:
        return False


# -----------------------------------------------------------------------------
# Normalização PT -> EN (para o modelo Telco)
# -----------------------------------------------------------------------------
YES_NO = {"Sim": "Yes", "Não": "No", "Yes": "Yes", "No": "No"}

MAPS: Dict[str, Dict[str, str]] = {
    "gender": {
        "Feminino": "Female",
        "Masculino": "Male",
        "Female": "Female",
        "Male": "Male",
    },
    "Partner": YES_NO,
    "Dependents": YES_NO,
    "PhoneService": YES_NO,
    "PaperlessBilling": YES_NO,
    "MultipleLines": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de telefone": "No phone service",
        "Yes": "Yes",
        "No": "No",
        "No phone service": "No phone service",
    },
    "InternetService": {
        "DSL": "DSL",
        "Fibra Ótica": "Fiber optic",
        "Nenhum": "No",
        "Fiber optic": "Fiber optic",
        "No": "No",
    },
    "OnlineSecurity": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "OnlineBackup": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "DeviceProtection": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "TechSupport": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "StreamingTV": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "StreamingMovies": {
        "Sim": "Yes",
        "Não": "No",
        "Sem serviço de internet": "No internet service",
        "Yes": "Yes",
        "No": "No",
        "No internet service": "No internet service",
    },
    "Contract": {
        "Mensal": "Month-to-month",
        "Anual": "One year",
        "Bianual": "Two year",
        "Month-to-month": "Month-to-month",
        "One year": "One year",
        "Two year": "Two year",
    },
    "PaymentMethod": {
        "Boleto": "Mailed check",
        "Débito em conta": "Bank transfer (automatic)",
        "Ted": "Bank transfer (automatic)",
        "Cartão de crédito": "Credit card (automatic)",
        "Pix": "Electronic check",
        "Mailed check": "Mailed check",
        "Bank transfer (automatic)": "Bank transfer (automatic)",
        "Credit card (automatic)": "Credit card (automatic)",
        "Electronic check": "Electronic check",
    },
}


def normalize_payload(data: Dict[str, Any]) -> Dict[str, Any]:
    out = dict(data)
    for k, v in data.items():
        if k in MAPS and isinstance(v, str):
            out[k] = MAPS[k].get(v, v)
    return out


# -----------------------------------------------------------------------------
# Modelo
# -----------------------------------------------------------------------------
BASE_DIR = Path(__file__).resolve().parent
DEFAULT_MODEL_PATH = (BASE_DIR.parent / "model" / "churn_xgboost_pipeline_tuned.joblib").resolve()

MODEL_PATH = Path(os.getenv("MODEL_PATH", str(DEFAULT_MODEL_PATH))).expanduser().resolve()
THRESHOLD = _as_float(os.getenv("THRESHOLD", "0.5"), 0.5)

modelo = None
model_loaded = False


def load_model() -> None:
    global modelo, model_loaded

    try:
        if not MODEL_PATH.exists():
            model_loaded = False
            logger.warning(f"⚠ Modelo não encontrado em: {MODEL_PATH}")
            return

        if _looks_like_lfs_pointer(MODEL_PATH):
            model_loaded = False
            logger.error(
                "✗ O arquivo do modelo ainda é um POINTER do Git LFS (não é binário). "
                "Rode: git lfs pull && git lfs checkout model/churn_xgboost_pipeline_tuned.joblib"
            )
            return

        modelo = joblib.load(MODEL_PATH)
        model_loaded = True
        logger.info(f"✓ Modelo carregado: {MODEL_PATH}")

    except Exception:
        model_loaded = False
        logger.exception("✗ Erro ao carregar modelo (joblib.load falhou)")


# Carrega ao iniciar o módulo
load_model()


# -----------------------------------------------------------------------------
# Endpoints
# -----------------------------------------------------------------------------
@app.get("/", response_model=None)
async def root():
    return {
        "service": "ChurnInsight DS",
        "version": "1.0.0",
        "docs": "/docs",
        "health": "/health",
        "predict": "/predict",
        "model_path": str(MODEL_PATH),
    }


@app.get("/health", response_model=HealthResponse)
async def health_check():
    return HealthResponse(
        status="UP" if model_loaded else "DEGRADED",
        model_loaded=model_loaded,
        service_version="1.0.0",
        threshold=THRESHOLD,
        modelo_path=str(MODEL_PATH),
    )


@app.post("/predict", response_model=ChurnPredictResponse)
async def predict(request: ChurnPredictRequest):
    if not model_loaded:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"Modelo não carregado. MODEL_PATH atual: {MODEL_PATH}",
        )

    data = normalize_payload(_to_dict(request))

    try:
        df_input = pd.DataFrame([data])

        # Probabilidade (preferencial) -> controla threshold
        prob_churn: Optional[float] = None
        try:
            proba = modelo.predict_proba(df_input)[0]
            prob_churn = float(proba[1])
        except Exception:
            prob_churn = None

        if prob_churn is not None:
            pred = 1 if prob_churn >= THRESHOLD else 0
        else:
            pred = int(modelo.predict(df_input)[0])
            prob_churn = float(pred)

        previsao_texto = "Vai cancelar" if int(pred) == 1 else "Vai continuar"
        confianca = abs(float(prob_churn) - 0.5) * 2

        return ChurnPredictResponse(
            previsao=previsao_texto,
            probabilidade=float(prob_churn),
            confianca=float(confianca),
        )

    except Exception as e:
        logger.exception("Erro ao predizer")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e),
        )
