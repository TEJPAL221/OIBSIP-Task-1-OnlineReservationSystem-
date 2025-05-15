import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class OnlineReservationSystem {
    private static final int MIN_PNR = 1000;
    private static final int MAX_PNR = 9999;
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/tejdb";
        
        // Login module
        User user = new User();
        String username = user.getUsername();
        String password = user.getPassword();

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish database connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("User Connection Granted.\n");
            
            // Create tables if they don't exist
            setupDatabase();
            
            // Main application loop
            boolean running = true;
            while (running) {
                displayMainMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        insertReservation();
                        break;
                    case 2:
                        cancelReservation();
                        break;
                    case 3:
                        showAllReservations();
                        break;
                    case 4:
                        System.out.println("Exiting the program. Thank you for using Online Reservation System!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                scanner.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // User class for authentication
    public static class User {
        private String username;
        private String password;

        public String getUsername() {
            System.out.print("Enter Username: ");
            username = scanner.nextLine();
            return username;
        }

        public String getPassword() {
            System.out.print("Enter Password: ");
            password = scanner.nextLine();
            return password;
        }
    }
    
    // PnrRecord class to handle reservation data
    public static class PnrRecord {
        private int pnrNumber;
        private String passengerName;
        private String trainNumber;
        private String trainName;  // Added to fulfill requirement
        private String classType;
        private String journeyDate;
        private String from;
        private String to;

        public int generatePnrNumber() {
            Random random = new Random();
            pnrNumber = random.nextInt(MAX_PNR - MIN_PNR + 1) + MIN_PNR;
            return pnrNumber;
        }

        public void collectReservationDetails() {
            // Get passenger details with validation
            System.out.print("Enter passenger name: ");
            passengerName = scanner.nextLine();
            
            while (passengerName.trim().isEmpty()) {
                System.out.println("Passenger name cannot be empty.");
                System.out.print("Enter passenger name: ");
                passengerName = scanner.nextLine();
            }
            
            // Get train number
            System.out.print("Enter train number: ");
            trainNumber = scanner.nextLine();
            
            while (trainNumber.trim().isEmpty()) {
                System.out.println("Train number cannot be empty.");
                System.out.print("Enter train number: ");
                trainNumber = scanner.nextLine();
            }
            
            // Simulate retrieving train name based on train number
            trainName = getTrainNameFromNumber(trainNumber);
            System.out.println("Train Name: " + trainName);
            
            // Get class type
            System.out.print("Enter class type (e.g., 1A, 2A, 3A, SL): ");
            classType = scanner.nextLine().toUpperCase();
            
            while (!isValidClassType(classType)) {
                System.out.println("Invalid class type. Valid options are: 1A, 2A, 3A, SL, CC, EC");
                System.out.print("Enter class type: ");
                classType = scanner.nextLine().toUpperCase();
            }
            
            // Get journey date with validation
            boolean validDate = false;
            do {
                System.out.print("Enter journey date (YYYY-MM-DD): ");
                journeyDate = scanner.nextLine();
                validDate = isValidDate(journeyDate);
                if (!validDate) {
                    System.out.println("Invalid date format or past date. Please use YYYY-MM-DD format and ensure date is not in the past.");
                }
            } while (!validDate);
            
            // Get source station
            System.out.print("Enter source station: ");
            from = scanner.nextLine();
            
            while (from.trim().isEmpty()) {
                System.out.println("Source station cannot be empty.");
                System.out.print("Enter source station: ");
                from = scanner.nextLine();
            }
            
            // Get destination station
            System.out.print("Enter destination station: ");
            to = scanner.nextLine();
            
            while (to.trim().isEmpty() || to.equalsIgnoreCase(from)) {
                if (to.trim().isEmpty()) {
                    System.out.println("Destination station cannot be empty.");
                } else {
                    System.out.println("Destination cannot be the same as source.");
                }
                System.out.print("Enter destination station: ");
                to = scanner.nextLine();
            }
        }
        
        // This method would typically fetch from a database
        // Here we're simulating it for the requirement
        private String getTrainNameFromNumber(String trainNumber) {
            // In a real application, this would query the database
            // For now, we'll use some predefined mappings
            switch (trainNumber) {
                case "12301": return "Rajdhani Express";
                case "12302": return "Shatabdi Express";
                case "12303": return "Duronto Express";
                case "12304": return "Garib Rath";
                default: return "Express " + trainNumber;
            }
        }
        
        private boolean isValidClassType(String classType) {
            String[] validClasses = {"1A", "2A", "3A", "SL", "CC", "EC"};
            for (String validClass : validClasses) {
                if (validClass.equals(classType)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean isValidDate(String dateStr) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                Date inputDate = dateFormat.parse(dateStr);
                Date today = new Date();
                // Reset time portion for accurate date comparison
                today.setHours(0);
                today.setMinutes(0);
                today.setSeconds(0);
                return inputDate.after(today) || inputDate.equals(today);
            } catch (ParseException e) {
                return false;
            }
        }
    }
    
    // Database setup
    private static void setupDatabase() {
        try {
            // Create trains table if not exists
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS trains (" +
                    "train_number VARCHAR(10) PRIMARY KEY, " +
                    "train_name VARCHAR(100) NOT NULL)");
                    
            // Create reservations table if not exists
            statement.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "pnr_number INT PRIMARY KEY, " +
                    "passenger_name VARCHAR(100) NOT NULL, " +
                    "train_number VARCHAR(10) NOT NULL, " +
                    "train_name VARCHAR(100) NOT NULL, " +
                    "class_type VARCHAR(5) NOT NULL, " +
                    "journey_date DATE NOT NULL, " +
                    "from_location VARCHAR(100) NOT NULL, " +
                    "to_location VARCHAR(100) NOT NULL)");
                    
            // Insert some sample train data if table is empty
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM trains");
            rs.next();
            if (rs.getInt(1) == 0) {
                statement.execute("INSERT INTO trains VALUES " +
                    "('12301', 'Rajdhani Express'), " +
                    "('12302', 'Shatabdi Express'), " +
                    "('12303', 'Duronto Express'), " +
                    "('12304', 'Garib Rath')");
            }
        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());
        }
    }
    
    // Display main menu
    private static void displayMainMenu() {
        System.out.println("\n======= ONLINE RESERVATION SYSTEM =======");
        System.out.println("1. Make New Reservation");
        System.out.println("2. Cancel Reservation");
        System.out.println("3. View All Reservations");
        System.out.println("4. Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
    }
    
    // Get user choice with validation
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }
    
    // Insert new reservation
    private static void insertReservation() {
        PnrRecord record = new PnrRecord();
        int pnrNumber = record.generatePnrNumber();
        
        // Check if PNR already exists, regenerate if needed
        while (pnrExists(pnrNumber)) {
            pnrNumber = record.generatePnrNumber();
        }
        
        System.out.println("\n== New Reservation (PNR: " + pnrNumber + ") ==");
        record.collectReservationDetails();
        
        String insertQuery = "INSERT INTO reservations VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, pnrNumber);
            preparedStatement.setString(2, record.passengerName);
            preparedStatement.setString(3, record.trainNumber);
            preparedStatement.setString(4, record.trainName);
            preparedStatement.setString(5, record.classType);
            preparedStatement.setString(6, record.journeyDate);
            preparedStatement.setString(7, record.from);
            preparedStatement.setString(8, record.to);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\n✓ Reservation completed successfully!");
                System.out.println("Your PNR Number is: " + pnrNumber);
                System.out.println("Please note down this number for future reference.");
            } else {
                System.out.println("× Failed to create reservation. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating reservation: " + e.getMessage());
        }
    }
    
    // Check if PNR already exists in database
    private static boolean pnrExists(int pnrNumber) {
        String query = "SELECT COUNT(*) FROM reservations WHERE pnr_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, pnrNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking PNR: " + e.getMessage());
        }
        return false;
    }
    
    // Cancel reservation with confirmation
    private static void cancelReservation() {
        System.out.println("\n======= CANCELLATION FORM =======");
        System.out.print("Enter PNR number to cancel: ");
        
        int pnrNumber;
        try {
            pnrNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid PNR format. Please enter a numeric PNR.");
            return;
        }
        
        // First check if the PNR exists and show details
        String selectQuery = "SELECT * FROM reservations WHERE pnr_number = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, pnrNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                // Display reservation details
                System.out.println("\n=== Reservation Details ===");
                System.out.println("PNR Number: " + resultSet.getInt("pnr_number"));
                System.out.println("Passenger Name: " + resultSet.getString("passenger_name"));
                System.out.println("Train Number: " + resultSet.getString("train_number"));
                System.out.println("Train Name: " + resultSet.getString("train_name"));
                System.out.println("Class Type: " + resultSet.getString("class_type"));
                System.out.println("Journey Date: " + resultSet.getString("journey_date"));
                System.out.println("From: " + resultSet.getString("from_location"));
                System.out.println("To: " + resultSet.getString("to_location"));
                System.out.println("===========================");
                
                // Confirm cancellation
                System.out.print("\nDo you want to cancel this reservation? (Y/N): ");
                String confirmation = scanner.nextLine().trim().toUpperCase();
                
                if (confirmation.equals("Y")) {
                    // Process cancellation
                    String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ?";
                    
                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        deleteStatement.setInt(1, pnrNumber);
                        int rowsAffected = deleteStatement.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            System.out.println("\n✓ Reservation cancelled successfully.");
                        } else {
                            System.out.println("× Failed to cancel reservation.");
                        }
                    }
                } else {
                    System.out.println("Cancellation aborted.");
                }
            } else {
                System.out.println("No reservation found with PNR: " + pnrNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error processing cancellation: " + e.getMessage());
        }
    }
    
    // Show all reservations
    private static void showAllReservations() {
        String query = "SELECT * FROM reservations";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            System.out.println("\n========== ALL RESERVATIONS ==========");
            boolean hasRecords = false;
            
            while (resultSet.next()) {
                hasRecords = true;
                System.out.println("\nPNR Number: " + resultSet.getInt("pnr_number"));
                System.out.println("Passenger Name: " + resultSet.getString("passenger_name"));
                System.out.println("Train Number: " + resultSet.getString("train_number"));
                System.out.println("Train Name: " + resultSet.getString("train_name"));
                System.out.println("Class Type: " + resultSet.getString("class_type"));
                System.out.println("Journey Date: " + resultSet.getString("journey_date"));
                System.out.println("From: " + resultSet.getString("from_location"));
                System.out.println("To: " + resultSet.getString("to_location"));
                System.out.println("--------------------------------------");
            }
            
            if (!hasRecords) {
                System.out.println("No reservations found in the system.");
            }
            
            System.out.println("=====================================");
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
        }
    }
}
