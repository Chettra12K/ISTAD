ALTER TABLE tasks
    ADD CONSTRAINT fk_task_sprint FOREIGN KEY (sprint_id)
    REFERENCES sprints(id) ON DELETE SET NULL;
