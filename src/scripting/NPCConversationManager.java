package scripting;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleStat;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.ItemLoader;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import server.MapleCarnivalChallenge;
import server.MapleCarnivalParty;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.MapleSquad;
import server.MapleStatEffect;
import server.MerchItemPackage;
import server.Randomizer;
import server.SpeedRunner;
import server.StructPotentialItem;
import server.Timer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.maps.AramiaFireWorks;
import server.maps.Event_DojoAgent;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SpeedRunType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.packet.PlayerShopPacket;

public class NPCConversationManager extends AbstractPlayerInteraction
{
    private MapleClient c;
    private int npc;
    private int questid;
    private String getText;
    private byte type;
    private byte lastMsg;
    public boolean pendingDisposal;
    private Invocable iv;
    private int wh;
    public int hyt;
    public int dxs;
    
    public NPCConversationManager(final MapleClient c, final int npc, final int questid, final byte type, final Invocable iv, final int wh) {
        super(c);
        this.lastMsg = -1;
        this.pendingDisposal = false;
        this.wh = 0;
        this.hyt = 0;
        this.dxs = 0;
        this.c = c;
        this.npc = npc;
        this.questid = questid;
        this.type = type;
        this.iv = iv;
        this.wh = wh;
    }
    
    public int getwh() {
        return this.wh;
    }
    
    public Invocable getIv() {
        return this.iv;
    }
    
    public String serverName() {
        return this.c.getChannelServer().getServerName();
    }
    
    public int getNpc() {
        return this.npc;
    }
    
    public int getQuest() {
        return this.questid;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void safeDispose() {
        this.pendingDisposal = true;
    }
    
    public void dispose() {
        NPCScriptManager.getInstance().dispose(this.c);
    }
    
    public void askMapSelection(final String sel) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getMapSelection(this.npc, sel));
        this.lastMsg = 13;
    }
    
    public void sendNext(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendNextS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", type));
        this.lastMsg = 0;
    }
    
    public void sendPrev(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendPrevS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", type));
        this.lastMsg = 0;
    }
    
    public void sendNextPrev(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", (byte)0));
        this.lastMsg = 0;
    }
    
    public void PlayerToNpc(final String text) {
        this.sendNextPrevS(text, (byte)3);
    }
    
    public void sendNextPrevS(final String text) {
        this.sendNextPrevS(text, (byte)3);
    }
    
    public void sendNextPrevS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", type));
        this.lastMsg = 0;
    }
    
    public void sendOk(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", (byte)0));
        this.lastMsg = 0;
    }
    
    public void sendOkS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", type));
        this.lastMsg = 0;
    }
    
    public void sendYesNo(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)1, text, "", (byte)0));
        this.lastMsg = 1;
    }
    
    public void sendYesNoS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)1, text, "", type));
        this.lastMsg = 1;
    }
    
    public void sendAcceptDecline(final String text) {
        this.askAcceptDecline(text);
    }
    
    public void sendAcceptDeclineNoESC(final String text) {
        this.askAcceptDeclineNoESC(text);
    }
    
    public void askAcceptDecline(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)11, text, "", (byte)0));
        this.lastMsg = 11;
    }
    
    public void askAcceptDeclineNoESC(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)12, text, "", (byte)0));
        this.lastMsg = 12;
    }
    
    public void askAvatar(final String text, final int card, final int... args) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkStyle(this.npc, text, card, args));
        this.lastMsg = 7;
    }
    
    public void sendSimple(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNext(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", (byte)0));
        this.lastMsg = 4;
    }
    
    public void sendSimple(final String text, final int speaker) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNext(text);
            return;
        }
        this.getClient().getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", (byte)speaker));
        this.lastMsg = 4;
    }
    
    public void sendSimpleS(final String text, final byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNextS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)4, text, "", type));
        this.lastMsg = 4;
    }
    
    public void sendStyle(final String text, final int caid, final int[] styles) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkStyle(this.npc, text, caid, styles));
        this.lastMsg = 7;
    }
    
    public void sendGetNumber(final String text, final int def, final int min, final int max) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkNum(this.npc, text, def, min, max));
        this.lastMsg = 3;
    }
    
    public void sendGetText(final String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkText(this.npc, text));
        this.lastMsg = 2;
    }
    
    public void setGetText(final String text) {
        this.getText = text;
    }
    
    public String getText() {
        return this.getText;
    }
    
    public void setHair(final int hair) {
        this.getPlayer().setHair(hair);
        this.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        this.getPlayer().equipChanged();
    }
    
    public void setFace(final int face) {
        this.getPlayer().setFace(face);
        this.getPlayer().updateSingleStat(MapleStat.FACE, face);
        this.getPlayer().equipChanged();
    }
    
    public void setSkin(final int color) {
        this.getPlayer().setSkinColor((byte)color);
        this.getPlayer().updateSingleStat(MapleStat.SKIN, color);
        this.getPlayer().equipChanged();
    }
    
    public int setRandomAvatar(final int ticket, final int[] args_all) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)(-1));
        final int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        }
        else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        }
        else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    
    public int setAvatar(final int ticket, final int args) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)(-1));
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        }
        else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        }
        else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    
    public void sendStorage() {
        this.c.getPlayer().setConversation(4);
        this.c.getPlayer().getStorage().sendStorage(this.c, this.npc);
    }
    
    public void openShop(final int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(this.c);
    }
    
    public int gainGachaponItem(final int id, final int quantity) {
        return this.gainGachaponItem(id, quantity, this.c.getPlayer().getMap().getStreetName() + " - " + this.c.getPlayer().getMap().getMapName());
    }
    
    public int gainGachaponItem(final int id, final int quantity, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final IItem item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + this.c.getPlayer().getName(), " : \u5927\u5bb6\u4e00\u8d77\u606d\u559c\u4ed6\uff08\u5979\uff09\u5427\uff01\uff01\uff01", item, rareness, this.getPlayer().getClient().getChannel()).getBytes());
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int gainGachaponItem(final int id, final int quantity, final String title, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final IItem item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + title + "] " + this.c.getPlayer().getName(), " : " + msg, item, rareness, this.getPlayer().getClient().getChannel()).getBytes());
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int gainGachaponItem(final int id, final int quantity, final String msg, final int \u6982\u7387) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final IItem item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            if (\u6982\u7387 > 0) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + this.c.getPlayer().getName(), " : \u5927\u5bb6\u4e00\u8d77\u606d\u559c\u4ed6\uff08\u5979\uff09\u5427\uff01\uff01\uff01", item, (byte)0, this.getPlayer().getClient().getChannel()).getBytes());
            }
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void changeJob(final int job) {
        this.c.getPlayer().changeJob(job);
    }
    
    public void startQuest(final int id) {
        MapleQuest.getInstance(id).start(this.getPlayer(), this.npc);
    }
    
    @Override
    public void completeQuest(final int id) {
        MapleQuest.getInstance(id).complete(this.getPlayer(), this.npc);
    }
    
    public void forfeitQuest(final int id) {
        MapleQuest.getInstance(id).forfeit(this.getPlayer());
    }
    
    public void forceStartQuest() {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), null);
    }
    
    @Override
    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(this.getPlayer(), this.getNpc(), null);
    }
    
    public void forceStartQuest(final String customData) {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), customData);
    }
    
    public void completeQuest() {
        this.forceCompleteQuest();
    }
    
    public void forceCompleteQuest() {
        MapleQuest.getInstance(this.questid).forceComplete(this.getPlayer(), this.getNpc());
    }
    
    @Override
    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(this.getPlayer(), this.getNpc());
    }
    
    public String getQuestCustomData() {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).getCustomData();
    }
    
    public void setQuestCustomData(final String customData) {
        this.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).setCustomData(customData);
    }
    
    public int getLevel() {
        return this.getPlayer().getLevel();
    }
    
    public int getMeso() {
        return this.getPlayer().getMeso();
    }
    
    public void gainAp(final int amount) {
        this.c.getPlayer().gainAp((short)amount);
    }
    
    public void expandInventory(final byte type, final int amt) {
        this.c.getPlayer().expandInventory(type, amt);
    }
    
    public void unequipEverything() {
        final MapleInventory equipped = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        final MapleInventory equip = this.getPlayer().getInventory(MapleInventoryType.EQUIP);
        final List<Short> ids = new LinkedList<Short>();
        for (final IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (final short id : ids) {
            MapleInventoryManipulator.unequip(this.getC(), id, equip.getNextFreeSlot());
        }
    }
    
    public final void clearSkills() {
        try {
            final Map<ISkill, SkillEntry> skills = this.getPlayer().getSkills();
            for (final Map.Entry<ISkill, SkillEntry> entry : skills.entrySet()) {
                this.getPlayer().changeSkillLevel2(entry.getKey(), (byte)0, (byte)0, entry.getKey().isTimeLimited() ? (System.currentTimeMillis() + 2592000000L) : -1L);
            }
            this.getPlayer().removeSkills();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasSkill(final int skillid) {
        final ISkill theSkill = SkillFactory.getSkill(skillid);
        return theSkill != null && this.c.getPlayer().getSkillLevel(theSkill) > 0;
    }
    
    public void showEffect(final boolean broadcast, final String effect) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
        }
        else {
            this.c.getSession().write((Object)MaplePacketCreator.showEffect(effect));
        }
    }
    
    public void playSound(final boolean broadcast, final String sound) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
        }
        else {
            this.c.getSession().write((Object)MaplePacketCreator.playSound(sound));
        }
    }
    
    public void environmentChange(final boolean broadcast, final String env) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, 2));
        }
        else {
            this.c.getSession().write((Object)MaplePacketCreator.environmentChange(env, 2));
        }
    }
    
    public void updateBuddyCapacity(final int capacity) {
        this.c.getPlayer().setBuddyCapacity((byte)capacity);
    }
    
    public int getBuddyCapacity() {
        return this.c.getPlayer().getBuddyCapacity();
    }
    
    public int partyMembersInMap() {
        int inMap = 0;
        for (final MapleCharacter char2 : this.getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() == this.getPlayer().getParty()) {
                ++inMap;
            }
        }
        return inMap;
    }
    
    public List<MapleCharacter> getPartyMembers() {
        if (this.getPlayer().getParty() == null) {
            return null;
        }
        final List<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            for (final ChannelServer channel : ChannelServer.getAllInstances()) {
                final MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) {
                    chars.add(ch);
                }
            }
        }
        return chars;
    }
    
    public void warpPartyWithExp(final int mapId, final int exp) {
        final MapleMap target = this.getMap(mapId);
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }
    
    public void warpPartyWithExpMeso(final int mapId, final int exp, final int meso) {
        final MapleMap target = this.getMap(mapId);
        for (final MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }
    
    public MapleSquad getSquad(final String type) {
        return this.c.getChannelServer().getMapleSquad(type);
    }
    
    public int getSquadAvailability(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }
    
    public boolean registerSquad(final String type, final int minutes, final String startText) {
        if (this.c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(this.c.getChannel(), type, this.c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = this.c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = this.c.getPlayer().getMap();
                map.broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.c.getPlayer().getName() + startText));
            }
            else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }
    
    public boolean getSquadList(final String type, final byte type_) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return false;
        }
        if (type_ == 0 || type_ == 3) {
            this.sendNext(squad.getSquadMemberString(type_));
        }
        else if (type_ == 1) {
            this.sendSimple(squad.getSquadMemberString(type_));
        }
        else if (type_ == 2) {
            if (squad.getBannedMemberSize() > 0) {
                this.sendSimple(squad.getSquadMemberString(type_));
            }
            else {
                this.sendNext(squad.getSquadMemberString(type_));
            }
        }
        return true;
    }
    
    public byte isSquadLeader(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getLeader() != null && squad.getLeader().getId() == this.c.getPlayer().getId()) {
            return 1;
        }
        return 0;
    }
    
    public boolean reAdd(final String eim, final String squad) {
        final EventInstanceManager eimz = this.getDisconnected(eim);
        final MapleSquad squadz = this.getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(this.getPlayer());
            eimz.registerPlayer(this.getPlayer());
            return true;
        }
        return false;
    }
    
    public void banMember(final String type, final int pos) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }
    
    public void acceptMember(final String type, final int pos) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }
    
    public String getReadableMillis(final long startMillis, final long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }
    
    public int addMember(final String type, final boolean join) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.addMember(this.c.getPlayer(), join);
        }
        return -1;
    }
    
    public byte isSquadMember(final String type) {
        final MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getMembers().contains(this.c.getPlayer())) {
            return 1;
        }
        if (squad.isBanned(this.c.getPlayer())) {
            return 2;
        }
        return 0;
    }
    
    public void resetReactors() {
        this.getPlayer().getMap().resetReactors();
    }
    
    public void genericGuildMessage(final int code) {
        this.c.getSession().write((Object)MaplePacketCreator.genericGuildMessage((byte)code));
    }
    
    public void disbandGuild() {
        final int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0 || this.c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }
    
    public void increaseGuildCapacity() {
        if (this.c.getPlayer().getMeso() < 2500000) {
            this.c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "You do not have enough mesos."));
            return;
        }
        final int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        World.Guild.increaseGuildCapacity(gid);
        this.c.getPlayer().gainMeso(-2500000, true, false, true);
    }
    
    public void displayGuildRanks() {
        this.c.getSession().write((Object)MaplePacketCreator.showGuildRanks(this.npc, MapleGuildRanking.getInstance().getGuildRank()));
    }
    
    public boolean removePlayerFromInstance() {
        if (this.c.getPlayer().getEventInstance() != null) {
            this.c.getPlayer().getEventInstance().removePlayer(this.c.getPlayer());
            return true;
        }
        return false;
    }
    
    public boolean isPlayerInstance() {
        return this.c.getPlayer().getEventInstance() != null;
    }
    
    public void changeStat(final byte slot, final int type, final short amount) {
        final Equip sel = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0: {
                sel.setStr(amount);
                break;
            }
            case 1: {
                sel.setDex(amount);
                break;
            }
            case 2: {
                sel.setInt(amount);
                break;
            }
            case 3: {
                sel.setLuk(amount);
                break;
            }
            case 4: {
                sel.setHp(amount);
                break;
            }
            case 5: {
                sel.setMp(amount);
                break;
            }
            case 6: {
                sel.setWatk(amount);
                break;
            }
            case 7: {
                sel.setMatk(amount);
                break;
            }
            case 8: {
                sel.setWdef(amount);
                break;
            }
            case 9: {
                sel.setMdef(amount);
                break;
            }
            case 10: {
                sel.setAcc(amount);
                break;
            }
            case 11: {
                sel.setAvoid(amount);
                break;
            }
            case 12: {
                sel.setHands(amount);
                break;
            }
            case 13: {
                sel.setSpeed(amount);
                break;
            }
            case 14: {
                sel.setJump(amount);
                break;
            }
            case 15: {
                sel.setUpgradeSlots((byte)amount);
                break;
            }
            case 16: {
                sel.setViciousHammer((byte)amount);
                break;
            }
            case 17: {
                sel.setLevel((byte)amount);
                break;
            }
            case 18: {
                sel.setEnhance((byte)amount);
                break;
            }
            case 19: {
                sel.setPotential1(amount);
                break;
            }
            case 20: {
                sel.setPotential2(amount);
                break;
            }
            case 21: {
                sel.setPotential3(amount);
                break;
            }
            case 22: {
                sel.setOwner(this.getText());
                break;
            }
        }
        this.c.getPlayer().equipChanged();
    }
    
    public void killAllMonsters() {
        final MapleMap map = this.c.getPlayer().getMap();
        final double range = Double.POSITIVE_INFINITY;
        for (final MapleMapObject monstermo : map.getMapObjectsInRange(this.c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            final MapleMonster mob = (MapleMonster)monstermo;
            if (mob.getStats().isBoss()) {
                map.killMonster(mob, this.c.getPlayer(), false, false, (byte)1);
            }
        }
    }
    
    public void giveMerchantMesos() {
        long mesos = 0L;
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            }
            else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE hiredmerchants SET mesos = 0 WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        this.c.getPlayer().gainMeso((int)mesos, true);
    }
    
    public void dc() {
        final MapleCharacter victim = this.c.getChannelServer().getPlayerStorage().getCharacterByName(this.c.getPlayer().getName().toString());
        victim.getClient().getSession().close();
        victim.getClient().disconnect(true, false);
    }
    
    public long getMerchantMesos() {
        long mesos = 0L;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            }
            else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        return mesos;
    }
    
    public void openDuey() {
        this.c.getPlayer().setConversation(2);
        this.c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)9, null));
    }
    
    public void openMerchantItemStore() {
        this.c.getPlayer().setConversation(3);
        this.c.getSession().write((Object)PlayerShopPacket.merchItemStore((byte)34));
    }
    
    public void openMerchantItemStore1() {
        final MerchItemPackage pack = loadItemFrom_Database(this.c.getPlayer().getId(), this.c.getPlayer().getAccountID());
        this.c.getSession().write((Object)PlayerShopPacket.merchItemStore_ItemData(pack));
    }
    
    private static final MerchItemPackage loadItemFrom_Database(final int charid, final int accountid) {
        final Connection con = DatabaseConnection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accountid);
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            final int packageid = rs.getInt("PackageId");
            final MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));
            ps.close();
            rs.close();
            final Map<Integer, Pair<IItem, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, charid);
            if (items != null) {
                final List<IItem> iters = new ArrayList<IItem>();
                for (final Pair<IItem, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }
            return pack;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void sendRepairWindow() {
        this.c.getSession().write((Object)MaplePacketCreator.sendRepairWindow(this.npc));
    }
    
    public final int getDojoPoints() {
        return this.c.getPlayer().getDojo();
    }
    
    public final int getDojoRecord() {
        return this.c.getPlayer().getDojoRecord();
    }
    
    public void setDojoRecord(final boolean reset) {
        this.c.getPlayer().setDojoRecord(reset);
    }
    
    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(this.c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(this.c.getPlayer(), party);
    }
    
    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(this.c.getPlayer());
    }
    
    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(this.c.getPlayer());
    }
    
    public final short getKegs() {
        return AramiaFireWorks.getInstance().getKegsPercentage();
    }
    
    public void giveKegs(final int kegs) {
        AramiaFireWorks.getInstance().giveKegs(this.c.getPlayer(), kegs);
    }
    
    public final short getSunshines() {
        return AramiaFireWorks.getInstance().getSunsPercentage();
    }
    
    public void addSunshines(final int kegs) {
        AramiaFireWorks.getInstance().giveSuns(this.c.getPlayer(), kegs);
    }
    
    public final short getDecorations() {
        return AramiaFireWorks.getInstance().getDecsPercentage();
    }
    
    public void addDecorations(final int kegs) {
        try {
            AramiaFireWorks.getInstance().giveDecs(this.c.getPlayer(), kegs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public final MapleInventory getInventory(final int type) {
        return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
    }
    
    public final MapleCarnivalParty getCarnivalParty() {
        return this.c.getPlayer().getCarnivalParty();
    }
    
    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return this.c.getPlayer().getNextCarnivalRequest();
    }
    
    public final MapleCarnivalChallenge getCarnivalChallenge(final MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }
    
    public void setHP(final short hp) {
        this.c.getPlayer().getStat().setHp(hp);
    }
    
    public void maxStats() {
        final List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(2);
        this.c.getPlayer().getStat().setStr((short)32767);
        this.c.getPlayer().getStat().setDex((short)32767);
        this.c.getPlayer().getStat().setInt((short)32767);
        this.c.getPlayer().getStat().setLuk((short)32767);
        this.c.getPlayer().getStat().setMaxHp((short)30000);
        this.c.getPlayer().getStat().setMaxMp((short)30000);
        this.c.getPlayer().getStat().setHp(30000);
        this.c.getPlayer().getStat().setMp(30000);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.DEX, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LUK, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.INT, 32767));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, 30000));
        this.c.getSession().write((Object)MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer().getJob()));
    }
    
    public Pair<String, Map<Integer, String>> getSpeedRun(final String typ) {
        final SpeedRunType type = SpeedRunType.valueOf(typ);
        if (SpeedRunner.getInstance().getSpeedRunData(type) != null) {
            return SpeedRunner.getInstance().getSpeedRunData(type);
        }
        return new Pair<String, Map<Integer, String>>("", new HashMap<Integer, String>());
    }
    
    public boolean getSR(final Pair<String, Map<Integer, String>> ma, final int sel) {
        if (ma.getRight().get(sel) == null || ma.getRight().get(sel).length() <= 0) {
            this.dispose();
            return false;
        }
        this.sendOk(ma.getRight().get(sel));
        return true;
    }
    
    public Equip getEquip(final int itemid) {
        return (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }
    
    public void setExpiration(final Object statsSel, final long expire) {
        if (statsSel instanceof Equip) {
            ((Equip)statsSel).setExpiration(System.currentTimeMillis() + expire * 24L * 60L * 60L * 1000L);
        }
    }
    
    public void setLock(final Object statsSel) {
        if (statsSel instanceof Equip) {
            final Equip eq = (Equip)statsSel;
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            }
            else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }
    
    public boolean addFromDrop(final Object statsSel) {
        if (statsSel instanceof IItem) {
            final IItem it = (IItem)statsSel;
            return MapleInventoryManipulator.checkSpace(this.getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(this.getClient(), it, false);
        }
        return false;
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int offset, final String type) {
        return this.replaceItem(slot, invType, statsSel, offset, type, false);
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int offset, final String type, final boolean takeSlot) {
        final MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        if (inv == null) {
            return false;
        }
        IItem item = this.getPlayer().getInventory(inv).getItem((byte)slot);
        if (item == null || statsSel instanceof IItem) {
            item = (IItem)statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            final Equip eq = (Equip)item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                }
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() - 1));
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() + offset));
            }
            else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte)(eq.getLevel() + offset));
            }
            else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte)(eq.getViciousHammer() + offset));
            }
            else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short)(eq.getStr() + offset));
            }
            else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short)(eq.getDex() + offset));
            }
            else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short)(eq.getInt() + offset));
            }
            else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short)(eq.getLuk() + offset));
            }
            else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short)(eq.getHp() + offset));
            }
            else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short)(eq.getMp() + offset));
            }
            else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short)(eq.getWatk() + offset));
            }
            else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short)(eq.getMatk() + offset));
            }
            else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short)(eq.getWdef() + offset));
            }
            else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short)(eq.getMdef() + offset));
            }
            else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short)(eq.getAcc() + offset));
            }
            else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short)(eq.getAvoid() + offset));
            }
            else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short)(eq.getHands() + offset));
            }
            else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short)(eq.getSpeed() + offset));
            }
            else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short)(eq.getJump() + offset));
            }
            else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            }
            else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration(eq.getExpiration() + offset);
            }
            else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte)(eq.getFlag() + offset));
            }
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            }
            else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(this.getClient(), inv, (short)slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
    }
    
    public boolean replaceItem(final int slot, final int invType, final Object statsSel, final int upgradeSlots) {
        return this.replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }
    
    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }
    
    public void buffGuild(final int buff, final int duration, final String msg) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && this.getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() == this.getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                    }
                }
            }
        }
    }
    
    public boolean createAlliance(final String alliancename) {
        final MapleParty pt = this.c.getPlayer().getParty();
        final MapleCharacter otherChar = this.c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == this.c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, this.c.getPlayer().getId(), otherChar.getId(), this.c.getPlayer().getGuildId(), otherChar.getGuildId());
        }
        catch (Exception re) {
            re.printStackTrace();
            return false;
        }
    }
    
    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && World.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                this.gainMeso(-10000000);
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }
    
    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && World.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }
    
    public byte getLastMsg() {
        return this.lastMsg;
    }
    
    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }
    
    public final void maxAllSkills() {
        for (final ISkill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId())) {
                this.teachSkill(skil.getId(), skil.getMaxLevel(), skil.getMaxLevel());
            }
        }
    }
    
    public final void resetStats(final int str, final int dex, final int z, final int luk) {
        this.c.getPlayer().resetStats(str, dex, z, luk);
    }
    
    public final boolean dropItem(final int slot, final int invType, final int quantity) {
        final MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        return inv != null && MapleInventoryManipulator.drop(this.c, inv, (short)slot, (short)quantity, true);
    }
    
    public final List<Integer> getAllPotentialInfo() {
        return new ArrayList<Integer>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
    }
    
    public final String getPotentialInfo(final int id) {
        final List<StructPotentialItem> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1;
        int maxLevel = 10;
        for (final StructPotentialItem item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }
    
    public final void sendRPS() {
        this.c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
    }
    
    public final void setQuestRecord(final Object ch, final int questid, final String data) {
        ((MapleCharacter)ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }
    
    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter)ch;
        Timer.CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    NPCConversationManager.this.warpMap(680000500, 0);
                }
            }
        }, 10000L);
        Timer.CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    if (NPCConversationManager.this.getPlayer() != null) {
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "3");
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160002, "0");
                    }
                    else if (chr != null) {
                        NPCConversationManager.this.setQuestRecord(chr, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(chr, 160002, "0");
                    }
                    NPCConversationManager.this.warpMap(680000500, 0);
                }
                else {
                    NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "2");
                    NPCConversationManager.this.setQuestRecord(chr, 160001, "2");
                    NPCConversationManager.this.sendNPCText(NPCConversationManager.this.getPlayer().getName() + " and " + chr.getName() + ", I wish you two all the best on your AsteriaSEA journey together!", 9201002);
                    NPCConversationManager.this.getMap().startExtendedMapEffect("You may now kiss the bride, " + NPCConversationManager.this.getPlayer().getName() + "!", 5120006);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), MaplePacketCreator.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (NPCConversationManager.this.getPlayer().getGuildId() > 0) {
                        World.Guild.guildPacket(NPCConversationManager.this.getPlayer().getGuildId(), MaplePacketCreator.sendMarriage(false, NPCConversationManager.this.getPlayer().getName()));
                    }
                    if (NPCConversationManager.this.getPlayer().getFamilyId() > 0) {
                        World.Family.familyPacket(NPCConversationManager.this.getPlayer().getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), NPCConversationManager.this.getPlayer().getId());
                    }
                }
            }
        }, 20000L);
    }
    
    public void openDD(final int type) {
        this.c.getSession().write((Object)MaplePacketCreator.openBeans(this.getPlayer().getBeans(), type));
    }
    
    public void worldMessage(final String text) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, text).getBytes());
    }
    
    public int getBeans() {
        return this.getClient().getPlayer().getBeans();
    }
    
    public void gainBeans(final int s) {
        this.getPlayer().gainBeans(s);
        this.c.getSession().write((Object)MaplePacketCreator.updateBeans(this.c.getPlayer().getId(), s));
    }
    
    public int getHyPay(final int type) {
        return this.getPlayer().getHyPay(type);
    }
    
    public void szhs(final String ss) {
        this.c.getSession().write((Object)MaplePacketCreator.\u6e38\u620f\u5c4f\u5e55\u4e2d\u95f4\u9ec4\u8272\u5b57\u4f53(ss));
    }
    
    public void szhs(final String ss, final int id) {
        this.c.getSession().write((Object)MaplePacketCreator.\u6e38\u620f\u5c4f\u5e55\u4e2d\u95f4\u9ec4\u8272\u5b57\u4f53(ss, id));
    }
    
    public int gainHyPay(final int hypay) {
        return this.getPlayer().gainHyPay(hypay);
    }
    
    public int addHyPay(final int hypay) {
        return this.getPlayer().addHyPay(hypay);
    }
    
    public int delPayReward(final int pay) {
        return this.getPlayer().delPayReward(pay);
    }
    
    public int getItemLevel(final int id) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return ii.getReqLevel(id);
    }
    
    public void alatPQ() {
    }
    
    public void xlkc(final long days) {
        final MapleQuestStatus marr = this.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122700));
        if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
            this.getPlayer().dropMessage(1, "\u9879\u94fe\u6269\u5145\u5931\u8d25\uff0c\u60a8\u5df2\u7ecf\u8fdb\u884c\u8fc7\u9879\u94fe\u6269\u5145\u3002");
        }
        else {
            final String customData = String.valueOf(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
            this.getPlayer().getQuestNAdd(MapleQuest.getInstance(122700)).setCustomData(customData);
            this.getPlayer().dropMessage(1, "\u9879\u94fe" + days + "\u6269\u5145\u6269\u5145\u6210\u529f\uff01");
        }
    }
    
    public String checkDrop(final int mobId) {
        int rate = this.getClient().getChannelServer().getDropRate();
        final MapleMonster mob = MapleLifeFactory.getMonster(mobId);
        if (MapleLifeFactory.getMonster(mobId) != null && mob.getStats().isBoss()) {
            rate = this.getClient().getChannelServer().getBossDropRate();
        }
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0;
            int itemId = 0;
            int ch = 0;
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); ++i) {
                final MonsterDropEntry de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (ii.itemExists(itemId)) {
                        if (num == 0) {
                            name.append("\u5f53\u524d\u602a\u7269 #o").append(mobId).append("# \u7684\u7206\u7387\u4e3a:\r\n");
                            name.append("--------------------------------------\r\n");
                        }
                        String namez = "#z" + itemId + "#";
                        if (itemId == 0) {
                            itemId = 4031041;
                            namez = de.Minimum * this.getClient().getChannelServer().getMesoRate() + " - " + de.Maximum * this.getClient().getChannelServer().getMesoRate() + " \u7684\u91d1\u5e01";
                        }
                        ch = de.chance * rate;
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append(" - ").append(((ch >= 999999) ? 1000000 : ch) / 10000.0).append("%\u7684\u7206\u7387. ").append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("\u9700\u8981\u63a5\u53d7\u4efb\u52a1: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                        ++num;
                    }
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return "\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u602a\u7269\u7684\u7206\u7387\u6570\u636e\u3002";
    }
    
    public String checkMapDrop() {
        final List<MonsterGlobalDropEntry> ranks = new ArrayList(MapleMonsterInformationProvider.getInstance().getGlobalDrop());
        final int mapid = this.c.getPlayer().getMap().getId();
        final int cashServerRate = this.getClient().getChannelServer().getCashRate();
        final int globalServerRate = 1;
        if (ranks != null && ranks.size() > 0) {
            int num = 0;
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); ++i) {
                final MonsterGlobalDropEntry de = ranks.get(i);
                if (de.continent < 0 || (de.continent < 10 && mapid / 100000000 == de.continent) || (de.continent < 100 && mapid / 10000000 == de.continent) || (de.continent < 1000 && mapid / 1000000 == de.continent)) {
                    int itemId = de.itemId;
                    if (num == 0) {
                        name.append("\u5f53\u524d\u5730\u56fe #r").append(mapid).append("#k - #m").append(mapid).append("# \u7684\u5168\u5c40\u7206\u7387\u4e3a:");
                        name.append("\r\n--------------------------------------\r\n");
                    }
                    String names = "#z" + itemId + "#";
                    if (itemId == 0 && cashServerRate != 0) {
                        itemId = 4031041;
                        names = de.Minimum * cashServerRate + " - " + de.Maximum * cashServerRate + " \u7684\u62b5\u7528\u5377";
                    }
                    final int chance = de.chance * globalServerRate;
                    if (this.getPlayer().isAdmin()) {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(names).append(" - ").append(((chance >= 999999) ? 1000000 : chance) / 10000.0).append("%\u7684\u7206\u7387. ").append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("\u9700\u8981\u63a5\u53d7\u4efb\u52a1: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                    }
                    else {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(names).append((de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0) ? ("\u9700\u8981\u63a5\u53d7\u4efb\u52a1: " + MapleQuest.getInstance(de.questid).getName()) : "").append("\r\n");
                    }
                    ++num;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return "\u5f53\u524d\u5730\u56fe\u6ca1\u6709\u8bbe\u7f6e\u5168\u5c40\u7206\u7387\u3002";
    }
    
    public int getzb() {
        int money = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
            final ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                money = rs.getInt("money");
            }
            limitCheck.close();
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return money;
    }
    
    public void setzb(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            try (final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET money =money+ " + slot + " WHERE id = " + cid + "")) {
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public int getmoneyb() {
        int moneyb = 0;
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (final PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "")) {
                rs = limitCheck.executeQuery();
                if (rs.next()) {
                    moneyb = rs.getInt("moneyb");
                }
            }
            rs.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
        return moneyb;
    }
    
    public void setmoneyb(final int slot) {
        try {
            final int cid = this.getPlayer().getAccountID();
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("UPDATE accounts SET moneyb =moneyb+ " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            ex.getStackTrace();
        }
    }
    
    public MapleMapFactory getMapFactory() {
        return this.getClient().getChannelServer().getMapFactory();
    }
    
    public void warpBack(final int mid, final int retmap, final int time) {
        final MapleMap warpMap = this.c.getChannelServer().getMapFactory().getMap(mid);
        this.c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
        this.c.sendPacket(MaplePacketCreator.getClock(time));
        Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                final MapleMap warpMap = NPCConversationManager.this.c.getChannelServer().getMapFactory().getMap(retmap);
                if (NPCConversationManager.this.c.getPlayer() != null) {
                    NPCConversationManager.this.c.sendPacket(MaplePacketCreator.stopClock());
                    NPCConversationManager.this.c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
                    NPCConversationManager.this.c.getPlayer().dropMessage(6, "\ue61d\u5230\u8fbe\u76ee\u7684\u5730\u30d8\ue019\ue6c7\ue5ec!");
                }
            }
        }, 1000 * time);
    }
    
    public void warpMapWithClock(final int mid, final int seconds) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(seconds));
        Timer.MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (NPCConversationManager.this.c.getPlayer() != null) {
                    for (final MapleCharacter chr : NPCConversationManager.this.c.getPlayer().getMap().getCharactersThreadsafe()) {
                        chr.changeMap(mid);
                    }
                }
            }
        }, seconds * 1000);
    }
    
    public void showlvl() {
        this.c.sendPacket(MaplePacketCreator.showlevelRanks(this.npc, MapleGuildRanking.getInstance().getLevelRank()));
    }
    
    public void showmeso() {
        this.c.sendPacket(MaplePacketCreator.showmesoRanks(this.npc, MapleGuildRanking.getInstance().getMesoRank()));
    }
    
    public void ShowMarrageEffect() {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendMarrageEffect());
    }
    
    public int getHour() {
        return Calendar.getInstance().get(11);
    }
    
    public int getMin() {
        return Calendar.getInstance().get(12);
    }
    
    public int getSec() {
        return Calendar.getInstance().get(13);
    }
    
    public static String getWeekOfDate() {
        final Date date = new Date();
        final SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        return dateFm.format(date);
    }
    
    public int gethour() {
        final Calendar cal = Calendar.getInstance();
        final int hour = cal.get(11);
        return hour;
    }
    
    public int getmin() {
        final Calendar cal = Calendar.getInstance();
        final int min = cal.get(12);
        return min;
    }
    
    public int getsec() {
        final Calendar cal = Calendar.getInstance();
        final int sec = cal.get(13);
        return sec;
    }
    
    public int getMount(final int s) {
        return GameConstants.getMountS(s);
    }
    
    public int gethyt() {
        return this.hyt;
    }
    
    public void sethyt(final int a) {
        this.hyt = a;
    }
    
    public void \u4eba\u6c14\u6392\u884c\u699c() {
        MapleGuild.\u4eba\u6c14\u6392\u884c(this.getClient(), this.npc);
    }
    
    public void deleteItem(final int inventorytype) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("Select * from inventoryitems where characterid=? and inventorytype=?");
            ps.setInt(1, this.getPlayer().getId());
            ps.setInt(2, inventorytype);
            final ResultSet re = ps.executeQuery();
            MapleInventoryType type = null;
            switch (inventorytype) {
                case 1: {
                    type = MapleInventoryType.EQUIP;
                    break;
                }
                case 2: {
                    type = MapleInventoryType.USE;
                    break;
                }
                case 3: {
                    type = MapleInventoryType.SETUP;
                    break;
                }
                case 4: {
                    type = MapleInventoryType.ETC;
                    break;
                }
                case 5: {
                    type = MapleInventoryType.CASH;
                    break;
                }
            }
            while (re.next()) {
                MapleInventoryManipulator.removeById(this.getC(), type, re.getInt("itemid"), 1, true, true);
            }
            re.close();
            ps.close();
        }
        catch (SQLException ex) {}
    }
    
    public void \u5587\u53ed(final int lx, final String msg) throws RemoteException {
        final StringBuilder sb = new StringBuilder();
        addMedalString(this.c.getPlayer(), sb);
        sb.append(this.c.getPlayer().getName());
        sb.append(" : ");
        sb.append(msg);
        switch (lx) {
            case 1: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(11, this.c.getChannel(), sb.toString()).getBytes());
                break;
            }
            case 2: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(12, this.c.getChannel(), sb.toString(), true).getBytes());
                break;
            }
            case 3: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, this.c.getChannel(), sb.toString()).getBytes());
                break;
            }
        }
    }
    
    public void \u5587\u53ed2(final int lx, final String title, final String msg) throws RemoteException {
        switch (lx) {
            case 1: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(11, this.c.getChannel(), "[" + title + "]" + this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
            case 2: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(12, this.c.getChannel(), "[" + title + "]" + this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
            case 3: {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, this.c.getChannel(), "[" + title + "]" + this.c.getPlayer().getName() + " : " + msg).getBytes());
                break;
            }
        }
    }
    
    private static final void addMedalString(final MapleCharacter c, final StringBuilder sb) {
        final IItem medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-26));
        if (medal != null) {
            sb.append("<");
            sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            sb.append("> ");
        }
    }
    
    public void \u516c\u544a(final String msg) {
        for (final ChannelServer cserv1 : ChannelServer.getAllInstances()) {
            for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                mch.startMapEffect(msg, 5121009);
            }
        }
    }
}
