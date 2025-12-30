"""
FastAPI Microservice para Predição de Churn
Integra o modelo treinado em Python com a API Java
"""

from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional
import joblib
import pandas as pd
import logging
import os
from pathlib import Path

# Configuração de logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Inicializar FastAPI
app = FastAPI(
    title="ChurnInsight - Microserviço de Predição",
    description="Microserviço Python que expõe o modelo de predição de churn",
    version="1.0.0"
)

# CORS - Permitir requisições da API Java
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ============================================================================
# DTOs (Pydantic Models)
# ============================================================================

class ChurnPredictRequest(BaseModel):
    """Request DTO para predição de churn"""
    gender: str = Field(..., description="Gênero: Masculino ou Feminino")
    SeniorCitizen: int = Field(..., ge=0, le=1, description="Idoso: 0 ou 1")
    Partner: str = Field(..., description="Cônjuge: Sim ou Não")
    Dependents: str = Field(..., description="Dependentes: Sim ou Não")
    tenure: int = Field(..., ge=0, le=72, description="Tempo de contrato em meses")
    PhoneService: str = Field(..., description="Serviço de Telefone: Sim ou Não")
    MultipleLines: str = Field(..., description="Múltiplas Linhas")
    InternetService: str = Field(..., description="Serviço de Internet")
    OnlineSecurity: str = Field(..., description="Segurança Online")
    OnlineBackup: str = Field(..., description="Backup Online")
    DeviceProtection: str = Field(..., description="Proteção de Dispositivo")
    TechSupport: str = Field(..., description="Suporte Técnico")
    StreamingTV: str = Field(..., description="TV Streaming")
    StreamingMovies: str = Field(..., description="Filmes Streaming")
    Contract: str = Field(..., description="Tipo de Contrato")
    PaperlessBilling: str = Field(..., description="Fatura Online")
    PaymentMethod: str = Field(..., description="Método de Pagamento")
    MonthlyCharges: float = Field(..., ge=0, description="Valor mensal")
    TotalCharges: float = Field(..., ge=0, description="Valor total")


class ChurnPredictResponse(BaseModel):
    """Response DTO com predição de churn"""
    previsao: str = Field(..., description="Predição: 'Vai cancelar' ou 'Vai continuar'")
    probabilidade: float = Field(..., ge=0, le=1, description="Probabilidade da predição (0-1)")
    confianca: float = Field(..., ge=0, le=1, description="Nível de confiança")


class HealthResponse(BaseModel):
    """Response DTO para health check"""
    status: str
    model_loaded: bool
    service_version: str


# ============================================================================
# Carregamento do Modelo
# ============================================================================

MODEL_PATH = os.getenv("MODEL_PATH", "../model/churn_xgboost_pipeline_tuned.joblib")
modelo = None
model_loaded = False


def load_model():
    """Carrega o modelo treinado"""
    global modelo, model_loaded
    try:
        if os.path.exists(MODEL_PATH):
            data_checkpoint = joblib.load(MODEL_PATH)

            if isinstance(data_checkpoint, dict) and 'model' in data_checkpoint:
                modelo = data_checkpoint['model']
            else:
                modelo = data_checkpoint

            model_loaded = True
            logger.info(f"✓ Modelo carregado com sucesso de {MODEL_PATH}")
        else:
            logger.warning(f"⚠ Arquivo de modelo não encontrado em {MODEL_PATH}")
            model_loaded = False
    except Exception as e:
        logger.error(f"✗ Erro ao carregar modelo: {str(e)}")
        model_loaded = False


# Carregar modelo na inicialização
load_model()


# ============================================================================
# Endpoints
# ============================================================================

@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Verifica o status do microserviço"""
    return HealthResponse(
        status="healthy" if model_loaded else "degraded",
        model_loaded=model_loaded,
        service_version="1.0.0"
    )


@app.post("/predict", response_model=ChurnPredictResponse)
async def predict(request: ChurnPredictRequest):
    """
    Endpoint principal de predição de churn
    
    Recebe dados do cliente e retorna predição de churn com probabilidade.
    """
    if not model_loaded:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Modelo não carregado. Verifique a configuração."
        )
    
    try:
        # Converter request para dicionário
        data_dict = request.dict()
        
        # Converter para DataFrame (mesmo formato usado no treinamento)
        df_input = pd.DataFrame([data_dict])
        
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
        )


@app.get("/")
async def root():
    """Endpoint raiz com informações do serviço"""
    return {
        "service": "ChurnInsight - Microserviço de Predição",
        "version": "1.0.0",
        "status": "running",
        "docs": "/docs",
        "health": "/health",
        "predict_endpoint": "/predict"
    }


# ============================================================================
# Event Handlers
# ============================================================================

@app.on_event("startup")
async def startup_event():
    """Executado na inicialização do microserviço"""
    logger.info("🚀 Inicializando microserviço ChurnInsight...")
    if model_loaded:
        logger.info("✓ Modelo pronto para predições")
    else:
        logger.warning("⚠ Microserviço iniciado mas modelo não está disponível")


@app.on_event("shutdown")
async def shutdown_event():
    """Executado no encerramento do microserviço"""
    logger.info("🛑 Encerrando microserviço ChurnInsight")


if __name__ == "__main__":
    import uvicorn
    
    # Configurações do servidor
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8001"))
    reload = os.getenv("RELOAD", "False").lower() == "true"
    
    logger.info(f"Iniciando servidor em {host}:{port}")
    
    uvicorn.run(
        "main:app",
        host=host,
        port=port,
        reload=reload,
        log_level="info"
    )
