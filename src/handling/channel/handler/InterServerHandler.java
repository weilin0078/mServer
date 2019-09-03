package handling.channel.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.BuddyEntry;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import constants.GameConstants;
import handling.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.guild.MapleGuild;
import scripting.NPCScriptManager;
import server.ServerProperties;
import server.maps.FieldLimitType;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.FamilyPacket;

public class InterServerHandler
{
    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        try {
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SUMMON) != null) {
                c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            }
            c.getPlayer().saveToDB(false, false);
            final String[] socket = c.getChannelServer().getIP().split(":");
            final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
            chr.changeRemoval();
            if (chr.getMessenger() != null) {
                final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }
            PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
            PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
            ch.removePlayer(chr);
            c.updateLoginState(6, c.getSessionIPAddress());
            c.getSession().write((Object)MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(CashShopServer.getIP().split(":")[1])));
            chr.saveToDB(false, false);
            chr.getMap().removePlayer(chr);
            c.getPlayer().expirationTask(true, false);
            c.setPlayer(null);
            c.setReceiving(false);
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(InterServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static final void EnterMTS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        final String[] socket = c.getChannelServer().getIP().split(":");
        if (c.getPlayer().getTrade() != null) {
            c.getPlayer().dropMessage(1, "\u4ea4\u6613\u4e2d\u65e0\u6cd5\u8fdb\u884c\u5176\u4ed6\u64cd\u4f5c\uff01");
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (!chr.isGM() || chr.isGM()) {
            NPCScriptManager.getInstance().start(c, 9900004);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
        else {
            try {
                final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
                chr.changeRemoval();
                if (chr.getMessenger() != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                    World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
                }
                PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
                PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
                PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
                World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
                ch.removePlayer(chr);
                c.updateLoginState(6, c.getSessionIPAddress());
                c.getSession().write((Object)MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(CashShopServer.getIP().split(":")[1])));
                chr.saveToDB(false, false);
                chr.getMap().removePlayer(chr);
                c.setPlayer(null);
                c.setReceiving(false);
            }
            catch (UnknownHostException ex) {
                Logger.getLogger(InterServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void Loggedin(final int playerid, final MapleClient c) {
        if (!GameConstants.\u7ed1\u5b9aIP.equals(ServerProperties.getProperty("tms.IP"))) {}
        final ChannelServer channelServer = c.getChannelServer();
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);
        MapleCharacter player;
        if (transfer == null) {
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        }
        else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }
        c.setPlayer(player);
        c.setAccID(player.getAccountID());
        c.loadAccountData(player.getAccountID());
        ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
        final int state = c.getLoginState();
        boolean allowLogin = true;
        if ((state == 1 || state == 6) && !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            System.out.print("\u81ea\u52a8\u65ad\u5f00\u8fde\u63a52");
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        channelServer.addPlayer(player);
        c.getSession().write((Object)MaplePacketCreator.getCharInfo(player));
        if (player.isGM()) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        }
        c.getSession().write((Object)MaplePacketCreator.temporaryStats_Reset());
        player.getMap().addPlayer(player);
        try {
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
            final Collection<Integer> buddyIds = player.getBuddylist().getBuddiesIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                World.Party.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            final CharacterIdChannelPair[] arr$;
            final CharacterIdChannelPair[] onlineBuddies = arr$ = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (final CharacterIdChannelPair onlineBuddy : arr$) {
                final BuddyEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write((Object)MaplePacketCreator.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<MaplePacket> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (final MaplePacket pack : packetList) {
                            if (pack != null) {
                                c.getSession().write((Object)pack);
                            }
                        }
                    }
                }
            }
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.getSession().write((Object)FamilyPacket.getFamilyInfo(player));
        }
        catch (Exception e) {
            FileoutputUtil.outputFileError("Logs/Log_\u767b\u5f55\u9519\u8bef.rtf", e);
        }
        c.getSession().write((Object)FamilyPacket.getFamilyData());
        player.sendMacros();
        player.showNote();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.updatePetEquip();
        player.baseSkills();
        c.getSession().write((Object)MaplePacketCreator.getKeymap(player.getKeyLayout()));
        for (final MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.getSession().write((Object)MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        final BuddyEntry pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddyEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getCharacterId(), "ETC", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
            c.sendPacket(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        if (player.getJob() == 132) {
            player.checkBerserk();
        }
        player.spawnClones();
        player.getHyPay(1);
        player.spawnSavedPets();
        c.getSession().write((Object)MaplePacketCreator.showCharCash(c.getPlayer()));
        System.out.println("[\u5192\u9669\u5c9b][\u540d\u5b57:" + c.getPlayer().getName() + "][\u7b49\u7ea7:" + c.getPlayer().getLevel() + "][IP:" + c.getSessionIPAddress() + "]\u767b\u5f55.");
        c.getSession().write((Object)MaplePacketCreator.weirdStatUpdate());
    }
    
    public static final void ChangeChannel(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer().getTrade() != null || !chr.isAlive() || chr.getEventInstance() != null || chr.getMap() == null || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        chr.expirationTask();
        chr.changeChannel(slea.readByte() + 1);
    }
}
