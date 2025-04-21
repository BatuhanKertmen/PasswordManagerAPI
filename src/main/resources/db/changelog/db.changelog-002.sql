INSERT INTO company (username, password, enabled, created_at, last_updated_at)
VALUES ('google', '$2a$10$bw4OeraWInDmmu8A6eUN4OlXzsdVHQTAoXx3Mh9OX4ZDJLzLCG1dC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO client (client_id, name, client_type, client_secret, company_id)
VALUES ('12345', 'Test Client', 'SERVER_APP', '$2a$10$bw4OeraWInDmmu8A6eUN4OlXzsdVHQTAoXx3Mh9OX4ZDJLzLCG1dC', 1),
       ('2', 'testo clio', 'SERVER_APP', '$2a$10$bw4OeraWInDmmu8A6eUN4OlXzsdVHQTAoXx3Mh9OX4ZDJLzLCG1dC', 1);

INSERT INTO users (username, password, enabled, first_name, last_name, middle_name, address, profile_picture, email, email_verified, birthday, phone_number, phone_number_verified, locale, created_at, last_updated_at)
VALUES ('123',
        '$2a$12$LgdCZQ4VjBbU30nax0MHMuP1ibSsX2.RBnCcC.L/xXQPxxB2Y.qU6',
        TRUE,
        'John',
        'Doe',
        'M.',
        '123 Test St, Test City',
        NULL,
        'testuser@example.com',
        TRUE,
        '1990-01-01',
        '1234567890',
        TRUE,
        'en',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO authority (authority, user_id, company_id)
VALUES ('USER', '1', NULL),
       ('COMPANY', NULL, '1');

INSERT INTO Redirect_uri (uri, client_client_id)
VALUES ('http://link1', '12345'),
       ('https://link1', '12345'),
       ('http://link2', '12345');

