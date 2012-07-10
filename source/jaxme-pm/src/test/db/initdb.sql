CREATE TABLE httpSession (
  id INT NOT NULL PRIMARY KEY,
  ipaddress VARCHAR(15) NOT NULL,
  logintime TIMESTAMP NOT NULL,
  lastaction TIMESTAMP NOT NULL,
  expiretime SMALLINT NOT NULL,
  cookie VARCHAR(25) NOT NULL,
  randomSeed DOUBLE NOT NULL,
  precedence REAL,
  UNIQUE (cookie)
);
