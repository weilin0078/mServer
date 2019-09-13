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
    
    public static class 跟踪 extends Warp
    {
    }
    
    public static class 封号 extends Ban
    {
    }
    
    public static class 隐身 extends Hide
    {
    }
    
    public static class 解除隐身 extends UnHide
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
                c.getPlayer().dropMessage(5, "[Syntax] !" + this.getCommand() + " <玩家> <原因>");
                return 0;
            }
            final int ch = World.Find.findChannel(splitted[1]);
            final StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            final MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (target == null || ch < 1) {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功离线封锁 " + splitted[1] + ".");
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败 " + splitted[1]);
                return 0;
            }
            else {
                if (c.getPlayer().getGMLevel() <= target.getGMLevel()) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 不能封锁GM...");
                    return 1;
                }
                sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, this.hellban)) {
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 成功封锁 " + splitted[1] + ".");
                    FileoutputUtil.logToFile_chr(c.getPlayer(), "Logs/Log_封号.rtf", sb.toString());
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[封号系统]" + target.getName() + " 因为使用非法软件而被永久封号。").getBytes());
                    return 1;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] 封锁失败.");
                return 0;
            }
        }
    }
    
    public static class online1 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "上线的角色 頻道-" + c.getChannel() + ":");
            c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }
    
    public static class CnGM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(5, "<GM聊天视窗>頻道" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return 1;
        }
    }
    
    public static class Hide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理员隐藏 = 开启 \r\n 解除请输入!unhide");
            return 0;
        }
    }
    
    public static class UnHide extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理员隐藏 = 关闭 \r\n 开启请输入!hide");
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
                        c.getPlayer().dropMessage(6, "正在换频道,请等待.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                }
                catch (Exception e) {
                    c.getPlayer().dropMessage(6, "该玩家不在线 " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }
}
