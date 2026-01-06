<p align="center">
  <img src="docs/logo-churnguard.png" alt="ChurnGuard Analytics" width="440">
</p>

<h1 align="center">ChurnInsight - PrevisÃ£o de Churn (Hackathon NoCountry)</h1>

<p align="center">
  MVP de previsÃ£o de churn com Data Science em Python + API REST em Java (Spring Boot)
</p>

<p align="center">
  <a href="#sumÃ¡rio">SumÃ¡rio</a> |
  <a href="#como-executar-local">Como executar</a> |
  <a href="#docker-compose-ui-completa">Docker Compose</a> |
  <a href="#endpoints-principais">Endpoints</a>
</p>

<p align="center">
  <a href="https://www.oracle.com/java/">
    <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=coffeescript&logoColor=white" alt="Java 21">
  </a>
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot">
  </a>
  <a href="https://www.python.org/">
    <img src="https://img.shields.io/badge/Python-3.11-3776AB?logo=python&logoColor=white" alt="Python 3.11">
  </a>
  <a href="https://fastapi.tiangolo.com/">
    <img src="https://img.shields.io/badge/FastAPI-0.1xx-009688?logo=fastapi&logoColor=white" alt="FastAPI">
  </a>
  <a href="https://www.docker.com/">
    <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white" alt="Docker Compose">
  </a>
</p>

<p align="center">
  <img src="docs/ui-screenshot.png" alt="UI ChurnInsight" width="900">
</p>

RepositÃ³rio **churninsight-nocountry** - MVP de previsÃ£o de churn (cancelamento de clientes) desenvolvido para o **hackathon da plataforma NoCountry**, focado em negÃ³cios de **serviÃ§os e assinaturas** (Telecom, Fintech, Streaming, E-commerce).

A soluÃ§Ã£o combina:

- **Data Science em Python** para treinar um modelo de classificaÃ§Ã£o binÃ¡ria (vai cancelar / vai continuar);
- **API REST em Java (Spring Boot)** para expor o modelo e permitir o consumo por outros sistemas;
- **UI Web** servida pela API para demonstraÃ§Ã£o funcional do fluxo de previsÃ£o.

---

## SumÃ¡rio

1. [Resumo rÃ¡pido](#resumo-rÃ¡pido)
2. [DescriÃ§Ã£o do desafio (Hackathon NoCountry)](#descriÃ§Ã£o-do-desafio-hackathon-nocountry)
   - [Setor de negÃ³cio](#setor-de-negÃ³cio)
   - [DescriÃ§Ã£o do projeto](#descriÃ§Ã£o-do-projeto)
   - [Necessidade do cliente (explicaÃ§Ã£o nÃ£o tÃ©cnica)](#necessidade-do-cliente-explicaÃ§Ã£o-nÃ£o-tÃ©cnica)
   - [ValidaÃ§Ã£o de mercado](#validaÃ§Ã£o-de-mercado)
   - [Expectativa para este hackathon](#expectativa-para-este-hackathon)
   - [EntregÃ¡veis desejados](#entregÃ¡veis-desejados)
   - [Funcionalidades exigidas (MVP)](#funcionalidades-exigidas-mvp)
   - [Funcionalidades opcionais](#funcionalidades-opcionais)
   - [OrientaÃ§Ãµes tÃ©cnicas para alunos](#orientaÃ§Ãµes-tÃ©cnicas-para-alunos)
   - [Contrato de integraÃ§Ã£o (JSON)](#contrato-de-integraÃ§Ã£o-json)
3. [VisÃ£o geral da soluÃ§Ã£o](#visÃ£o-geral-da-soluÃ§Ã£o)
4. [Arquitetura](#arquitetura)
5. [Estrutura do repositÃ³rio](#estrutura-do-repositÃ³rio)
6. [Tecnologias](#tecnologias)
7. [Como executar (local)](#como-executar-local)
   - [Data Science (Python)](#data-science-python)
   - [MicroserviÃ§o Python (opcional)](#microserviÃ§o-python-opcional)
   - [API Java (Spring Boot)](#api-java-spring-boot)
   - [UI (Web)](#ui-web)
8. [Docker Compose (UI completa)](#docker-compose-ui-completa)
9. [Endpoints principais](#endpoints-principais)
10. [Dataset e modelo](#dataset-e-modelo)
11. [Testes](#testes)
12. [Troubleshooting](#troubleshooting)
13. [Time](#time)

---

## Resumo rÃ¡pido

| Camada | O que entrega |
| --- | --- |
| Data Science (Python) | EDA, features, treino e serializaÃ§Ã£o do modelo |
| MicroserviÃ§o (FastAPI) | Endpoint `/predict` com modelo carregado |
| API Java (Spring Boot) | ValidaÃ§Ã£o, integraÃ§Ã£o DS e UI web |
| UI Web | FormulÃ¡rio, presets e visualizaÃ§Ã£o de status |
| Banco (opcional) | PersistÃªncia via PostgreSQL |

[Voltar ao SumÃ¡rio](#sumÃ¡rio)

---

## DescriÃ§Ã£o do desafio (Hackathon NoCountry)

### Setor de negÃ³cio
ServiÃ§os e assinaturas (Telecom, Fintech, Streaming, E-commerce) - empresas que dependem de clientes recorrentes e desejam reduzir cancelamentos.

### DescriÃ§Ã£o do projeto
O desafio do **ChurnInsight** consiste em criar uma soluÃ§Ã£o que preveja se um cliente estÃ¡ propenso a cancelar um serviÃ§o (churn). O objetivo Ã© que o time de Data Science desenvolva um modelo preditivo e que o time de Back-end construa uma API para disponibilizar essa previsÃ£o a outros sistemas, permitindo que o negÃ³cio aja antes que o cliente decida sair.

### Necessidade do cliente (explicaÃ§Ã£o nÃ£o tÃ©cnica)
Toda empresa que vende por assinatura ou contrato recorrente sofre com cancelamentos. Manter clientes fiÃ©is Ã© mais barato do que conquistar novos. A empresa quer prever antecipadamente quem estÃ¡ prestes a cancelar, para poder agir e reter essas pessoas.

### ValidaÃ§Ã£o de mercado
PrediÃ§Ã£o de churn Ã© uma aplicaÃ§Ã£o comum e valiosa da ciÃªncia de dados. Empresas de telecom, bancos digitais, academias, streaming e SaaS usam modelos de churn para reduzir perdas financeiras, entender comportamento e aumentar o lifetime value.

### Expectativa para este hackathon
PÃºblico: alunos iniciantes em tecnologia com base em Back-end (Java) e Data Science (Python).  
Objetivo: construir, em grupo, um MVP capaz de prever churn e disponibilizar essa previsÃ£o via API funcional.

### EntregÃ¡veis desejados
- Notebook (EDA, features, treino, mÃ©tricas e serializaÃ§Ã£o do modelo).
- API REST em Java com endpoint de previsÃ£o.
- DocumentaÃ§Ã£o mÃ­nima (README com passos de execuÃ§Ã£o e exemplos).
- DemonstraÃ§Ã£o funcional.

### Funcionalidades exigidas (MVP)
- Endpoint `POST /predict` com previsÃ£o e probabilidade.
- Carregamento de modelo preditivo.
- ValidaÃ§Ã£o de entrada.
- Resposta estruturada (previsÃ£o + probabilidade).
- Exemplos de uso.

### Funcionalidades opcionais
Stats, persistÃªncia, dashboard simples, explicabilidade bÃ¡sica, batch prediction, containerizaÃ§Ã£o e testes automatizados.

### OrientaÃ§Ãµes tÃ©cnicas para alunos
Controlar o volume de dados e o uso de recursos (ex.: free tier de cloud).  
Data Science: dataset limpo, modelo simples, features intuitivas, salvar pipeline com `joblib`.  
Back-end: API REST, validaÃ§Ã£o de entrada, integraÃ§Ã£o com modelo (microserviÃ§o Python ou ONNX).

### Contrato de integraÃ§Ã£o (JSON)
Entrada:
```json
{
  "tempo_contrato_meses": 12,
  "atrasos_pagamento": 2,
  "uso_mensal": 14.5,
  "plano": "Premium"
}
```

SaÃ­da:
```json
{
  "previsao": "Vai cancelar",
  "probabilidade": 0.81
}
```

[Voltar ao Sumário](#Sumário)

---

## VisÃ£o geral da soluÃ§Ã£o

A soluÃ§Ã£o Ã© composta por:

- **Data Science**: notebooks em `notebooks/`, dataset em `data/`, modelo serializado em `model/`.
- **MicroserviÃ§o Python (FastAPI)**: em `ds_service/`, responsÃ¡vel por carregar o modelo e servir `/predict`.
- **API Java (Spring Boot)**: em `back-end/churn/`, expÃµe endpoints e serve a UI.
- **Banco de dados**: PostgreSQL via Docker (opcional para persistÃªncia).
- **UI Web**: pÃ¡gina estÃ¡tica servida pelo back-end Java em `http://localhost:8080/`.

[Voltar ao Sumário](#Sumário)

---

## Arquitetura

```mermaid
graph LR
  UI["UI Web - Spring Boot Static"] --> API["API Java /churn/predict"]
  API --> DS["DS Service - FastAPI"]
  DS --> MODEL["Modelo .pkl/.joblib"]
  API --> DB[(PostgreSQL)]
  NOTE["Notebooks / data"] --> MODEL
```

Fluxo: a UI chama a API Java, que valida dados e delega a previsÃ£o ao microserviÃ§o Python. O resultado retorna para a UI. A persistÃªncia em banco Ã© opcional.

[Voltar ao Sumário](#Sumário)

---

## Estrutura do repositÃ³rio

```text
.
|-- back-end/
|   `-- churn/                 # API Java (Spring Boot)
|-- backend/                   # Pasta legado (se aplicÃ¡vel)
|-- data/
|   `-- raw/                   # Dados brutos
|-- ds_service/                # MicroserviÃ§o Python (FastAPI)
|-- model/                     # Modelos serializados (.joblib/.pkl)
|-- notebooks/                 # EDA e modelagem
|-- docker-compose.yml
`-- README.md                  # Este arquivo
```

[Voltar ao Sumário](#Sumário)

---

## Tecnologias

- **Java 21**, **Spring Boot**
- **Python 3.11**, **FastAPI**, **scikit-learn**, **xgboost**
- **PostgreSQL** (opcional)
- **Docker / Docker Compose**

[Voltar ao Sumário](#Sumário)

---

## Como executar (local)

### Data Science (Python)
Opcional: treinar ou atualizar o modelo.

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r ds_service\requirements.txt
```

### MicroserviÃ§o Python (opcional)
Garanta o modelo no caminho esperado:

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry
mkdir ds_service\models -Force
Copy-Item .\model\churn_xgboost_pipeline_tuned.joblib .\ds_service\models\modelo_churn_final.pkl -Force
```

Inicie o FastAPI:
```powershell
cd ds_service
$env:MODEL_PATH="C:\Users\USER\Documents\Repositorios\churninsight-nocountry\ds_service\models\modelo_churn_final.pkl"
uvicorn main:app --reload --host 127.0.0.1 --port 8001
```

### API Java (Spring Boot)
Defina variaveis de ambiente e rode o back-end:

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry\back-end\churn
$env:DS_SERVICE_URL="http://127.0.0.1:8001"
.\mvnw.cmd spring-boot:run
```

### UI (Web)
Abra no navegador:
```
http://localhost:8080/
```

[Voltar ao Sumário](#Sumário)

---

## Docker Compose (UI completa)

Este modo sobe **PostgreSQL + Java API + DS Service**.

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry
docker compose up -d --build
```

UI:
```
http://localhost:8080/
```

Checks rÃ¡pidos:
```
http://localhost:8080/churn/health
http://localhost:8080/churn/ds-health
```

Logs:
```powershell
docker compose logs -f java-api ds-service
```

[Voltar ao Sumário](#Sumário)

---

## Endpoints principais

API Java:
- `GET /churn/health`
- `GET /churn/ds-health`
- `POST /churn/predict`

Exemplo de request:
```json
{
  "gender": "Masculino",
  "seniorCitizen": 0,
  "partner": "Sim",
  "dependents": "Sim",
  "tenure": 60
}
```

[Voltar ao Sumário](#Sumário)

---

## Dataset e modelo

- Dataset: localizado em `data/` (raw/processed).
- Modelos treinados salvos em `model/`.
- Pipeline utiliza transformacoes de features e classificador supervisionado.

Se o arquivo do modelo estiver com ~134 bytes, ele e um ponteiro LFS. Use um arquivo real (~500 KB).

[Voltar ao Sumário](#Sumário)

---

## Testes

Execute os testes automatizados do back-end (unitÃ¡rios e integraÃ§Ã£o) usando o Maven Wrapper do projeto. Rode a partir da raiz do repositÃ³rio: `C:\Users\USER\Documents\Repositorios\churninsight-nocountry`.

```powershell
cd back-end\churn
.\mvnw.cmd test
```

[Voltar ao Sumário](#Sumário)

---

## Troubleshooting

- **503 no /predict**: DS Service sem modelo carregado ou `xgboost` ausente.
- **404 na UI**: back-end nÃ£o serviu a pÃ¡gina estÃ¡tica (ver logs).
- **Falha no modelo**: verificar tamanho do `.pkl` e logs do DS Service.

[Voltar ao Sumário](#Sumário)

---

## Time

Contribuidores do hackathon (NoCountry). Veja o histÃ³rico de commits no GitHub.

- [brodyandre](https://github.com/brodyandre)
- [walkii-dev](https://github.com/walkii-dev)
- [augustoramos000](https://github.com/augustoramos000)

[Voltar ao Sumário](#Sumário)
