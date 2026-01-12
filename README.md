# ChurnInsight – Previsão de Cancelamento de Clientes (Hackathon No Country)

Repositório `churninsight-nocountry` — MVP de previsão de churn (cancelamento de clientes) desenvolvido para o **hackaton da plataforma No Country**, com foco em negócios de **serviços e assinaturas** (Telecom, Fintech, Streaming, E-commerce).

A solução combina:

- **Data Science em Python** para treinar um modelo de classificação binária (`vai cancelar` / `vai continuar`);  
- **API REST em Java (Spring Boot)** para expor o modelo e permitir o consumo por outros sistemas.

---

## 📌 Sumário

1. [Contexto e objetivo](#contexto-e-objetivo)  
2. [Visão geral da solução](#visão-geral-da-solução)  
3. [Arquitetura](#arquitetura)  
4. [Dataset utilizado](#dataset-utilizado)  
5. [Tecnologias](#tecnologias)  
6. [Como Executar](#como-executar)  
   - [Data Science (Python)](#data-science-python)  
   - [Microserviço Python (opcional)](#microserviço-python-opcional)  
   - [API Java (Spring Boot)](#api-java-spring-boot)  
7. [Contrato da API / JSON](#contrato-da-api--json)  
8. [Métricas do modelo](#métricas-do-modelo)  
9. [Próximos passos](#próximos-passos)  
10. [Time](#time)  

---

## Contexto e objetivo

Empresas que trabalham com **modelos de assinatura ou contratos recorrentes** sofrem com cancelamentos de clientes (churn). Reter clientes é, em geral, **mais barato** do que adquirir novos.

Este projeto tem como objetivo:

- Prever se um cliente **vai cancelar** ou **vai continuar** utilizando o serviço;
- Estimar a **probabilidade numérica** associada a essa previsão;
- Disponibilizar essa previsão via uma **API REST**, permitindo que times de negócio (marketing, suporte, sucesso do cliente) ajam de forma **proativa** para retenção.


[Voltar ao Sumário](#-sumário)

---



## Visão geral da solução

A solução está dividida em dois grandes componentes:

1. **Data Science (Python)**  
   - Exploração e limpeza dos dados (EDA);  
   - Engenharia de features (tempo de contrato, uso, tipo de plano etc.);  
   - Treinamento de modelos supervisionados (ex.: Logistic Regression, Random Forest);  
   - Avaliação com métricas como Acurácia, Precisão, Recall e F1-score;  
   - Serialização do melhor modelo em um arquivo (`.joblib` / `.pkl`).

2. **Back-end (Java + Spring Boot)**  
   - API REST com endpoint principal `POST /predict`;  
   - Recebe um JSON com os dados do cliente e retorna:  
     - `previsao`: texto (`"Vai cancelar"` / `"Vai continuar"`);  
     - `probabilidade`: número entre 0 e 1;  
   - Validação de entrada e tratamento de erros;  
   - Integração com o modelo de Data Science (via microserviço Python ou modelo carregado).


[Voltar ao Sumário](#-sumário)

---

## Arquitetura

Fluxo simplificado:

1. Um sistema cliente (Postman, front-end, outro serviço) faz uma requisição `POST /predict` para a API.  
2. A API Java:
   - Valida a estrutura e os tipos dos campos recebidos;
   - Converte o JSON para o formato esperado pelo modelo;
   - Envia os dados para:
     - um **microserviço Python** (FastAPI/Flask) que carrega o modelo e executa `predict`, ou  
     - um modelo carregado diretamente (ex.: via ONNX, opção mais avançada);
   - Recebe a previsão e a probabilidade;  
   - Retorna uma resposta JSON estruturada ao cliente.


[Voltar ao Sumário](#-sumário)

---

## Estrutura de pastas sugerida no repositório

```text
.
├── data/
│   ├── raw/               # Dados brutos (ex.: CSV do Kaggle)
│   └── processed/         # Dados tratados / features
├── notebooks/             # Notebooks de EDA e modelagem
├── model/                 # Arquivos de modelo serializado (.joblib, .pkl)
├── ds_service/            # (Opcional) Microserviço Python (FastAPI/Flask)
├── backend/               # API Java + Spring Boot
├── docs/                  # Diagramas, imagens, anotações
└── README.md
```

## Dataset utilizado





[Voltar ao Sumário](#-sumário)

---
## Tecnologias





[Voltar ao Sumário](#-sumário)

---
## Como Executar
### Data Science (Python)





[Voltar ao Sumário](#-sumário)

### API Java (Spring Boot)
   




[Voltar ao Sumário](#-sumário)

### Microserviço Python (opcional)





[Voltar ao Sumário](#-sumário)

---
## Contrato da API / JSON





[Voltar ao Sumário](#-sumário)

---
## Métricas do modelo





[Voltar ao Sumário](#-sumário)

---
## Próximos Passos





[Voltar ao Sumário](#-sumário)

---
## Time





[Voltar ao Sumário](#-sumário)

---