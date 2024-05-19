ALTER TABLE beer_order
    ADD COLUMN beer_order_shipment_id VARCHAR(36);

ALTER TABLE beer_order
    ADD CONSTRAINT bos_shipment_fk
       FOREIGN KEY (beer_order_shipment_id) REFERENCES beer_order_shipment (id);