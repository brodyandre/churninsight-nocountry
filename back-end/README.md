# ChurnInsight – Previsão de Churn de Clientes

Projeto desenvolvido para o **Hackathon No Country**, com foco em **previsão de cancelamento de clientes (churn)** utilizando **Data Science (Python)** e **Back-end (Java + Spring Boot)**.

## Descrição e Objetivo geral do Projeto
Empresas que trabalham com modelos de assinatura ou contratos recorrentes sofrem com cancelamentos de clientes (churn). Reter clientes é, em geral, mais barato do que adquirir novos.

**Serviços e Assinaturas**, como:

* Telecomunicações
* Fintechs
* Streaming
* E-commerce
* Software por assinatura (SaaS)

Empresas desse setor dependem de **clientes recorrentes** e precisam reduzir **cancelamentos e desistências**.

### Este projeto tem como objetivo:

Prever se um cliente vai cancelar ou vai continuar utilizando o serviço;
Estimar a probabilidade numérica associada a essa previsão;
Disponibilizar essa previsão via uma API REST, permitindo que times de negócio (marketing, suporte, sucesso do cliente) ajam de forma proativa para retenção.

### Objetivo Back-End

Construir uma API para disponibilizar o uso do modelo preditivo para diversos sistemas.

## Visão Geral da Solução

Aplicação Back-End (API REST) do time de Java:

Endpoint que recebe informações de um cliente e retorna a previsão do modelo (Ex.: “Vai cancelar” / “Vai continuar”);

Integração com o modelo de DS (direta ou via microserviço Python);

Logs e tratamento de erros


Receber JSON com dados de cliente e devolver a previsão;


Conectar-se ao modelo do DS:

via microserviço Python (FastAPI/Flask), ou

carregando modelo exportado em formato ONNX (opção mais avançada);

Validar entradas e retornar erros claros quando faltar informação.


## Arquitetura

Arquitetura em camadas MVC
API REST

## Tecnologias e Dependências

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)



## Tecnologias e Dependências

### 🔹 Stack Principal
- Java 21
- Spring Boot 4.0.0
- Maven

### 🔹 Back-end
- Spring Web MVC – API REST
- Spring Data JPA – Persistência
- Spring Validation – Validação de dados
- Lombok – Redução de boilerplate
- Spring Boot DevTools – Desenvolvimento

### 🔹 Banco de Dados
- PostgreSQL
- Flyway – Migração e versionamento do banco de dados

### 🔹 Testes
- Spring Boot Starter Test (Web, JPA e Validation)

### Resumo
Java: 21  
Spring Boot: 4.0.0  
Banco de Dados: PostgreSQL  
Migração: Flyway  
Arquitetura: API REST

## Como Instalar o Projeto

### Pré-Requisitos

Github
Java
IDE para java
Postgres

### Etapas

1. Clone o repositório no endereço 
```bash
https://github.com/brodyandre/churninsight-nocountry.git
```
2. Acesse a pasta do projeto
```bash
cd churninsight-nocountry
```
3. Configure o banco de dados PostgreSQL no arquivo:
```bash
back-end/churn/src/main/resources/application.properties
```

## Como Executar o Projeto

1. Abra o projeto na sua IDE Java
2. Execute a classe principal do back-end
3. A API estará sendo servida em:
```bash
http://localhost:8080
```

## Endpoints da API

### GET /test/example  
Endpoint de teste da API.

### POST /predict  
Recebe dados do cliente e retorna a previsão de churn.


## Exemplo de Requisição

```json
{

"tempo_contrato_meses": 12,

"atrasos_pagamento": 2,

"uso_mensal": 14.5,

"plano": "Premium"

}
```

## Exemplo de Resposta

```json
{

"previsao": "Vai cancelar",

"probabilidade": 0.81

}
```

## Exemplos de Teste
As requisições podem ser testadas utilizando:
- Postman
- Insomnia
- cURL
- Extensões REST no VS Code

## Observações Finais
- O projeto está em desenvolvimento e faz parte de um MVP
- O banco de dados utiliza PostgreSQL com suporte a migrações via Flyway

## Time
Projeto desenvolvido colaborativamente durante o Hackathon ONE (Oracle Next Education) através da plataforma No Country, por alunos das áreas de Data Science e Back-end.
