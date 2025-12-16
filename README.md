# ChurnInsight — Previsão de Churn (Hackathon No Country) 🧠📉

![Status](https://img.shields.io/badge/status-MVP-success)
![Python](https://img.shields.io/badge/Python-3.10%2B-blue)
![FastAPI](https://img.shields.io/badge/FastAPI-API%20REST-009688)
![Streamlit](https://img.shields.io/badge/Streamlit-App%20UI-FF4B4B)
![Java](https://img.shields.io/badge/Java-Spring%20Boot-6DB33F)
![ML](https://img.shields.io/badge/ML-XGBoost%20%7C%20RF%20%7C%20CatBoost-orange)

Repositório `churninsight-nocountry` — MVP de **previsão de churn** (cancelamento de clientes) desenvolvido para o **hackathon da No Country**, com foco em negócios de **serviços e assinaturas** (Telecom, Fintech, Streaming, E-commerce).

> **Escopo da Solução proposta (o que funciona ponta a ponta)**  
> Notebook treina e compara **3 modelos** → escolhe o melhor (tuned) → **serializa artefato `.joblib`** → **FastAPI** carrega o artefato e expõe `POST /predict` → **Spring Boot** consome a previsão para integrar com o “produto” (regras de negócio, CRM, automações). 🚀

---

<a id="menu"></a>
## 📌 Sumário

1. [Contexto e objetivo 🎯](#sec-01)  
2. [Visão geral da solução 🧩](#sec-02)  
3. [Arquitetura 🧱](#sec-03)  
4. [Estrutura do repositório 🗂️](#sec-04)  
5. [Dataset utilizado 📦](#sec-05)  
6. [Modelagem, métricas e decisão do MVP 📈](#sec-06)  
7. [Artefatos e serialização 💾](#sec-07)  
8. [Como executar localmente ⚙️](#sec-08)  
9. [Contrato da API (JSON) 🧾](#sec-09)  
10. [Testes rápidos (cURL) 🧪](#sec-10)  
11. [Próximos passos 🔭](#sec-11)  
12. [Time 👥](#sec-12)  

---

<a id="sec-01"></a>
## 1) Contexto e objetivo 🎯

Empresas com **receita recorrente** sofrem impacto direto quando clientes cancelam (churn). Reter clientes costuma ser mais barato do que adquirir novos — portanto, **antecipar risco** permite ações de retenção mais eficientes.

**Objetivos do projeto:**
- Prever se um cliente **vai cancelar** ou **vai continuar**;
- Retornar também uma **probabilidade** (0 a 1) para priorização;
- Disponibilizar a previsão via **API REST**, facilitando consumo por times e sistemas.

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-02"></a>
## 2) Visão geral da solução 🧩

A solução é composta por três blocos principais:

### 🧪 Data Science (Python)
- EDA (análise exploratória), limpeza e preparação de dados;
- Pré-processamento com `ColumnTransformer` + `OneHotEncoder`;
- Treino e comparação de **3 algoritmos**:
  - **XGBoost**
  - **Random Forest**
  - **CatBoost** (opcional, se instalado)
- Tuning com **RandomizedSearchCV** e métrica **PR-AUC**;
- Calibração de **threshold** no conjunto de validação (*thr_VAL*, não fixo em 0.50);
- Serialização do **pipeline/artefato** em `.joblib`.

### 🚀 Serviço do modelo (FastAPI)
- Carrega o artefato serializado (`model/*.joblib`);
- Expõe endpoint principal `POST /predict`;
- Inclui endpoints de demo e documentação (`/`, `/health`, `/docs`, `/redoc`);
- Valida payload com Pydantic e retorna erros de forma controlada (ex.: **HTTP 422**).

### ☕ Back-end (Java + Spring Boot)
- Camada de aplicação/produto:
  - Consome a previsão do microserviço (FastAPI) e aplica regras de negócio;
  - Integra com CRM/automação (ex.: abrir ticket, disparar campanha, priorizar atendimento);
  - Exposição de endpoints e governança (logs, auditoria, autenticação etc.).

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-03"></a>
## 3) Arquitetura 🧱


flowchart LR
  A["Notebook / Python 🧪<br/>EDA + Treino + Tuning"] --> B["Artefato .joblib 📦<br/>(model + preprocess + threshold)"]
  B --> C["FastAPI ds_service 🚀<br/>POST /predict"]
  C -->|HTTP JSON| D["Spring Boot backend ☕<br/>Regras de negócio + Integrações"]
  D --> E["Clientes / Sistemas 🧑‍💻<br/>Front • CRM • Postman"]


**Por que separar assim?**
- **Notebook**: laboratório (experimento, avaliação, decisão técnica).
- **FastAPI**: execução “production-like” do modelo (inferência consistente).
- **Spring Boot**: produto (orquestração e integração no ecossistema do negócio).

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-04"></a>
## 4) Estrutura do repositório 🗂️

Estrutura esperada (pode haver pequenas variações conforme o time organizar):

```text
.
├── data/
│   ├── raw/                      # CSV bruto (dataset)
│   └── processed/                # (opcional) dados tratados
├── notebooks/
│   └── churn_modeling.ipynb      # EDA + treino + tuning + relatório
├── model/
│   ├── churn_xgboost_pipeline.joblib
│   ├── churn_random_forest_pipeline.joblib
│   ├── churn_catboost_pipeline.joblib
│   └── churn_xgboost_pipeline_tuned.joblib
├── ds_service/
│   ├── app.py                    # FastAPI (serviço do modelo)
│   └── static/                   # CSS/landing page (demo)
├── backend/
│   └── ...                       # API Java (Spring Boot)
└── README.md
```

> Dica: se você estiver no VS Code, use `Ctrl+P` e procure por `ds_service/app.py` para abrir o serviço rapidamente.

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-05"></a>
## 5) Dataset utilizado 📦

- Dataset: `WA_Fn-UseC_-Telco-Customer-Churn.csv`
- Local esperado: `data/raw/WA_Fn-UseC_-Telco-Customer-Churn.csv`
- Target: `Churn` (No/Yes)

<details>
  <summary><b>📚 Principais features (exemplos)</b></summary>

- Perfil: `gender`, `SeniorCitizen`, `Partner`, `Dependents`  
- Contrato: `Contract`, `tenure`, `PaperlessBilling`, `PaymentMethod`  
- Serviços: `InternetService`, `OnlineSecurity`, `TechSupport`, `StreamingTV`, etc.  
- Valores: `MonthlyCharges`, `TotalCharges`

</details>

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-06"></a>
## 6) Modelagem, métricas e decisão do MVP 📈

### Por que treinar 3 modelos? 🤖
Testar **algoritmos diferentes** reduz o risco de “apostar no escuro” e aumenta a robustez da decisão:
- Cada modelo aprende padrões de formas distintas;
- Com o **mesmo pré-processamento** e o **mesmo protocolo** (train/val/test estratificado), a comparação é justa;
- A decisão final é baseada em evidência (métricas), não em preferência.

### Métrica principal: PR-AUC (Average Precision) 🧭
Em churn, a classe positiva (cancelamento) costuma ser **minoritária**. Por isso, **PR-AUC** é uma métrica mais informativa do que acurácia pura.

### Resultado dos modelos tunados (teste) 🧪
| Algoritmo | Threshold (VAL) | Accuracy | Precision | Recall | F1 | ROC-AUC | PR-AUC |
|---|---:|---:|---:|---:|---:|---:|---:|
| CatBoost | 0.62 | 0.7828 | 0.5776 | 0.6765 | 0.6232 | 0.8478 | **0.6684** |
| **XGBoost (MVP)** | **0.60** | 0.7793 | 0.5655 | **0.7273** | **0.6363** | 0.8476 | 0.6673 |
| Random Forest | 0.55 | 0.7750 | 0.5600 | 0.7112 | 0.6266 | 0.8442 | 0.6526 |

### Decisão do MVP (produção/demo): XGBoost (Tuned) 🏁
Embora o CatBoost tenha PR-AUC **ligeiramente** maior, a diferença é **marginal** (~0.001). Para churn, priorizamos reduzir **Falsos Negativos** (clientes que cancelam e o modelo não detecta). Nesse critério, o **XGBoost** entregou:
- **Melhor Recall** (captura mais churners);
- **Melhor F1** (equilíbrio entre precision e recall);
- Integração mais direta e estável com o pipeline serializado e a demo via FastAPI.

> **Por que o threshold não é 0.50?**  
> Porque 0.50 é arbitrário. O threshold é calibrado em validação para equilibrar erro e capacidade operacional de retenção (trade-off entre FN e FP).

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-07"></a>
## 7) Artefatos e serialização 💾

Após o treino/tuning, geramos artefatos `.joblib` com:
- Pipeline completo (`preprocess + model`);
- **Threshold calibrado** (`thr_VAL`);
- Lista de features esperadas (contrato de entrada);
- Metadados do tuning (melhores parâmetros, PR-AUC em CV).

**Artefato principal do MVP:**
- `model/churn_xgboost_pipeline_tuned.joblib` ✅

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-08"></a>
## 8) Como executar localmente ⚙️

### 8.1 Pré-requisitos 🧰
- **Python 3.10+**
- (Opcional, recomendado) **Java 17+** para o Spring Boot
- VS Code (recomendado)
- Dataset em `data/raw/`

---

### 8.2 Data Science (Notebook) 🧪
1) (Opcional) Crie e ative um virtualenv:
```bash
python -m venv venv
# Windows (PowerShell)
venv\Scripts\Activate.ps1
# Linux/Mac
source venv/bin/activate
```

2) Instale dependências:
```bash
pip install -r requirements.txt
```

3) Execute o notebook em `notebooks/` e gere/atualize os artefatos em `model/`.

---

### 8.3 Microserviço do modelo (FastAPI) 🚀
1) Na raiz do repositório, rode:
```bash
uvicorn ds_service.app:app --reload --host 0.0.0.0 --port 8000
```

2) Verificações rápidas (no navegador):
- Landing page (demo): `http://localhost:8000/`
- Health check: `http://localhost:8000/health`
- Swagger: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

---

### 8.4 API Java (Spring Boot) ☕
> O Spring Boot atua como “camada de produto” e pode consumir a FastAPI para obter a previsão do modelo.

**Opção A — Maven**
```bash
cd backend
mvn spring-boot:run
```

**Opção B — Maven Wrapper (se existir no projeto)**
```bash
cd backend
./mvnw spring-boot:run
```

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-09"></a>
## 9) Contrato da API (JSON) 🧾

### Endpoint principal (FastAPI)
- `POST /predict`
- Content-Type: `application/json`

#### Payload (exemplo válido)


```json

{

  "gender": "Female",
  "SeniorCitizen": 0,
  "Partner": "Yes",
  "Dependents": "No",
  "tenure": 12,
  "PhoneService": "Yes",
  "MultipleLines": "No",
  "InternetService": "Fiber optic",
  "OnlineSecurity": "No",
  "OnlineBackup": "Yes",
  "DeviceProtection": "No",
  "TechSupport": "No",
  "StreamingTV": "Yes",
  "StreamingMovies": "No",
  "Contract": "Month-to-month",
  "PaperlessBilling": "Yes",
  "PaymentMethod": "Electronic check",
  "MonthlyCharges": 70.35,
  "TotalCharges": 151.65
}
```

#### Resposta (exemplo)
```json
{
  "previsao": "Vai cancelar",
  "probabilidade": 0.93
}
```

#### Erros
- **422 (Unprocessable Entity)**: payload inválido (campo faltando / tipo incorreto) → resposta simplificada: `Inválido`
- **500 (Internal Server Error)**: falha interna (modelo não carregado, artefato ausente etc.)

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-10"></a>
## 10) Testes rápidos (cURL) 🧪

Defina a URL base (bash):
```bash
BASE_URL="http://localhost:8000"
```

### 10.1 🛑 Alto risco
```bash
curl -X POST "$BASE_URL/predict"   -H "Content-Type: application/json"   -d 

'{
    "gender": "Female",
    "SeniorCitizen": 0,
    "Partner": "Yes",
    "Dependents": "No",
    "tenure": 1,
    "PhoneService": "Yes",
    "MultipleLines": "No",
    "InternetService": "Fiber optic",
    "OnlineSecurity": "No",
    "OnlineBackup": "Yes",
    "DeviceProtection": "No",
    "TechSupport": "No",
    "StreamingTV": "Yes",
    "StreamingMovies": "No",
    "Contract": "Month-to-month",
    "PaperlessBilling": "Yes",
    "PaymentMethod": "Electronic check",
    "MonthlyCharges": 99.0,
    "TotalCharges": 99.0
  }'
```

### 10.2 🟢 Baixo risco
```bash
curl -X POST "$BASE_URL/predict"   -H "Content-Type: application/json"   -d 

'{
    "gender": "Male",
    "SeniorCitizen": 0,
    "Partner": "Yes",
    "Dependents": "Yes",
    "tenure": 60,
    "PhoneService": "Yes",
    "MultipleLines": "Yes",
    "InternetService": "DSL",
    "OnlineSecurity": "Yes",
    "OnlineBackup": "Yes",
    "DeviceProtection": "Yes",
    "TechSupport": "Yes",
    "StreamingTV": "No",
    "StreamingMovies": "No",
    "Contract": "Two year",
    "PaperlessBilling": "No",
    "PaymentMethod": "Credit card (automatic)",
    "MonthlyCharges": 29.0,
    "TotalCharges": 1700.0
  }'
```

### 10.3 ⚠️ Inválido (exemplo para 422)
JSON válido (sintaxe), mas com tipo incorreto:
```bash
curl -X POST "$BASE_URL/predict"   -H "Content-Type: application/json"   -d 

'{
    "gender": "Female",
    "SeniorCitizen": 0,
    "Partner": "Yes",
    "Dependents": "No",
    "tenure": "doze",
    "PhoneService": "Yes",
    "MultipleLines": "No",
    "InternetService": "Fiber optic",
    "OnlineSecurity": "No",
    "OnlineBackup": "Yes",
    "DeviceProtection": "No",
    "TechSupport": "No",
    "StreamingTV": "Yes",
    "StreamingMovies": "No",
    "Contract": "Month-to-month",
    "PaperlessBilling": "Yes",
    "PaymentMethod": "Electronic check",
    "MonthlyCharges": 70.35,
    "TotalCharges": 151.65
  }'
```

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-11"></a>
## 11) Próximos passos 🔭

- 🔐 Adicionar autenticação/autorização na camada Spring Boot;
- 📊 Criar monitoramento e logging estruturado (request_id, latência, taxa de erro);
- 🧪 Avaliar calibração adicional do threshold focada em custo (FN vs FP) e capacidade do time;
- 🧠 Explorar interpretabilidade (ex.: SHAP) para justificar previsões;
- ☁️ Deploy (Docker + cloud) para demo pública.

[⬆ Voltar ao Sumário](#menu)

---

<a id="sec-12"></a>
## 12) Time 👥

- Hackathon No Country — equipe de Data Science (ChurnInsight)

> Atualize esta seção com os nomes/links do time conforme o padrão do repositório.

[⬆ Voltar ao Sumário](#menu)
