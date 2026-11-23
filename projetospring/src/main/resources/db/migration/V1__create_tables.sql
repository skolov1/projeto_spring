-- V1__create_tables.sql
-- Criação das tabelas iniciais para o sistema de finanças pessoais

CREATE TABLE conta (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    saldo_inicial NUMERIC(19, 2) NOT NULL DEFAULT 0.00 CHECK (saldo_inicial >= 0)
);

CREATE TABLE transacao (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL CHECK (valor > 0),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    data DATE NOT NULL,
    categoria VARCHAR(100),
    conta_id BIGINT NOT NULL REFERENCES conta(id) ON DELETE CASCADE
);

-- Índices para performance nas consultas frequentes
CREATE INDEX idx_transacao_conta_id ON transacao(conta_id);
CREATE INDEX idx_transacao_data ON transacao(data DESC);
CREATE INDEX idx_transacao_tipo ON transacao(tipo);
CREATE INDEX idx_transacao_conta_data ON transacao(conta_id, data DESC);