DROP DATABASE IF EXISTS `AirplaneManagement`;
CREATE DATABASE `AirplaneManagement`;
USE `AirplaneManagement`;

CREATE TABLE airport (
  code VARCHAR(3) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  city VARCHAR(255) NOT NULL,
  state CHAR(2) NOT NULL
);

CREATE TABLE airplane (
  id INT PRIMARY KEY,
  model VARCHAR(255) NOT NULL,
  num_seats INT NOT NULL
);

CREATE INDEX airplane_model_idx ON airplane (model);

CREATE TABLE crew (
  crew_id INT PRIMARY KEY,
  start_date DATE NOT NULL,
  job_role VARCHAR(255) NOT NULL
);

CREATE TABLE admin (
  admin_id INT PRIMARY KEY,
  start_date DATE NOT NULL,
  job_position VARCHAR(255) NOT NULL,
  department VARCHAR(255) NOT NULL
);

CREATE TABLE passenger (
  passenger_id INT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  date_of_birth DATE NOT NULL,
  gender CHAR(1) NOT NULL,
  passport VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  medical_conditions TEXT
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

CREATE TABLE scheduled_flight (
  flight_id INT PRIMARY KEY,
  departure_city VARCHAR(255) NOT NULL,
  destination VARCHAR(255) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  departure_datetime DATETIME NOT NULL,
  duration TIME NOT NULL,
  airplane_model VARCHAR(255) NOT NULL,
  FOREIGN KEY (airplane_model) REFERENCES airplane (model) ON UPDATE CASCADE ON DELETE RESTRICT
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
  PRIMARY KEY (admin_id, scheduled_flight_id),
  FOREIGN KEY (admin_id) REFERENCES admin (admin_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (scheduled_flight_id) REFERENCES scheduled_flight (flight_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE user (
  username VARCHAR(255) PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  date_of_birth DATE NOT NULL,
  gender CHAR(1) NOT NULL,
  passport VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  type ENUM('admin', 'crew', 'passenger') NOT NULL
);

CREATE TABLE ticket_payment (
  ticket_id INT NOT NULL,
  payment_id INT NOT NULL,
  PRIMARY KEY (ticket_id, payment_id),
  FOREIGN KEY (ticket_id) REFERENCES ticket (ticket_id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (payment_id) REFERENCES payment (payment_id) ON UPDATE CASCADE ON DELETE RESTRICT
);
