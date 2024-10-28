package source.model;

import java.sql.Time;

public class Bus {
    private int busnumber;
    private int seat;
    private int busId;
    private String travelsName;
    private boolean isAc;
    private boolean isSleeper;
    private int fromRoute;
    private int toRoute;
    private int admin_id;
    private Time departureTime;  
    public Bus(int busnumber, int seat, boolean isAc, boolean isSleeper, int fromRoute, int toRoute, Time departureTime, int admin_id) {
    this.busnumber = busnumber;
    this.seat = seat;
    this.isAc = isAc;
    this.isSleeper = isSleeper;
    this.fromRoute = fromRoute;
    this.toRoute = toRoute;
    this.departureTime = departureTime;
    this.admin_id = admin_id;
}
 
    public int getBusId() {
        return busId;
    }
    public int getBusnum() {
        return busnumber;
    }

    public int getSeat() {
        return seat;
    }

    public boolean getAc() {
        return isAc;
    }

    public boolean getSleeper() {
        return isSleeper;
    }

    public int getFromRoute() {
        return fromRoute;
    }

    public int getToRoute() {
        return toRoute;
    }

    public int getAdmin() {
        return admin_id;
    }

    public Time getDepartureTime() {
        return departureTime;  
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }
    public String getTravelsName() {
        return travelsName;
    }

    
}

