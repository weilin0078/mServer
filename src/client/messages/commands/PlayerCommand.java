package client.messages.commands;

import java.util.Map;

import client.MapleClient;
import constants.ServerConstants;
import handling.world.World;
import scripting.NPCScriptManager;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class PlayerCommand
{
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.NORMAL;
    }
    
    public static class \u5b58\u6863 extends save
    {
    }
    
    public static class \u5e2e\u52a9 extends help
    {
    }
    
    public static class \u9886\u53d6\u70b9\u5238 extends gainPoint
    {
    }
    
    public static class \u7206\u7387 extends Mobdrop
    {
    }
    
    public static class ea extends \u67e5\u770b
    {
    }
    
    public static class \u89e3\u5361 extends \u67e5\u770b
    {
    }
    
    public static class \u81ea\u7531 extends ziyou
    {
    }
    
    public static class \u91cd\u8fd4 extends backBoss
    {
    }
    
    public static class \u67e5\u770b extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "\u5047\u6b7b\u5df2\u5904\u7406\u5b8c\u6bd5.");
            c.getPlayer().dropMessage(6, "\u5f53\u524d\u65f6\u95f4\u662f" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 | \u7ecf\u9a8c\u503c\u500d\u7387 " + Math.round((float)c.getPlayer().getEXPMod()) * 100 * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, \u602a\u7269\u500d\u7387 " + Math.round((float)c.getPlayer().getDropMod()) * 100 * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, \u91d1\u5e01\u500d\u7387 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100L + "%");
            c.getPlayer().dropMessage(6, "\u5f53\u524d\u5ef6\u8fdf " + c.getPlayer().getClient().getLatency() + " \u6beb\u79d2");
            if (c.getPlayer().isAdmin()) {
                c.sendPacket(MaplePacketCreator.sendPyramidEnergy("massacre_hit", String.valueOf(50)));
            }
            return 1;
        }
    }
    
    public static class save extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().dropMessage("\u5b58\u6863\u6210\u529f");
            return 1;
        }
    }
    
    public static class ziyou extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            switch (c.getPlayer().getMapId()) {
                case 925100500: {
                    c.getPlayer().dropMessage(1, "\u6211\u4e22\u96f7\u697c\u67d0\uff0c\u5fd8\u54a9\u554a,\u53c8\u60f3\u4e0d\u6253boss\u5c31\u901a\u5173.");
                    break;
                }
                case 220080001: {
                    c.getPlayer().dropMessage(1, "\u5e74\u8f7b\u4eba,\u4f60\u7684\u601d\u60f3\u5f88\u5371\u9669\u554a...");
                    break;
                }
                case 240060200: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 280030000: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 270050100: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 910000000: {
                    c.getPlayer().dropMessage(1, "\u4f60\u5df2\u7ecf\u5728\u81ea\u7531\u4e86,\u8fd8\u60f3\u4e0a\u5929?");
                    break;
                }
                case 922010100: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010200: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010300: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010400: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010500: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010600: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010700: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010800: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010900: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922011000: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922011100: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                case 922010000: {
                    c.getPlayer().dropMessage(1, "\u5f53\u524d\u5730\u56fe\u65e0\u6cd5\u4f7f\u7528\u4f20\u9001!");
                    break;
                }
                default: {
                    c.getPlayer().changeMap(910000000);
                    c.getPlayer().dropMessage("\u4f20\u9001\u6210\u529f");
                    break;
                }
            }
            return 1;
        }
    }
    
    public static class gainPoint extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9270034);
            return 1;
        }
    }
    
    public static class Mobdrop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            final NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 2000);
            return 1;
        }
    }
    
    public static class CGM extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted[1] == null) {
                c.getPlayer().dropMessage(6, "\u8bf7\u6253\u5b57\u8c22\u8c22.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "\u56e0\u4e3a\u4f60\u81ea\u5df1\u662fGM\u65e0\u6cd5\u4f7f\u7528\u6b64\u547d\u4ee4,\u53ef\u4ee5\u5c1d\u8bd5!cngm <\u8baf\u606f> \u4f86\u5efa\u7acbGM\u804a\u5929\u983b\u9053~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) {
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "\u983b\u9053 " + c.getPlayer().getClient().getChannel() + " \u73a9\u5bb6 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "\u8baf\u606f\u5df2\u7ecf\u53d1\u7ed9GM\u4e86!");
            }
            else {
                c.getPlayer().dropMessage(6, "\u4e3a\u4e86\u9632\u6b62\u5bf9GM\u5237\u5c4f\u6240\u4ee5\u6bcf1\u5206\u9418\u53ea\u80fd\u53d1\u4e00\u6b21.");
            }
            return 1;
        }
    }
    
    public static class backBoss extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getPlayer().checkBossBack() <= 0) {
                c.getPlayer().dropMessage("\u67e5\u8be2\u4e0d\u5230\u91cd\u8fd4\u8bb0\u5f55!");
                return 0;
            }
            final Map<String, Object> backBoss = c.getPlayer().getBackBoss();
            final int ch = World.Find.findChannel(c.getPlayer().getName());
            if (ch == Integer.parseInt(backBoss.get("channel").toString())) {
                final String string = backBoss.get("boss_name").toString();
                switch (string) {
                    case "\u9ed1\u9f99": {
                        c.getPlayer().changeMap(240060200);
                        break;
                    }
                    case "\u624e\u6606": {
                        c.getPlayer().changeMap(280030000);
                        break;
                    }
                    case "\u5996\u50e7": {
                        c.getPlayer().changeMap(702060000);
                        break;
                    }
                    case "PB": {
                        c.getPlayer().changeMap(270050100);
                        break;
                    }
                }
                c.getPlayer().dropMessage("\u91cd\u8fd4BOSS\u6210\u529f!");
                return 1;
            }
            c.getPlayer().dropMessage("\u5f53\u524d\u9891\u9053\u6ca1\u6709\u91cd\u8fd4BOSS\u4fe1\u606f,\u6b63\u5728\u5207\u6362" + Integer.parseInt(backBoss.get("channel").toString()) + "\u9891\u9053,\u5207\u6362\u6210\u529f\u540e\u8bf7\u5728\u6b21\u8f93\u5165@\u91cd\u8fd4!");
            c.getPlayer().changeChannel(Integer.parseInt(backBoss.get("channel").toString()));
            return 0;
        }
    }
    
    public static class help extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "\u6307\u4ee4\u5217\u8868 :");
            c.getPlayer().dropMessage(5, "@\u89e3\u5361/@\u67e5\u770b/@ea  < \u89e3\u9664\u5f02\u5e38+\u67e5\u770b\u5f53\u524d\u72b6\u6001 >");
            c.getPlayer().dropMessage(5, "@CGM \u8baf\u606f        < \u4f20\u9001\u8baf\u606f\u7d66GM >");
            c.getPlayer().dropMessage(5, "@\u7206\u7387            < \u67e5\u8be2\u5f53\u524d\u5730\u56fe\u602a\u7269\u7206\u7387 >");
            c.getPlayer().dropMessage(5, "@\u9886\u53d6\u70b9\u5238        < \u5145\u503c\u9886\u53d6\u70b9\u5238 >");
            c.getPlayer().dropMessage(5, "@\u5b58\u6863            < \u50a8\u5b58\u5f53\u524d\u4eba\u7269\u4fe1\u606f >");
            c.getPlayer().dropMessage(5, "@\u81ea\u7531            < \u4f20\u9001\u5230\u81ea\u7531\u5e02\u573a >");
            c.getPlayer().dropMessage(5, "@\u91cd\u8fd4            < \u6253BOSS\u6389\u7ebf\u91cd\u8fd4\u5730\u56fe >");
            return 1;
        }
    }
}
