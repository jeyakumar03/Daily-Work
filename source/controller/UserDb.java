package source.controller;

import source.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class UserDb {
    public boolean insertUser(User user) {
        try {
            PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement("INSERT INTO users(name, mobile, mail) VALUES (?, ?, ?)");
            pst.setString(1, user.getName());
            pst.setLong(2, user.getMobile());
            pst.setString(3, user.getEmail());
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                int userId = getUserIdByEmail(user.getEmail());
                return insertUserCredentials(userId, user.getUsername(), user.getPassword());
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertUserCredentials(int userId, String username, String password) {
        try {
            PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement("INSERT INTO usercred(id, username, password) VALUES (?, ?, ?)");
            pst.setInt(1, userId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

     public static int login(String username, String password) {
        int userId = -1;
        try {
            PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement(QueriesContainer.Queries.LOGIN_QUERY.getQuery());
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
                return userId;
            } 
        } catch (SQLException e) {
            System.out.println("User Not Found");
        }
        return userId;
    }
    public boolean updateUser(String column, String newValue, int userId) {
        String query = "UPDATE users SET " + column + " = ? WHERE id = ?";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            if ("mobile".equals(column)) {
                pst.setLong(1, Long.parseLong(newValue));
            } else {
                pst.setString(1, newValue);
            }
            pst.setInt(2, userId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateUserCredentials(String colname, String newValue, int userId) {
        String query = "UPDATE usercred SET " + colname + " = ? WHERE id = ?";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, newValue);
            pst.setInt(2, userId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private int getUserIdByEmail(String email) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT id FROM users WHERE mail = ?");
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    public String viewTicket(int userId) {
        StringBuilder result = new StringBuilder();
        try {
            PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement(
                    "SELECT b.id AS booking_id, b.tickets_booked AS Tickets_Booked, b.booked_date AS Booked_Date, " +
                            "b.travel_date AS Travel_Date, bus.bus_number AS Bus_Number, bus.isac AS Type, " +
                            "bus.issleeper AS Sleeper, admin.travels_name AS Travels_Name, bus.departure_time AS Departure_Time " +
                            "FROM booking b " +
                            "JOIN bus ON b.bus_id = bus.id " +
                            "JOIN admin ON bus.admin_id = admin.id " +
                            "WHERE b.user_id = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            boolean bookingsFound = false;
            while (rs.next()) {
                bookingsFound = true;
                result.append("-------------------------------------------------\n");
                result.append("Booking ID        : ").append(rs.getInt("booking_id")).append("\n");
                result.append("Tickets Booked    : ").append(rs.getInt("Tickets_Booked")).append("\n");
                result.append("Booked Date       : ").append(rs.getDate("Booked_Date")).append("\n");
                result.append("Travel Date       : ").append(rs.getDate("Travel_Date")).append("\n");
                result.append("Bus Number        : ").append(rs.getString("Bus_Number")).append("\n");
                boolean isAc = rs.getBoolean("Type");
                result.append("Bus Type (AC)     : ").append(isAc ? "AC" : "Non-AC").append("\n");
                boolean isSleeper = rs.getBoolean("Sleeper");
                result.append("Sleeper Type      : ").append(isSleeper ? "Sleeper" : "Semi-Sleeper").append("\n");
                result.append("Travels Name      : ").append(rs.getString("Travels_Name")).append("\n");
                Time departureTime = rs.getTime("Departure_Time");
                result.append("Departure Time    : ").append(departureTime != null ? departureTime.toString() : "N/A").append("\n");
                result.append("-------------------------------------------------\n");
            }
            if (!bookingsFound) {
                return "No bookings found for the user.";
            }
        } catch (SQLException e) {
            return "Error occurred while retrieving bookings: " + e.getMessage();
        }
        return result.toString();
    }

    public boolean cancelTicket(int bookingId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            String selectSQL = "SELECT travel_date, bus.departure_time FROM booking " +
                               "JOIN bus ON booking.bus_id = bus.id WHERE booking.id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                java.sql.Date travelDate = resultSet.getDate("travel_date");
                Time departureTime = resultSet.getTime("departure_time");
                if (travelDate != null && departureTime != null) {
                    LocalDate localTravelDate = travelDate.toLocalDate();
                    java.time.LocalTime localDepartureTime = departureTime.toLocalTime();
                    java.time.LocalDateTime departureDateTime = java.time.LocalDateTime.of(localTravelDate, localDepartureTime);
                    java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();
                    java.time.Duration duration = java.time.Duration.between(currentDateTime, departureDateTime);
                    if (duration.toHours() >= 4) {
                        con.setAutoCommit(false);
                        String deletePassengersSQL = "DELETE FROM passenger WHERE booking_id = ?";
                        PreparedStatement deletePassengersStmt = con.prepareStatement(deletePassengersSQL);
                        deletePassengersStmt.setInt(1, bookingId);
                        deletePassengersStmt.executeUpdate();
                        String deleteBookingSQL = "DELETE FROM booking WHERE id = ?";
                        PreparedStatement deleteBookingStmt = con.prepareStatement(deleteBookingSQL);
                        deleteBookingStmt.setInt(1, bookingId);
                        
                        
                        int rowsAffected = deleteBookingStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            con.commit();
                            return true;
                        } else {
                            con.rollback();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
}
