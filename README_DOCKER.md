# Execução local com Docker Compose (ChurnInsight)

Este repositório roda **2 serviços**:

- **ds-service (FastAPI)**: expõe `/predict`, `/health` e carrega o modelo serializado (`joblib`).
- **spring-backend (Spring Boot)**: expõe a UI e APIs `/api/churn/*`, e **faz proxy** para o FastAPI.

## Pré-requisitos
- Docker Desktop (Windows/Mac) ou Docker Engine + Docker Compose (Linux)
- Portas livres: **8000** (FastAPI) e **8080** (Spring)

## Estrutura esperada (resumo)
- `docker-compose.yml` na raiz
- `ds_service/Dockerfile` + `ds_service/requirements.txt`
- `backend/churninsight-backend/Dockerfile`
- `model/` contendo o artefato `.joblib`

## Subir a stack
Na raiz do repo:

```bash
docker compose up --build
```

## Verificar saúde
- FastAPI health: `http://localhost:8000/health`
- Spring health: `http://localhost:8080/api/churn/health`
- DS health via Spring (proxy): `http://localhost:8080/api/churn/ds-health`

## Teste rápido de predição via Spring (recomendado)
```bash
curl -X POST "http://localhost:8080/api/churn/predict" \
  -H "Content-Type: application/json" \
  -d '{"gender":"Female","SeniorCitizen":1,"Partner":"No","Dependents":"No","tenure":2,"PhoneService":"Yes","MultipleLines":"No","InternetService":"Fiber optic","OnlineSecurity":"No","OnlineBackup":"No","DeviceProtection":"No","TechSupport":"No","StreamingTV":"Yes","StreamingMovies":"Yes","Contract":"Month-to-month","PaperlessBilling":"Yes","PaymentMethod":"Electronic check","MonthlyCharges":99.85,"TotalCharges":199.7}'
```

## Dica de troubleshooting
- Se o Spring subir antes do DS, o `depends_on` + healthcheck deve segurar o start.
- Se o DS falhar no import do `xgboost`, verifique se `libgomp1` está instalado (já está no Dockerfile).
- Se o modelo não for encontrado, confirme o caminho esperado no FastAPI (ex.: `/app/model/...joblib`).
