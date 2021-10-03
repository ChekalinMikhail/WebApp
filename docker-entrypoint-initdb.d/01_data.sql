INSERT INTO users(id, username, password)
VALUES (1, 'admin', '$argon2id$v=19$m=4096,t=3,p=1$zO/oXcFUSuCIRLUoCOoJpw$ycCjziJSggTRAbj/fpKrZnwCc1kMPkoWn2HTGISz8XM'),
       (2, 'student', '$argon2id$v=19$m=4096,t=3,p=1$h72NaCQZpBSnuCQGSCHYTg$8ug0f3CRXawj/USqdewXn1FKyD5MZyek/Zzq4iRMLM8');

ALTER SEQUENCE users_id_seq RESTART WITH 3;

INSERT INTO tokens(token, "userId")
VALUES ('Rx8j2BUXoBojF7jxV//tNwbL1hnvGsnAQp1bz7cVzNgqAnYF02TrelfgrAWP72y8WYLSUpMzX83fG26+H7TC2g==',1);

INSERT INTO cards(id, "ownerId", number, balance)
VALUES (1, 1, '**** 5486', 50000),
       (2, 1, '**** 4765', 35000),
       (3, 1, '**** 5544', 1000),
       (4, 2, '**** 1343', 12000),
       (5, 2, '**** 6123', 86000);

ALTER SEQUENCE cards_id_seq RESTART WITH 6;

INSERT INTO roles ("userId", role)
VALUES (1, 'admin'),
       (1, 'user'),
       (2, 'user');
