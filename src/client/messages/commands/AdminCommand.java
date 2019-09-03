package client.messages.commands;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.mina.core.session.IoSession;

import com.mysql.jdbc.PreparedStatement;

import client.ISkill;
import client.LoginCrypto;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.login.handler.AutoRegister;
import handling.world.CheaterData;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import scripting.EventManager;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.CashItemFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.ShutdownServer;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MobSkillFactory;
import server.life.OverrideMonsterStats;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.CPUSampler;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MaplePacketCreator;
import tools.MockIOSession;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.MobPacket;

public class AdminCommand
{
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.ADMIN;
    }
    
    public static class \u5f00\u653e\u5730\u56fe extends openmap
    {
    }
    
    public static class \u5173\u95ed\u5730\u56fe extends closemap
    {
    }
    
    public static class \u6ce8\u518c extends register
    {
    }
    
    public static class \u6ee1\u5c5e\u6027 extends maxstats
    {
    }
    
    public static class \u6ee1\u6280\u80fd extends maxSkills
    {
    }
    
    public static class \u62c9\u5168\u90e8 extends WarpAllHere
    {
    }
    
    public static class \u7ed9\u91d1\u5e01 extends mesoEveryone
    {
    }
    
    public static class \u7ed9\u7ecf\u9a8c extends ExpEveryone
    {
    }
    
    public static class \u7ed9\u6240\u6709\u4eba\u70b9\u5377 extends CashEveryone
    {
    }
    
    public static class \u7ed9\u70b9\u5377 extends GainCash
    {
    }
    
    public static class \u5237\u65b0\u5730\u56fe extends ReloadMap
    {
    }
    
    public static class \u795d\u798f extends buff
    {
    }
    
    public static class \u500d\u7387\u8bbe\u7f6e extends setRate
    {
    }
    
    public static class \u5730\u56fe\u4ee3\u7801 extends WhereAmI
    {
    }
    
    public static class \u5237 extends Item
    {
    }
    
    public static class \u4e22 extends Drop
    {
    }
    
    public static class \u5168\u90e8\u590d\u6d3b extends HealMap
    {
    }
    
    public static class \u6e05\u602a extends KillAll
    {
    }
    
    public static class \u8bbe\u7f6e\u4eba\u6c14 extends Fame
    {
    }
    
    public static class \u5438\u602a extends MobVac
    {
    }
    
    public static class \u6e05\u9664\u5730\u677f extends cleardrops
    {
    }
    
    public static class \u53ec\u5524\u602a\u7269 extends Spawn
    {
    }
    
    public static class \u8ba1\u65f6\u5668 extends Clock
    {
    }
    
    public static class \u81ea\u52a8\u6ce8\u518c extends autoreg
    {
    }
    
    public static class \u602a\u7269\u4ee3\u7801 extends mob
    {
    }
    
    public static class \u4eba\u6570\u4e0a\u9650 extends setUserLimit
    {
    }
    
    public static class \u5c01\u53f7\u72b6\u6001 extends BanStatus
    {
    }
    
    public static class \u53d1\u9001\u516c\u544a extends sendNoticeGG
    {
    }
    
    public static class Debug extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setDebugMessage(!c.getPlayer().getDebugMessage());
            return 1;
        }
    }
    
    public static class sendNoticeGG extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage("\u4f7f\u7528\u65b9\u6cd5: !\u53d1\u9001\u516c\u544a [\u516c\u544a\u5185\u5bb9] - \u7ed9\u6240\u6709\u4eba\u53d1\u9001\u516c\u544a");
                return 0;
            }
            final String msg = splitted[1];
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect(msg, 5121009);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u53d1\u9001\u516c\u544a [\u516c\u544a\u5185\u5bb9] - \u7ed9\u6240\u6709\u4eba\u53d1\u9001\u516c\u544a").toString();
        }
    }
    
    public static class BanStatus extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            String mac = "";
            String ip = "";
            int acid = 0;
            boolean Systemban = false;
            boolean ACbanned = false;
            boolean IPbanned = false;
            boolean MACbanned = false;
            String reason = null;
            try {
                final Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = (PreparedStatement)con.prepareStatement("select accountid from characters where name = ?");
                ps.setString(1, name);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        acid = rs.getInt("accountid");
                    }
                }
                ps = (PreparedStatement)con.prepareStatement("select banned, banreason, macs, Sessionip from accounts where id = ?");
                ps.setInt(1, acid);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Systemban = (rs.getInt("banned") == 2);
                        ACbanned = (rs.getInt("banned") == 1 || rs.getInt("banned") == 2);
                        reason = rs.getString("banreason");
                        mac = rs.getString("macs");
                        ip = rs.getString("Sessionip");
                    }
                }
                ps.close();
            }
            catch (Exception ex) {}
            if (reason == null || reason == "") {
                reason = "?";
            }
            if (c.isBannedIP(ip)) {
                IPbanned = true;
            }
            if (c.isBannedMac(mac)) {
                MACbanned = true;
            }
            c.getPlayer().dropMessage("\u73a9\u5bb6[" + name + "] \u5e10\u53f7ID[" + acid + "]\u662f\u5426\u88ab\u5c01\u9501: " + (ACbanned ? "\u662f" : "\u5426") + (Systemban ? "(\u7cfb\u7edf\u81ea\u52a8\u5c01\u9501)" : "") + ", \u539f\u56e0: " + reason);
            c.getPlayer().dropMessage("IP: " + ip + " \u662f\u5426\u5728\u5c01\u9501IP\u540d\u5355: " + (IPbanned ? "\u662f" : "\u5426"));
            c.getPlayer().dropMessage("MAC: " + mac + " \u662f\u5426\u5728\u5c01\u9501MAC\u540d\u5355: " + (MACbanned ? "\u662f" : "\u5426"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!BanStatus <\ue010\u7522\ue6bd\u563f> - \u7429\ue0f8\ue010\u7522\u740c\ue725\u7806\ue087\u739b\u306e\ue165\ue6c3").toString();
        }
    }
    
    public static class setUserLimit extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int UserLimit = LoginServer.getUserLimit();
            try {
                UserLimit = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            LoginServer.setUserLimit(UserLimit);
            c.getPlayer().dropMessage("\u670d\u52a1\u5668\u4eba\u6570\u4e0a\u9650\u5df2\u66f4\u6539\u4e3a" + UserLimit);
            return 1;
        }
    }
    
    public static class SavePlayerShops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            c.getPlayer().dropMessage(6, "\u96c7\u4f63\u5546\u4eba\u50a8\u5b58\u5b8c\u6bd5.");
            return 1;
        }
    }
    
    public static class Shutdown extends CommandExecute
    {
        private static Thread t;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, "\u5173\u95ed\u670d\u52a1\u5668...");
            if (Shutdown.t == null || !Shutdown.t.isAlive()) {
                (Shutdown.t = new Thread(ShutdownServer.getInstance())).start();
            }
            else {
                c.getPlayer().dropMessage(6, "\u5df2\u5728\u6267\u884c\u4e2d...");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdown - \u5173\u95ed\u670d\u52a1\u5668").toString();
        }
        
        static {
            Shutdown.t = null;
        }
    }
    
    public static class ShutdownTime extends CommandExecute
    {
        private static ScheduledFuture<?> ts;
        private int minutesLeft;
        private static Thread t;
        
        public ShutdownTime() {
            this.minutesLeft = 0;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            this.minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "\u670d\u52a1\u5668\u5c06\u5728 " + this.minutesLeft + "\u5206\u949f\u540e\u5173\u95ed. \u8bf7\u5c3d\u901f\u5173\u95ed\u7cbe\u7075\u5546\u4eba \u5e76\u4e0b\u7ebf.");
            if (ShutdownTime.ts == null && (ShutdownTime.t == null || !ShutdownTime.t.isAlive())) {
                ShutdownTime.t = new Thread(ShutdownServer.getInstance());
                ShutdownTime.ts = Timer.EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (ShutdownTime.this.minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            ShutdownTime.t.start();
                            ShutdownTime.ts.cancel(false);
                            return;
                        }
                        final StringBuilder message = new StringBuilder();
                        message.append("[\u5192\u9669\u5c9b\u516c\u544a] \u670d\u52a1\u5668\u5c06\u5728 ");
                        message.append(ShutdownTime.this.minutesLeft);
                        message.append("\u5206\u949f\u540e\u5173\u95ed. \u8bf7\u5c3d\u901f\u5173\u95ed\u7cbe\u7075\u5546\u4eba \u5e76\u4e0b\u7ebf.");
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, message.toString()).getBytes());
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()).getBytes());
                        for (final ChannelServer cs : ChannelServer.getAllInstances()) {
                            cs.setServerMessage("\u670d\u52a1\u5668\u5c06\u4e8e " + ShutdownTime.this.minutesLeft + " \u5206\u949f\u540e\u5f00\u542f");
                        }
                        ShutdownTime.this.minutesLeft--;
                    }
                }, 60000L);
            }
            else {
                c.getPlayer().dropMessage(6, "\u670d\u52a1\u5668\u5173\u95ed\u65f6\u95f4\u4fee\u6539\u4e3a " + this.minutesLeft + "\u5206\u949f\u540e\uff0c\u8bf7\u7a0d\u7b49\u670d\u52a1\u5668\u5173\u95ed");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <\u79d2\u6570> - \u5173\u95ed\u670d\u52a1\u5668").toString();
        }
        
        static {
            ShutdownTime.ts = null;
            ShutdownTime.t = null;
        }
    }
    
    public static class SaveAll extends CommandExecute
    {
        private int p;
        
        public SaveAll() {
            this.p = 0;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                final List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (final MapleCharacter chr : chrs) {
                    ++this.p;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[\u4fdd\u5b58] " + this.p + "\u4e2a\u73a9\u5bb6\u6570\u636e\u4fdd\u5b58\u5230\u6570\u636e\u4e2d.");
            this.p = 0;
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!saveall - \u4fdd\u5b58\u6240\u6709\u89d2\u8272\u8cc7\u6599").toString();
        }
    }
    
    public static class LowHP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getStat().setHp(1);
            c.getPlayer().getStat().setMp(1);
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lowhp - \u8840\u9b54\u5f52\u3127").toString();
        }
    }
    
    public static class Heal extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().dispelDebuffs();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!heal - \u8865\u6ee1\u8840\u9b54").toString();
        }
    }
    
    public static class UnbanIP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL \u9519\u8bef.");
            }
            else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] \u89d2\u8272\u4e0d\u5b58\u5728.");
            }
            else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            }
            else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP\u6216Mac\u5df2\u89e3\u9501\u5176\u4e2d\u4e00\u500b.");
            }
            else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP\u4ee5\u53caMac\u5df2\u6210\u529f\u89e3\u9501.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!unbanip <\u73a9\u5bb6\u540d\u79f0> - \u89e3\u9501\u73a9\u5bb6").toString();
        }
    }
    
    public static class TempBan extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);
            final Calendar cal = Calendar.getInstance();
            cal.add(5, numDay);
            final DateFormat df = DateFormat.getInstance();
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] \u627e\u4e0d\u5230\u76ee\u6807\u89d2\u8272");
            }
            else {
                victim.tempban("\u7531" + c.getPlayer().getName() + "\u6682\u65f6\u9501\u5b9a\u4e86", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " \u5df2\u6210\u529f\u88ab\u6682\u65f6\u9501\u5b9a\u81f3 " + df.format(cal.getTime()));
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!tempban <\u73a9\u5bb6\u540d\u79f0> - \u6682\u65f6\u9501\u5b9a\u73a9\u5bb6").toString();
        }
    }
    
    public static class Kill extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return 0;
            }
            for (int i = 1; i < splitted.length; ++i) {
                final String name = splitted[1];
                final int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    return 0;
                }
                final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.getPlayer().dropMessage(6, "[kill] \u73a9\u5bb6 " + splitted[i] + " \u4e0d\u5b58\u5728.");
                }
                else if (player.allowedToTarget(victim)) {
                    victim.getStat().setHp(0);
                    victim.getStat().setMp(0);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!kill <\u73a9\u5bb6\u540d\u79f01> <\u73a9\u5bb6\u540d\u79f02> ...  - \u6740\u6389\u73a9\u5bb6").toString();
        }
    }
    
    public static class Skill extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            final byte masterlevel = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!skill <\u6280\u80fdID> [\u6280\u80fd\u7b49\u7d1a] [\u6280\u80fd\u6700\u5927\u7b49\u7d1a] ...  - \u5b66\u4e60\u6280\u80fd").toString();
        }
    }
    
    public static class Fame extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage("!fame <\u89d2\u8272\u540d\u79f0> <\u540d\u58f0> ...  - \u540d\u58f0");
                return 0;
            }
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            short fame;
            try {
                fame = Short.parseShort(splitted[2]);
            }
            catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "\u4e0d\u5408\u6cd5\u7684\u6570\u5b57");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            }
            else {
                c.getPlayer().dropMessage(6, "[fame] \u89d2\u8272\u4e0d\u5b58\u5728");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fame <\u89d2\u8272\u540d\u79f0> <\u540d\u58f0> ...  - \u540d\u58f0").toString();
        }
    }
    
    public static class autoreg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage("\u76ee\u524d\u81ea\u52a8\u6ce8\u518c\u5df2\u7ecf " + ServerConstants.ChangeAutoReg());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!autoreg  - \u81ea\u52a8\u6ce8\u518c\u5f00\u5173").toString();
        }
    }
    
    public static class HealMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            for (final MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp());
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp());
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                    mch.dispelDebuffs();
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!healmap  - \u6cbb\u6108\u5730\u56fe\u4e0a\u6240\u6709\u7684\u4eba").toString();
        }
    }
    
    public static class GodMode extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "\u65e0\u654c\u5df2\u7ecf\u5173\u95ed");
            }
            else {
                player.setInvincible(true);
                player.dropMessage(6, "\u65e0\u654c\u5df2\u7ecf\u5f00\u542f.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!godmode  - \u65e0\u654c\u5f00\u5173").toString();
        }
    }
    
    public static class GiveSkill extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            final byte masterlevel = (byte)CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!giveskill <\u73a9\u5bb6\u540d\u79f0> <\u6280\u80fdID> [\u6280\u80fd\u7b49\u7d1a] [\u6280\u80fd\u6700\u5927\u7b49\u7d1a] - \u7ed9\u4e88\u6280\u80fd").toString();
        }
    }
    
    public static class SP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.sendPacket(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!sp [\u6570\u91cf] - \u589e\u52a0SP").toString();
        }
    }
    
    public static class AP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().setRemainingAp((short)CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!ap [\u6570\u91cf] - \u589e\u52a0AP").toString();
        }
    }
    
    public static class Shop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId;
            try {
                shopId = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                return 0;
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            else {
                c.getPlayer().dropMessage(5, "\u6b64\u5546\u5e97ID\u4e0d\u5b58\u5728");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!shop - \u5f00\u542f\u5546\u5e97").toString();
        }
    }
    
    public static class \u5173\u952e\u65f6\u523b extends CommandExecute
    {
        protected static ScheduledFuture<?> ts;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 1) {
                return 0;
            }
            if (\u5173\u952e\u65f6\u523b.ts != null) {
                \u5173\u952e\u65f6\u523b.ts.cancel(false);
                c.getPlayer().dropMessage(0, "\u539f\u5b9a\u7684\u5173\u952e\u65f6\u523b\u5df2\u53d6\u6d88");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                return 0;
            }
            if (minutesLeft > 0) {
                \u5173\u952e\u65f6\u523b.ts = Timer.EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                                if (!c.getPlayer().isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010);
                                }
                            }
                        }
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "\u5173\u952e\u65f6\u523b\u5df2\u7ecf\u5f00\u59cb\u4e86!!!").getBytes());
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage("\u5173\u952e\u65f6\u523b\u5df2\u7ecf\u5f00\u59cb\u4e86!!!").getBytes());
                        \u5173\u952e\u65f6\u523b.ts.cancel(false);
                        \u5173\u952e\u65f6\u523b.ts = null;
                    }
                }, minutesLeft * 60 * 1000);
                c.getPlayer().dropMessage(0, "\u5173\u952e\u65f6\u523b\u9884\u5b9a\u5df2\u5b8c\u6210");
            }
            else {
                c.getPlayer().dropMessage(0, "\u8bbe\u5b9a\u7684\u65f6\u95f4\u5fc5\u987b > 0\u3002");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u5173\u952e\u65f6\u523b <\u65f6\u95f4:\u5206\u949f> - \u5173\u952e\u65f6\u523b").toString();
        }
        
        static {
            \u5173\u952e\u65f6\u523b.ts = null;
        }
    }
    
    public static class GainCash extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            MapleCharacter player = c.getPlayer();
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            }
            catch (Exception ex) {
                c.getPlayer().dropMessage("\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf");
                return 1;
            }
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf");
                return 1;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf");
                return 1;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("\u5df2\u7ecf\u6536\u5230\u70b9\u5377" + amount + "\u70b9");
            final String msg = "[GM \u5bc6\u8bed] GM " + c.getPlayer().getName() + " \u7d66\u4e86 " + player.getName() + " \u70b9\u5377 " + amount + "\u70b9";
            FileoutputUtil.logToFile("logs/Data/\u7ed9\u4e88\u70b9\u5377.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " \u7ed9\u4e86 " + player.getName() + " \u70b9\u5377 " + amount + "\u70b9");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gaingash <\u6578\u91cf> <\u73a9\u5bb6> - \u53d6\u5f97Gash\u70b9\u6570").toString();
        }
    }
    
    public static class GainMaplePoint extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int amount = Integer.parseInt(splitted[1]);
            final String name = splitted[2];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                return 0;
            }
            player.modifyCSPoints(2, amount, true);
            final String msg = "[GM \u5bc6\u8bed] GM " + c.getPlayer().getName() + " \u7ed9\u4e86 " + player.getName() + " \u67ab\u53f6\u70b9\u6570 " + amount + "\u70b9";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <\u6578\u91cf> <\u73a9\u5bb6> - \u53d6\u5f97\u67ab\u53f6\u70b9\u6570").toString();
        }
    }
    
    public static class GainPoint extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int amount = Integer.parseInt(splitted[1]);
            final String name = splitted[2];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return 0;
            }
            final MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                return 0;
            }
            player.setPoints(player.getPoints() + amount);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainpoint <\u6578\u91cf> <\u73a9\u5bb6> - \u53d6\u5f97Point").toString();
        }
    }
    
    public static class GainVP extends GainPoint
    {
    }
    
    public static class LevelUp extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().levelUp();
            }
            else {
                int up = 0;
                try {
                    up = Integer.parseInt(splitted[1]);
                }
                catch (Exception ex) {}
                for (int i = 0; i < up; ++i) {
                    c.getPlayer().levelUp();
                }
            }
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!levelup - \u7b49\u7d1a\u4e0a\u5347").toString();
        }
    }
    
    public static class UnlockInv extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final java.util.Map<IItem, MapleInventoryType> eqs = new ArrayMap<IItem, MapleInventoryType>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("\u5168\u90e8")) {
                for (final MapleInventoryType type : MapleInventoryType.values()) {
                    for (final IItem item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte)(item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte)(item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            }
            else if (splitted[1].equals("\u5df2\u88c5\u5907\u9053\u5177")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7d93\u89e3\u9396");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            }
            else if (splitted[1].equals("\u6b66\u5668")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            }
            else if (splitted[1].equals("\u6d88\u8017")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.USE);
                    }
                    add = false;
                }
            }
            else if (splitted[1].equals("\u88c5\u9970")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            }
            else if (splitted[1].equals("\u5176\u4ed6")) {
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            }
            else {
                if (!splitted[1].equals("\u7279\u6b8a")) {
                    return 0;
                }
                for (final IItem item2 : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (ItemFlag.UNTRADEABLE.check(item2.getFlag())) {
                        item2.setFlag((byte)(item2.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u89e3\u9501");
                    }
                    if (add) {
                        eqs.put(item2, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            }
            for (final java.util.Map.Entry<IItem, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!unlockinv <\u5168\u90e8/\u5df2\u88c5\u5907\u9053\u5177/\u6b66\u5668/\u6d88\u8017/\u88c5\u9970/\u5176\u4ed6/\u7279\u6b8a> - \u89e3\u9501\u9053\u5177").toString();
        }
    }
    
    public static class Item extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final short quantity = (short)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                final MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(c, itemId, (short)1, c.getPlayer().getName(), pet, 90L, (byte)0);
                }
            }
            else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - \u7269\u54c1\u4e0d\u5b58\u5728");
            }
            else {
                byte flag = 0;
                flag |= (byte)ItemFlag.LOCK.getValue();
                IItem item;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip)ii.getEquipById(itemId));
                }
                else {
                    item = new client.inventory.Item(itemId, (short)0, quantity, (byte)0);
                    if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {}
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());
                MapleInventoryManipulator.addbyItem(c, item);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!item <\u9053\u5177ID> - \u53d6\u5f97\u9053\u5177").toString();
        }
    }
    
    public static class serverMsg extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                for (final ChannelServer ch : ChannelServer.getAllInstances()) {
                    ch.setServerMessage(sb.toString());
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()).getBytes());
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!servermsg \u8baf\u606f - \u66f4\u6539\u4e0a\u65b9\u9ec3\u8272\u516c\u544a").toString();
        }
    }
    
    public static class Say extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!say \u8baf\u606f - \u670d\u52a1\u5668\u516c\u544a").toString();
        }
    }
    
    public static class Letter extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "\u6307\u4ee4\u89c4\u5219: ");
                return 0;
            }
            int start;
            int nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            }
            else {
                if (!splitted[1].equalsIgnoreCase("red")) {
                    c.getPlayer().dropMessage(6, "\u672a\u77e5\u7684\u984f\u8272!");
                    return 1;
                }
                start = 3991000;
                nstart = 3990009;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            final List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            for (int i = 0; i < splitString.length(); ++i) {
                final char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                }
                else if (chr >= 'A' && chr <= 'Z') {
                    chars.add((int)chr);
                }
                else if (chr >= '0' && chr <= '9') {
                    chars.add(chr + '\u00c8');
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - splitString.length() / 2 * 32;
            for (final Integer j : chars) {
                if (j == -1) {
                    dStart += 32;
                }
                else if (j < 200) {
                    final int val = start + j - 65;
                    final client.inventory.Item item = new client.inventory.Item(val, (byte)0, (short)1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += 32;
                }
                else {
                    if (j < 200 || j > 300) {
                        continue;
                    }
                    final int val = nstart + j - 48 - 200;
                    final client.inventory.Item item = new client.inventory.Item(val, (byte)0, (short)1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += 32;
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - \u9001\u4fe1").toString();
        }
    }
    
    public static class Marry extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "\u9519\u8bef\u7684\u6212\u6307ID.");
            }
            else {
                final String name = splitted[1];
                final int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                    return 0;
                }
                final MapleCharacter fff = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                }
                else {
                    final int[] ringID = { MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance() };
                    try {
                        final MapleCharacter[] chrz = { fff, c.getPlayer() };
                        for (int i = 0; i < chrz.length; ++i) {
                            final Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "\u9519\u8bef\u7684\u6212\u6307ID.");
                                return 1;
                            }
                            eq.setUniqueId(ringID[i]);
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].dropMessage(6, "\u6210\u529f\u4e0e  " + chrz[i].getName() + " \u7ed3\u5a5a");
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    }
                    catch (SQLException ex) {}
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!marry <\u73a9\u5bb6\u540d\u79f0> <\u6212\u6307\u4ee3\u7801> - \u7ed3\u5a5a").toString();
        }
    }
    
    public static class ItemCheck extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3 || splitted[1] == null || splitted[1].equals("") || splitted[2] == null || splitted[2].equals("")) {
                return 0;
            }
            final int item = Integer.parseInt(splitted[2]);
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int itemamount = chr.getItemQuantity(item, true);
            if (itemamount > 0) {
                c.getPlayer().dropMessage(6, chr.getName() + " \u6709 " + itemamount + " (" + item + ").");
            }
            else {
                c.getPlayer().dropMessage(6, chr.getName() + " \u5e76\u6c92\u6709 (" + item + ")");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - \u68c0\u67e5\u7269\u54c1").toString();
        }
    }
    
    public static class MobVac extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                final MapleMonster monster = (MapleMonster)mmo;
                c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, -1, 0, 0, 0, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getPosition(), c.getPlayer().getLastRes()));
                monster.setPosition(c.getPlayer().getPosition());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mobvac - \u5168\u56fe\u5438\u602a").toString();
        }
    }
    
    public static class Song extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(splitted[1]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!song - \u64ad\u653e\u97f3\u4e50").toString();
        }
    }
    
    public static class \u5f00\u542f\u81ea\u52a8\u6d3b\u52a8 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u5f00\u542f\u81ea\u52a8\u6d3b\u52a8 - \u5f00\u542f\u81ea\u52a8\u6d3b\u52a8").toString();
        }
    }
    
    public static class \u6d3b\u52a8\u5f00\u59cb extends CommandExecute
    {
        private static ScheduledFuture<?> ts;
        private int min;
        
        public \u6d3b\u52a8\u5f00\u59cb() {
            this.min = 1;
        }
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u5173\u95ed\u6d3b\u52a8\u5165\u53e3\uff0c\u53ef\u4ee5\u4f7f\u7528 !\u6d3b\u52a8\u5f00\u59cb \u4f86\u542f\u52a8\u3002");
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "\u983b\u9053:" + c.getChannel() + "\u6d3b\u52a8\u76ee\u524d\u5df2\u7ecf\u5173\u95ed\u5927\u95e8\u53e3\u3002").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                \u6d3b\u52a8\u5f00\u59cb.ts = Timer.EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (\u6d3b\u52a8\u5f00\u59cb.this.min == 0) {
                            MapleEvent.onStartEvent(c.getPlayer());
                            \u6d3b\u52a8\u5f00\u59cb.ts.cancel(false);
                            return;
                        }
                        \u6d3b\u52a8\u5f00\u59cb.this.min--;
                    }
                }, 60000L);
                return 1;
            }
            c.getPlayer().dropMessage(5, "\u60a8\u5fc5\u987b\u5148\u4f7f\u7528 !\u9009\u62e9\u6d3b\u52a8 \u8bbe\u5b9a\u7576\u524d\u983b\u9053\u7684\u6d3b\u52a8\uff0c\u5e76\u5728\u5f53\u524d\u983b\u9053\u6d3b\u52a8\u5730\u56fe\u91cc\u4f7f\u7528\u3002");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u6d3b\u52a8\u5f00\u59cb - \u6d3b\u52a8\u5f00\u59cb").toString();
        }
        
        static {
            \u6d3b\u52a8\u5f00\u59cb.ts = null;
        }
    }
    
    public static class \u5173\u95ed\u6d3b\u52a8\u5165\u53e3 extends CommandExecute
    {
        private static boolean tt;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "\u5df2\u7ecf\u5173\u95ed\u6d3b\u52a8\u5165\u53e3\uff0c\u53ef\u4ee5\u4f7f\u7528 !\u6d3b\u52a8\u5f00\u59cb \u4f86\u542f\u52a8\u3002");
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "\u983b\u9053:" + c.getChannel() + "\u6d3b\u52a8\u76ee\u524d\u5df2\u7ecf\u5173\u95ed\u5927\u95e8\u53e3\u3002").getBytes());
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(60));
                Timer.EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        \u5173\u95ed\u6d3b\u52a8\u5165\u53e3.tt = true;
                    }
                }, 60000L);
                if (\u5173\u95ed\u6d3b\u52a8\u5165\u53e3.tt) {
                    MapleEvent.onStartEvent(c.getPlayer());
                }
                return 1;
            }
            c.getPlayer().dropMessage(5, "\u60a8\u5fc5\u987b\u5148\u4f7f\u7528 !\u9009\u62e9\u6d3b\u52a8 \u8bbe\u5b9a\u5f53\u524d\u983b\u9053\u7684\u6d3b\u52a8\uff0c\u5e76\u5728\u5f53\u524d\u983b\u9053\u6d3b\u52a8\u5730\u56fe\u91cc\u4f7f\u7528\u3002");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u5173\u95ed\u6d3b\u52a8\u5165\u53e3 -\u5173\u95ed\u6d3b\u52a8\u5165\u53e3").toString();
        }
        
        static {
            \u5173\u95ed\u6d3b\u52a8\u5165\u53e3.tt = false;
        }
    }
    
    public static class \u9009\u62e9\u6d3b\u52a8 extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("\u76ee\u524d\u5f00\u653e\u7684\u6d3b\u52a8\u6709: ");
                for (final MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u9009\u62e9\u6d3b\u52a8 - \u9009\u62e9\u6d3b\u52a8").toString();
        }
    }
    
    public static class CheckGash extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter chrs = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230\u8be5\u89d2\u8272");
            }
            else {
                c.getPlayer().dropMessage(6, chrs.getName() + " \u6709 " + chrs.getCSPoints(1) + " \u70b9\u6570.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!checkgash <\u73a9\u5bb6\u540d\u79f0> - \u68c0\u67e5\u70b9\u6570").toString();
        }
    }
    
    public static class RemoveItem extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            final String name = splitted[1];
            final int id = Integer.parseInt(splitted[2]);
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "\u6b64\u73a9\u5bb6\u5e76\u4e0d\u5b58\u5728");
            }
            else {
                chr.removeAll(id);
                c.getPlayer().dropMessage(6, "\u6240\u6709ID\u4e3a " + id + " \u7684\u9053\u5177\u5df2\u7ecf\u4ece " + name + " \u8eab\u4e0a\u88ab\u79fb\u9664\u4e86");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <\u89d2\u8272\u540d\u79f0> <\u7269\u54c1ID> - \u79fb\u9664\u73a9\u5bb6\u8eab\u4e0a\u7684\u9053\u5177").toString();
        }
    }
    
    public static class KillMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp(0);
                    map.getStat().setMp(0);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmap - \u6740\u6389\u6240\u6709\u73a9\u5bb6").toString();
        }
    }
    
    public static class SpeakMega extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleCharacter victim = null;
            if (splitted.length >= 2) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            }
            try {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, (victim == null) ? c.getChannel() : victim.getClient().getChannel(), (victim == null) ? splitted[1] : (victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2)), true).getBytes());
            }
            catch (Exception e) {
                return 0;
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakmega [\u73a9\u5bb6\u540d\u79f0] <\u8baf\u606f> - \u5bf9\u67d0\u4e2a\u73a9\u5bb6\u7684\u983b\u9053\u8fdb\u884c\u5e7f\u64ad").toString();
        }
    }
    
    public static class Speak extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230 '" + splitted[1]);
                return 0;
            }
            victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speak <\u73a9\u5bb6\u540d\u79f0> <\u8baf\u606f> - \u5bf9\u67d0\u4e2a\u73a9\u5bb6\u53d1\u4fe1\u606f").toString();
        }
    }
    
    public static class SpeakMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakmap <\u8baf\u606f> - \u5bf9\u76ee\u524d\u5730\u56fe\u8fdb\u884c\u53d1\u9001\u4fe1\u606f").toString();
        }
    }
    
    public static class SpeakChannel extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <\u8baf\u606f> - \u5bf9\u76ee\u524d\u9891\u9053\u8fdb\u884c\u53d1\u9001\u4fe1\u606f").toString();
        }
    }
    
    public static class SpeakWorld extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <\u8baf\u606f> - \u5bf9\u76ee\u524d\u670d\u52a1\u5668\u8fdb\u884c\u4f20\u9001\u4fe1\u606f").toString();
        }
    }
    
    public static class Disease extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            int type;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            }
            else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            }
            else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            }
            else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            }
            else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            }
            else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            }
            else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            }
            else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            }
            else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            }
            else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            }
            else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            }
            else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            }
            else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            }
            else {
                if (!splitted[1].equalsIgnoreCase("FREEZE")) {
                    return 0;
                }
                type = 137;
            }
            final MapleDisease dis = MapleDisease.getBySkill(type);
            if (splitted.length == 4) {
                final String name = splitted[2];
                final int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                    return 0;
                }
                final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230\u6b64\u73a9\u5bb6");
                }
                else {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
                }
            }
            else {
                for (final MapleCharacter victim2 : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim2.setChair(0);
                    victim2.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim2.getMap().broadcastMessage(victim2, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim2.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!disease <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [\u89d2\u8272\u540d\u79f0] <\u72b6\u6001\u7b49\u7ea7> - \u8ba9\u4eba\u5f97\u5230\u7279\u6b8a\u72b6\u6001").toString();
        }
    }
    
    public static class SendAllNote extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length >= 1) {
                final String text = StringUtil.joinStringFrom(splitted, 1);
                for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <\u6587\u5b57> \u4f20\u9001Note\u7d66\u76ee\u524d\u983b\u9053\u7684\u6240\u6709\u4eba").toString();
        }
    }
    
    public static class giveMeso extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int gain = Integer.parseInt(splitted[2]);
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230 '" + name);
            }
            else {
                victim.gainMeso(gain, true);
                final String msg = "[GM \u5bc6\u8bed] GM " + c.getPlayer().getName() + " \u7ed9\u4e86 " + victim.getName() + " \u91d1\u5e01 " + gain + "\u70b9";
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <\u540d\u5b57> <\u6570\u91cf> - \u7d66\u73a9\u5bb6\u91d1\u5e01").toString();
        }
    }
    
    public static class CloneMe extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!cloneme - \u4ea7\u751f\u514b\u9686\u4f53").toString();
        }
    }
    
    public static class DisposeClones extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "\u4e2a\u514b\u9686\u4f53\u6d88\u5931\u4e86.");
            c.getPlayer().disposeClones();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - \u6467\u6bc1\u514b\u9686\u4f53").toString();
        }
    }
    
    public static class Monitor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                }
                else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            }
            else {
                c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230\u8be5\u73a9\u5bb6");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!monitor <\u73a9\u5bb6> - \u8bb0\u5f55\u73a9\u5bb6\u8d44\u8baf").toString();
        }
    }
    
    public static class PermWeather extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "\u5730\u56fe\u5929\u6c14\u5df2\u88ab\u7981\u7528.");
            }
            else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "\u65e0\u6548\u7684ID.");
                }
                else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "\u5730\u56fe\u5929\u6c14\u5df2\u542f\u7528.");
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!permweather - \u8bbe\u5b9a\u5929\u6c14").toString();
        }
    }
    
    public static class CharInfo extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final StringBuilder builder = new StringBuilder();
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter other = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (other == null) {
                builder.append("\u89d2\u8272\u4e0d\u5b58\u5728");
                c.getPlayer().dropMessage(6, builder.toString());
            }
            else {
                if (other.getClient().getLastPing() <= 0L) {
                    other.getClient().sendPing();
                }
                builder.append(MapleClient.getLogMessage(other, ""));
                builder.append(" \u5728 ").append(other.getPosition().x);
                builder.append(" /").append(other.getPosition().y);
                builder.append(" || \u8840\u91cf : ");
                builder.append(other.getStat().getHp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxHp());
                builder.append(" || \u9b54\u91cf : ");
                builder.append(other.getStat().getMp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxMp());
                builder.append(" || \u7269\u7406\u653b\u64ca\u529b : ");
                builder.append(other.getStat().getTotalWatk());
                builder.append(" || \u9b54\u6cd5\u653b\u64ca\u529b : ");
                builder.append(other.getStat().getTotalMagic());
                builder.append(" || \u6700\u9ad8\u653b\u64ca : ");
                builder.append(other.getStat().getCurrentMaxBaseDamage());
                builder.append(" || \u653b\u64ca%\u6578 : ");
                builder.append(other.getStat().dam_r);
                builder.append(" || BOSS\u653b\u64ca%\u6578 : ");
                builder.append(other.getStat().bossdam_r);
                builder.append(" || \u529b\u91cf : ");
                builder.append(other.getStat().getStr());
                builder.append(" || \u654f\u6377 : ");
                builder.append(other.getStat().getDex());
                builder.append(" || \u667a\u529b : ");
                builder.append(other.getStat().getInt());
                builder.append(" || \u5e78\u904b : ");
                builder.append(other.getStat().getLuk());
                builder.append(" || \u5168\u90e8\u529b\u91cf : ");
                builder.append(other.getStat().getTotalStr());
                builder.append(" || \u5168\u90e8\u654f\u6377 : ");
                builder.append(other.getStat().getTotalDex());
                builder.append(" || \u5168\u90e8\u667a\u529b : ");
                builder.append(other.getStat().getTotalInt());
                builder.append(" || \u5168\u90e8\u5e78\u904b : ");
                builder.append(other.getStat().getTotalLuk());
                builder.append(" || \u7d93\u9a57\u503c : ");
                builder.append(other.getExp());
                builder.append(" || \u7d44\u968a\u72c0\u614b : ");
                builder.append(other.getParty() != null);
                builder.append(" || \u4ea4\u6613\u72c0\u614b: ");
                builder.append(other.getTrade() != null);
                builder.append(" || Latency: ");
                builder.append(other.getClient().getLatency());
                builder.append(" || \u6700\u5f8cPING: ");
                builder.append(other.getClient().getLastPing());
                builder.append(" || \u6700\u5f8cPONG: ");
                builder.append(other.getClient().getLastPong());
                builder.append(" || IP: ");
                builder.append(other.getClient().getSessionIPAddress());
                other.getClient().DebugMessage(builder);
                c.getPlayer().dropMessage(6, builder.toString());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!charinfo <\u89d2\u8272\u540d\u79f0> - \u67e5\u770b\u89d2\u8272\u72b6\u6001").toString();
        }
    }
    
    public static class whoishere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            StringBuilder builder = new StringBuilder("\u5728\u6b64\u5730\u56fe\u7684\u73a9\u5bb6: ");
            for (final MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) {
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!whoishere - \u67e5\u770b\u76ee\u524d\u5730\u56fe\u4e0a\u7684\u73a9\u5bb6").toString();
        }
    }
    
    public static class Cheaters extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; --x) {
                final CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!cheaters - \u67e5\u770b\u4f5c\u5f0a\u89d2\u8272").toString();
        }
    }
    
    public static class Connected extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final java.util.Map<Integer, Integer> connected = World.getConnected();
            final StringBuilder conStr = new StringBuilder("\u5df2\u8fde\u63a5\u7684\u5ba2\u6236\u7aef: ");
            boolean first = true;
            for (final int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                }
                else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("\u6240\u6709: ");
                    conStr.append(connected.get(i));
                }
                else {
                    conStr.append("\u983b\u9053 ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!connected - \u67e5\u770b\u5df2\u8fde\u7ebf\u7684\u5ba2\u6236\u7aef").toString();
        }
    }
    
    public static class ResetQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetquest <\u4efb\u52a1ID> - \u91cd\u7f6e\u4efb\u52a1").toString();
        }
    }
    
    public static class StartQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!startquest <\u4efb\u52a1ID> - \u5f00\u59cb\u4efb\u52a1").toString();
        }
    }
    
    public static class CompleteQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!completequest <\u4efb\u52a1ID> - \u5b8c\u6210\u4efb\u52a1").toString();
        }
    }
    
    public static class FStartQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), (splitted.length >= 4) ? splitted[3] : null);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fstartquest <\u4efb\u52a1ID> - \u5f3a\u5236\u5f00\u59cb\u4efb\u52a1").toString();
        }
    }
    
    public static class FCompleteQuest extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fcompletequest <\u4efb\u52a1ID> - \u5f3a\u5236\u5b8c\u6210\u4efb\u52a1").toString();
        }
    }
    
    public static class FStartOther extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), (splitted.length >= 4) ? splitted[4] : null);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fstartother - \u4e0d\u77e5\u9053\u5565").toString();
        }
    }
    
    public static class FCompleteOther extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fcompleteother - \u4e0d\u77e5\u9053\u5565").toString();
        }
    }
    
    public static class NearestPortal extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!nearestportal - \u4e0d\u77e5\u9053\u5565").toString();
        }
    }
    
    public static class SpawnDebug extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawndebug - debug\u602a\u7269\u51fa\u751f").toString();
        }
    }
    
    public static class Threads extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; ++i) {
                final String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!threads - \u67e5\u770bThreads\u8d44\u8baf").toString();
        }
    }
    
    public static class ShowTrace extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            final Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (final StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!showtrace - show trace info").toString();
        }
    }
    
    public static class FakeRelog extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            c.sendPacket(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!fakerelog - \u5047\u767b\u51fa\u518d\u767b\u5165").toString();
        }
    }
    
    public static class ToggleOffense extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            try {
                final CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            }
            catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!toggleoffense <Offense> - \u5f00\u542f\u6216\u5173\u95edCheatOffense").toString();
        }
    }
    
    public static class toggleDrop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!toggledrop - \u5f00\u542f\u6216\u5173\u95ed\u6389\u843d").toString();
        }
    }
    
    public static class ToggleMegaphone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "\u5e7f\u64ad\u662f\u5426\u5c01\u9501 : " + (c.getChannelServer().getMegaphoneMuteState() ? "\u662f" : "\u5426"));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - \u5f00\u542f\u6216\u8005\u5173\u95ed\u5e7f\u64ad").toString();
        }
    }
    
    public static class SpawnReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final MapleReactorStats reactorSt = MapleReactorFactory.getReactor(id);
            final MapleReactor reactor = new MapleReactor(reactorSt, id);
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawnreactor - \u8bbe\u7acbReactor").toString();
        }
    }
    
    public static class HReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - \u89e6\u78b0Reactor").toString();
        }
    }
    
    public static class DestroyReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (final MapleMapObject reactorL : reactors) {
                    final MapleReactor reactor2l = (MapleReactor)reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            }
            else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!drstroyreactor - \u79fb\u9664Reactor").toString();
        }
    }
    
    public static class ResetReactors extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetReactors();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetreactors - \u91cd\u7f6e\u6b64\u5730\u56fe\u6240\u6709\u7684Reactor").toString();
        }
    }
    
    public static class SetReactor extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - \u89e6\u78b0Reactor").toString();
        }
    }
    
    public static class cleardrops extends RemoveDrops
    {
    }
    
    public static class RemoveDrops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "\u6e05\u9664\u4e86 " + c.getPlayer().getMap().getNumItems() + " \u4e2a\u6389\u843d\u7269");
            c.getPlayer().getMap().removeDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removedrops - \u79fb\u9664\u5730\u4e0a\u7684\u7269\u54c1").toString();
        }
    }
    
    public static class ExpRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "\u7ecf\u9a8c\u500d\u7387\u5df2\u6539\u53d8\u66f4\u4e3a " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!exprate <\u500d\u7387> - \u66f4\u6539\u7ecf\u9a8c\u500d\u7387").toString();
        }
    }
    
    public static class DropRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "\u6389\u5b9d\u500d\u7387\u5df2\u6539\u53d8\u66f4\u4e3a " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!droprate <\u500d\u7387> - \u66f4\u6539\u6389\u843d\u500d\u7387").toString();
        }
    }
    
    public static class MesoRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                }
                else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "\u91d1\u5e01\u7206\u7387\u5df2\u6539\u53d8\u66f4\u4e3a " + rate + "x");
                return 1;
            }
            return 0;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mesorate <\u500d\u7387> - \u66f4\u6539\u91d1\u94b1\u500d\u7387").toString();
        }
    }
    
    public static class DCAll extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int range = -1;
            if (splitted.length < 2) {
                return 0;
            }
            String input = null;
            try {
                input = splitted[1];
            }
            catch (Exception ex) {}
            final String s = splitted[1];
            switch (s) {
                case "m": {
                    range = 0;
                    break;
                }
                case "c": {
                    range = 1;
                    break;
                }
                default: {
                    range = 2;
                    break;
                }
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            }
            else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll();
            }
            else if (range == 2) {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            String show = "";
            switch (range) {
                case 0: {
                    show = "\u5730\u56fe";
                    break;
                }
                case 1: {
                    show = "\u983b\u9053";
                    break;
                }
                case 2: {
                    show = "\u4e16\u754c";
                    break;
                }
            }
            final String msg = "[GM \u5bc6\u8bed] GM " + c.getPlayer().getName() + "  DC \u4e86 " + show + "\u73a9\u5bb6";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - \u6240\u6709\u73a9\u5bb6\u65ad\u7ebf").toString();
        }
    }
    
    public static class GoTo extends CommandExecute
    {
        private static final HashMap<String, Integer> gotomaps;
        
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
            }
            else if (GoTo.gotomaps.containsKey(splitted[1])) {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(GoTo.gotomaps.get(splitted[1]));
                final MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            }
            else if (splitted[1].equals("locations")) {
                c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                final StringBuilder sb = new StringBuilder();
                for (final String s : GoTo.gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            }
            else {
                c.getPlayer().dropMessage(6, "Invalid command \u6307\u4ee4\u898f\u5247 - Use !goto <location>. For a list of locations, use !goto locations.");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!goto <\u540d\u79f0> - \u5230\u67d0\u4e2a\u5730\u56fe").toString();
        }
        
        static {
            (gotomaps = new HashMap<String, Integer>()).put("gmmap", 180000000);
            GoTo.gotomaps.put("southperry", 2000000);
            GoTo.gotomaps.put("amherst", 1010000);
            GoTo.gotomaps.put("henesys", 100000000);
            GoTo.gotomaps.put("ellinia", 101000000);
            GoTo.gotomaps.put("perion", 102000000);
            GoTo.gotomaps.put("kerning", 103000000);
            GoTo.gotomaps.put("lithharbour", 104000000);
            GoTo.gotomaps.put("sleepywood", 105040300);
            GoTo.gotomaps.put("florina", 110000000);
            GoTo.gotomaps.put("orbis", 200000000);
            GoTo.gotomaps.put("happyville", 209000000);
            GoTo.gotomaps.put("elnath", 211000000);
            GoTo.gotomaps.put("ludibrium", 220000000);
            GoTo.gotomaps.put("aquaroad", 230000000);
            GoTo.gotomaps.put("leafre", 240000000);
            GoTo.gotomaps.put("mulung", 250000000);
            GoTo.gotomaps.put("herbtown", 251000000);
            GoTo.gotomaps.put("omegasector", 221000000);
            GoTo.gotomaps.put("koreanfolktown", 222000000);
            GoTo.gotomaps.put("newleafcity", 600000000);
            GoTo.gotomaps.put("sharenian", 990000000);
            GoTo.gotomaps.put("pianus", 230040420);
            GoTo.gotomaps.put("horntail", 240060200);
            GoTo.gotomaps.put("chorntail", 240060201);
            GoTo.gotomaps.put("mushmom", 100000005);
            GoTo.gotomaps.put("griffey", 240020101);
            GoTo.gotomaps.put("manon", 240020401);
            GoTo.gotomaps.put("zakum", 280030000);
            GoTo.gotomaps.put("czakum", 280030001);
            GoTo.gotomaps.put("papulatus", 220080001);
            GoTo.gotomaps.put("showatown", 801000000);
            GoTo.gotomaps.put("zipangu", 800000000);
            GoTo.gotomaps.put("ariant", 260000100);
            GoTo.gotomaps.put("nautilus", 120000000);
            GoTo.gotomaps.put("boatquay", 541000000);
            GoTo.gotomaps.put("malaysia", 550000000);
            GoTo.gotomaps.put("taiwan", 740000000);
            GoTo.gotomaps.put("thailand", 500000000);
            GoTo.gotomaps.put("erev", 130000000);
            GoTo.gotomaps.put("ellinforest", 300000000);
            GoTo.gotomaps.put("kampung", 551000000);
            GoTo.gotomaps.put("singapore", 540000000);
            GoTo.gotomaps.put("amoria", 680000000);
            GoTo.gotomaps.put("timetemple", 270000000);
            GoTo.gotomaps.put("pinkbean", 270050100);
            GoTo.gotomaps.put("peachblossom", 700000000);
            GoTo.gotomaps.put("fm", 910000000);
            GoTo.gotomaps.put("freemarket", 910000000);
            GoTo.gotomaps.put("oxquiz", 109020001);
            GoTo.gotomaps.put("ola", 109030101);
            GoTo.gotomaps.put("fitness", 109040000);
            GoTo.gotomaps.put("snowball", 109060000);
            GoTo.gotomaps.put("cashmap", 741010200);
            GoTo.gotomaps.put("golden", 950100000);
            GoTo.gotomaps.put("phantom", 610010000);
            GoTo.gotomaps.put("cwk", 610030000);
            GoTo.gotomaps.put("rien", 140000000);
        }
    }
    
    public static class KillAll extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            boolean drop = false;
            if (splitted.length > 1) {
                final int irange = 9999;
                if (splitted.length < 2) {
                    range = irange * irange;
                }
                else {
                    try {
                        map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        range = Integer.parseInt(splitted[2]) * Integer.parseInt(splitted[2]);
                    }
                    catch (Exception ex) {}
                }
                if (splitted.length >= 3) {
                    drop = splitted[3].equalsIgnoreCase("true");
                }
            }
            final List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (final MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                final MapleMonster mob = (MapleMonster)monstermo;
                map.killMonster(mob, c.getPlayer(), drop, false, (byte)1);
            }
            c.getPlayer().dropMessage("\u60a8\u603b\u5171\u6740\u4e86 " + monsters.size() + " \u602a\u7269");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - \u6740\u6389\u6240\u6709\u602a\u7269").toString();
        }
    }
    
    public static class ResetMobs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!resetmobs - \u91cd\u7f6e\u5730\u56fe\u4e0a\u6240\u6709\u602a\u7269").toString();
        }
    }
    
    public static class KillMonster extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final double range = Double.POSITIVE_INFINITY;
            for (final MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                final MapleMonster mob = (MapleMonster)monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmonster <mobid> - \u6740\u6389\u5730\u56fe\u4e0a\u67d0\u4e2a\u602a\u7269").toString();
        }
    }
    
    public static class KillMonsterByOID extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final MapleMap map = c.getPlayer().getMap();
            final int targetId = Integer.parseInt(splitted[1]);
            final MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte)1);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!killmonsterbyoid <moboid> - \u6740\u6389\u5730\u56fe\u4e0a\u67d0\u4e2a\u602a\u7269").toString();
        }
    }
    
    public static class HitMonsterByOID extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleMap map = c.getPlayer().getMap();
            final int targetId = Integer.parseInt(splitted[1]);
            final int damage = Integer.parseInt(splitted[2]);
            final MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - \u78b0\u649e\u5730\u56fe\u4e0a\u67d0\u500b\u602a\u7269").toString();
        }
    }
    
    public static class NPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int npcId = 0;
            try {
                npcId = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            final MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
            }
            else {
                c.getPlayer().dropMessage(6, "\u627e\u4e0d\u5230\u6b64\u4ee3\u7801\u4e3a" + npcId + "\u7684Npc");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!npc <npcid> - \u547c\u53eb\u51faNPC").toString();
        }
    }
    
    public static class RemoveNPCs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removenpcs - \u522a\u9664\u6240\u6709NPC").toString();
        }
    }
    
    public static class LookNPCs extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                final MapleNPC reactor2l = (MapleNPC)reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!looknpcs - \u67e5\u770b\u6240\u6709NPC").toString();
        }
    }
    
    public static class LookReactors extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                final MapleReactor reactor2l = (MapleReactor)reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lookreactors - \u67e5\u770b\u6240\u6709\u53cd\u5e94\u5806").toString();
        }
    }
    
    public static class LookPortals extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u53cd\u5e94\u5806 - \u67e5\u770b\u6240\u6709\u53cd\u5e94\u5806").toString();
        }
    }
    
    public static class MakePNPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 3) {
                return 0;
            }
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                final String name = splitted[1];
                final int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                    return 1;
                }
                final MapleCharacter chhr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " is not online");
                }
                else {
                    final int npcId = Integer.parseInt(splitted[2]);
                    final MapleNPC npc_c = MapleLifeFactory.getNPC(npcId);
                    if (npc_c == null || npc_c.getName().equals("MISSINGNO")) {
                        c.getPlayer().dropMessage(6, "NPC\u4e0d\u5b58\u5728");
                        return 1;
                    }
                    final PlayerNPC npc = new PlayerNPC(chhr, npcId, c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u73a9\u5bb6npc <playername> <npcid> - \u521b\u9020\u73a9\u5bb6NPC").toString();
        }
    }
    
    public static class MakeOfflineP extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                final MapleClient cs = new MapleClient(null, null, (IoSession)new MockIOSession());
                final MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " does not exist");
                }
                else {
                    final PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u79bb\u7ebfnpc <charname> <npcid> - \u521b\u9020\u79bb\u7ebfPNPC").toString();
        }
    }
    
    public static class DestroyPNPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC)npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                }
                else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!destroypnpc [objectid] - \u522a\u9664PNPC").toString();
        }
    }
    
    public static class MyPos extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH() + "| CY:" + pos.y);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!mypos - \u6211\u7684\u4f4d\u7f6e").toString();
        }
    }
    
    public static class ReloadOps extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!reloadops - \u91cd\u65b0\u8f7d\u5165OpCode").toString();
        }
    }
    
    public static class ReloadDrops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u91cd\u65b0\u8f7d\u5165\u6389\u5b9d - \u91cd\u65b0\u8f09\u5165\u6389\u5b9d").toString();
        }
    }
    
    public static class ReloadPortals extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            PortalScriptManager.getInstance().clearScripts();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!reloadportals - \u91cd\u65b0\u8f7d\u5165\u8fdb\u5165\u70b9").toString();
        }
    }
    
    public static class ReloadShops extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleShopFactory.getInstance().clear();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u91cd\u65b0\u8f7d\u5165\u5546\u5e97 - \u91cd\u65b0\u8f7d\u5165\u5546\u5e97").toString();
        }
    }
    
    public static class ReloadEvents extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }
    
    public static class ReloadQuests extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleQuest.clearQuests();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u91cd\u65b0\u8f7d\u5165\u4efb\u52a1 - \u91cd\u65b0\u8f7d\u5165\u4efb\u52a1").toString();
        }
    }
    
    public static class Spawn extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int mid = 0;
            try {
                mid = Integer.parseInt(splitted[1]);
            }
            catch (Exception ex) {}
            int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            if (num > 1000) {
                num = 1000;
            }
            final Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            final Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            final Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            final Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            }
            catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "\u9519\u8bef: " + e.getMessage());
                return 1;
            }
            long newhp;
            if (hp != null) {
                newhp = hp;
            }
            else if (php != null) {
                newhp = (long)(onemob.getMobMaxHp() * (php / 100.0));
            }
            else {
                newhp = onemob.getMobMaxHp();
            }
            int newexp;
            if (exp != null) {
                newexp = exp;
            }
            else if (pexp != null) {
                newexp = (int)(onemob.getMobExp() * (pexp / 100.0));
            }
            else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1L) {
                newhp = 1L;
            }
            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; ++i) {
                final MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!spawn <\u602a\u7269ID> <hp|exp|php||pexp = ?> - \u53ec\u5524\u602a\u7269").toString();
        }
    }
    
    public static class Clock extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!clock <time> \u65f6\u949f").toString();
        }
    }
    
    public static class WarpPlayersTo extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                final MapleMap from = c.getPlayer().getMap();
                for (final MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            }
            catch (Exception e) {
                return 0;
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!WarpPlayersTo <maipid> \u628a\u6240\u6709\u73a9\u5bb6\u4f20\u9001\u5230\u67d0\u4e2a\u5730\u56fe").toString();
        }
    }
    
    public static class LOLCastle extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length != 2) {
                return 0;
            }
            final MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted[1]).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!lolcastle level (level = 1-5) - \u4e0d\u77e5\u9053\u662f\u5565").toString();
        }
    }
    
    public static class Map extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(5, "\u5730\u56fe\u4e0d\u5b58\u5728.");
                    return 1;
                }
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    }
                    catch (IndexOutOfBoundsException e2) {
                        c.getPlayer().dropMessage(5, "\u4f20\u9001\u70b9\u9519\u8bef.");
                    }
                    catch (NumberFormatException ex) {}
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                c.getPlayer().changeMap(target, targetPortal);
            }
            catch (Exception e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!map <mapid|charname> [portal] - \u4f20\u9001\u5230\u67d0\u5730\u56fe/\u4eba").toString();
        }
    }
    
    public static class StartProfiling extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants");
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!startprofiling \u5f00\u59cb\u8bb0\u5f55JVM\u8d44\u8baf").toString();
        }
    }
    
    public static class StopProfiling extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                final File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return 1;
                }
                sampler.stop();
                try (final FileWriter fw = new FileWriter(file)) {
                    sampler.save(fw, 1, 10);
                }
            }
            catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!stopprofiling <filename> - \u53d6\u6d88\u8bb0\u5f55JVM\u8d44\u8baf\u5e76\u4fdd\u5b58\u5230\u6863\u6848").toString();
        }
    }
    
    public static class ReloadMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return 0;
            }
            final boolean custMap = splitted.length >= 2;
            final int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            final MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                final MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                final MaplePortal newPor = newMap.getPortal(0);
                final LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters());
            Label_0139:
                for (final MapleCharacter m : mcs) {
                    int x = 0;
                    while (x < 5) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue Label_0139;
                        }
                        catch (Throwable t) {
                            ++x;
                            continue;
                        }
                        
                    }
                    player.dropMessage("Failed warping " + m.getName() + " to the new map. Skipping...");
                }
                player.dropMessage("\u5730\u56fe\u5237\u65b0\u5b8c\u6bd5\uff0c\u5982\u8fd8\u51fa\u73b0NPC\u4e0d\u89c1\u8bf7\u4f7f\u7528\u6b64\u547d\u4ee4.");
                return 1;
            }
            player.dropMessage("Unsuccessful reset!");
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!reloadmap <maipid> - \u91cd\u7f6e\u67d0\u4e2a\u5730\u56fe").toString();
        }
    }
    
    public static class Respawn extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!respawn - \u91cd\u65b0\u8f7d\u5165\u5730\u56fe").toString();
        }
    }
    
    public static class ResetMap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!respawn - \u91cd\u7f6e\u8fd9\u4e2a\u5730\u56fe").toString();
        }
    }
    
    public static class Reloadall extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            MapleShopFactory.getInstance().clear();
            PortalScriptManager.getInstance().clearScripts();
            MapleItemInformationProvider.getInstance().load();
            CashItemFactory.getInstance().initialize();
            MapleMonsterInformationProvider.getInstance().clearDrops();
            MapleGuild.loadAll();
            MapleFamily.loadAll();
            MapleLifeFactory.loadQuestCounts();
            MapleQuest.initQuests();
            MapleOxQuizFactory.getInstance();
            ReactorScriptManager.getInstance().clearDrops();
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!Reloadall - \u91cd\u7f6e\u5168\u670d\u52a1\u5668").toString();
        }
    }
    
    public static class PNPC extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final int npcId = Integer.parseInt(splitted[1]);
            final MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                try {
                    final com.mysql.jdbc.Connection con = (com.mysql.jdbc.Connection)DatabaseConnection.getConnection();
                    try (final PreparedStatement ps = (PreparedStatement)con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, 0);
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "Failed to save NPC to the database");
                }
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).addMapObject(npc);
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
                }
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the NPC will disappear till the next restart.");
            }
            else {
                c.getPlayer().dropMessage(6, "\u67e5\u65e0\u6b64 Npc ");
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!\u6c38\u4e45npc - \u5efa\u7acb\u6c38\u4e45NPC").toString();
        }
    }
    
    public static class copyInv extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            int type = 1;
            if (splitted.length < 2) {
                return 0;
            }
            final String name = splitted[1];
            final int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "\u73a9\u5bb6\u5fc5\u987b\u5728\u7ebf");
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("\u627e\u4e0d\u5230\u8be5\u73a9\u5bb6");
                return 1;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            }
            catch (Exception ex) {}
            if (type == 0) {
                for (final IItem ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    final IItem n = ii.copy();
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                }
                player.fakeRelog();
            }
            else {
                MapleInventoryType types;
                if (type == 1) {
                    types = MapleInventoryType.EQUIP;
                }
                else if (type == 2) {
                    types = MapleInventoryType.USE;
                }
                else if (type == 3) {
                    types = MapleInventoryType.ETC;
                }
                else if (type == 4) {
                    types = MapleInventoryType.SETUP;
                }
                else if (type == 5) {
                    types = MapleInventoryType.CASH;
                }
                else {
                    types = null;
                }
                if (types == null) {
                    c.getPlayer().dropMessage("\u53d1\u751f\u9519\u8bef");
                    return 1;
                }
                final int[] equip = new int[97];
                for (int i = 1; i < 97; ++i) {
                    if (victim.getInventory(types).getItem((short)i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; ++i) {
                    if (equip[i] != 0) {
                        final IItem n2 = victim.getInventory(types).getItem((short)equip[i]).copy();
                        player.getInventory(types).addItem(n2);
                    }
                }
                player.fakeRelog();
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!copyinv \u73a9\u5bb6\u540d\u79f0 \u88c5\u5907\u680f\u4f4d(0 = \u88c5\u5907\u4e2d 1=\u88c5\u5907\u680f 2=\u6d88\u8017\u680f 3=\u5176\u4ed6\u680f 4=\u88c5\u9970\u680f 5=\u70b9\u6570\u680f)(\u9884\u8bbe\u88c5\u5907\u680f) - \u590d\u5236\u73a9\u5bb6\u9053\u5177").toString();
        }
    }
    
    public static class RemoveItemOff extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            final com.mysql.jdbc.Connection dcon = (com.mysql.jdbc.Connection)DatabaseConnection.getConnection();
            try {
                int id = 0;
                final int quantity = 0;
                final String name = splitted[2];
                final PreparedStatement ps = (PreparedStatement)dcon.prepareStatement("select * from characters where name = ?");
                ps.setString(1, name);
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                    }
                }
                if (id == 0) {
                    c.getPlayer().dropMessage(5, "\u89d2\u8272\u4e0d\u5b58\u5728\u8d44\u6599\u5e93\u3002");
                    return 0;
                }
                final PreparedStatement ps2 = (PreparedStatement)dcon.prepareStatement("delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps2.setInt(1, Integer.parseInt(splitted[1]));
                ps2.setInt(2, id);
                ps2.executeUpdate();
                c.getPlayer().dropMessage(6, "\u6240\u6709ID\u4e3a " + splitted[1] + " \u7684\u9053\u5177" + quantity + "\u5df2\u7ecf\u4ece " + name + " \u8eab\u4e0a\u88ab\u79fb\u9664\u4e86");
                ps.close();
                ps2.close();
                return 1;
            }
            catch (SQLException e) {
                return 0;
            }
        }
        
        public String getMessage() {
            return new StringBuilder().append("!removeitem <\u7269\u54c1ID> <\u89d2\u8272\u540d\u7a31> - \u79fb\u9664\u73a9\u5bb6\u8eab\u4e0a\u7684\u9053\u5177").toString();
        }
    }
    
    public static class ExpEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <\u7ecf\u9a8c\u91cf>");
                return 0;
            }
            final int gain = Integer.parseInt(splitted[1]);
            int ret = 0;
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainExp(gain, true, true, true);
                    ++ret;
                }
            }
            for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : cserv2.getPlayerStorage().getAllCharacters()) {
                    mch.startMapEffect("\u7ba1\u7406\u5458\u53d1\u653e" + gain + "\u7ecf\u9a8c\u7ed9\u5728\u7ebf\u7684\u6240\u6709\u73a9\u5bb6\uff01\u795d\u60a8\u73a9\u7684\u5f00\u5fc3\u73a9\u7684\u5feb\u4e50", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "\u547d\u4ee4\u4f7f\u7528\u6210\u529f\uff0c\u5f53\u524d\u5171\u6709: " + ret + " \u4e2a\u73a9\u5bb6\u83b7\u5f97: " + gain + " \u70b9\u7684" + " \u7ecf\u9a8c " + " \u603b\u8ba1: " + ret * gain);
            return 1;
        }
    }
    
    public static class CashEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length > 2) {
                int type = Integer.parseInt(splitted[1]);
                final int quantity = Integer.parseInt(splitted[2]);
                switch (type) {
                    case 1: {
                        type = 1;
                        break;
                    }
                    case 2: {
                        type = 2;
                        break;
                    }
                    default: {
                        c.getPlayer().dropMessage(6, "\u7528\u6cd5: !\u7ed9\u6240\u6709\u4eba\u70b9\u5377 [\u70b9\u5377\u7c7b\u578b1-2] [\u70b9\u5377\u6570\u91cf][1\u662f\u70b9\u5377.2\u662f\u62b5\u7528\u5377]");
                        return 0;
                    }
                }
                int ret = 0;
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                        mch.modifyCSPoints(type, quantity);
                        ++ret;
                    }
                }
                final String show = (type == 1) ? "\u70b9\u5377" : "\u62b5\u7528\u5377";
                for (final ChannelServer cserv2 : ChannelServer.getAllInstances()) {
                    for (final MapleCharacter mch2 : cserv2.getPlayerStorage().getAllCharacters()) {
                        mch2.startMapEffect("\u7ba1\u7406\u5458\u53d1\u653e" + quantity + show + "\u70b9\u5377\u7ed9\u5728\u7ebf\u7684\u6240\u6709\u73a9\u5bb6\uff01\u795d\u60a8\u7684\u5f00\u5fc3\u73a9\u7684\u5feb\u4e50", 5121009);
                    }
                }
                c.getPlayer().dropMessage(6, "\u547d\u4ee4\u4f7f\u7528\u6210\u529f\uff0c\u5f53\u524d\u5171\u6709: " + ret + " \u4e2a\u73a9\u5bb6\u83b7\u5f97: " + quantity + " \u70b9\u7684" + ((type == 1) ? "\u70b9\u5238 " : " \u62b5\u7528\u5238 ") + " \u603b\u8ba1: " + ret * quantity);
            }
            else {
                c.getPlayer().dropMessage(6, "\u7528\u6cd5: !\u7ed9\u6240\u6709\u4eba\u70b9\u5377 [\u70b9\u5377\u7c7b\u578b1-2] [\u70b9\u5377\u6570\u91cf][1\u662f\u70b9\u5377.2\u662f\u62b5\u7528\u5377]");
            }
            return 1;
        }
    }
    
    public static class mesoEveryone extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " <\u91d1\u5e01\u91cf>");
                return 0;
            }
            int ret = 0;
            final int gain = Integer.parseInt(splitted[1]);
            for (final MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                mch.gainMeso(gain, true);
                ++ret;
            }
            for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch2 : cserv1.getPlayerStorage().getAllCharacters()) {
                    mch2.startMapEffect("\u7ba1\u7406\u5458\u53d1\u653e" + gain + "\u5192\u9669\u5e01\u7ed9\u5728\u7ebf\u7684\u6240\u6709\u73a9\u5bb6\uff01\u795d\u60a8\u73a9\u7684\u5f00\u5fc3\u73a9\u7684\u5feb\u4e50", 5121009);
                }
            }
            c.getPlayer().dropMessage(6, "\u547d\u4ee4\u4f7f\u7528\u6210\u529f\uff0c\u5f53\u524d\u5171\u6709: " + ret + " \u4e2a\u73a9\u5bb6\u83b7\u5f97: " + gain + " \u5192\u9669\u5e01 " + " \u603b\u8ba1: " + ret * gain);
            return 1;
        }
    }
    
    public static class setRate extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter mc;
            final MapleCharacter player = mc = c.getPlayer();
            if (splitted.length > 2) {
                final int arg = Integer.parseInt(splitted[2]);
                final int seconds = Integer.parseInt(splitted[3]);
                final int mins = Integer.parseInt(splitted[4]);
                final int hours = Integer.parseInt(splitted[5]);
                final int time = seconds + mins * 60 + hours * 60 * 60;
                boolean bOk = true;
                if (splitted[1].equals("\u7ecf\u9a8c")) {
                    if (arg <= 1000) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setExpRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "\u7ecf\u9a8c\u500d\u7387\u5df2\u7ecf\u6210\u529f\u4fee\u6539\u4e3a " + arg + "\u500d\u3002\u795d\u5927\u5bb6\u6e38\u620f\u5f00\u5fc3.\u7ecf\u9a8c\u500d\u7387\u5c06\u5728\u65f6\u95f4\u5230\u540e\u81ea\u52a8\u66f4\u6b63\uff01"));
                        }
                    }
                    else {
                        mc.dropMessage("\u64cd\u4f5c\u5df2\u88ab\u7cfb\u7edf\u9650\u5236\u3002");
                    }
                }
                else if (splitted[1].equals("\u7206\u7387")) {
                    if (arg <= 50) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "\u7206\u7387\u500d\u7387\u5df2\u7ecf\u6210\u529f\u4fee\u6539\u4e3a " + arg + "\u500d\u3002\u795d\u5927\u5bb6\u6e38\u620f\u5f00\u5fc3.\u7206\u7387\u500d\u7387\u5c06\u5728\u65f6\u95f4\u5230\u540e\u81ea\u52a8\u66f4\u6b63\uff01\uff01"));
                        }
                    }
                    else {
                        mc.dropMessage("\u64cd\u4f5c\u5df2\u88ab\u7cfb\u7edf\u9650\u5236\u3002");
                    }
                }
                else if (splitted[1].equals("\u91d1\u5e01")) {
                    if (arg <= 50) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setMesoRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "\u91d1\u5e01\u500d\u7387\u5df2\u7ecf\u6210\u529f\u4fee\u6539\u4e3a " + arg + "\u500d\u3002\u795d\u5927\u5bb6\u6e38\u620f\u5f00\u5fc3.\u91d1\u5e01\u500d\u7387\u5c06\u5728\u65f6\u95f4\u5230\u540e\u81ea\u52a8\u66f4\u6b63\uff01\uff01"));
                        }
                    }
                    else {
                        mc.dropMessage("\u64cd\u4f5c\u5df2\u88ab\u7cfb\u7edf\u9650\u5236\u3002");
                    }
                }
                else if (splitted[1].equalsIgnoreCase("boss\u7206\u7387")) {
                    if (arg <= 50) {
                        for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                            cservs.setBossDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "BOSS\u6389\u5b9d\u5df2\u7ecf\u6210\u529f\u4fee\u6539\u4e3a " + arg + "\u500d\u3002\u795d\u5927\u5bb6\u6e38\u620f\u5f00\u5fc3.boos\u6389\u843d\u500d\u7387\u5c06\u5728\u65f6\u95f4\u5230\u540e\u81ea\u52a8\u66f4\u6b63\uff01\uff01"));
                        }
                    }
                    else {
                        mc.dropMessage("\u64cd\u4f5c\u5df2\u88ab\u7cfb\u7edf\u9650\u5236\u3002");
                    }
                }
                else if (splitted[1].equals("\u5ba0\u7269\u7ecf\u9a8c")) {
                    if (arg > 5) {
                        mc.dropMessage("\u64cd\u4f5c\u5df2\u88ab\u7cfb\u7edf\u9650\u5236\u3002");
                    }
                }
                else {
                    bOk = false;
                }
                if (bOk) {
                    final String rate = splitted[1];
                    World.scheduleRateDelay(rate, time);
                }
                else {
                    mc.dropMessage("\u4f7f\u7528\u65b9\u6cd5: !\u500d\u7387\u8bbe\u7f6e <exp\u7ecf\u9a8c|drop\u7206\u7387|meso\u91d1\u5e01|bossboss\u7206\u7387|pet> <\u7c7b> <\u79d2> <\u5206> <\u65f6>");
                }
            }
            else {
                mc.dropMessage("\u4f7f\u7528\u65b9\u6cd5: !\u500d\u7387\u8bbe\u7f6e <exp\u7ecf\u9a8c|drop\u7206\u7387|meso\u91d1\u5e01|bossboss\u7206\u7387|pet> <\u7c7b> <\u79d2> <\u5206> <\u65f6>");
            }
            return 1;
        }
    }
    
    public static class WarpAllHere extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            for (final ChannelServer CS : ChannelServer.getAllInstances()) {
                for (final MapleCharacter mch : CS.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (mch.getMapId() != c.getPlayer().getMapId()) {
                        mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                    }
                    if (mch.getClient().getChannel() != c.getPlayer().getClient().getChannel()) {
                        mch.changeChannel(c.getPlayer().getClient().getChannel());
                    }
                }
            }
            return 1;
        }
        
        public String getMessage() {
            return new StringBuilder().append("!WarpAllHere \u628a\u6240\u6709\u73a9\u5bb6\u4f20\u9001\u5230\u8fd9\u91cc").toString();
        }
    }
    
    public static class maxSkills extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.maxSkills();
            return 1;
        }
    }
    
    public static class Drop extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            if (splitted.length < 2) {
                return 0;
            }
            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            }
            catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            final short quantity = (short)CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "\u5ba0\u7269\u8bf7\u5230\u8d2d\u7269\u5546\u57ce\u8d2d\u4e70.");
            }
            else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - \u7269\u54c1\u4e0d\u5b58\u5728");
            }
            else {
                IItem toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = ii.randomizeStats((Equip)ii.getEquipById(itemId));
                }
                else {
                    toDrop = new client.inventory.Item(itemId, (short)0, quantity, (byte)0);
                }
                toDrop.setGMLog(c.getPlayer().getName());
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return 1;
        }
    }
    
    public static class buff extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            SkillFactory.getSkill(9001002).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001003).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001008).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            return 1;
        }
    }
    
    public static class maxstats extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MapleCharacter player = c.getPlayer();
            player.getStat().setMaxHp((short)30000);
            player.getStat().setMaxMp((short)30000);
            player.getStat().setStr((short)32767);
            player.getStat().setDex((short)32767);
            player.getStat().setInt((short)32767);
            player.getStat().setLuk((short)32767);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            player.updateSingleStat(MapleStat.STR, 32767);
            player.updateSingleStat(MapleStat.DEX, 32767);
            player.updateSingleStat(MapleStat.INT, 32767);
            player.updateSingleStat(MapleStat.LUK, 32767);
            return 1;
        }
    }
    
    public static class WhereAmI extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().dropMessage(5, "\u76ee\u524d\u5730\u56fe " + c.getPlayer().getMap().getId() + "\u5750\u6807 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return 1;
        }
    }
    
    public static class Packet extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            final int packetheader = Integer.parseInt(splitted[1]);
            String packet_in = " 00 00 00 00 00 00 00 00 00 ";
            if (splitted.length > 2) {
                packet_in = StringUtil.joinStringFrom(splitted, 2);
            }
            mplew.writeShort(packetheader);
            mplew.write(HexTool.getByteArrayFromHexString(packet_in));
            mplew.writeZeroBytes(20);
            c.getSession().write((Object)mplew.getPacket());
            c.getPlayer().dropMessage(packetheader + "\u5df2\u4f20\u9001\u5c01\u5305[" + mplew.getPacket().getBytes().length + "] : " + mplew.toString());
            return 1;
        }
    }
    
    public static class mob extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster)monstermo;
                if (monster.isAlive()) {
                    c.getPlayer().dropMessage(6, "\u602a\u7269 " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "\u627e\u4e0d\u5230\u602a\u7269");
            }
            return 1;
        }
    }
    
    public static class register extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            String acc = null;
            String password = null;
            try {
                acc = splitted[1];
                password = splitted[2];
            }
            catch (Exception ex3) {}
            if (acc == null || password == null) {
                c.getPlayer().dropMessage("\u8d26\u53f7\u6216\u5bc6\u7801\u5f02\u5e38");
                return 0;
            }
            final boolean ACCexist = AutoRegister.getAccountExists(acc);
            if (ACCexist) {
                c.getPlayer().dropMessage("\u5e10\u53f7\u5df2\u88ab\u4f7f\u7528");
                return 0;
            }
            if (acc.length() >= 12) {
                c.getPlayer().dropMessage("\u5bc6\u7801\u957f\u5ea6\u8fc7\u957f");
                return 0;
            }
            try {
                final Connection con = DatabaseConnection.getConnection();
            }
            catch (Exception ex) {
                System.out.println(ex);
                return 0;
            }
            Connection con = null;
            try (final PreparedStatement ps = (PreparedStatement)con.prepareStatement("INSERT INTO accounts (name, password, md5pass) VALUES (?, ? ,?)")) {
                ps.setString(1, acc);
                ps.setString(2, LoginCrypto.hexSha1(password));
                ps.setString(3, "");
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException ex2) {
                System.out.println(ex2);
                return 0;
            }
            c.getPlayer().dropMessage("[\u6ce8\u518c\u5b8c\u6210]\u8d26\u53f7: " + acc + " \u5bc6\u7801: " + password);
            return 1;
        }
    }
    
    public static class openmap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int mapid = 0;
            String input = null;
            final MapleMap map = null;
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " - \u5f00\u653e\u5730\u56fe");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().HealMap(mapid);
            }
            return 1;
        }
    }
    
    public static class closemap extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            int mapid = 0;
            String input = null;
            final MapleMap map = null;
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(splitted[0] + " - \u5173\u95ed\u5730\u56fe");
                return 0;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            }
            catch (Exception ex) {}
            if (c.getChannelServer().getMapFactory().getMap(mapid) == null) {
                c.getPlayer().dropMessage("\u5730\u56fe\u4e0d\u5b58\u5728");
                return 0;
            }
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().destroyMap(mapid, true);
            }
            return 1;
        }
    }
    
    public static class reloadcpq extends CommandExecute
    {
        @Override
        public int execute(final MapleClient c, final String[] splitted) {
            c.getPlayer().getMap().reloadCPQ();
            c.getPlayer().dropMessage("\u5609\u5e74\u534e\u5730\u56fe\u66f4\u65b0\u6210\u529f");
            return 1;
        }
    }
}
