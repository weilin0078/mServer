package server;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleDisease;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import provider.MapleData;
import provider.MapleDataTool;
import server.life.MapleMonster;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleStatEffect implements Serializable
{
    private static final long serialVersionUID = 9179541993413738569L;
    private byte mastery;
    private byte mhpR;
    private byte mmpR;
    private byte mobCount;
    private byte attackCount;
    private byte bulletCount;
    private short hp;
    private short mp;
    private short watk;
    private short matk;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short hands;
    private short speed;
    private short jump;
    private short mpCon;
    private short hpCon;
    private short damage;
    private short prop;
    private short ehp;
    private short emp;
    private short ewatk;
    private short ewdef;
    private short emdef;
    private double hpR;
    private double mpR;
    private int duration;
    private int sourceid;
    private int moveTo;
    private int x;
    private int y;
    private int z;
    private int itemCon;
    private int itemConNo;
    private int bulletConsume;
    private int moneyCon;
    private int cooldown;
    private int morphId;
    private int expinc;
    private boolean overTime;
    private boolean skill;
    private boolean partyBuff;
    private List<Pair<MapleBuffStat, Integer>> statups;
    private Map<MonsterStatus, Integer> monsterStatus;
    private Point lt;
    private Point rb;
    private int expBuff;
    private int itemup;
    private int mesoup;
    private int cashup;
    private int berserk;
    private int illusion;
    private int booster;
    private int berserk2;
    private int cp;
    private int nuffSkill;
    private byte level;
    private List<MapleDisease> cureDebuffs;
    
    public MapleStatEffect() {
        this.morphId = 0;
        this.partyBuff = true;
    }
    
    public static final MapleStatEffect loadSkillEffectFromData(final MapleData source, final int skillid, final boolean overtime, final byte level) {
        return loadFromData(source, skillid, true, overtime, level);
    }
    
    public static final MapleStatEffect loadItemEffectFromData(final MapleData source, final int itemid) {
        return loadFromData(source, itemid, false, false, (byte)1);
    }
    
    private static final void addBuffStatPairToListIfNotZero(final List<Pair<MapleBuffStat, Integer>> list, final MapleBuffStat buffstat, final Integer val) {
        if (val != 0) {
            list.add(new Pair<MapleBuffStat, Integer>(buffstat, val));
        }
    }
    
    private static MapleStatEffect loadFromData(final MapleData source, final int sourceid, final boolean skill, final boolean overTime, final byte level) {
        final MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = level;
        if (source == null) {
            return ret;
        }
        ret.duration = MapleDataTool.getIntConvert("time", source, -1);
        ret.hp = (short)MapleDataTool.getInt("hp", source, 0);
        ret.hpR = MapleDataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short)MapleDataTool.getInt("mp", source, 0);
        ret.mpR = MapleDataTool.getInt("mpR", source, 0) / 100.0;
        ret.mhpR = (byte)MapleDataTool.getInt("mhpR", source, 0);
        ret.mmpR = (byte)MapleDataTool.getInt("mmpR", source, 0);
        ret.mpCon = (short)MapleDataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short)MapleDataTool.getInt("hpCon", source, 0);
        ret.prop = (short)MapleDataTool.getInt("prop", source, 100);
        ret.cooldown = MapleDataTool.getInt("cooltime", source, 0);
        ret.expinc = MapleDataTool.getInt("expinc", source, 0);
        ret.morphId = MapleDataTool.getInt("morph", source, 0);
        ret.cp = MapleDataTool.getInt("cp", source, 0);
        ret.nuffSkill = MapleDataTool.getInt("nuffSkill", source, 0);
        ret.mobCount = (byte)MapleDataTool.getInt("mobCount", source, 1);
        if (skill) {
            switch (sourceid) {
                case 1100002:
                case 1100003:
                case 1200002:
                case 1200003:
                case 1300002:
                case 1300003:
                case 3100001:
                case 3200001:
                case 11101002:
                case 13101002: {
                    ret.mobCount = 6;
                    break;
                }
            }
        }
        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        }
        else {
            final MapleStatEffect mapleStatEffect = ret;
            mapleStatEffect.duration *= 1000;
            ret.overTime = (overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack());
        }
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
        ret.mastery = (byte)MapleDataTool.getInt("mastery", source, 0);
        ret.watk = (short)MapleDataTool.getInt("pad", source, 0);
        ret.wdef = (short)MapleDataTool.getInt("pdd", source, 0);
        ret.matk = (short)MapleDataTool.getInt("mad", source, 0);
        ret.mdef = (short)MapleDataTool.getInt("mdd", source, 0);
        ret.ehp = (short)MapleDataTool.getInt("emhp", source, 0);
        ret.emp = (short)MapleDataTool.getInt("emmp", source, 0);
        ret.ewatk = (short)MapleDataTool.getInt("epad", source, 0);
        ret.ewdef = (short)MapleDataTool.getInt("epdd", source, 0);
        ret.emdef = (short)MapleDataTool.getInt("emdd", source, 0);
        ret.acc = (short)MapleDataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short)MapleDataTool.getInt("eva", source, 0);
        ret.speed = (short)MapleDataTool.getInt("speed", source, 0);
        ret.jump = (short)MapleDataTool.getInt("jump", source, 0);
        ret.expBuff = MapleDataTool.getInt("expBuff", source, 0);
        ret.cashup = MapleDataTool.getInt("cashBuff", source, 0);
        ret.itemup = MapleDataTool.getInt("itemupbyitem", source, 0);
        ret.mesoup = MapleDataTool.getInt("mesoupbyitem", source, 0);
        ret.berserk = MapleDataTool.getInt("berserk", source, 0);
        ret.berserk2 = MapleDataTool.getInt("berserk2", source, 0);
        ret.booster = MapleDataTool.getInt("booster", source, 0);
        ret.illusion = MapleDataTool.getInt("illusion", source, 0);
        final List<MapleDisease> cure = new ArrayList<MapleDisease>(5);
        if (MapleDataTool.getInt("poison", source, 0) > 0) {
            cure.add(MapleDisease.POISON);
        }
        if (MapleDataTool.getInt("seal", source, 0) > 0) {
            cure.add(MapleDisease.SEAL);
        }
        if (MapleDataTool.getInt("darkness", source, 0) > 0) {
            cure.add(MapleDisease.DARKNESS);
        }
        if (MapleDataTool.getInt("weakness", source, 0) > 0) {
            cure.add(MapleDisease.WEAKEN);
        }
        if (MapleDataTool.getInt("curse", source, 0) > 0) {
            cure.add(MapleDisease.CURSE);
        }
        ret.cureDebuffs = cure;
        final MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point)ltd.getData();
            ret.rb = (Point)source.getChildByPath("rb").getData();
        }
        ret.x = MapleDataTool.getInt("x", source, 0);
        ret.y = MapleDataTool.getInt("y", source, 0);
        ret.z = MapleDataTool.getInt("z", source, 0);
        ret.damage = (short)MapleDataTool.getIntConvert("damage", source, 100);
        ret.attackCount = (byte)MapleDataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = (byte)MapleDataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = MapleDataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = MapleDataTool.getIntConvert("moneyCon", source, 0);
        ret.itemCon = MapleDataTool.getInt("itemCon", source, 0);
        ret.itemConNo = MapleDataTool.getInt("itemConNo", source, 0);
        ret.moveTo = MapleDataTool.getInt("moveTo", source, -1);
        final Map<MonsterStatus, Integer> monsterStatus = new EnumMap<MonsterStatus, Integer>(MonsterStatus.class);
        if (ret.overTime && ret.getSummonMovementType() == null) {
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WATK, (int)ret.watk);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WDEF, (int)ret.wdef);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MATK, (int)ret.matk);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MDEF, (int)ret.mdef);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACC, (int)ret.acc);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AVOID, (int)ret.avoid);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.SPEED, (int)ret.speed);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.JUMP, (int)ret.jump);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXHP, (int)ret.mhpR);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXMP, (int)ret.mmpR);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.EXPRATE, ret.expBuff);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACASH_RATE, ret.cashup);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.DROP_RATE, ret.itemup * 200);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MESO_RATE, ret.mesoup * 200);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BERSERK_FURY, ret.berserk2);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.PYRAMID_PQ, ret.berserk);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BOOSTER, ret.booster);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ILLUSION, ret.illusion);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WATK, (int)ret.ewatk);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WDEF, (int)ret.ewdef);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MDEF, (int)ret.emdef);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXHP, (int)ret.ehp);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXMP, (int)ret.ehp);
        }
        if (skill) {
            switch (sourceid) {
                case 2001002:
                case 12001001:
                case 22111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_GUARD, ret.x));
                    break;
                }
                case 2301003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INVINCIBLE, ret.x));
                    break;
                }
                case 35001002:
                case 35120000: {
                    ret.duration = 7200000;
                    break;
                }
                case 9001004: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                }
                case 4001003:
                case 4330001:
                case 13101006:
                case 14001003:
                case 30001001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                }
                case 4211003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PICKPOCKET, ret.x));
                    break;
                }
                case 4211005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOGUARD, ret.x));
                    break;
                }
                case 4111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOUP, ret.x));
                    break;
                }
                case 4111002:
                case 14111000: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, ret.x));
                    break;
                }
                case 11101002:
                case 13101002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINALATTACK, ret.x));
                    break;
                }
                case 8001:
                case 2311002:
                case 3101004:
                case 3201004:
                case 10008001:
                case 13101003:
                case 20008001:
                case 20018001:
                case 30008001:
                case 33101003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, ret.x));
                    break;
                }
                case 1211003:
                case 1211004:
                case 1211005:
                case 1211006:
                case 1211007:
                case 1211008:
                case 1221003:
                case 1221004:
                case 11111007:
                case 15101006:
                case 21111005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WK_CHARGE, ret.x));
                    break;
                }
                case 12101005:
                case 22121001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ELEMENT_RESET, ret.x));
                    break;
                }
                case 3121008: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CONCENTRATE, ret.x));
                    break;
                }
                case 5110001:
                case 15100004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, 0));
                    break;
                }
                case 1101004:
                case 1101005:
                case 1201004:
                case 1201005:
                case 1301004:
                case 1301005:
                case 2111005:
                case 2211005:
                case 3101002:
                case 3201002:
                case 4101003:
                case 4201002:
                case 4301002:
                case 5101006:
                case 5201003:
                case 11101001:
                case 12101004:
                case 13101001:
                case 14101002:
                case 15101002:
                case 21001003:
                case 22141002:
                case 32101005:
                case 33001003:
                case 35001003:
                case 35101006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BOOSTER, ret.x));
                    break;
                }
                case 5121009:
                case 15111005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPEED_INFUSION, ret.x));
                    break;
                }
                case 4321000: {
                    ret.duration = 1000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_SPEED, 100 + ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_JUMP, ret.y));
                    break;
                }
                case 5001005:
                case 15001003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_SPEED, ret.x * 2));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_JUMP, ret.y * 2));
                    break;
                }
                case 1101007:
                case 1201007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.POWERGUARD, ret.x));
                    break;
                }
                case 32111004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CONVERSION, ret.x));
                    break;
                }
                case 8003:
                case 1301007:
                case 9001008:
                case 10008003:
                case 20008003:
                case 20018003:
                case 30008003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXMP, ret.y));
                    break;
                }
                case 1001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.RECOVERY, ret.x));
                    break;
                }
                case 1111002:
                case 11111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
                    break;
                }
                case 21120007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO_BARRIER, ret.x));
                    break;
                }
                case 5211006:
                case 5220011:
                case 22151002: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOMING_BEACON, ret.x));
                    break;
                }
                case 1011:
                case 10001011:
                case 20001011:
                case 20011011:
                case 30001011: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BERSERK_FURY, 1));
                    break;
                }
                case 1010:
                case 10001010:
                case 20001010:
                case 20011010:
                case 30001010: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DIVINE_BODY, 1));
                    break;
                }
                case 1311006: {
                    ret.hpR = -ret.x / 100.0;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DRAGON_ROAR, ret.y));
                    break;
                }
                case 4341007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.THORNS, ret.x << 8 | ret.y));
                    break;
                }
                case 4341002: {
                    ret.duration = 60000;
                    ret.hpR = -ret.x / 100.0;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINAL_CUT, ret.y));
                    break;
                }
                case 4331002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MIRROR_IMAGE, ret.x));
                    break;
                }
                case 4331003: {
                    ret.duration = 60000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.OWL_SPIRIT, ret.y));
                    break;
                }
                case 1311008: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DRAGONBLOOD, ret.x));
                    break;
                }
                case 1121000:
                case 1221000:
                case 1321000:
                case 2121000:
                case 2221000:
                case 2321000:
                case 3121000:
                case 3221000:
                case 4121000:
                case 4221000:
                case 4341000:
                case 5121000:
                case 5221000:
                case 21121000:
                case 22171000:
                case 32121007:
                case 33121007:
                case 35121007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAPLE_WARRIOR, ret.x));
                    break;
                }
                case 15111006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPARK, ret.x));
                    break;
                }
                case 8002:
                case 3121002:
                case 3221002:
                case 10008002:
                case 20008002:
                case 20018002:
                case 30008002:
                case 33121004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHARP_EYES, ret.x << 8 | ret.y));
                    break;
                }
                case 22151003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_RESISTANCE, ret.x));
                    break;
                }
                case 21101003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BODY_PRESSURE, ret.x));
                    break;
                }
                case 21000000: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ARAN_COMBO, 100));
                    break;
                }
                case 21100005:
                case 32101004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO_DRAIN, ret.x));
                    break;
                }
                case 21111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SMART_KNOCKBACK, ret.x));
                    break;
                }
                case 22131001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_SHIELD, ret.x));
                    break;
                }
                case 22181003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOUL_STONE, 1));
                    break;
                }
                case 4001002:
                case 14001002: {
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                }
                case 5221009: {
                    monsterStatus.put(MonsterStatus.HYPNOTIZE, 1);
                    break;
                }
                case 1201006: {
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                }
                case 1111005:
                case 1111006:
                case 1111008:
                case 1211002:
                case 3101005:
                case 4121008:
                case 4201004:
                case 4211002:
                case 4221007:
                case 4331005:
                case 5101002:
                case 5101003:
                case 5111002:
                case 5121004:
                case 5121005:
                case 5121007:
                case 5201004:
                case 15101005:
                case 22151001:
                case 32111010:
                case 32121004:
                case 33101001:
                case 33101002:
                case 33111002:
                case 33121002:
                case 35101003:
                case 35111015: {
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                }
                case 4321002: {
                    monsterStatus.put(MonsterStatus.DARKNESS, 1);
                    break;
                }
                case 4121003:
                case 4221003:
                case 33121005: {
                    monsterStatus.put(MonsterStatus.SHOWDOWN, ret.x);
                    monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                }
                case 2121006:
                case 2201004:
                case 2211002:
                case 2211006:
                case 2221007:
                case 3211003:
                case 5211005:
                case 21120006:
                case 22121000: {
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    final MapleStatEffect mapleStatEffect2 = ret;
                    mapleStatEffect2.duration *= 2;
                    break;
                }
                case 2101003:
                case 2201003:
                case 12101001:
                case 22141003: {
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                }
                case 2101005:
                case 2111006:
                case 2121003:
                case 2221003:
                case 3111003:
                case 22161002: {
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                }
                case 4121004:
                case 4221004: {
                    monsterStatus.put(MonsterStatus.NINJA_AMBUSH, (int)ret.damage);
                    break;
                }
                case 2311005: {
                    monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                }
                case 32111006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.REAPER, 1));
                    break;
                }
                case 3111002:
                case 3211002:
                case 4341006:
                case 5211001:
                case 5220002:
                case 13111004:
                case 33111003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PUPPET, 1));
                    break;
                }
                case 3111005:
                case 3211005:
                case 33111005:
                case 35111002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                }
                case 2121005:
                case 3221005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                }
                case 1321007:
                case 2221005:
                case 2311006:
                case 2321003:
                case 3121006:
                case 5211002:
                case 11001004:
                case 12001004:
                case 12111004:
                case 13001004:
                case 14001005:
                case 15001004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    break;
                }
                case 2311003:
                case 9001002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SYMBOL, ret.x));
                    break;
                }
                case 2111004:
                case 2211004:
                case 12111002: {
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                }
                case 4111003:
                case 14111001: {
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                }
                case 4121006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPIRIT_CLAW, 0));
                    break;
                }
                case 2121004:
                case 2221004:
                case 2321004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INFINITY, ret.x));
                    break;
                }
                case 1121002:
                case 1221002:
                case 1321002:
                case 21121003:
                case 32121005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.STANCE, (int)ret.prop));
                    break;
                }
                case 1005:
                case 10001005:
                case 20001005:
                case 20011005:
                case 30001005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ECHO_OF_HERO, ret.x));
                    break;
                }
                case 1026:
                case 10001026:
                case 20001026:
                case 20011026:
                case 30001026: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOARING, 1));
                    break;
                }
                case 2121002:
                case 2221002:
                case 2321002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MANA_REFLECTION, 1));
                    break;
                }
                case 2321005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SHIELD, ret.x));
                    break;
                }
                case 3121007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HAMSTRING, ret.x));
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                }
                case 3221006:
                case 33111004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLIND, ret.x));
                    monsterStatus.put(MonsterStatus.ACC, ret.x);
                    break;
                }
                case 33121006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WATK, ret.y));
                    break;
                }
                case 32001003:
                case 32120000: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARK_AURA, ret.x));
                    break;
                }
                case 32101002:
                case 32110000: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLUE_AURA, ret.x));
                    break;
                }
                case 32101003:
                case 32120001: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.YELLOW_AURA, ret.x));
                    break;
                }
                case 33101004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.RAINING_MINES, ret.x));
                    break;
                }
                case 35101007: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PERFECT_ARMOR, ret.x));
                    break;
                }
                case 35121006: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SATELLITESAFE_PROC, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SATELLITESAFE_ABSORB, ret.y));
                    break;
                }
                case 35001001:
                case 35101009:
                case 35111007: {
                    ret.duration = 8000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, (int)level));
                    break;
                }
                case 35101002:
                case 35121013: {
                    ret.duration = 5000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, (int)level));
                    break;
                }
                case 35121005: {
                    ret.duration = 7200000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, (int)level));
                    break;
                }
            }
        }
        if (ret.isMonsterRiding()) {
            statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 1));
        }
        if (ret.isMorph() || ret.isPirateMorph()) {
            statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, ret.getMorph()));
        }
        ret.monsterStatus = monsterStatus;
        statups.trimToSize();
        ret.statups = statups;
        return ret;
    }
    
    public final void applyPassive(final MapleCharacter applyto, final MapleMapObject obj) {
        if (this.makeChanceResult()) {
            switch (this.sourceid) {
                case 2100000:
                case 2200000:
                case 2300000: {
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    final MapleMonster mob = (MapleMonster)obj;
                    if (mob.getStats().isBoss()) {
                        break;
                    }
                    final int absorbMp = Math.min((int)(mob.getMobMaxMp() * (this.getX() / 70.0)), mob.getMp());
                    if (absorbMp > 0) {
                        mob.setMp(mob.getMp() - absorbMp);
                        applyto.getStat().setMp((short)(applyto.getStat().getMp() + absorbMp));
                        applyto.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 1));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1), false);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public final boolean applyTo(final MapleCharacter chr) {
        return this.applyTo(chr, chr, true, null, this.duration);
    }
    
    public final boolean applyTo(final MapleCharacter chr, final Point pos) {
        return this.applyTo(chr, chr, true, pos, this.duration);
    }
    
    private final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos) {
        return this.applyTo(applyfrom, applyto, primary, pos, this.duration);
    }
    
    public final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos, final int newDuration) {
        if (this.isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
            return false;
        }
        if (this.sourceid == 4341006 && applyfrom.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) == null) {
            applyfrom.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (this.sourceid == 33101004 && applyfrom.getMap().isTown()) {
            applyfrom.dropMessage(5, "You may not use this skill in towns.");
            applyfrom.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        int hpchange = this.calcHPChange(applyfrom, primary);
        int mpchange = this.calcMPChange(applyfrom, primary);
        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (this.itemConNo != 0 && !applyto.isClone()) {
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(this.itemCon), this.itemCon, this.itemConNo, false, true);
            }
        }
        else if (!primary && this.isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0);
        }
        if (this.isDispel() && this.makeChanceResult()) {
            applyto.dispelDebuffs();
        }
        else if (this.isHeroWill()) {
            applyto.dispelDebuff(MapleDisease.SEDUCE);
        }
        else if (this.cureDebuffs.size() > 0) {
            for (final MapleDisease debuff : this.cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        }
        else if (this.isMPRecovery()) {
            final int toDecreaseHP = stat.getMaxHp() / 100 * 10;
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP;
                mpchange += toDecreaseHP / 100 * this.getY();
            }
            else {
                hpchange = ((stat.getHp() == 1) ? 0 : (stat.getHp() - 1));
            }
        }
        final List<Pair<MapleStat, Integer>> hpmpupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (hpchange != 0) {
            if (hpchange < 0 && -hpchange > stat.getHp() && !applyto.hasDisease(MapleDisease.ZOMBIFY)) {
                return false;
            }
            stat.setHp(stat.getHp() + hpchange);
        }
        if (mpchange != 0) {
            if (mpchange < 0 && -mpchange > stat.getMp()) {
                return false;
            }
            stat.setMp(stat.getMp() + mpchange);
            hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.MP, (int)stat.getMp()));
        }
        hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.HP, (int)stat.getHp()));
        applyto.getClient().getSession().write((Object)MaplePacketCreator.updatePlayerStats(hpmpupdate, true, applyto.getJob()));
        if (this.expinc != 0) {
            applyto.gainExp(this.expinc, true, true, false);
        }
        else if (GameConstants.isMonsterCard(this.sourceid)) {
            applyto.getMonsterBook().addCard(applyto.getClient(), this.sourceid);
        }
        else if (this.isSpiritClaw() && !applyto.isClone()) {
            final MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            for (int i = 0; i < use.getSlotLimit(); ++i) {
                final IItem item = use.getItem((byte)i);
                if (item != null && GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 200) {
                    MapleInventoryManipulator.removeById(applyto.getClient(), MapleInventoryType.USE, item.getItemId(), 200, false, true);
                    break;
                }
            }
        }
        else if (this.cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, this.cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            for (final MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
            }
        }
        else if (this.nuffSkill != 0 && applyto.getParty() != null) {
            final MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(this.nuffSkill);
            if (skil != null) {
                final MapleDisease dis = skil.getDisease();
                for (final MapleCharacter chr2 : applyto.getMap().getCharactersThreadsafe()) {
                    if ((chr2.getParty() == null || chr2.getParty().getId() != applyto.getParty().getId()) && (skil.targetsAll || Randomizer.nextBoolean())) {
                        if (dis == null) {
                            chr2.dispel();
                        }
                        else if (skil.getSkill() == null) {
                            chr2.giveDebuff(dis, 1, 30000L, MapleDisease.getByDisease(dis), 1);
                        }
                        else {
                            chr2.giveDebuff(dis, skil.getSkill());
                        }
                        if (!skil.targetsAll) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        if (this.overTime && !this.isEnergyCharge()) {
            this.applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }
        if (this.skill) {}
        if (primary) {
            if ((this.overTime || this.isHeal()) && !this.isEnergyCharge()) {
                this.applyBuff(applyfrom, newDuration);
            }
            if (this.isMonsterBuff()) {
                this.applyMonsterBuff(applyfrom);
            }
        }
        final SummonMovementType summonMovementType = this.getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(applyfrom, this, new Point((pos == null) ? applyfrom.getPosition() : pos), summonMovementType);
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.getSummons().put(this.sourceid, tosummon);
            tosummon.addHP((short)this.x);
            if (this.isBeholder()) {
                tosummon.addHP((short)1);
            }
            if (this.sourceid == 4341006) {
                applyfrom.cancelEffectFromBuffStat(MapleBuffStat.MIRROR_IMAGE);
            }
        }
        else if (this.isMagicDoor()) {
            final MapleDoor door = new MapleDoor(applyto, new Point(applyto.getPosition()), this.sourceid);
            if (door.getTownPortal() != null) {
                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);
                final MapleDoor townDoor = new MapleDoor(door);
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);
                if (applyto.getParty() != null) {
                    applyto.silentPartyUpdate();
                }
            }
            else {
                applyto.dropMessage(5, "你可能沒辦法使用傳送們因為村莊內禁止..");
            }
        }
        else if (this.isMist()) {
            final Rectangle bounds = this.calculateBoundingBox((pos != null) ? pos : new Point(applyfrom.getPosition()), applyfrom.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, this.getDuration(), false);
        }
        else if (this.isTimeLeap()) {
            for (final MapleCoolDownValueHolder j : applyto.getCooldowns()) {
                if (j.skillId != 5121010) {
                    applyto.removeCooldown(j.skillId);
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.skillCooldown(j.skillId, 0));
                }
            }
        }
        else {
            for (final WeakReference<MapleCharacter> chrz : applyto.getClones()) {
                if (chrz.get() != null) {
                    this.applyTo(chrz.get(), chrz.get(), primary, pos, newDuration);
                }
            }
        }
        return true;
    }
    
    public final boolean applyReturnScroll(final MapleCharacter applyto) {
        if (this.moveTo != -1) {
            MapleMap target;
            if (this.moveTo == 999999999) {
                target = applyto.getMap().getReturnMap();
            }
            else {
                target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(this.moveTo);
                if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61 && target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20 && target.getId() / 10000000 != 12 && target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                    return false;
                }
            }
            try {
                applyto.changeMap(target, target.getPortal(0));
            }
            catch (Exception ex) {
                applyto.dropMessage(5, "本地图目前尚未开放.");
                return false;
            }
            return true;
        }
        return false;
    }
    
    private final boolean isSoulStone() {
        return this.skill && this.sourceid == 22181003;
    }
    
    private final void applyBuff(final MapleCharacter applyfrom, final int newDuration) {
        if (this.isSoulStone()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (final MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if (chr.getParty() != null && chr.getParty().equals(applyfrom.getParty()) && chr.isAlive()) {
                        ++membrs;
                    }
                }
                final List<MapleCharacter> awarded = new ArrayList<MapleCharacter>();
                while (awarded.size() < Math.min(membrs, this.y)) {
                    for (final MapleCharacter chr2 : applyfrom.getMap().getCharactersThreadsafe()) {
                        if (chr2.isAlive() && chr2.getParty().equals(applyfrom.getParty()) && !awarded.contains(chr2) && Randomizer.nextInt(this.y) == 0) {
                            awarded.add(chr2);
                        }
                    }
                }
                for (final MapleCharacter chr2 : awarded) {
                    this.applyTo(applyfrom, chr2, false, null, newDuration);
                    chr2.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2));
                    chr2.getMap().broadcastMessage(chr2, MaplePacketCreator.showBuffeffect(chr2.getId(), this.sourceid, 2), false);
                }
            }
        }
        else if (this.isPartyBuff() && (applyfrom.getParty() != null || this.isGmBuff())) {
            final Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
            for (final MapleMapObject affectedmo : affecteds) {
                final MapleCharacter affected = (MapleCharacter)affectedmo;
                if (affected != applyfrom && (this.isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    if ((this.isResurrection() && !affected.isAlive()) || (!this.isResurrection() && affected.isAlive())) {
                        this.applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2));
                        affected.getMap().broadcastMessage(affected, MaplePacketCreator.showBuffeffect(affected.getId(), this.sourceid, 2), false);
                    }
                    if (!this.isTimeLeap()) {
                        continue;
                    }
                    for (final MapleCoolDownValueHolder i : affected.getCooldowns()) {
                        if (i.skillId != 5121010) {
                            affected.removeCooldown(i.skillId);
                            affected.getClient().getSession().write((Object)MaplePacketCreator.skillCooldown(i.skillId, 0));
                        }
                    }
                }
            }
        }
    }
    
    private final void removeMonsterBuff(final MapleCharacter applyfrom) {
        final List<MonsterStatus> cancel = new ArrayList<MonsterStatus>();
        switch (this.sourceid) {
            case 1111007: {
                cancel.add(MonsterStatus.WDEF);
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                break;
            }
            case 1211009: {
                cancel.add(MonsterStatus.MDEF);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                break;
            }
            case 1311007: {
                cancel.add(MonsterStatus.WATK);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MATK);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            }
            default: {
                return;
            }
        }
        final Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;
        for (final MapleMapObject mo : affected) {
            if (this.makeChanceResult()) {
                for (final MonsterStatus stat : cancel) {
                    ((MapleMonster)mo).cancelStatus(stat);
                }
            }
            if (++i >= this.mobCount) {
                break;
            }
        }
    }
    
    private final void applyMonsterBuff(final MapleCharacter applyfrom) {
        final Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;
        for (final MapleMapObject mo : affected) {
            if (this.makeChanceResult()) {
                for (final Map.Entry<MonsterStatus, Integer> stat : this.getMonsterStati().entrySet()) {
                    ((MapleMonster)mo).applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), this.sourceid, null, false), this.isPoison(), this.getDuration(), false);
                }
            }
            if (++i >= this.mobCount) {
                break;
            }
        }
    }
    
    private final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
        if (this.lt == null || this.rb == null) {
            return new Rectangle(posFrom.x, posFrom.y, facingLeft ? 1 : -1, 1);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(this.lt.x + posFrom.x, this.lt.y + posFrom.y);
            myrb = new Point(this.rb.x + posFrom.x, this.rb.y + posFrom.y);
        }
        else {
            myrb = new Point(this.lt.x * -1 + posFrom.x, this.rb.y + posFrom.y);
            mylt = new Point(this.rb.x * -1 + posFrom.x, this.lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }
    
    public final void setDuration(final int d) {
        this.duration = d;
    }
    
    public final void silentApplyBuff(final MapleCharacter chr, final long starttime) {
        final int localDuration = this.alchemistModifyVal(chr, this.duration, false);
        chr.registerEffect(this, starttime, Timer.BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime), starttime + localDuration - System.currentTimeMillis()));
        final SummonMovementType summonMovementType = this.getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(chr, this, chr.getPosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getMap().spawnSummon(tosummon);
                chr.getSummons().put(this.sourceid, tosummon);
                tosummon.addHP((short)this.x);
                if (this.isBeholder()) {
                    tosummon.addHP((short)1);
                }
            }
        }
    }
    
    public final void applyComboBuffA(final MapleCharacter applyto, final short combo) {
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
        statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHARP_EYES, this.x << 8 | this.y));
        applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, 29999, statups, this));
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + 29999L - System.currentTimeMillis());
        applyto.registerEffect(this, starttime, schedule);
    }
    
    public final void applyComboBuff(final MapleCharacter applyto, final short combo) {
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
        statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.矛连击强化, combo / 5));
        statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WDEF, combo / 2));
        statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MDEF, combo / 2));
        applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, 29999, statups, this));
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + 29999L - System.currentTimeMillis());
        applyto.registerEffect(this, starttime, schedule);
    }
    
    public final void applyEnergyBuff(final MapleCharacter applyto, final boolean infinity) {
        final List<Pair<MapleBuffStat, Integer>> stat = this.statups;
        final long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.setBuffedValue(MapleBuffStat.ENERGY_CHARGE, 0);
            applyto.getClient().getSession().write((Object)MaplePacketCreator.能量条(stat, this.duration / 1000));
            applyto.registerEffect(this, starttime, null);
        }
        else {
            applyto.cancelEffect(this, true, -1L);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveEnergyChargeTest(applyto.getId(), 10000, this.duration / 1000), false);
            final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + this.duration - System.currentTimeMillis());
            this.statups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, 10000));
            applyto.registerEffect(this, starttime, schedule);
            this.statups = stat;
        }
    }
    
    private final void applyBuffEffect(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final int newDuration) {
        int localDuration = newDuration;
        if (primary) {
            localDuration = this.alchemistModifyVal(applyfrom, localDuration, false);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1), false);
        }
        List<Pair<MapleBuffStat, Integer>> localstatups = this.statups;
        boolean normal = true;
        switch (this.sourceid) {
            case 5001005:
            case 5121009:
            case 15001003:
            case 15111005: {
                applyto.getClient().getSession().write((Object)MaplePacketCreator.givePirate(this.statups, localDuration / 1000, this.sourceid));
                normal = false;
                break;
            }
            case 5211006:
            case 5220011:
            case 22151002: {
                if (applyto.getLinkMid() > 0) {
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.cancelHoming());
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.giveHoming(this.sourceid, applyto.getLinkMid()));
                    normal = false;
                    break;
                }
                return;
            }
            case 4001003:
            case 4330001:
            case 13101006:
            case 14001003: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 32001003:
            case 32120000: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARK_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                break;
            }
            case 32101002:
            case 32110000: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLUE_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            case 32101003:
            case 32120001: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.YELLOW_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            case 35001001:
            case 35101002:
            case 35101009:
            case 35111007:
            case 35121005:
            case 35121013: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 1111002:
            case 11111001: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 3101004:
            case 3201004:
            case 13101003: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 4111002:
            case 14111000: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 15111006: {
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPARK, this.x));
                applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 1121010: {
                applyto.handleOrbconsume();
                break;
            }
            default: {
                if (this.isMorph() || this.isPirateMorph()) {
                    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, this.getMorph(applyto)));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                    break;
                }
                if (this.isMonsterRiding()) {
                    localDuration = 2100000000;
                    int mountid = parseMountInfo(applyto, this.sourceid);
                    int mountid2 = parseMountInfo_Pure(applyto, this.sourceid);
                    if (this.sourceid == 1013 && applyto.getMountId() != 0) {
                        mountid = applyto.getMountId();
                        mountid2 = applyto.getMountId();
                    }
                    if (mountid != 0 && mountid2 != 0) {
                        final List<Pair<MapleBuffStat, Integer>> stat2 = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 0));
                        applyto.cancelEffectFromBuffStat(MapleBuffStat.POWERGUARD);
                        applyto.cancelEffectFromBuffStat(MapleBuffStat.MANA_REFLECTION);
                        applyto.getClient().getSession().write((Object)MaplePacketCreator.giveMount(applyto, mountid2, this.sourceid, stat2));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat2, mountid, this.sourceid), false);
                        normal = false;
                        break;
                    }
                    return;
                }
                else {
                    if (this.isSoaring()) {
                        localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOARING, 1));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), localstatups, this), false);
                        applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                        break;
                    }
                    if (this.isBerserkFury() || this.berserk2 > 0) {
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BERSERK_FURY, 1));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                        break;
                    }
                    if (this.isDivineBody()) {
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DIVINE_BODY, 1));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                        break;
                    }
                    break;
                }
                
            }
        }
        if (!this.isMonsterRiding_()) {
            applyto.cancelEffect(this, true, -1L, localstatups);
        }
        if (normal && this.statups.size() > 0) {
            applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.skill ? this.sourceid : (-this.sourceid), localDuration, this.statups, this));
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + localDuration - System.currentTimeMillis());
        applyto.registerEffect(this, starttime, schedule, localstatups);
    }
    
    public static final int parseMountInfo(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 1004:
            case 10001004:
            case 20001004:
            case 20011004:
            case 30001004: {
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-118)) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-119)) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-118)).getItemId();
                }
                return parseMountInfo_Pure(player, skillid);
            }
            default: {
                return GameConstants.getMountItem(skillid);
            }
        }
    }
    
    public static final int parseMountInfo_Pure(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 1004:
            case 11004:
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
            case 80001000: {
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18)) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-19)) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-18)).getItemId();
                }
                return 0;
            }
            default: {
                return GameConstants.getMountItem(skillid);
            }
        }
    }
    
    private final int calcHPChange(final MapleCharacter applyfrom, final boolean primary) {
        int hpchange = 0;
        if (this.hp != 0) {
            if (!this.skill) {
                if (primary) {
                    hpchange += this.alchemistModifyVal(applyfrom, this.hp, true);
                }
                else {
                    hpchange += this.hp;
                }
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange /= 2;
                }
            }
            else {
                hpchange += makeHealHP(this.hp / 100.0, applyfrom.getStat().getTotalMagic(), 3.0, 5.0);
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (this.hpR != 0.0) {
            hpchange += (int)(applyfrom.getStat().getCurrentMaxHp() * this.hpR) / (applyfrom.hasDisease(MapleDisease.ZOMBIFY) ? 2 : 1);
        }
        if (primary && this.hpCon != 0) {
            hpchange -= this.hpCon;
        }
        switch (this.sourceid) {
            case 4211001: {
                final PlayerStats stat = applyfrom.getStat();
                final int v42 = this.getY() + 100;
                final int v43 = Randomizer.rand(1, 100) + 100;
                hpchange = (int)((v43 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(this.getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
            }
        }
        return hpchange;
    }
    
    private static final int makeHealHP(final double rate, final double stat, final double lowerfactor, final double upperfactor) {
        return (int)(Math.random() * ((int)(stat * upperfactor * rate) - (int)(stat * lowerfactor * rate) + 1) + (int)(stat * lowerfactor * rate));
    }
    
    private static final int getElementalAmp(final int job) {
        switch (job) {
            case 211:
            case 212: {
                return 2110001;
            }
            case 221:
            case 222: {
                return 2210001;
            }
            case 1211:
            case 1212: {
                return 12110001;
            }
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                return 22150000;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int calcMPChange(final MapleCharacter applyfrom, final boolean primary) {
        int mpchange = 0;
        if (this.mp != 0) {
            if (primary) {
                mpchange += this.alchemistModifyVal(applyfrom, this.mp, true);
            }
            else {
                mpchange += this.mp;
            }
        }
        if (this.mpR != 0.0) {
            mpchange += (int)(applyfrom.getStat().getCurrentMaxMp() * this.mpR);
        }
        if (primary && this.mpCon != 0) {
            double mod = 1.0;
            final int ElemSkillId = getElementalAmp(applyfrom.getJob());
            if (ElemSkillId != -1) {
                final ISkill amp = SkillFactory.getSkill(ElemSkillId);
                final int ampLevel = applyfrom.getSkillLevel(amp);
                if (ampLevel > 0) {
                    final MapleStatEffect ampStat = amp.getEffect(ampLevel);
                    mod = ampStat.getX() / 100.0;
                }
            }
            final Integer Concentrate = applyfrom.getBuffedSkill_X(MapleBuffStat.CONCENTRATE);
            final int percent_off = applyfrom.getStat().mpconReduce + ((Concentrate == null) ? 0 : Concentrate);
            if (applyfrom.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                mpchange = 0;
            }
            else {
                mpchange -= (int)((this.mpCon - this.mpCon * percent_off / 100) * mod);
            }
        }
        return mpchange;
    }
    
    private final int alchemistModifyVal(final MapleCharacter chr, final int val, final boolean withX) {
        if (!this.skill) {
            int offset = chr.getStat().RecoveryUP;
            final MapleStatEffect alchemistEffect = this.getAlchemistEffect(chr);
            if (alchemistEffect != null) {
                offset += (withX ? alchemistEffect.getX() : alchemistEffect.getY());
            }
            else {
                offset += 100;
            }
            return val * offset / 100;
        }
        return val;
    }
    
    private final MapleStatEffect getAlchemistEffect(final MapleCharacter chr) {
        switch (chr.getJob()) {
            case 411:
            case 412: {
                final ISkill al = SkillFactory.getSkill(4110000);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            }
            case 1411:
            case 1412: {
                final ISkill al = SkillFactory.getSkill(14110003);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            }
            default: {
                if (!GameConstants.isResist(chr.getJob())) {
                    return null;
                }
                final ISkill al = SkillFactory.getSkill(30000002);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            }
        }
    }
    
    public final void setSourceId(final int newid) {
        this.sourceid = newid;
    }
    
    private final boolean isGmBuff() {
        switch (this.sourceid) {
            case 1005:
            case 9001000:
            case 9001001:
            case 9001002:
            case 9001003:
            case 9001005:
            case 9001008:
            case 10001005:
            case 20001005:
            case 20011005:
            case 30001005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private final boolean isEnergyCharge() {
        return this.skill && (this.sourceid == 5110001 || this.sourceid == 15100004);
    }
    
    private final boolean isMonsterBuff() {
        switch (this.sourceid) {
            case 1201006:
            case 2101003:
            case 2111004:
            case 2201003:
            case 2211004:
            case 2311005:
            case 4111003:
            case 4121004:
            case 4221004:
            case 4321002:
            case 12101001:
            case 12111002:
            case 14111001:
            case 22121000:
            case 22141003:
            case 22151001:
            case 22161002: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final void setPartyBuff(final boolean pb) {
        this.partyBuff = pb;
    }
    
    private final boolean isPartyBuff() {
        if (this.lt == null || this.rb == null || !this.partyBuff) {
            return this.isSoulStone();
        }
        switch (this.sourceid) {
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1221003:
            case 1221004:
            case 4311001:
            case 11111007:
            case 12101005: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public final boolean isHeal() {
        return this.sourceid == 2301002 || this.sourceid == 9101000;
    }
    
    public final boolean isResurrection() {
        return this.sourceid == 9001005 || this.sourceid == 2321006;
    }
    
    public final boolean isTimeLeap() {
        return this.sourceid == 5121010;
    }
    
    public final short getHp() {
        return this.hp;
    }
    
    public final short getMp() {
        return this.mp;
    }
    
    public final byte getMastery() {
        return this.mastery;
    }
    
    public final short getWatk() {
        return this.watk;
    }
    
    public final short getMatk() {
        return this.matk;
    }
    
    public final short getWdef() {
        return this.wdef;
    }
    
    public final short getMdef() {
        return this.mdef;
    }
    
    public final short getAcc() {
        return this.acc;
    }
    
    public final short getAvoid() {
        return this.avoid;
    }
    
    public final short getHands() {
        return this.hands;
    }
    
    public final short getSpeed() {
        return this.speed;
    }
    
    public final short getJump() {
        return this.jump;
    }
    
    public final int getDuration() {
        return this.duration;
    }
    
    public final boolean isOverTime() {
        return this.overTime;
    }
    
    public final List<Pair<MapleBuffStat, Integer>> getStatups() {
        return this.statups;
    }
    
    public final boolean sameSource(final MapleStatEffect effect) {
        return effect != null && this.sourceid == effect.sourceid && this.skill == effect.skill;
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    public final int getZ() {
        return this.z;
    }
    
    public final short getDamage() {
        return this.damage;
    }
    
    public final byte getAttackCount() {
        return this.attackCount;
    }
    
    public final byte getBulletCount() {
        return this.bulletCount;
    }
    
    public final int getBulletConsume() {
        return this.bulletConsume;
    }
    
    public final byte getMobCount() {
        return this.mobCount;
    }
    
    public final int getMoneyCon() {
        return this.moneyCon;
    }
    
    public final int getCooldown() {
        return this.cooldown;
    }
    
    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return this.monsterStatus;
    }
    
    public final int getBerserk() {
        return this.berserk;
    }
    
    public final boolean isHide() {
        return this.skill && this.sourceid == 9001004;
    }
    
    public final boolean isDragonBlood() {
        return this.skill && this.sourceid == 1311008;
    }
    
    public final boolean isBerserk() {
        return this.skill && this.sourceid == 1320006;
    }
    
    public final boolean isBeholder() {
        return this.skill && this.sourceid == 1321007;
    }
    
    public final boolean isMPRecovery() {
        return this.skill && this.sourceid == 5101005;
    }
    
    public final boolean isMonsterRiding_() {
        return this.skill && (this.sourceid == 1004 || this.sourceid == 10001004 || this.sourceid == 20001004 || this.sourceid == 20011004 || this.sourceid == 30001004);
    }
    
    public final boolean isMonsterRiding() {
        return this.skill && (this.isMonsterRiding_() || GameConstants.getMountItem(this.sourceid) != 0);
    }
    
    public final boolean isMagicDoor() {
        return this.skill && (this.sourceid == 2311002 || this.sourceid == 8001 || this.sourceid == 10008001 || this.sourceid == 20008001 || this.sourceid == 20018001 || this.sourceid == 30008001);
    }
    
    public final boolean isMesoGuard() {
        return this.skill && this.sourceid == 4211005;
    }
    
    public final boolean isCharge() {
        switch (this.sourceid) {
            case 1211003:
            case 1211008:
            case 11111007:
            case 12101005:
            case 15101006:
            case 21111005: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isPoison() {
        switch (this.sourceid) {
            case 2101005:
            case 2111003:
            case 2111006:
            case 2121003:
            case 2221003:
            case 3111003:
            case 12111005:
            case 22161002: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    private final boolean isMist() {
        return this.skill && (this.sourceid == 2111003 || this.sourceid == 4221006 || this.sourceid == 12111005 || this.sourceid == 14111006 || this.sourceid == 22161003);
    }
    
    private final boolean isSpiritClaw() {
        return this.skill && this.sourceid == 4121006;
    }
    
    private final boolean isDispel() {
        return this.skill && (this.sourceid == 2311001 || this.sourceid == 9001000);
    }
    
    private final boolean isHeroWill() {
        switch (this.sourceid) {
            case 1121011:
            case 1221012:
            case 1321010:
            case 2121008:
            case 2221008:
            case 2321009:
            case 3121009:
            case 3221008:
            case 4121009:
            case 4221008:
            case 4341008:
            case 5121008:
            case 5221010:
            case 21121008:
            case 22171004:
            case 32121008:
            case 33121008:
            case 35121008: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isAranCombo() {
        return this.sourceid == 21000000;
    }
    
    public final boolean isCombo() {
        switch (this.sourceid) {
            case 1111002:
            case 11111001: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isPirateMorph() {
        switch (this.sourceid) {
            case 5111005:
            case 5121003:
            case 15111002: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isMorph() {
        return this.morphId > 0;
    }
    
    public final int getMorph() {
        switch (this.sourceid) {
            case 5111005:
            case 15111002: {
                return 1000;
            }
            case 5121003: {
                return 1001;
            }
            case 5101007: {
                return 1002;
            }
            case 13111005: {
                return 1003;
            }
            default: {
                return this.morphId;
            }
        }
    }
    
    public final boolean isDivineBody() {
        switch (this.sourceid) {
            case 1010:
            case 10001010:
            case 20001010:
            case 20011010:
            case 30001010: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isBerserkFury() {
        switch (this.sourceid) {
            case 1011:
            case 10001011:
            case 20001011:
            case 20011011:
            case 30001011: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final int getMorph(final MapleCharacter chr) {
        final int morph = this.getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003: {
                return morph + ((chr.getGender() == 1) ? 100 : 0);
            }
            default: {
                return morph;
            }
        }
    }
    
    public final byte getLevel() {
        return this.level;
    }
    
    public final SummonMovementType getSummonMovementType() {
        if (!this.skill) {
            return null;
        }
        switch (this.sourceid) {
            case 3111002:
            case 3211002:
            case 5211001:
            case 5220002:
            case 13111004: {
                return SummonMovementType.STATIONARY;
            }
            case 2311006:
            case 3111005:
            case 3121006:
            case 3211005:
            case 3221005: {
                return SummonMovementType.CIRCLE_FOLLOW;
            }
            case 5211002: {
                return SummonMovementType.CIRCLE_STATIONARY;
            }
            case 32111006: {
                return SummonMovementType.WALK_STATIONARY;
            }
            case 1321007:
            case 2121005:
            case 2221005:
            case 2321003:
            case 11001004:
            case 12001004:
            case 12111004:
            case 13001004:
            case 14001005:
            case 15001004: {
                return SummonMovementType.FOLLOW;
            }
            default: {
                return null;
            }
        }
    }
    
    public final boolean isSkill() {
        return this.skill;
    }
    
    public final int getSourceId() {
        return this.sourceid;
    }
    
    public final boolean isSoaring() {
        switch (this.sourceid) {
            case 1026:
            case 10001026:
            case 20001026:
            case 20011026:
            case 30001026: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isFinalAttack() {
        switch (this.sourceid) {
            case 11101002:
            case 13101002: {
                return this.skill;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean makeChanceResult() {
        return this.prop == 100 || Randomizer.nextInt(99) < this.prop;
    }
    
    public final short getProb() {
        return this.prop;
    }
    
    private boolean isBattleShip() {
        return this.skill && this.sourceid == 5221006;
    }
    
    public static class CancelEffectAction implements Runnable
    {
        private final MapleStatEffect effect;
        private final WeakReference<MapleCharacter> target;
        private final long startTime;
        
        public CancelEffectAction(final MapleCharacter target, final MapleStatEffect effect, final long startTime) {
            this.effect = effect;
            this.target = new WeakReference<MapleCharacter>(target);
            this.startTime = startTime;
        }
        
        @Override
        public void run() {
            final MapleCharacter realTarget = this.target.get();
            if (realTarget != null && !realTarget.isClone()) {
                realTarget.cancelEffect(this.effect, false, this.startTime);
            }
        }
    }
}
