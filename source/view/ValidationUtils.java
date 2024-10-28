package source.view;
import java.util.Scanner;
import java.util.regex.Pattern;
public class ValidationUtils {
    private static Scanner scanner = new Scanner(System.in);
     String getValidName() {
        String name;
        while (true) {
            System.out.print("Enter the name: ");
            name = scanner.nextLine();
            if (isValidName(name)) {
                return name;
            } else {
                System.out.println("Invalid name! Name should only contain letters and spaces.");
            }
        }
    }

     String getValidUsername() {
        String username;
        while (true) {
            System.out.print("Enter the username: ");
            username = scanner.nextLine();
            if (isValidUsername(username)) {
                return username;
            } else {
                System.out.println("Invalid username! Username should be between 3 and 20 characters.");
            }
        }
    }

      long getValidMobile() {
        long mobile;
        while (true) {
            System.out.print("Enter the mobile number: ");
            try {
                mobile = scanner.nextLong();
                scanner.nextLine();
                if (isValidMobile(mobile)) {
                    return mobile;
                } else {
                    System.out.println("Invalid mobile number! Please enter a valid 10-digit mobile number.");
                }
            } catch (Exception e) {
                scanner.nextLine(); 
                System.out.println("Please enter a numeric value for the mobile number.");
            }
        }
    }

      String getValidEmail() {
        String email;
        while (true) {
            System.out.print("Enter the email id: ");
            email = scanner.nextLine();
            if (isValidEmail(email)) {
                return email;
            } else {
                System.out.println("Invalid email format! Please enter a valid email.");
            }
        }
    }

      String getValidPassword() {
        String password;
        while (true) {
            System.out.print("Enter the password: ");
            password = scanner.nextLine();
            if (isValidPassword(password)) {
                return password;
            } else {
                System.out.println("Invalid password! Password must be at least 8 characters long and contain a mix of letters, numbers, and special characters.");
            }
        }
    }

    private  boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z\\s]+$");
    }

    private  boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 20;
    }

    private  boolean isValidMobile(long mobile) {
        String mobileStr = String.valueOf(mobile);
        return mobileStr.length() == 10;
    }

    private  boolean isValidEmail(String email) {
        String emailRegex = "^[a-z0-9_+&*-]+(?:\\.[a-z0-9_+&*-]+)*@(?:[a-z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    private  boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }
}

