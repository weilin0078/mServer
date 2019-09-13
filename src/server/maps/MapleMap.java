package server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import scripting.EventManager;
import server.MapleCarnivalFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleSquad;
import server.MapleStatEffect;
import server.Randomizer;
import server.SpeedRunner;
import server.Timer;
import server.events.MapleEvent;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.OverrideMonsterStats;
import server.life.SpawnPoint;
import server.life.SpawnPointAreaBoss;
import server.life.Spawns;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

public final class MapleMap
{
    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<MapleCharacter> characters;
    private final ReentrantReadWriteLock charactersLock;
    private int runningOid;
    private final Lock runningOidLock;
    private final List<Spawns> monsterSpawn;
    private final AtomicInteger spawnedMonstersOnMap;
    private final Map<Integer, MaplePortal> portals;
    private MapleFootholdTree footholds;
    private float monsterRate;
    private float recoveryRate;
    private MapleMapEffect mapEffect;
    private byte channel;
    private short decHP;
    private short createMobInterval;
    private int consumeItemCoolTime;
    private int protectItem;
    private int decHPInterval;
    private int mapid;
    private int returnMapId;
    private int timeLimit;
    private int fieldLimit;
    private int maxRegularSpawn;
    private int fixedMob;
    private int forcedReturnMap;
    private int lvForceMove;
    private int lvLimit;
    private int permanentWeather;
    private boolean town;
    private boolean clock;
    private boolean personalShop;
    private boolean everlast;
    private boolean dropsDisabled;
    private boolean gDropsDisabled;
    private boolean soaring;
    private boolean squadTimer;
    private boolean isSpawns;
    private String mapName;
    private String streetName;
    private String onUserEnter;
    private String onFirstUserEnter;
    private String speedRunLeader;
    private List<Integer> dced;
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart;
    private long lastSpawnTime;
    private long lastHurtTime;
    private MapleNodes nodes;
    private MapleSquad.MapleSquadType squad;
    private int fieldType;
    private Map<String, Integer> environment;
    
    public MapleMap(final int mapid, final int channel, final int returnMapId, final float monsterRate) {
        this.characters = new ArrayList<MapleCharacter>();
        this.charactersLock = new ReentrantReadWriteLock();
        this.runningOid = 100000;
        this.runningOidLock = new ReentrantLock();
        this.monsterSpawn = new ArrayList<Spawns>();
        this.spawnedMonstersOnMap = new AtomicInteger(0);
        this.portals = new HashMap<Integer, MaplePortal>();
        this.footholds = null;
        this.decHP = 0;
        this.createMobInterval = 9000;
        this.consumeItemCoolTime = 0;
        this.protectItem = 0;
        this.decHPInterval = 10000;
        this.maxRegularSpawn = 0;
        this.forcedReturnMap = 999999999;
        this.lvForceMove = 0;
        this.lvLimit = 0;
        this.permanentWeather = 0;
        this.everlast = false;
        this.dropsDisabled = false;
        this.gDropsDisabled = false;
        this.soaring = false;
        this.squadTimer = false;
        this.isSpawns = true;
        this.speedRunLeader = "";
        this.dced = new ArrayList<Integer>();
        this.speedRunStart = 0L;
        this.lastSpawnTime = 0L;
        this.lastHurtTime = 0L;
        this.environment = new LinkedHashMap<String, Integer>();
        this.mapid = mapid;
        this.channel = (byte)channel;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999) {
            this.returnMapId = mapid;
        }
        this.monsterRate = monsterRate;
        final EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>>(MapleMapObjectType.class);
        final EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<MapleMapObjectType, ReentrantReadWriteLock>(MapleMapObjectType.class);
        for (final MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<Integer, MapleMapObject>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap((Map<? extends MapleMapObjectType, ? extends LinkedHashMap<Integer, MapleMapObject>>)objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap((Map<? extends MapleMapObjectType, ? extends ReentrantReadWriteLock>)objlockmap);
    }
    
    public final void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }
    
    public final boolean getSpawns() {
        return this.isSpawns;
    }
    
    public final void setFixedMob(final int fm) {
        this.fixedMob = fm;
    }
    
    public final void setForceMove(final int fm) {
        this.lvForceMove = fm;
    }
    
    public final int getForceMove() {
        return this.lvForceMove;
    }
    
    public final void setLevelLimit(final int fm) {
        this.lvLimit = fm;
    }
    
    public final int getLevelLimit() {
        return this.lvLimit;
    }
    
    public final void setReturnMapId(final int rmi) {
        this.returnMapId = rmi;
    }
    
    public final void setSoaring(final boolean b) {
        this.soaring = b;
    }
    
    public final boolean canSoar() {
        return this.soaring;
    }
    
    public final void toggleDrops() {
        this.dropsDisabled = !this.dropsDisabled;
    }
    
    public final void setDrops(final boolean b) {
        this.dropsDisabled = b;
    }
    
    public final void toggleGDrops() {
        this.gDropsDisabled = !this.gDropsDisabled;
    }
    
    public final int getId() {
        return this.mapid;
    }
    
    public final MapleMap getReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.returnMapId);
    }
    
    public final int getReturnMapId() {
        return this.returnMapId;
    }
    
    public final int getForcedReturnId() {
        return this.forcedReturnMap;
    }
    
    public final MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.forcedReturnMap);
    }
    
    public final void setForcedReturnMap(final int map) {
        this.forcedReturnMap = map;
    }
    
    public final float getRecoveryRate() {
        return this.recoveryRate;
    }
    
    public final void setRecoveryRate(final float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }
    
    public final int getFieldLimit() {
        return this.fieldLimit;
    }
    
    public final void setFieldLimit(final int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }
    
    public final void setCreateMobInterval(final short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }
    
    public final void setTimeLimit(final int timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public final void setMapName(final String mapName) {
        this.mapName = mapName;
    }
    
    public final String getMapName() {
        return this.mapName;
    }
    
    public final String getStreetName() {
        return this.streetName;
    }
    
    public final void setFirstUserEnter(final String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }
    
    public final void setUserEnter(final String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }
    
    public void setOnUserEnter(final String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }
    
    public final boolean hasClock() {
        return this.clock;
    }
    
    public final void setClock(final boolean hasClock) {
        this.clock = hasClock;
    }
    
    public final boolean isTown() {
        return this.town;
    }
    
    public final void setTown(final boolean town) {
        this.town = town;
    }
    
    public final boolean allowPersonalShop() {
        return this.personalShop;
    }
    
    public final void setPersonalShop(final boolean personalShop) {
        this.personalShop = personalShop;
    }
    
    public final void setStreetName(final String streetName) {
        this.streetName = streetName;
    }
    
    public final void setEverlast(final boolean everlast) {
        this.everlast = everlast;
    }
    
    public final boolean getEverlast() {
        return this.everlast;
    }
    
    public final int getHPDec() {
        return this.decHP;
    }
    
    public final void setHPDec(final int delta) {
        if (delta > 0 || this.mapid == 749040100) {
            this.lastHurtTime = System.currentTimeMillis();
        }
        this.decHP = (short)delta;
    }
    
    public final int getHPDecInterval() {
        return this.decHPInterval;
    }
    
    public final void setHPDecInterval(final int delta) {
        this.decHPInterval = delta;
    }
    
    public final int getHPDecProtect() {
        return this.protectItem;
    }
    
    public final void setHPDecProtect(final int delta) {
        this.protectItem = delta;
    }
    
    public final int getCurrentPartyId() {
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter chr : this.characters) {
                if (chr.getPartyId() != -1) {
                    return chr.getPartyId();
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return -1;
    }
    
    public final void addMapObject(final MapleMapObject mapobject) {
        this.runningOidLock.lock();
        int newOid;
        try {
            newOid = ++this.runningOid;
        }
        finally {
            this.runningOidLock.unlock();
        }
        mapobject.setObjectId(newOid);
        this.mapobjectlocks.get(mapobject.getType()).writeLock().lock();
        try {
            this.mapobjects.get(mapobject.getType()).put(newOid, mapobject);
        }
        finally {
            this.mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
        }
    }
    
    private void spawnAndAddRangedMapObject(final MapleMapObject mapobject, final DelayedPacketCreation packetbakery, final SpawnCondition condition) {
        this.addMapObject(mapobject);
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter chr : this.characters) {
                if ((condition == null || condition.canSpawn(chr)) && !chr.isClone() && chr.getPosition().distanceSq(mapobject.getPosition()) <= GameConstants.maxViewRangeSq()) {
                    packetbakery.sendPackets(chr.getClient());
                    chr.addVisibleMapObject(mapobject);
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }
    
    public final void removeMapObject(final MapleMapObject obj) {
        this.mapobjectlocks.get(obj.getType()).writeLock().lock();
        try {
            this.mapobjects.get(obj.getType()).remove(obj.getObjectId());
        }
        finally {
            this.mapobjectlocks.get(obj.getType()).writeLock().unlock();
        }
    }
    
    public final Point calcPointBelow(final Point initial) {
        final MapleFoothold fh = this.footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            final double s1 = Math.abs(fh.getY2() - fh.getY1());
            final double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int)(Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
            else {
                dropY = fh.getY1() + (int)(Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }
    
    public final Point calcDropPos(final Point initial, final Point fallback) {
        final Point ret = this.calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }
    
    private void dropFromMonster(final MapleCharacter chr, final MapleMonster mob) {
        if (mob == null || chr == null || ChannelServer.getInstance(this.channel) == null || this.dropsDisabled || mob.dropsDisabled() || chr.getPyramidSubway() != null) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte)(mob.getStats().isExplosiveReward() ? 3 : (mob.getStats().isFfaLoot() ? 2 : ((chr.getParty() != null) ? 1 : 0)));
        final int mobpos = mob.getPosition().x;
        final int cmServerrate = ChannelServer.getInstance(this.channel).getMesoRate();
        int chServerrate = ChannelServer.getInstance(this.channel).getDropRate();
        final int caServerrate = ChannelServer.getInstance(this.channel).getCashRate();
        byte d = 1;
        final Point pos = new Point(0, mob.getPosition().y);
        double showdown = 100.0;
        final MonsterStatusEffect mse = mob.getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }
        if (mob.getStats().isBoss()) {
            chServerrate = ChannelServer.getInstance(this.channel).getBossDropRate();
        }
        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> dropEntry = mi.retrieveDrop(mob.getId());
        Collections.shuffle(dropEntry);
        for (final MonsterDropEntry de : dropEntry) {
            if (de.itemId == mob.getStolen()) {
                continue;
            }
            final int Rand = Randomizer.nextInt(999999);
            final int part1 = de.chance;
            final int part2 = chServerrate;
            final int part3 = chr.getDropMod();
            final int part4 = (int)(chr.getStat().dropBuff / 100.0);
            final int part5 = (int)(showdown / 100.0);
            final int last = part1 * part2 * part3 * part4 * part5;
            if (Rand >= last) {
                continue;
            }
            if (droptype == 3) {
                pos.x = mobpos + ((d % 2 == 0) ? (40 * (d + 1) / 2) : (-(40 * (d / 2))));
            }
            else {
                pos.x = mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : (-(25 * (d / 2))));
            }
            if (de.itemId != 0) {
                IItem idrop;
                if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                    idrop = ii.randomizeStats((Equip)ii.getEquipById(de.itemId));
                }
                else {
                    final int range = Math.abs(de.Maximum - de.Minimum);
                    idrop = new Item(de.itemId, (short)0, (short)((de.Maximum != 1) ? (Randomizer.nextInt((range <= 0) ? 1 : range) + de.Minimum) : 1), (byte)0);
                }
                if (Randomizer.nextInt(100) <= 7 && !mob.getStats().isBoss() && chr.getEventInstance() == null) {
                    idrop = new Item(4001126, (short)0, (short)1, (byte)0);
                }
                if (Randomizer.nextInt(100) <= 10 && chr.getQuestStatus(28172) == 1) {
                    idrop = new Item(4001341, (short)0, (short)1, (byte)0);
                }
                this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
            }
            ++d;
        }
        double mesoDecrease = Math.pow(0.93, mob.getStats().getExp() / 350.0);
        if (mesoDecrease > 1.0) {
            mesoDecrease = 1.0;
        }
        final int tempmeso = Math.min(30000, (int)(mesoDecrease * mob.getStats().getExp() * (1.0 + Math.random() * 5.0) / 10.0));
        if (tempmeso > 0) {
            pos.x = Math.min(Math.max(mobpos - 25 * (d / 2), this.footholds.getMinDropX() + 25), this.footholds.getMaxDropX() - d * 25);
            this.spawnMobMesoDrop((int)(tempmeso * (chr.getStat().mesoBuff / 100.0) * chr.getDropMod() * cmServerrate), this.calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
        }
        final List<MonsterGlobalDropEntry> globalEntry = new ArrayList<MonsterGlobalDropEntry>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        final int cashz = ((mob.getStats().isBoss() && mob.getStats().getHPDisplayType() == 0) ? 20 : 1) * caServerrate;
        final int cashModifier = (int)(mob.getStats().isBoss() ? 0L : (mob.getMobExp() / 1000 + mob.getMobMaxHp() / 10000L));
        for (final MonsterGlobalDropEntry de2 : globalEntry) {
            if (Randomizer.nextInt(999999) < de2.chance && (de2.continent < 0 || (de2.continent < 10 && this.mapid / 100000000 == de2.continent) || (de2.continent < 100 && this.mapid / 10000000 == de2.continent) || (de2.continent < 1000 && this.mapid / 1000000 == de2.continent))) {
                if (droptype == 3) {
                    pos.x = mobpos + ((d % 2 == 0) ? (40 * (d + 1) / 2) : (-(40 * (d / 2))));
                }
                else {
                    pos.x = mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : (-(25 * (d / 2))));
                }
                if (de2.itemId == 0) {
                    continue;
                }
                if (this.gDropsDisabled) {
                    continue;
                }
                IItem idrop;
                if (GameConstants.getInventoryType(de2.itemId) == MapleInventoryType.EQUIP) {
                    idrop = ii.randomizeStats((Equip)ii.getEquipById(de2.itemId));
                }
                else {
                    idrop = new Item(de2.itemId, (short)0, (short)((de2.Maximum != 1) ? (Randomizer.nextInt(de2.Maximum - de2.Minimum) + de2.Minimum) : 1), (byte)0);
                }
                if (Randomizer.nextInt(100) <= 7 && !mob.getStats().isBoss() && chr.getEventInstance() == null) {
                    idrop = new Item(4001126, (short)0, (short)1, (byte)0);
                }
                this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getPosition()), mob, chr, (byte)(de2.onlySelf ? 0 : droptype), de2.questid);
                ++d;
            }
        }
    }
    
    public void removeMonster(final MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 0));
        this.removeMapObject(monster);
    }
    
    private void killMonster(final MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0L);
        monster.spawnRevives(this);
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 1));
        this.removeMapObject(monster);
    }
    
    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, final byte animation) {
        this.killMonster(monster, chr, withDrops, second, animation, 0);
    }
    
    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation, final int lastSkill) {
        if ((monster.getId() == 8810122 || monster.getId() == 8810018) && !second) {
            Timer.MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    MapleMap.this.killMonster(monster, chr, true, true, (byte)1);
                    MapleMap.this.killAllMonsters(true);
                }
            }, 3000L);
            return;
        }
        if (monster.getId() == 8820014) {
            this.killMonster(8820000);
        }
        else if (monster.getId() == 9300166) {
            animation = 2;
        }
        else if (this.getId() == 910320100) {}
        this.spawnedMonstersOnMap.decrementAndGet();
        this.removeMapObject(monster);
        final int dropOwner = monster.killBy(chr, lastSkill);
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animation));
        if (monster.getBuffToGive() > -1) {
            final int buffid = monster.getBuffToGive();
            final MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);
            this.charactersLock.readLock().lock();
            try {
                for (final MapleCharacter mc : this.characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);
                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001: {
                                mc.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(buffid, 11));
                                this.broadcastMessage(mc, MaplePacketCreator.showBuffeffect(mc.getId(), buffid, 11), false);
                                continue;
                            }
                        }
                    }
                }
            }
            finally {
                this.charactersLock.readLock().unlock();
            }
        }
        final int mobid = monster.getId();
        SpeedRunType type = SpeedRunType.NULL;
        final MapleSquad sqd = this.getSquadByMap();
        if (mobid == 8810018 && this.mapid == 240060200) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，" + chr.getName() + "所带领的队伍终于击败了暗黑龙王的远征队！你们才是龙之林的真正英雄~").getBytes());
            FileoutputUtil.log("Logs/Log_暗黑龙王.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Horntail;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
            final Collection<MaplePartyCharacter> members = chr.getParty().getMembers();
            for (final MaplePartyCharacter member : members) {
                chr.delBossBack(member.getId());
            }
        }
        else if (mobid == 8810122 && this.mapid == 240060201) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，" + chr.getName() + "所带领的队伍终于击败了混沌暗黑龙王的远征队！你们才是龙之林的真正英雄~").getBytes());
            FileoutputUtil.log("Logs/Log_暗黑龙王.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.ChaosHT;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9600025 && this.mapid == 702060000) {
            final Collection<MaplePartyCharacter> members = chr.getParty().getMembers();
            for (final MaplePartyCharacter member : members) {
                chr.delBossBack(member.getId());
            }
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，" + chr.getName() + "所带领的队伍终于击败了妖僧！你们才是少林寺的真正英雄~").getBytes());
        }
        else if (mobid == 8500002 && this.mapid == 220080001) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，" + chr.getName() + "所带领的队伍终于击败了闹钟！你们才是时间隧道的真正英雄~").getBytes());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Papulatus;
            }
        }
        else if (mobid == 9400266 && this.mapid == 802000111) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Nameless_Magic_Monster;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400265 && this.mapid == 802000211) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Vergamot;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400270 && this.mapid == 802000411) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Dunas;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400273 && this.mapid == 802000611) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Nibergen;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400294 && this.mapid == 802000711) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Dunas_2;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400296 && this.mapid == 802000803) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Core_Blaze;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid == 9400289 && this.mapid == 802000821) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Aufhaven;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if ((mobid == 9420549 || mobid == 9420544) && this.mapid == 551030200) {
            if (this.speedRunStart > 0L) {
                if (mobid == 9420549) {
                    type = SpeedRunType.Scarlion;
                }
                else {
                    type = SpeedRunType.Targa;
                }
            }
        }
        else if (mobid == 8820001 && this.mapid == 270050100) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + "经过带领的队伍经过无数次的挑战，终于击败了时间的宠儿－品克缤的远征队！你们才是时间神殿的真正英雄~").getBytes());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Pink_Bean;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
            FileoutputUtil.log("Logs/Log_品克缤.rtf", this.MapDebug_Log());
            final Collection<MaplePartyCharacter> members = chr.getParty().getMembers();
            for (final MaplePartyCharacter member : members) {
                chr.delBossBack(member.getId());
            }
        }
        else if (mobid == 8800002 && this.mapid == 280030000) {
            FileoutputUtil.log("Logs/Log_扎昆.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Zakum;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
            final Collection<MaplePartyCharacter> members = chr.getParty().getMembers();
            for (final MaplePartyCharacter member : members) {
                chr.delBossBack(member.getId());
            }
        }
        else if (mobid == 8800102 && this.mapid == 280030001) {
            FileoutputUtil.log("Logs/Log_扎昆.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Chaos_Zakum;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        }
        else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = this.getAllMonstersThreadsafe();
            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final MapleMonster mons2 = (MapleMonster)object;
                    if (mons2.getId() == 8800000) {
                        final Point pos = mons2.getPosition();
                        this.killAllMonsters(true);
                        this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                        break;
                    }
                }
            }
        }
        else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = this.getAllMonstersThreadsafe();
            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800103 && mons.getId() <= 8800110) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMonster mons : monsters) {
                    if (mons.getId() == 8800100) {
                        final Point pos2 = mons.getPosition();
                        this.killAllMonsters(true);
                        this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos2);
                        break;
                    }
                }
            }
        }
        if (type != SpeedRunType.NULL && this.speedRunStart > 0L && this.speedRunLeader.length() > 0) {
            final long endTime = System.currentTimeMillis();
            final String time = StringUtil.getReadableMillis(this.speedRunStart, endTime);
            this.broadcastMessage(MaplePacketCreator.serverNotice(5, this.speedRunLeader + "'远征队花了 " + time + " 时间打败了 " + type + "!"));
            this.getRankAndAdd(this.speedRunLeader, time, type, endTime - this.speedRunStart, (sqd == null) ? null : sqd.getMembers());
            this.endSpeedRun();
        }
        if (mobid == 8820008) {
            for (final MapleMapObject mmo : this.getAllMonstersThreadsafe()) {
                final MapleMonster mons3 = (MapleMonster)mmo;
                if (mons3.getLinkOid() != monster.getObjectId()) {
                    this.killMonster(mons3, chr, false, false, animation);
                }
            }
        }
        else if (mobid >= 8820010 && mobid <= 8820014) {
            for (final MapleMapObject mmo : this.getAllMonstersThreadsafe()) {
                final MapleMonster mons3 = (MapleMonster)mmo;
                if (mons3.getId() != 8820000 && mons3.getObjectId() != monster.getObjectId() && mons3.getLinkOid() != monster.getObjectId()) {
                    this.killMonster(mons3, chr, false, false, animation);
                }
            }
        }
        if (withDrops) {
            MapleCharacter drop = null;
            if (dropOwner <= 0) {
                drop = chr;
            }
            else {
                drop = this.getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            this.dropFromMonster(drop, monster);
        }
    }
    
    public List<MapleReactor> getAllReactor() {
        return this.getAllReactorsThreadsafe();
    }
    
    public List<MapleReactor> getAllReactorsThreadsafe() {
        final ArrayList<MapleReactor> ret = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ret.add((MapleReactor)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }
    
    public List<MapleMapObject> getAllDoor() {
        return this.getAllDoorsThreadsafe();
    }
    
    public List<MapleMapObject> getAllDoorsThreadsafe() {
        final ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
                ret.add(mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }
    
    public List<MapleMapObject> getAllMerchant() {
        return this.getAllHiredMerchantsThreadsafe();
    }
    
    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        final ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        this.mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
                ret.add(mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }
    
    public List<MapleMonster> getAllMonster() {
        return this.getAllMonstersThreadsafe();
    }
    
    public List<MapleMonster> getAllMonstersThreadsafe() {
        final ArrayList<MapleMonster> ret = new ArrayList<MapleMonster>();
        this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                ret.add((MapleMonster)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }
    
    public final void killAllMonsters(final boolean animate) {
        for (final MapleMapObject monstermo : this.getAllMonstersThreadsafe()) {
            final MapleMonster monster = (MapleMonster)monstermo;
            this.spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0L);
            this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), (int)(animate ? 1 : 0)));
            this.removeMapObject(monster);
            monster.killed();
        }
    }
    
    public final void killMonster(final int monsId) {
        for (final MapleMapObject mmo : this.getAllMonstersThreadsafe()) {
            if (((MapleMonster)mmo).getId() == monsId) {
                this.spawnedMonstersOnMap.decrementAndGet();
                this.removeMapObject(mmo);
                this.broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
                break;
            }
        }
    }
    
    private String MapDebug_Log() {
        final StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(FileoutputUtil.CurrentReadable_Time());
        sb.append(" | Mapid : ").append(this.mapid);
        this.charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(this.characters.size()).append("] | ");
            for (final MapleCharacter mc : this.characters) {
                sb.append(mc.getName()).append(", ");
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return sb.toString();
    }
    
    public final void limitReactor(final int rid, final int num) {
        final List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        final Map<Integer, Integer> contained = new LinkedHashMap<Integer, Integer>();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor mr = (MapleReactor)obj;
                if (contained.containsKey(mr.getReactorId())) {
                    if (contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                    }
                    else {
                        contained.put(mr.getReactorId(), contained.get(mr.getReactorId()) + 1);
                    }
                }
                else {
                    contained.put(mr.getReactorId(), 1);
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (final MapleReactor mr2 : toDestroy) {
            this.destroyReactor(mr2.getObjectId());
        }
    }
    
    public final void destroyReactors(final int first, final int last) {
        final List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor mr = (MapleReactor)obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    toDestroy.add(mr);
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (final MapleReactor mr2 : toDestroy) {
            this.destroyReactor(mr2.getObjectId());
        }
    }
    
    public final void destroyReactor(final int oid) {
        final MapleReactor reactor = this.getReactorByOid(oid);
        this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        this.removeMapObject(reactor);
        reactor.setTimerActive(false);
        if (reactor.getDelay() > 0) {
            Timer.MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public final void run() {
                    MapleMap.this.respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }
    
    public final void reloadReactors() {
        final List<MapleReactor> toSpawn = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor reactor = (MapleReactor)obj;
                this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (final MapleReactor r : toSpawn) {
            this.removeMapObject(r);
            if (r.getReactorId() != 9980000 && r.getReactorId() != 9980001) {
                this.respawnReactor(r);
            }
        }
    }
    
    public final void resetReactors() {
        this.setReactorState((byte)0);
    }
    
    public final void setReactorState() {
        this.setReactorState((byte)1);
    }
    
    public final void setReactorState(final byte state) {
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor)obj).forceHitReactor(state);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }
    
    public final void shuffleReactors() {
        this.shuffleReactors(0, 9999999);
    }
    
    public final void shuffleReactors(final int first, final int last) {
        final List<Point> points = new ArrayList<Point>();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor mr = (MapleReactor)obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    points.add(mr.getPosition());
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor mr = (MapleReactor)obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    mr.setPosition(points.remove(points.size() - 1));
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }
    
    public final void updateMonsterController(final MapleMonster monster) {
        if (!monster.isAlive()) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() == this) {
                return;
            }
            monster.getController().stopControllingMonster(monster);
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter chr : this.characters) {
                if (!chr.isHidden() && !chr.isClone() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1)) {
                    mincontrolled = chr.getControlledSize();
                    newController = chr;
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            }
            else {
                newController.controlMonster(monster, false);
            }
        }
    }
    
    public final MapleMapObject getMapObject(final int oid, final MapleMapObjectType type) {
        this.mapobjectlocks.get(type).readLock().lock();
        try {
            return this.mapobjects.get(type).get(oid);
        }
        finally {
            this.mapobjectlocks.get(type).readLock().unlock();
        }
    }
    
    public final boolean containsNPC(final int npcid) {
        this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (final MapleMapObject n : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
                if (((MapleNPC)n).getId() == npcid) {
                    return true;
                }
            }
            return false;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }
    
    public MapleNPC getNPCById(final int id) {
        this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (final MapleMapObject n : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
                if (((MapleNPC)n).getId() == id) {
                    return (MapleNPC)n;
                }
            }
            return null;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }
    
    public MapleMonster getMonsterById(final int id) {
        this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            for (final MapleMapObject n : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                if (((MapleMonster)n).getId() == id) {
                    ret = (MapleMonster) n;
                    break;
                }
            }
            return ret;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }
    
    public int countMonsterById(final int id) {
        this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            for (final MapleMapObject n : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                if (((MapleMonster)n).getId() == id) {
                    ++ret;
                }
            }
            return ret;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }
    
    public MapleReactor getReactorById(final int id) {
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            for (final MapleMapObject n : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                if (((MapleReactor)n).getReactorId() == id) {
                    ret = (MapleReactor) n;
                    break;
                }
            }
            return ret;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }
    
    public final MapleMonster getMonsterByOid(final int oid) {
        final MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster)mmo;
    }
    
    public final MapleNPC getNPCByOid(final int oid) {
        final MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC)mmo;
    }
    
    public final MapleReactor getReactorByOid(final int oid) {
        final MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor)mmo;
    }
    
    public final MapleReactor getReactorByName(final String name) {
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor mr = (MapleReactor)obj;
                if (mr.getName().equalsIgnoreCase(name)) {
                    return mr;
                }
            }
            return null;
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }
    
    public final void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(this.getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        this.addMapObject(npc);
        this.broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
    }
    
    public final void removeNpc(final int npcid) {
        this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            final Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                final MapleNPC npc = (MapleNPC) itr.next();
                if (npc.isCustom() && npc.getId() == npcid) {
                    this.broadcastMessage(MaplePacketCreator.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }
    
    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType) {
        final Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        this.spawnMonster(mob, spawnType);
    }
    
    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        this.spawnMonster_sSack(mob, pos, -2);
    }
    
    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType, final int hp) {
        final Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        mob.setHp(hp);
        this.spawnMonster(mob, spawnType);
    }
    
    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos, final int hp) {
        this.spawnMonster_sSack(mob, pos, -2, hp);
    }
    
    public final int spawnMonsterWithEffectBelow(final MapleMonster mob, final Point pos, final int effect) {
        final Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        return this.spawnMonsterWithEffect(mob, effect, spos);
    }
    
    public final void spawnZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        final Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        this.spawnFakeMonster(mainb);
        final int[] arr$;
        final int[] zakpart = arr$ = new int[] { 8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010 };
        for (final int i : arr$) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
        }
    }
    
    public final void spawnChaosZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        final Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        this.spawnFakeMonster(mainb);
        final int[] arr$;
        final int[] zakpart = arr$ = new int[] { 8800103, 8800104, 8800105, 8800106, 8800107, 8800108, 8800109, 8800110 };
        for (final int i : arr$) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
        }
    }
    
    public List<MapleMist> getAllMistsThreadsafe() {
        final ArrayList<MapleMist> ret = new ArrayList<MapleMist>();
        this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
                ret.add((MapleMist)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }
    
    public final void spawnFakeMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        final Point calcPointBelow;
        final Point spos = calcPointBelow = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        --calcPointBelow.y;
        mob.setPosition(spos);
        this.spawnFakeMonster(mob);
    }
    
    public int getMobsSize() {
        return this.mapobjects.get(MapleMapObjectType.MONSTER).size();
    }
    
    private void checkRemoveAfter(final MapleMonster monster) {
        final int ra = monster.getStats().getRemoveAfter();
        if (ra > 0) {
            Timer.MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public final void run() {
                    if (monster != null && monster == MapleMap.this.getMapObject(monster.getObjectId(), monster.getType())) {
                        MapleMap.this.killMonster(monster);
                    }
                }
            }, ra * 1000);
        }
    }
    
    public final void spawnRevives(final MapleMonster monster, final int oid) {
        monster.setMap(this);
        this.checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, 0, oid));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }
    
    public final void spawnMonster(final MapleMonster monster, final int spawnType) {
        if (monster.getId() == 6090000) {
            return;
        }
        monster.setMap(this);
        this.checkRemoveAfter(monster);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, spawnType, 0, 0));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }
    
    public final int spawnMonsterWithEffect(final MapleMonster monster, final int effect, final Point pos) {
        try {
            monster.setMap(this);
            monster.setPosition(pos);
            this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
                @Override
                public final void sendPackets(final MapleClient c) {
                    c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, effect, 0));
                }
            }, null);
            this.updateMonsterController(monster);
            this.spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        }
        catch (Exception e) {
            return -1;
        }
    }
    
    public final void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, 252, 0));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }
    
    public final void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        this.spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.spawnReactor(reactor));
            }
        }, null);
    }
    
    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte)0);
        reactor.setAlive(true);
        this.spawnReactor(reactor);
    }
    
    public final void spawnDoor(final MapleDoor door) {
        this.spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.spawnDoor(door.getOwner().getId(), door.getTargetPosition(), false));
                if (door.getOwner().getParty() != null && (door.getOwner() == c.getPlayer() || door.getOwner().getParty().containsMembers(new MaplePartyCharacter(c.getPlayer())))) {
                    c.getSession().write((Object)MaplePacketCreator.partyPortal(door.getTown().getId(), door.getTarget().getId(), door.getSkill(), door.getTargetPosition()));
                }
                c.getSession().write((Object)MaplePacketCreator.spawnPortal(door.getTown().getId(), door.getTarget().getId(), door.getSkill(), door.getTargetPosition()));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }, new SpawnCondition() {
            @Override
            public final boolean canSpawn(final MapleCharacter chr) {
                return door.getTarget().getId() == chr.getMapId() || door.getOwnerId() == chr.getId() || (door.getOwner() != null && door.getOwner().getParty() != null && door.getOwner().getParty().getMemberById(chr.getId()) != null);
            }
        });
    }
    
    public final void spawnSummon(final MapleSummon summon) {
        summon.updateMap(this);
        this.spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                if (!summon.isChangedMap() || summon.getOwnerId() == c.getPlayer().getId()) {
                    c.getSession().write((Object)MaplePacketCreator.spawnSummon(summon, true));
                }
            }
        }, null);
    }
    
    public final void spawnDragon(final MapleDragon summon) {
        this.spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
            }
        }, null);
    }
    
    public final void spawnMist(final MapleMist mist, final int duration, final boolean fake) {
        this.spawnAndAddRangedMapObject(mist, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                mist.sendSpawnData(c);
            }
        }, null);
        final Timer.MapTimer tMan = Timer.MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule ;
        switch (mist.isPoisonMist()) {
            case 1: {
                final MapleCharacter owner = this.getCharacterById(mist.getOwnerId());
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                            if (mist.makeChanceResult() && !((MapleMonster)mo).isBuffed(MonsterStatus.POISON)) {
                                ((MapleMonster)mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, duration, true);
                            }
                        }
                    }
                }, 2000L, 2500L);
                break;
            }
            case 2: {
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = (MapleCharacter)mo;
                                chr.addMP((int)(mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0)));
                            }
                        }
                    }
                }, 2000L, 2500L);
                break;
            }
            default: {
                poisonSchedule = null;
                break;
            }
        }
        tMan.schedule(new Runnable() {
            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMist(mist.getObjectId(), false));
                MapleMap.this.removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, duration);
    }
    
    public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, final Point pos) {
        final Point droppos = this.calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)1, false);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)3), drop.getPosition());
    }
    
    public final void spawnMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = this.calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getPosition(), droppos, (byte)1));
            }
        }, null);
        if (!this.everlast) {
            mdrop.registerExpire(120000L);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000L);
            }
        }
    }
    
    public final void spawnMobMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getPosition(), position, (byte)1));
            }
        }, null);
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
    }
    
    public final void spawnMobDrop(final IItem idrop, final Point dropPos, final MapleMonster mob, final MapleCharacter chr, final byte droptype, final short questid) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                if (questid <= 0 || c.getPlayer().getQuestStatus(questid) == 1) {
                    c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, mob.getPosition(), dropPos, (byte)1));
                }
            }
        }, null);
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
        this.activateItemReactors(mdrop, chr.getClient());
    }
    
    public final void spawnRandDrop() {
        if (this.mapid != 910000000 || this.channel != 1) {
            return;
        }
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (final MapleMapObject o : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem)o).isRandDrop()) {
                    return;
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        Timer.MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                final Point pos = new Point(Randomizer.nextInt(800) + 531, 34 - Randomizer.nextInt(800));
                int itemid = 0;
                itemid = 4000463;
                MapleMap.this.spawnAutoDrop(itemid, pos);
            }
        }, 600000L);
    }
    
    public final void spawnAutoDrop(final int itemid, final Point pos) {
        IItem idrop = null;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip)ii.getEquipById(itemid));
        }
        else {
            idrop = new Item(itemid, (short)0, (short)1, (byte)0);
        }
        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte)1));
            }
        }, null);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte)0));
        mdrop.registerExpire(120000L);
    }
    
    public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, final Point pos, final boolean ffaDrop, final boolean playerDrop) {
        final Point droppos = this.calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)2, playerDrop);
        this.spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(final MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)1));
            }
        }, null);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)0));
        if (!this.everlast) {
            drop.registerExpire(120000L);
            this.activateItemReactors(drop, owner.getClient());
        }
    }
    
    private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
        final IItem item = drop.getItem();
        this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject o : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor react = (MapleReactor)o;
                if (react.getReactorType() == 100 && GameConstants.isCustomReactItem(react.getReactorId(), item.getItemId(), react.getReactItem().getLeft()) && react.getReactItem().getRight() == item.getQuantity() && react.getArea().contains(drop.getPosition()) && !react.isTimerActive()) {
                    Timer.MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000L);
                    react.setTimerActive(true);
                    break;
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }
    
    public int getItemsSize() {
        return this.mapobjects.get(MapleMapObjectType.ITEM).size();
    }
    
    public List<MapleMapItem> getAllItems() {
        return this.getAllItemsThreadsafe();
    }
    
    public List<MapleMapItem> getAllItemsThreadsafe() {
        final ArrayList<MapleMapItem> ret = new ArrayList<MapleMapItem>();
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }
    
    public final void returnEverLastItem(final MapleCharacter chr) {
        for (final MapleMapObject o : this.getAllItemsThreadsafe()) {
            final MapleMapItem item = (MapleMapItem)o;
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);
                this.broadcastMessage(MaplePacketCreator.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getPosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                }
                else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                this.removeMapObject(item);
            }
        }
        this.spawnRandDrop();
    }
    
    public final void talkMonster(final String msg, final int itemId, final int objectid) {
        if (itemId > 0) {
            this.startMapEffect(msg, itemId, false);
        }
        this.broadcastMessage(MobPacket.talkMonster(objectid, itemId, msg));
        this.broadcastMessage(MobPacket.removeTalkMonster(objectid));
    }
    
    public final void startMapEffect(final String msg, final int itemId) {
        this.startMapEffect(msg, itemId, false);
    }
    
    public final void startMapEffect(final String msg, final int itemId, final boolean jukebox) {
        if (this.mapEffect != null) {
            return;
        }
        (this.mapEffect = new MapleMapEffect(msg, itemId)).setJukebox(jukebox);
        this.broadcastMessage(this.mapEffect.makeStartData());
        Timer.MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MapleMap.this.mapEffect.makeDestroyData());
                MapleMap.this.mapEffect = null;
            }
        }, jukebox ? 300000L : 30000L);
    }
    
    public final void startExtendedMapEffect(final String msg, final int itemId) {
        this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
        Timer.MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMapEffect());
                MapleMap.this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, false));
            }
        }, 60000L);
    }
    
    public final void startJukebox(final String msg, final int itemId) {
        this.startMapEffect(msg, itemId, true);
    }
    
    public final void addPlayer(final MapleCharacter chr) {
        this.mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            this.mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().unlock();
        }
        this.charactersLock.writeLock().lock();
        try {
            this.characters.add(chr);
        }
        finally {
            this.charactersLock.writeLock().unlock();
        }
        final boolean 进入地图开启显示数据 = false;
        if (this.mapid == 109080000 || this.mapid == 109080001 || this.mapid == 109080002 || this.mapid == 109080003 || this.mapid == 109080010 || this.mapid == 109080011 || this.mapid == 109080012) {
            chr.setCoconutTeam(this.getAndSwitchTeam() ? 0 : 1);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据A");
            }
        }
        if (!chr.isHidden()) {
            this.broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据B");
            }
            if (chr.isGM() && this.speedRunStart > 0L) {
                this.endSpeedRun();
                this.broadcastMessage(MaplePacketCreator.serverNotice(5, "The speed run has ended."));
                if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                    System.out.println("进入地图加载数据C");
                }
            }
        }
        if (!chr.isClone()) {
            this.sendObjectPlacement(chr);
            chr.getClient().getSession().write((Object)MaplePacketCreator.spawnPlayerMapobject(chr));
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据D");
            }
            switch (this.mapid) {
                case 109030001:
                case 109040000:
                case 109060001:
                case 109080000:
                case 109080010: {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEventInstructions());
                    break;
                }
                case 809000101:
                case 809000201: {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEquipEffect());
                    break;
                }
            }
        }
        for (final MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                chr.getClient().getSession().write((Object)PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition()), true));
                this.broadcastMessage(chr, PetPacket.showPet(chr, pet, false, false), false);
                if (!ServerConstants.封包显示 && !进入地图开启显示数据) {
                    continue;
                }
                System.out.println("进入地图加载数据F");
            }
        }
        if (this.hasForcedEquip()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.showForcedEquip());
        }
        chr.getClient().getSession().write((Object)MaplePacketCreator.removeTutorialStats());
        if (chr.getMapId() >= 914000200 && chr.getMapId() <= 914000220) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.addTutorialStats());
        }
        if ((chr.getMapId() >= 140090100 && chr.getMapId() <= 140090500) || (chr.getJob() == 1000 && chr.getMapId() != 130030000)) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.spawnTutorialSummon(1));
        }
        chr.startMapEffect(chr.getMap().getMapName(), 5120016, 5000);
        if (!this.onUserEnter.equals("")) {
            MapScriptMethods.startScript_User(chr.getClient(), this.onUserEnter);
        }
        if (!this.onFirstUserEnter.equals("") && this.getCharacters().size() == 1) {
            MapScriptMethods.startScript_FirstUser(chr.getClient(), this.onFirstUserEnter);
        }
        final MapleStatEffect stat = chr.getStatForBuff(MapleBuffStat.SUMMON);
        if (stat != null && !chr.isClone()) {
            final MapleSummon summon = chr.getSummons().get(stat.getSourceId());
            summon.setPosition(chr.getPosition());
            try {
                summon.setFh(this.getFootholds().findBelow(chr.getPosition()).getId());
            }
            catch (NullPointerException e) {
                summon.setFh(0);
            }
            this.spawnSummon(summon);
            chr.addVisibleMapObject(summon);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据H");
            }
        }
        if (chr.getChalkboard() != null) {
            chr.getClient().getSession().write((Object)MTSCSPacket.useChalkboard(chr.getId(), chr.getChalkboard()));
        }
        this.broadcastMessage(MaplePacketCreator.loveEffect());
        if (this.timeLimit > 0 && this.getForcedReturnMap() != null && !chr.isClone()) {
            chr.startMapTimeLimitTask(this.timeLimit, this.getForcedReturnMap());
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据I");
            }
        }
        if (this.getSquadBegin() != null && this.getSquadBegin().getTimeLeft() > 0L && this.getSquadBegin().getStatus() == 1) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(this.getSquadBegin().getTimeLeft() / 1000L)));
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据O");
            }
        }
        if (chr.getCarnivalParty() != null && chr.getEventInstance() != null) {
            chr.getEventInstance().onMapLoad(chr);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据M");
            }
        }
        MapleEvent.mapLoad(chr, this.channel);
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted() && !chr.isClone()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(chr.getEventInstance().getTimeLeft() / 1000L)));
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据K");
            }
        }
        if (this.hasClock()) {
            final Calendar cal = Calendar.getInstance();
            chr.getClient().getSession().write((Object)MaplePacketCreator.getClockTime(cal.get(11), cal.get(12), cal.get(13)));
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据L");
            }
        }
        if (this.isTown()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.RAINING_MINES);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据W-------------完");
            }
        }
        if (chr.getParty() != null && !chr.isClone()) {
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据G");
            }
        }
        if (this.permanentWeather > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.startMapEffect("", this.permanentWeather, false));
        }
        if (this.getPlatforms().size() > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getMovingPlatforms(this));
        }
        if (this.environment.size() > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getUpdateEnvironment(this));
        }
        if (this.isTown()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.RAINING_MINES);
            if (ServerConstants.封包显示 || 进入地图开启显示数据) {
                System.out.println("进入地图加载数据W-------------完");
            }
        }
    }
    
    public int getNumItems() {
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            return this.mapobjects.get(MapleMapObjectType.ITEM).size();
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
    }
    
    private boolean hasForcedEquip() {
        return this.fieldType == 81 || this.fieldType == 82;
    }
    
    public void setFieldType(final int fieldType) {
        this.fieldType = fieldType;
    }
    
    public int getNumMonsters() {
        this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            return this.mapobjects.get(MapleMapObjectType.MONSTER).size();
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }
    
    public void doShrine(final boolean spawned) {
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
        }
        final int mode = (this.mapid == 280030000) ? 1 : ((this.mapid == 280030001) ? 2 : ((this.mapid == 240060200 || this.mapid == 240060201) ? 3 : 0));
        final MapleSquad sqd = this.getSquadByMap();
        final EventManager em = this.getEMByMap();
        if (sqd != null && em != null && this.getCharactersSize() > 0) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            MapleMap returnMapa = this.getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == this.mapid) {
                returnMapa = this.getReturnMap();
            }
            if (mode == 1) {
                this.broadcastMessage(MaplePacketCreator.showZakumShrine(spawned, 5));
            }
            else if (mode == 2) {
                this.broadcastMessage(MaplePacketCreator.showChaosZakumShrine(spawned, 5));
            }
            else if (mode == 3) {
                this.broadcastMessage(MaplePacketCreator.showChaosHorntailShrine(spawned, 5));
            }
            else {
                this.broadcastMessage(MaplePacketCreator.showHorntailShrine(spawned, 5));
            }
            if (mode == 1 || spawned) {
                this.broadcastMessage(MaplePacketCreator.getClock(300));
            }
            final MapleMap returnMapz = returnMapa;
            Runnable run;
            if (!spawned) {
                final List<MapleMonster> monsterz = this.getAllMonstersThreadsafe();
                final List<Integer> monsteridz = new ArrayList<Integer>();
                for (final MapleMapObject m : monsterz) {
                    monsteridz.add(m.getObjectId());
                }
                run = new Runnable() {
                    @Override
                    public void run() {
                        final MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && MapleMap.this.getNumMonsters() == monsterz.size() && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            boolean passed = monsterz.isEmpty();
                            for (final MapleMapObject m : MapleMap.this.getAllMonstersThreadsafe()) {
                                for (final int i : monsteridz) {
                                    if (m.getObjectId() == i) {
                                        passed = true;
                                        break;
                                    }
                                }
                                if (passed) {
                                    break;
                                }
                            }
                            if (passed) {
                                MaplePacket packet;
                                if (mode == 1) {
                                    packet = MaplePacketCreator.showZakumShrine(spawned, 0);
                                }
                                else if (mode == 2) {
                                    packet = MaplePacketCreator.showChaosZakumShrine(spawned, 0);
                                }
                                else {
                                    packet = MaplePacketCreator.showHorntailShrine(spawned, 0);
                                }
                                for (final MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                    chr.getClient().getSession().write((Object)packet);
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0));
                                }
                                MapleMap.this.checkStates("");
                                MapleMap.this.resetFully();
                            }
                        }
                    }
                };
            }
            else {
                run = new Runnable() {
                    @Override
                    public void run() {
                        final MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            MaplePacket packet;
                            if (mode == 1) {
                                packet = MaplePacketCreator.showZakumShrine(spawned, 0);
                            }
                            else if (mode == 2) {
                                packet = MaplePacketCreator.showChaosZakumShrine(spawned, 0);
                            }
                            else {
                                packet = MaplePacketCreator.showHorntailShrine(spawned, 0);
                            }
                            for (final MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                chr.getClient().getSession().write((Object)packet);
                                chr.changeMap(returnMapz, returnMapz.getPortal(0));
                            }
                            MapleMap.this.checkStates("");
                            MapleMap.this.resetFully();
                        }
                    }
                };
            }
            this.squadSchedule = Timer.MapTimer.getInstance().schedule(run, 300000L);
        }
    }
    
    public final MapleSquad getSquadByMap() {
        MapleSquad.MapleSquadType zz = null;
        switch (this.mapid) {
            case 105100300:
            case 105100400: {
                zz = MapleSquad.MapleSquadType.bossbalrog;
                break;
            }
            case 280030000: {
                zz = MapleSquad.MapleSquadType.zak;
                break;
            }
            case 280030001: {
                zz = MapleSquad.MapleSquadType.chaoszak;
                break;
            }
            case 240060200: {
                zz = MapleSquad.MapleSquadType.horntail;
                break;
            }
            case 240060201: {
                zz = MapleSquad.MapleSquadType.chaosht;
                break;
            }
            case 270050100: {
                zz = MapleSquad.MapleSquadType.pinkbean;
                break;
            }
            case 802000111: {
                zz = MapleSquad.MapleSquadType.nmm_squad;
                break;
            }
            case 802000211: {
                zz = MapleSquad.MapleSquadType.vergamot;
                break;
            }
            case 802000311: {
                zz = MapleSquad.MapleSquadType.tokyo_2095;
                break;
            }
            case 802000411: {
                zz = MapleSquad.MapleSquadType.dunas;
                break;
            }
            case 802000611: {
                zz = MapleSquad.MapleSquadType.nibergen_squad;
                break;
            }
            case 802000711: {
                zz = MapleSquad.MapleSquadType.dunas2;
                break;
            }
            case 802000801:
            case 802000802:
            case 802000803: {
                zz = MapleSquad.MapleSquadType.core_blaze;
                break;
            }
            case 802000821:
            case 802000823: {
                zz = MapleSquad.MapleSquadType.aufheben;
                break;
            }
            case 211070100:
            case 211070101:
            case 211070110: {
                zz = MapleSquad.MapleSquadType.vonleon;
                break;
            }
            case 551030200: {
                zz = MapleSquad.MapleSquadType.scartar;
                break;
            }
            case 271040100: {
                zz = MapleSquad.MapleSquadType.cygnus;
                break;
            }
            default: {
                return null;
            }
        }
        return ChannelServer.getInstance(this.channel).getMapleSquad(zz);
    }
    
    public final MapleSquad getSquadBegin() {
        if (this.squad != null) {
            return ChannelServer.getInstance(this.channel).getMapleSquad(this.squad);
        }
        return null;
    }
    
    public final EventManager getEMByMap() {
        String em = null;
        switch (this.mapid) {
            case 105100400: {
                em = "BossBalrog_EASY";
                break;
            }
            case 105100300: {
                em = "BossBalrog_NORMAL";
                break;
            }
            case 280030000: {
                em = "ZakumBattle";
                break;
            }
            case 240060200: {
                em = "HorntailBattle";
                break;
            }
            case 280030001: {
                em = "ChaosZakum";
                break;
            }
            case 240060201: {
                em = "ChaosHorntail";
                break;
            }
            case 270050100: {
                em = "PinkBeanBattle";
                break;
            }
            case 802000111: {
                em = "NamelessMagicMonster";
                break;
            }
            case 802000211: {
                em = "Vergamot";
                break;
            }
            case 802000311: {
                em = "2095_tokyo";
                break;
            }
            case 802000411: {
                em = "Dunas";
                break;
            }
            case 802000611: {
                em = "Nibergen";
                break;
            }
            case 802000711: {
                em = "Dunas2";
                break;
            }
            case 802000801:
            case 802000802:
            case 802000803: {
                em = "CoreBlaze";
                break;
            }
            case 802000821:
            case 802000823: {
                em = "Aufhaven";
                break;
            }
            case 211070100:
            case 211070101:
            case 211070110: {
                em = "VonLeonBattle";
                break;
            }
            case 551030200: {
                em = "ScarTarBattle";
                break;
            }
            case 271040100: {
                em = "CygnusBattle";
                break;
            }
            case 262030300: {
                em = "HillaBattle";
                break;
            }
            case 262031300: {
                em = "DarkHillaBattle";
                break;
            }
            case 272020110:
            case 272030400: {
                em = "ArkariumBattle";
                break;
            }
            case 955000100:
            case 955000200:
            case 955000300: {
                em = "AswanOffSeason";
                break;
            }
            case 280030100: {
                em = "ZakumBattle";
                break;
            }
            case 272020200: {
                em = "Akayile";
                break;
            }
            case 689013000: {
                em = "PinkZakum";
                break;
            }
            case 703200400: {
                em = "0AllBoss";
                break;
            }
            default: {
                return null;
            }
        }
        return ChannelServer.getInstance(this.channel).getEventSM().getEventManager(em);
    }
    
    public final void removePlayer(final MapleCharacter chr) {
        if (this.everlast) {
            this.returnEverLastItem(chr);
        }
        this.charactersLock.writeLock().lock();
        try {
            this.characters.remove(chr);
        }
        finally {
            this.charactersLock.writeLock().unlock();
        }
        this.removeMapObject(chr);
        chr.checkFollow();
        this.broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        if (!chr.isClone()) {
            final List<MapleMonster> update = new ArrayList<MapleMonster>();
            final Iterator<MapleMonster> controlled = chr.getControlled().iterator();
            while (controlled.hasNext()) {
                final MapleMonster monster = controlled.next();
                if (monster != null) {
                    monster.setController(null);
                    monster.setControllerHasAggro(false);
                    monster.setControllerKnowsAboutAggro(false);
                    controlled.remove();
                    update.add(monster);
                }
            }
            for (final MapleMonster mons : update) {
                this.updateMonsterController(mons);
            }
            chr.leaveMap();
            this.checkStates(chr.getName());
            if (this.mapid == 109020001) {
                chr.canTalk(true);
            }
            for (final WeakReference<MapleCharacter> chrz : chr.getClones()) {
                if (chrz.get() != null) {
                    this.removePlayer(chrz.get());
                }
            }
        }
        chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        boolean cancelSummons = false;
        for (final MapleSummon summon : chr.getSummons().values()) {
            if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || summon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                cancelSummons = true;
            }
            else {
                summon.setChangedMap(true);
                this.removeMapObject(summon);
            }
        }
        if (cancelSummons) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
        }
        if (chr.getDragon() != null) {
            this.removeMapObject(chr.getDragon());
        }
    }
    
    public List<MapleMapObject> getAllPlayers() {
        return this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
    }
    
    public final void broadcastMessage(final MaplePacket packet) {
        this.broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }
    
    public final void broadcastMessage(final MapleCharacter source, final MaplePacket packet, final boolean repeatToSource) {
        this.broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }
    
    public final void broadcastMessage(final MaplePacket packet, final Point rangedFrom) {
        this.broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }
    
    public final void broadcastMessage(final MapleCharacter source, final MaplePacket packet, final Point rangedFrom) {
        this.broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }
    
    private void broadcastMessage(final MapleCharacter source, final MaplePacket packet, final double rangeSq, final Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter chr : this.characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) > rangeSq) {
                            continue;
                        }
                        chr.getClient().getSession().write((Object)packet);
                    }
                    else {
                        chr.getClient().getSession().write((Object)packet);
                    }
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }
    
    private void sendObjectPlacement(final MapleCharacter c) {
        if (c == null || c.isClone()) {
            return;
        }
        for (final MapleMapObject o : this.getAllMonstersThreadsafe()) {
            this.updateMonsterController((MapleMonster)o);
        }
        for (final MapleMapObject o : this.getMapObjectsInRange(c.getPosition(), GameConstants.maxViewRangeSq(), GameConstants.rangedMapobjectTypes)) {
            if (o.getType() == MapleMapObjectType.REACTOR && !((MapleReactor)o).isAlive()) {
                continue;
            }
            o.sendSpawnData(c.getClient());
            c.addVisibleMapObject(o);
        }
    }
    
    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq) {
        final List<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (final MapleMapObjectType type : MapleMapObjectType.values()) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                for (final MapleMapObject mmo : this.mapobjects.get(type).values()) {
                    if (from.distanceSq(mmo.getPosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            }
            finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }
    
    public List<MapleMapObject> getItemsInRange(final Point from, final double rangeSq) {
        return this.getMapObjectsInRange(from, rangeSq, Arrays.asList(MapleMapObjectType.ITEM));
    }
    
    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (final MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                for (final MapleMapObject mmo : this.mapobjects.get(type).values()) {
                    if (from.distanceSq(mmo.getPosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            }
            finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }
    
    public final List<MapleMapObject> getMapObjectsInRect(final Rectangle box, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (final MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                for (final MapleMapObject mmo : this.mapobjects.get(type).values()) {
                    if (box.contains(mmo.getPosition())) {
                        ret.add(mmo);
                    }
                }
            }
            finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }
    
    public final List<MapleCharacter> getPlayersInRectAndInList(final Rectangle box, final List<MapleCharacter> chrList) {
        final List<MapleCharacter> character = new LinkedList<MapleCharacter>();
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter a : this.characters) {
                if (chrList.contains(a) && box.contains(a.getPosition())) {
                    character.add(a);
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return character;
    }
    
    public final void addPortal(final MaplePortal myPortal) {
        this.portals.put(myPortal.getId(), myPortal);
    }
    
    public final MaplePortal getPortal(final String portalname) {
        for (final MaplePortal port : this.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }
    
    public final MaplePortal getPortal(final int portalid) {
        return this.portals.get(portalid);
    }
    
    public final void resetPortals() {
        for (final MaplePortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }
    
    public final void setFootholds(final MapleFootholdTree footholds) {
        this.footholds = footholds;
    }
    
    public final MapleFootholdTree getFootholds() {
        return this.footholds;
    }
    
    public final void loadMonsterRate(final boolean first) {
        final int spawnSize = this.monsterSpawn.size();
        this.maxRegularSpawn = Math.round(spawnSize * this.monsterRate);
        if (this.maxRegularSpawn < 2) {
            this.maxRegularSpawn = 2;
        }
        else if (this.maxRegularSpawn > spawnSize) {
            this.maxRegularSpawn = spawnSize - spawnSize / 15;
        }
        if (this.fixedMob > 0) {
            this.maxRegularSpawn = this.fixedMob;
        }
        final Collection<Spawns> newSpawn = new LinkedList<Spawns>();
        final Collection<Spawns> newBossSpawn = new LinkedList<Spawns>();
        for (final Spawns s : this.monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue;
            }
            if (s.getMonster().getStats().isBoss()) {
                newBossSpawn.add(s);
            }
            else {
                newSpawn.add(s);
            }
        }
        this.monsterSpawn.clear();
        this.monsterSpawn.addAll(newBossSpawn);
        this.monsterSpawn.addAll(newSpawn);
        if (first && spawnSize > 0) {
            this.lastSpawnTime = System.currentTimeMillis();
            if (GameConstants.isForceRespawn(this.mapid)) {
                this.createMobInterval = 15000;
            }
        }
    }
    
    public final SpawnPoint addMonsterSpawn(final MapleMonster monster, final int mobTime, final byte carnivalTeam, final String msg) {
        final Point calcPointBelow;
        final Point newpos = calcPointBelow = this.calcPointBelow(monster.getPosition());
        --calcPointBelow.y;
        final SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            this.monsterSpawn.add(0, sp);
        }
        else {
            this.monsterSpawn.add(sp);
        }
        return sp;
    }
    
    public final void addAreaMonsterSpawn(final MapleMonster monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg) {
        pos1 = this.calcPointBelow(pos1);
        pos2 = this.calcPointBelow(pos2);
        pos3 = this.calcPointBelow(pos3);
        if (pos1 != null) {
            final Point point = pos1;
            --point.y;
        }
        if (pos2 != null) {
            final Point point2 = pos2;
            --point2.y;
        }
        if (pos3 != null) {
            final Point point3 = pos3;
            --point3.y;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + this.mapid + ", monster " + monster.getId() + " could not be spawned.");
            return;
        }
        if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        }
        else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        }
        else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        this.monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg));
    }
    
    public final List<MapleCharacter> getCharacters() {
        return this.getCharactersThreadsafe();
    }
    
    public final List<MapleCharacter> getCharactersThreadsafe() {
        final List<MapleCharacter> chars = new ArrayList<MapleCharacter>();
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter mc : this.characters) {
                chars.add(mc);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return chars;
    }
    
    public final MapleCharacter getCharacterById_InMap(final int id) {
        return this.getCharacterById(id);
    }
    
    public final MapleCharacter getCharacterById(final int id) {
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter mc : this.characters) {
                if (mc.getId() == id) {
                    return mc;
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return null;
    }
    
    public final void updateMapObjectVisibility(final MapleCharacter chr, final MapleMapObject mo) {
        if (chr == null || chr.isClone()) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) {
            if (mo.getType() == MapleMapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= GameConstants.maxViewRangeSq()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        }
        else if (mo.getType() != MapleMapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > GameConstants.maxViewRangeSq()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        }
    }
    
    public void moveMonster(final MapleMonster monster, final Point reportedPos) {
        monster.setPosition(reportedPos);
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter mc : this.characters) {
                this.updateMapObjectVisibility(mc, monster);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }
    
    public void movePlayer(final MapleCharacter player, final Point newPosition) {
        player.setPosition(newPosition);
        if (!player.isClone()) {
            try {
                final Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
                final ArrayList<MapleMapObject> copy = new ArrayList<MapleMapObject>(visibleObjects);
                for (final MapleMapObject mo : copy) {
                    if (mo != null && this.getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                        this.updateMapObjectVisibility(player, mo);
                    }
                    else {
                        if (mo == null) {
                            continue;
                        }
                        visibleObjects.remove(mo);
                    }
                }
                for (final MapleMapObject mo2 : this.getMapObjectsInRange(player.getPosition(), GameConstants.maxViewRangeSq())) {
                    if (mo2 != null && !player.isMapObjectVisible(mo2)) {
                        mo2.sendSpawnData(player.getClient());
                        visibleObjects.add(mo2);
                    }
                }
            }
            finally {
                player.unlockWriteVisibleMapObjects();
            }
        }
    }
    
    public MaplePortal findClosestSpawnpoint(final Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (final MaplePortal portal : this.portals.values()) {
            final double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }
    
    public String spawnDebug() {
        final StringBuilder sb = new StringBuilder("Mapobjects in map : ");
        sb.append(this.getMapObjectSize());
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(this.spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(this.monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(this.maxRegularSpawn);
        sb.append(" actual monsters: ");
        sb.append(this.getNumMonsters());
        return sb.toString();
    }
    
    public int characterSize() {
        return this.characters.size();
    }
    
    public final int getMapObjectSize() {
        return this.mapobjects.size() + this.getCharactersSize() - this.characters.size();
    }
    
    public final int getCharactersSize() {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            for (final MapleCharacter chr : this.characters) {
                if (!chr.isClone()) {
                    ++ret;
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }
    
    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection((Collection<? extends MaplePortal>)this.portals.values());
    }
    
    public int getSpawnedMonstersOnMap() {
        return this.spawnedMonstersOnMap.get();
    }
    
    public void spawnLove(final MapleLove love) {
        this.addMapObject(love);
        this.broadcastMessage(love.makeSpawnData());
        final Timer.MapTimer tMan = Timer.MapTimer.getInstance();
        tMan.schedule(new Runnable() {
            @Override
            public void run() {
                MapleMap.this.removeMapObject(love);
                MapleMap.this.broadcastMessage(love.makeDestroyData());
            }
        }, 3600000L);
    }
    
    public void AutoNx(final int dy, final int dd) {
        for (final MapleCharacter chr : this.characters) {
            int 经验倍数 = 1;
            if (chr.getLevel() <= 30) {
                经验倍数 = Randomizer.nextInt(10) + 10;
            }
            else if (chr.getLevel() <= 70) {
                经验倍数 = Randomizer.nextInt(10) + 20;
            }
            else if (chr.getLevel() <= 120) {
                经验倍数 = Randomizer.nextInt(10) + 30;
            }
            else if (chr.getLevel() > 120) {
                经验倍数 = Randomizer.nextInt(10) + 40;
            }
            chr.gainExp(chr.getLevel() * 经验倍数, true, false, true);
            final int cashdy = 1 + Randomizer.nextInt(10) + dy;
            chr.modifyCSPoints(2, cashdy);
            final int cashdd = 1 + Randomizer.nextInt(10) + dd;
            chr.gainBeans(cashdd);
            chr.getClient().getSession().write((Object)MaplePacketCreator.serverNotice(5, "[系统奖励] 挂机获得[" + cashdy + "] 抵用卷,[" + cashdd + "] 豆豆,[" + chr.getLevel() * 经验倍数 + "] 经验"));
        }
    }
    
    public void respawn(final boolean force) {
        this.lastSpawnTime = System.currentTimeMillis();
        if (force) {
            final int numShouldSpawn = this.monsterSpawn.size() - this.spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                int spawned = 0;
                for (final Spawns spawnPoint : this.monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    if (++spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
        else {
            final int numShouldSpawn = this.maxRegularSpawn - this.spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                int spawned = 0;
                final List<Spawns> randomSpawn = new ArrayList<Spawns>(this.monsterSpawn);
                Collections.shuffle(randomSpawn);
                for (final Spawns spawnPoint2 : randomSpawn) {
                    if (!this.isSpawns && spawnPoint2.getMobTime() > 0) {
                        continue;
                    }
                    if (spawnPoint2.shouldSpawn() || GameConstants.isForceRespawn(this.mapid)) {
                        spawnPoint2.spawnMonster(this);
                        ++spawned;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }
    
    public String getSnowballPortal() {
        final int[] teamss = new int[2];
        for (final MapleCharacter chr : this.getCharactersThreadsafe()) {
            if (chr.getPosition().y > -80) {
                final int[] array = teamss;
                final int n = 0;
                ++array[n];
            }
            else {
                final int[] array2 = teamss;
                final int n2 = 1;
                ++array2[n2];
            }
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        }
        return "st00";
    }
    
    public boolean isDisconnected(final int id) {
        return this.dced.contains(id);
    }
    
    public void addDisconnected(final int id) {
        this.dced.add(id);
    }
    
    public void resetDisconnected() {
        this.dced.clear();
    }
    
    public void startSpeedRun() {
        final MapleSquad squad = this.getSquadByMap();
        if (squad != null) {
            for (final MapleCharacter chr : this.getCharactersThreadsafe()) {
                if (chr.getName().equals(squad.getLeaderName())) {
                    this.startSpeedRun(chr.getName());
                }
            }
        }
    }
    
    public void startSpeedRun(final String leader) {
        this.speedRunStart = System.currentTimeMillis();
        this.speedRunLeader = leader;
    }
    
    public void endSpeedRun() {
        this.speedRunStart = 0L;
        this.speedRunLeader = "";
    }
    
    public void getRankAndAdd(final String leader, final String time, final SpeedRunType type, final long timz, final Collection<String> squad) {
        try {
            final StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (final String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)");
            ps.setString(1, type.name());
            ps.setString(2, leader);
            ps.setString(3, time);
            ps.setLong(4, timz);
            ps.setString(5, z);
            ps.executeUpdate();
            ps.close();
            if (SpeedRunner.getInstance().getSpeedRunData(type) == null) {
                SpeedRunner.getInstance().addSpeedRunData(type, SpeedRunner.getInstance().addSpeedRunData(new StringBuilder("#rThese are the speedrun times for " + type + ".#k\r\n\r\n"), new HashMap<Integer, String>(), z, leader, 1, time));
            }
            else {
                SpeedRunner.getInstance().removeSpeedRunData(type);
                SpeedRunner.getInstance().loadSpeedRunData(type);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public long getSpeedRunStart() {
        return this.speedRunStart;
    }
    
    public final void disconnectAll() {
        for (final MapleCharacter chr : this.getCharactersThreadsafe()) {
            if (!chr.isGM()) {
                chr.getClient().disconnect(true, false);
                chr.getClient().getSession().close();
            }
        }
    }
    
    public List<MapleNPC> getAllNPCs() {
        return this.getAllNPCsThreadsafe();
    }
    
    public List<MapleNPC> getAllNPCsThreadsafe() {
        final ArrayList<MapleNPC> ret = new ArrayList<MapleNPC>();
        this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }
    
    public final void resetNPCs() {
        final List<MapleNPC> npcs = this.getAllNPCsThreadsafe();
        for (final MapleNPC npc : npcs) {
            if (npc.isCustom()) {
                this.broadcastMessage(MaplePacketCreator.spawnNPC(npc, false));
                this.removeMapObject(npc);
            }
        }
    }
    
    public final void resetFully() {
        this.resetFully(true);
    }
    
    public final void resetFully(final boolean respawn) {
        this.killAllMonsters(false);
        this.reloadReactors();
        this.removeDrops();
        this.resetNPCs();
        this.resetSpawns();
        this.resetDisconnected();
        this.endSpeedRun();
        this.cancelSquadSchedule();
        this.resetPortals();
        this.environment.clear();
        if (respawn) {
            this.respawn(true);
        }
    }
    
    public final void cancelSquadSchedule() {
        this.squadTimer = false;
        if (this.squadSchedule != null) {
            this.squadSchedule.cancel(false);
            this.squadSchedule = null;
        }
    }
    
    public final void removeDrops() {
        final List<MapleMapItem> items = this.getAllItemsThreadsafe();
        for (final MapleMapItem i : items) {
            i.expire(this);
        }
    }
    
    public final void resetAllSpawnPoint(final int mobid, final int mobTime) {
        final Collection<Spawns> sss = new LinkedList<Spawns>(this.monsterSpawn);
        this.resetFully();
        this.monsterSpawn.clear();
        for (final Spawns s : sss) {
            final MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            final MapleMonster oldMons = s.getMonster();
            newMons.setCy(oldMons.getCy());
            newMons.setF(oldMons.getF());
            newMons.setFh(oldMons.getFh());
            newMons.setRx0(oldMons.getRx0());
            newMons.setRx1(oldMons.getRx1());
            newMons.setPosition(new Point(oldMons.getPosition()));
            newMons.setHide(oldMons.isHidden());
            this.addMonsterSpawn(newMons, mobTime, (byte)(-1), null);
        }
        this.loadMonsterRate(true);
    }
    
    public final void resetSpawns() {
        boolean changed = false;
        final Iterator<Spawns> sss = this.monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        this.setSpawns(true);
        if (changed) {
            this.loadMonsterRate(true);
        }
    }
    
    public final boolean makeCarnivalSpawn(final int team, final MapleMonster newMons, final int num) {
        MapleNodes.MonsterPoint ret = null;
        for (final MapleNodes.MonsterPoint mp : this.nodes.getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                final Point calcPointBelow;
                final Point newpos = calcPointBelow = this.calcPointBelow(new Point(mp.x, mp.y));
                --calcPointBelow.y;
                boolean found = false;
                for (final Spawns s : this.monsterSpawn) {
                    if (s.getCarnivalId() > -1 && (mp.team == -1 || s.getCarnivalTeam() == mp.team) && s.getPosition().x == newpos.x && s.getPosition().y == newpos.y) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ret = mp;
                    break;
                }
                if (!found) {
                    ret = mp;
                    break;
                }
                continue;
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0);
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50);
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            final SpawnPoint sp = this.addMonsterSpawn(newMons, 1, (byte)team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }
    
    public final boolean makeCarnivalReactor(final int team, final int num) {
        final MapleReactor old = this.getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5) {
            return false;
        }
        Point guardz = null;
        final List<MapleReactor> react = this.getAllReactorsThreadsafe();
        for (final Pair<Point, Integer> guard : this.nodes.getGuardians()) {
            if (guard.right == team || guard.right == -1) {
                boolean found = false;
                for (final MapleReactor r : react) {
                    if (r.getPosition().x == guard.left.x && r.getPosition().y == guard.left.y && r.getState() < 5) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    guardz = guard.left;
                    break;
                }
                continue;
            }
        }
        if (guardz != null) {
            final MapleReactorStats stats = MapleReactorFactory.getReactor(9980000 + team);
            final MapleReactor my = new MapleReactor(stats, 9980000 + team);
            stats.setFacingDirection((byte)0);
            my.setPosition(guardz);
            my.setState((byte)1);
            my.setDelay(0);
            my.setName(team + "" + num);
            this.spawnReactor(my);
            final MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (final MapleMonster mons : this.getAllMonstersThreadsafe()) {
                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false);
                }
            }
        }
        return guardz != null;
    }
    
    public final void blockAllPortal() {
        for (final MaplePortal p : this.portals.values()) {
            p.setPortalState(false);
        }
    }
    
    public boolean getAndSwitchTeam() {
        return this.getCharactersSize() % 2 != 0;
    }
    
    public void setSquad(final MapleSquad.MapleSquadType s) {
        this.squad = s;
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public int getConsumeItemCoolTime() {
        return this.consumeItemCoolTime;
    }
    
    public void setConsumeItemCoolTime(final int ciit) {
        this.consumeItemCoolTime = ciit;
    }
    
    public void setPermanentWeather(final int pw) {
        this.permanentWeather = pw;
    }
    
    public int getPermanentWeather() {
        return this.permanentWeather;
    }
    
    public void checkStates(final String chr) {
        final MapleSquad sqd = this.getSquadByMap();
        final EventManager em = this.getEMByMap();
        final int size = this.getCharactersSize();
        if (sqd != null) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equals(chr)) {
                    em.setProperty("leader", "false");
                }
                if (chr.equals("") || size == 0) {
                    sqd.clear();
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    this.cancelSquadSchedule();
                }
            }
        }
        if (em != null && em.getProperty("state") != null && size == 0) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
                em.setProperty("leader", "true");
            }
        }
        if (this.speedRunStart > 0L && this.speedRunLeader.equalsIgnoreCase(chr)) {
            if (size > 0) {
                this.broadcastMessage(MaplePacketCreator.serverNotice(5, "队长不在地图上！你的挑战失败"));
            }
            this.endSpeedRun();
        }
    }
    
    public void setNodes(final MapleNodes mn) {
        this.nodes = mn;
    }
    
    public final List<MapleNodes.MaplePlatform> getPlatforms() {
        return this.nodes.getPlatforms();
    }
    
    public Collection<MapleNodes.MapleNodeInfo> getNodes() {
        return this.nodes.getNodes();
    }
    
    public MapleNodes.MapleNodeInfo getNode(final int index) {
        return this.nodes.getNode(index);
    }
    
    public final List<Rectangle> getAreas() {
        return this.nodes.getAreas();
    }
    
    public final Rectangle getArea(final int index) {
        return this.nodes.getArea(index);
    }
    
    public final void changeEnvironment(final String ms, final int type) {
        this.broadcastMessage(MaplePacketCreator.environmentChange(ms, type));
    }
    
    public final void toggleEnvironment(final String ms) {
        if (this.environment.containsKey(ms)) {
            this.moveEnvironment(ms, (this.environment.get(ms) == 1) ? 2 : 1);
        }
        else {
            this.moveEnvironment(ms, 1);
        }
    }
    
    public final void moveEnvironment(final String ms, final int type) {
        this.environment.put(ms, type);
    }
    
    public final Map<String, Integer> getEnvironment() {
        return this.environment;
    }
    
    public final int getNumPlayersInArea(final int index) {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = this.characters.iterator();
            while (ltr.hasNext()) {
                if (this.getArea(index).contains(ltr.next().getPosition())) {
                    ++ret;
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }
    
    public void broadcastGMMessage(final MapleCharacter source, final MaplePacket packet, final boolean repeatToSource) {
        this.broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }
    
    private void broadcastGMMessage(final MapleCharacter source, final MaplePacket packet, final double rangeSq, final Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (final MapleCharacter chr : this.characters) {
                    if (chr.isStaff()) {
                        chr.getClient().getSession().write((Object)packet);
                    }
                }
            }
            else {
                for (final MapleCharacter chr : this.characters) {
                    if (chr != source && chr.getGMLevel() >= source.getGMLevel()) {
                        chr.getClient().getSession().write((Object)packet);
                    }
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }
    
    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }
    
    public final List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }
    
    public final boolean canSpawn() {
        return this.lastSpawnTime > 0L && this.isSpawns && this.lastSpawnTime + this.createMobInterval < System.currentTimeMillis();
    }
    
    public final boolean canHurt() {
        if (this.lastHurtTime > 0L && this.lastHurtTime + this.decHPInterval < System.currentTimeMillis()) {
            this.lastHurtTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
    public List<Integer> getAllUniqueMonsters() {
        final ArrayList ret = new ArrayList();
        this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                final int theId = ((MapleMonster)mmo).getId();
                if (!ret.contains(theId)) {
                    ret.add(theId);
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return (List<Integer>)ret;
    }
    
    public int getNumPlayersItemsInArea(final int index) {
        return this.getNumPlayersItemsInRect(this.getArea(index));
    }
    
    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int ret = this.getNumPlayersInRect(rect);
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (final MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (rect.contains(mmo.getPosition())) {
                    ++ret;
                }
            }
        }
        finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }
    
    public int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            final Iterator ltr = this.characters.iterator();
            while (ltr.hasNext()) {
                if (rect.contains(((AbstractMapleMapObject) ltr.next()).getTruePosition())) {
                    ++ret;
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }
    
    public final void resetAriantPQ(final int level) {
        this.killAllMonsters(true);
        this.reloadReactors();
        this.removeDrops();
        this.resetNPCs();
        this.resetSpawns();
        this.resetDisconnected();
        this.endSpeedRun();
        this.resetPortals();
        this.environment.clear();
        this.respawn(true);
        for (final MapleMonster mons : this.getAllMonstersThreadsafe()) {
            mons.changeLevel(level, true);
        }
        this.resetSpawnLevel(level);
    }
    
    public final void resetSpawnLevel(final int level) {
        for (final Spawns spawn : this.monsterSpawn) {
            if (spawn instanceof SpawnPoint) {
                ((SpawnPoint)spawn).setLevel(level);
            }
        }
    }
    
    public void setDocked(final boolean x) {
    }
    
    public void spawnRabbit(int hp) {
        hp = 100000;
        final int mid = 9300061;
        final MapleMonster onemob = MapleLifeFactory.getMonster(mid);
        final OverrideMonsterStats overrideStats = new OverrideMonsterStats(hp, onemob.getMobMaxMp(), 0, false);
        final MapleMonster mob = MapleLifeFactory.getMonster(mid);
        mob.setHp(hp);
        mob.setOverrideStats(overrideStats);
        this.spawnMonsterOnGroundBelow(mob, new Point(-183, -433));
    }
    
    public final void KillFk(final boolean animate) {
        final List<MapleMapObject> monsters = this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
        for (final MapleMapObject monstermo : monsters) {
            final MapleMonster monster = (MapleMonster)monstermo;
            if (monster.getId() == 3230300 || monster.getId() == 3230301) {
                this.spawnedMonstersOnMap.decrementAndGet();
                monster.setHp(0L);
                this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), (int)(animate ? 1 : 0)));
                this.removeMapObject(monster);
                monster.killed();
            }
        }
    }
    
    public final int mobCount() {
        final List<MapleMapObject> mobsCount = this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
        return mobsCount.size();
    }
    
    public final int playerCount() {
        final List<MapleMapObject> players = this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
        return players.size();
    }
    
    public void killMonster_2(final MapleMonster monster) {
        if (monster == null) {
            return;
        }
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0L);
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), (monster.getStats().getSelfD() < 0) ? 1 : monster.getStats().getSelfD()));
        this.removeMapObject(monster);
        monster.killed();
    }
    
    public void reloadCPQ() {
        final int[] maps = { 980000101, 980000201, 980000301, 980000401, 980000501, 980000601 };
        for (int i = 0; i < maps.length; ++i) {
            final int mapid = maps[i];
            for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().destroyMap(mapid, true);
                cserv.getMapFactory().HealMap(mapid);
            }
        }
    }
    
    private class ActivateItemReactor implements Runnable
    {
        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;
        
        public ActivateItemReactor(final MapleMapItem mapitem, final MapleReactor reactor, final MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }
        
        @Override
        public void run() {
            if (this.mapitem != null && this.mapitem == MapleMap.this.getMapObject(this.mapitem.getObjectId(), this.mapitem.getType())) {
                if (this.mapitem.isPickedUp()) {
                    this.reactor.setTimerActive(false);
                    return;
                }
                this.mapitem.expire(MapleMap.this);
                this.reactor.hitReactor(this.c);
                this.reactor.setTimerActive(false);
                if (this.reactor.getDelay() > 0) {
                    Timer.MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            ActivateItemReactor.this.reactor.forceHitReactor((byte)0);
                        }
                    }, this.reactor.getDelay());
                }
            }
            else {
                this.reactor.setTimerActive(false);
            }
        }
    }
    
    private interface SpawnCondition
    {
        boolean canSpawn(final MapleCharacter p0);
    }
    
    private interface DelayedPacketCreation
    {
        void sendPackets(final MapleClient p0);
    }
}
