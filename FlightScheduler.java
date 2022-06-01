import java.util.*;
import java.lang.*;
import java.io.*;

public class FlightScheduler {

    private static FlightScheduler instance;


	private HashMap<String, Location> locations = new HashMap<String, Location>();
	private TreeMap<String, Location> exportLocations = new TreeMap<String, Location>();

	private HashMap<Integer, Flight> flights = new HashMap<Integer, Flight>();
	// private TreeMap<Integer, Flight> sortedFlights = new TreeMap<Integer, Flight>();
	private ArrayList<Flight> sortedFlights = new ArrayList<Flight>();

	private TreeMap<Integer, Flight> exportSortedFlights = new TreeMap<Integer, Flight>();


	public Location Source;
	public Location Destination;
	public int counter;
	public String outputMessageD;
	public String outputMessageA;

    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public static FlightScheduler getInstance() {
        return instance;
    }

    public FlightScheduler(String[] args) {
		this.counter = 0;
		this.outputMessageD = "";
		this.outputMessageA = "";
	}

    public void run() {
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.

        // START YOUR CODE HERE
		Scanner scan = new Scanner(System.in);
		boolean act = true;

		while(act){
			System.out.print("User: ");

			String input = scan.nextLine();
			String[] inputList = input.split(" ");

			// command handling begins here

			// command exit
			if(inputList[0].equalsIgnoreCase("exit")){
				System.out.println("Application closed.");
				act = false;
			}

			// Command help
			else if(inputList[0].equalsIgnoreCase("help")){
				System.out.println("FLIGHTS - list all available flights ordered by departure time, then departure location name\nFLIGHT ADD <departure time> <from> <to> <capacity> - add a flight\nFLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file\nFLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)\nFLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings until the capacity is full.\nFLIGHT <id> REMOVE - remove a flight from the schedule\nFLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.");
				System.out.println();
				System.out.println("LOCATIONS - list all available locations in alphabetical order\nLOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location\nLOCATION <name> - view details about a location (it's name, coordinates, demand coefficient)\nLOCATION IMPORT/EXPORT <filename> - import/export locations to csv file\nSCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart\nDEPARTURES <location_name> - list all departing flights, in order of departure time\nARRIVALS <location_name> - list all arriving flights, in order of arrival time");
				System.out.println();
				System.out.println("TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not provided, display the first one in the order. If n is larger than the number of flights available, display the last one in the ordering.");
				System.out.println();
				System.out.println("can have other orderings:\nTRAVEL <from> <to> cost - minimum current cost\nTRAVEL <from> <to> duration - minimum total duration\nTRAVEL <from> <to> stopovers - minimum stopovers\nTRAVEL <from> <to> layover - minimum layover time\nTRAVEL <from> <to> flight_time - minimum flight time");
				System.out.println();
				System.out.println("HELP - outputs this help string.\nEXIT - end the program.");
				System.out.println();
			}

			else if(inputList[0].equalsIgnoreCase("schedule")){
				if (inputList.length < 2){
					System.out.println("This location does not exist in the system.");
					System.out.println();
				}
				else{
					String inputName = inputList[1];
					Location A = null;
					for (HashMap.Entry<String,Location> entry : this.locations.entrySet()){
						if(entry.getKey().equalsIgnoreCase(inputName)){
							A = this.locations.get(entry.getKey());
						}
					}

					if(A == null){
						System.out.println("This location does not exist in the system.");
						System.out.println();
					}
					else{
						System.out.println(A.getName());
						System.out.println("-------------------------------------------------------");
						System.out.printf("%-5s%-12s%-18s%-8s%-8s\n", "ID", "Time", "Departure/Arrival", "to/from", "Location");
						System.out.println("-------------------------------------------------------");

						Iterator itr = A.allFlights.keySet().iterator();
						while (itr.hasNext()){
							int key = (int)itr.next();
							System.out.printf("%4d", A.allFlights.get(key).getFlightID());

							if(A.allFlights.get(key).source.getName().equalsIgnoreCase(A.getName())){
								System.out.printf(" %-12s%-12s\n", A.allFlights.get(key).DepartureTime.getTimePrint(), ("Departure to " + A.allFlights.get(key).destination.getName()));
							}
							else{
								System.out.printf(" %-12s%-12s\n", A.allFlights.get(key).ArrivalTime.getTimePrint(), ("Arrival from " + A.allFlights.get(key).source.getName()));
							}
						}
						System.out.println();

					}
				}

				// System.out.println();
			}

			else if(inputList[0].equalsIgnoreCase("arrivals")){
				if (inputList.length < 2){
					System.out.println("This location does not exist in the system.");
					System.out.println();
				}
				else{
					String inputName = inputList[1];
					Location A = null;
					for (HashMap.Entry<String,Location> entry : this.locations.entrySet()){
						if(entry.getKey().equalsIgnoreCase(inputName)){
							A = this.locations.get(entry.getKey());
						}
					}

					if(A == null){
						System.out.println("This location does not exist in the system.");
						System.out.println();
					}
					else{
						System.out.println(A.getName());
						System.out.println("-------------------------------------------------------");
						System.out.printf("%-5s%-12s%-18s%-8s%-8s\n", "ID", "Time", "Departure/Arrival", "to/from", "Location");
						System.out.println("-------------------------------------------------------");

						Comparator<Flight> compareByTime = new Comparator<Flight>() {
							@Override
							public int compare(Flight f1, Flight f2) {
								return Double.compare(f1.ArrivalTime.getTimeMins(), f2.ArrivalTime.getTimeMins());
							}
						};

						Collections.sort(A.arrivals, compareByTime);

						for(Flight i : A.arrivals){
							System.out.printf("%4d", i.getFlightID());
							System.out.printf(" %-12s%-12s\n", i.ArrivalTime.getTimePrint(), ("Arrival from " + i.source.getName()));
						}
						System.out.println();

					}
				}

				// System.out.println();
			}

			else if(inputList[0].equalsIgnoreCase("departures")){
				if (inputList.length < 2){
					System.out.println("This location does not exist in the system.");
					System.out.println();
				}
				else{
					String inputName = inputList[1];
					Location A = null;
					for (HashMap.Entry<String,Location> entry : this.locations.entrySet()){
						if(entry.getKey().equalsIgnoreCase(inputName)){
							A = this.locations.get(entry.getKey());
						}
					}

					if(A == null){
						System.out.println("This location does not exist in the system.");
						System.out.println();
					}
					else{
						System.out.println(A.getName());
						System.out.println("-------------------------------------------------------");
						System.out.printf("%-5s%-12s%-18s%-8s%-8s\n", "ID", "Time", "Departure/Arrival", "to/from", "Location");
						System.out.println("-------------------------------------------------------");

						Comparator<Flight> compareByTime = new Comparator<Flight>() {
							@Override
							public int compare(Flight f1, Flight f2) {
								return Double.compare(f1.DepartureTime.getTimeMins(), f2.DepartureTime.getTimeMins());
							}
						};

						Collections.sort(A.departures, compareByTime);

						for(Flight i : A.departures){
							System.out.printf("%4d", i.getFlightID());
							System.out.printf(" %-12s%-12s\n", i.DepartureTime.getTimePrint(), ("Departure to " + i.destination.getName()));
						}
						System.out.println();

					}
				}

			}

			// Command: locations
			else if (inputList[0].equalsIgnoreCase("locations")){
				int a = 0;
				SortedSet<String> sortedNames = new TreeSet<String>(locations.keySet());
				StringBuilder str = new StringBuilder();

				for(String i : sortedNames){
					str.append(i + ", ");
					a += 1;
				}

				if(str.length()>0){
					str.setLength(str.length() - 2);
				}
				System.out.println("Locations ("+a+"):");
				if(a == 0){
					System.out.println("(None)");
				}
				else{
					System.out.println(str);
				}
				System.out.println();

			}

			else if(inputList[0].equalsIgnoreCase("location")){
				if(inputList.length < 50){

					if (inputList.length == 1){
						System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD <name> <latitude> <longitude> <demand_coefficient>\nLOCATION IMPORT/EXPORT <filename>");
						System.out.println();
					}
					else{

						if(inputList[1].equalsIgnoreCase("add")){
							if(inputList.length < 6){
								System.out.println("Usage:   LOCATION ADD <name> <lat> <long> <demand_coefficient>\nExample: LOCATION ADD Sydney -33.847927 150.651786 0.2");
							}
							else{
								int result = this.addLocation(inputList[2], inputList[3], inputList[4], inputList[5]);
								if(result == -1){
									System.out.println("This location already exists.");
								}
								else if(result == -2){
									System.out.println("Invalid latitude. It must be a number of degrees between -85 and +85.");
								}
								else if(result == -3){
									System.out.println("Invalid longitude. It must be a number of degrees between -180 and +180.");
								}
								else if(result == -4){
									System.out.println("Invalid demand coefficient. It must be a number between -1 and +1.");
								}
								else{
									System.out.println("Successfully added location " + inputList[2] + ".");
								}

							}
							System.out.println();

						}

						else if (inputList[1].equalsIgnoreCase("import")){
							this.importLocations(inputList);
							System.out.println();
						}

						else if (inputList[1].equalsIgnoreCase("export")){
							if(inputList.length < 3){
								System.out.println("Error writing file.");
							}
							else{
								int counter = 0;
								File f = new File(inputList[2]);
								try{
									PrintWriter writer = new PrintWriter(f);
									Set<String> Set1 = this.exportLocations.keySet();
									for(String key : Set1) {
										writer.println(this.exportLocations.get(key).getName()+ "," + this.exportLocations.get(key).getLat() + "," + this.exportLocations.get(key).getLon() + "," + this.exportLocations.get(key).getDemand());
										counter += 1;
									}
									writer.close();
									System.out.println("Exported " + counter + " locations.");

								}catch(FileNotFoundException e){
									System.out.println("Error writing file.");
								}
							}

							System.out.println();

						}
						else{
							String inputName = inputList[1];
							Location A = null;
							for (HashMap.Entry<String,Location> entry : locations.entrySet()){
								if(entry.getKey().equalsIgnoreCase(inputName)){
									A = this.locations.get(entry.getKey());
								}
							}

							if(A == null){
								System.out.println("Invalid location name.");
								System.out.println();
							}
							else{
								System.out.printf("Location:    %s\n", A.getName());
								System.out.printf("Latitude:    %.6f\n", A.getLat());
								System.out.printf("Longitude:   %.6f\n", A.getLon());
								System.out.printf("Demand:      %+.4f\n", A.getDemand());
								System.out.println();
							}
						}
					}

				}
				else{
					System.out.println("Invalid Error Case.");
				}

			}

			// Command: Flights
			else if(inputList[0].equalsIgnoreCase("flights")){
				System.out.println("Flights");
				System.out.println("-------------------------------------------------------");
				System.out.printf("%-5s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Departure", "Arrival", "Source", "-->", "Destination");
				System.out.println("-------------------------------------------------------");
				if(this.sortedFlights.size() == 0){
					System.out.println("(None)");
				}
				else{

					Comparator<Flight> compareByTime = new Comparator<Flight>() {
						@Override
						public int compare(Flight f1, Flight f2) {
							// return Double.compare(f1.DepartureTime.getTimeMins(), f1.DepartureTime.getTimeMins());
							if (f1.DepartureTime.getTimeMins() > f2.DepartureTime.getTimeMins()) {
								return 1;
							} else if (f2.DepartureTime.getTimeMins() > f1.DepartureTime.getTimeMins()) {
								return -1;
							}
							else{
								return f1.source.getName().toLowerCase().compareTo(f2.source.getName().toLowerCase());
							}
						}
					};

					Collections.sort(this.sortedFlights, compareByTime);

					for(Flight i : this.sortedFlights){
						System.out.printf("%4d", i.getFlightID());
						System.out.printf(" %-12s%-12s%-12s\n", i.DepartureTime.getTimePrint(), i.ArrivalTime.getTimePrint(), (i.source.getName() + " --> " + i.destination.getName()));
					}

				}

				System.out.println();

			}

			// All Commands starting with Flight:
			else if(inputList[0].equalsIgnoreCase("flight")){
				if(inputList.length > 1){
					if(inputList[1].equalsIgnoreCase("add")){
						if(inputList.length < 7){
							System.out.println("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\nExample: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
							System.out.println();
						}
						else{
							int book = 0;
							int result = this.addFlight(inputList[2], inputList[3], inputList[4], inputList[5], inputList[6], book);
							if(result == -1){
								System.out.println("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
								System.out.println();
							}
							else if(result == -2){
								System.out.println("Invalid starting location.");
								System.out.println();
							}
							else if(result == -3){
								System.out.println("Invalid ending location.");
								System.out.println();
							}
							else if(result == -4){
								System.out.println("Invalid positive integer capacity.");
								System.out.println();
							}
							else if(result == -5){
								System.out.println("Source and destination cannot be the same place.");
								System.out.println();
							}
							else if(result == -6){
								System.out.println(this.outputMessageD);
								System.out.println();
							}
							else if(result == -7){
								System.out.println(this.outputMessageA);
								System.out.println();
							}
							else{
								System.out.println("Successfully added Flight " + (this.counter - 1) + ".");
								System.out.println();
							}

						}
					}
					else if(inputList[1].equalsIgnoreCase("import")){
						if(inputList.length < 3){
							System.out.println("Error reading file.");
							System.out.println();
						}
						else{
							this.importFlights(inputList);
							System.out.println();
						}
					}
					else if(inputList[1].equalsIgnoreCase("export")){
						if(inputList.length < 3){
							System.out.println("Error writing file.");
							System.out.println();
						}
						else{
							int counter = 0;
							File f = new File(inputList[2]);
							try{
								PrintWriter writer = new PrintWriter(f);
								// for(HashMap.Entry<Integer,Flight> entry : this.flights.entrySet()){
								// 	writer.println(entry.getValue().DepartureTime.getTime() + "," + entry.getValue().source.getName() + "," + entry.getValue().destination.getName() + "," + entry.getValue().getCapacity() + "," + entry.getValue().getNumberOfPassengers());
								// 	counter += 1;
								// }
								Set<Integer> Set1 = this.exportSortedFlights.keySet();
								for(Integer key : Set1) {
									writer.println(this.exportSortedFlights.get(key).DepartureTime.getTime() + "," + this.exportSortedFlights.get(key).source.getName() + "," + this.exportSortedFlights.get(key).destination.getName() + "," + this.exportSortedFlights.get(key).getCapacity() + "," + this.exportSortedFlights.get(key).getNumberOfPassengers());
									counter += 1;
								}
								writer.close();
								System.out.println("Exported " + counter + " flights.");
							}catch(FileNotFoundException e){
								System.out.println("Error writing file.");
							}
							System.out.println();
						}
					}
					else if(inputList[1].equalsIgnoreCase("book") || inputList[1].equalsIgnoreCase("remove") || inputList[1].equalsIgnoreCase("reset")){
						System.out.println("Invalid Flight ID.");
						System.out.println();
					}
					else if(inputList.length > 2 && inputList[2].equalsIgnoreCase("remove")){
						try{
							int ID = Integer.parseInt(inputList[1]);
							Flight f = this.flights.get(ID);
							if (f == null){
								System.out.println("Invalid Flight ID.");
								System.out.println();
							}
							else{

								f.source.removeFlight(f);
								f.destination.removeFlight(f);

								f.source.departures.remove(f);
								f.destination.arrivals.remove(f);

								System.out.println("Removed Flight " + ID +", " + f.DepartureTime.getTimePrint() + " " + f.source.getName() + " --> " + f.destination.getName() + ", from the flight schedule.");
								this.exportSortedFlights.remove(f.getFlightID());
								// this.sortedFlights.remove(f.DepartureTime.getTimeMins());
								this.sortedFlights.remove(f);
								this.flights.remove(ID);
								System.out.println();
							}
						}catch(NumberFormatException e){
							System.out.println("Invalid Flight ID.");
							System.out.println();
						}
					}
					else if(inputList.length > 2 && inputList[2].equalsIgnoreCase("reset")){
						try{
							int ID = Integer.parseInt(inputList[1]);
							Flight f = this.flights.get(ID);
							if (f == null){
								System.out.println("Invalid Flight ID.");
								System.out.println();
							}
							else{
								f.setNumberOfPassengers(0.0);
								System.out.println("Reset passengers booked to 0 for Flight " + ID + ", " + f.DepartureTime.getTimePrint() + " " + f.source.getName() + " --> " + f.destination.getName() + ".");
								System.out.println();
							}
						}catch (NumberFormatException e){
							System.out.println("Invalid Flight ID.");
							System.out.println();
						}
					}
					else if(inputList.length > 2 && inputList[2].equalsIgnoreCase("book")){
						if(inputList.length < 4){
							try{
								int ID = Integer.parseInt(inputList[1]);
								Flight A = this.flights.get(ID);

								if(A == null){
									System.out.println("Invalid Flight ID.");
								}

								else if(A.isFull()){
									System.out.println("Booked 0 passengers on flight " + ID + " for a total cost of $0.00");
									System.out.println("Flight is now full.");
								}
								else{
									double cost = Math.round(A.book(1) * 100.0)/100.0;
									System.out.printf("Booked 1 passengers on flight " + ID + " for a total cost of $%.2f\n", cost);
									if(A.isFull()){
										System.out.println("Flight is now full.");
									}
								}

							}catch (NumberFormatException e){
								System.out.println("Invalid Flight ID.");
							}

						}

						else{
							try{
								int ID = Integer.parseInt(inputList[1]);
								Flight A = this.flights.get(ID);

								try{
									int number = Integer.parseInt(inputList[3]);

									if(A == null){
										System.out.println("Invalid Flight ID.");
									}
									else if(A.isFull()){
										System.out.println("Booked 0 passengers on flight " + ID + " for a total cost of $0.00");
										System.out.println("Flight is now full.");
									}
									else if (number < 0){
										System.out.println("Invalid number of passengers to book.");
									}
									else {
										int booked = A.numberBooked(number);
										double cost1 = Math.round(A.book(number) * 100.0)/100.0;
										System.out.printf("Booked " + booked + " passengers on flight " + ID + " for a total cost of $%.2f\n", cost1);
										if(A.isFull()){
											System.out.println("Flight is now full.");
										}
									}
								}catch(NumberFormatException e){
									System.out.println("Invalid number of passengers to book.");
								}


							}catch (NumberFormatException e){
								System.out.println("Invalid Flight ID.");
							}
						}
						System.out.println();
					}
					else{
						try{
							int ID = Integer.parseInt(inputList[1]);
							Flight A = this.flights.get(ID);
							if(A == null){
								System.out.println("Invalid Flight ID.");
								System.out.println();
							}
							else{
								System.out.println("Flight " + ID);
								System.out.printf("Departure:    %s\n", A.DepartureTime.getTimePrint() + " " + this.flights.get(ID).source.getName());
								System.out.printf("Arrival:      %s\n", A.ArrivalTime.getTimePrint() + " " + this.flights.get(ID).destination.getName());
								System.out.printf("Distance:     %,d", A.getIntDistance());
								System.out.print("km\n");
								System.out.printf("Duration:     %s\n", A.getDurationPrint());
								System.out.printf("Ticket Cost:  $%.2f\n", A.getTicketPrice());
								System.out.printf("Passengers:   %s\n", A.getBookedStatus());
								System.out.println();
							}
						}catch(NumberFormatException e){
							System.out.println("Invalid Flight ID.");
							System.out.println();
						}

					}

				}
				else{
					System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\nFLIGHT ADD <departure time> <from> <to> <capacity>\nFLIGHT IMPORT/EXPORT <filename>");
					System.out.println();
				}
			}



			else if(inputList[0].equalsIgnoreCase("travel")){
				if(inputList.length < 3){
					System.out.println("Usage: TRAVEL <from> <to> [cost/duration/stopovers/layover/flight_time]");
					System.out.println();
				}
				else {
					String startName = inputList[1];
					Location start = null;
					for (HashMap.Entry<String,Location> entry : locations.entrySet()){
						if(entry.getKey().equalsIgnoreCase(startName)){
							start = this.locations.get(entry.getKey());
						}
					}

					String endName = inputList[2];
					Location end = null;
					for (HashMap.Entry<String,Location> entry : locations.entrySet()){
						if(entry.getKey().equalsIgnoreCase(endName)){
							end = this.locations.get(entry.getKey());
						}
					}

					if (start == null){
						System.out.println("Starting location not found.");
						System.out.println();
					}
					else if(end == null){
						System.out.println("Ending location not found.");
						System.out.println();
					}
					else{
						ArrayList<ArrayList<Flight>> allPaths = new ArrayList<ArrayList<Flight>>();
						for(Flight i : start.departures){
							// direct flight
							if(i.destination.getName().equalsIgnoreCase(end.getName())){
								ArrayList<Flight> path = new ArrayList<Flight>();
								path.add(i);
								allPaths.add(path);
							}
							// 1st StopOver
							for(Flight j : i.destination.departures){
								if(j.source.getName().equalsIgnoreCase(end.getName())){
									continue;
								}
								else if(j.destination.getName().equalsIgnoreCase(end.getName())){
									ArrayList<Flight> path1 = new ArrayList<Flight>();
									path1.add(i);
									path1.add(j);
									allPaths.add(path1);
								}

								// 2nd Stopover
								for(Flight k : j.destination.departures){
									if(k.source.getName().equalsIgnoreCase(end.getName())){
										continue;
									}
									else if(k.destination.getName().equalsIgnoreCase(end.getName())){
										ArrayList<Flight> path2 = new ArrayList<Flight>();
										path2.add(i);
										path2.add(j);
										path2.add(k);
										allPaths.add(path2);
									}

									// 3rd stopover
									for(Flight h : k.destination.departures){
										if(h.source.getName().equalsIgnoreCase(end.getName())){
											continue;
										}
										else if(h.destination.getName().equalsIgnoreCase(end.getName())){
											ArrayList<Flight> path3 = new ArrayList<Flight>();
											path3.add(i);
											path3.add(j);
											path3.add(k);
											path3.add(h);
											allPaths.add(path3);
										}
									}
								}
							}
						}

						// Defining All Comparators:
						Comparator<ArrayList<Flight>> compareByDuration = new Comparator<ArrayList<Flight>>(){
							@Override
							public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
								if (p1.get(0).getCombinedDuration(p1) > p2.get(0).getCombinedDuration(p2)) {
									return 1;
								} else if (p2.get(0).getCombinedDuration(p2) > p1.get(0).getCombinedDuration(p1)) {
									return -1;
								}
								else{
									return Double.compare(p1.get(0).getCombinedTicketPrice(p1), p2.get(0).getCombinedTicketPrice(p2));
								}

							}
						};

						if(inputList.length == 3){
							if(allPaths.size() == 0){
								System.out.println("Sorry, no flights with 3 or less stopovers are available from " + start.getName() + " to " + end.getName() + ".");
								System.out.println();
							}
							else{
								Collections.sort(allPaths, compareByDuration);
								System.out.printf("Legs:             %d\n", allPaths.get(0).size());
								System.out.printf("Total Duration:   %s\n", allPaths.get(0).get(0).getTotalDurationPrint(allPaths.get(0)));
								System.out.printf("Total Cost:       $%.2f\n", allPaths.get(0).get(0).getCombinedTicketPrice(allPaths.get(0)));
								System.out.println("-------------------------------------------------------------");
								System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
								System.out.println("-------------------------------------------------------------");

								for(int i = 0; i < allPaths.get(0).size() - 1; i++){
									System.out.printf("%4d $ ", allPaths.get(0).get(i).getFlightID());
									System.out.printf("%7.2f", allPaths.get(0).get(i).getTicketPrice());
									System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(0).get(i).DepartureTime.getTimePrint(), allPaths.get(0).get(i).ArrivalTime.getTimePrint(), (allPaths.get(0).get(i).source.getName() + " --> " + allPaths.get(0).get(i).destination.getName()));
									System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(0).get(i), allPaths.get(0).get(i + 1))) + " at " + allPaths.get(0).get(i).destination.getName());
								}
								System.out.printf("%4d $ ", allPaths.get(0).get(allPaths.get(0).size() - 1).getFlightID());
								System.out.printf("%7.2f", allPaths.get(0).get(allPaths.get(0).size() - 1).getTicketPrice());
								System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(0).get(allPaths.get(0).size() - 1).DepartureTime.getTimePrint(), allPaths.get(0).get(allPaths.get(0).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(0).get(allPaths.get(0).size() - 1).source.getName() + " --> " + allPaths.get(0).get(allPaths.get(0).size() - 1).destination.getName()));
								System.out.println();
							}

						}
						else{

							if(inputList[3].equalsIgnoreCase("cost")){
								Comparator<ArrayList<Flight>> compareByCost = new Comparator<ArrayList<Flight>>(){
									@Override
									public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
										if (p1.get(0).getCombinedTicketPrice(p1) > p2.get(0).getCombinedTicketPrice(p2)) {
											return 1;
										} else if (p2.get(0).getCombinedTicketPrice(p2) > p1.get(0).getCombinedTicketPrice(p1)) {
											return -1;
										}
										else{
											return Double.compare(p1.get(0).getCombinedDuration(p1), p2.get(0).getCombinedDuration(p2));
										}

									}
								};

								Collections.sort(allPaths, compareByCost);


							}
							else if(inputList[3].equalsIgnoreCase("duration")){
								Comparator<ArrayList<Flight>> compareByDuration2 = new Comparator<ArrayList<Flight>>(){
									@Override
									public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
										if (p1.get(0).getCombinedDuration(p1) > p2.get(0).getCombinedDuration(p2)) {
											return 1;
										} else if (p2.get(0).getCombinedDuration(p2) > p1.get(0).getCombinedDuration(p1)) {
											return -1;
										}
										else{
											return Double.compare(p1.get(0).getCombinedTicketPrice(p1), p2.get(0).getCombinedTicketPrice(p2));
										}

									}
								};

								Collections.sort(allPaths, compareByDuration2);


							}
							else if(inputList[3].equalsIgnoreCase("stopovers")){
								Comparator<ArrayList<Flight>> compareByStopovers = new Comparator<ArrayList<Flight>>(){
									@Override
									public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
										if (p1.get(0).getStopovers(p1) > p2.get(0).getStopovers(p2)) {
											return 1;
										} else if (p2.get(0).getStopovers(p2) > p1.get(0).getStopovers(p1)) {
											return -1;
										}
										else{
											if (p1.get(0).getCombinedDuration(p1) > p2.get(0).getCombinedDuration(p2)) {
												return 1;
											} else if (p2.get(0).getCombinedDuration(p2) > p1.get(0).getCombinedDuration(p1)) {
												return -1;
											}
											else{
												return Double.compare(p1.get(0).getCombinedTicketPrice(p1), p2.get(0).getCombinedTicketPrice(p2));
											}
										}

									}
								};

								Collections.sort(allPaths, compareByStopovers);


							}
							else if(inputList[3].equalsIgnoreCase("layover")){
								Comparator<ArrayList<Flight>> compareByLayover = new Comparator<ArrayList<Flight>>(){
									@Override
									public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
										if (p1.get(0).getLayoverTime(p1) > p2.get(0).getLayoverTime(p2)) {
											return 1;
										} else if (p2.get(0).getLayoverTime(p2) > p1.get(0).getLayoverTime(p1)) {
											return -1;
										}
										else{
											if (p1.get(0).getCombinedDuration(p1) > p2.get(0).getCombinedDuration(p2)) {
												return 1;
											} else if (p2.get(0).getCombinedDuration(p2) > p1.get(0).getCombinedDuration(p1)) {
												return -1;
											}
											else{
												return Double.compare(p1.get(0).getCombinedTicketPrice(p1), p2.get(0).getCombinedTicketPrice(p2));
											}
										}

									}
								};

								Collections.sort(allPaths, compareByLayover);

							}
							else if(inputList[3].equalsIgnoreCase("flight_time")){
								Comparator<ArrayList<Flight>> compareByFlightTime = new Comparator<ArrayList<Flight>>(){
									@Override
									public int compare(ArrayList<Flight> p1, ArrayList<Flight> p2) {
										if (p1.get(0).getCombinedFlightTime(p1) > p2.get(0).getCombinedFlightTime(p2)) {
											return 1;
										} else if (p2.get(0).getCombinedFlightTime(p2) > p1.get(0).getCombinedFlightTime(p1)) {
											return -1;
										}
										else{
											if (p1.get(0).getCombinedDuration(p1) > p2.get(0).getCombinedDuration(p2)) {
												return 1;
											} else if (p2.get(0).getCombinedDuration(p2) > p1.get(0).getCombinedDuration(p1)) {
												return -1;
											}
											else{
												return Double.compare(p1.get(0).getCombinedTicketPrice(p1), p2.get(0).getCombinedTicketPrice(p2));
											}
										}

									}
								};

								Collections.sort(allPaths, compareByFlightTime);


							}


							if(inputList[3].equalsIgnoreCase("cost") == false && inputList[3].equalsIgnoreCase("duration") == false && inputList[3].equalsIgnoreCase("stopovers") == false && inputList[3].equalsIgnoreCase("layover") == false && inputList[3].equalsIgnoreCase("flight_time") == false ){
								System.out.println("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
								System.out.println();
							}
							else if(allPaths.size() == 0){
								System.out.println("Sorry, no flights with 3 or less stopovers are available from " + start.getName() + " to " + end.getName() + ".");
								System.out.println();
							}
							else{
								if(inputList.length == 4){
									// System.out.println(allPaths.size());
									System.out.printf("Legs:             %d\n", allPaths.get(0).size());
									System.out.printf("Total Duration:   %s\n", allPaths.get(0).get(0).getTotalDurationPrint(allPaths.get(0)));
									System.out.printf("Total Cost:       $%.2f\n", allPaths.get(0).get(0).getCombinedTicketPrice(allPaths.get(0)));
									System.out.println("-------------------------------------------------------------");
									System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
									System.out.println("-------------------------------------------------------------");

									for(int i = 0; i < allPaths.get(0).size() - 1; i++){
										System.out.printf("%4d $ ", allPaths.get(0).get(i).getFlightID());
										System.out.printf("%7.2f", allPaths.get(0).get(i).getTicketPrice());
										System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(0).get(i).DepartureTime.getTimePrint(), allPaths.get(0).get(i).ArrivalTime.getTimePrint(), (allPaths.get(0).get(i).source.getName() + " --> " + allPaths.get(0).get(i).destination.getName()));
										System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(0).get(i), allPaths.get(0).get(i + 1))) + " at " + allPaths.get(0).get(i).destination.getName());
									}
									System.out.printf("%4d $ ", allPaths.get(0).get(allPaths.get(0).size() - 1).getFlightID());
									System.out.printf("%7.2f", allPaths.get(0).get(allPaths.get(0).size() - 1).getTicketPrice());
									System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(0).get(allPaths.get(0).size() - 1).DepartureTime.getTimePrint(), allPaths.get(0).get(allPaths.get(0).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(0).get(allPaths.get(0).size() - 1).source.getName() + " --> " + allPaths.get(0).get(allPaths.get(0).size() - 1).destination.getName()));
									System.out.println();
								}
								else{
									try{
										int n = Integer.parseInt(inputList[4]);
										if(n >= 0){
											if(n >= allPaths.size()){
												// System.out.println(allPaths.size());
												n = allPaths.size() - 1 ;
												System.out.printf("Legs:             %d\n", allPaths.get(n).size());
												System.out.printf("Total Duration:   %s\n", allPaths.get(n).get(0).getTotalDurationPrint(allPaths.get(n)));
												System.out.printf("Total Cost:       $%.2f\n", allPaths.get(n).get(0).getCombinedTicketPrice(allPaths.get(n)));
												System.out.println("-------------------------------------------------------------");
												System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
												System.out.println("-------------------------------------------------------------");

												for(int i = 0; i < allPaths.get(n).size() - 1; i++){
													System.out.printf("%4d $ ", allPaths.get(n).get(i).getFlightID());
													System.out.printf("%7.2f", allPaths.get(n).get(i).getTicketPrice());
													System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(n).get(i).DepartureTime.getTimePrint(), allPaths.get(n).get(i).ArrivalTime.getTimePrint(), (allPaths.get(n).get(i).source.getName() + " --> " + allPaths.get(n).get(i).destination.getName()));
													System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(n).get(i), allPaths.get(n).get(i + 1))) + " at " + allPaths.get(n).get(i).destination.getName());
												}
												System.out.printf("%4d $ ", allPaths.get(n).get(allPaths.get(n).size() - 1).getFlightID());
												System.out.printf("%7.2f", allPaths.get(n).get(allPaths.get(n).size() - 1).getTicketPrice());
												System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(n).get(allPaths.get(n).size() - 1).DepartureTime.getTimePrint(), allPaths.get(n).get(allPaths.get(n).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(n).get(allPaths.get(n).size() - 1).source.getName() + " --> " + allPaths.get(n).get(allPaths.get(n).size() - 1).destination.getName()));
												System.out.println();
											}
											else{
												System.out.printf("Legs:             %d\n", allPaths.get(n).size());
												System.out.printf("Total Duration:   %s\n", allPaths.get(n).get(0).getTotalDurationPrint(allPaths.get(n)));
												System.out.printf("Total Cost:       $%.2f\n", allPaths.get(n).get(0).getCombinedTicketPrice(allPaths.get(n)));
												System.out.println("-------------------------------------------------------------");
												System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
												System.out.println("-------------------------------------------------------------");

												for(int i = 0; i < allPaths.get(n).size() - 1; i++){
													System.out.printf("%4d $ ", allPaths.get(n).get(i).getFlightID());
													System.out.printf("%7.2f", allPaths.get(n).get(i).getTicketPrice());
													System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(n).get(i).DepartureTime.getTimePrint(), allPaths.get(n).get(i).ArrivalTime.getTimePrint(), (allPaths.get(n).get(i).source.getName() + " --> " + allPaths.get(n).get(i).destination.getName()));
													System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(n).get(i), allPaths.get(n).get(i + 1))) + " at " + allPaths.get(n).get(i).destination.getName());
												}
												System.out.printf("%4d $ ", allPaths.get(n).get(allPaths.get(n).size() - 1).getFlightID());
												System.out.printf("%7.2f", allPaths.get(n).get(allPaths.get(n).size() - 1).getTicketPrice());
												System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(n).get(allPaths.get(n).size() - 1).DepartureTime.getTimePrint(), allPaths.get(n).get(allPaths.get(n).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(n).get(allPaths.get(n).size() - 1).source.getName() + " --> " + allPaths.get(n).get(allPaths.get(n).size() - 1).destination.getName()));
												System.out.println();
											}
										}
										else{
											System.out.printf("Legs:             %d\n", allPaths.get(0).size());
											System.out.printf("Total Duration:   %s\n", allPaths.get(0).get(0).getTotalDurationPrint(allPaths.get(0)));
											System.out.printf("Total Cost:       $%.2f\n", allPaths.get(0).get(0).getCombinedTicketPrice(allPaths.get(0)));
											System.out.println("-------------------------------------------------------------");
											System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
											System.out.println("-------------------------------------------------------------");

											for(int i = 0; i < allPaths.get(0).size() - 1; i++){
												System.out.printf("%4d $ ", allPaths.get(0).get(i).getFlightID());
												System.out.printf("%7.2f", allPaths.get(0).get(i).getTicketPrice());
												System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(0).get(i).DepartureTime.getTimePrint(), allPaths.get(0).get(i).ArrivalTime.getTimePrint(), (allPaths.get(0).get(i).source.getName() + " --> " + allPaths.get(0).get(i).destination.getName()));
												System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(0).get(i), allPaths.get(0).get(i + 1))) + " at " + allPaths.get(0).get(i).destination.getName());
											}
											System.out.printf("%4d $ ", allPaths.get(0).get(allPaths.get(0).size() - 1).getFlightID());
											System.out.printf("%7.2f", allPaths.get(0).get(allPaths.get(0).size() - 1).getTicketPrice());
											System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(0).get(allPaths.get(0).size() - 1).DepartureTime.getTimePrint(), allPaths.get(0).get(allPaths.get(0).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(0).get(allPaths.get(0).size() - 1).source.getName() + " --> " + allPaths.get(0).get(allPaths.get(0).size() - 1).destination.getName()));
											System.out.println();
										}
									}catch(NumberFormatException e){
										System.out.printf("Legs:             %d\n", allPaths.get(0).size());
										System.out.printf("Total Duration:   %s\n", allPaths.get(0).get(0).getTotalDurationPrint(allPaths.get(0)));
										System.out.printf("Total Cost:       $%.2f\n", allPaths.get(0).get(0).getCombinedTicketPrice(allPaths.get(0)));
										System.out.println("-------------------------------------------------------------");
										System.out.printf("%-5s%-10s%-12s%-12s%-7s%-4s%-4s\n", "ID", "Cost", "Departure", "Arrival", "Source", "-->", "Destination");
										System.out.println("-------------------------------------------------------------");

										for(int i = 0; i < allPaths.get(0).size() - 1; i++){
											System.out.printf("%4d $ ", allPaths.get(0).get(i).getFlightID());
											System.out.printf("%7.2f", allPaths.get(0).get(i).getTicketPrice());
											System.out.printf(" %-12s%-12s%-12s\n",  allPaths.get(0).get(i).DepartureTime.getTimePrint(), allPaths.get(0).get(i).ArrivalTime.getTimePrint(), (allPaths.get(0).get(i).source.getName() + " --> " + allPaths.get(0).get(i).destination.getName()));
											System.out.println("LAYOVER " + Flight.layoverPrint(Flight.layover(allPaths.get(0).get(i), allPaths.get(0).get(i + 1))) + " at " + allPaths.get(0).get(i).destination.getName());
										}
										System.out.printf("%4d $ ", allPaths.get(0).get(allPaths.get(0).size() - 1).getFlightID());
										System.out.printf("%7.2f", allPaths.get(0).get(allPaths.get(0).size() - 1).getTicketPrice());
										System.out.printf(" %-12s%-12s%-12s\n", allPaths.get(0).get(allPaths.get(0).size() - 1).DepartureTime.getTimePrint(), allPaths.get(0).get(allPaths.get(0).size() - 1).ArrivalTime.getTimePrint(), (allPaths.get(0).get(allPaths.get(0).size() - 1).source.getName() + " --> " + allPaths.get(0).get(allPaths.get(0).size() - 1).destination.getName()));
										System.out.println();
									}
								}
							}
						}

						// System.out.printf("Legs: %d\n", allPaths.get(0).size());
						// System.out.printf("Total Duration: ", allPaths.get(0).get(0).getCombinedDuration(allPaths.get(0)));
						// System.out.p

					}

				}

			}

			// Command: Invalid
			else{
				System.out.println("Invalid command. Type 'help' for a list of commands.");
				System.out.println();
			}

		}

	}

	// Add a flight to the database
	// handle error cases and return status negative if error
	// (different status codes for different messages)
	// do not print out anything in this function


	public int addFlight(String date1, String date2, String start, String end, String capacity, int booked) {
		//check scheduling conflicts for starting location first, the ending location.
		ArrayList<String> week = new ArrayList<String>();
		week.add("Monday");
        week.add("Tuesday");
        week.add("Wednesday");
        week.add("Thursday");
        week.add("Friday");
        week.add("Saturday");
        week.add("Sunday");

		int d = 0;
        for(int i = 0; i < week.size(); i++){
            if(date1.equalsIgnoreCase(week.get(i))){
                d += 1;
            }
        }
		if(d == 0) return -1;

		String[] timeInput = date2.split(":");
		if(timeInput.length != 2) return -1;
		try{
			int x = Integer.parseInt(timeInput[0]);
			int y = Integer.parseInt(timeInput[1]);
		} catch(NumberFormatException e){
			return -1;
		}
		if(Integer.parseInt(timeInput[0]) > 23) return -1;
		if(Integer.parseInt(timeInput[1]) > 59) return -1;

		int h = Integer.parseInt(timeInput[0]);
		int m = Integer.parseInt(timeInput[1]);
		Time DepartTime = new Time(date1, h, m);

// invalid start loc
		int a = 0;
        for (HashMap.Entry<String,Location> entry : locations.entrySet()){
            if(entry.getKey().equalsIgnoreCase(start)){
                a += 1;
            }
			else{
				a += 0;
			}
        }
		if(a == 0) return -2;


// invalid end loc
		int b = 0;
        for (HashMap.Entry<String,Location> entry2 : locations.entrySet()){
            if(entry2.getKey().equalsIgnoreCase(end)){
                b += 1;
            }
			else{
				b += 0;
			}
        }
		if(b == 0) return -3;

// invalid capacity
		double c = Double.parseDouble(capacity);
		if(c < 0.0) return -4;

// invalid: start and end are same
		if(start.equalsIgnoreCase(end)) return -5;

		Location Source = null;
		Location Destination = null;
        for (HashMap.Entry<String,Location> entry : locations.entrySet()){
            if(entry.getKey().equalsIgnoreCase(start)){
                Source = this.locations.get(entry.getKey());
            }
			if(entry.getKey().equalsIgnoreCase(end)){
				Destination = this.locations.get(entry.getKey());
			}
        }

		Flight A = new Flight(date1, DepartTime, Source, Destination, c, booked);

		this.outputMessageD = Source.hasRunwayDepartureSpace(A);
		if (this.outputMessageD != null) return -6;

		this.outputMessageA = Destination.hasRunwayArrivalSpace(A);
		if (this.outputMessageA != null) return -7;

		A.setFlightID(this.counter);
		this.flights.put(this.counter, A);

		// this.sortedFlights.put(A.DepartureTime.getTimeMins(), A);
		this.sortedFlights.add(A);
		this.exportSortedFlights.put(A.getFlightID(), A);
		this.counter += 1;

		Source.addDeparture(A);
		Destination.addArrival(A);

		Source.addFlight(A);
		Destination.addFlight(A);

		// Source.sortedDepartures.put(A.DepartureTime.getTimeMins(), A);
		// Destination.sortedArrivals.put(A.ArrivalTime.getTimeMins(), A);

		return 0;

	}

	// Add a location to the database
    // do not print out anything in this function
    // return negative numbers for error cases
	public int addLocation(String name, String lat, String lon, String demand) {

        for (HashMap.Entry<String,Location> entry : locations.entrySet()){
            if(entry.getKey().equalsIgnoreCase(name)){
                return -1;
            }
        }


		// double x = Double.parseDouble(lat);
		// double y = Double.parseDouble(lon);
		// double z = Double.parseDouble(demand);

		try{
			double x = Double.parseDouble(lat);
		}catch(NumberFormatException e){
			return -2;
		}

		try{
			double y = Double.parseDouble(lon);
		}catch(NumberFormatException e){
			return -3;
		}

		try{
			double z = Double.parseDouble(demand);
		}catch(NumberFormatException e){
			return -4;
		}

		double x = Double.parseDouble(lat);
		double y = Double.parseDouble(lon);
		double z = Double.parseDouble(demand);

		if(x < -85 || x > 85) return -2;
		if(y < -180 || y > 180) return -3;
		if(z < -1 || z > 1) return -4;

		Location A = new Location(name, x, y, z);
		this.locations.put(name, A);
		this.exportLocations.put(name, A);
		return 0;

	}




	//flight import <filename>
	public void importFlights(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;

			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 5) continue;
				String[] dparts = lparts[0].split(" ");
				if (dparts.length < 2) continue;
				int booked = 0;

				try {
					booked = Integer.parseInt(lparts[4]);

				} catch (NumberFormatException e) {
					continue;
				}

				int status = addFlight(dparts[0], dparts[1], lparts[1], lparts[2], lparts[3], booked);
				if (status < 0) {
					err++;
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" flight"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}
		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}

	//location import <filename>
	public void importLocations(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;

			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 4) continue;

				int status = addLocation(lparts[0], lparts[1], lparts[2], lparts[3]);
				if (status < 0) {
					err++;
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" location"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}

		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}

}
