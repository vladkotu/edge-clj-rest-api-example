INSERT INTO authors (name, email, nickname, biography)
VALUES ('Vlad', 'Kot@mail.com', 'vladkot', NULL),
       ('Vik', 'vik@mail.com', 'vik', '*No sombre un suele'),
       ('Clayton', 'clay@mail.com', 'clay', NULL);

INSERT INTO books (title, author_id)
VALUES ('Winter is coming', 1),
       ('Etymology of Dvarfs names', 2),
       ('Programming as hell', 2);

INSERT INTO comments (message, author_id, book_id)
VALUES ('Relly like this book.', 1, 2),
       ('fix an typo at 234', 3, 1),
       ('fix an typo at 21', 3, 2),
       ('fix an typo at 111', 3, 3),
       ('Awesome story!!', 2, 1);
