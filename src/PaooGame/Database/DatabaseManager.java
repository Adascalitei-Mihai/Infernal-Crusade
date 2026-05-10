package PaooGame.Database;

import PaooGame.Handler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clasa care comunica cu baza de date
 * @see java.sql
 */
public class DatabaseManager {
    private Connection connection;

    /**
     * Metoda de conectare <br>
     * Apeleaza {@link #createTables()}
     * @param url Locatia bazei de date
     */
    public void connect(String url) {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("[DATABASE]: Conexiunea la baza de date stabilita.");

            createTables();
        } catch (SQLException e) {
            System.err.println("[EROARE]: Conectarea DB: " + e.getMessage());
        }
    }

    /**
     * Metoda de creeare a tabelelor (daca nu exista)
     */
    private void createTables() {
        String createPlayers = "CREATE TABLE IF NOT EXISTS Players (" +
                "ID_P INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT NOT NULL UNIQUE," +
                "CurrLevel INTEGER," +
                "CurrChkp INTEGER" +
                ");";

        String createScores = "CREATE TABLE IF NOT EXISTS Scores (" +
                "ID_S INTEGER," +
                "ID_P INTEGER," +
                "Level_ID INTEGER," +
                "Start_Chkp INTEGER," +
                "End_Chkp INTEGER," +
                "Best_Score INTEGER," +
                "FOREIGN KEY(ID_P) REFERENCES Players(ID_P) ON DELETE CASCADE," +
                "PRIMARY KEY(ID_S AUTOINCREMENT)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayers);
            stmt.execute(createScores);
        } catch (SQLException e) {
            System.err.println("[EROARE]: Crearea tabelelor: " + e.getMessage());
        }
    }

    /**
     * Metoda de inchidere
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (SQLException e) {
            System.err.println("[EROARE]: Inchiderea DB: " + e.getMessage());
        }
    }

    /**
     * Metoda care seteaza detaliile jucatorului din handler <br>
     * Apeleaza {@link #createNewPlayer(String, Handler)} (daca nu exista)
     * @param name Numele jucatorului
     * @param handler Handler-ul
     */
    public void loadOrCreatePlayer(String name, Handler handler) {
        String querySelect = "SELECT * FROM Players WHERE Name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(querySelect)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                handler.setPlayerID(rs.getInt("ID_P"));
                handler.setCurrLevel(rs.getInt("CurrLevel"));
                handler.setCurrChkp(rs.getInt("CurrChkp"));
                System.out.println("[DATABASE]: Profil incarcat: " + name + " (ID: " + handler.getPlayerID() + ")");
            } else {
                createNewPlayer(name, handler);
            }
        }
        catch (SQLException e) {
            System.err.println("Eroare la loadOrCreatePlayer: " + e.getMessage());
        }
    }

    /**
     * Metoda care creeaza un nou jucator <br>
     * Il initializeaza cu valorile specifice
     * @param name Numele jucatorului
     * @param handler Handler-ul
     */
    private void createNewPlayer(String name, Handler handler) {
        String queryInsert = "INSERT INTO Players(Name, CurrLevel, CurrChkp) VALUES(?, 1, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                handler.setPlayerID(generatedKeys.getInt(1));
                handler.setCurrLevel(1);
                handler.setCurrChkp(0);
                System.out.println("[DATABASE]: Jucator nou creat: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la crearea jucatorului: " + e.getMessage());
        }
    }

    /**
     * Metoda care salveaza noul scor <br>
     * Daca scorul curent este mai bun, acesta este actualizat <br>
     * Daca este prima oara cand acel jucatorul completeaza segmentul, creeaza o intrare noua
     * @param playerID ID-ul jucatorului
     * @param levelID ID-ul nivelului
     * @param startChkp ID-ul checkpoint-ului de inceput
     * @param endChkp ID-ul checkpoint-ului de final
     * @param score Scorul nou
     */
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
                        System.out.println("[DATABASE]: Scor actualizat: " + score);
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
                    System.out.println("[DATABASE]: Scor nou inserat!");
                }
            }
        } catch (SQLException e) {
            System.err.println("[EROARE]: Salvare scor: " + e.getMessage());
        }
    }

    /**
     * Metoda care actualizeaza checkpoint-ul curent
     * @param playerID ID-ul jucatorului
     * @param newChkp ID-ul noului checkpoint
     */
    public void updatePlayerCheckpoint(int playerID, int newChkp) {
        String sql = "UPDATE Players SET CurrChkp = ? WHERE ID_P = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newChkp);
            pstmt.setInt(2, playerID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DATABASE]: Progres salvat in DB: Checkpoint " + newChkp);
            }
        } catch (SQLException e) {
            System.err.println("[EROARE]: Setare checkpoint nou: " + e.getMessage());
        }
    }

    /**
     * Metoda care actualizeaza nivelul si checkpoint-ul curent
     * @param playerID ID-ul jucatorului
     * @param newLevel ID-ul noului nivel
     * @param newChkp ID-ul noului checkpoint
     */
    public void updatePlayerProgress(int playerID, int newLevel, int newChkp) {
        String sql = "UPDATE Players SET CurrLevel = ?, CurrChkp = ? WHERE ID_P = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newLevel);
            pstmt.setInt(2, newChkp);
            pstmt.setInt(3, playerID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DATABASE]: Progres salvat în DB: Nivel " + newLevel + ", Checkpoint " + newChkp);
            }
        } catch (SQLException e) {
            System.err.println("[EROARE]: Setare progres: " + e.getMessage());
        }
    }

    /**
     * Metoda care returneaza scorul total al unui jucator
     * @param playerID ID-ul jucatorului
     * @param totalRequiredSegments Numarul de segmente care trebuie completate
     * @return Scorul total
     */
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
            System.err.println("[EROARE]: Calcularea scorului total: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Metoda care returneaza scorul total global
     * @param totalSegmentsRequired Numarul de segmente care trebuie completate
     * @return Scorul total
     */
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
            System.err.println("[EROARE]: Calcularea scorului total global: " + e.getMessage());
        }
        return 0;
    }
}