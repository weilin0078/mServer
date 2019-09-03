package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.ServerProperties;

public class DatabaseConnection
{
    private static final HashMap<Integer, ConWrapper> connections;
    private static final Logger log;
    private static String dbDriver;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;
    private static final long connectionTimeOut = 1800000L;
    private static final ReentrantLock lock;
    public static final Runnable CloseSQLConnections;
    
    public static int getConnectionsCount() {
        return DatabaseConnection.connections.size();
    }
    
    public static void close() {
        try {
            final Thread cThread = Thread.currentThread();
            final Integer threadID = (int)cThread.getId();
            final ConWrapper ret = DatabaseConnection.connections.get(threadID);
            if (ret != null) {
                final Connection c = ret.getConnection();
                if (!c.isClosed()) {
                    c.close();
                }
                DatabaseConnection.lock.lock();
                try {
                    DatabaseConnection.connections.remove(threadID);
                }
                finally {
                    DatabaseConnection.lock.unlock();
                }
            }
        }
        catch (SQLException ex) {}
    }
    
    public static Connection getConnection() {
        if (!isInitialized()) {
            InitDB();
        }
        final Thread cThread = Thread.currentThread();
        final Integer threadID = (int)cThread.getId();
        ConWrapper ret = DatabaseConnection.connections.get(threadID);
        if (ret == null) {
            final Connection retCon = connectToDB();
            ret = new ConWrapper(threadID, retCon);
            DatabaseConnection.lock.lock();
            try {
                DatabaseConnection.connections.put(threadID, ret);
            }
            finally {
                DatabaseConnection.lock.unlock();
            }
        }
        final Connection c = ret.getConnection();
        try {
            if (c.isClosed()) {
                final Connection retCon2 = connectToDB();
                DatabaseConnection.lock.lock();
                try {
                    DatabaseConnection.connections.remove(threadID);
                    DatabaseConnection.connections.put(threadID, ret);
                }
                finally {
                    DatabaseConnection.lock.unlock();
                }
                ret = new ConWrapper(threadID, retCon2);
            }
        }
        catch (Exception ex) {}
        return ret.getConnection();
    }
    
    private static Connection connectToDB() {
        try {
            final Properties props = new Properties();
            props.put("user", DatabaseConnection.dbUser);
            props.put("password", DatabaseConnection.dbPass);
            props.put("autoReconnect", "true");
            props.put("characterEncoding", "UTF8");
            props.put("connectTimeout", "2000000");
            props.put("serverTimezone", "Asia/Taipei");
            final Connection con = DriverManager.getConnection(DatabaseConnection.dbUrl, props);
            final PreparedStatement ps = con.prepareStatement("SET time_zone = '+08:00'");
            ps.execute();
            ps.close();
            return con;
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
    
    public static boolean isInitialized() {
        return !DatabaseConnection.dbUser.equals("");
    }
    
    public static void InitDB() {
        DatabaseConnection.dbDriver = "com.mysql.jdvc.Driver";
        final String db = ServerProperties.getProperty("database", "twms");
        final String ip = ServerProperties.getProperty("ip", "localhost");
        final String port = ServerProperties.getProperty("port", "3306");
        DatabaseConnection.dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + db + "?autoReconnect=true&characterEncoding=UTF8&maxReconnects=2147483640&connectTimeout=3600000&socketTimeout=3600000";
        DatabaseConnection.dbUser = ServerProperties.getProperty("user", "twms113");
        DatabaseConnection.dbPass = ServerProperties.getProperty("password", "twms113");
    }
    
    public static void closeTimeout() {
        int i = 0;
        DatabaseConnection.lock.lock();
        final List<Integer> keys = new ArrayList<Integer>(DatabaseConnection.connections.keySet());
        try {
            for (final Integer tid : keys) {
                final ConWrapper con = DatabaseConnection.connections.get(tid);
                if (con.close()) {
                    ++i;
                }
            }
        }
        finally {
            DatabaseConnection.lock.unlock();
        }
    }
    
    public static void closeAll() {
        synchronized (DatabaseConnection.connections) {
            for (final ConWrapper con : DatabaseConnection.connections.values()) {
                try {
                    con.connection.close();
                }
                catch (SQLException ex) {}
            }
        }
    }
    
    static {
        connections = new HashMap<Integer, ConWrapper>();
        log = LoggerFactory.getLogger((Class)DatabaseConnection.class);
        DatabaseConnection.dbDriver = "";
        DatabaseConnection.dbUrl = "";
        DatabaseConnection.dbUser = "";
        DatabaseConnection.dbPass = "";
        lock = new ReentrantLock();
        CloseSQLConnections = new Runnable() {
            @Override
            public void run() {
                DatabaseConnection.closeTimeout();
            }
        };
    }
    
    static class ConWrapper
    {
        private final int tid;
        private long lastAccessTime;
        private Connection connection;
        
        public ConWrapper(final int tid, final Connection con) {
            this.tid = tid;
            this.lastAccessTime = System.currentTimeMillis();
            this.connection = con;
        }
        
        public boolean close() {
            boolean ret = false;
            if (this.connection == null) {
                ret = false;
            }
            else {
                try {
                    DatabaseConnection.lock.lock();
                    try {
                        Label_0058: {
                            if (!this.expiredConnection()) {
                                if (!this.connection.isValid(10)) {
                                    break Label_0058;
                                }
                            }
                            try {
                                this.connection.close();
                                ret = true;
                            }
                            catch (SQLException e) {
                                ret = false;
                            }
                        }
                        DatabaseConnection.connections.remove(this.tid);
                    }
                    finally {
                        DatabaseConnection.lock.unlock();
                    }
                }
                catch (SQLException ex) {
                    ret = false;
                }
            }
            return ret;
        }
        
        public Connection getConnection() {
            if (this.expiredConnection()) {
                try {
                    this.connection.close();
                }
                catch (SQLException ex) {}
                this.connection = connectToDB();
            }
            this.lastAccessTime = System.currentTimeMillis();
            return this.connection;
        }
        
        public boolean expiredConnection() {
            return System.currentTimeMillis() - this.lastAccessTime >= 1800000L;
        }
    }
}
