package handling.login;

import java.util.Map;

import client.MapleClient;
import handling.channel.ChannelServer;
import server.Timer;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

public class LoginWorker
{
    private static long lastUpdate;
    
    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u670d\u52a1\u5668\u6b63\u5728\u7ef4\u62a4\u4e2d"));
            c.getSession().write((Object)LoginPacket.getLoginFailed(7));
            return;
        }
        if (System.currentTimeMillis() - LoginWorker.lastUpdate > 600000L) {
            LoginWorker.lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) {
                LoginWorker.lastUpdate = 0L;
                c.getSession().write((Object)LoginPacket.getLoginFailed(7));
                return;
            }
            final double loads = load.size();
            final double userlimit = LoginServer.getUserLimit();
            final double loadFactor = 1200.0 / (LoginServer.getUserLimit() / (double)load.size());
            for (final Map.Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int)(entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            LoginWorker.lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            if (c.getGender() == 10) {
                c.getSession().write((Object)LoginPacket.getGenderNeeded(c));
            }
            else {
                c.getSession().write((Object)LoginPacket.getAuthSuccessRequest(c));
                c.getSession().write((Object)LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
                c.getSession().write((Object)LoginPacket.getEndOfServerList());
            }
            c.setIdleTask(Timer.PingTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                }
            }, 6000000L));
        }
        else if (c.getGender() == 10) {
            c.getSession().write((Object)LoginPacket.getGenderNeeded(c));
        }
        else {
            c.getSession().write((Object)LoginPacket.getAuthSuccessRequest(c));
            c.getSession().write((Object)LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
            c.getSession().write((Object)LoginPacket.getEndOfServerList());
        }
    }
    
    static {
        LoginWorker.lastUpdate = 0L;
    }
}
