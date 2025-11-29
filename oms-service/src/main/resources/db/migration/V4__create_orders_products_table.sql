-- Join table between orders and product
CREATE TABLE product (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255)    NOT NULL,
    sku     VARCHAR(100)    NOT NULL UNIQUE,
    price   NUMERIC(10,2)   NOT NULL
);

CREATE TABLE orders_products (
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    PRIMARY KEY (order_id, product_id),

    CONSTRAINT fk_orders_products_order
        FOREIGN KEY (order_id) REFERENCES orders (id),

    CONSTRAINT fk_orders_products_product
        FOREIGN KEY (product_id) REFERENCES product (id)
);
