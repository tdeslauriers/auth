DROP TABLE IF EXISTS refresh;

CREATE TABLE refresh (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(1020) NOT NULL,
    revoked BOOLEAN NOT NULL,
    date_created TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);