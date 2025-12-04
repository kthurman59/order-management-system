-- Seed sample customers, products and orders for dev
INSERT INTO customer (name, email) VALUES
  ('Alice Customer', 'alice@example.com'),
  ('Bob Customer', 'bob@example.com'),
  ('Carol Customer', 'carol@example.com'),
  ('Dave Customer', 'dave@example.com');

-- Products table is named "products" in the current schema
INSERT INTO products (name, sku, price) VALUES
  ('Widget Basic',    'WIDGET_BASIC', 10.00),
  ('Widget Pro',      'WIDGET_PRO',   20.00),
  ('Gadget Standard', 'GADGET_STD',   15.50);

-- Orders
INSERT INTO orders (customer_id, order_date, status, total_amount) VALUES
  ((SELECT id FROM customer WHERE email = 'alice@example.com'),
   NOW() - INTERVAL '10 days',
   'COMPLETED',
   30.00),
  ((SELECT id FROM customer WHERE email = 'bob@example.com'),
   NOW() - INTERVAL '5 days',
   'PENDING',
   20.00),
  ((SELECT id FROM customer WHERE email = 'carol@example.com'),
   NOW() - INTERVAL '2 days',
   'COMPLETED',
   15.50),
  ((SELECT id FROM customer WHERE email = 'dave@example.com'),
   NOW() - INTERVAL '1 days',
   'CANCELLED',
   10.00);

-- Join orders to products
-- Note: join table columns are order_id and products_id
INSERT INTO orders_products (order_id, products_id) VALUES
  (
    (SELECT o.id
       FROM orders o
       JOIN customer c ON o.customer_id = c.id
      WHERE c.email = 'alice@example.com'
      ORDER BY o.order_date
      LIMIT 1),
    (SELECT id FROM products WHERE sku = 'WIDGET_BASIC')
  ),
  (
    (SELECT o.id
       FROM orders o
       JOIN customer c ON o.customer_id = c.id
      WHERE c.email = 'alice@example.com'
      ORDER BY o.order_date
      LIMIT 1),
    (SELECT id FROM products WHERE sku = 'WIDGET_PRO')
  ),
  (
    (SELECT o.id
       FROM orders o
       JOIN customer c ON o.customer_id = c.id
      WHERE c.email = 'bob@example.com'
      ORDER BY o.order_date
      LIMIT 1),
    (SELECT id FROM products WHERE sku = 'WIDGET_PRO')
  ),
  (
    (SELECT o.id
       FROM orders o
       JOIN customer c ON o.customer_id = c.id
      WHERE c.email = 'carol@example.com'
      ORDER BY o.order_date
      LIMIT 1),
    (SELECT id FROM products WHERE sku = 'GADGET_STD')
  ),
  (
    (SELECT o.id
       FROM orders o
       JOIN customer c ON o.customer_id = c.id
      WHERE c.email = 'dave@example.com'
      ORDER BY o.order_date
      LIMIT 1),
    (SELECT id FROM products WHERE sku = 'WIDGET_BASIC')
  );

