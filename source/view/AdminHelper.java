package source.view;
import java.util.Scanner;
import java.util.List;
import java.sql.Time;
import java.sql.SQLException;
import source.model.Ticket;
import source.model.Bus;
import source.model.Route;
import source.controller.AdminDb;
import source.view.AdminInfo;
public class AdminHelper {
    private Scanner ob = new Scanner(System.in);
    private AdminDb adminDb;
    public AdminHelper(AdminDb adminDb) {
        this.adminDb = adminDb;
    }
    public void signin() throws SQLException {
        System.out.print("Enter the username: ");
        String username = ob.nextLine();
        System.out.print("Enter the password: ");
        String password = ob.nextLine(); 
        int userId = adminDb.adminLogin(username, password);
        if (userId != -1) {
            System.out.println("Admin Logged In Succesfully");
            new AdminInfo(adminDb).show(userId);
        } else {
            System.out.println("Invalid credentials. Please try again.");
            signin(); 
        }
    }
    public void addBus(int userId) {
        try {
            System.out.print("Enter the bus number: ");
            int number = ob.nextInt();
            System.out.print("Enter the seat capacity: ");
            int seat = ob.nextInt();
            System.out.print("Enter the bus type AC(true) or NON-AC(false): ");
            boolean isAc = ob.nextBoolean();
            System.out.print("Enter the bus type Sleeper(true) or SemiSleeper(false): ");
            boolean isSleeper = ob.nextBoolean();
            
            System.out.println("Select The Starting point of your Bus:");
	 List<Route> routeDetails = adminDb.displayFromRoute();
    	 printRouteTable(routeDetails);
            int fromRoute = ob.nextInt();
            
            System.out.println("Select The Destination point of your Bus:");
            List<Route> routeDetail = adminDb.displayToRoute();
            printRouteToTable(routeDetail);
            int toRoute = ob.nextInt();
            
            System.out.print("Enter the departure time (HH:mm): ");
            String timeString = ob.next(); 
            Time departureTime = Time.valueOf(timeString + ":00"); 
            
            Bus bus = new Bus(number, seat, isAc, isSleeper, fromRoute, toRoute, departureTime, userId);
            adminDb.addBus(bus);
            System.out.println("Bus added successfully.");
        } catch (Exception e) {
            System.out.println("Error occurred while adding bus: " + e.getMessage());
        }
    }

    
      public void editAdmin(int userId) throws SQLException {
    System.out.println("Enter the details you want to edit:\n1. Username\n2. Travels Name\n3. Mobile\n4. Mail");
    int choice = ob.nextInt();
    ob.nextLine(); 

    int rowsUpdated;

    switch (choice) {
        case 1:
            System.out.print("Enter new Username: ");
            String newUsername = ob.nextLine();
            rowsUpdated = adminDb.updateAdmin("username", newUsername, userId);
            break;

        case 2:
            System.out.print("Enter new travels name: ");
            String newTravelsName = ob.nextLine();
            rowsUpdated = adminDb.updateAdmin("travels_name", newTravelsName, userId);
            break;

        case 3:
            System.out.print("Enter new mobile number: ");
            long newMobile = ob.nextLong();
            ob.nextLine(); 
            rowsUpdated = adminDb.updateAdmin("mobile", String.valueOf(newMobile), userId);
            break;

        case 4:
            System.out.print("Enter new mail: ");
            String newMail = ob.nextLine();
            rowsUpdated = adminDb.updateAdmin("mail", newMail, userId);
            break;

        default:
            System.out.println("Invalid choice. Please select a valid option.");
            return; 
    }


    if (rowsUpdated > 0) {
        System.out.println("Admin details updated successfully.");
    } else {
        System.out.println("No admin found with the provided ID.");
    }
}


    public void editBus(int userId) throws SQLException {
        System.out.println("Select The bus id:");
        List<String> busDetails = adminDb.displayBus(userId);
        busDetails.forEach(System.out::println);
        
        int busId = ob.nextInt(); 
        ob.nextLine(); 
        System.out.println("Select the detail you want to edit:");
        System.out.println("1. Bus Number");
        System.out.println("2. Seat Capacity");
        System.out.println("3. AC Type");
        System.out.println("4. Sleeper Type");
        System.out.println("5. Starting Place");
        System.out.println("6. Destination Place");
        System.out.print("Enter your choice: ");
        
        int choice = ob.nextInt();
        ob.nextLine();
        int rowsUpdated=0;
        
        switch (choice) {
            case 1:
                System.out.print("Enter the new bus number: ");
                int newBusNumber = ob.nextInt();
                rowsUpdated=adminDb.updateBus("bus_number", newBusNumber, busId);
                break;
            case 2:
                System.out.print("Enter the new seat capacity: ");
                int newSeatCapacity = ob.nextInt();
                rowsUpdated=adminDb.updateBus("seat_capacity", newSeatCapacity, busId);
                break;
            case 3:
                System.out.print("Is the bus AC? (true/false): ");
                boolean isAc = ob.nextBoolean();
                rowsUpdated=adminDb.updateBus("isac", isAc, busId); 
                break;
            case 4:
                System.out.print("Is the bus a sleeper? (true/false): ");
                boolean isSleeper = ob.nextBoolean();
                rowsUpdated=adminDb.updateBus("issleeper", isSleeper, busId);
                break;
            case 5:
                System.out.println("Select The Starting point of your Bus:");
                System.out.println(adminDb.displayFromRoute());
                int fromRoute = ob.nextInt();
                rowsUpdated=adminDb.updateBus("from_place", fromRoute, busId); 
                break;
            case 6:
                System.out.println("Select The Destination point of your Bus:");
                System.out.println(adminDb.displayToRoute());
                int toRoute = ob.nextInt();
                rowsUpdated=adminDb.updateBus("to_place", toRoute, busId); 
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
        if (rowsUpdated!= 0) {
           System.out.println("Bus details updated successfully.");
        } else {
        System.out.println("No Bus found with the provided ID.");
    }
    }

    public void viewTickets(int userId) throws SQLException {
             List<String> busDetails = adminDb.displayBus(userId);
             busDetails.forEach(System.out::println);
    
             System.out.print("Select The bus id: "); 
             int busId = ob.nextInt(); 
             ob.nextLine();
    
              List<Ticket> tickets = adminDb.viewTicket(busId);
              printTicketsTable(tickets);
    }

    private void printTicketsTable(List<Ticket> tickets) {
              if (tickets.isEmpty()) {
                   System.out.println("No tickets found for the bus with ID.");
                   return;
               }
               System.out.printf("%-20s %-30s%n", "| Bus Detail", "| Value");
               System.out.println("+---------------------+-----------------------------+");
    

               for(Ticket ticket : tickets) {
                   System.out.printf("| %-20s | %-25d |%n", "Booking ID", ticket.getBookingId());
                   System.out.printf("| %-20s | %-25d |%n", "User ID", ticket.getUserId());
                   System.out.printf("| %-20s | %-25s |%n", "Name", ticket.getName());
                   System.out.printf("| %-20s | %-25s |%n", "Mobile", ticket.getMobile());
                   System.out.printf("| %-20s | %-25s |%n", "Email", ticket.getEmail());
                   System.out.printf("| %-20s | %-25s |%n", "Travel Date", ticket.getTravelDate());
                   System.out.printf("| %-20s | %-25s |%n", "Booked Date", ticket.getBookedDate());
                   System.out.printf("| %-20s | %-25d |%n", "No. of Tickets", ticket.getNoOfTickets());
                   System.out.println("+---------------------+-----------------------------+");
               }
     }
     private static void printRouteTable(List<Route> routeList) {
              System.out.println("+-----+---------------------+");
              System.out.println("| ID  | From                |");
              System.out.println("+-----+---------------------+");

              for (Route route : routeList) {
                   System.out.printf("| %-3d | %-19s |\n", 
                   route.getId(),
                   route.getPlace());
             }

             System.out.println("+-----+---------------------+");
   }
   private static void printRouteToTable(List<Route> routeList) {
    System.out.println("+-----+---------------------+");
    System.out.println("| ID  | To                  |");
    System.out.println("+-----+---------------------+");

    for (Route route : routeList) {
        System.out.printf("| %-3d | %-19s |\n", 
                route.getId(),
                route.getPlace());
    }

    System.out.println("+-----+---------------------+");
   }


}

