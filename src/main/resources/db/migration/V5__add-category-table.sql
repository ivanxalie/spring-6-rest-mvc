DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    description varchar(50),
    created_date timestamp,
    last_modified_date timestamp DEFAULT NULL,
    version integer DEFAULT NULL
) ENGINE = InnoDB;