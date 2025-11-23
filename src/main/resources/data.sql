-- Datos iniciales para el sistema bancario
INSERT INTO accounts (account_number, owner_name, balance) VALUES 
('ACC001', 'Juan Pérez', 1000.00),
('ACC002', 'María García', 2500.00),
('ACC003', 'Carlos López', 500.00)
ON CONFLICT (account_number) DO NOTHING;