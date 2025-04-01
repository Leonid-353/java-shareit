-- Добавление записей в таблицу users
INSERT INTO users (name, email)
VALUES ('User', 'user@mail.ru'),
       ('Owner', 'owner@mail.ru'),
       ('BookerAuthor', 'bookerAuthor@mail.ru'),
       ('Requestor', 'requestor@mail.ru');

-- Добавление записей в таблицу item_requests
INSERT INTO item_requests (description, requestor_id, created)
VALUES ('Description', 4, '2025-03-27 11:32:00'),
       ('Description', 2, '2025-03-30 12:30:00');

-- Добавление записей в таблицу items
INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES ('AvailableItem', 'DescriptionAvailableItem', true, 2, null),
       ('NotAvailableItem', 'DescriptionNotAvailableItem', false, 2, 1);

-- Добавление записей в таблицу bookings
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('3025-03-27 12:00:00', '3025-03-30 12:30:00', 1, 3, 'WAITING'),
       ('2023-03-27 12:00:00', '3024-03-30 12:30:00', 1, 3, 'APPROVED'),
       ('2022-03-27 12:00:00', '2022-03-30 12:30:00', 1, 3, 'APPROVED'),
       ('2020-03-27 12:00:00', '2020-03-30 12:30:00', 1, 3, 'REJECTED');

-- Добавление записей в таблицу comments
INSERT INTO comments (text, item_id, author_id, created)
VALUES ('TextComment', 1, 3, '2025-03-28 11:32:00');