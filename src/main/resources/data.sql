DROP TABLE IF EXISTS transaction;

CREATE TABLE transaction (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  price INT(250) NOT NULL,
  rewards INT(250) NOT NULL,
  created_date TIMESTAMP NOT NULL
);