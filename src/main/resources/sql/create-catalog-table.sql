CREATE TABLE catalog
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    land_id      VARCHAR(250) NOT NULL UNIQUE,
    full_name    VARCHAR(250),
    address      VARCHAR(250),
    phone_number VARCHAR(250),
    notes        VARCHAR(250),
    telegram_id  VARCHAR(250)
);

