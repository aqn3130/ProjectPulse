CREATE TABLE updates (
    id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    project_id INT NOT NULL,
    project_name varchar (80) NOT NULL,
    metadata jsonb,
    updated_at timestamp,
    created_at timestamp,
    PRIMARY KEY (id),
    UNIQUE (project_name)
);

ALTER TABLE updates
    ADD CONSTRAINT fk_projects_updates FOREIGN KEY (project_id) REFERENCES projects (id)