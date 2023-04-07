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
drop trigger flight_sold_seats_after_insert_scheduled_flight;
DELIMITER $$
create trigger flight_sold_seats_after_insert_scheduled_flight after insert on scheduled_flight for each row
begin
    insert into flight_sold_seats(flight_id) values (new.flight_id);
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
-- select * from scheduled_flight natural join flight_sold_seats join company where company.cid = scheduled_flight.company_id;
drop procedure all_flights_detail;
DELIMITER $$
create procedure all_flights_detail()
begin
    select * from scheduled_flight natural join flight_sold_seats join company where company.cid = scheduled_flight.company_id;
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

-- update flight
-- when update the flight, if this flight had been bought by passenger,
-- and the new total seats is smaller than sold seats, then show error message
drop procedure update_flight;
DELIMITER $$
create procedure update_flight(IN flight_id_p INT, IN new_departure_airport_name VARCHAR(255), IN new_destination_name VARCHAR(255),
                               IN new_price DECIMAL, IN new_departure_datetime DATETIME, IN new_duration TIME, IN new_seats INT, IN new_company VARCHAR(255))
BEGIN
    DECLARE new_departure_code, new_destination_code VARCHAR(255);
    DECLARE new_company_id INT;
    DECLARE flight_count INT;

    DECLARE sold_seats_num INT;

    IF new_departure_airport_name = new_destination_name then
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Destination and Departure cannot be same';
    ELSE
        SELECT COUNT(*) INTO flight_count FROM scheduled_flight WHERE flight_id = flight_id_p;
        IF flight_count = 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Flight ID does not exist';
        ELSE
            select sold_seats into sold_seats_num from flight_sold_seats where flight_id = flight_id_p;
            IF sold_seats_num > new_seats then
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'The new total seats are smaller than sold seats of this flight';
            ELSE
                select code into new_departure_code from airport where name = new_departure_airport_name;
                select code into new_destination_code from airport where name = new_destination_name;
                select cid into new_company_id from company where name = new_company;

                UPDATE scheduled_flight SET
                departure_airport = new_departure_code,
                destination_airport = new_destination_code,
                price = new_price,
                departure_datetime = new_departure_datetime,
                duration = new_duration,
                seats = new_seats,
                company_id = new_company_id
                WHERE flight_id = flight_id_p;
            END IF;
        END IF;
    END IF;
END $$
DELIMITER ;

call update_flight("123", "Ocean Reef Club Airport", "Loring Seaplane Base", 200, "2022-10-11 23:59:59", "03:21:21", 100, "American Airlines");

-- procedure to get ticket amount of specific user
-- select passenger.*, ticket.ticket_id, ticket.amount from passenger left join ticket on ticket.passenger_id = passenger.passenger_id;

-- select t1.passenger_id, t1.username, SUM(ifnull(t1.amount,0)) as ticket_total from
-- 	(select passenger.*, ticket.ticket_id, ticket.amount from passenger left join ticket on ticket.passenger_id = passenger.passenger_id) as t1
--     group by t1.passenger_id;
drop procedure get_ticket_total_of_passenger;
DELIMITER $$
create procedure get_ticket_total_of_passenger()
BEGIN
    select t1.username,t1.name, SUM(ifnull(t1.amount,0)) as ticket_total from
        (select passenger.*, ticket.ticket_id, ticket.amount from passenger left join ticket on ticket.passenger_id = passenger.passenger_id) as t1
    group by t1.passenger_id;
END $$
DELIMITER ;

call get_ticket_total_of_passenger();

-- function check if passenger exists
drop function passenger_exists;
DELIMITER $$
CREATE FUNCTION passenger_exists(username_p VARCHAR(255))
    RETURNS int deterministic reads sql data
BEGIN
    DECLARE user_count INT;
    SELECT COUNT(*) INTO user_count FROM passenger WHERE username = username_p;
    RETURN (user_count > 0);
END $$
DELIMITER ;

select passenger_exists("w");

-- procedure for insert record of admin created a scheduled flight
drop procedure add_admin_scheduled_flight;
DELIMITER $$
CREATE PROCEDURE add_admin_scheduled_flight(IN admin_username_p VARCHAR(255), flight_id_p INT)
BEGIN
    declare id INT;
    select admin_id into id from admin where username = admin_username_p;
    INSERT ignore into admin_scheduled_flight values (id, flight_id_p);
END $$
DELIMITER ;

call add_admin_scheduled_flight("admin", 19821);

-- procedure for funding the creator by providing flight id
-- select username from admin where admin_id = (select admin_id from admin_scheduled_flight where scheduled_flight_id = 123);
DROP PROCEDURE find_admin_of_flight;
DELIMITER $$
CREATE PROCEDURE find_admin_of_flight(flight_id_p INT)
BEGIN
    DECLARE flight_count INT;
    SELECT COUNT(*) INTO flight_count FROM scheduled_flight WHERE flight_id = flight_id_p;
    IF flight_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Flight ID does not exist';
    ELSE
        select username from admin where admin_id = (select admin_id from admin_scheduled_flight where scheduled_flight_id = flight_id_p);
    END IF;
END $$
DELIMITER ;

call find_admin_of_flight(22222);

-- procedure for getting all passengers of specific flight
-- select passenger.username, passenger.name, t1.amount, t1.details from (select * from ticket where scheduled_flight_id = 123) as t1
-- 	join passenger on passenger.passenger_id = t1.passenger_id;

DELIMITER $$
CREATE PROCEDURE get_all_passengers(IN flight_id_p INT)
BEGIN
    select passenger.username, passenger.name, t1.amount, t1.details from (select * from ticket where scheduled_flight_id = flight_id_p) as t1
                                                                              join passenger on passenger.passenger_id = t1.passenger_id;
END $$
DELIMITER ;
call get_all_passengers(123);

