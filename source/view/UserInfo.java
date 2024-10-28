package source.view;

import source.view.BookingHelper;
import source.view.UserHelper;

import java.util.Scanner;
import java.sql.SQLException;

public class UserInfo {
    private static Scanner ob = new Scanner(System.in);

    public static void show() throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("-------------------------------------------------");
            System.out.println("1. Sign in\n2. Sign up\n3. Exit");
            System.out.println("-------------------------------------------------");
            System.out.println("Enter your choice:");
            int ch = ob.nextInt();
            System.out.println("-------------------------------------------------");

            switch (ch) {
                case 1:
                    UserHelper.login();
                    break;
                case 2:
                    UserHelper.signup();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Please enter a valid choice");
                    break;
            }
        }
    }

    public static void activity(int userId) throws SQLException {
        boolean b = true;
        while (b) {
            System.out.println("-------------------------------------------------");
            System.out.println("1. Book a Ticket\n2. View Booking Details\n3. Cancel Ticket\n4. Edit User Info\n5. Exit");
            System.out.println("Enter your choice:");
            System.out.println("-------------------------------------------------");
            int choice = ob.nextInt();
            System.out.println("-------------------------------------------------");

            switch (choice) {
                case 1:
                    BookingHelper.bookTicket(userId);
                    break;
                case 2:
                    UserHelper.viewTicket(userId);
                    break;
                case 3:
                    UserHelper.cancelTicket(userId);
                    break;
                case 4:
                    UserHelper.updateUser(userId);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Please Enter a Valid Choice");
                    break;
            }
        }
    }
}
