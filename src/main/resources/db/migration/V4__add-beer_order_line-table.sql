drop table if exists beer_order_line;

create table beer_order_line (
    id varchar(36) not null,
    beer_id varchar(36),
    created_date datetime(6),
    last_modified_date datetime(6),
    order_quantity int,
    quantity_allocated int,
    version integer,
    beer_order_id varchar(36) REFERENCES beer_order(id),
    primary key(id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (beer_order_id) REFERENCES beer_order(id)
) ENGINE = InnoDB;