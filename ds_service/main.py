"""
FastAPI Microservice para Predição de Churn
"""

from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import joblib
import pandas as pd
import logging
import os
from pathlib import Path
from typing import Any, Dict, Optional

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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

# =========================
# DTOs
# =========================


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


TRADUCAO_INPUTS = {
    # Gênero
    "Masculino": "Male",
    "Feminino": "Female",

    # Sim / Não genéricos
    "Sim": "Yes",
    "Não": "No",

    # Serviço de internet
    "Fibra Ótica": "Fiber optic",
    "Nenhum": "No",

    # Contrato
    "Mensal": "Month-to-month",
    "Anual": "One year",
    "Bianual": "Two year",

    # Método de pagamento
    "Cartão de crédito": "Credit card (automatic)",
    "Débito em conta": "Bank transfer (automatic)",
    "Ted": "Bank transfer (automatic)",
    "Boleto": "Mailed check",
    "Pix": "Electronic check"
}


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    service_version: str
<<<<<<< Updated upstream
    threshold: Optional[float] = None
    model_path: Optional[str] = None
=======
    modelo_path: Optional[str] = None
    threshold: float = 0.5
>>>>>>> Stashed changes


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


# =========================
# Modelo: caminho correto
# =========================

BASE_DIR = Path(__file__).resolve().parent
DEFAULT_MODEL_PATH = BASE_DIR.parent / "model" / "churn_xgboost_pipeline_tuned.joblib"

MODEL_PATH = Path(os.getenv("MODEL_PATH", str(DEFAULT_MODEL_PATH))).expanduser()
THRESHOLD = _as_float(os.getenv("THRESHOLD", "0.5"), 0.5)

<<<<<<< Updated upstream
MODEL_PATH = os.getenv("MODEL_PATH", "../model/churn_xgboost_pipeline_tuned.joblib")
=======
>>>>>>> Stashed changes
modelo = None
model_metadata = {}
model_loaded = False


<<<<<<< Updated upstream
def load_model():
    """Carrega o modelo treinado"""
    global modelo, model_loaded, model_metadata
    try:
        if os.path.exists(MODEL_PATH):
            data_checkpoint = joblib.load(MODEL_PATH)

            if isinstance(data_checkpoint, dict) and 'model' in data_checkpoint:
                modelo = data_checkpoint['model']
                model_metadata = {k: v for k, v in data_checkpoint.items() if k != 'model'}
            else:
                modelo = data_checkpoint
                model_metadata = {}

=======
def load_model() -> None:
    global modelo, model_loaded
    try:
        if MODEL_PATH.exists():
            modelo = joblib.load(MODEL_PATH)
>>>>>>> Stashed changes
            model_loaded = True
            logger.info(f"✓ Modelo carregado: {MODEL_PATH}")
        else:
            model_loaded = False
            logger.warning(f"⚠ Modelo não encontrado em: {MODEL_PATH}")
    except Exception as e:
        model_loaded = False
        logger.error(f"✗ Erro ao carregar modelo: {e}")


load_model()

# =========================
# (Opcional) Normalização PT -> EN
# =========================

YES_NO = {"Sim": "Yes", "Não": "No", "Yes": "Yes", "No": "No"}

MAPS = {
    "gender": {"Feminino": "Female", "Masculino": "Male", "Female": "Female", "Male": "Male"},
    "Partner": YES_NO,
    "Dependents": YES_NO,
    "PhoneService": YES_NO,
    "PaperlessBilling": YES_NO,
    "MultipleLines": {"Sim": "Yes", "Não": "No", "Sem serviço de telefone": "No phone service",
                      "Yes": "Yes", "No": "No", "No phone service": "No phone service"},
    "InternetService": {"DSL": "DSL", "Fibra Ótica": "Fiber optic", "Nenhum": "No",
                        "Fiber optic": "Fiber optic", "No": "No"},
    "OnlineSecurity": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                       "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "OnlineBackup": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                     "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "DeviceProtection": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                         "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "TechSupport": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                    "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "StreamingTV": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                    "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "StreamingMovies": {"Sim": "Yes", "Não": "No", "Sem serviço de internet": "No internet service",
                        "Yes": "Yes", "No": "No", "No internet service": "No internet service"},
    "Contract": {"Mensal": "Month-to-month", "Anual": "One year", "Bianual": "Two year",
                 "Month-to-month": "Month-to-month", "One year": "One year", "Two year": "Two year"},
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


# =========================
# Endpoints
# =========================

@app.get("/health", response_model=HealthResponse)
async def health_check():
    return HealthResponse(
        status="UP" if model_loaded else "DEGRADED",
        model_loaded=model_loaded,
        service_version="1.0.0",
<<<<<<< Updated upstream
        threshold=model_metadata.get('threshold', 0.5),
        model_path=MODEL_PATH
=======
        modelo_path=str(MODEL_PATH),
        threshold=THRESHOLD,
>>>>>>> Stashed changes
    )

@app.post("/predict", response_model=ChurnPredictResponse)
async def predict(request: ChurnPredictRequest):
    if not model_loaded:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
<<<<<<< Updated upstream
            detail="Modelo não carregado. Verifique a configuração."
        )
    
    try:
        # Converter request para dicionário
        data_dict = request.dict()
        
        data_traduzido = {
            k: (TRADUCAO_INPUTS.get(v, v) if isinstance(v, str) else v)
            for k, v in data_dict.items()
        }

        # Converter para DataFrame (mesmo formato usado no treinamento)
        df_input = pd.DataFrame([data_traduzido])

        logger.info(f"Processando predição para cliente com {df_input.shape[0]} registro(s)")
        
        # Realizar predição
        predicao_numerica = modelo.predict(df_input)[0]
        
        # Obter probabilidades (se o modelo suporta)
        try:
            probabilidades = modelo.predict_proba(df_input)[0]
            # probabilidades[0] = classe 0 (não churn), probabilidades[1] = classe 1 (churn)
            probabilidade_churn = float(probabilidades[1])
        except AttributeError:
            # Se o modelo não tem predict_proba, usar uma abordagem simples
            probabilidade_churn = float(predicao_numerica)
        
        # Converter predição numérica para texto
        previsao_texto = "Vai cancelar" if predicao_numerica == 1 else "Vai continuar"
        
        # Calcular confiança (distância da probabilidade para 0.5)
        confianca = abs(probabilidade_churn - 0.5) * 2
        
        resposta = ChurnPredictResponse(
            previsao=previsao_texto,
            probabilidade=probabilidade_churn,
            confianca=confianca
        )
        
        logger.info(f"✓ Predição realizada: {previsao_texto} (prob: {probabilidade_churn:.2%})")
        
        return resposta
        
    except ValueError as ve:
        logger.error(f"Erro de validação nos dados: {str(ve)}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Erro ao processar dados: {str(ve)}"
        )
    except Exception as e:
        logger.error(f"Erro ao realizar predição: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro interno do servidor: {str(e)}"
=======
            detail=f"Modelo não carregado. MODEL_PATH atual: {MODEL_PATH}",
>>>>>>> Stashed changes
        )

    data = _to_dict(request)
    data = normalize_payload(data)

    try:
        df_input = pd.DataFrame([data])

        pred = modelo.predict(df_input)[0]

        try:
            proba = modelo.predict_proba(df_input)[0]
            prob_churn = float(proba[1])
        except Exception:
            prob_churn = float(pred)

        previsao_texto = "Vai cancelar" if int(pred) == 1 else "Vai continuar"
        confianca = abs(prob_churn - 0.5) * 2

        return ChurnPredictResponse(
            previsao=previsao_texto,
            probabilidade=prob_churn,
            confianca=confianca,
        )

    except Exception as e:
        logger.exception("Erro ao predizer")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e),
        )

@app.get("/")
async def root():
    return {"service": "ChurnInsight DS", "docs": "/docs", "health": "/health", "predict": "/predict"}
