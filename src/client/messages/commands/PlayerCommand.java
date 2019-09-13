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
    
    public static class 存档 extends save
    {
    }
    
    public static class 帮助 extends help
    {
    }
    
    public static class 领取点券 extends gainPoint
    {
    }
    
    public static class 爆率 extends Mobdrop
    {
    }
    
    public static class ea extends 查看
    {
    }
    
    public static class 解卡 extends 查看
    {
    }
    
    public static class 自由 extends ziyou
    {
    }
    
    public static class 重返 extends backBoss
    {
    }
    
    public static class 查看 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "假死已处理完毕.");
            c.getPlayer().dropMessage(6, "当前时间是" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 | 经验值倍率 " + Math.round((float)c.getPlayer().getEXPMod()) * 100 * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, 怪物倍率 " + Math.round((float)c.getPlayer().getDropMod()) * 100 * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, 金币倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100L + "%");
            c.getPlayer().dropMessage(6, "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒");
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
            c.getPlayer().dropMessage("存档成功");
            return 1;
        }
    }
    
    public static class ziyou extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            switch (c.getPlayer().getMapId()) {
                case 925100500: {
                    c.getPlayer().dropMessage(1, "我丢雷楼某，忘咩啊,又想不打boss就通关.");
                    break;
                }
                case 220080001: {
                    c.getPlayer().dropMessage(1, "年轻人,你的思想很危险啊...");
                    break;
                }
                case 240060200: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 280030000: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 270050100: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 910000000: {
                    c.getPlayer().dropMessage(1, "你已经在自由了,还想上天?");
                    break;
                }
                case 922010100: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010200: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010300: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010400: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010500: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010600: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010700: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010800: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010900: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922011000: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922011100: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                case 922010000: {
                    c.getPlayer().dropMessage(1, "当前地图无法使用传送!");
                    break;
                }
                default: {
                    c.getPlayer().changeMap(910000000);
                    c.getPlayer().dropMessage("传送成功");
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
                c.getPlayer().dropMessage(6, "请打字谢谢.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因为你自己是GM无法使用此命令,可以尝试!cngm <讯息> 來建立GM聊天頻道~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) {
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "讯息已经发给GM了!");
            }
            else {
                c.getPlayer().dropMessage(6, "为了防止对GM刷屏所以每1分鐘只能发一次.");
            }
            return 1;
        }
    }
    
    public static class backBoss extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getPlayer().checkBossBack() <= 0) {
                c.getPlayer().dropMessage("查询不到重返记录!");
                return 0;
            }
            final Map<String, Object> backBoss = c.getPlayer().getBackBoss();
            final int ch = World.Find.findChannel(c.getPlayer().getName());
            if (ch == Integer.parseInt(backBoss.get("channel").toString())) {
                final String string = backBoss.get("boss_name").toString();
                switch (string) {
                    case "黑龙": {
                        c.getPlayer().changeMap(240060200);
                        break;
                    }
                    case "扎昆": {
                        c.getPlayer().changeMap(280030000);
                        break;
                    }
                    case "妖僧": {
                        c.getPlayer().changeMap(702060000);
                        break;
                    }
                    case "PB": {
                        c.getPlayer().changeMap(270050100);
                        break;
                    }
                }
                c.getPlayer().dropMessage("重返BOSS成功!");
                return 1;
            }
            c.getPlayer().dropMessage("当前频道没有重返BOSS信息,正在切换" + Integer.parseInt(backBoss.get("channel").toString()) + "频道,切换成功后请在次输入@重返!");
            c.getPlayer().changeChannel(Integer.parseInt(backBoss.get("channel").toString()));
            return 0;
        }
    }
    
    public static class help extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "指令列表 :");
            c.getPlayer().dropMessage(5, "@解卡/@查看/@ea  < 解除异常+查看当前状态 >");
            c.getPlayer().dropMessage(5, "@CGM 讯息        < 传送讯息給GM >");
            c.getPlayer().dropMessage(5, "@爆率            < 查询当前地图怪物爆率 >");
            c.getPlayer().dropMessage(5, "@领取点券        < 充值领取点券 >");
            c.getPlayer().dropMessage(5, "@存档            < 储存当前人物信息 >");
            c.getPlayer().dropMessage(5, "@自由            < 传送到自由市场 >");
            c.getPlayer().dropMessage(5, "@重返            < 打BOSS掉线重返地图 >");
            return 1;
        }
    }
}
