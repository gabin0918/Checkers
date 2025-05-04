import java.sql.*;

public class UserDatabase {
    // Zmodyfikuj dane do logowania, jeśli trzeba
    private static final String URL = "jdbc:mysql://localhost:3306/checkersdatabase";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Rejestracja nowego użytkownika
    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Użytkownik już istnieje.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logowanie użytkownika
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // jeśli coś zwrócił — dane poprawne

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Pobieranie rankingu użytkownika
    public static int getUserRanking(String username) {
        String sql = "SELECT ranking FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("ranking");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // nie znaleziono
    }

    // Aktualizacja rankingu
    public static void updateRanking(String username, int newRanking) {
        if (username.startsWith("Gość")) {
            System.out.println("[DB] Użytkownik '" + username + "' to gość – nie zapisuję rankingu.");
            return;
        }
    
        String sql = "UPDATE users SET ranking = ? WHERE username = ?";
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, newRanking);
            stmt.setString(2, username);
            stmt.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateRankingAfterGame(String winnerUsername, String loserUsername) {
        if (winnerUsername.startsWith("Gość") && loserUsername.startsWith("Gość")) {
            System.out.println("[DB] Obaj gracze to goście – ranking nie zmieniony.");
            return;
        }
    
        int winnerRank = getUserRanking(winnerUsername);
        int loserRank = getUserRanking(loserUsername);
    
        updateRanking(winnerUsername, winnerRank + 20);
        updateRanking(loserUsername, Math.max(0, loserRank - 10));
    }
    
    
}
