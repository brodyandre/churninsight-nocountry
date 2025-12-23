# DEMO — ChurnInsight (Docker Compose) | Spring Boot + FastAPI

Este arquivo é um roteiro **curto** para demonstrar o projeto para a banca: subir a stack, validar health e executar uma predição fim-a-fim (Spring → FastAPI → modelo).

---

## 0) Pré-requisitos (rápido)

- Docker Desktop em execução
- Portas livres: `8080` (Spring) e `8000` (FastAPI)

---

## 1) Subir a aplicação (1 comando)

Na raiz do repositório:

```bash
docker compose up --build
```
Acesse a UI:

http://localhost:8080/

## 2) Validar integração Spring ↔ DS (health)
2.1 Health do Spring (mostra DS_SERVICE_URL resolvida no Docker)

No PowerShell:
```bash
irm http://localhost:8080/api/churn/health

```

Saída esperada (exemplo):

status: ok

ds_service_url: http://ds-service:8000

## 2.2 Health do FastAPI (ds-service)

```bash
irm http://localhost:8000/health

```

## 3) Smoke test automático (recomendado para a banca)

Executa health + predict e imprime um resultado de predição com probabilidade, threshold e latência:

```bash
.\scripts\smoke_test.ps1

```

Saída esperada (exemplo):

- Spring health OK + ds_service_url

- DS health OK

- Predição OK (previsao/probabilidade/threshold/latencia_ms)

## 4) Predição manual (opcional, se quiser mostrar payload)

```powershell
$payload = [ordered]@{
  gender="Female"
  SeniorCitizen=1
  Partner="No"
  Dependents="No"
  tenure=2
  PhoneService="Yes"
  MultipleLines="No"
  InternetService="Fiber optic"
  OnlineSecurity="No"
  OnlineBackup="No"
  DeviceProtection="No"
  TechSupport="No"
  StreamingTV="Yes"
  StreamingMovies="Yes"
  Contract="Month-to-month"
  PaperlessBilling="Yes"
  PaymentMethod="Electronic check"
  MonthlyCharges=99.85
  TotalCharges=199.7
}

$json = $payload | ConvertTo-Json -Depth 5 -Compress

irm "http://localhost:8080/api/churn/predict" -Method Post -ContentType "application/json" -Body $json

```

## 5) Encerrar a demo

No terminal onde está o compose:

Ctrl + C

Em seguida:

```bash
docker compose down

```
Referências

Guia completo: README_DOCKER.md

Deploy story (OCI/OKE): DEPLOY_STORY_OCI.md