DROP TABLE IF EXISTS password_history;

CREATE TABLE password_history (
    id INT NOT NULL AUTO_INCREMENT,
    password VARCHAR(255) NOT NULL,
    updated DATE NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pwhistory_user_id FOREIGN KEY (user_id) REFERENCES user (id)
);