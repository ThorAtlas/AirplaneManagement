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


DROP PROCEDURE IF EXISTS delete_crew;
DELIMITER $$
CREATE PROCEDURE delete_crew ( IN new_crew_id INT)
BEGIN
  DECLARE check_crew INT;
  DECLARE crew_count INT;
    select crew_id into check_crew from crew where crew_id = new_crew_id;
    IF check_crew IS NULL then
    		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: No such crew exists, please try again';
    ELSE
		SELECT count(*) into crew_count from crew_on_scheduled_flight where crew_id = new_crew_id;
        IF crew_count = 0 then
			delete from crew where crew_id = new_crew_id;
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Cannot delete the crew, as it is assigned to some scheduled flights.';
		END IF;
    END IF;
END$$
DELIMITER ;

-- add new flight
drop procedure add_new_flight;
DELIMITER $$
CREATE PROCEDURE add_new_flight (IN new_flight_id INT, IN new_departure_airport_name VARCHAR(255), IN new_destination_name VARCHAR(255),
					IN new_price DECIMAL, IN new_departure_datetime DATETIME, IN new_duration TIME, IN new_seats INT, IN new_company VARCHAR(255))
BEGIN
	DECLARE new_departure_code, new_destination_code VARCHAR(255);
    DECLARE new_company_id, count_id INT;
    IF new_departure_airport_name = new_destination_name then
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Destination and Departure cannot be same';
	ELSE
	  select count(*) into count_id from scheduled_flight where flight_id = new_flight_id;
	  IF count_id > 0 then
	    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Flight id already exists, try with a different ID';
	  ELSE
		  select code into new_departure_code from airport where name = new_departure_airport_name;
		  select code into new_destination_code from airport where name = new_destination_name;
		  select cid into new_company_id from company where name = new_company;

		  INSERT INTO scheduled_flight VALUES (new_flight_id, new_departure_code, new_destination_code, new_price,
			  new_departure_datetime, new_duration, new_seats, new_company_id);
	  END IF;
	END IF;
END$$
DELIMITER ;

-- add new crew to flight
drop procedure add_crew_to_flight;
DELIMITER $$
CREATE PROCEDURE add_crew_to_flight (IN crew_id INT, IN flight_id INT)
BEGIN
  INSERT INTO crew_on_scheduled_flight VALUES (crew_id,flight_id);
END $$
DELIMITER ;

-- procedure to delete crew from flight
drop procedure delete_crew_from_flight;
DELIMITER $$
create procedure delete_crew_from_flight(IN crew_id_p INT, IN flight_id_p INT)
begin
		DECLARE crew_count INT;
SELECT COUNT(*) INTO crew_count FROM crew_on_scheduled_flight WHERE scheduled_flight_id = flight_id_p and crew_id = crew_id_p;
IF crew_count = 0 THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Crew is not scheduled to work on this flight.';
ELSE
DELETE FROM crew_on_scheduled_flight WHERE scheduled_flight_id = flight_id_p and crew_id = crew_id_p;
END IF;
end $$
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

-- procedure for getting all ticket of a passenger
-- select scheduled_flight.flight_id, scheduled_flight.departure_airport, scheduled_flight.destination_airport,
-- 	scheduled_flight.departure_datetime,scheduled_flight.duration, company.name, t1.amount, scheduled_flight.price from
-- 		(select * from ticket where passenger_id = (select passenger_id from passenger where username = "test1")) as t1
--         join scheduled_flight on scheduled_flight.flight_id = t1.scheduled_flight_id join company on company.cid = scheduled_flight.company_id;
drop procedure if exists get_all_tickets;
DELIMITER $$
CREATE PROCEDURE get_all_tickets(IN username_p VARCHAR(255))
BEGIN
    select scheduled_flight.flight_id, scheduled_flight.departure_airport, scheduled_flight.destination_airport,
           scheduled_flight.departure_datetime,scheduled_flight.duration, company.name, t1.amount, scheduled_flight.price from
        (select * from ticket where passenger_id = (select passenger_id from passenger where username = username_p)) as t1
            join scheduled_flight on scheduled_flight.flight_id = t1.scheduled_flight_id join company on company.cid = scheduled_flight.company_id;
END $$
DELIMITER ;
call get_all_tickets("test1");

-- when user buy a flight ticket, ticket table will insert a new record
drop procedure if exists create_a_new_ticket;
DELIMITER $$
CREATE PROCEDURE create_a_new_ticket(IN amount_p INT, IN details_p TEXT, IN scheduled_flight_id_p INT, IN passenger_name_p VARCHAR(255))
BEGIN
    declare passenger_id_p INT;
    declare ticket_count INT;
    select passenger_id into passenger_id_p from passenger where username = passenger_name_p;
    SELECT COUNT(*) INTO ticket_count FROM ticket WHERE scheduled_flight_id = scheduled_flight_id_p and passenger_id = passenger_id_p;
    IF ticket_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'You had bought this flight! ';
    ELSE
        INSERT INTO ticket(amount,details, scheduled_flight_id,passenger_id) VALUES (amount_p, details_p, scheduled_flight_id_p, passenger_id_p);
    END IF;
END $$
DELIMITER ;

call create_a_new_ticket(1, "", 123, "user");
-- delete from ticket where scheduled_flight_id = 123;

-- trigger, when user buy a ticket of flight, the trigger will update the sold seat of this flight
drop trigger if exists update_flight_sold_seats_after_insert_ticket;
DELIMITER $$
CREATE TRIGGER update_flight_sold_seats_after_insert_ticket AFTER INSERT ON ticket FOR EACH ROW
BEGIN
    UPDATE flight_sold_seats
    SET sold_seats = sold_seats + NEW.amount
    WHERE flight_id = NEW.scheduled_flight_id;
END $$
DELIMITER ;

-- procedure for cancel flight
drop procedure delete_ticket;
DELIMITER $$
CREATE PROCEDURE delete_ticket( IN scheduled_flight_id_p INT, IN passenger_name_p VARCHAR(255))
BEGIN
    declare passenger_id_p INT;
    declare ticket_count INT;
    select passenger_id into passenger_id_p from passenger where username = passenger_name_p;
    SELECT COUNT(*) INTO ticket_count FROM ticket WHERE scheduled_flight_id = scheduled_flight_id_p and passenger_id = passenger_id_p;
    IF ticket_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'You had not bought this flight yet! ';
    ELSE
        DELETE from ticket where scheduled_flight_id = scheduled_flight_id_p and passenger_id = passenger_id_p;
    END IF;
END $$
DELIMITER ;
call delete_ticket(19821, "user");


-- trigger, when user cancel a ticket of flight, the trigger will update the sold seat of this flight
drop trigger if exists update_flight_sold_seats_after_delete_ticket;
DELIMITER $$
CREATE TRIGGER update_flight_sold_seats_after_delete_ticket AFTER delete ON ticket FOR EACH ROW
BEGIN
    UPDATE flight_sold_seats
    SET sold_seats = sold_seats - OLD.amount
    WHERE flight_id = OLD.scheduled_flight_id;
END $$
DELIMITER ;

-- procedure update ticket
drop procedure if exists update_ticket;
DELIMITER $$
CREATE PROCEDURE update_ticket(IN username_p VARCHAR(255), IN flight_id_p INT, IN amount_p INT, IN details_p TEXT)
BEGIN
    DECLARE ticket_count INT;
    DECLARE sold_seats_num INT;
    DECLARE total_seats INT;
    DECLARE available_seats INT;
    DECLARE passenger_id_p VARCHAR(255);
    DECLARE passenger_seats INT;
    select passenger_id into passenger_id_p from passenger where username = username_p;
    SELECT COUNT(*) INTO ticket_count FROM ticket WHERE scheduled_flight_id = flight_id_p and passenger_id = passenger_id_p;

    IF ticket_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'You have not bought this flight';
    ELSE
        select sold_seats into sold_seats_num from flight_sold_seats where flight_id = flight_id_p;
        select seats into total_seats from scheduled_flight where flight_id = flight_id_p;
        select amount into passenger_seats from ticket where passenger_id = passenger_id_p and scheduled_flight_id = flight_id_p;
        set available_seats = total_seats - sold_seats_num + passenger_seats;
        IF available_seats < amount_p then
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'You cannot buy amount of tickets larger than availabe seats of this flight';
        ELSE
            UPDATE ticket SET
                              amount = amount_p,
                              details = details_p
            WHERE scheduled_flight_id = flight_id_p and passenger_id = passenger_id_p;
        END IF;
    END IF;
END $$
DELIMITER ;

call update_ticket("user", 123, 1000,"");



-- trigger update flight_sold_seats after update the ticket
drop trigger if exists update_flight_sold_seats_after_update_ticket;
DELIMITER $$
CREATE TRIGGER update_flight_sold_seats_after_update_ticket after update on ticket for each row
BEGIN
    DECLARE new_sold_seats INT;
    select sold_seats into new_sold_seats from flight_sold_seats where flight_id = NEW.scheduled_flight_id;
    UPDATE flight_sold_seats
    SET sold_seats = new_sold_seats - OLD.amount + NEW.amount
    WHERE flight_id = OLD.scheduled_flight_id;
END $$
DELIMITER ;

-- procedure: get ticket details
DELIMITER $$
CREATE PROCEDURE get_ticket_details (IN username_p VARCHAR(255), IN flight_id_p INT)
BEGIN
    DECLARE passenger_id_p VARCHAR(255);
    select passenger_id into passenger_id_p from passenger where username = username_p;
    select details from ticket where scheduled_flight_id = flight_id_p and passenger_id = passenger_id_p;
END $$
DELIMITER ;
call get_ticket_details("user", 123);

