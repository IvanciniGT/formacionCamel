-- ==============================================
-- Script de inicialización de datos de prueba
-- Base de datos H2 en memoria
-- ==============================================

-- Personas ACTIVAS (adultos)
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-001', 'Ada Lovelace', 36, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-002', 'Alan Turing', 41, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-003', 'Grace Hopper', 85, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-004', 'Margaret Hamilton', 55, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Personas ACTIVAS (menores)
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-005', 'Elon Code', 16, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-006', 'Marie Code Junior', 12, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Personas INACTIVAS
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-007', 'Dennis Ritchie', 70, 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-008', 'Ken Thompson', 78, 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Persona PENDING (esperando validación)
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-009', 'Linus Torvalds', 52, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Persona INVALID (edad inválida)
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-010', 'Invalid Person', -5, 'INVALID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Persona INCOMPLETE (falta nombre)
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-011', '', 25, 'INCOMPLETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Más personas ACTIVAS para pruebas de consultas
INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-012', 'Tim Berners-Lee', 67, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-013', 'Bjarne Stroustrup', 72, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-014', 'James Gosling', 68, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO persons (external_id, name, age, status, created_at, updated_at) 
VALUES ('EXT-015', 'Guido van Rossum', 66, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==============================================
-- Resumen de datos insertados:
-- - 10 personas ACTIVAS (4 adultos mayores, 4 seniors, 2 menores)
-- - 2 personas INACTIVAS
-- - 1 persona PENDING
-- - 1 persona INVALID
-- - 1 persona INCOMPLETE
-- TOTAL: 15 registros
-- ==============================================
