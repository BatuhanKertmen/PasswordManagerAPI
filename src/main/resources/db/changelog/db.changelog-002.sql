INSERT INTO users (username, password, enabled)
VALUES ('admin', '{bcrypt}$2a$10$O6b.DOPcMwlZA3R/l3osEeI6d6925zaAx.A8R/QbJTa2k7WUYjb0e', true);

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_SUPER_ADMIN');