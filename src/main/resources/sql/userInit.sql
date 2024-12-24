INSERT INTO roles (role_name) values ('USER'), ('ADMIN') , ('GUEST');

INSERT INTO grades (tier, min_amount, max_amount, point_rate) values ('STANDARD', 0,100000,0.01),('ROYAL', 100000, 200000, 0.02),('GOLD', 200000, 300000, 0.025),('PLATINUM', 300000, null, 0.03);