# ChurnInsight - Microserviço de Predição (FastAPI)

Microserviço Python que expõe o modelo de predição de churn via API REST, permitindo integração com o backend Java.

## 📋 Pré-requisitos

- Python 3.11+
- pip ou conda
- Arquivo do modelo treinado (`modelo_churn_final.pkl`)

## 🚀 Inicialização

### Instalação de Dependências

```bash
cd ds_service
pip install -r requirements.txt
```

### Executar Localmente (Desenvolvimento)

```bash
python main.py
```

Ou com auto-reload:

```bash
RELOAD=True python main.py
```

O servidor estará disponível em `http://localhost:8001`

### Documentação Interativa

- **Swagger UI**: http://localhost:8001/docs
- **ReDoc**: http://localhost:8001/redoc

## 🐳 Usando Docker

### Build da Imagem

```bash
docker build -t churninsight-ds:latest .
```

### Executar Container

```bash
docker run -p 8001:8001 \
  -v $(pwd)/models:/app/models \
  churninsight-ds:latest
```

## 📡 Endpoints

### Health Check

```bash
GET /health
```

**Response:**
```json
{
  "status": "healthy",
  "model_loaded": true,
  "service_version": "1.0.0"
}
```

### Predição de Churn

```bash
POST /predict
Content-Type: application/json
```

**Request Body:**
```json
{
  "gender": "Masculino",
  "SeniorCitizen": 0,
  "Partner": "Sim",
  "Dependents": "Não",
  "tenure": 24,
  "PhoneService": "Sim",
  "MultipleLines": "Sim",
  "InternetService": "Fibra Ótica",
  "OnlineSecurity": "Sim",
  "OnlineBackup": "Sim",
  "DeviceProtection": "Não",
  "TechSupport": "Sim",
  "StreamingTV": "Não",
  "StreamingMovies": "Não",
  "Contract": "Mensal",
  "PaperlessBilling": "Sim",
  "PaymentMethod": "Cartão de crédito",
  "MonthlyCharges": 65.50,
  "TotalCharges": 1571.20
}
```

**Response:**
```json
{
  "previsao": "Vai continuar",
  "probabilidade": 0.15,
  "confianca": 0.7
}
```

## 📦 Estrutura do Projeto

```
ds_service/
├── main.py                  # Aplicação FastAPI
├── requirements.txt         # Dependências Python
├── Dockerfile              # Configuração Docker
├── .env.example            # Variáveis de ambiente (exemplo)
├── README.md               # Este arquivo
└── models/
    └── modelo_churn_final.pkl  # Modelo treinado
```

## 🔧 Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `HOST` | `0.0.0.0` | Host do servidor |
| `PORT` | `8001` | Porta do servidor |
| `MODEL_PATH` | `models/modelo_churn_final.pkl` | Caminho do modelo |
| `RELOAD` | `False` | Auto-reload em desenvolvimento |

## 🔌 Integração com Backend Java

O backend Java (Spring Boot) chama este microserviço via HTTP:

```java
// PredictionService.java
ResponseEntity<PredictDTO> response = restTemplate.postForEntity(
    "http://localhost:8001/predict",
    churnDataDTO,
    PredictDTO.class
);
```

Configure em `application.properties`:
```properties
ds.service.url=http://localhost:8001
```

## 🧪 Testando Localmente

### Com cURL

```bash
curl -X POST http://localhost:8001/predict \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "Masculino",
    "SeniorCitizen": 0,
    "Partner": "Sim",
    "Dependents": "Não",
    "tenure": 24,
    "PhoneService": "Sim",
    "MultipleLines": "Sim",
    "InternetService": "Fibra Ótica",
    "OnlineSecurity": "Sim",
    "OnlineBackup": "Sim",
    "DeviceProtection": "Não",
    "TechSupport": "Sim",
    "StreamingTV": "Não",
    "StreamingMovies": "Não",
    "Contract": "Mensal",
    "PaperlessBilling": "Sim",
    "PaymentMethod": "Cartão de crédito",
    "MonthlyCharges": 65.50,
    "TotalCharges": 1571.20
  }'
```

### Com Python

```python
import requests

data = {
    "gender": "Masculino",
    "SeniorCitizen": 0,
    # ... outros campos
}

response = requests.post(
    "http://localhost:8001/predict",
    json=data
)

print(response.json())
```

## 📊 Logs

O serviço registra informações detalhadas de predições:

```
INFO:     Application startup complete
INFO:     ✓ Modelo carregado com sucesso de models/modelo_churn_final.pkl
INFO:     ✓ Predição realizada: Vai continuar (prob: 85.00%)
```

## ⚠️ Tratamento de Erros

| Status | Descrição |
|--------|-----------|
| `200` | Predição realizada com sucesso |
| `400` | Erro na validação dos dados de entrada |
| `500` | Erro interno do servidor |
| `503` | Modelo não disponível |

## 🤝 Contribuindo

1. Crie uma branch: `git checkout -b feature/seu-feature`
2. Commit suas mudanças: `git commit -am 'Adiciona novo feature'`
3. Push para a branch: `git push origin feature/seu-feature`
4. Abra um Pull Request

## 📝 Licença

Este projeto faz parte do Hackathon No Country.
