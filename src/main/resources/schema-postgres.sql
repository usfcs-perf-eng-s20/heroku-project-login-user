CREATE TABLE users(
 userId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 userName VARCHAR(255),
 email VARCHAR (100),
 password VARCHAR(100),
 age INT ,
 city VARCHAR(100),
 loginFrequency INT,
 lastLogin DATE);