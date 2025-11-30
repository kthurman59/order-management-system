-- Align join table column names with JPA defaults

ALTER TABLE orders_products
    RENAME COLUMN order_id TO orders_id;

ALTER TABLE orders_products
    RENAME COLUMN product_id TO products_id;
