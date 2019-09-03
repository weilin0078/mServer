package client.messages.commands;

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.maps.MapleMap;
import tools.ArrayMap;
import tools.Pair;
import tools.StringUtil;

public class GMCommand
{
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.GM;
    }
    
    public static class \u62c9 extends WarpHere
    {
    }
    
    public static class \u7b49\u7ea7 extends Level
    {
    }
    
    public static class \u8f6c\u804c extends Job
    {
    }
    
    public static class \u6e05\u7a7a extends ClearInv
    {
    }
    
    public static class \u8e22\u4eba extends DC
    {
    }
    
    public static class \u8bfb\u53d6\u73a9\u5bb6 extends spy
    {
    }
    
    public static class \u5728\u7ebf\u4eba\u6570 extends online
    {
    }
    
    public static class \u89e3\u9664\u5c01\u53f7 extends UnBan
    {
    }
    
    public static class \u5237\u94b1 extends GainMeso
    {
    }
    
    public static class WarpHere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            }
            else {
                final int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "\u89d2\u8272\u4e0d\u5728\u7ebf");
                    return 1;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                c.getPlayer().dropMessage(5, "\u6b63\u5728\u4f20\u9001\u73a9\u5bb6\u5230\u8eab\u8fb9");
                victim.dropMessage(5, "GM\u6b63\u5728\u4f20\u9001\u4f60");
                if (victim.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    victim.changeMap(mapp, mapp.getPortal(0));
                }
                victim.changeChannel(c.getChannel());
            }
            return 1;
        }
    }
    
    public static class UnBan extends CommandExecute
    {
        protected boolean hellban;
        
        public UnBan() {
            this.hellban = false;
        }
        
        private String getCommand() {
            return "UnBan";
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[Syntax] !" + this.getCommand() + " <\u539f\u56e0>");
                return 0;
            }
            byte ret;
            if (this.hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            }
            else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] SQL error.");
                return 0;
            }
            if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] The character does not exist.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] Successfully unbanned!");
            final byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            }
            else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            }
            else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            }
            else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            }
            else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return (ret_ > 0) ? 1 : 0;
        }
    }
    
    public static class DC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            ChannelServer.forceRemovePlayerByCharName(splitted[1]);
            c.getPlayer().dropMessage("\u89e3\u9664\u5361\u53f7\u5361\u89d2\u6210\u529f");
            return 1;
        }
    }
    
    public static class Job extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().changeJob(Integer.parseInt(splitted[1]));
            return 1;
        }
    }
    
    public static class GainMeso extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return 1;
        }
    }
    
    public static class Level extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }
    
    public static class spy extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "\u4f7f\u7528\u89c4\u5219: !spy <\u73a9\u5bb6\u540d\u5b57>");
            }
            else {
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (victim.getGMLevel() > c.getPlayer().getGMLevel() && c.getPlayer().getId() != victim.getId()) {
                    c.getPlayer().dropMessage(5, "\u4f60\u4e0d\u80fd\u67e5\u770b\u6bd4\u4f60\u9ad8\u6743\u9650\u7684\u4eba!");
                    return 0;
                }
                if (victim != null) {
                    c.getPlayer().dropMessage(5, "\u6b64\u73a9\u5bb6(" + victim.getId() + ")\u72b6\u6001:");
                    c.getPlayer().dropMessage(5, "\u7b49\u7d1a: " + victim.getLevel() + "\u804c\u4e1a: " + victim.getJob() + "\u540d\u58f0: " + victim.getFame());
                    c.getPlayer().dropMessage(5, "\u5730\u56fe: " + victim.getMapId() + " - " + victim.getMap().getMapName().toString());
                    c.getPlayer().dropMessage(5, "\u529b\u91cf: " + victim.getStat().getStr() + "  ||  \u654f\u6377: " + victim.getStat().getDex() + "  ||  \u667a\u529b: " + victim.getStat().getInt() + "  ||  \u8fd0\u6c14: " + victim.getStat().getLuk());
                    c.getPlayer().dropMessage(5, "\u62e5\u6709 " + victim.getMeso() + " \u91d1\u5e01.");
                }
                else {
                    c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230\u6b64\u73a9\u5bb6.");
                }
            }
            return 1;
        }
    }
    
    public static class online extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int total = 0;
            final int curConnected = c.getChannelServer().getConnectedClients();
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, "\u983b\u9053: " + c.getChannelServer().getChannel() + " \u7ebf\u4e0a\u4eba\u6570: " + curConnected);
            total += curConnected;
            for (final MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (chr != null && c.getPlayer().getGMLevel() >= chr.getGMLevel()) {
                    final StringBuilder ret = new StringBuilder();
                    ret.append(" \u89d2\u8272\u540d\u79f0 ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 15));
                    ret.append(" ID: ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 4));
                    ret.append(" \u7b49\u7ea7: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 4));
                    ret.append(" \u804c\u4e1a: ");
                    ret.append(chr.getJob());
                    if (chr.getMap() == null) {
                        continue;
                    }
                    ret.append(" \u5730\u56fe: ");
                    ret.append(chr.getMapId());
                    ret.append("(").append(chr.getMap().getMapName()).append(")");
                    c.getPlayer().dropMessage(6, ret.toString());
                }
            }
            c.getPlayer().dropMessage(6, "\u5f53\u524d\u9891\u9053\u603b\u8ba1\u5728\u7ebf\u4eba\u6570: " + total);
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            final int channelOnline = c.getChannelServer().getConnectedClients();
            int totalOnline = 0;
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }
            c.getPlayer().dropMessage(6, "\u5f53\u524d\u670d\u52a1\u5668\u603b\u8ba1\u5728\u7ebf\u4eba\u6570: " + totalOnline + "\u4e2a");
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            return 1;
        }
    }
    
    public static class ClearInv extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<Pair<Short, Short>, MapleInventoryType>();
            if (splitted[1].equals("\u5168\u90e8")) {
                for (final MapleInventoryType type : MapleInventoryType.values()) {
                    for (final IItem item : c.getPlayer().getInventory(type)) {
                        eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), type);
                    }
                }
            }
            else if (splitted[1].equals("\u5df2\u88c5\u5907\u9053\u5177")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.EQUIPPED);
                }
            }
            else if (splitted[1].equals("\u6b66\u5668")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.EQUIP);
                }
            }
            else if (splitted[1].equals("\u6d88\u8017")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.USE);
                }
            }
            else if (splitted[1].equals("\u88c5\u9970")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.SETUP);
                }
            }
            else if (splitted[1].equals("\u5176\u4ed6")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.ETC);
                }
            }
            else if (splitted[1].equals("\u7279\u6b8a")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    eqs.put(new Pair<Short, Short>(item2.getPosition(), item2.getQuantity()), MapleInventoryType.CASH);
                }
            }
            else {
                c.getPlayer().dropMessage(6, "[\u5168\u90e8/\u5df2\u88c5\u5907\u9053\u5177/\u6b66\u5668/\u6d88\u8017/\u88c5\u9970/\u5176\u4ed6/\u7279\u6b8a]");
            }
            for (final Map.Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return 1;
        }
    }
}
