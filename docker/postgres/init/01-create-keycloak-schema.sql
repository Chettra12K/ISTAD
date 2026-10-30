-- DevFlow local PostgreSQL initialization
-- Keycloak uses the same PostgreSQL database as DevFlow but stores
-- its internal tables in a dedicated schema.
CREATE SCHEMA IF NOT EXISTS keycloak AUTHORIZATION devflow;
