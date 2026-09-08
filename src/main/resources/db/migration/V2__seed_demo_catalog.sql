-- ---------------------------------------------------------------------------
-- Demo catalogue.
--
-- A deployed portfolio API that returns [] is indistinguishable from a broken
-- one, so the catalogue ships with the schema. Demo *users* are not seeded
-- here: their passwords have to be hashed with the same BCrypt encoder the
-- application uses, so they are created at startup by DemoDataSeeder.
-- ---------------------------------------------------------------------------

insert into categories (name)
values ('Electronics'),
       ('Home & Kitchen'),
       ('Books'),
       ('Fitness');

insert into products (name, description, price, image_url, category_id)
values ('Aurora Wireless Headphones',
        'Over-ear headphones with active noise cancellation and 40 hours of battery life.',
        189.99, 'https://picsum.photos/seed/aurora-headphones/600/600',
        (select id from categories where name = 'Electronics')),

       ('Meridian Mechanical Keyboard',
        'Compact 75% mechanical keyboard with hot-swappable switches and PBT keycaps.',
        129.50, 'https://picsum.photos/seed/meridian-keyboard/600/600',
        (select id from categories where name = 'Electronics')),

       ('Nimbus Portable SSD 1TB',
        'USB-C external solid-state drive with 1050 MB/s sequential reads.',
        94.00, 'https://picsum.photos/seed/nimbus-ssd/600/600',
        (select id from categories where name = 'Electronics')),

       ('Kettle & Co Pour-Over Set',
        'Borosilicate carafe, stainless filter and gooseneck kettle for filter coffee.',
        62.25, 'https://picsum.photos/seed/pourover-set/600/600',
        (select id from categories where name = 'Home & Kitchen')),

       ('Cast Iron Skillet, 26cm',
        'Pre-seasoned cast iron skillet that works on induction, gas and open flame.',
        45.00, 'https://picsum.photos/seed/cast-iron/600/600',
        (select id from categories where name = 'Home & Kitchen')),

       ('Linen Bedding Set, Queen',
        'Stonewashed French linen duvet cover with two pillowcases.',
        158.00, 'https://picsum.photos/seed/linen-bedding/600/600',
        (select id from categories where name = 'Home & Kitchen')),

       ('Designing Data-Intensive Applications',
        'Martin Kleppmann on the systems behind reliable, scalable and maintainable software.',
        41.99, 'https://picsum.photos/seed/ddia-book/600/600',
        (select id from categories where name = 'Books')),

       ('Release It! Second Edition',
        'Michael Nygard on designing software that survives contact with production.',
        38.50, 'https://picsum.photos/seed/release-it/600/600',
        (select id from categories where name = 'Books')),

       ('Adjustable Dumbbell Pair, 24kg',
        'Two dial-adjustable dumbbells replacing fifteen pairs of fixed weights.',
        279.00, 'https://picsum.photos/seed/dumbbell-pair/600/600',
        (select id from categories where name = 'Fitness')),

       ('Cork Yoga Mat, 5mm',
        'Natural cork surface on a recycled rubber base, grippier when damp.',
        74.90, 'https://picsum.photos/seed/cork-mat/600/600',
        (select id from categories where name = 'Fitness'));
