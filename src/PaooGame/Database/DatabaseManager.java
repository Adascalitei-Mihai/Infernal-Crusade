package PaooGame.Database;

import PaooGame.Game;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public void connect(String url) {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Conexiune la baza de date stabilita.");
        }
        catch (SQLException e) {
            System.out.println("Eroare la conectarea DB: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        }
        catch (SQLException e) {
            System.out.println("Eroare la inchiderea DB: " + e.getMessage());
        }
    }

    public void loadOrCreatePlayer(String name, Game game) {
        String querySelect = "SELECT * FROM Players WHERE Name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(querySelect)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                game.setPlayerID(rs.getInt("ID_P"));
                game.setPlayerName(rs.getString("Name"));
                game.setCurrLevel(rs.getInt("CurrLevel"));
                game.setCurrChkp(rs.getInt("CurrChkp"));
                System.out.println("Profil încărcat: " + name + " (ID: " + game.getPlayerID() + ")");
            }
            else {
                createNewPlayer(name, game);
            }
        }
        catch (SQLException e) {
            System.err.println("Eroare la loadOrCreatePlayer: " + e.getMessage());
        }
    }

    private void createNewPlayer(String name, Game game) {
        String queryInsert = "INSERT INTO Players(Name, CurrLevel, CurrChkp) VALUES(?, 1, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                game.setPlayerID(generatedKeys.getInt(1));
                game.setPlayerName(name);
                game.setCurrLevel(1);
                game.setCurrChkp(0);
                System.out.println("Jucător nou creat: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la crearea jucătorului: " + e.getMessage());
        }
    }

    public int getTotalBestScore(int playerID, int totalRequiredSegments) {
        String sql = "SELECT SUM(Best_Score) AS Total " +
                "FROM Scores " +
                "WHERE ID_P = ? " +
                "GROUP BY ID_P " +
                "HAVING COUNT(ID_S) = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerID);
            pstmt.setInt(2, totalRequiredSegments);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la calcularea scorului total: " + e.getMessage());
        }
        return 0;
    }

    public int getGlobalBestScore(int totalSegmentsRequired) {
        String sql = "SELECT SUM(Best_Score) AS Total " +
                "FROM Scores " +
                "GROUP BY ID_P " +
                "HAVING COUNT(ID_S) = ? " +
                "ORDER BY Total DESC " +
                "LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, totalSegmentsRequired);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la Global Best Score: " + e.getMessage());
        }
        return 0;
    }
}