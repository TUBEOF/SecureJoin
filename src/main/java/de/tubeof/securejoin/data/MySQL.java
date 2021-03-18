package de.tubeof.securejoin.data;

import de.tubeof.securejoin.main.SecureJoin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;
import java.util.UUID;

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

    private final String tableUserAuthKeys = cache.getStringValue("DataPool.MySQL.Prefix") + "userAuthKeys";

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

    public boolean hasPlayerAuthEnabled(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT uuid FROM " + tableUserAuthKeys + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return hasPlayerAuthEnabled(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAuthKey(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT authKey FROM " + tableUserAuthKeys + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = getResult(ps);
            if(rs.next()) return rs.getString("authKey");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return getAuthKey(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public boolean isAuthKeyExists(String authKey) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT authKey FROM " + tableUserAuthKeys + " WHERE authKey = ?");
            ps.setString(1, authKey);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return isAuthKeyExists(authKey);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAuthVerified(String authKey) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT verified FROM " + tableUserAuthKeys + " WHERE authKey = ?");
            ps.setString(1, authKey);
            ResultSet rs = ps.executeQuery();
            return rs.getBoolean("verified");
        } catch (SQLNonTransientConnectionException e) {
            reconnect();
            return isAuthKeyExists(authKey);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createAuthKey(UUID uuid, String authKey) {
        if (!isAuthKeyExists(authKey)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO " + tableUserAuthKeys + " (uuid,authKey,verified) VALUES (?,?,?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, authKey);
                ps.setBoolean(3, false);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return createAuthKey(uuid, authKey);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateVerifiedState(String authKey, Boolean state) {
        if (isAuthKeyExists(authKey)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("UPDATE " + tableUserAuthKeys + " SET verified = ? WHERE authKey = ?");
                ps.setBoolean(1, state);
                ps.setString(2, authKey);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return updateVerifiedState(authKey, state);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteAuthKey(String authKey) {
        if (isAuthKeyExists(authKey)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM " + tableUserAuthKeys + " WHERE authkey = ?");
                ps.setString(1, authKey);
                ps.executeUpdate();

                return true;
            } catch (SQLNonTransientConnectionException e) {
                reconnect();
                return deleteAuthKey(authKey);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void createTable() {
        if (!isConnected()) {
            ccs.sendMessage(data.getLoggerPrefix() + "§cThere is no connection to MySQL!");
            return;
        }
        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + tableUserAuthKeys + "(uuid VARCHAR(36), authKey VARCHAR(32), verified BOOLEAN)");
            ps.executeUpdate();
            PreparedStatement ps1 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + tableUserAuthKeys + "(uuid VARCHAR(36), lastConnectedIp VARCHAR(32), lastConnectedTimestamp LONG)");
            ps1.executeUpdate();

            ccs.sendMessage(data.getLoggerPrefix() + "§aDefault tables were created successfully!");
        } catch (SQLException e) {
            ccs.sendMessage(data.getLoggerPrefix() + "§cDefault tables could not be created!");
            e.printStackTrace();
        }
    }

    public ResultSet getResult(PreparedStatement preparedStatement) {
        try {
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
