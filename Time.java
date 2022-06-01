import java.util.*;

public class Time{
    public String day;
    public int dayNumber;
    public int hours;
    public int minutes;
    public ArrayList<String> week = new ArrayList<String>();
    public String printDay;

    public Time(String day, int hours, int minutes){
        this.day = day;
        this.printDay = day;
        this.hours = hours;
        this.minutes = minutes;

        this.dayNumber = 0;

        week.add("Monday");
        week.add("Tuesday");
        week.add("Wednesday");
        week.add("Thursday");
        week.add("Friday");
        week.add("Saturday");
        week.add("Sunday");

        for(int i = 0; i < week.size(); i++){
            if(this.day.equalsIgnoreCase(week.get(i))){
                this.dayNumber = i;
                this.printDay = week.get(i).substring(0,3);
                this.day = week.get(i);
            }
        }

    }

    public Time addTime(int minutes){
        int currentTime = (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
        int newTime = currentTime + minutes;
        int newDayNumber = (int)Math.floor((newTime/(60*24))%7);
        String newDay = "";


        int currentTime2 = (this.hours * 60) + this.minutes;
        int newTime2 = currentTime2 + (minutes%(60*24));
        int newHours = (int)Math.floor((newTime2/60)%24);
        int newMinutes = newTime2 % 60;

        for(int i = 0; i < week.size(); i++){
            if(i == newDayNumber){
                newDay = week.get(i);
            }
        }

        Time a = new Time(newDay, newHours, newMinutes);
        return a;
    }

    public boolean isBefore(Time t1){
        int currentTime = (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
        int nextTime = (t1.dayNumber * 60 * 24) + (t1.hours * 60) + t1.minutes;

        if(currentTime <= nextTime) return true;
        else return false;

    }

    public boolean isAfter(Time t1){
        int currentTime = (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
        int nextTime = (t1.dayNumber * 60 * 24) + (t1.hours * 60) + t1.minutes;

        if(currentTime > nextTime) return true;
        else return false;

    }

    // public int getDifference(Time t1){
    //     int currentTime = (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
    //     int nextTime = (t1.dayNumber * 60 * 24) + (t1.hours * 60) + t1.minutes;

    //     return Math.abs(currentTime - nextTime);
    // }

    public int getDifference(Time t1){
        if(this.isBefore(t1)){
            int currentTime = (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
            int nextTime = (t1.dayNumber * 60 * 24) + (t1.hours * 60) + t1.minutes;

            return (nextTime - currentTime);
        }
        else{
            Time lastTime = new Time("Sunday", 23, 59);
            Time firstTime = new Time("Monday", 0, 0);

            int a = this.getDifference(lastTime);
            int b = firstTime.getDifference(t1);

            return (a + b) + 1;
        }
    }

    public String getTime(){
        if(this.hours < 10 && this.minutes < 10){
            return(this.day + " 0" + this.hours + ":0" + this.minutes);
        }
        else if (this.hours < 10 && this.minutes >= 10){
            return (this.day + " 0" + this.hours + ":" + this.minutes);
        }
        else if (this.hours >= 10 && this.minutes < 10){
            return (this.day + " " + this.hours + ":0" + this.minutes);
        }
        else{
            return (this.day + " " + this.hours + ":" + this.minutes);
        }
    }

    public String getTimePrint(){
        if(this.hours < 10 && this.minutes < 10){
            return(this.printDay + " 0" + this.hours + ":0" + this.minutes);
        }
        else if (this.hours < 10 && this.minutes >= 10){
            return (this.printDay + " 0" + this.hours + ":" + this.minutes);
        }
        else if (this.hours >= 10 && this.minutes < 10){
            return (this.printDay + " " + this.hours + ":0" + this.minutes);
        }
        else{
            return (this.printDay + " " + this.hours + ":" + this.minutes);
        }
    }

    public int getTimeMins(){
        return (this.dayNumber * 60 * 24) + (this.hours * 60) + this.minutes;
    }

    public static void main(String[] args){
        Time a = new Time("Tuesday",04,42);
        Time b = new Time("Tuesday",04,40);
        System.out.println(a.getDifference(b));
    }
}
