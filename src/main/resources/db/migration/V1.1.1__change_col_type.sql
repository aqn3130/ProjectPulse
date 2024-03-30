ALTER TABLE projects
    ALTER COLUMN projectname TYPE varchar (80);
ALTER TABLE projects
    ADD COLUMN updated_at timestamp;
ALTER TABLE projects
    ADD COLUMN created_at timestamp;
ALTER TABLE projects
    RENAME COLUMN projectname to project_name;
