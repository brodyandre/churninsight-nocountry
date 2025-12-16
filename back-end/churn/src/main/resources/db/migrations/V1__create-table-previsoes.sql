CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS previsoes (
    id BIGSERIAL NOT NULL,
    previsao VARCHAR(100) NOT NULL,
    probabilidade FLOAT NOT NULL,
    data_previsao TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_previsoes PRIMARY KEY(id)
);