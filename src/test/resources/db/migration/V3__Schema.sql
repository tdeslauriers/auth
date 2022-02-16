INSERT INTO role (role) values ("GENERAL_ADMISSION"), ("PROFILE_ADMIN");

INSERT INTO user (username, password, firstname, lastname, date_created, enabled, account_expired, account_locked)
    VALUES ("admin@deslauriers.world", "$2a$13$jaRgQQ0wsoxQbpUaEqoofOz3mYrN7Y2yp61Xy.S2GCqIVuA8o3JtS", "tom", "desl", "2022-02-16", TRUE, FALSE, FALSE);

INSERT INTO user_role (user_id, role_id) VALUES (
    (SELECT id FROM user WHERE username = "admin@deslauriers.world"),
    (SELECT id FROM role WHERE role = "PROFILE_ADMIN"));
