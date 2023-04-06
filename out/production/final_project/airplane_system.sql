-- database should name lower-case 
-- DROP DATABASE IF EXISTS `AirplaneManagement`;
DROP DATABASE IF EXISTS `airplane_management`;
CREATE DATABASE `airplane_management`;
USE `airplane_management`;

DROP TABLE if exists country;
CREATE TABLE country (
	country_id VARCHAR(64) PRIMARY KEY,
    code CHAR(2) UNIQUE,
    name VARCHAR(255) NOT NULL,
    continent VARCHAR(255) NOT NULL
);

DROP TABLE if exists state;
CREATE TABLE state (
	state_id VARCHAR(64) PRIMARY KEY,
    code CHAR(10) UNIQUE,
    name VARCHAR(255) NOT NULL,
    country_code CHAR(2) NOT NULL,
    FOREIGN KEY (country_code) REFERENCES country (code) ON UPDATE CASCADE ON DELETE RESTRICT
);

DROP TABLE IF EXISTS airport;
CREATE TABLE airport (
	airport_id INT PRIMARY KEY,
    code VARCHAR(3)  UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country_code VARCHAR(255) NOT NULL,
    state_code CHAR(10) NOT NULL,
    FOREIGN KEY (country_code)
        REFERENCES country (code)
        ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY (state_code) REFERENCES state (code) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- according from professor's feedback, model should be an entity, and the model decide the num of seats of plane
# DROP TABLE IF EXISTS model;
# CREATE TABLE model (
# 	mid VARCHAR(20) PRIMARY KEY,
#     num_seats INT NOT NULL
# );

# DROP TABLE IF EXISTS airplane;
# CREATE TABLE airplane (
#   id INT PRIMARY KEY,
#   model_id VARCHAR(20) NOT NULL,
#   FOREIGN KEY  (model_id) REFERENCES model(mid) ON UPDATE CASCADE ON DELETE RESTRICT
# );

-- create a table name company which provide a airplane
DROP TABLE IF EXISTS company;
CREATE TABLE company (
 cid INT PRIMARY KEY,
 name VARCHAR(255) NOT NULL
);


# CREATE INDEX airplane_model_idx ON airplane (model);

-- maybe we can delete user table, because there is not relationship with any other tables
-- CREATE TABLE user (
--   username VARCHAR(255) PRIMARY KEY,
--   password VARCHAR(255) NOT NULL,
--   name VARCHAR(255) NOT NULL,
--   date_of_birth DATE NOT NULL,
--   gender CHAR(1) NOT NULL,
--   passport VARCHAR(255) NOT NULL,
--   address VARCHAR(255) NOT NULL,
--   type ENUM('admin', 'crew', 'passenger') NOT NULL -- simplize, maybe do not need crew to login
-- );

DROP TABLE IF EXISTS crew;
CREATE TABLE crew (
  crew_id INT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  job_role VARCHAR(255) NOT NULL
  -- FOREIGN KEY (name) references user(name) ON UPDATE CASCADE ON DELETE RESTRICT
);

DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
	admin_id INT PRIMARY KEY,
    username varchar(255) not null,
	name varchar(255) not null,
	password varchar(255) not null
-- start_date DATE NOT NULL,         -- may do not need it ?
--   job_position VARCHAR(255) NOT NULL,  -- may do not need it?
--   department VARCHAR(255) NOT NULL -- may do not need it?
	-- FOREIGN KEY (name) references user(name) ON UPDATE CASCADE ON DELETE RESTRICT

);

CREATE TABLE passenger (
   passenger_id INT PRIMARY KEY auto_increment,
   username VARCHAR(255) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   name VARCHAR(255) NOT NULL,
   date_of_birth DATE NOT NULL,
   gender CHAR(1) NOT NULL,
   passport VARCHAR(255) NOT NULL,
   address VARCHAR(255) NOT NULL,
   medical_conditions TEXT
    -- FOREIGN KEY (name) references user(name) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE credit_card (
  card_number VARCHAR(16) PRIMARY KEY,
  name_on_card VARCHAR(255) NOT NULL,
  expiry_date DATE NOT NULL,
  type VARCHAR(10) NOT NULL
);

CREATE TABLE payment (
  payment_id INT PRIMARY KEY,
  amount DECIMAL(10,2) NOT NULL,
  date DATE NOT NULL,
  details TEXT,
  credit_card_number VARCHAR(16) NOT NULL,
  FOREIGN KEY (credit_card_number) REFERENCES credit_card (card_number) ON UPDATE CASCADE ON DELETE RESTRICT
);

DROP TABLE IF EXISTS scheduled_flight;
CREATE TABLE scheduled_flight (
  flight_id INT PRIMARY KEY,
  departure_airport INT NOT NULL,
  destination_airport INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  departure_datetime DATETIME NOT NULL,
  duration TIME NOT NULL,
  -- airplane_model VARCHAR(20) NOT NULL, -- should we check if this airplne is provide by the company?
  company_id INT NOT NULL, -- (airport company which provide the plane)
  seats INT NOT NULL,
  CONSTRAINT scheduled_flight_ibfk_from FOREIGN KEY (departure_airport) references airport (airport_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT scheduled_flight_ibfk_to FOREIGN KEY (destination_airport) references airport (airport_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  -- should we check the departure_city is not the same as destination?
  FOREIGN KEY (company_id) references company(cid) ON UPDATE CASCADE ON DELETE RESTRICT
);

DROP TABLE IF EXISTS company_has_flight;
CREATE TABLE company_has_flight(
   flight_id INT,
   company_id INT,
   PRIMARY KEY (flight_id, company_id),
   FOREIGN key (flight_id) references scheduled_flight(flight_id) ON UPDATE CASCADE ON DELETE CASCADE,
   FOREIGN key (company_id) references company(cid) ON UPDATE CASCADE ON DELETE RESTRICT
);

DROP TABLE IF EXISTS flight_sold_seats;
CREATE TABLE flight_sold_seats(
    flight_id INT,
    sold_seats INT DEFAULT 0,
    PRIMARY KEY (flight_id),
    FOREIGN key (flight_id) references scheduled_flight(flight_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE ticket (
  ticket_id INT PRIMARY KEY,
  price DECIMAL(10,2) NOT NULL,
  details TEXT,
  scheduled_flight_id INT NOT NULL,
  passenger_id INT NOT NULL,
  FOREIGN KEY (scheduled_flight_id) REFERENCES scheduled_flight (flight_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (passenger_id) REFERENCES passenger (passenger_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE crew_scheduled_flight (
  crew_id INT NOT NULL,
  scheduled_flight_id INT NOT NULL,
  PRIMARY KEY (crew_id, scheduled_flight_id),
  FOREIGN KEY (crew_id) REFERENCES crew (crew_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (scheduled_flight_id) REFERENCES scheduled_flight (flight_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE admin_scheduled_flight (
  admin_id INT NOT NULL,
  scheduled_flight_id INT NOT NULL,
  -- crew_id ((just consider about when admin create a flight, does admin need to assign the crews of that flight?)
  PRIMARY KEY (admin_id, scheduled_flight_id),
  FOREIGN KEY (admin_id) REFERENCES admin (admin_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (scheduled_flight_id) REFERENCES scheduled_flight (flight_id) ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE ticket_payment (
  ticket_id INT NOT NULL,
  payment_id INT NOT NULL,
  PRIMARY KEY (ticket_id, payment_id),
  FOREIGN KEY (ticket_id) REFERENCES ticket (ticket_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (payment_id) REFERENCES payment (payment_id) ON UPDATE CASCADE ON DELETE RESTRICT
);
