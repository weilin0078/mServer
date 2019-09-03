package server;

import java.util.Set;

import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import tools.MaplePacketCreator;

public class ShutdownServer implements Runnable
{
    private static final ShutdownServer instance;
    public static boolean running;
    public int mode;
    
    public ShutdownServer() {
        this.mode = 0;
    }
    
    public static ShutdownServer getInstance() {
        return ShutdownServer.instance;
    }
    
    public void shutdown() {
        this.run();
    }
    
    @Override
    public void run() {
        Timer.WorldTimer.getInstance().stop();
        Timer.MapTimer.getInstance().stop();
        Timer.BuffTimer.getInstance().stop();
        Timer.CloneTimer.getInstance().stop();
        Timer.EventTimer.getInstance().stop();
        Timer.EtcTimer.getInstance().stop();
        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.closeAllMerchant();
        }
        try {
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
        }
        catch (Exception ex) {}
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, " \u6e38\u620f\u670d\u52a1\u5668\u5c06\u5173\u95ed\u7ef4\u62a4\uff0c\u8bf7\u73a9\u5bb6\u5b89\u5168\u4e0b\u7ebf..."));
        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
            try {
                cs.setServerMessage("\u6e38\u620f\u670d\u52a1\u5668\u5c06\u5173\u95ed\u7ef4\u62a4\uff0c\u8bf7\u73a9\u5bb6\u5b89\u5168\u4e0b\u7ebf...");
            }
            catch (Exception ex2) {}
        }
        final Set<Integer> channels = ChannelServer.getAllInstance();
        for (final Integer channel : channels) {
            try {
                final ChannelServer cs2 = ChannelServer.getInstance(channel);
                cs2.saveAll();
                cs2.setFinishShutdown();
                cs2.shutdown();
            }
            catch (Exception e2) {
                System.out.println("\u9891\u9053" + String.valueOf(channel) + " \u5173\u95ed\u9519\u8bef.");
            }
        }
        System.out.println("\u670d\u52a1\u7aef\u5173\u95ed\u4e8b\u4ef6 1 \u5df2\u5b8c\u6210.");
        System.out.println("\u670d\u52a1\u7aef\u5173\u95ed\u4e8b\u4ef6 2 \u5f00\u59cb...");
        try {
            LoginServer.shutdown();
            System.out.println("\u767b\u5f55\u4f3a\u670d\u5668\u5173\u95ed\u5b8c\u6210...");
        }
        catch (Exception ex3) {}
        try {
            CashShopServer.shutdown();
            System.out.println("\u5546\u57ce\u4f3a\u670d\u5668\u5173\u95ed\u5b8c\u6210...");
        }
        catch (Exception ex4) {}
        try {
            DatabaseConnection.closeAll();
        }
        catch (Exception ex5) {}
        System.out.println("\u670d\u52a1\u7aef\u5173\u95ed\u4e8b\u4ef6 2 \u5df2\u5b8c\u6210.");
        try {
            Thread.sleep(1000L);
        }
        catch (Exception e) {
            System.out.println("\u5173\u95ed\u670d\u52a1\u7aef\u9519\u8bef - 2" + e);
        }
        System.exit(0);
    }
    
    static {
        instance = new ShutdownServer();
        ShutdownServer.running = false;
    }
}
