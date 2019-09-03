package handling.login.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.LoginCrypto;
import constants.ServerConstants;
import database.DatabaseConnection;

public class AutoRegister
{
    private static final int ACCOUNTS_PER_MAC = 1;
    public static boolean autoRegister;
    public static boolean success;
    public static boolean mac;
    
    public static boolean getAccountExists(final String login) {
        boolean accountExists = false;
        final Connection con = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
        return accountExists;
    }
    
    public static void createAccount(final String login, final String pwd, final String eip, final String macs) {
        final String sockAddr = eip;
        Connection con;
        try {
            con = DatabaseConnection.getConnection();
        }
        catch (Exception ex) {
            System.out.println(ex);
            return;
        }
        try {
            final PreparedStatement ipc = con.prepareStatement("SELECT macs FROM accounts WHERE macs = ?");
            ipc.setString(1, macs);
            final ResultSet rs = ipc.executeQuery();
            if (!rs.first() || (rs.last() && rs.getRow() < 1)) {
                final PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, login);
                ps.setString(2, LoginCrypto.hexSha1(pwd));
                ps.setString(3, "autoregister@mail.com");
                ps.setString(4, "2016-04-10");
                ps.setString(5, macs);
                ps.setString(6, "/" + sockAddr.substring(1, sockAddr.lastIndexOf(58)));
                ps.executeUpdate();
                AutoRegister.success = true;
            }
            if (rs.getRow() >= 1) {
                AutoRegister.mac = false;
            }
            rs.close();
        }
        catch (SQLException ex2) {
            System.out.println(ex2);
        }
    }
    
    static {
        AutoRegister.autoRegister = ServerConstants.getAutoReg();
        AutoRegister.success = false;
        AutoRegister.mac = true;
    }
}
