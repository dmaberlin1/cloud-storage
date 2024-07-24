CREATE TABLE users (
                       id serial PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       role VARCHAR(255) NOT NULL
);

-- Создание индекса для поля username
CREATE INDEX idx_username ON users(username);

-- Создание индекса для поля email
CREATE INDEX idx_email ON users(email);
