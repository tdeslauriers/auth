DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_role;

CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(40) NOT NULL,
    password VARCHAR(255) NOT NULL,
    date_created DATE NOT NULL,
    enabled BOOLEAN NOT NULL,
    account_expired BOOLEAN NOT NULL,
    account_locked BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE role (
    id INT NOT NULL AUTO_INCREMENT,
    role VARCHAR(40),
    PRIMARY KEY (id)
);

CREATE TABLE user_role (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES role (id)
);