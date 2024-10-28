package source.view;

import source.controller.BookingDb;
import source.model.Booking;
import source.model.Passenger;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.HashSet;

public class BookingHelper {
    private static Scanner scanner = new Scanner(System.in);
    private static BookingDb bookingDb = new BookingDb();

    public static boolean bookTicket(int userId) throws SQLException {
        System.out.println("Select the date of travel (You can select from the next 7 days):");
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate travelDate = today.plusDays(i);
            System.out.println((i + 1) + ". " + travelDate);
        }
        int dateChoice = getValidDateChoice();
        if (dateChoice == -1) return false;
        LocalDate selectedDate = today.plusDays(dateChoice - 1);

        System.out.println("Select the starting place:");
        System.out.println(bookingDb.displayFromRoute());
        int from_id = getValidRouteId("Starting place");

        System.out.println("Select the destination place:");
        System.out.println(bookingDb.displayToRoute());
        int to_id = getValidRouteId("Destination place");

        List<String> busDetails = bookingDb.getBusdetails(from_id, to_id);
        if (busDetails.isEmpty()) {
            System.out.println("No buses available for this route on " + selectedDate + ". Please select another location or date.");
            return false;
        }

        displayBusDetails(busDetails);
        int bus_id = getValidBusId();

        String busDetailsById = bookingDb.getBusDetailsById(bus_id);
        if (busDetailsById == null) {
            System.out.println("No bus details found for bus ID: " + bus_id);
            return false;
        }

        String[] busInfo = busDetailsById.split("\\|");
        LocalTime departureTime = LocalTime.parse(busInfo[6]);

        if (selectedDate.equals(today)) {
            LocalTime currentTime = LocalTime.now();

            if (currentTime.isAfter(departureTime)) {
                System.out.println("Booking is not allowed for past departure times.");
                return false;
            }
        }

        int busCapacity = bookingDb.seatCapacity(bus_id);
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);
        int booked_seat = bookingDb.available_seat(bus_id, sqlDate);
        int available_seat = busCapacity - booked_seat;
        System.out.println("Available Seats: " + available_seat);

        displaySeatLayout(bus_id, selectedDate);
        List<Integer> selectedSeats = getUserSelectedSeats(bus_id, sqlDate, available_seat);
        if (selectedSeats.isEmpty()) {
            return false;
        }

        List<Passenger> passengers = getPassengerDetails(selectedSeats.size());
        Booking booking = new Booking(userId, bus_id, selectedSeats.size(), selectedDate);
        boolean bookingSuccess = bookingDb.bookTicket(booking, passengers, selectedSeats);

        if (bookingSuccess) {
            System.out.println("----------------------------------------------------");
            System.out.println("Ticket booked successfully for " + selectedDate + "!");
            displayBookingDetails(bus_id, selectedDate, selectedSeats, passengers);
            System.out.println("----------------------------------------------------");
            return true;
        } else {
            System.out.println("Failed to book the ticket. Please try again.");
            return false;
        }
    }

    private static void displaySeatLayout(int busId, LocalDate travelDate) throws SQLException {
        boolean[] seatAvailability = bookingDb.getSeatAvailability(busId, java.sql.Date.valueOf(travelDate));

        System.out.println("Seat Layout :(X-Booked)");

        for (int i = 0; i < seatAvailability.length; i++) {
            if (i % 4 == 0 && i != 0) {
                System.out.println();
            }

            if (i % 4 == 2) {
                System.out.print("    ");
            }

            if (seatAvailability[i]) {
                System.out.print(" X  ");
            } else {
                if (i + 1 <= 9) {
                    System.out.print(" 0" + (i + 1) + " ");
                } else {
                    System.out.print(" " + (i + 1) + " ");
                }
            }
        }

        System.out.println();
    }

    private static List<Integer> getUserSelectedSeats(int busId, java.sql.Date travelDate, int availableSeats) throws SQLException {
        List<Integer> selectedSeats = new ArrayList<>();
        HashSet<Integer> bookedSeats = bookingDb.getBookedSeats(busId, travelDate);
        System.out.println("Enter seat numbers you want to book (available seats only): ");
        while (true) {
            System.out.print("Select seat (Enter -1 to finish): ");
            int seatNumber = scanner.nextInt();
            scanner.nextLine();
            if (seatNumber == -1) {
                break;
            }
            if (seatNumber < 1 || seatNumber > 40) {
                System.out.println("Invalid seat number. Please select a valid seat.");
            } else if (bookedSeats.contains(seatNumber)) {
                System.out.println("Seat " + seatNumber + " is already booked. Please select another.");
            } else {
                selectedSeats.add(seatNumber);
                if (selectedSeats.size() >= availableSeats) {
                    System.out.println("You've selected all available seats.");
                    break;
                }
            }
        }
        return selectedSeats;
    }

    private static int getValidDateChoice() {
        System.out.println("Enter the number of the date you want to select (1-7):");
        int dateChoice = scanner.nextInt();
        scanner.nextLine();

        if (dateChoice < 1 || dateChoice > 7) {
            System.out.println("Invalid choice. Please select a number between 1 and 7.");
            return -1;
        }
        return dateChoice;
    }

    private static int getValidRouteId(String routeType) {
        System.out.println("Select the " + routeType + " ID:");
        return scanner.nextInt();
    }

    private static void displayBusDetails(List<String> busDetails) {
        System.out.println("+--------+-----------------+--------------------+------------------+---------------+-------------+------------------+-------------+-------------------+");
        System.out.println("| ID     | Bus Number      | Seat Capacity      | AC Type          | Sleeper       | Travels     | From Place       | To Place    | Departure Time    |");
        System.out.println("+--------+-----------------+--------------------+------------------+---------------+-------------+------------------+-------------+-------------------+");
        for (String bus : busDetails) {
            String[] details = bus.split("\\|");
            System.out.printf("| %-6s | %-15s | %-18s | %-16s | %-13s | %-11s | %-16s | %-10s | %-17s |\n",
                    details[0], details[1], details[2], details[3], details[4], details[5], details[6], details[7], details[8]);
        }
        System.out.println("+--------+-----------------+--------------------+------------------+---------------+-------------+------------------+-------------+-------------------+");
    }

    private static int getValidBusId() {
        System.out.println("Select the Bus ID:");
        return scanner.nextInt();
    }

    private static List<Passenger> getPassengerDetails(int numberOfPassengers) {
        List<Passenger> passengers = new ArrayList<>();
        String email = "";
        String mobile = "";
        for (int i = 0; i < numberOfPassengers; i++) {
            System.out.println("Enter details for Passenger " + (i + 1) + ":");
            String name = getValidName();
            String gender = getValidGender();
            if (i == 0) {
                mobile = getValidMobile();
                email = getValidEmail();
            }
            passengers.add(new Passenger(name, gender, mobile, email));
        }
        return passengers;
    }

    private static String getValidName() {
        String name;
        while (true) {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty() && name.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid name. Please enter a valid name (alphabetic characters only).");
            }
        }
        return name;
    }

    private static String getValidGender() {
        String gender;
        while (true) {
            System.out.print("Gender (M/F): ");
            gender = scanner.nextLine().trim().toUpperCase();
            if (gender.equals("M") || gender.equals("F")) {
                break;
            } else {
                System.out.println("Invalid gender. Please enter M for Male or F for Female.");
            }
        }
        return gender;
    }

    private static String getValidMobile() {
        String mobile;
        while (true) {
            System.out.print("Enter Mobile (10 digits): ");
            mobile = scanner.nextLine().trim();
            if (mobile.matches("\\d{10}")) {
                break;
            } else {
                System.out.println("Invalid mobile number. Please enter a valid 10-digit mobile number.");
            }
        }
        return mobile;
    }

    private static String getValidEmail() {
        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine().trim();
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (email.matches(emailRegex)) {
                break;
            } else {
                System.out.println("Invalid email address. Please enter a valid email.");
            }
        }
        return email;
    }

    private static void displayBookingDetails(int busId, LocalDate selectedDate, List<Integer> selectedSeats, List<Passenger> passengers) throws SQLException {
        String busDetails = bookingDb.getBusDetailsById(busId);

        if (busDetails != null) {
            String[] busInfo = busDetails.split("\\|");
            String busType = busInfo[3].equals("t") ? "AC" : "Non-AC";
            String sleeperType = busInfo[2].equals("t") ? "Sleeper" : "Semi-Sleeper";

            System.out.println("-----------------------------------------------------");
            System.out.println("Booking Details");
            System.out.println("-----------------------------------------------------");
            System.out.println("Bus Number                     : " + busInfo[0]);
            System.out.println("Travels                        : " + busInfo[1]);
            System.out.println("Bus Type                       : " + busType);
            System.out.println("Bus Type (Sleeper/Semi-Sleeper): " + sleeperType);
            System.out.println("From                           : " + busInfo[4]);
            System.out.println("To                             : " + busInfo[5]);
            System.out.println("Departure Time                 : " + busInfo[6]);
            System.out.println("Date of Travel                 : " + selectedDate);

            for (int seat : selectedSeats) {
                System.out.println("Seat Number                    : " + seat);
            }

            System.out.println("------------------------------------------------------");
            System.out.println("Passenger Details");
            System.out.println("----------------------------------------------------- ");

            for (Passenger passenger : passengers) {
                System.out.println("Name      : " + passenger.getName());
                System.out.println("Gender    : " + passenger.getGender());
                System.out.println("Mobile    : " + passenger.getMobile());
                System.out.println("Email     : " + passenger.getEmail());
                System.out.println("-------------------------------------------------");
            }
        } else {
            System.out.println("Bus details not found.");
        }
    }
}
