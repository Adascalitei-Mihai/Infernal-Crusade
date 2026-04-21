package PaooGame.Database;

import PaooGame.Handler;

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

    public void loadOrCreatePlayer(String name, Handler handler) {
        String querySelect = "SELECT * FROM Players WHERE Name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(querySelect)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                handler.setPlayerID(rs.getInt("ID_P"));
                handler.setPlayerName(rs.getString("Name"));
                handler.setCurrLevel(rs.getInt("CurrLevel"));
                handler.setCurrChkp(rs.getInt("CurrChkp"));
                System.out.println("Profil încărcat: " + name + " (ID: " + handler.getPlayerID() + ")");
            }
            else {
                createNewPlayer(name, handler);
            }
        }
        catch (SQLException e) {
            System.err.println("Eroare la loadOrCreatePlayer: " + e.getMessage());
        }
    }

    private void createNewPlayer(String name, Handler handler) {
        String queryInsert = "INSERT INTO Players(Name, CurrLevel, CurrChkp) VALUES(?, 1, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                handler.setPlayerID(generatedKeys.getInt(1));
                handler.setPlayerName(name);
                handler.setCurrLevel(1);
                handler.setCurrChkp(0);
                System.out.println("Jucător nou creat: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la crearea jucătorului: " + e.getMessage());
        }
    }
    public void saveScore(int playerID, int levelID, int startChkp, int endChkp, int score) {
        String checkSql = "SELECT Best_Score FROM Scores WHERE ID_P = ? AND Level_ID = ? AND Start_Chkp = ? AND End_Chkp = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
            pstmt.setInt(1, playerID);
            pstmt.setInt(2, levelID);
            pstmt.setInt(3, startChkp);
            pstmt.setInt(4, endChkp);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int oldScore = rs.getInt("Best_Score");
                if (score > oldScore) {
                    String updateSql = "UPDATE Scores SET Best_Score = ? WHERE ID_P = ? AND Level_ID = ? AND Start_Chkp = ? AND End_Chkp = ?";
                    try (PreparedStatement updPstmt = connection.prepareStatement(updateSql)) {
                        updPstmt.setInt(1, score);
                        updPstmt.setInt(2, playerID);
                        updPstmt.setInt(3, levelID);
                        updPstmt.setInt(4, startChkp);
                        updPstmt.setInt(5, endChkp);
                        updPstmt.executeUpdate();
                        System.out.println("Scor actualizat: " + score);
                    }
                }
            } else {
                String insertSql = "INSERT INTO Scores (ID_P, Level_ID, Start_Chkp, End_Chkp, Best_Score) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insPstmt = connection.prepareStatement(insertSql)) {
                    insPstmt.setInt(1, playerID);
                    insPstmt.setInt(2, levelID);
                    insPstmt.setInt(3, startChkp);
                    insPstmt.setInt(4, endChkp);
                    insPstmt.setInt(5, score);
                    insPstmt.executeUpdate();
                    System.out.println("Scor nou inserat!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare manuală la salvare scor: " + e.getMessage());
        }
    }
    public void updatePlayerCheckpoint(int playerID, int newChkp) {
        String sql = "UPDATE Players SET CurrChkp = ? WHERE ID_P = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newChkp);
            pstmt.setInt(2, playerID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Progres salvat în DB: Checkpoint " + newChkp);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la updatePlayerCheckpoint: " + e.getMessage());
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