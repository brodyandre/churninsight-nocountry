<p align="center">
  <img src="docs/logo-churnguard.png" alt="ChurnGuard Analytics" width="440">
</p>

<h1 align="center">ChurnInsight - Previsão de Churn (Hackathon NoCountry)</h1>

<p align="center">
  MVP de previsão de churn com Data Science em Python + API REST em Java (Spring Boot)
</p>

<p align="center">
  <a href="#sumario">Sumário</a> |
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

Documentacao de Data Science: [README_DATA_SCIENCE.md](README_DATA_SCIENCE.md)

Repositório **churninsight-nocountry** - MVP de previsão de churn (cancelamento de clientes) desenvolvido para o **hackathon da plataforma NoCountry**, focado em negócios de **serviços e assinaturas** (Telecom, Fintech, Streaming, E-commerce).

A solução combina:

- **Data Science em Python** para treinar um modelo de classificação binária (vai cancelar / vai continuar);
- **API REST em Java (Spring Boot)** para expor o modelo e permitir o consumo por outros sistemas;
- **UI Web** servida pela API para demonstração funcional do fluxo de previsão.

---

<a id="sumario"></a>
## Sumário

1. [Resumo rápido](#resumo-rapido)
2. [Descrição do desafio (Hackathon NoCountry)](#descricao-do-desafio-hackathon-nocountry)
   - [Setor de negócio](#setor-de-negocio)
   - [Descrição do projeto](#descricao-do-projeto)
   - [Necessidade do cliente (explicação não técnica)](#necessidade-do-cliente-explicacao-nao-tecnica)
   - [Validação de mercado](#validacao-de-mercado)
   - [Expectativa para este hackathon](#expectativa-para-este-hackathon)
   - [Entregáveis desejados](#entregaveis-desejados)
   - [Funcionalidades exigidas (MVP)](#funcionalidades-exigidas-mvp)
   - [Funcionalidades opcionais](#funcionalidades-opcionais)
   - [Orientações técnicas para alunos](#orientacoes-tecnicas-para-alunos)
   - [Contrato de integração (JSON)](#contrato-de-integracao-json)
3. [Visão geral da solução](#visao-geral-da-solucao)
4. [Arquitetura](#arquitetura)
5. [Estrutura do repositório](#estrutura-do-repositorio)
6. [Tecnologias](#tecnologias)
7. [Como executar (local)](#como-executar-local)
   - [Data Science (Python)](#data-science-python)
   - [Microserviço Python (opcional)](#microservico-python-opcional)
   - [API Java (Spring Boot)](#api-java-spring-boot)
   - [UI (Web)](#ui-web)
8. [Docker Compose (UI completa)](#docker-compose-ui-completa)
9. [Endpoints principais](#endpoints-principais)
10. [Dataset e modelo](#dataset-e-modelo)
11. [Testes](#testes)
12. [Troubleshooting](#troubleshooting)
13. [Time](#time)

---

<a id="resumo-rapido"></a>
## Resumo rápido

| Camada | O que entrega |
| --- | --- |
| Data Science (Python) | EDA, features, treino e serialização do modelo |
| Microserviço (FastAPI) | Endpoint `/predict` com modelo carregado |
| API Java (Spring Boot) | Validação, integração DS e UI web |
| UI Web | Formulário, presets e visualização de status |
| Banco (opcional) | Persistência via PostgreSQL |

[Voltar ao Sumário](#sumario)

---

<a id="descricao-do-desafio-hackathon-nocountry"></a>
## Descrição do desafio (Hackathon NoCountry)

<a id="setor-de-negocio"></a>
### Setor de negócio
Serviços e assinaturas (Telecom, Fintech, Streaming, E-commerce) - empresas que dependem de clientes recorrentes e desejam reduzir cancelamentos.

<a id="descricao-do-projeto"></a>
### Descrição do projeto
O desafio do **ChurnInsight** consiste em criar uma solução que preveja se um cliente está propenso a cancelar um serviço (churn). O objetivo é que o time de Data Science desenvolva um modelo preditivo e que o time de Back-end construa uma API para disponibilizar essa previsão a outros sistemas, permitindo que o negócio aja antes que o cliente decida sair.

<a id="necessidade-do-cliente-explicacao-nao-tecnica"></a>
### Necessidade do cliente (explicação não técnica)
Toda empresa que vende por assinatura ou contrato recorrente sofre com cancelamentos. Manter clientes fiéis é mais barato do que conquistar novos. A empresa quer prever antecipadamente quem está prestes a cancelar, para poder agir e reter essas pessoas.

<a id="validacao-de-mercado"></a>
### Validação de mercado
Predição de churn é uma aplicação comum e valiosa da ciência de dados. Empresas de telecom, bancos digitais, academias, streaming e SaaS usam modelos de churn para reduzir perdas financeiras, entender comportamento e aumentar o lifetime value.

<a id="expectativa-para-este-hackathon"></a>
### Expectativa para este hackathon
Público: alunos iniciantes em tecnologia com base em Back-end (Java) e Data Science (Python).  
Objetivo: construir, em grupo, um MVP capaz de prever churn e disponibilizar essa previsão via API funcional.

<a id="entregaveis-desejados"></a>
### Entregáveis desejados
- Notebook (EDA, features, treino, métricas e serialização do modelo).
- API REST em Java com endpoint de previsão.
- Documentação mínima (README com passos de execução e exemplos).
- Demonstração funcional.

<a id="funcionalidades-exigidas-mvp"></a>
### Funcionalidades exigidas (MVP)
- Endpoint `POST /predict` com previsão e probabilidade.
- Carregamento de modelo preditivo.
- Validação de entrada.
- Resposta estruturada (previsão + probabilidade).
- Exemplos de uso.

<a id="funcionalidades-opcionais"></a>
### Funcionalidades opcionais
Stats, persistência, dashboard simples, explicabilidade básica, batch prediction, containerização e testes automatizados.

<a id="orientacoes-tecnicas-para-alunos"></a>
### Orientações técnicas para alunos
Controlar o volume de dados e o uso de recursos (ex.: free tier de cloud).  
Data Science: dataset limpo, modelo simples, features intuitivas, salvar pipeline com `joblib`.  
Back-end: API REST, validação de entrada, integração com modelo (microserviço Python ou ONNX).

<a id="contrato-de-integracao-json"></a>
### Contrato de integração (JSON)
Entrada:
```json
{
  "tempo_contrato_meses": 12,
  "atrasos_pagamento": 2,
  "uso_mensal": 14.5,
  "plano": "Premium"
}
```

Saída:
```json
{
  "previsao": "Vai cancelar",
  "probabilidade": 0.81
}
```

[Voltar ao Sumário](#sumario)

---

<a id="visao-geral-da-solucao"></a>
## Visão geral da solução

A solução é composta por:

- **Data Science**: notebooks em `notebooks/`, dataset em `data/`, modelo serializado em `model/`.
- **Microserviço Python (FastAPI)**: em `ds_service/`, responsável por carregar o modelo e servir `/predict`.
- **API Java (Spring Boot)**: em `back-end/churn/`, expõe endpoints e serve a UI.
- **Banco de dados**: PostgreSQL via Docker (opcional para persistência).
- **UI Web**: página estática servida pelo back-end Java em `http://localhost:8080/`.

[Voltar ao Sumário](#sumario)

---

<a id="arquitetura"></a>
## Arquitetura

```mermaid
graph LR
  UI["UI Web - Spring Boot Static"] --> API["API Java /churn/predict"]
  API --> DS["DS Service - FastAPI"]
  DS --> MODEL["Modelo .pkl/.joblib"]
  API --> DB[(PostgreSQL)]
  NOTE["Notebooks / data"] --> MODEL
```

Fluxo: a UI chama a API Java, que valida dados e delega a previsão ao microserviço Python. O resultado retorna para a UI. A persistência em banco é opcional.

[Voltar ao Sumário](#sumario)

---

<a id="estrutura-do-repositorio"></a>
## Estrutura do repositório

```text
.
|-- back-end/
|   `-- churn/                 # API Java (Spring Boot)
|-- data/
|   `-- raw/                   # Dados brutos
|-- docs/                      # Imagens e documentação
|-- ds_service/                # Microserviço Python (FastAPI)
|-- model/                     # Modelos serializados (.joblib/.pkl)
|-- notebooks/                 # EDA e modelagem
|-- docker-compose.yml
`-- README.md                  # Este arquivo
```

[Voltar ao Sumário](#sumario)

---

<a id="tecnologias"></a>
## Tecnologias

- **Java 21**, **Spring Boot**
- **Python 3.11**, **FastAPI**, **scikit-learn**, **xgboost**
- **PostgreSQL** (opcional)
- **Docker / Docker Compose**

[Voltar ao Sumário](#sumario)

---

<a id="como-executar-local"></a>
## Como executar (local)

<a id="data-science-python"></a>
### Data Science (Python)
Opcional: treinar ou atualizar o modelo.

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r ds_service\requirements.txt
```

<a id="microservico-python-opcional"></a>
### Microserviço Python (opcional)
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

<a id="api-java-spring-boot"></a>
### API Java (Spring Boot)
Defina variáveis de ambiente e rode o back-end:

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry\back-end\churn
$env:DS_SERVICE_URL="http://127.0.0.1:8001"
.\mvnw.cmd spring-boot:run
```

<a id="ui-web"></a>
### UI (Web)
Abra no navegador:
```
http://localhost:8080/
```

[Voltar ao Sumário](#sumario)

---

<a id="docker-compose-ui-completa"></a>
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

Checks rápidos:
```
http://localhost:8080/churn/health
http://localhost:8080/churn/ds-health
```

Logs:
```powershell
docker compose logs -f java-api ds-service
```

[Voltar ao Sumário](#sumario)

---

<a id="endpoints-principais"></a>
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

[Voltar ao Sumário](#sumario)

---

<a id="dataset-e-modelo"></a>
## Dataset e modelo

- Dataset: localizado em `data/` (raw/processed).
- Modelos treinados salvos em `model/`.
- Pipeline utiliza transformações de features e classificador supervisionado.

Se o arquivo do modelo estiver com ~134 bytes, ele é um ponteiro LFS. Use um arquivo real (~500 KB).

[Voltar ao Sumário](#sumario)

---

<a id="testes"></a>
## Testes

A partir da raiz do repositório, rode os testes do back-end Java dentro de `back-end\churn`:

```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry\back-end\churn
.\mvnw.cmd test
```

[Voltar ao Sumário](#sumario)

---

<a id="troubleshooting"></a>
## Troubleshooting

- **503 no /predict**: DS Service sem modelo carregado ou `xgboost` ausente.
- **404 na UI**: back-end não serviu a página estática (ver logs).
- **Falha no modelo**: verificar tamanho do `.pkl` e logs do DS Service.

[Voltar ao Sumário](#sumario)

---

<a id="time"></a>
## Time

- [brodyandre](https://github.com/brodyandre)
- [walkii-dev](https://github.com/walkii-dev)
- [augustoramos000](https://github.com/augustoramos000)

[Voltar ao Sumário](#sumario)
