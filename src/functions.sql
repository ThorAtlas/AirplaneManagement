use airplane_management;

-- DELIMITER $$
-- CREATE FUNCTION add_crew (new_name VARCHAR(255), new_start_date DATE, new_job_role VARCHAR(255))
-- RETURNS INT
-- BEGIN
--   INSERT INTO crew (name, start_date, job_role) VALUES (new_name, new_start_date, new_job_role);
--   RETURN LAST_INSERT_ID();
-- END$$
-- DELIMITER ;

DELIMITER $$
CREATE PROCEDURE add_crew (IN new_name VARCHAR(255), IN new_start_date DATE, IN new_job_role VARCHAR(255))
BEGIN
  INSERT INTO crew (name, start_date, job_role) VALUES (new_name, new_start_date, new_job_role);
END$$
DELIMITER ;
CALL add_crew('John Doe', '2023-04-05', 'Pilot');

-- add new flight
drop procedure add_new_flight;
DELIMITER $$
CREATE PROCEDURE add_new_flight (IN new_flight_id INT, IN new_departure_airport_name VARCHAR(255), IN new_destination_name VARCHAR(255),
					IN new_price DECIMAL, IN new_departure_datetime DATETIME, IN new_duration TIME, IN new_seats INT, IN new_company VARCHAR(255))
BEGIN
	DECLARE new_departure_code, new_destination_code VARCHAR(255);
    DECLARE new_company_id INT;
    IF new_departure_airport_name = new_destination_name then
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Destination and Departure cannot be same';
	ELSE 
		select code into new_departure_code from airport where name = new_departure_airport_name;
		select code into new_destination_code from airport where name = new_destination_name;
		select cid into new_company_id from company where name = new_company;

		INSERT INTO scheduled_flight VALUES (new_flight_id, new_departure_code, new_destination_code, new_price, 
			new_departure_datetime, new_duration, new_seats, new_company_id);
	END IF;
END$$
DELIMITER ;

-- trigger for update flight_avaiable_seats table after add new flight
DELIMITER $$
create trigger flight_available_seats_after_insert_scheduled_flight after insert on scheduled_flight for each row
	begin
		IF NOT EXISTS (SELECT * FROM flight_available_seats where flight_id = NEW.flight_id) then
			insert into flight_available_seats values (new.flight_id, new.seats);
		end if;
    end $$
DELIMITER ;
-- trigger for update company_has_flight table after add new flight
DELIMITER $$
create trigger company_flight_after_insert_scheduled_flight AFTER INSERT on scheduled_flight for each row
	begin
		IF NOT EXISTS (SELECT * FROM company_has_flight WHERE company_id = NEW.company_id AND flight_id = NEW.flight_id) THEN
        INSERT ignore INTO company_has_flight (company_id, flight_id) VALUES (NEW.company_id, NEW.flight_id);
    END IF;
    end $$
DELIMITER ;

call add_new_flight("123", "Ocean Reef Club Airport", "Loring Seaplane Base", 100.2, "2022-10-11 23:59:59", "03:21:21", 60, "American Airlines");
call add_new_flight("223", "Ocean Reef Club Airport", "Loring Seaplane Base", 132.6, "2022-10-11 23:59:59", "03:21:21", 60, "American Airlines");
select * from scheduled_flight natural join flight_available_seats join company where company.cid = scheduled_flight.company_id;
drop procedure all_flights_detail;
DELIMITER $$
create procedure all_flights_detail()
	begin
		select * from scheduled_flight natural join flight_available_seats join company where company.cid = scheduled_flight.company_id;
    end $$
DELIMITER ;
call all_flights_detail();


-- procedure for delete a flight
drop procedure delete_flight;
DELIMITER $$
create procedure delete_flight(IN flight_id_p INT)
begin
		DECLARE flight_count INT;
SELECT COUNT(*) INTO flight_count FROM scheduled_flight WHERE flight_id = flight_id_p;
IF flight_count = 0 THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Flight ID does not exist';
ELSE
DELETE FROM scheduled_flight WHERE flight_id = flight_id_p;
END IF;
end $$
DELIMITER ;

call delete_flight(123);

