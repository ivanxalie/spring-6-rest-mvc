
    drop table if exists beer_audit;

    CREATE TABLE beer_audit (
        audit_id VARCHAR(36) NOT NULL PRIMARY KEY,
        id VARCHAR(36) NOT NULL,
        version INTEGER,
        name VARCHAR(50),
        beer_style tinyint not null check (beer_style between 0 and 9),
        upc VARCHAR(250),
        quantity_on_hand INTEGER,
        price DECIMAL(19, 2),
        created_date TIMESTAMP,
        update_date TIMESTAMP,
        created_date_audit TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        principal_name VARCHAR(255),
        audit_event_type VARCHAR(255)
    );

    ALTER TABLE beer_audit ADD CONSTRAINT id_unique UNIQUE (id);