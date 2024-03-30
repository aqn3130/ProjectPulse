ALTER TABLE projects
    ADD COLUMN metadata jsonb;

UPDATE projects
SET metadata = '{}'::jsonb;

ALTER TABLE projects
    ALTER COLUMN metadata SET NOT NULL;



-- Real world: Create new column, populate, start using it; In next deployment drop old column