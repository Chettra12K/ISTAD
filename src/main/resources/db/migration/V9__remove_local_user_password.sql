-- Authentication credentials are managed by Keycloak.
ALTER TABLE users
DROP COLUMN password;