package de.tubeof.securejoin.data;

import de.tubeof.securejoin.main.SecureJoin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;

public class MySQL {

    private final ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    private final Cache cache = SecureJoin.getCache();
    private final Data data = SecureJoin.getData();

    public String host = cache.getStringValue("DataPool.MySQL.Host");
    public String port = cache.getStringValue("DataPool.MySQL.Port");
    public String database = cache.getStringValue("DataPool.MySQL.Database");
    public String username = cache.getStringValue("DataPool.MySQL.Username");
    public String password = cache.getStringValue("DataPool.MySQL.Password");

    private Connection connection;

    public MySQL(boolean connect) {
        if (connect) connect();
    }

    public void connect() {
        if (!isConnected()) {
            try {
                ccs.sendMessage(data.getLoggerPrefix() + "§aTrying to connect to MySQL-Server §e" + host + " §a...");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                ccs.sendMessage(data.getLoggerPrefix() + "§aConnection to MySQL-Server successfully established.");
                createTable();
            } catch (SQLException e) {
                e.printStackTrace();
                ccs.sendMessage(data.getLoggerPrefix() + "§cCould not connect to MySQL!");
            }
        }
    }

    public void reconnect() {
        try {
            ccs.sendMessage(data.getLoggerPrefix() + "§aTrying to reconnect to MySQL-Server §e" + host + " §a...");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            ccs.sendMessage(data.getLoggerPrefix() + "§aConnection to MySQL-Server successfully established.");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
            ccs.sendMessage(data.getLoggerPrefix() + "§cCould not connect to MySQL!");
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
                connection = null;
                ccs.sendMessage(data.getLoggerPrefix() + "§aConnection to MySQL successfully closed.");
            } catch (SQLException e) {
                ccs.sendMessage(data.getLoggerPrefix() + "§cThe connection could not be closed!");
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getPaymentId(String userId) {
        try {
            ResultSet rs = getResult("SELECT * FROM Transactions WHERE userid='" + userId + "'");
            if(rs.next()) return rs.getString("paymentId");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return getPaymentId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public boolean isMessageExists(String messageId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT messageId FROM Messages WHERE messageId = ?");
            ps.setString(1, messageId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return isMessageExists(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPaymentExists(String paymentId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT paymentId FROM Transactions WHERE paymentId = ?");
            ps.setString(1, paymentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return isPaymentExists(paymentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPaymentClaimed(String paymentId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM Transactions WHERE paymentId = ?");
            ps.setString(1, paymentId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getBoolean("DONE");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return isPaymentExists(paymentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMessage(String messageId, String newText) {
        if (isMessageExists(messageId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("UPDATE Messages SET message = ? WHERE messageId = ?");
                ps.setString(1, newText);
                ps.setString(2, messageId);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return updateMessage(messageId, newText);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(messageId + " existiert in der Datenbank nicht und kann nicht geändert werden!");
        }
        return false;
    }

    public boolean createMessage(String messageId, String message, String author) {
        if (!isMessageExists(messageId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO Messages (messageId,message,author) VALUES (?,?,?)");
                ps.setString(1, messageId);
                ps.setString(2, message);
                ps.setString(3, author);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return createMessage(messageId, message, author);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteMessage(String messageId) {
        if (isMessageExists(messageId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM Messages WHERE messageId='" + messageId + "'");
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return deleteMessage(messageId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean createTransaction(String paymentId) {
        if (!isPaymentExists(paymentId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO Transactions (paymentId,userId,email,DONE) VALUES (?,?,?,?)");
                ps.setString(1, paymentId);
                ps.setString(2, "NULL");
                ps.setString(3, "NULL");
                ps.setBoolean(4, false);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return createTransaction(paymentId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean createTransaction(String paymentId, String email) {
        if (!isPaymentExists(paymentId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO Transactions (paymentId,userId,email,DONE) VALUES (?,?,?,?)");
                ps.setString(1, paymentId);
                ps.setString(2, "NULL");
                ps.setString(3, email);
                ps.setBoolean(4, false);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return createTransaction(paymentId, email);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteTransaction(String paymentID) {
        if (isPaymentExists(paymentID)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM Transactions WHERE paymentId='" + paymentID + "'");
                ps.executeUpdate();
                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return deleteTransaction(paymentID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updatePayment(String paymentId, String userId) {
        if (isPaymentExists(paymentId)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("UPDATE Transactions SET userid = ?, DONE = ? WHERE paymentId = ?");
                ps.setString(1, userId);
                ps.setBoolean(2, true);
                ps.setString(3, paymentId);
                ps.executeUpdate();
                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return updatePayment(paymentId, userId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(paymentId + " existiert in der Datenbank nicht und kann nicht geändert werden!");
        }
        return false;
    }

    public void createTable() {
        if (!isConnected()) {
            ccs.sendMessage(data.getLoggerPrefix() + "§cThere is no connection to MySQL!");
            return;
        }
        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + cache.getStringValue("DataPool.MySQL.Prefix") + "userAuthKeys(uuid VARCHAR(36), authKey VARCHAR(32), verified BOOLEAN)");
            ps.executeUpdate();
            PreparedStatement ps1 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + cache.getStringValue("DataPool.MySQL.Prefix") + "userSessions(uuid VARCHAR(36), lastConnectedIp VARCHAR(32), lastConnectedTimestamp LONG)");
            ps1.executeUpdate();

            ccs.sendMessage(data.getLoggerPrefix() + "§aDefault tables were created successfully!");
        } catch (SQLException e) {
            ccs.sendMessage(data.getLoggerPrefix() + "§cDefault tables could not be created!");
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String query) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            return rs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getOldMessage(String messageId) {
        try {
            ResultSet rs = getResult("SELECT * FROM Messages WHERE messageId='" + messageId + "'");
            if(rs.next()) return rs.getString("message");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return getOldMessage(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public String getAuthor(String messageId) {
        try {
            ResultSet rs = getResult("SELECT * FROM Messages WHERE messageId='" + messageId + "'");
            if(rs.next()) return rs.getString("author");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return getAuthor(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NULL";
    }
}
