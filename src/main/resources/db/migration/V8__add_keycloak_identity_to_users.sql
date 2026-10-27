-- V8: Link DevFlow user profiles to Keycloak identities.
-- Nullable initially so existing profiles remain compatible.
-- A partial unique index allows multiple NULL values but prevents
-- duplicate non-null Keycloak subject identifiers.

ALTER TABLE users
    ADD COLUMN keycloak_id VARCHAR(36);

CREATE UNIQUE INDEX uk_users_keycloak_id
    ON users (keycloak_id)
    WHERE keycloak_id IS NOT NULL;
