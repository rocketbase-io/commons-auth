INSERT INTO invite (id, created, expiration, invitor, message, email, first_name, last_name, roles) VALUES ('1314202d-e866-4452-b7fc-781e87d44c6c', '2020-01-27 20:30:00.000000', '2030-12-31 23:59:59.999000', 'Marten', 'Please and join our Team from rocketbase.io', 'valid@rocketbase.io', null, 'Valid', 'USER;SERVICE');
INSERT INTO invite (id, created, expiration, invitor, message, email, first_name, last_name, roles) VALUES ('d182397f-2a30-4006-afe8-a2d8ec427142', '2020-01-20 20:30:00.000000', '2030-12-31 23:59:59.999000', 'Lukas', '...', 'hello@rocketbase.io', null, 'Valid', 'SERVICE');
INSERT INTO invite (id, created, expiration, invitor, message, email, first_name, last_name, roles) VALUES ('3ac876a7-5156-499d-8f86-3b137e7fdcbc', '2020-01-20 20:30:00.000000', '2020-01-22 20:30:00.000000', 'System Invalid', 'Please and join our Team from rocketbase.io', 'expired@rocketbase.io', null, null, 'USER;SERVICE');

INSERT INTO invite_keyvalue_pairs (invite_id, field_key, field_value) VALUES ('1314202d-e866-4452-b7fc-781e87d44c6c', 'workspace', '1');
INSERT INTO invite_keyvalue_pairs (invite_id, field_key, field_value) VALUES ('1314202d-e866-4452-b7fc-781e87d44c6c', 'special', 'abc');
INSERT INTO invite_keyvalue_pairs (invite_id, field_key, field_value) VALUES ('1314202d-e866-4452-b7fc-781e87d44c6c', '_secret', 'secure');
INSERT INTO invite_keyvalue_pairs (invite_id, field_key, field_value) VALUES ('3ac876a7-5156-499d-8f86-3b137e7fdcbc', 'workspace', '1');
