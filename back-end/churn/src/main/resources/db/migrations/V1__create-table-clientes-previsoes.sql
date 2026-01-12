CREATE SCHEMA IF NOT EXISTS public;

CREATE TYPE genero_enum AS ENUM (
    'FEMININO',
    'MASCULINO'
);

CREATE TYPE tipo_contrato_enum AS ENUM (
    'MENSAL',
    'ANUAL',
    'BIANUAL'
);

CREATE TYPE servico_internet_enum AS ENUM (
    'DSL',
    'FIBRA_OTICA',
    'NENHUM'
);

CREATE TYPE metodo_pagamento_enum AS ENUM (
    'BOLETO',
    'CARTAO_CREDITO',
    'DEBITO_EM_CONTA',
    'PIX',
    'TED'
);

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL,
    genero genero_enum NOT NULL,
    idoso INTEGER NOT NULL,
    conjuge VARCHAR(3) NOT NULL,
    dependentes VARCHAR(3) NOT NULL,
    tipo_contrato tipo_contrato_enum NOT NULL,
    tempo_contrato integer NOT NULL,
    servico_telefone VARCHAR(3) NOT NULL,
    multiplas_linhas_tel VARCHAR(30) NOT NULL,
    servico_internet servico_internet_enum NOT NULL,
    seguranca_online VARCHAR(30) NOT NULL,
    backup_online VARCHAR(30) NOT NULL,
    protecao_dispositivo VARCHAR(30) NOT NULL,
    suporte_tecnico VARCHAR(30) NOT NULL,
    tv_streaming VARCHAR(30) NOT NULL,
    filmes_streaming VARCHAR(30) NOT NULL,
    fatura_online VARCHAR(3) NOT NULL,
    metodo_pagamento metodo_pagamento_enum NOT NULL,
    valor_mensal FLOAT NOT NULL,
    valor_total FLOAT NOT NULL,
    data_criacao TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_clientes PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS previsoes (
    id BIGSERIAL,
    cliente_id BIGINT NOT NULL,
    previsao VARCHAR(50) NOT NULL,
    probabilidade DOUBLE PRECISION NOT NULL,
    data_criacao TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_previsoes PRIMARY KEY(id),
    CONSTRAINT fk_clientes FOREIGN KEY(cliente_id) REFERENCES clientes(id)
);