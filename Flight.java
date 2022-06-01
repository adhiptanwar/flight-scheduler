import java.lang.*;
import java.util.*;

public class Flight {

    public int FlightID;
    public String Day;
    public Time DepartureTime;
    public Time ArrivalTime;
    public Location source;
    public Location destination;
    public double Capacity;
    public double NumberOfPassengers;
    public double TicketPrice;


    public Flight(String Day, Time DepartureTime, Location source, Location destination, double Capacity, double NumberOfPassengers){
        this.Day = Day;
        this.DepartureTime = DepartureTime;
        this.source = source;
        this.destination = destination;
        this.Capacity = Capacity;
        this.NumberOfPassengers = NumberOfPassengers;
        // this.TicketPrice = 30.0 + ( 4.0 * (this.destination.demand - this.source.demand) );

        this.FlightID = 0;

        this.ArrivalTime = this.DepartureTime.addTime(this.getDuration());
    }

    public void setNumberOfPassengers(double num){
        this.NumberOfPassengers = num;
    }

    public int getCapacity(){
        int C = (int)(this.Capacity);
        return C;
    }

    public int getNumberOfPassengers(){
        int N = (int)(this.NumberOfPassengers);
        return N;
    }

    public String getBookedStatus(){
        int booked = (int)this.NumberOfPassengers;
        int cap = (int)this.Capacity;
        return (booked + "/" + cap);
    }

    public void setFlightID(int ID){
        this.FlightID = ID;
    }

    public int getFlightID(){
        return this.FlightID;
    }

    //get the number of minutes this flight takes (round to nearest whole number)
    public int getDuration() {
        double dist = this.getDistance();
        double avgSpeed = 12.0;

        double time = dist/avgSpeed;
        int duration = (int)Math.round(time);

        return duration;

    }

    public String getDurationPrint(){
        int duration = this.getDuration();
        int hours = duration / 60;
        int minutes = duration % 60;
        return (hours + "h " + minutes + "m");
    }

    //implement the ticket price formula
    public double getTicketPrice() {
        double originalPrice = 30.0 + ( 4.0 * (this.destination.demand - this.source.demand) );

        double x = this.NumberOfPassengers/this.Capacity;

        if(x == 0){
            return (originalPrice/100.0) * this.getDistance();
        }
        else if(x > 0 && x <= 0.5){
            double y = (-0.4 * x) + 1;
            this.TicketPrice = y * (this.getDistance()/100.0) * originalPrice;
            return this.TicketPrice;
        }
        else if(x > 0.5 && x <= 0.7){
            double y = x + 0.3;
            this.TicketPrice = y * (this.getDistance()/100.0) * originalPrice;
            return this.TicketPrice;

        }
        else if(x > 0.7 && x <= 1){
            double y = (0.2/Math.PI) * Math.atan((20 * x) - 14) + 1;
            this.TicketPrice = y * (this.getDistance()/100.0) * originalPrice;
            return this.TicketPrice;
        }
        // this is error case (see later)
        else return 0.0;
    }

    public String getTicketPricePrint(){
        double ticketP = this.getTicketPrice();

        double roundedP = Math.round(ticketP * 100.0) / 100.0;

        return ("$" + roundedP);

    }


    //book the given number of passengers onto this flight, returning the total cost
    public double book(int num) {
        // write for scenario when user books 30, but only 20 are left
        if(num > this.Capacity - this.NumberOfPassengers){
            num = (int)(this.Capacity - this.NumberOfPassengers);
        }

        double totalCost = 0.0;

        for(int i = 0; i < num; i++){
            double currentPrice = this.getTicketPrice();
            this.NumberOfPassengers += 1;
            totalCost += currentPrice;
        }

        return totalCost;

    }

    public int numberBooked(int num){
        if(num <= this.Capacity - this.NumberOfPassengers){
            return num;
        }
        else{
            num = (int)(this.Capacity - this.NumberOfPassengers);
            return num;
        }

    }

    //return whether or not this flight is full
    public boolean isFull() {
        if(this.NumberOfPassengers == this.Capacity) return true;
		else return false;
	}

    //get the distance of this flight in km
    public double getDistance() {
        // double dist = this.source.distance(this.source, this.destination);
        double dist = Location.distance(this.source, this.destination);
		return dist;

	}

    public int getIntDistance(){
        double dist = this.getDistance();
        int d = (int)Math.round(dist);
        return d;
    }

    //get the layover time, in minutes, between two flights
    public static int layover(Flight x, Flight y) {
        int layoverTime = x.ArrivalTime.getDifference(y.DepartureTime);
        return layoverTime;

    }

    public static String layoverPrint(int totalmins) {
        int hours = totalmins / 60;
        int minutes = totalmins % 60;
        return (hours + "h " + minutes + "m");

    }

    public int getCombinedDuration(ArrayList<Flight> list){
        int Duration = 0;

        for(Flight i : list){
            Duration += i.getDuration();
        }

        if(list.size() == 2){
            Duration += Flight.layover(list.get(0), list.get(1));
        }
        else if(list.size() == 3){
            Duration += Flight.layover(list.get(0), list.get(1));
            Duration += Flight.layover(list.get(1), list.get(2));
        }
        else if(list.size() == 4){
            Duration += Flight.layover(list.get(0), list.get(1));
            Duration += Flight.layover(list.get(1), list.get(2));
            Duration += Flight.layover(list.get(2), list.get(3));
        }

        return Duration;

    }

    public double getCombinedTicketPrice(ArrayList<Flight> list){
        double price = 0.0;

        for(Flight i : list){
            price += i.getTicketPrice();
        }

        return price;
    }

    public int getStopovers(ArrayList<Flight> list){
        return (list.size() - 1);
    }

    public int getLayoverTime(ArrayList<Flight> list){
        int layoverTime = 0;

        if(list.size() == 1){
            layoverTime += 0;
        }
        else if(list.size() == 2){
            layoverTime += Flight.layover(list.get(0), list.get(1));
        }
        else if(list.size() == 3){
            layoverTime += Flight.layover(list.get(0), list.get(1));
            layoverTime += Flight.layover(list.get(1), list.get(2));
        }
        else if(list.size() == 4){
            layoverTime += Flight.layover(list.get(0), list.get(1));
            layoverTime += Flight.layover(list.get(1), list.get(2));
            layoverTime += Flight.layover(list.get(2), list.get(3));
        }

        return layoverTime;
    }

    public int getCombinedFlightTime(ArrayList<Flight> list){
        int flightTime = 0;

        for(Flight i : list){
            flightTime += i.getDuration();
        }

        return flightTime;
    }

    public String getTotalDurationPrint(ArrayList<Flight> list){
        int duration = this.getCombinedDuration(list);
        int hours = duration / 60;
        int minutes = duration % 60;
        return (hours + "h " + minutes + "m");
    }

    public String getTotalCostPrint(ArrayList<Flight> list){
        double cost = this.getCombinedTicketPrice(list);

        double roundedC = Math.round(cost * 100.0) / 100.0;

        return ("$" + roundedC);
    }


}
