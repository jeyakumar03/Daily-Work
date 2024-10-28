package source.view;

import source.controller.UserDb;
import source.model.User;


import java.sql.SQLException;
import java.util.Scanner;

public class UserHelper {
    private static Scanner scanner = new Scanner(System.in);
    private static UserDb userdb = new UserDb();
    private static ValidationUtils check=new ValidationUtils();
    public static void signup() throws SQLException {
    String name = check.getValidName();
    String username = check.getValidUsername();
    long mobile = check.getValidMobile();
    String email = check.getValidEmail();
    String password = check.getValidPassword();

    User user = new User(name, username, mobile, email, password);
    if (userdb.insertUser(user)) {
        System.out.println("Signup successful!");
    } else {
        System.out.println("Signup failed. Please try again.");
    }
}


    public static int login() throws SQLException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        int userId = userdb.login(username, password);
        if (userId != -1) {
            System.out.println("Login successful! Welcome, " + username);
            UserInfo.activity(userId);
        } else {
            System.out.println("Invalid username or password.");
        }
        return userId;
    }

    public static void updateUser(int id) throws SQLException {
        while (true) {
            System.out.println("-----------------------------------------------");
            System.out.println("Enter the choice for update:\n1.Name\n2.Mobile\n3.Mail\n4.Password\n5.username\n6.Exit");
            System.out.println("-----------------------------------------------");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Enter the new name: ");
                    String name = check.getValidName();
                    if (userdb.updateUser("name", name, id)) {
                        System.out.println("Updated successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;
                case 2:
                    System.out.println("Enter the new mobile number: ");
                    long mobile = check.getValidMobile();
                    if (userdb.updateUser("mobile", String.valueOf(mobile), id)) {
                        System.out.println("Updated successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;
                case 3:
                    System.out.println("Enter the new email: ");
                    String email = check.getValidEmail();
                    if (userdb.updateUser("email", email, id)) {
                        System.out.println("Updated successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;
                case 4:
                    String password = check.getValidPassword();
                    if (userdb.updateUserCredentials("password", password, id)) {
                        System.out.println("Updated successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;
                case 5:
                    String username =check.getValidUsername();
                    if (userdb.updateUserCredentials("username", username, id)) {
                        System.out.println("Updated successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                    break;
                case 6:
                    System.out.println("Exiting update...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void viewTicket(int userId) {
        String result = userdb.viewTicket(userId);
        System.out.println(result);
    }

    public static void cancelTicket(int userId) {
        String result = userdb.viewTicket(userId);
        System.out.println(result);
        System.out.println("Enter the booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        boolean success = userdb.cancelTicket(bookingId);
        if (success) {
            System.out.println("Ticket cancelled successfully.");
        } else {
            System.out.println("Failed to cancel the ticket or cancellation not allowed.");
        }
    }

    
}

