import java.lang.*;
import java.util.*;

public class Location {

	public String name;
	public double lat;
	public double lon;
	public double demand;
	public ArrayList<Flight> departures = new ArrayList<Flight>();
	// public TreeMap<Integer, Flight> sortedDepartures = new TreeMap<Integer, Flight>();

	public ArrayList<Flight> arrivals = new ArrayList<Flight>();
	// public TreeMap<Integer, Flight> sortedArrivals = new TreeMap<Integer, Flight>();
	public TreeMap<Integer, Flight> allFlights = new TreeMap<Integer, Flight>();


	public Location(String name, double lat, double lon, double demand) {
	// write error cases
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.demand = demand;
	}

	public void addFlight(Flight f){
		for(Flight i : this.departures){
			if(i.getFlightID() == f.getFlightID()){
				this.allFlights.put(f.DepartureTime.getTimeMins(), f);
			}
		}
		for(Flight i : this.arrivals){
			if(i.getFlightID() == f.getFlightID()){
				this.allFlights.put(f.ArrivalTime.getTimeMins(), f);
			}
		}

	}

	public void removeFlight(Flight f){
		for(Flight i : this.departures){
			if(i.getFlightID() == f.getFlightID()){
				this.allFlights.remove(f.DepartureTime.getTimeMins());
			}
		}
		for(Flight i : this.arrivals){
			if(i.getFlightID() == f.getFlightID()){
				this.allFlights.remove(f.ArrivalTime.getTimeMins());
			}
		}
	}


// my own for convenience
	public String getName(){
		return this.name;
	}

	public double getLat(){
		return this.lat;
	}

	public double getLon(){
		return this.lon;
	}

	public double getDemand(){
		return this.demand;
	}

	// public String getDepartureArrivalString(Flight f){
	// 	String result = "";

	// 	for(Flight i : this.departures){
	// 		if(i.getFlightID() == f.getFlightID()){
	// 			result = f.DepartureTime.getTimePrint() + "   Departure to " + f.destination.getName();
	// 		}
	// 	}

	// 	for(Flight i : this.arrivals){
	// 		if(i.getFlightID() == f.getFlightID()){
	// 			result = f.ArrivalTime.getTimePrint() + "   Arrival from " + f.source.getName();
	// 		}
	// 	}

	// 	return result;
	// }

    //Implement the Haversine formula - return value in kilometres
    public static double distance(Location l1, Location l2) {
			double radius = 6371.0;
			double dlat = l2.lat - l1.lat;
			dlat = Math.toRadians(dlat);

			double dlon = l2.lon - l1.lon;
			dlon = Math.toRadians(dlon);

			double x = Math.pow(Math.sin(dlat/2.0), 2) + ( Math.cos(Math.toRadians(l1.lat)) * Math.cos(Math.toRadians(l2.lat)) * Math.pow(Math.sin(dlon/2.0),2) );

			double dist = 2 * radius * Math.asin(Math.sqrt(x));

			return dist;



    }

    public void addArrival(Flight f) {
		this.arrivals.add(f);
	}

	public void addDeparture(Flight f) {
		this.departures.add(f);
	}

	/**
	 * Check to see if Flight f can depart from this location.
	 * If there is a clash, the clashing flight string is returned, otherwise null is returned.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
	 * @param f The flight to check.
	 * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
	 */
	public String hasRunwayDepartureSpace(Flight f) {
		//check departures first

		for(Flight i : this.departures){
			if(i.DepartureTime.isBefore(f.DepartureTime) == false){
				if(f.DepartureTime.getDifference(i.DepartureTime) < 60 || i.DepartureTime.getDifference(f.DepartureTime) < 60 ){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " departing from " + i.source.getName() + " on "+ i.DepartureTime.getTime() + ".");
				}
			}
		}

		for(Flight i : this.arrivals){
			if(i.ArrivalTime.isBefore(f.DepartureTime) == false){
				if(f.DepartureTime.getDifference(i.ArrivalTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " arriving at " + i.destination.getName() + " on "+ i.ArrivalTime.getTime()+ ".");
				}
			}
		}

		for(Flight i : this.departures){
			if(i.DepartureTime.isBefore(f.DepartureTime)){
				if(i.DepartureTime.getDifference(f.DepartureTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " departing from " + i.source.getName() + " on "+ i.DepartureTime.getTime()+ ".");
				}
			}
		}

		//then check arrivals



		for(Flight i : this.arrivals){
			if(i.ArrivalTime.isBefore(f.DepartureTime)){
				if(i.ArrivalTime.getDifference(f.DepartureTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " arriving at " + i.destination.getName() + " on "+ i.ArrivalTime.getTime()+ ".");
				}
			}
		}

		return null;
    }

    /**
	 * Check to see if Flight f can arrive at this location.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
	 * @param f The flight to check.
	 * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
	 */
	public String hasRunwayArrivalSpace(Flight f) {
		//check departures first

		for(Flight i : this.departures){
			if(i.DepartureTime.isBefore(f.ArrivalTime) == false){
				if(f.ArrivalTime.getDifference(i.DepartureTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " departing from " + i.source.getName() + " on "+ i.DepartureTime.getTime()+ ".");
				}
			}
		}

		for(Flight i : this.arrivals){
			if(i.ArrivalTime.isBefore(f.ArrivalTime) == false){
				if(f.ArrivalTime.getDifference(i.ArrivalTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " arriving at " + i.destination.getName() + " on "+ i.ArrivalTime.getTime()+ ".");
				}
			}
		}

		for(Flight i : this.departures){
			if(i.DepartureTime.isBefore(f.ArrivalTime)){
				if(i.DepartureTime.getDifference(f.ArrivalTime) < 60){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " departing from " + i.source.getName() + " on "+ i.DepartureTime.getTime()+ ".");
				}
			}
		}

		//then check arrivals



		for(Flight i : this.arrivals){
			if(i.ArrivalTime.isBefore(f.ArrivalTime)){
				if(i.ArrivalTime.getDifference(f.ArrivalTime) < 60 ){
					return ("Scheduling conflict! This flight clashes with Flight " + i.getFlightID() + " arriving at " + i.destination.getName() + " on "+ i.ArrivalTime.getTime()+ ".");
				}
			}
		}

		return null;
    }



// created by Adhip for testing purpose

	// public static void main(String[] args){
	// 	Location A = new Location("Noida",52.5,13.15,0.2222);
	// 	Location B = new Location("SFO",40.7,-74.26,0);

	// 	System.out.println(distance(A, B));
	// }
}
