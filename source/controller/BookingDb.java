package source.controller;
import source.view.*;
import source.model.Booking;
import source.model.Passenger;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
public class BookingDb {
    private static Scanner scanner = new Scanner(System.in);
    private List<String> displayRoute(String query) throws SQLException {
        List<String> routes = new ArrayList<>();
        StringBuilder formattedRoutes = new StringBuilder();
        try (PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            formattedRoutes.append("+-----+---------------------+\n");
            formattedRoutes.append("| ID  | Route               |\n");
            formattedRoutes.append("+-----+---------------------+\n");
            while (rs.next()) {
                formattedRoutes.append(String.format("| %-3d | %-19s |\n", rs.getInt(1), rs.getString(2)));
            }
            formattedRoutes.append("+-----+---------------------+\n");
        }
        routes.add(formattedRoutes.toString());
        return routes;
    }

    public List<String> displayFromRoute() throws SQLException {
        return displayRoute("select * from route");
    }

    public List<String> displayToRoute() throws SQLException {
        return displayRoute("select * from route_toPlace");
    }
    public static List<String> getBusdetails(int from_id, int to_id) throws SQLException {
        List<String> busDetails = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        try 
        (PreparedStatement pst = con.prepareStatement("SELECT bus.id, bus.bus_number, bus.seat_capacity, bus.isac, bus.issleeper, admin.travels_name, route.from_place, route_toPlace.to_place,departure_time  FROM bus JOIN route ON bus.from_place = route.id JOIN route_toPlace ON bus.to_place = route_toPlace.id JOIN admin ON bus.admin_id = admin.id WHERE bus.from_place = ? AND bus.to_place = ?")){
            pst.setInt(1, from_id);
            pst.setInt(2, to_id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Time departureTime = rs.getTime("departure_time");
                    String busDetail = String.format("%d|%s|%d|%s|%s|%s|%s|%s|%s",
                            rs.getInt("id"),
                            rs.getString("bus_number"),
                            rs.getInt("seat_capacity"),
                            rs.getBoolean("isac") ? "AC" : "Non-AC",
                            rs.getBoolean("issleeper") ? "Sleeper" : "Semi-Sleeper",
                            rs.getString("travels_name"),
                            rs.getString("from_place"),
                            rs.getString("to_place"),
                            departureTime != null ? departureTime.toString() : "N/A");
                    busDetails.add(busDetail);
                }
            }
        }
        return busDetails;
    }

    public int seatCapacity(int id) {
        try (PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement("Select seat_capacity from bus where id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("seat_capacity");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching seat capacity.");
        }
        return 0;
    }

    public int available_seat(int bus_id, Date travel_date) {
        try (PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement("select sum(tickets_booked) FROM booking where bus_id=? and travel_date=? group by travel_date")) {
            pst.setInt(1, bus_id);
            pst.setDate(2, travel_date);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching available seats.");
        }
        return 0;
    }

    public boolean[] getSeatAvailability(int busId, java.sql.Date travelDate) throws SQLException {
        boolean[] seatAvailability = new boolean[40];
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("SELECT seat_number FROM seat_booking WHERE bus_id = ? AND travel_date = ?")) {
            stmt.setInt(1, busId);
            stmt.setDate(2, travelDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookedSeatNumber = rs.getInt("seat_number");
                seatAvailability[bookedSeatNumber - 1] = true;
            }
        }

        return seatAvailability;
    }

    public HashSet<Integer> getBookedSeats(int busId, Date travelDate) throws SQLException {
        HashSet<Integer> bookedSeats = new HashSet<>();
        try (PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement("SELECT seat_number FROM seat_booking WHERE bus_id = ? AND travel_date = ?")){
            pst.setInt(1, busId);
            pst.setDate(2, travelDate);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getInt("seat_number"));
                }
            }
        }
        return bookedSeats;
    }
    public boolean bookTicket(Booking booking, List<Passenger> passengers, List<Integer> selectedSeats) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        String bookingQuery = "INSERT INTO booking(user_id, bus_id, tickets_booked, booked_date, travel_date) VALUES (?, ?, ?, ?, ?)";
        String passengerQuery = "INSERT INTO passenger (booking_id, name, gender, mobile, email) VALUES (?, ?, ?, ?, ?)";
        String seatBookingQuery = "INSERT INTO seat_booking (booking_id, user_id, bus_id, seat_number, travel_date, booking_date) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);
            PreparedStatement bookingStmt = con.prepareStatement(bookingQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement passengerStmt = con.prepareStatement(passengerQuery);
            PreparedStatement seatBookingStmt = con.prepareStatement(seatBookingQuery);
            bookingStmt.setInt(1, booking.getUserId());
            bookingStmt.setInt(2, booking.getBusId());
            bookingStmt.setInt(3, booking.getSeatCount());
            bookingStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            bookingStmt.setDate(5, booking.getTravelDateAsSqlDate());
            bookingStmt.executeUpdate();
            ResultSet generatedKeys = bookingStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int bookingId = generatedKeys.getInt(1);
                for (Passenger passenger : passengers) {
                    passengerStmt.setInt(1, bookingId);
                    passengerStmt.setString(2, passenger.getName());
                    passengerStmt.setString(3, passenger.getGender());
                    passengerStmt.setString(4, passenger.getMobile());
                    passengerStmt.setString(5, passenger.getEmail());
                    passengerStmt.executeUpdate();
                }
                for (int seatNumber : selectedSeats) {
                    seatBookingStmt.setInt(1, bookingId);
                    seatBookingStmt.setInt(2, booking.getUserId());
                    seatBookingStmt.setInt(3, booking.getBusId());
                    seatBookingStmt.setInt(4, seatNumber);
                    seatBookingStmt.setDate(5, booking.getTravelDateAsSqlDate());
                    seatBookingStmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
                    seatBookingStmt.executeUpdate();
                }

                con.commit();
                return true;
            } else {
                throw new SQLException("Failed to retrieve booking ID.");
            }
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            System.err.println("Error occurred during booking: " + e.getMessage());
            return false;
        }
    }

    public String getBusDetailsById(int busId) throws SQLException {
        String query = "SELECT bus_number, admin.travels_name AS travels, isac, issleeper, " +
                       "route.from_place AS starting, route_toPlace.to_place AS destination, " +
                       "departure_time " +
                       "FROM bus " +
                       "JOIN admin ON bus.admin_id = admin.id " +
                       "JOIN route ON route.id = bus.from_place " +
                       "JOIN route_toPlace ON bus.to_place = route_toPlace.id " +
                       "WHERE bus.id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, busId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("bus_number") + "|" +
                       rs.getString("travels") + "|" +
                       rs.getString("issleeper") + "|" +
                       rs.getString("isac") + "|" +
                       rs.getString("starting") + "|" +
                       rs.getString("destination") + "|" +
                       rs.getString("departure_time");
            }
        }
        return null;
    }
}

