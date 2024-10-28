package source.view;

import java.util.Scanner;
import java.sql.SQLException;

import source.view.AdminHelper;
import source.controller.AdminDb;

public class AdminInfo {
    private Scanner ob = new Scanner(System.in);
    private AdminDb adminDb;
    public AdminInfo(AdminDb adminDb) {
        this.adminDb = adminDb;
    }
    public void show(int userId) throws SQLException {
        boolean continueLoop = true;
        while (continueLoop) {
            printMenu();
            int choice = getUserChoice();

            switch(choice) {
                case 1:
                    new AdminHelper(adminDb).addBus(userId);  
                    break;
                case 2:
                    System.out.println(adminDb.displayBus(userId)); 
                    break;
                case 3:
                    new AdminHelper(adminDb).editBus(userId);
                    break;
                case 4:
                    new AdminHelper(adminDb).viewTickets(userId);
                    break;
                case 5:
                    continueLoop = false;
                    break;
                default:
                    System.out.println("Please enter a valid choice.");
                    break;
            }
        }
    }
    private void printMenu() {
        System.out.println("----------------------------------");
        System.out.println("1. Add Bus");
        System.out.println("2. Display Bus Details");
        System.out.println("3. Edit Bus Details");
        System.out.println("4. View Booked Tickets");
        System.out.println("5. Exit");
        System.out.println("----------------------------------");
        System.out.print("Enter your choice: ");
    }
    private int getUserChoice() {
        return ob.nextInt();
    }
    }
