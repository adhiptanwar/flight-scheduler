# flight-scheduler
A java program for airlines to use to schedule flights between different locations, producing timetable plans, and an easy way to check routing between cities on multiple flights.

## How to run?
From project directory: ```javac FlightScheduler.java``` (to compile the file), followed by ```java FlightScheduler``` (to run the file)

## How to use?

FLIGHTS - list all available flights ordered by departure time, then departure location name
FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight
FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file
FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price,
capacity, passengers booked)
FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price,
and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1
passenger. If the given number of bookings is more than the remaining capacity, only accept bookings
until the capacity is full.
FLIGHT <id> REMOVE - remove a flight from the schedule
FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.
LOCATIONS - list all available locations in alphabetical order
LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location
LOCATION <name> - view details about a location (it’s name, coordinates, demand coefficient)
LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file
SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart
DEPARTURES <location_name> - list all departing flights, in order of departure time
ARRIVALS <location_name> - list all arriving flights, in order of arrival time
TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and
destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not
provided, display the first one in the order. If n is larger than the number of flights available, display the
last one in the ordering.
can have other orderings:
TRAVEL <from> <to> cost - minimum current cost
TRAVEL <from> <to> duration - minimum total duration
TRAVEL <from> <to> stopovers - minimum stopovers
TRAVEL <from> <to> layover - minimum layover time
TRAVEL <from> <to> flight_time - minimum flight time
HELP – outputs this help string.
EXIT – end the program. 
