package handling.cashshop;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
import handling.channel.PlayerStorage;
import handling.mina.MapleCodecFactory;
import server.ServerProperties;

public class CashShopServer
{
    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private static final int PORT = 8596;
    private static IoAcceptor acceptor;
    private static PlayerStorage players;
    private static PlayerStorage playersMTS;
    private static boolean finishedShutdown;
    
    public static void run_startup_configurations() {
        CashShopServer.ip = ServerProperties.getProperty("tms.IP") + ":" + 8596;
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator((IoBufferAllocator)new SimpleBufferAllocator());
        CashShopServer.acceptor = (IoAcceptor)new NioSocketAcceptor();
        CashShopServer.acceptor.getFilterChain().addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new MapleCodecFactory()));
        ((SocketSessionConfig)CashShopServer.acceptor.getSessionConfig()).setTcpNoDelay(true);
        CashShopServer.players = new PlayerStorage(-10);
        CashShopServer.playersMTS = new PlayerStorage(-20);
        try {
            CashShopServer.acceptor.setHandler((IoHandler)new MapleServerHandler(-1, true));
            CashShopServer.acceptor.bind((SocketAddress)new InetSocketAddress(8596));
            System.out.println("\u5546\u57ce    1: \u542f\u52a8\u7aef\u53e3 8596");
        }
        catch (Exception e) {
            System.err.println("Binding to port 8596 failed");
            e.printStackTrace();
            throw new RuntimeException("Binding failed.", e);
        }
    }
    
    public static final String getIP() {
        return CashShopServer.ip;
    }
    
    public static final PlayerStorage getPlayerStorage() {
        return CashShopServer.players;
    }
    
    public static final PlayerStorage getPlayerStorageMTS() {
        return CashShopServer.playersMTS;
    }
    
    public static final void shutdown() {
        if (CashShopServer.finishedShutdown) {
            return;
        }
        System.out.println("\u6b63\u5728\u65ad\u5f00\u5546\u57ce\u5185\u73a9\u5bb6...");
        CashShopServer.players.disconnectAll();
        System.out.println("\u6b63\u5728\u5173\u95ed\u5546\u57ce\u4f3a\u670d\u5668...");
        CashShopServer.finishedShutdown = true;
    }
    
    public static boolean isShutdown() {
        return CashShopServer.finishedShutdown;
    }
    
    static {
        CashShopServer.finishedShutdown = false;
    }
}
