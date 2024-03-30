CREATE TABLE projects
(
    id   INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    projectname TEXT                      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (projectname)
);



