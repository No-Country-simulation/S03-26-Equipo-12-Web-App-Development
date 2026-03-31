-- Password: Admin1234!
-- Hash generado con BCrypt rounds=12
-- IMPORTANTE: cambiar en producción
INSERT INTO users (email, password_hash, name, role)
VALUES (
    'admin@testimonialcms.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdHNcmA.6d.6tGi',
    'Administrator',
    'ADMIN'
) ON CONFLICT (email) DO NOTHING;
