INSERT INTO role (role) values ("GENERAL_ADMISSION"), ("PROFILE_ADMIN");

INSERT INTO user (username, password, firstname, lastname, date_created, enabled, account_expired, account_locked)
    VALUES ("admin@deslauriers.world", "$2a$13$jaRgQQ0wsoxQbpUaEqoofOz3mYrN7Y2yp61Xy.S2GCqIVuA8o3JtS", "tom", "desl", "2022-02-16", TRUE, FALSE, FALSE);

INSERT INTO user_role (user_id, role_id)
    VALUES (
        (SELECT id FROM user WHERE username = "admin@deslauriers.world"),
        (SELECT id FROM role WHERE role = "PROFILE_ADMIN"));

INSERT INTO address (address, city, state, zip)
    VALUES ("123 House Street", "Citytown", "CA", "55555"),
            ("456 Apartment Blvd", "Towncity", "TX", "44444");

INSERT INTO user_address (user_id, address_id)
    VALUES
        ((SELECT id FROM user WHERE username = "admin@deslauriers.world"),
        (SELECT id FROM address WHERE address = "123 House Street"));
