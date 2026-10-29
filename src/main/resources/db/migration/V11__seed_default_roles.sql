-- =========================================================
-- V11: Seed canonical DevFlow application roles
-- =========================================================

INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Administrator with elevated application permissions'),
    ('USER', 'Standard authenticated DevFlow user'),
    ('PROJECT_MANAGER', 'Manages projects, teams, and project workflows')
ON CONFLICT (name) DO NOTHING;
