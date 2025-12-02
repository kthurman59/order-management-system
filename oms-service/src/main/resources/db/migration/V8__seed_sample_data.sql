-- Seed customers
INSERT INTO customer (id, name, email) VALUES
  (1, 'Acme Corp', 'acme@example.com'),
  (2, 'Globex Inc', 'globex@example.com');

-- Seed products
INSERT INTO products (id, name, sku, price) VALUES
  (1, 'Standard Widget', 'WIDGET-001', 19.99),
  (2, 'Advanced Widget', 'WIDGET-002', 29.99),
  (3, 'Gadget', 'GADGET-001', 9.50);

-- Seed orders
INSERT INTO orders (id, customer_id, status, total_amount, created_at, order_date) VALUES
  (1, 1, 'CREATED', 49.49, now(), now()),
  (2, 2, 'CREATED', 29.99, now(), now());

-- Link orders to products
INSERT INTO orders_products (order_id, products_id) VALUES
  (1, 1),
  (1, 3),
  (2, 2);

-- Keep sequences in sync with explicit ids
SELECT pg_catalog.setval('customer_id_seq', 2, true);
SELECT pg_catalog.setval('product_id_seq', 3, true);
SELECT pg_catalog.setval('orders_id_seq', 2, true);
