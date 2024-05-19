drop table if exists beer_order;

create table beer_order (
    id varchar(36) not null,
    created_date datetime(6),
    customer_ref varchar(255),
    last_modified_date datetime(6),
    version integer,
    customer_id varchar(36),
    primary key(id),
    FOREIGN KEY(customer_id) REFERENCES beer(id)
) ENGINE = InnoDB;