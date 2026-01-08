<a id="topo"></a>
# 🚀 ChurnInsight – Backend (Hackathon)

API backend desenvolvida para o projeto **ChurnInsight**, criado no contexto de um **hackathon**, com o objetivo de fornecer uma base sólida, escalável e bem estruturada para análise e previsão de *churn* (evasão de clientes).

Este backend foi construído seguindo boas práticas de arquitetura em camadas, separação de responsabilidades e foco em manutenibilidade, servindo como base para integração com frontend, pipelines de dados e possíveis modelos preditivos.

---

## 📌 Sumário

* [Visão Geral](#visao-geral)
* [Objetivos do Projeto](#objetivos-do-projeto)
* [Arquitetura e Padrões](#arquitetura-e-padroes)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Camadas da Aplicação](#camadas-da-aplicacao)
* [Configurações e Recursos](#configuracoes-e-recursos)
* [Banco de Dados e Migrações](#banco-de-dados-e-migracoes)
* [Schema do Banco de Dados](#schema-do-banco-de-dados)
* [Fluxo da Predição](#fluxo-da-predicao)
* [Endpoints](#endpoints)
* [Códigos de Erro Esperados](#codigos-de-erro-esperados)
* [Testes](#testes)
* [Como Executar o Projeto](#como-executar-o-projeto)
* [Boas Práticas Adotadas](#boas-praticas-adotadas)
* [Decisões Técnicas & Tradeoffs](#decisoes-tecnicas-tradeoffs)
* [Próximos Passos](#proximos-passos)
* [Licença de Uso](#licenca-de-uso)
* [Equipe](#equipe)

---
<a id="visao-geral"></a>
## 🧠 Visão Geral

O **ChurnInsight Backend** é responsável por centralizar as regras de negócio, persistência de dados e exposição de endpoints que suportam análises relacionadas à **predição de churn de clientes**.

O projeto integra um **modelo de Machine Learning baseado em XGBoost**, permitindo que dados históricos sejam processados e avaliados para estimar a probabilidade de churn. Esses resultados são disponibilizados via API para consumo por um **frontend simples**, incluído no próprio backend, facilitando demonstrações rápidas para a banca técnica.

A aplicação foi estruturada para equilibrar **velocidade de entrega**, **qualidade técnica** e **clareza arquitetural**, características essenciais em um ambiente de hackathon.

🔝 [Voltar ao topo](#topo)

---
<a id="objetivos-do-projeto"></a>
## 🎯 Objetivos do Projeto

* Fornecer uma API organizada e extensível
* Centralizar regras de negócio relacionadas a churn
* Facilitar integração com frontend e outras aplicações
* Permitir evolução futura para uso de modelos de Machine Learning
* Demonstrar domínio técnico e boas práticas em um hackathon

🔝 [Voltar ao topo](#topo)

---
<a id="arquitetura-e-padroes"></a>
## 🧩 Arquitetura e Padrões

O projeto segue uma **arquitetura em camadas**, inspirada em padrões amplamente utilizados em aplicações Java com Spring:

* Separação clara entre **Controller**, **Service** e **Repository**
* Uso de **DTOs** para comunicação externa
* **Validações centralizadas**
* Tratamento de exceções desacoplado

Essa abordagem facilita manutenção, testes e escalabilidade.

🔝 [Voltar ao topo](#topo)

---
<a id="tecnologias-utilizadas"></a>
## 🛠 Tecnologias Utilizadas

* **Java**
* **Spring Boot** (framework principal)
* **Spring Data JPA** (persistência)
* **Maven** (gerenciamento de dependências)
* **Banco de Dados Relacional** (via JPA)
* **Flyway** (migrações de banco de dados)
* **XGBoost** (modelo de predição de churn)
* **JUnit / Mockito** (testes)
* **HTML / CSS / JavaScript** (frontend simples para visualização)
* **Swagger (OpenAPI)** (documentação interativa da API REST)

🔝 [Voltar ao topo](#topo)

---
<a id="estrutura-do-projeto"></a>
## 📂 Estrutura do Projeto

```
churn
└── src
    ├── main
    │   ├── java
    │   │   └── nocountry.churninsight.churn
    │   │       ├── config
    │   │       ├── controller
    │   │       ├── dto
    │   │       ├── exception
    │   │       ├── model
    │   │       ├── repository
    │   │       ├── service
    │   │       └── validator
    │   └── resources
    │       ├── db/migrations
    │       ├── presets
    │       └── static
    └── test
        └── java
            └── nocountry.churninsight.churn
                ├── controller
                ├── dto
                ├── repository
                ├── service
                └── validator
```

🔝 [Voltar ao topo](#topo)

---
<a id="camadas-da-aplicacao"></a>
## 🧱 Camadas da Aplicação

### Controller

Responsável por expor os endpoints da API e receber requisições HTTP. Atua como camada de entrada, delegando a lógica de negócio para os serviços.

---

### Service

Contém a **regra de negócio** da aplicação. Orquestra chamadas aos repositórios, validações e a **integração com o modelo de Machine Learning (XGBoost)**.

É nesta camada que ocorre a preparação dos dados de entrada, chamada ao modelo preditivo e tratamento dos resultados antes de retorná-los à camada de controller.

---

### Repository

Camada responsável pela comunicação com o banco de dados, utilizando abstrações do Spring Data JPA.

---

### Model

Representa as entidades do domínio e o mapeamento das tabelas do banco de dados.

---

### DTO

Objetos de transferência de dados usados para isolar o domínio interno da representação externa da API.

---

### Validator

Centraliza regras de validação, garantindo consistência e reutilização.

---

### Exception

Tratamento centralizado de exceções para padronizar respostas de erro da API.

🔝 [Voltar ao topo](#topo)

---
<a id="configuracoes-e-recursos"></a>
## ⚙️ Configurações e Recursos

A pasta `config` contém classes responsáveis por configurações globais da aplicação, como:

* Configuração de beans
* Integração com o modelo de Machine Learning
* CORS e configurações web

Os recursos estáticos ficam organizados em:

```
src/main/resources/static
```

Esse frontend simples permite:

* Envio de dados para predição
* Visualização direta dos resultados de churn
* Demonstração rápida da solução

🔝 [Voltar ao topo](#topo)

---
<a id="banco-de-dados-e-migracoes"></a>
## 🗄 Banco de Dados e Migrações

As migrações de banco de dados são gerenciadas via **Flyway**, localizadas em:

```
src/main/resources/db/migrations
```

Isso garante versionamento, reprodutibilidade do schema e facilidade de evolução do modelo de dados ao longo do hackathon.

🔝 [Voltar ao topo](#topo)

---
<a id="schema-do-banco-de-dados"></a>
## 🧩 Schema do Banco de Dados

O schema abaixo representa o modelo real do banco de dados utilizado pelo ChurnInsight Backend, alinhado às *features* consumidas pelo modelo de predição **XGBoost**.

---

### 👤 Tabela: `clientes`

Armazena os dados cadastrais e comportamentais dos clientes, utilizados como entrada para o modelo de Machine Learning.

| Campo | Tipo | Descrição |
|------|------|-----------|
| id | BIGSERIAL | Identificador único do cliente |
| genero | genero_enum | Gênero do cliente |
| idoso | VARCHAR(3) | Indica se o cliente é idoso |
| conjuge | VARCHAR(3) | Possui cônjuge |
| dependentes | VARCHAR(3) | Possui dependentes |
| tipo_contrato | tipo_contrato_enum | Tipo de contrato |
| tempo_contrato | INTEGER | Tempo de contrato (em meses) |
| servico_telefone | VARCHAR(3) | Possui serviço de telefone |
| multiplas_linhas_tel | VARCHAR(3) | Possui múltiplas linhas telefônicas |
| servico_internet | servico_internet_enum | Tipo de serviço de internet |
| seguranca_online | VARCHAR(3) | Possui segurança online |
| backup_online | VARCHAR(3) | Possui backup online |
| protecao_dispositivo | VARCHAR(3) | Possui proteção de dispositivo |
| suporte_tecnico_tel | VARCHAR(3) | Possui suporte técnico telefônico |
| tv_streaming | VARCHAR(3) | Possui serviço de TV por streaming |
| filmes_streaming | VARCHAR(3) | Possui serviço de filmes por streaming |
| fatura_online | VARCHAR(3) | Utiliza fatura online |
| metodo_pagamento | metodo_pagamento_enum | Método de pagamento |
| valor_mensal | FLOAT | Valor mensal cobrado |
| valor_total | FLOAT | Valor total acumulado |
| data_criacao | TIMESTAMPTZ | Data de criação do registro |

---

### 📈 Tabela: `previsoes`

Armazena o histórico de predições de churn geradas pelo modelo **XGBoost**.

| Campo | Tipo | Descrição |
|------|------|-----------|
| id | BIGSERIAL | Identificador da predição |
| cliente_id | BIGINT | Referência ao cliente |
| previsao | VARCHAR(50) | Classe prevista (ex: CHURN / NO_CHURN) |
| probabilidade | DOUBLE | Probabilidade associada à predição |
| data_criacao | TIMESTAMPTZ | Data da predição |

---

### 🔗 Relacionamentos

- Um **cliente** pode possuir **múltiplas previsões** ao longo do tempo
- `previsoes.cliente_id` referencia `clientes.id`

🔝 [Voltar ao topo](#topo)

---
<a id="fluxo-da-predicao"></a>
## 🔁 Fluxo da Predição (End-to-End)

Abaixo está o fluxo completo da predição de churn, desde a entrada do usuário até a resposta final:

```
[Frontend (static)]
        |
        | 1. Envio de dados do cliente (HTTP POST)
        v
[Controller]
        |
        | 2. Validação de payload (DTO + Validator)
        v
[Service]
        |
        | 3. Pré-processamento dos dados
        | 4. Chamada ao modelo XGBoost
        v
[Modelo de ML]
        |
        | 5. Retorno da probabilidade de churn
        v
[Service]
        |
        | 6. Pós-processamento / regras de negócio
        v
[Controller]
        |
        | 7. Resposta JSON padronizada
        v
[Frontend / Cliente]
```

Esse fluxo evidencia a separação clara de responsabilidades e a integração real entre backend e Machine Learning.

🔝 [Voltar ao topo](#topo)

---
<a id="endpoints"></a>
## 📡 Endpoints

> **Observação:** O endpoint abaixo representa a estrutura conceitual utilizada pelo projeto para integração com o modelo XGBoost.

### `POST /churn/predict`

#### Exemplo de Request

```json
{
  "customerId": "12345",
  "tenure": 12,
  "monthlyCharges": 89.90,
  "totalCharges": 1050.30,
  "contractType": "MONTHLY",
  "paymentMethod": "CREDIT_CARD",
  "hasInternetService": true
}
```

#### Exemplo de Response (Sucesso)

```json
{
  "customerId": "12345",
  "churnProbability": 0.82,
  "churnPrediction": true,
  "model": "XGBoost",
  "confidence": "HIGH"
}
```

---
<a id="codigos-de-erro-esperados"></a>
### ❌ Códigos de Erro Esperados

#### `400 Bad Request`

Payload inválido ou dados inconsistentes enviados pelo cliente.

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Campos obrigatórios ausentes ou inválidos",
  "details": ["tenure", "monthlyCharges"]
}
```

#### `404 Not Found`

Recurso não encontrado ou cliente inexistente.

```json
{
  "error": "RESOURCE_NOT_FOUND",
  "message": "Cliente não encontrado"
}
```

#### `422 Unprocessable Entity`

Dados válidos, porém incompatíveis com o modelo de predição.

```json
{
  "error": "MODEL_INPUT_ERROR",
  "message": "Dados incompatíveis com o modelo XGBoost"
}
```

#### `500 Internal Server Error`

Erro inesperado durante o processamento ou execução do modelo.

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "Erro ao processar a predição de churn"
}
```

Esses códigos seguem boas práticas REST e facilitam o consumo da API por clientes e frontend.

🔝 [Voltar ao topo](#topo)

---
<a id="testes"></a>
## 🧪 Testes

Os testes estão organizados de forma espelhada ao código principal, cobrindo:

* Controllers
* Services
* Repositories
* Validators

O objetivo é garantir confiabilidade e facilitar refatorações rápidas — essenciais em hackathons.

🔝 [Voltar ao topo](#topo)

---
<a id="como-executar-o-projeto"></a>
## ▶️ Como Executar o Projeto

1. Certifique-se de ter **Java** e **Maven** instalados
2. Configure as variáveis de ambiente necessárias (banco de dados)
3. Execute o comando:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em ambiente local.

🔝 [Voltar ao topo](#topo)

---
<a id="boas-praticas-adotadas"></a>
## ✅ Boas Práticas Adotadas

* Separação clara de responsabilidades
* Código organizado por domínio
* Uso de DTOs para evitar exposição direta das entidades
* Validações centralizadas
* Migrações versionadas

🔝 [Voltar ao topo](#topo)

---
<a id="decisoes-tecnicas-tradeoffs"></a>
## 🧠 Decisões Técnicas & Trade-offs

### Arquitetura em Camadas

**Decisão:** Uso de arquitetura tradicional em camadas (Controller / Service / Repository)

* ✔ Facilita entendimento rápido pela banca
* ✔ Reduz acoplamento
* ❌ Menos flexível que arquiteturas reativas ou hexagonais

### Integração com XGBoost

**Decisão:** Modelo de ML tratado como dependência do service

* ✔ Permite troca futura de modelo
* ✔ Evita acoplamento direto com controllers
* ❌ Pode exigir cuidados extras de performance em produção

### Frontend Integrado ao Backend

**Decisão:** Frontend simples servido via `static`

* ✔ Demonstração rápida end-to-end
* ✔ Reduz complexidade de deploy
* ❌ Não ideal para aplicações de grande escala

### Foco em Clareza vs. Overengineering

**Decisão:** Priorizar legibilidade e organização

* ✔ Ideal para hackathon
* ✔ Facilita avaliação técnica
* ❌ Algumas otimizações foram propositalmente postergadas

🔝 [Voltar ao topo](#topo)

---
<a id="proximos-passos"></a>
### Próximos Passos

* Versionamento de modelos
* Monitoramento de drift
* Autenticação e autorização

🔝 [Voltar ao topo](#topo)

---
<a id="licenca-de-uso"></a>
## Licenca de Uso



🔝 [Voltar ao topo](#topo)

---
<a id="equipe"></a>
## Equipe

🔝 [Voltar ao topo](#topo)
