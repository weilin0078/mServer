package handling.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferAllocator;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import server.ServerProperties;
import tools.Triple;

public class LoginServer
{
    public static int PORT;
    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load;
    private static String serverName;
    private static String eventMessage;
    private static byte flag;
    private static int maxCharacters;
    private static int userLimit;
    private static int usersOn;
    private static boolean finishedShutdown;
    private static boolean adminOnly;
    private static final HashMap<Integer, Triple<String, String, Integer>> loginAuth;
    private static final HashSet<String> loginIPAuth;
    private static LoginServer instance;
    
    public static LoginServer getInstance() {
        return LoginServer.instance;
    }
    
    public static void putLoginAuth(final int chrid, final String ip, final String tempIp, final int channel) {
        LoginServer.loginAuth.put(chrid, new Triple<String, String, Integer>(ip, tempIp, channel));
        LoginServer.loginIPAuth.add(ip);
    }
    
    public static Triple<String, String, Integer> getLoginAuth(final int chrid) {
        return LoginServer.loginAuth.remove(chrid);
    }
    
    public static boolean containsIPAuth(final String ip) {
        return LoginServer.loginIPAuth.contains(ip);
    }
    
    public static void removeIPAuth(final String ip) {
        LoginServer.loginIPAuth.remove(ip);
    }
    
    public static void addIPAuth(final String ip) {
        LoginServer.loginIPAuth.add(ip);
    }
    
    public static final void addChannel(final int channel) {
        LoginServer.load.put(channel, 0);
    }
    
    public static final void removeChannel(final int channel) {
        LoginServer.load.remove(channel);
    }
    
    public static final void run_startup_configurations() {
        LoginServer.userLimit = Integer.parseInt(ServerProperties.getProperty("tms.UserLimit"));
        LoginServer.serverName = ServerProperties.getProperty("tms.ServerName");
        LoginServer.eventMessage = ServerProperties.getProperty("tms.EventMessage");
        LoginServer.flag = Byte.parseByte(ServerProperties.getProperty("tms.Flag"));
        LoginServer.PORT = Integer.parseInt(ServerProperties.getProperty("tms.LPort"));
        LoginServer.adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"));
        LoginServer.maxCharacters = Integer.parseInt(ServerProperties.getProperty("tms.MaxCharacters"));
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator((IoBufferAllocator)new SimpleBufferAllocator());
        LoginServer.acceptor = (IoAcceptor)new NioSocketAcceptor();
        LoginServer.acceptor.getFilterChain().addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new MapleCodecFactory()));
        LoginServer.acceptor.setHandler((IoHandler)new MapleServerHandler(-1, false));
        ((SocketSessionConfig)LoginServer.acceptor.getSessionConfig()).setTcpNoDelay(true);
        try {
            LoginServer.acceptor.bind((SocketAddress)new InetSocketAddress(LoginServer.PORT));
            System.out.println("\u670d\u52a1\u5668   \u84dd\u8717\u725b: \u542f\u52a8\u7aef\u53e3 " + LoginServer.PORT);
        }
        catch (IOException e) {
            System.err.println("Binding to port " + LoginServer.PORT + " failed" + e);
        }
    }
    
    public static final void shutdown() {
        if (LoginServer.finishedShutdown) {
            return;
        }
        System.out.println("\u6b63\u5728\u5173\u95ed\u767b\u5f55\u4f3a\u670d\u5668...");
        LoginServer.finishedShutdown = true;
    }
    
    public static final String getServerName() {
        return LoginServer.serverName;
    }
    
    public static final String getEventMessage() {
        return LoginServer.eventMessage;
    }
    
    public static final byte getFlag() {
        return LoginServer.flag;
    }
    
    public static final int getMaxCharacters() {
        return LoginServer.maxCharacters;
    }
    
    public static final Map<Integer, Integer> getLoad() {
        return LoginServer.load;
    }
    
    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        LoginServer.load = load_;
        LoginServer.usersOn = usersOn_;
    }
    
    public static final void setEventMessage(final String newMessage) {
        LoginServer.eventMessage = newMessage;
    }
    
    public static final void setFlag(final byte newflag) {
        LoginServer.flag = newflag;
    }
    
    public static final int getUserLimit() {
        return LoginServer.userLimit;
    }
    
    public static final int getUsersOn() {
        return LoginServer.usersOn;
    }
    
    public static final void setUserLimit(final int newLimit) {
        LoginServer.userLimit = newLimit;
    }
    
    public static final int getNumberOfSessions() {
        return LoginServer.acceptor.getManagedSessions().size();
    }
    
    public static final boolean isAdminOnly() {
        return LoginServer.adminOnly;
    }
    
    public static final boolean isShutdown() {
        return LoginServer.finishedShutdown;
    }
    
    public static final void setOn() {
        LoginServer.finishedShutdown = false;
    }
    
    static {
        LoginServer.PORT = 8484;
        LoginServer.load = new HashMap<Integer, Integer>();
        LoginServer.usersOn = 0;
        LoginServer.finishedShutdown = true;
        LoginServer.adminOnly = false;
        loginAuth = new HashMap<Integer, Triple<String, String, Integer>>();
        loginIPAuth = new HashSet<String>();
        LoginServer.instance = new LoginServer();
    }
}
