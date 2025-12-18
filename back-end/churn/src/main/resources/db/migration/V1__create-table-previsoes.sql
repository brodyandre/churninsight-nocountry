CREATE TABLE previsoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    previsao VARCHAR(50) NOT NULL,
    probabilidade DECIMAL(5, 4) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_previsao CHECK (previsao IN ('Vai cancelar', 'Vai continuar')),
    CONSTRAINT chk_probabilidade CHECK (probabilidade >= 0 AND probabilidade <= 1)
);

CREATE INDEX idx_cliente_id ON previsoes(cliente_id);
CREATE INDEX idx_data_criacao ON previsoes(data_criacao);
