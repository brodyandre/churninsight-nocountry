CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    genero VARCHAR(20) NOT NULL,
    idoso INTEGER NOT NULL,
    conjuge VARCHAR(3) NOT NULL,
    dependentes VARCHAR(3) NOT NULL,
    tipo_contrato VARCHAR(20) NOT NULL,
    tempo_contrato INTEGER NOT NULL,
    servico_telefone VARCHAR(3) NOT NULL,
    multiplas_linhas_tel VARCHAR(3) NOT NULL,
    servico_internet VARCHAR(20) NOT NULL,
    seguranca_online VARCHAR(3) NOT NULL,
    backup_online VARCHAR(3) NOT NULL,
    seguro_tel VARCHAR(1) NOT NULL,
    suporte_tecnico_tel VARCHAR(3) NOT NULL,
    tv_cabo VARCHAR(3) NOT NULL,
    servico_streaming VARCHAR(3) NOT NULL,
    fatura_online VARCHAR(3) NOT NULL,
    metodo_pagamento VARCHAR(30) NOT NULL,
    valor_mensal FLOAT NOT NULL,
    valor_total FLOAT NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    CONSTRAINT pk_clientes PRIMARY KEY(id)
);

CREATE INDEX idx_cliente_genero ON clientes(genero);
CREATE INDEX idx_cliente_tipo_contrato ON clientes(tipo_contrato);
CREATE INDEX idx_cliente_criado_em ON clientes(criado_em);
