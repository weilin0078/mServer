package handling.channel;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferAllocator;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import client.MapleCharacter;
import client.MapleClient;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.login.LoginServer;
import handling.mina.MapleCodecFactory;
import handling.world.CheaterData;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.ServerProperties;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import tools.CollectionUtil;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;

public class ChannelServer implements Serializable
{
    public static long serverStartTime;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private int cashRate;
    private int BossdropRate;
    private int doubleExp;
    private int doubleMeso;
    private int doubleDrop;
    private short port;
    private static final short DEFAULT_PORT = 7574;
    private int channel;
    private int running_MerchantID;
    private int flags;
    private String serverMessage;
    private String key;
    private String ip;
    private String serverName;
    private boolean shutdown;
    private boolean finishedShutdown;
    private boolean MegaphoneMuteState;
    private boolean adminOnly;
    private PlayerStorage players;
    private MapleServerHandler serverHandler;
    private IoAcceptor acceptor;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static final Map<Integer, ChannelServer> instances;
    private final Map<MapleSquad.MapleSquadType, MapleSquad> mapleSquads;
    private final Map<Integer, HiredMerchant> merchants;
    private final Map<Integer, PlayerNPC> playerNPCs;
    private final ReentrantReadWriteLock merchLock;
    private final ReentrantReadWriteLock squadLock;
    private int eventmap;
    private final Map<MapleEventType, MapleEvent> events;
    private boolean debugMode;
    private int instanceId;
    
    private ChannelServer(final int channel) {
        this.BossdropRate = 1;
        this.doubleExp = 1;
        this.doubleMeso = 1;
        this.doubleDrop = 1;
        this.port = 7574;
        this.running_MerchantID = 0;
        this.flags = 0;
        this.shutdown = false;
        this.finishedShutdown = false;
        this.MegaphoneMuteState = false;
        this.adminOnly = false;
        this.mapleSquads = new ConcurrentEnumMap<MapleSquad.MapleSquadType, MapleSquad>(MapleSquad.MapleSquadType.class);
        this.merchants = new HashMap<Integer, HiredMerchant>();
        this.playerNPCs = new HashMap<Integer, PlayerNPC>();
        this.merchLock = new ReentrantReadWriteLock();
        this.squadLock = new ReentrantReadWriteLock();
        this.eventmap = -1;
        this.events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);
        this.debugMode = false;
        this.instanceId = 0;
        this.channel = channel;
        this.mapFactory = new MapleMapFactory(channel);
    }
    
    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(ChannelServer.instances.keySet());
    }
    
    public final void loadEvents() {
        if (this.events.size() != 0) {
            return;
        }
        this.events.put(MapleEventType.\u6253\u6930\u5b50\u6bd4\u8d5b, new MapleCoconut(this.channel, MapleEventType.\u6253\u6930\u5b50\u6bd4\u8d5b.mapids));
        this.events.put(MapleEventType.\u6253\u74f6\u76d6\u6bd4\u8d5b, new MapleCoconut(this.channel, MapleEventType.\u6253\u74f6\u76d6\u6bd4\u8d5b.mapids));
        this.events.put(MapleEventType.\u5411\u9ad8\u5730, new MapleFitness(this.channel, MapleEventType.\u5411\u9ad8\u5730.mapids));
        this.events.put(MapleEventType.\u4e0a\u697c\u4e0a\u697c, new MapleOla(this.channel, MapleEventType.\u4e0a\u697c\u4e0a\u697c.mapids));
        this.events.put(MapleEventType.\u5feb\u901f0X\u731c\u9898, new MapleOxQuiz(this.channel, MapleEventType.\u5feb\u901f0X\u731c\u9898.mapids));
        this.events.put(MapleEventType.\u96ea\u7403\u8d5b, new MapleSnowball(this.channel, MapleEventType.\u96ea\u7403\u8d5b.mapids));
    }
    
    public final void run_startup_configurations() {
        this.setChannel(this.channel);
        try {
            this.expRate = Integer.parseInt(ServerProperties.getProperty("tms.Exp"));
            this.mesoRate = Integer.parseInt(ServerProperties.getProperty("tms.Meso"));
            this.dropRate = Integer.parseInt(ServerProperties.getProperty("tms.Drop"));
            this.BossdropRate = Integer.parseInt(ServerProperties.getProperty("tms.BDrop"));
            this.cashRate = Integer.parseInt(ServerProperties.getProperty("tms.Cash"));
            this.serverMessage = ServerProperties.getProperty("tms.ServerMessage");
            this.serverName = ServerProperties.getProperty("tms.ServerName");
            this.flags = Integer.parseInt(ServerProperties.getProperty("tms.WFlags", "0"));
            this.adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"));
            this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("tms.Events").split(","));
            this.port = Short.parseShort(ServerProperties.getProperty("tms.Port" + this.channel, String.valueOf(7574 + this.channel)));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.ip = ServerProperties.getProperty("tms.IP") + ":" + this.port;
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator((IoBufferAllocator)new SimpleBufferAllocator());
        this.acceptor = (IoAcceptor)new NioSocketAcceptor();
        this.acceptor.getFilterChain().addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new MapleCodecFactory()));
        this.players = new PlayerStorage(this.channel);
        this.loadEvents();
        try {
            this.acceptor.setHandler((IoHandler)new MapleServerHandler(this.channel, false));
            this.acceptor.bind((SocketAddress)new InetSocketAddress(this.port));
            ((SocketSessionConfig)this.acceptor.getSessionConfig()).setTcpNoDelay(true);
            System.out.println("\u9891\u9053 " + this.channel + ": \u542f\u52a8\u7aef\u53e3 " + this.port + ": \u670d\u52a1\u5668IP " + this.ip + "");
            this.eventSM.init();
        }
        catch (IOException e2) {
            System.out.println("Binding to port " + this.port + " failed (ch: " + this.getChannel() + ")" + e2);
        }
    }
    
    public final void shutdown(final Object threadToNotify) {
        if (this.finishedShutdown) {
            return;
        }
        this.broadcastPacket(MaplePacketCreator.serverNotice(0, "\u8fd9\u4e2a\u9891\u9053\u6b63\u5728\u5173\u95ed\u4e2d."));
        this.shutdown = true;
        System.out.println("Channel " + this.channel + ", Saving hired merchants...");
        System.out.println("Channel " + this.channel + ", Saving characters...");
        System.out.println("Channel " + this.channel + ", Unbinding...");
        ChannelServer.instances.remove(this.channel);
        LoginServer.removeChannel(this.channel);
        this.setFinishShutdown();
    }
    
    public final boolean hasFinishedShutdown() {
        return this.finishedShutdown;
    }
    
    public final MapleMapFactory getMapFactory() {
        return this.mapFactory;
    }
    
    public static final ChannelServer newInstance(final int channel) {
        return new ChannelServer(channel);
    }
    
    public static final ChannelServer getInstance(final int channel) {
        return ChannelServer.instances.get(channel);
    }
    
    public final void addPlayer(final MapleCharacter chr) {
        this.getPlayerStorage().registerPlayer(chr);
        chr.getClient().getSession().write((Object)MaplePacketCreator.serverMessage(this.serverMessage));
    }
    
    public final PlayerStorage getPlayerStorage() {
        if (this.players == null) {
            this.players = new PlayerStorage(this.channel);
        }
        return this.players;
    }
    
    public final void removePlayer(final MapleCharacter chr) {
        this.getPlayerStorage().deregisterPlayer(chr);
    }
    
    public final void removePlayer(final int idz, final String namez) {
        this.getPlayerStorage().deregisterPlayer(idz, namez);
    }
    
    public final String getServerMessage() {
        return this.serverMessage;
    }
    
    public final void setServerMessage(final String newMessage) {
        this.serverMessage = newMessage;
        this.broadcastPacket(MaplePacketCreator.serverMessage(this.serverMessage));
    }
    
    public final void broadcastPacket(final MaplePacket data) {
        this.getPlayerStorage().broadcastPacket(data);
    }
    
    public final void broadcastSmegaPacket(final MaplePacket data) {
        this.getPlayerStorage().broadcastSmegaPacket(data);
    }
    
    public final void broadcastGMPacket(final MaplePacket data) {
        this.getPlayerStorage().broadcastGMPacket(data);
    }
    
    public final int getExpRate() {
        return this.expRate * this.doubleExp;
    }
    
    public final void setExpRate(final int expRate) {
        this.expRate = expRate;
    }
    
    public final int getCashRate() {
        return this.cashRate;
    }
    
    public final void setCashRate(final int cashRate) {
        this.cashRate = cashRate;
    }
    
    public final int getChannel() {
        return this.channel;
    }
    
    public final void setChannel(final int channel) {
        ChannelServer.instances.put(channel, this);
        LoginServer.addChannel(channel);
    }
    
    public static final Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection((Collection<? extends ChannelServer>)ChannelServer.instances.values());
    }
    
    public final String getSocket() {
        return this.ip;
    }
    
    public final String getIP() {
        return this.ip;
    }
    
    public String getIPA() {
        return this.ip;
    }
    
    public final boolean isShutdown() {
        return this.shutdown;
    }
    
    public final int getLoadedMaps() {
        return this.mapFactory.getLoadedMaps();
    }
    
    public final EventScriptManager getEventSM() {
        return this.eventSM;
    }
    
    public final void reloadEvents() {
        this.eventSM.cancel();
        (this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("tms.Events").split(","))).init();
    }
    
    public final int getBossDropRate() {
        return this.BossdropRate;
    }
    
    public final void setBossDropRate(final int dropRate) {
        this.BossdropRate = dropRate;
    }
    
    public final int getMesoRate() {
        return this.mesoRate * this.doubleMeso;
    }
    
    public final void setMesoRate(final int mesoRate) {
        this.mesoRate = mesoRate;
    }
    
    public final int getDropRate() {
        return this.dropRate * this.doubleDrop;
    }
    
    public final void setDropRate(final int dropRate) {
        this.dropRate = dropRate;
    }
    
    public int getDoubleExp() {
        if (this.doubleExp < 0 || this.doubleExp > 2) {
            return 1;
        }
        return this.doubleExp;
    }
    
    public void setDoubleExp(final int doubleExp) {
        if (doubleExp < 0 || doubleExp > 2) {
            this.doubleExp = 1;
        }
        else {
            this.doubleExp = doubleExp;
        }
    }
    
    public int getDoubleMeso() {
        if (this.doubleMeso < 0 || this.doubleMeso > 2) {
            return 1;
        }
        return this.doubleMeso;
    }
    
    public void setDoubleMeso(final int doubleMeso) {
        if (doubleMeso < 0 || doubleMeso > 2) {
            this.doubleMeso = 1;
        }
        else {
            this.doubleMeso = doubleMeso;
        }
    }
    
    public int getDoubleDrop() {
        if (this.doubleDrop < 0 || this.doubleDrop > 2) {
            return 1;
        }
        return this.doubleDrop;
    }
    
    public void setDoubleDrop(final int doubleDrop) {
        if (doubleDrop < 0 || doubleDrop > 2) {
            this.doubleDrop = 1;
        }
        else {
            this.doubleDrop = doubleDrop;
        }
    }
    
    public static void startChannel_Main() {
        ChannelServer.serverStartTime = System.currentTimeMillis();
        int ch = Integer.parseInt(ServerProperties.getProperty("tms.Count", "0"));
        if (ch > 10) {
            ch = 10;
        }
        for (int i = 0; i < ch; ++i) {
            newInstance(i + 1).run_startup_configurations();
        }
    }
    
    public static final void startChannel(final int channel) {
        ChannelServer.serverStartTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("tms.Count", "0")); ++i) {
            if (channel == i + 1) {
                newInstance(i + 1).run_startup_configurations();
                break;
            }
        }
    }
    
    public Map<MapleSquad.MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap((Map<? extends MapleSquad.MapleSquadType, ? extends MapleSquad>)this.mapleSquads);
    }
    
    public final MapleSquad getMapleSquad(final String type) {
        return this.getMapleSquad(MapleSquad.MapleSquadType.valueOf(type.toLowerCase()));
    }
    
    public final MapleSquad getMapleSquad(final MapleSquad.MapleSquadType type) {
        return this.mapleSquads.get(type);
    }
    
    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquad.MapleSquadType types = MapleSquad.MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !this.mapleSquads.containsKey(types)) {
            this.mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }
    
    public boolean removeMapleSquad(final MapleSquad squad, final MapleSquad.MapleSquadType type) {
        if (type != null && this.mapleSquads.containsKey(type) && this.mapleSquads.get(type) == squad) {
            this.mapleSquads.remove(type);
            return true;
        }
        return false;
    }
    
    public final boolean removeMapleSquad(final MapleSquad.MapleSquadType types) {
        if (types != null && this.mapleSquads.containsKey(types)) {
            this.mapleSquads.remove(types);
            return true;
        }
        return false;
    }
    
    public int closeAllMerchant() {
        int ret = 0;
        this.merchLock.writeLock().lock();
        try {
            final Iterator merchants_ = this.merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                final HiredMerchant hm = (HiredMerchant) ((Map.Entry)merchants_.next()).getValue();
                hm.closeShop(true, false);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ++ret;
            }
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
        return ret;
    }
    
    public final int addMerchant(final HiredMerchant hMerchant) {
        this.merchLock.writeLock().lock();
        int runningmer = 0;
        try {
            runningmer = this.running_MerchantID;
            this.merchants.put(this.running_MerchantID, hMerchant);
            ++this.running_MerchantID;
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
        return runningmer;
    }
    
    public final void removeMerchant(final HiredMerchant hMerchant) {
        this.merchLock.writeLock().lock();
        try {
            this.merchants.remove(hMerchant.getStoreId());
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
    }
    
    public final boolean containsMerchant(final int accid) {
        boolean contains = false;
        this.merchLock.readLock().lock();
        try {
            final Iterator itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                if (((HiredMerchant)itr.next()).getOwnerAccId() == accid) {
                    contains = true;
                    break;
                }
            }
        }
        finally {
            this.merchLock.readLock().unlock();
        }
        return contains;
    }
    
    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<HiredMerchant>();
        this.merchLock.readLock().lock();
        try {
            for (final HiredMerchant hm : this.merchants.values()) {
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        }
        finally {
            this.merchLock.readLock().unlock();
        }
        return list;
    }
    
    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }
    
    public final boolean getMegaphoneMuteState() {
        return this.MegaphoneMuteState;
    }
    
    public int getEvent() {
        return this.eventmap;
    }
    
    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }
    
    public MapleEvent getEvent(final MapleEventType t) {
        return this.events.get(t);
    }
    
    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return this.playerNPCs.values();
    }
    
    public final PlayerNPC getPlayerNPC(final int id) {
        return this.playerNPCs.get(id);
    }
    
    public final void addPlayerNPC(final PlayerNPC npc) {
        if (this.playerNPCs.containsKey(npc.getId())) {
            this.removePlayerNPC(npc);
        }
        this.playerNPCs.put(npc.getId(), npc);
        this.getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }
    
    public final void removePlayerNPC(final PlayerNPC npc) {
        if (this.playerNPCs.containsKey(npc.getId())) {
            this.playerNPCs.remove(npc.getId());
            this.getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }
    
    public final String getServerName() {
        return this.serverName;
    }
    
    public final void setServerName(final String sn) {
        this.serverName = sn;
    }
    
    public final int getPort() {
        return this.port;
    }
    
    public static final Set<Integer> getChannelServer() {
        return new HashSet<Integer>(ChannelServer.instances.keySet());
    }
    
    public final void setShutdown() {
        this.shutdown = true;
        System.out.println("\u9891\u9053 " + this.channel + " \u5df2\u5f00\u59cb\u5173\u95ed.");
    }
    
    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("\u9891\u9053 " + this.channel + " \u5df2\u5173\u95ed\u5b8c\u6210.");
    }
    
    public final boolean isAdminOnly() {
        return this.adminOnly;
    }
    
    public static final int getChannelCount() {
        return ChannelServer.instances.size();
    }
    
    public final MapleServerHandler getServerHandler() {
        return this.serverHandler;
    }
    
    public final int getTempFlag() {
        return this.flags;
    }
    
    public static Map<Integer, Integer> getChannelLoad() {
        final Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (final ChannelServer cs : ChannelServer.instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }
    
    public int getConnectedClients() {
        return this.getPlayerStorage().getConnectedClients();
    }
    
    public List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = this.getPlayerStorage().getCheaters();
        Collections.sort(cheaters);
        return CollectionUtil.copyFirst(cheaters, 20);
    }
    
    public void broadcastMessage(final byte[] message) {
        this.broadcastPacket(new ByteArrayMaplePacket(message));
    }
    
    public void broadcastMessage(final MaplePacket message) {
        this.broadcastPacket(message);
    }
    
    public void broadcastSmega(final byte[] message) {
        this.broadcastSmegaPacket(new ByteArrayMaplePacket(message));
    }
    
    public void broadcastGMMessage(final byte[] message) {
        this.broadcastGMPacket(new ByteArrayMaplePacket(message));
    }
    
    public void saveAll() {
        int ppl = 0;
        for (final MapleCharacter chr : this.players.getAllCharactersThreadSafe()) {
            if (chr != null) {
                ++ppl;
                chr.saveToDB(false, false);
            }
        }
        System.out.println("[\u81ea\u52a8\u5b58\u6863] \u5df2\u7ecf\u5c06\u9891\u9053 " + this.channel + " \u7684 " + ppl + " \u4e2a\u73a9\u5bb6\u4fdd\u5b58\u5230\u6570\u636e\u4e2d.");
    }
    
    public void AutoNx(final int dy, final int dd) {
        this.mapFactory.getMap(910000000).AutoNx(dy, dd);
    }
    
    public void \u516c\u544a(final String msg) {
        for (final ChannelServer cserv1 : getAllInstances()) {
            for (final MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                mch.startMapEffect(msg, 5121009);
            }
        }
    }
    
    public void AutoTime(final int dy) {
        try {
            for (final ChannelServer chan : getAllInstances()) {
                for (final MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                    if (chr == null) {
                        continue;
                    }
                    chr.gainGamePoints(1);
                    if (chr.getGamePoints() >= 5) {
                        continue;
                    }
                    chr.resetGamePointsPD();
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public int getInstanceId() {
        return this.instanceId;
    }
    
    public void addInstanceId() {
        ++this.instanceId;
    }
    
    public void shutdown() {
        if (this.finishedShutdown) {
            return;
        }
        this.broadcastPacket(MaplePacketCreator.serverNotice(0, "\u6e38\u620f\u5373\u5c06\u5173\u95ed\u7ef4\u62a4..."));
        this.shutdown = true;
        System.out.println("\u9891\u9053 " + this.channel + " \u6b63\u5728\u6e05\u7406\u6d3b\u52a8\u811a\u672c...");
        this.eventSM.cancel();
        System.out.println("\u9891\u9053 " + this.channel + " \u6b63\u5728\u4fdd\u5b58\u6240\u6709\u89d2\u8272\u6570\u636e...");
        System.out.println("\u9891\u9053 " + this.channel + " \u89e3\u9664\u7ed1\u5b9a\u7aef\u53e3...");
        this.acceptor.unbind((SocketAddress)new InetSocketAddress(this.port));
        ChannelServer.instances.remove(this.channel);
        this.setFinishShutdown();
    }
    
    public static boolean forceRemovePlayerByCharName(final String Name) {
        for (final ChannelServer ch : getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (final MapleCharacter c : chrs) {
                if (c.getName().equalsIgnoreCase(Name)) {
                    try {
                        if (c.getMap() != null) {
                            c.getMap().removePlayer(c);
                        }
                        if (c.getClient() != null) {
                            c.getClient().disconnect(true, false, false);
                            c.getClient().getSession().close();
                        }
                    }
                    catch (Exception ex) {}
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                        return true;
                    }
                    continue;
                }
            }
        }
        return false;
    }
    
    public static void forceRemovePlayerByAccId(final MapleClient c, final int accid) {
        for (final ChannelServer ch : getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (final MapleCharacter chr : chrs) {
                if (chr.getAccountID() == accid) {
                    try {
                        if (chr.getClient() != null && chr.getClient() != c) {
                            chr.getClient().disconnect(true, false, false);
                        }
                    }
                    catch (Exception ex) {}
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chr.getClient() == c) {
                        continue;
                    }
                    if (chrs.contains(chr)) {
                        ch.removePlayer(chr);
                    }
                    if (chr.getMap() == null) {
                        continue;
                    }
                    chr.getMap().removePlayer(chr);
                }
            }
        }
        try {
            final Collection<MapleCharacter> chrs2 = CashShopServer.getPlayerStorage().getAllCharactersThreadSafe();
            for (final MapleCharacter chr2 : chrs2) {
                if (chr2.getAccountID() == accid) {
                    try {
                        if (chr2.getClient() == null || chr2.getClient() == c) {
                            continue;
                        }
                        chr2.getClient().disconnect(true, false, false);
                    }
                    catch (Exception ex2) {}
                }
            }
        }
        catch (Exception ex3) {}
    }
    
    public static void forceRemovePlayerByAccId(final int accid) {
        for (final ChannelServer ch : getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (final MapleCharacter c : chrs) {
                if (c.getAccountID() == accid) {
                    try {
                        if (c.getClient() != null) {
                            c.getClient().disconnect(true, false, false);
                        }
                    }
                    catch (Exception ex) {}
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                    }
                    if (c.getMap() == null) {
                        continue;
                    }
                    c.getMap().removePlayer(c);
                }
            }
        }
    }
    
    static {
        instances = new HashMap<Integer, ChannelServer>();
    }
}
