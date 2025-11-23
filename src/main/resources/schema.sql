-- schema.sql
CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    from_account_id BIGINT REFERENCES accounts(id),
    to_account_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_from_account FOREIGN KEY(from_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_to_account FOREIGN KEY(to_account_id) REFERENCES accounts(id)
);

-- Datos iniciales (solo si no existen)
INSERT INTO accounts (account_number, owner_name, balance) 
SELECT 'ACC001', 'Juan Pérez', 1000.00
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = 'ACC001');

INSERT INTO accounts (account_number, owner_name, balance) 
SELECT 'ACC002', 'María García', 2500.00
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = 'ACC002');

INSERT INTO accounts (account_number, owner_name, balance) 
SELECT 'ACC003', 'Carlos López', 500.00
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = 'ACC003');