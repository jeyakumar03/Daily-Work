package source.model;

import java.sql.Date;
import java.time.LocalDate; 

public class Booking {
    private int userId;
    private int busId;
    private int seatCount;
    private LocalDate travelDate; 

    public Booking(int userId, int busId, int seatCount, LocalDate travelDate) {
        this.userId = userId;
        this.busId = busId;
        this.seatCount = seatCount;
        this.travelDate = travelDate;
    }


    public int getUserId() {
        return userId;
    }

    public int getBusId() {
        return busId;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }


    public Date getTravelDateAsSqlDate() {
        return Date.valueOf(travelDate); 
    }
}
