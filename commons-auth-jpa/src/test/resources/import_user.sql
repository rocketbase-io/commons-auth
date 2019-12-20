INSERT INTO user (id, avatar, created, email, enabled, first_name, last_login, last_name, last_token_invalidation, password, username) VALUES ('401fb225-057e-4e0a-a0ff-e99e76030d52', 'https://www.gravatar.com/avatar/fc40e22b7bcd7230b49c34eb113d5dbc.jpg?s=160&d=retro','2019-11-16 06:39:04.945000', 'marten@rocketbase.io', 1, 'Marten', '2019-12-18 21:48:18.540409', 'Prieß', NULL,'$2a$10$g4RhbzNkit7Z9XWTUE.e3OROINetRYOZ0M203LgXVPZAmkVkQsqDq', 'marten');
INSERT INTO user (id, avatar, created, email, enabled, first_name, last_login, last_name, last_token_invalidation, password, username) VALUES ('c3c58d60-e948-442f-9783-c0341c65a367', 'https://www.gravatar.com/avatar/fdd298809a945e71f68cddd9a86c44a7.jpg?s=160&d=retro', '2019-11-16 06:39:04.505000', 'niels@rocketbase.io', 1, 'Niels', NULL, 'Schelbach', NULL, '$2a$10$ptCGKpsQ4UPbnZtVbq0/pOdyy3ZEInxNAXzlkUNkG5eZ05cvsxIUa', 'niels');
INSERT INTO user (id, avatar, created, email, enabled, first_name, last_login, last_name, last_token_invalidation, password, username) VALUES ('d74678ea-6689-4c6f-a055-e275b4a2a61c', 'https://www.gravatar.com/avatar/c814c20fe2cc25ab8559f38a638d5a88.jpg?s=160&d=retro', '2019-11-16 06:39:03.374000', 'sampled@rocketbase.io', 0, 'Sample', NULL, 'User', NULL, '$2a$10$l0ItxSE6XPl5mqKDjhuIlO7VnpvEjPrPyRDJl3FyJyZmjDZWgQrKC', 'sample');
INSERT INTO user (id, avatar, created, email, enabled, first_name, last_login, last_name, last_token_invalidation, password, username) VALUES ('f55e3176-3fca-4100-bb26-853106269fb1', 'https://www.gravatar.com/avatar/c8eff552d931386a9edd37b5cee0b44c.jpg?s=160&d=retro', '2019-11-16 06:39:03.767000', 'servicee@rocketbase.io', 1, 'Service', NULL, NULL, NULL, '$2a$10$CxNjJi0rHxpAws1UFrlz0Oyo2k2O7ZKACMrWxdraBgk5sidHDFQbG', 'service');



INSERT INTO user_roles (id, role) VALUES ('401fb225-057e-4e0a-a0ff-e99e76030d52', 'ADMIN');
INSERT INTO user_roles (id, role) VALUES ('c3c58d60-e948-442f-9783-c0341c65a367', 'USER');
INSERT INTO user_roles (id, role) VALUES ('d74678ea-6689-4c6f-a055-e275b4a2a61c', 'user');
INSERT INTO user_roles (id, role) VALUES ('f55e3176-3fca-4100-bb26-853106269fb1', 'service');


INSERT INTO user_keyvalue_pairs (id, field_key, field_value) VALUES ('401fb225-057e-4e0a-a0ff-e99e76030d52', 'workspace', '1');
INSERT INTO user_keyvalue_pairs (id, field_key, field_value) VALUES ('c3c58d60-e948-442f-9783-c0341c65a367', 'workspace', '1');
INSERT INTO user_keyvalue_pairs (id, field_key, field_value) VALUES ('c3c58d60-e948-442f-9783-c0341c65a367', 'language', 'en');