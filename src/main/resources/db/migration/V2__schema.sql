DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS user_address;
DROP TABLE IF EXISTS phone;
DROP TABLE IF EXISTS user_phone;

CREATE TABLE address (
    id INT NOT NULL AUTO_INCREMENT,
    address VARCHAR(128) NOT NULL,
    city VARCHAR(64) NOT NULL,
    state VARCHAR(32) NOT NULL,
    zip VARCHAR(16),
    PRIMARY KEY (id)
);

CREATE TABLE user_address (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_address_id FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_address_id FOREIGN KEY (address_id) REFERENCES address (id)
);

CREATE TABLE phone (
    id INT NOT NULL AUTO_INCREMENT,
    phone INT (32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_phone (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    phone_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_phone_id FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_phone_id FOREIGN KEY (phone_id) REFERENCES phone (id)
);