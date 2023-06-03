package covid;

import java.sql.*;
import java.util.Scanner;

public class CovidVaccinationApp {
   
    private static final String DB_URL = "jdbc:mysql://localhost/covid_vaccination";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "pradiksha";
    private static final String USER_TABLE = "users";
    private static final String VACCINATION_CENTRE_TABLE = "vaccination_centres";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    
    private static Connection connection;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        try {
           
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            
            createTables();

           
            boolean exit = false;
            while (!exit) {
                showMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        signUp();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        searchVaccinationCentres();
                        break;
                    case 4:
                        applyForVaccination();
                        break;
                    case 5:
                        adminLogin();
                        break;
                    case 6:
                        addVaccinationCentre();
                        break;
                    case 7:
                        getDosageDetails();
                        break;
                    case 8:
                        removeVaccinationCentre();
                        break;
                    case 9:
                        logout();
                        break;
                    case 10:
                        exit = true;
                        System.out.println("Exiting the application.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showMenu() {
        System.out.println("----- COVID Vaccination Booking -----");
        System.out.println("1. Sign up");
        System.out.println("2. Login");
        System.out.println("3. Search Vaccination Centres");
        System.out.println("4. Apply for Vaccination");
        System.out.println("5. Admin Login");
        System.out.println("6. Add Vaccination Centre");
        System.out.println("7. Get Dosage Details");
        System.out.println("8. Remove Vaccination Centre");
        System.out.println("9. Logout");
        System.out.println("10. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void signUp() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (createUser(username, password)) {
            System.out.println("Sign up successful!");
        } else {
            System.out.println("Sign up failed. Please try again.");
        }
    }

    private static void login() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authenticateUser(username, password)) {
            System.out.println("Login successful!");

            
            boolean loggedIn = true;
            while (loggedIn) {
                showUserMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        searchVaccinationCentres();
                        break;
                    case 2:
                        applyForVaccination();
                        break;
                    case 3:
                        loggedIn = false;
                        logout();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    private static void showUserMenu() {
        System.out.println("----- User Menu -----");
        System.out.println("1. Search Vaccination Centres");
        System.out.println("2. Apply for Vaccination");
        System.out.println("3. Logout");
        System.out.print("Enter your choice: ");
    }

    private static void searchVaccinationCentres() throws SQLException {
        System.out.print("Enter the location: ");
        String location = scanner.nextLine();

        ResultSet resultSet = searchVaccinationCentresByLocation(location);
        printVaccinationCentres(resultSet);
    }

    private static void applyForVaccination() throws SQLException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        if (applyForVaccinationSlot(username)) {
            System.out.println("Vaccination slot applied successfully!");
        } else {
            System.out.println("Failed to apply for a vaccination slot. Please try again.");
        }
    }

    private static void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();

        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (authenticateAdmin(username, password)) {
            System.out.println("Admin login successful!");

            
            boolean loggedIn = true;
            while (loggedIn) {
                showAdminMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        addVaccinationCentre();
                        break;
                    case 2:
                        getDosageDetails();
                        break;
                    case 3:
                        removeVaccinationCentre();
                        break;
                    case 4:
                        loggedIn = false;
                        logout();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } else {
            System.out.println("Admin login failed. Invalid username or password.");
        }
    }

    private static void showAdminMenu() {
        System.out.println("----- Admin Menu -----");
        System.out.println("1. Add Vaccination Centre");
        System.out.println("2. Get Dosage Details");
        System.out.println("3. Remove Vaccination Centre");
        System.out.println("4. Logout");
        System.out.print("Enter your choice: ");
    }

    private static void addVaccinationCentre() throws SQLException {
        System.out.print("Enter the centre name: ");
        String centreName = scanner.nextLine();

        System.out.print("Enter the location: ");
        String location = scanner.nextLine();

        System.out.print("Enter the working hours: ");
        String workingHours = scanner.nextLine();

        if (addVaccinationCentre(centreName, location, workingHours)) {
            System.out.println("Vaccination centre added successfully!");
        } else {
            System.out.println("Failed to add vaccination centre. Please try again.");
        }
    }

    private static void getDosageDetails() throws SQLException {
        ResultSet resultSet = getDosageDetailsByCentre();
        printDosageDetails(resultSet);
    }

    private static void removeVaccinationCentre() throws SQLException {
        System.out.print("Enter the centre name: ");
        String centreName = scanner.nextLine();

        if (removeVaccinationCentre(centreName)) {
            System.out.println("Vaccination centre removed successfully!");
        } else {
            System.out.println("Failed to remove vaccination centre. Please try again.");
        }
    }

    private static void logout() {
     
        System.out.println("Logged out successfully!");
    }

    private static void createTables() throws SQLException {
        Statement statement = connection.createStatement();

       
        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
                ")";
        statement.execute(createUserTableQuery);

        
        String createCentreTableQuery = "CREATE TABLE IF NOT EXISTS " + VACCINATION_CENTRE_TABLE + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "centre_name VARCHAR(255) NOT NULL," +
                "location VARCHAR(255) NOT NULL," +
                "working_hours VARCHAR(255) NOT NULL" +
                ")";
        statement.execute(createCentreTableQuery);
    }

    private static boolean createUser(String username, String password) throws SQLException {
        String insertUserQuery = "INSERT INTO " + USER_TABLE + " (username, password) VALUES (?, ?)";

        PreparedStatement statement = connection.prepareStatement(insertUserQuery);
        statement.setString(1, username);
        statement.setString(2, password);

        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    }

    private static boolean authenticateUser(String username, String password) throws SQLException {
        String selectUserQuery = "SELECT * FROM " + USER_TABLE + " WHERE username = ? AND password = ?";

        PreparedStatement statement = connection.prepareStatement(selectUserQuery);
        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private static ResultSet searchVaccinationCentresByLocation(String location) throws SQLException {
        String selectCentresQuery = "SELECT * FROM " + VACCINATION_CENTRE_TABLE + " WHERE location = ?";

        PreparedStatement statement = connection.prepareStatement(selectCentresQuery);
        statement.setString(1, location);

        return statement.executeQuery();
    }

    private static void printVaccinationCentres(ResultSet resultSet) throws SQLException {
        System.out.println("----- Vaccination Centres -----");
        while (resultSet.next()) {
            String centreName = resultSet.getString("centre_name");
            String location = resultSet.getString("location");
            String workingHours = resultSet.getString("working_hours");

            System.out.println("Centre Name: " + centreName);
            System.out.println("Location: " + location);
            System.out.println("Working Hours: " + workingHours);
            System.out.println();
        }
    }

    private static boolean applyForVaccinationSlot(String username) throws SQLException {
     
        return false;
    }

    private static boolean authenticateAdmin(String username, String password) {
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    private static boolean addVaccinationCentre(String centreName, String location, String workingHours) throws SQLException {
        String insertCentreQuery = "INSERT INTO " + VACCINATION_CENTRE_TABLE + " (centre_name, location, working_hours) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(insertCentreQuery);
        statement.setString(1, centreName);
        statement.setString(2, location);
        statement.setString(3, workingHours);

        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    }

    private static ResultSet getDosageDetailsByCentre() throws SQLException {
        String dosageDetailsQuery = "SELECT vaccination_centres.centre_name, COUNT(*) AS dosage_count " +
                "FROM vaccination_centres " +
                "JOIN applications ON vaccination_centres.id = applications.vaccination_centre_id " +
                "GROUP BY vaccination_centres.id";

        Statement statement = connection.createStatement();
        return statement.executeQuery(dosageDetailsQuery);
    }

    private static void printDosageDetails(ResultSet resultSet) throws SQLException {
        System.out.println("----- Dosage Details -----");
        while (resultSet.next()) {
            String centreName = resultSet.getString("centre_name");
            int dosageCount = resultSet.getInt("dosage_count");

            System.out.println("Centre Name: " + centreName);
            System.out.println("Dosage Count: " + dosageCount);
            System.out.println();
        }
    }

    private static boolean removeVaccinationCentre(String centreName) throws SQLException {
        String removeCentreQuery = "DELETE FROM " + VACCINATION_CENTRE_TABLE + " WHERE centre_name = ?";

        PreparedStatement statement = connection.prepareStatement(removeCentreQuery);
        statement.setString(1, centreName);

        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    }
}
