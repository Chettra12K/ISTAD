CREATE UNIQUE INDEX IF NOT EXISTS uk_project_owner_lower_name
    ON projects (owner_id, LOWER(name))
    WHERE active = true;
