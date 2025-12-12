INSERT INTO users (user_id, first_name, last_name, mobile_number, email, password)
VALUES
    (10000, 'Admin',  'Istrator', '+431234567890', 'admin@app.com', '{noop}password'),
    (20000, 'Normal', 'User',     '+436601234567', 'user@app.com',  '{noop}password');



-- admin gets ADMIN + USER
INSERT INTO user_role (user_user_id, roles)
VALUES
    ((SELECT user_id FROM users WHERE email = 'admin@app.com'), 'ADMIN'),
    ((SELECT user_id FROM users WHERE email = 'admin@app.com'), 'USER');

-- normal user gets USER
INSERT INTO user_role (user_user_id, roles)
VALUES
    ((SELECT user_id FROM users WHERE email = 'user@app.com'), 'USER');

