package source.model;
import java.sql.Date;

public class Ticket {
    private int bookingId;
    private int userId;
    private String name;
    private String mobile;
    private String email;
    private Date travelDate;
    private Date bookedDate;
    private int noOfTickets;

    public Ticket(int bookingId, int userId, String name, String mobile, String email, Date travelDate, Date bookedDate, int noOfTickets) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.travelDate = travelDate;
        this.bookedDate = bookedDate;
        this.noOfTickets = noOfTickets;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    // Setters
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public void setNoOfTickets(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", travelDate=" + travelDate +
                ", bookedDate=" + bookedDate +
                ", noOfTickets=" + noOfTickets +
                '}';
    }
}

