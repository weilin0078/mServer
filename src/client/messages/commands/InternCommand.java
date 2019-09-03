package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.MapleMap;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class InternCommand
{
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERN;
    }
    
    public static class \u8ddf\u8e2a extends Warp
    {
    }
    
    public static class \u5c01\u53f7 extends Ban
    {
    }
    
    public static class \u9690\u8eab extends Hide
    {
    }
    
    public static class \u89e3\u9664\u9690\u8eab extends UnHide
    {
    }
    
    public static class Ban extends CommandExecute
    {
        protected boolean hellban;
        
        public Ban() {
            this.hellban = false;
        }
        
        private String getCommand() {
            return "Ban";
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "[Syntax] !" + this.getCommand() + " <\u73a9\u5bb6> <\u539f\u56e0>");
                return 0;
            }
            final int ch = World.Find.findChannel(splitted[1]);
            final StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            final MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (target == null || ch < 1) {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u6210\u529f\u79bb\u7ebf\u5c01\u9501 " + splitted[1] + ".");
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u5c01\u9501\u5931\u8d25 " + splitted[1]);
                return 0;
            }
            else {
                if (c.getPlayer().getGMLevel() <= target.getGMLevel()) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u4e0d\u80fd\u5c01\u9501GM...");
                    return 1;
                }
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, this.hellban)) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u6210\u529f\u5c01\u9501 " + splitted[1] + ".");
                    FileoutputUtil.logToFile_chr(c.getPlayer(), "Logs/Log_\u5c01\u53f7.rtf", sb.toString());
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[\u5c01\u53f7\u7cfb\u7edf]" + target.getName() + " \u56e0\u4e3a\u4f7f\u7528\u975e\u6cd5\u8f6f\u4ef6\u800c\u88ab\u6c38\u4e45\u5c01\u53f7\u3002").getBytes());
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u5c01\u9501\u5931\u8d25.");
                return 0;
            }
        }
    }
    
    public static class online1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "\u4e0a\u7ebf\u7684\u89d2\u8272 \u983b\u9053-" + c.getChannel() + ":");
            c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }
    
    public static class CnGM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(5, "<GM\u804a\u5929\u89c6\u7a97>\u983b\u9053" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return 1;
        }
    }
    
    public static class Hide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "\u7ba1\u7406\u5458\u9690\u85cf = \u5f00\u542f \r\n \u89e3\u9664\u8bf7\u8f93\u5165!unhide");
            return 0;
        }
    }
    
    public static class UnHide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "\u7ba1\u7406\u5458\u9690\u85cf = \u5173\u95ed \r\n \u5f00\u542f\u8bf7\u8f93\u5165!hide");
            return 1;
        }
    }
    
    public static class Warp extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                }
                else {
                    final MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            }
            else {
                try {
                    victim = c.getPlayer();
                    final int ch = World.Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        final MapleMap target2 = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        c.getPlayer().changeMap(target2, target2.getPortal(0));
                    }
                    else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "\u6b63\u5728\u6362\u9891\u9053,\u8bf7\u7b49\u5f85.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                }
                catch (Exception e) {
                    c.getPlayer().dropMessage(6, "\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }
}
