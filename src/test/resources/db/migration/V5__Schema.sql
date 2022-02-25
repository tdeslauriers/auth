INSERT INTO phone (phone, type) VALUES ("1112223333", "CELL"), ("4445556666", "WORK");

INSERT INTO user_phone (user_id, phone_id)
    VALUES (
        (SELECT id FROM user WHERE username = "admin@deslauriers.world"),
        (SELECT id FROM phone WHERE phone = "1112223333"));