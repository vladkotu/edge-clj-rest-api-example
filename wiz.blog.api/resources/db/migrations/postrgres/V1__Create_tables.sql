CREATE TABLE IF NOT EXISTS authors (
  id serial PRIMARY KEY,
  name VARCHAR(120),
  email VARCHAR(120) UNIQUE,
  nickname VARCHAR(80) NOT NULL,
  biography text,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
  id serial PRIMARY KEY,
  title VARCHAR,
  author_id INT REFERENCES authors(id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
  id serial PRIMARY KEY,
  message VARCHAR,
  author_id INT REFERENCES authors(id),
  book_id INT REFERENCES books(id),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
