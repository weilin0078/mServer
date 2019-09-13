package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import database.DatabaseConnection;

public class ServerProperties
{
    private static final Properties props;
    private static final String[] toLoad;
    
    private ServerProperties() {
    }
    
    public static String getProperty(final String s) {
        return ServerProperties.props.getProperty(s);
    }
    
    public static void setProperty(final String prop, final String newInf) {
        ServerProperties.props.setProperty(prop, newInf);
    }
    
    public static String getProperty(final String s, final String def) {
        return ServerProperties.props.getProperty(s, def);
    }
    
    static {
        props = new Properties();
        toLoad = new String[] { "Settings.ini" };
        for (final String s : ServerProperties.toLoad) {
            try {
                final InputStreamReader fr = new InputStreamReader(new FileInputStream(s), "UTF-8");
                ServerProperties.props.load(fr);
                fr.close();
            }
            catch (IOException ex) {
                System.out.println("º”‘ÿSettings¥ÌŒÛ£∫" + ex);
            }
        }
        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM auth_server_channel_ip");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServerProperties.props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex2) {
            ex2.printStackTrace();
            System.exit(0);
        }
    }
}
