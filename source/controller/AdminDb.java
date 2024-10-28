package source.controller;

import source.view.*;
import source.model.Admin;
import source.model.Bus;
import source.model.Ticket;
import source.model.Route;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
public class AdminDb {
    private Connection con;
    public AdminDb() throws SQLException {
        this.con = DatabaseConnection.getConnection();
    }
    public int addAdmin(Admin admin) throws SQLException {
        int rowsAffected = 0;
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.INSERT_ADMIN.getQuery())) {
            pst.setString(1, admin.getName());
            pst.setLong(2, admin.getMobile());
            pst.setString(3, admin.getMail());
            pst.setString(4, admin.getTravels());
            rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                int userId = getId(admin.getMail());
                insertAdminCredentials(userId, admin.getUsername(), admin.getPassword());
            }
        }
        return rowsAffected;
    }
    private void insertAdminCredentials(int userId, String username, String password) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.INSERT_ADMIN_CRED.getQuery())) {
            pst.setInt(1, userId);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.executeUpdate();
        }
    }
    public static int adminLogin(String username, String password) {
        int userId = -1;
        try{
            PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement(QueriesContainer.Queries.ADMIN_LOGIN_QUERY.getQuery());
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                userId = rs.getInt("id");
                return userId;
            } 
        } catch (SQLException e) {
        	   e.printStackTrace();
        }
        return userId;
    }
    public int getId(String email) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.SELECT_ADMIN_ID.getQuery())) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("No user found with email: " + email);
    }
    public int deleteAdmin(String mail) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.ADMIN_DELETE.getQuery())) {
            pst.setString(1, mail);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected;
        }
    }
    public List<Admin> displayAdmin() throws SQLException {
    List<Admin> admins = new ArrayList<>();
    try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.DISPLAY_ADMIN.getQuery());
         ResultSet rs = pst.executeQuery()) {
         while (rs.next()) {
            Admin admin = new Admin(
                rs.getInt(1),
                rs.getString(2),
                rs.getLong(3),
                rs.getString(4),
                rs.getString(5)
            );
            admins.add(admin);
        }
    }
    return admins;
   }
   public List<Route> displayFromRoute() throws SQLException {
    List<Route> routes = new ArrayList<>();
    try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.DISPLAY_ROUTE.getQuery());
        ResultSet rs = pst.executeQuery()) {
        while (rs.next()) {
            Route route = new Route(
                rs.getInt(1),
                rs.getString(2)
            );
            routes.add(route);
        }
    }
    return routes;
  }
    public List<Route> displayToRoute() throws SQLException {
       List<Route> routes = new ArrayList<>();

       try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.DISPLAY_ROUTE_TO.getQuery());
         ResultSet rs = pst.executeQuery()) {

         while (rs.next()) {
            Route route = new Route(
                rs.getInt(1),
                rs.getString(2)
            );
            routes.add(route);
        }
    }
    return routes;
  }
    public void addBus(Bus bus) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.INSERT_BUS.getQuery())) {
            pst.setInt(1, bus.getBusnum());
            pst.setInt(2, bus.getSeat());
            pst.setBoolean(3, bus.getAc());
            pst.setBoolean(4, bus.getSleeper());
            pst.setInt(5, bus.getAdmin());
            pst.setInt(6, bus.getFromRoute());
            pst.setInt(7, bus.getToRoute());
            pst.setTime(8, bus.getDepartureTime());
            pst.executeUpdate();
        }
    }
    public List<String> displayBus(int userId) throws SQLException {
        List<String> buses = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(QueriesContainer.Queries.DISPLAY_BUS.getQuery())) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    StringBuilder busDetails = new StringBuilder();
                    busDetails.append("+---------------------+-----------------------------+\n");
                    busDetails.append("| Bus Detail           | Value                       |\n");
                    busDetails.append("+---------------------+-----------------------------+\n");
                    busDetails.append(String.format("| %-19s | %-27d |\n", "Bus ID", rs.getInt("id")));
                    busDetails.append(String.format("| %-19s | %-27d |\n", "Bus Number", rs.getInt("Bus_Number")));
                    busDetails.append(String.format("| %-19s | %-27s |\n", "Travels", rs.getString("Travels_Name")));
                    busDetails.append(String.format("| %-19s | %-27d |\n", "Seat Capacity", rs.getInt("Seat")));
                    String acType = rs.getBoolean("AC") ? "AC" : "Non-AC";
                    busDetails.append(String.format("| %-19s | %-27s |\n", "AC Type", acType));
                    String sleeperType = rs.getBoolean("Sleeper") ? "Sleeper" : "Semi-Sleeper";
                    busDetails.append(String.format("| %-19s | %-27s |\n", "Sleeper", sleeperType));
                    busDetails.append(String.format("| %-19s | %-27s |\n", "Starting Place", rs.getString("Starting_Place")));
                    busDetails.append(String.format("| %-19s | %-27s |\n", "Destination", rs.getString("Destination")));
                    busDetails.append("+---------------------+-----------------------------+\n");
                    buses.add(busDetails.toString());
                }
            }
        }
        return buses;
    }
    public int updateAdmin(String column, String newValue, int adminId) throws SQLException {
        String query = "UPDATE admin SET " + column + " = ? WHERE id = ?";
        int rowUpdate=0;
        try (PreparedStatement pst = con.prepareStatement(query)) {
            if ("mobile".equals(column)) {
                pst.setLong(1, Long.parseLong(newValue));
            } else {
                pst.setString(1, newValue);
            }
            pst.setInt(2, adminId);
            pst.executeUpdate();
            rowUpdate++;
        }
        return rowUpdate;
    }
    public int updateBus(String column, Object newValue, int busId) throws SQLException {
        String query = "UPDATE bus SET " + column + " = ? WHERE id = ?";
        int rowUpdated=0;
        try (PreparedStatement pst = con.prepareStatement(query)) {
            if (newValue instanceof String) {
                pst.setString(1, (String) newValue);
            } else if (newValue instanceof Integer) {
                pst.setInt(1, (Integer) newValue);
            } else if (newValue instanceof Boolean) {
                pst.setBoolean(1, (Boolean) newValue);
            }
            pst.setInt(2, busId);
            int rowsUpdated = pst.executeUpdate();
            return rowUpdated;
        }
    }
    public List<Ticket> viewTicket(int busId) throws SQLException {
         List<Ticket> tickets = new ArrayList<>();
    
         try (PreparedStatement pst = DatabaseConnection.getConnection().prepareStatement(QueriesContainer.Queries.VIEW_TICKET.getQuery())) {
              pst.setInt(1, busId);
              ResultSet rs = pst.executeQuery();
        
              while (rs.next()) {
                 int bookingId = rs.getInt("id");
                 String name = rs.getString("Name");
                 String mobile = rs.getString("mobile");
                 String email = rs.getString("Mail");
                 Date travelDate = rs.getDate("travel_date");
                 Date bookedDate = rs.getDate("booked_date");
                 int noOfTickets = rs.getInt("No_Of_Tickets");

                 Ticket ticket = new Ticket(bookingId, rs.getInt("user_id"), name, mobile, email, travelDate, bookedDate, noOfTickets);
                 tickets.add(ticket);
              }
       } 
    return tickets;
   }
}
