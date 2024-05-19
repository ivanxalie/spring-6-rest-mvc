DROP TABLE IF EXISTS beer_order_shipment;

CREATE TABLE beer_order_shipment
(
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    beer_order_id VARCHAR(36) UNIQUE,
    tracking_number VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_date DATETIME(6) DEFAULT NULL,
    version integer DEFAULT NULL,
    CONSTRAINT bos_pk FOREIGN KEY (beer_order_id) REFERENCES beer_order(id)
) ENGINE = InnoDB;