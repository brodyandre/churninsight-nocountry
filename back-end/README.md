<p align="center">
  <a id="topo"></a>
</p>

<h1 align="center">ChurnInsight – Backend (Hackathon ONE BR)</h1>

<div align="center">

  [![Badge Java](https://img.shields.io/badge/Java-21-EE6300?logo=coffeescript&logoColor=white)](https://www.oracle.com/java/)
  [![Badge Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-479E3E?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
  [![Badge Maven](https://img.shields.io/badge/Maven-3.9.x-C71A36?logo=maven&logoColor=white)](https://maven.apache.org/)
  [![Badge JUnit](https://img.shields.io/badge/JUnit-5-D0372D?logo=junit&logoColor=white)](https://junit.org/)
  [![Badge PostgreSQL](https://img.shields.io/badge/PostreSQL-16-31638C?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

</div>
<br>

API _backend_ desenvolvida para o projeto **ChurnInsight**, criado no contexto de um **hackathon**, com o objetivo de fornecer uma base sólida, escalável e bem estruturada para análise e previsão de *churn* (evasão de clientes).

Este _backend_ foi construído seguindo boas práticas de arquitetura em camadas, separação de responsabilidades e foco em manutenibilidade, servindo como base para integração com _frontend_, _pipelines_ de dados e possíveis modelos preditivos.

---

## 📌 Sumário

* [Visão Geral](#visao-geral)
* [Objetivos do Projeto](#objetivos-do-projeto)
* [Arquitetura e Padrões](#arquitetura-e-padroes)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Camadas da Aplicação](#camadas-da-aplicacao)
* [Fluxo da Predição](#fluxo-da-predicao)
* [Banco de Dados e Migrações](#banco-de-dados-e-migracoes)
* [_Schema_ do Banco de Dados](#schema-do-banco-de-dados)
* [_Endpoints_ Principais](#endpoints_principais)
* [_Endpoints_ de Infraestrutura e Suporte](#endpoints_infraestrutura)
* [Tratamento de Erros e Respostas HTTP](#tratamento-erros-e-respostas)
* [Como Executar o Projeto](#como-executar-o-projeto)
* [Testes](#testes)
* [Boas Práticas Adotadas](#boas-praticas-adotadas)
* [Decisões Técnicas & _Tradeoffs_](#decisoes-tecnicas-tradeoffs)
* [Próximos Passos](#proximos-passos)
* [Licença de Uso](#licenca-de-uso)
* [Equipe](#equipe)

---

<a id="visao-geral"></a>
## 🧠 Visão Geral

O **ChurnInsight _Backend_** é responsável por centralizar as regras de negócio, persistência de dados e exposição de _endpoints_ que suportam análises relacionadas à **predição de _churn_ de clientes**.

O projeto atua como um orquestrador que integra um **modelo de Machine Learning baseado em XGBoost**, permitindo que dados históricos sejam processados e avaliados para estimar a probabilidade de evasão. 

A aplicação serve nativamente uma interface para facilitar a demonstração técnica. Esse _frontend_ permite o envio de dados, a visualização imediata de resultados e a execução de exemplos pré-configurados sem a necessidade de ferramentas externas.

A aplicação foi estruturada para equilibrar **velocidade de entrega**, **qualidade técnica** e **clareza arquitetural**, características essenciais em um ambiente de _hackathon_.

🔝 [Voltar ao topo](#topo)

---

<a id="objetivos-do-projeto"></a>
## 🎯 Objetivos do Projeto

* Disponibilizar uma API robusta e extensível através de _endpoints_ padronizados para análise de dados de clientes.
* Centralizar a lógica de negócio e as regras de validação para garantir que a consistência dos dados e as métricas de retenção sejam processadas de forma segura.
* Integrar inteligência preditiva ao sistema via consumo resiliente de um motor de Machine Learning para o fornecimento de probabilidades de evasão.
* Facilitar a experiência de demonstração técnica por meio de uma interface integrada e dados de exemplo (_presets_) que permitem a validação imediata das funcionalidades.
* Demonstrar rigor arquitetural mediante a aplicação de padrões de projeto e boas práticas de desenvolvimento voltadas à manutenibilidade e alta performance.

🔝 [Voltar ao topo](#topo)

---

<a id="arquitetura-e-padroes"></a>
## 🧩 Arquitetura e Padrões

O projeto adota uma **estrutura em camadas** para garantir a separação de responsabilidades e a manutenibilidade do código:

* Organização em **Controller**, **Service** e **Repository** para isolar a recepção de dados, as regras de negócio e a persistência.
* Uso de **DTOs** para proteger o modelo interno do banco de dados e padronizar a comunicação com a API.
* **Validações** de negócio centralizadas para assegurar a integridade dos dados enviados ao motor de Inteligência Artificial.
* Tratamento de **exceções** desacoplado para fornecer respostas de erro consistentes e aumentar a resiliência do sistema.

🔝 [Voltar ao topo](#topo)

---

<a id="tecnologias-utilizadas"></a>
## 🛠️ Tecnologias Utilizadas

* **Java 21**: Linguagem principal utilizada para o desenvolvimento da lógica de _backend_.
* **Spring Boot 3.5.x**: _Framework_ base para construção da API REST, gerenciamento de _beans_ e injeção de dependências.
* **Spring Data JPA**: Abstração de persistência utilizada para a comunicação com o banco de dados.
* **PostgreSQL**: Banco de dados relacional utilizado para armazenamento persistente das informações.
* **Flyway (9.22 / 10.4)**: Ferramenta utilizada para o versionamento e migração automatizada do esquema de banco de dados.
* **Lombok**: Utilizado para redução de código repetitivo (_boilerplate_) em entidades e DTOs.
* **SpringDoc OpenAPI (Swagger)**: Gerador de documentação interativa para exploração e testes dos _endpoints_.
* **Maven**: Gerenciador de dependências e automação do processo de construção (_build_) do projeto.
* **JUnit / Spring Boot Test**: _Frameworks_ utilizados para a implementação de testes de integração e validação de regras de negócio.
* **HTML5 / CSS3 / JavaScript**: Tecnologias aplicadas na construção da inteface de demonstração integrada.
* **Motor de Inteligência Artificial (externo)**: Utilização de um modelo preditivo baseado em **XGBoost**, integrado ao _backend_ via requisições HTTP para fornecimento das probabilidades de _churn_.

🔝 [Voltar ao topo](#topo)

---

<a id="estrutura-do-projeto"></a>
## 📂 Estrutura do Projeto

```
src/main/java/nocountry/churninsight/churn/
├── config/             # Configurações globais e infraestrutura (Bean, RestTemplate)
├── controller/         # Exposição de endpoints REST e controle de requisições
├── dto/                # Objetos para transporte de dados e validações de entrada
├── exception/          # Tratamento de exceções e respostas padronizadas de erro
├── model/              # Entidades para persistência de dados (JPA)
├── repository/         # Interfaces de acesso ao banco de dados PostgreSQL
├── service/            # Camada de lógica de negócio e integração com motor de IA
├── swagger/            # Customização visual e técnica da documentação OpenAPI
└── validator/          # Regras de validação de integridade do domínio

src/main/resources/
├── db/migrations/      # Versionamento do esquema do banco de dados (Flyway)
├── presets/            # Arquivos JSON de exemplo para testes rápidos
└── static/             # Frontend integrado (HTML, CSS e JavaScript)

src/test/java/          # Estrutura de testes unitários e de integração
```

🔝 [Voltar ao topo](#topo)

---

<a id="camadas-da-aplicacao"></a>
## 🧱 Camadas da Aplicação

### _Controller_

Funciona como a **porta de entrada** da aplicação. No projeto, o **ChurnController** recebe as requisições de predição e arquivos CSV, enquanto o **InfrastructureController** monitora a saúde do sistema e fornece dados de demonstração. Sua função é receber os dados e direcionar para o serviço correto.

---

### _Service_

Contém a **regra de negócio** da aplicação. Para garantir a manutenibilidade, o projeto distribui as responsabilidades da seguinte forma:
- Predição e Lote: O **PredictionService** centraliza a lógica de envio de dados para a IA e o processamento de arquivos CSV (lote).
- Métricas: O **StatsService** foca exclusivamente em cálculos analíticos, como taxa de _churn_ e contagem de registros, otimizando a performance ao consultar diretamente o banco de dados.
- Infraestrutura: O **SystemHealthService** e o **DemoDataService** garantem a estabilidade da conexão com o motor Python e o fornecimento de dados para testes rápidos.

---

### _Validator_

Garante que os dados tenham **coerência lógica**. Enquanto outras camadas olham se os campos estão preenchidos, o **ChurnDataValidator** impede "erros impossíveis", como um cliente possuir serviços de _internet_ (_backup_, segurança) sem possuir um plano de _internet_ ativo.

---

### _Repository_

Atua como o **bibliotecário do sistema**. Ele é o único que conversa diretamente com o banco de dados através do Spring Data JPA para salvar informações dos clientes ou buscar contagens específicas via consultas customizadas (`@Query`).

---

### _Data Transfer Object_ (DTO)

Funciona como um **envelope de segurança para o transporte de dados**. Eles garantem que apenas as informações necessárias para a predição viajem entre o _frontend_ e o _backend_, protegendo o modelo interno do banco de dados e validando o que o usuário preencheu.

---

### _Exception_

É o **protocolo de emergência** para quando algo dá errado. O **GlobalExceptionHandler** captura falhas (como o motor de IA estar fora do ar ou dados inválidos) e transforma erros técnicos complexos em mensagens claras e educadas para o usuário final.

---

### Config

Contém as **definições estruturais** que permitem o funcionamento da aplicação e sua comunicação com o exterior.
- Comunicação Externa (**AppConfig**): Define a configuração do **RestTemplate**, estabelecendo limites de tempo (_timeouts_) para conexão e leitura, impedindo o bloqueio do _backend_ em caso de latência no motor de Inteligência Artificial.
- Roteamento Web (**WebConfig**): Estabelece as regras para o fornecimento de recursos estáticos (HTML, CSS e JS) e garante o mapeamento correto da pasta `static` e o direcionamento automático para o painel principal ao acessar a raiz da aplicação.

---

### _Model_

Representa as **tabelas do banco de dados** no código Java. É a estrutura fundamental onde os dados dos clientes e os resultados das previsões são mapeados para serem armazenados permanentemente.

---

### _Database_ (_Migrations_)

A gestão do esquema do banco de dados é realizada de forma automatizada.
O uso de migrações (`db/migration`) permite que a estrutura do banco de dados evolua de forma controlada e reprodutível, além de garantir que todos os ambientes (desenvolvimento, teste e produção) utilizem a mesma versão das tabelas, evitando erros de incompatibilidade entre o código Java e o esquema SQL.

---

### Swagger

Esta camada é dedicada à **documentação técnica**.
- Documentação Dinâmica: Através do **SwaggerConfig**, o projeto expõe uma interface interativa onde é possível testar todos os _endpoints_ da API sem ferramentas externas.
- Customização Visual: O **SwaggerCustomCssInjector** realiza a injeção de um arquivo CSS exclusivo (`custom.css`) em tempo de execução. Esta abordagem garante que a documentação técnica mantenha a identidade visual padronizada do projeto.


🔝 [Voltar ao topo](#topo)

---

<a id="fluxo-da-predicao"></a>
## 🔁 Fluxo da Predição

O processo de predição de _churn_ segue um fluxo estruturado para garantir a integridade dos dados e a precisão do resultado final:

```
[Interface / Cliente]
        |
        | 1. Envio de dados (HTTP POST / JSON)
        v
[ChurnController]
        |
        | 2. Recebimento e validação estrutural (DTO)
        v
[ChurnDataValidator]
        |
        | 3. Verificação de consistência lógica do domínio
        v
[PredictionService]
        |
        | 4. Orquestração e chamada via RestTemplate
        v
[Motor de ML (FastAPI / XGBoost)]
        |
        | 5. Processamento e cálculo da probabilidade
        v
[PredictDTO]
        |
        | 6. Instanciação e cálculo de confiança (Enriquecimento)
        v
[ChurnController]
        |
        | 7. Resposta final padronizada
        v
[Interface / Cliente]
```

🔝 [Voltar ao topo](#topo)

---

<a id="banco-de-dados-e-migracoes"></a>
## 🗄️ Banco de Dados e Migrações

O projeto utiliza o **PostgreSQL** como banco de dados relacional. A gestão e o versionamento do esquema são realizados de forma automatizada através do **Flyway**, com _scripts_ localizados em:
```
src/main/resources/db/migrations
```

Isso garante versionamento, reprodutibilidade do _schema_ e facilidade de evolução do modelo de dados ao longo do _hackathon_.

🔝 [Voltar ao topo](#topo)

---

<a id="schema-do-banco-de-dados"></a>
## 📊 Schema do Banco de Dados

O _schema_ abaixo representa o modelo real do banco de dados utilizado pelo projeto, alinhado às _features_ consumidas pelo modelo de predição.

### 👤 Tabela: `clientes`

Armazena os dados cadastrais e comportamentais dos clientes, utilizados como entrada para o modelo de Machine Learning.

| Campo | Tipo                 | Descrição |
|------|----------------------|-----------|
| id | BIGSERIAL            | Identificador único do cliente |
| genero | genero_enum          | Gênero do cliente |
| idoso | INTEGER              | Indica se o cliente é idoso |
| conjuge | VARCHAR(3)           | Possui cônjuge |
| dependentes | VARCHAR(3)           | Possui dependentes |
| tipo_contrato | tipo_contrato_enum   | Tipo de contrato |
| tempo_contrato | INTEGER              | Tempo de contrato (em meses) |
| servico_telefone | VARCHAR(3)           | Possui serviço de telefone |
| multiplas_linhas_tel | VARCHAR(30)          | Possui múltiplas linhas telefônicas |
| servico_internet | servico_internet_enum | Tipo de serviço de internet |
| seguranca_online | VARCHAR(30)          | Possui segurança online |
| backup_online | VARCHAR(30)          | Possui backup online |
| protecao_dispositivo | VARCHAR(30)          | Possui proteção de dispositivo |
| suporte_tecnico_tel | VARCHAR(30)          | Possui suporte técnico telefônico |
| tv_streaming | VARCHAR(30)          | Possui serviço de TV por streaming |
| filmes_streaming | VARCHAR(30)          | Possui serviço de filmes por streaming |
| fatura_online | VARCHAR(3)           | Utiliza fatura online |
| metodo_pagamento | metodo_pagamento_enum | Método de pagamento |
| valor_mensal | DOUBLE               | Valor mensal cobrado |
| valor_total | DOUBLE               | Valor total acumulado |
| data_criacao | TIMESTAMPTZ          | Data de criação do registro |

---

### 📈 Tabela: `previsoes`

Armazena o histórico de predições de _churn_ geradas pelo modelo **ChurnInsight XGBoost**.

| Campo | Tipo | Descrição                                              |
|------|------|--------------------------------------------------------|
| id | BIGSERIAL | Identificador único da predição                        |
| cliente_id | BIGINT | Chave estrangeira (FK) vinculado ao cliente            |
| previsao | VARCHAR(50) | Classe prevista (ex: "Vai cancelar" / "Vai continuar") |
| probabilidade | DOUBLE | Probabilidade associada à predição                     |
| data_criacao | TIMESTAMPTZ | _Tmestamp_ do momento da predição                      |

---

### 🔗 Relacionamentos

- Cada cliente possui uma **única predição** ativa no sistema, garantindo que a análise de _churn_ reflita o estado mais recente dos dados do usuário.
- A coluna `previsoes.cliente_id` possui uma restrição de unicidade (_Unique Constraint_) e referencia `clientes.id`.

---

### 📍 Estratégia de Persistência

Embora o sistema conte com uma integração completa com o PostgreSQL, o fluxo de predição foi desenhado para ser transiente (_stateless_).
Esta decisão foi tomada para garantir a viabilidade técnica do projeto frente a limitações orçamentárias de infraestrutura em nuvem.
A camada de persistência permanece implementada e testada, pronta para ser ativada em ambientes com maior disponibilidade de recursos.

🔝 [Voltar ao topo](#topo)

---

<a id="endpoints_principais"></a>
## 📡 Endpoints Principais

### `POST /churn/predict`

Realiza o processamento de dados de um **cliente individual** e retorna a análise de probabilidade de evasão.

#### Exemplo de Requisição

O objeto enviado deve conter as características demográficas e contratuais do cliente.

```json
{
  "gender": "Masculino",
  "SeniorCitizen": 0,
  "Partner": "Sim",
  "Dependents": "Sim",
  "tenure": 60,
  "PhoneService": "Sim",
  "MultipleLines": "Sim",
  "InternetService": "DSL",
  "OnlineSecurity": "Sim",
  "OnlineBackup": "Sim",
  "DeviceProtection": "Sim",
  "TechSupport": "Sim",
  "StreamingTV": "Não",
  "StreamingMovies": "Não",
  "Contract": "Bianual",
  "PaperlessBilling": "Não",
  "PaymentMethod": "Cartão de crédito",
  "MonthlyCharges": 29.00,
  "TotalCharges": 1700.00
}
```

#### Exemplo de Resposta (Sucesso)

A resposta é enriquecida pelo _backend_ com o cálculo de confiança, caso necessário.

```json
{
  "previsao": "Vai continuar",
  "probabilidade": 0.85,
  "confianca": 0.70
}
```

---

### `POST /churn/upload`

_Endpoint_ para processamento analítico em lote (_bulk processing_).

Utiliza um algoritmo customizado de _parsing_ linear (**BufferedReader**) para iterar sobre o arquivo CSV sem carregar todo o conteúdo na memória (focado em performance).
Para cada linha válida, o sistema realiza uma chamada ao motor de IA e consolida o resultado em um **PredictDTO**.

> [!NOTE]
> Linhas com formato inconsistente (menos de 19 colunas) são ignoradas e registradas em _log_, impedindo que erros isolados interrompam o processamento do lote.

#### Exemplo de Resposta (Sucesso)

```json
[
  {
    "previsao": "Vai cancelar",
    "probabilidade": 0.85,
    "confianca": 0.70
  },
  {
    "previsao": "Vai continuar",
    "probabilidade": 0.12,
    "confianca": 0.76
  },
  {
    "previsao": "Vai cancelar",
    "probabilidade": 0.55,
    "confianca": 0.10
  }
]
```

---

### `GET /churn/stats`

Recupera métricas agregadas baseadas no histórico de predições armazenadas no banco de dados.

#### Exemplo de Resposta (Sucesso)

```json
{
  "totalClients": 150,
  "totalPredictions": 150,
  "churnRate": 24.0,
  "retainedClients": 114,
  "churnedClients": 36
}
```

🔝 [Voltar ao topo](#topo)

---

<a id="endpoints_infraestrutura"></a>
## ⚙️ Endpoints de Infraestrutura e Suporte

Estes _endpoints_ não possuem lógica de negócio, mas garantem a operabilidade, documentação e facilidade de demonstração do ecossistema.

### `GET /`

Serve o _frontend_ minimalista embutido no _backend_.
Através da configuração de **ResourceHandlers** no Spring Web, o _backend_ atua como um servidor de arquivos estáticos para o SPA (_Single Page Application_) localizado em `src/main/resources/static`.

<p align="center">
  <img src="churn\assets\print-interface.png" alt="Interface ChurnInsight" width="800">
  <br>
  <em><strong>Figura 1:</strong> Interface SPA integrada ao Spring Boot. O painel demonstra o consumo dos <strong>Presets</strong> e a visualização dinâmica da <strong>predição de evasão</strong> de clientes.</em>
</p>

---

### `GET /swagger-ui/index.html`

Interface para exploração e execução de testes manuais nos _endpoints_ sem necessidade de ferramentas externas.

<p align="center">
  <img src="churn\assets\print-swagger.png" alt="Documentação Swagger OpenAPI" width="800">
  <br>
  <em><strong>Figura 2:</strong> Documentação interativa via <strong>SpringDoc OpenAPI 3</strong>. A interface permite a validação dos contratos de dados e testes diretos nos endpoints de predição e estatísticas.</em>
</p>

---

### `GET /demo-examples`

Fornece uma lista de cenários pré-configurados (_presets_) para preenchimento automático e testes rápidos da interface.

#### Exemplo de Resposta (Sucesso)

```json
[
  {
    "id": "low-risk",
    "order": 1,
    "label": "Cliente Fiel (Risco Baixo)",
    "risk": "low",
    "description": "Contrato Bianual com todos os serviços de suporte.",
    "payload": {
      "gender": "Masculino",
        "SeniorCitizen": 0,
        "Partner": "Sim",
        "Dependents": "Sim",
        "tenure": 60,
        "PhoneService": "Sim",
        "MultipleLines": "Sim",
        "InternetService": "DSL",
        "OnlineSecurity": "Sim",
        "OnlineBackup": "Sim",
        "DeviceProtection": "Sim",
        "TechSupport": "Sim",
        "StreamingTV": "Não",
        "StreamingMovies": "Não",
        "Contract": "Bianual",
        "PaperlessBilling": "Não",
        "PaymentMethod": "Cartão de crédito",
        "MonthlyCharges": 29.00,
        "TotalCharges": 1700.00
    }
  }
]
```

---

### `GET /health`

Verifica a integridade do _backend_ (Spring Boot) e fornece detalhes sobre a instância em execução.

#### Exemplo de Resposta (Sucesso)

```json
{
  "status": "UP",
  "java_service_url": "http://127.0.0.1:8080",
  "java_internal_latency": 15
}
```

#### Exemplo de Resposta (Serviço _Offline_)

```json
{
  "status": "DOWN",
  "error": "Mensagem do erro"
}
```

---

### `GET /ds-health`

Verifica a conectividade e o estado do Serviço de Data Science (FastAPI/XGBoost).

> [!NOTE] 
> Este _endpoint_ retorna o _header_ customizado `X-Proxy-Latency-Ms`, indicando o tempo de ida e volta (_round-trip_) entre o Java e o Python.

#### Exemplo de Resposta (Sucesso)

```json
{
  "status": "online",
  "model_loaded": true,
  "model_path": "models/xgb_model.joblib",
  "threshold": 0.5,
  "ds_service_url": "http://api-ml-url",
  "internal_latency": 45
}
```

#### Exemplo de Resposta (Serviço _Offline_)

```json
{
  "status": "offline",
  "model_loaded": false,
  "ds_service_url": "http://api-ml-url",
  "internal_latency": -1,
  "threshold": "-",
  "model_path": "indisponível",
  "error_message": "Connection refused"
}
```

🔝 [Voltar ao topo](#topo)

---

<a id="tratamento-erros-e-respostas"></a>
## ❌ Tratamento de Erros e Respostas HTTP

A API utiliza um **GlobalExceptionHandler** para capturar exceções e padronizar as respostas de erro, garantindo que o cliente receba informações claras para a correção de requisições. 
Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2024-05-20T14:30:00",
  "status": 400,
  "message": "Mensagem descritiva do erro",
  "details": "Detalhes técnicos ou lista de campos inválidos"
}
```

| Status | Causa Comum | Exceção Capturada |
| :--- | :--- | :--- |
| **400** | Erro de validação no DTO ou JSON malformado. | `MethodArgumentNotValidException` |
| **404** | Recurso solicitado não existe no banco. | `ResourceNotFoundException` |
| **405** | Tentativa de uso de método não suportado. | `HttpRequestMethodNotSupportedException` |
| **422** | Dados válidos, mas inconsistentes para a IA. | `InvalidChurnDataException` |
| **502** | O serviço de Data Science (Python) está _offline_. | `IntegrationException` |
| **504** | O motor de predição excedeu o tempo limite. | `PredictionServiceTimeoutException` |


🔝 [Voltar ao topo](#topo)

---

<a id="como-executar-o-projeto"></a>
## ▶️ Como Executar o Projeto

Para rodar a aplicação em ambiente local, é necessário seguir os passos de configuração de ambiente e execução de comandos.

### 📋 Pré-requisitos

* **Java 21** (LTS)
* **Maven 3.8+**
* **PostgreSQL** ativo e acessível

---

### 1️⃣ Configuração do Banco de Dados

O projeto utiliza o **Flyway** para gerir as migrações de esquema. Antes de iniciar, crie uma base de dados no PostgreSQL (ex: `churninsight`) e configure as variáveis de ambiente no ficheiro `.env` na raiz do diretório `churn` ou diretamente no sistema:

```bash
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=churninsight
DATABASE_USER=seu_usuario
DATABASE_PASSWORD=sua_senha
DS_SERVICE_URL=http://localhost:8000
```

---

### 2️⃣ Build e Execução

Na raiz do diretório `churn`, utilize o Maven para baixar as dependências e iniciar o servidor:

```bash
# Limpa e instala as dependências
mvn clean install

# Inicia a aplicação Spring Boot
mvn spring-boot:run

# Limpa e instala as dependências ignorando os testes
mvn clean install -DskipTests

# Inicia a aplicação Spring Boot sem executar os testes
mvn spring-boot:run -DskipTests
```

---

### 3️⃣ Acesso à Aplicação

Após o _log_ de sucesso do Spring Boot, os serviços estarão disponíveis nos seguintes endereços:

* **Interface (_Frontend_):** `http://localhost:8080/index.html`
* **Documentação (Swagger):** `http://localhost:8080/swagger-ui.html`

🔝 [Voltar ao topo](#topo)

---

<a id="testes"></a>
## 🧪 Testes

O projeto adota uma pirâmide de testes robusta para garantir que as predições de _churn_ e o processamento de dados ocorram sem falhas de integridade.
A suíte de testes foi dividida em três camadas principais utilizando **JUnit 5**, **Mockito** e **AssertJ**:

1. **Testes de Unidade (Puros):** Focados na lógica de negócio isolada (**Services** e **Validators**).
2. **Testes de Integração (_Slice_):** Validação de persistência com `DataJpaTest` (PostgreSQL) e serialização JSON com `JsonTest`.
3. **Testes de Controller (MockMvc):** Garantem que os contratos da API (HTTP Status, JSON de saída) sejam respeitados.

A suíte de testes atinge uma cobertura de **87% das classes** do sistema. O foco da validação concentra-se na camada de serviços e validadores, garantindo que 100% das regras de consistência de dados e cálculos de confiança sejam verificados automaticamente a cada _build_.

---

### 🛠️ Como executar os testes

Para rodar toda a suíte de testes e gerar o relatório de execução, utilize o comando dentro do diretório `churn`:

```bash
# Executa todos os testes
mvn test

# Executa apenas uma classe específica
mvn test -Dtest=ChurnControllerTests
```

> **Configuração de Ambiente:** Os testes utilizam o arquivo `application-tests.properties` que carrega automaticamente as variáveis do seu `.env` para conectar ao banco de dados durante a validação dos **Repositories**.

🔝 [Voltar ao topo](#topo)

---

<a id="boas-praticas-adotadas"></a>
## ✅ Boas Práticas Adotadas

O desenvolvimento do projeto seguiu padrões de mercado para garantir manutenibilidade, segurança e performance.

* Arquitetura e _Design_ de Software
  * Organização de código estruturada por domínio, facilitando a localização de componentes e a expansão de funcionalidades.
  * Uso sistemático de _Data Transfer Objects_ para isolar as entidades do banco de dados da camada de exposição (API), prevenindo o acoplamento excessivo.
  * Controle de evolução do banco de dados através do Flyway, garantindo que o esquema seja replicável em qualquer ambiente.
* Validação e Consistência
  * Combinação de _Bean Validation_ (`@Valid`) para integridade de formato e **ChurnDataValidator** para regras de negócio complexas (ex: consistência lógica entre serviços contratados e infraestrutura disponível).
  * Centralização da lógica de erro no **GlobalExceptionHandler**, entregando respostas padronizadas e seguras ao consumidor da API.
* Resiliência e Integração
  * Implementação de mecanismos de recuperação para falhas em serviços externos, garantindo que a aplicação permaneça operacional mesmo com degradação parcial de recursos.
  * Rastreio de latência de integração via _header_ customizado `X-Proxy-Latency-Ms`, permitindo auditoria de performance em tempo real.
* Produtividade e Experiência do Desenvolvedor
  * Uso do **DemoDataService** com o padrão _Resource Resolver_ para carregar automaticamente exemplos de teste (_presets_) sem necessidade de alterar código-fonte.
  * Interface customizada e detalhada para facilitar a exploração dos _endpoints_ e acelerar a integração com o _frontend_.

🔝 [Voltar ao topo](#topo)

---

<a id="decisoes-tecnicas-tradeoffs"></a>
## 🧠 Decisões Técnicas & _Trade-offs_

Abaixo estão listadas as principais decisões de engenharia e as renúncias (_trade-offs_) assumidas para este projeto.

### Arquitetura em Camadas

**Decisão:** Uso de arquitetura tradicional em camadas (Controller / Service / Repository).

* **Pró**: Facilita o entendimento imediato pela banca e isola as regras de negócio.
* **Contra**: Menos flexível que arquiteturas reativas ou hexagonais em sistemas de altíssima escala.

### Integração com Motor de Inteligência Artificial

**Decisão:** Modelo XGBoost tratado como serviço externo via `RestTemplate`.

* **Pró**: Permite a evolução independente do modelo de Machine Learning e do _backend_ Java.
* **Contra**: Introduz latência de rede, mitigada com configurações de _timeout_ e monitoramento.

### _Frontend_ Integrado ao _Backend_

**Decisão:** Interface simples servida via recursos estáticos do Spring Boot.

* **Pró**: Garante uma demonstração _end-to-end_ imediata com _deploy_ simplificado (artefato único).
* **Contra**: Limita a escalabilidade independente da interface em relação à API.

### Foco em Legibilidade

**Decisão:** Priorizar clareza, organização de domínios e documentação sobre abstrações complexas.

* **Pró**: Facilita a avaliação técnica e garante estabilidade para o MVP.
* **Contra**: Otimizações avançadas de performance (como mensageria) foram deliberadamente postergadas.

🔝 [Voltar ao topo](#topo)

---

<a id="proximos-passos"></a>
## 🔜 Próximos Passos

Como parte da evolução planejada para a solução, foram mapeadas as seguintes melhorias técnicas:

1. [ ] **Métricas Financeiras (_Revenue at Risk_)**: Integrar a lógica de cálculo de média e soma de valores para exibir o impacto financeiro estimado dos clientes em risco de evasão.
2. [ ] **Séries Temporais**: Evoluir o _StatsService_ para agrupar predições por períodos (diário/mensal), permitindo a visualização de gráficos de linha com a evolução da taxa de _churn_ ao longo do tempo.
3. [ ] **Segmentação por Atributos**: Implementar filtros dinâmicos para calcular estatísticas baseadas em categorias (ex: Taxa de _Churn_ apenas para clientes com "Fibra Óptica" vs "DSL").
4. [ ] **Refatoração do _Parser_ de CSV**: Substituição da lógica manual de _split_ pela integração total com a biblioteca Apache Commons CSV, visando suporte a delimitadores complexos e tratamento de campos com aspas.
5. [ ] **Autenticação e Segurança**: Implementação de Spring Security com JWT ou Basic Auth para proteger os _endpoints_ de estatísticas e infraestrutura, mantendo apenas a interface de demonstração pública.
6. [ ] **Persistência de Auditoria**: Implementar a gravação automática na tabela `previsoes` para fins de auditoria e retreinamento futuro do modelo de IA.
7. [ ] **Persistência de Lote**: Implementação da gravação automática no banco de dados para todas as predições realizadas via _upload_, permitindo análises históricas retroativas.
8. [ ] **Histórico do Cliente**: Permitir que, mediante autenticação, um cliente possa salvar o seu resultado de _churn_ para acompanhar a evolução do seu risco ao longo dos meses.
9. [ ] **Expansão da Malha de Testes**: Ampliar a cobertura de testes de integração para alcançar 95% dos serviços críticos, incluindo testes de carga para o processamento de CSV e simulações de falhas de rede intermitentes.
10. [ ] **Otimização da Cobertura de Código**: Elevar a cobertura de linhas de 65% para 85%, focando na inclusão de testes de borda (_edge cases_) para o processamento de CSV e tratamentos de exceções específicas na integração com a API de Data Science.

🔝 [Voltar ao topo](#topo)

---

<a id="licenca-de-uso"></a>
## 📄 Licenca de Uso

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

🔝 [Voltar ao topo](#topo)

---

<a id="equipe"></a>
## 👥 Equipe

| Foto                                                                                                                      | Nome                      | Role              | LinkedIn                                                           | GitHub |
|---------------------------------------------------------------------------------------------------------------------------|---------------------------|-------------------|--------------------------------------------------------------------|--------|
| <img src="https://github.com/augustoramos000.png" alt="Avatar de Augusto Ramos" style="width: 30px; border-radius: 50%;"> | **Augusto Ramos**         | Backend Developer | [LinkedIn](https://www.linkedin.com/in/augustoramos00/)            | [GitHub](https://github.com/augustoramos000) |
| <img src="https://github.com/biachristie.png" alt="Avatar de Beatriz Christie" style="width: 30px; border-radius: 50%;">  | **Beatriz Christie**      | Backend Developer | [LinkedIn](https://www.linkedin.com/in/beatriz-christie/)          | [GitHub](https://github.com/biachristie) |
| <img src="https://github.com/walkii-dev.png" alt="Avatar de Lucas Oliveira" style="width: 30px; border-radius: 50%;">     | **Lucas Oliveira**        | Backend Developer | [LinkedIn](https://www.linkedin.com/in/luoliveiracode/)            | [GitHub](https://github.com/walkii-dev) |
| <img src="https://github.com/joaojosers.png" alt="Avatar de João José Sousa" style="width: 30px; border-radius: 50%;">    | **João José Sousa**       | Backend Developer | [LinkedIn](https://www.linkedin.com/in/joao-jose-sousa-developer/) | [GitHub](https://github.com/joaojosers) |
| <img src="https://github.com/PHmore.png" alt="Avatar de Patryck Silva" style="width: 30px; border-radius: 50%;">          | **Patryck Henryck Silva** | Backend Developer | [LinkedIn](https://www.linkedin.com/in//)                          | [GitHub](https://github.com/PHmore) |
