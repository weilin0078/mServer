package constants;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;
import handling.channel.handler.AttackInfo;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.MapleMapObjectType;

public class GameConstants
{
    public static final List<MapleMapObjectType> rangedMapobjectTypes;
    private static final int[] exp;
    private static final int[] closeness;
    private static final int[] mountexp;
    public static final int[] itemBlock;
    public static final int[] cashBlock;
    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int[] blockedSkills;
    public static final String MASTER = "%&HYGEomgLOL";
    public static final String[] RESERVED;
    private static final int[] mobHpVal;
    public static int 单机IP;
    public static String 绑定IP;
    public static final int game = 1;
    public static final int[] goldrewards;
    public static final int[] silverrewards;
    public static int[] eventCommonReward;
    public static int[] eventUncommonReward;
    public static int[] eventRareReward;
    public static int[] eventSuperReward;
    public static int[] fishingReward;
    public static int[] Equipments_Bonus;
    public static int[] blockedMaps;
    public static final int[] normalDrops;
    public static final int[] rareDrops;
    public static final int[] superDrops;
    public static int[] owlItems;
    
    public static int getExpNeededForLevel(final int level) {
        if (level < 0 || level >= GameConstants.exp.length) {
            return Integer.MAX_VALUE;
        }
        return GameConstants.exp[level];
    }
    
    public static int getClosenessNeededForLevel(final int level) {
        return GameConstants.closeness[level - 1];
    }
    
    public static int getMountExpNeededForLevel(final int level) {
        return GameConstants.mountexp[level - 1];
    }
    
    public static int getBookLevel(final int level) {
        return 5 * level * (level + 1);
    }
    
    public static int getTimelessRequiredEXP(final int level) {
        return 70 + level * 10;
    }
    
    public static int getReverseRequiredEXP(final int level) {
        return 60 + level * 5;
    }
    
    public static int maxViewRangeSq() {
        return 100000000;
    }
    
    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }
    
    public static boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }
    
    public static boolean isEvan(final int job) {
        return job == 2001 || (job >= 2200 && job <= 2218);
    }
    
    public static boolean isAran(final int job) {
        return job >= 2000 && job <= 2112 && job != 2001;
    }
    
    public static boolean isResist(final int job) {
        return job >= 3000 && job <= 3512;
    }
    
    public static boolean isAdventurer(final int job) {
        return job >= 0 && job < 1000;
    }
    
    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1110000:
            case 1210000:
            case 2000000:
            case 4100002:
            case 4200001:
            case 11110000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }
    
    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21110007:
            case 21110008: {
                return 21110002;
            }
            case 21120009:
            case 21120010: {
                return 21120002;
            }
            case 4321001: {
                return 4321000;
            }
            case 33101006:
            case 33101007: {
                return 33101005;
            }
            case 33101008: {
                return 33101004;
            }
            case 35101009:
            case 35101010: {
                return 35100008;
            }
            case 35111009:
            case 35111010: {
                return 35111001;
            }
            default: {
                return id;
            }
        }
    }
    
    public static int getBOF_ForJob(final int job) {
        if (isAdventurer(job)) {
            return 12;
        }
        if (isKOC(job)) {
            return 10000012;
        }
        if (isResist(job)) {
            return 30000012;
        }
        if (isEvan(job)) {
            return 20010012;
        }
        return 20000012;
    }
    
    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212: {
                return 2100000;
            }
            case 220:
            case 221:
            case 222: {
                return 2200000;
            }
            case 230:
            case 231:
            case 232: {
                return 2300000;
            }
            default: {
                return 2100000;
            }
        }
    }
    
    public static int getJobShortValue(int job) {
        if (job >= 1000) {
            job -= job / 1000 * 1000;
        }
        job /= 100;
        if (job == 4) {
            job *= 2;
        }
        else if (job == 3) {
            ++job;
        }
        else if (job == 5) {
            job += 11;
        }
        return job;
    }
    
    public static boolean isPyramidSkill(final int skill) {
        switch (skill) {
            case 1020:
            case 10001020:
            case 20001020:
            case 20011020:
            case 30001020: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isMulungSkill(final int skill) {
        switch (skill) {
            case 1009:
            case 1010:
            case 1011:
            case 10001009:
            case 10001010:
            case 10001011:
            case 20001009:
            case 20001010:
            case 20001011:
            case 20011009:
            case 20011010:
            case 20011011:
            case 30001009:
            case 30001010:
            case 30001011: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10000 == 207;
    }
    
    public static boolean isBullet(final int itemId) {
        return itemId / 10000 == 233;
    }
    
    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }
    
    public static boolean isOverall(final int itemId) {
        return itemId / 10000 == 105;
    }
    
    public static boolean isPet(final int itemId) {
        return itemId / 10000 == 500;
    }
    
    public static boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }
    
    public static boolean isArrowForBow(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }
    
    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138;
    }
    
    public static boolean isWeapon(final int itemId) {
        return itemId >= 1300000 && itemId < 1500000;
    }
    
    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte)(itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }
    
    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat %= 100;
        switch (cat) {
            case 30: {
                return MapleWeaponType.SWORD1H;
            }
            case 31: {
                return MapleWeaponType.AXE1H;
            }
            case 32: {
                return MapleWeaponType.BLUNT1H;
            }
            case 33: {
                return MapleWeaponType.DAGGER;
            }
            case 34: {
                return MapleWeaponType.KATARA;
            }
            case 37: {
                return MapleWeaponType.WAND;
            }
            case 38: {
                return MapleWeaponType.STAFF;
            }
            case 40: {
                return MapleWeaponType.SWORD2H;
            }
            case 41: {
                return MapleWeaponType.AXE2H;
            }
            case 42: {
                return MapleWeaponType.BLUNT2H;
            }
            case 43: {
                return MapleWeaponType.SPEAR;
            }
            case 44: {
                return MapleWeaponType.POLE_ARM;
            }
            case 45: {
                return MapleWeaponType.BOW;
            }
            case 46: {
                return MapleWeaponType.CROSSBOW;
            }
            case 47: {
                return MapleWeaponType.CLAW;
            }
            case 48: {
                return MapleWeaponType.KNUCKLE;
            }
            case 49: {
                return MapleWeaponType.GUN;
            }
            default: {
                return MapleWeaponType.NOT_A_WEAPON;
            }
        }
    }
    
    public static boolean isShield(final int itemId) {
        int cat = itemId / 10000;
        cat %= 100;
        return cat == 9;
    }
    
    public static boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }
    
    public static boolean isCleanSlate(final int itemId) {
        return itemId / 100 == 20490;
    }
    
    public static boolean isAccessoryScroll(final int itemId) {
        return itemId / 100 == 20492;
    }
    
    public static boolean isChaosScroll(final int itemId) {
        return (itemId < 2049105 || itemId > 2049110) && itemId / 100 == 20491;
    }
    
    public static int getChaosNumber(final int itemId) {
        return (itemId == 2049116) ? 10 : 5;
    }
    
    public static boolean isEquipScroll(final int scrollId) {
        return scrollId / 100 == 20493;
    }
    
    public static boolean isPotentialScroll(final int scrollId) {
        return scrollId / 100 == 20494;
    }
    
    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727:
            case 2041058: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isTwoHanded(final int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
            case GUN:
            case KNUCKLE:
            case BLUNT2H:
            case BOW:
            case CLAW:
            case CROSSBOW:
            case POLE_ARM:
            case SPEAR:
            case SWORD2H: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isTownScroll(final int id) {
        return id >= 2030000 && id < 2040000;
    }
    
    public static boolean isUpgradeScroll(final int id) {
        return id >= 2040000 && id < 2050000;
    }
    
    public static boolean isGun(final int id) {
        return id >= 1492000 && id < 1500000;
    }
    
    public static boolean isUse(final int id) {
        return id >= 2000000 && id <= 2490000;
    }
    
    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }
    
    public static boolean isMonsterCard(final int id) {
        return id / 10000 == 238;
    }
    
    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }
    
    public static int getCardShortId(final int id) {
        return id % 10000;
    }
    
    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }
    
    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 1032062:
            case 1142156:
            case 1142157:
            case 2040727:
            case 2041058:
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 4032312:
            case 4032334: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isCustomQuest(final int id) {
        return id > 99999;
    }
    
    public static int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int)Math.round(0.06 * meso);
        }
        if (meso >= 25000000) {
            return (int)Math.round(0.05 * meso);
        }
        if (meso >= 10000000) {
            return (int)Math.round(0.04 * meso);
        }
        if (meso >= 5000000) {
            return (int)Math.round(0.03 * meso);
        }
        if (meso >= 1000000) {
            return (int)Math.round(0.018 * meso);
        }
        if (meso >= 100000) {
            return (int)Math.round(0.008 * meso);
        }
        return 0;
    }
    
    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int)Math.round(0.03 * meso);
        }
        if (meso >= 25000000) {
            return (int)Math.round(0.025 * meso);
        }
        if (meso >= 10000000) {
            return (int)Math.round(0.02 * meso);
        }
        if (meso >= 5000000) {
            return (int)Math.round(0.015 * meso);
        }
        if (meso >= 1000000) {
            return (int)Math.round(0.009 * meso);
        }
        if (meso >= 100000) {
            return (int)Math.round(0.004 * meso);
        }
        return 0;
    }
    
    public static short getSummonAttackDelay(final int id) {
        switch (id) {
            case 2121005:
            case 2221005:
            case 2311006:
            case 2321003:
            case 3111005:
            case 3121006:
            case 3211005:
            case 3221005:
            case 11001004:
            case 12001004:
            case 13001004:
            case 14001005:
            case 15001004: {
                return 3030;
            }
            case 5211001:
            case 5211002:
            case 5220002: {
                return 1530;
            }
            case 1321007:
            case 3111002:
            case 3211002: {
                return 0;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static short getAttackDelay(final int id) {
        switch (id) {
            case 4321001: {
                return 40;
            }
            case 3121004:
            case 4221001:
            case 5201006:
            case 5221004:
            case 13111002:
            case 33121009: {
                return 120;
            }
            case 13101005: {
                return 360;
            }
            case 2301002:
            case 5001003: {
                return 390;
            }
            case 1121006:
            case 1221007:
            case 1321003:
            case 5001001:
            case 15001001: {
                return 450;
            }
            case 4201005:
            case 5211004:
            case 5211005: {
                return 480;
            }
            case 0:
            case 1001004:
            case 1001005:
            case 1311005:
            case 5111002:
            case 11001002:
            case 11001003:
            case 15101005: {
                return 570;
            }
            case 311004:
            case 1121008:
            case 1211002:
            case 1221009:
            case 1311003:
            case 1311004:
            case 2101005:
            case 2121003:
            case 2121006:
            case 2221003:
            case 3101005:
            case 3111003:
            case 3111006:
            case 3201005:
            case 3211003:
            case 3211004:
            case 3211006:
            case 3221001:
            case 4001334:
            case 4001344:
            case 4101005:
            case 4111004:
            case 4111005:
            case 4121007:
            case 4201004:
            case 4211004:
            case 5101004:
            case 5201001:
            case 5221007:
            case 11111004:
            case 12101002:
            case 13111000:
            case 14001004:
            case 14111002:
            case 14111005:
            case 15101003: {
                return 600;
            }
            case 1311001:
            case 1311002:
            case 2221006:
            case 4221007:
            case 5001002:
            case 5201004:
            case 5211000:
            case 15001002: {
                return 660;
            }
            case 2001004:
            case 2001005:
            case 2121001:
            case 2201004:
            case 2201005:
            case 2211002:
            case 2221001:
            case 2301005:
            case 2321001:
            case 2321007:
            case 4121008:
            case 4211006:
            case 5101002:
            case 5121005:
            case 5211006:
            case 5221008:
            case 11101004:
            case 12001003:
            case 12111006: {
                return 750;
            }
            case 2111006:
            case 2211006:
            case 15111007: {
                return 810;
            }
            case 2111002:
            case 4211002:
            case 5101003:
            case 13111006: {
                return 900;
            }
            case 2311004:
            case 5121003: {
                return 930;
            }
            case 13111007: {
                return 960;
            }
            case 4121003:
            case 4221003:
            case 14101006: {
                return 1020;
            }
            case 12101006: {
                return 1050;
            }
            case 5121001: {
                return 1060;
            }
            case 1311006:
            case 2211003: {
                return 1140;
            }
            case 11111006: {
                return 1230;
            }
            case 12111005: {
                return 1260;
            }
            case 2111003: {
                return 1320;
            }
            case 5111006:
            case 15111003: {
                return 1500;
            }
            case 5121007:
            case 15111004: {
                return 1830;
            }
            case 5121004:
            case 5221003: {
                return 2160;
            }
            case 2321008: {
                return 2700;
            }
            case 2121007:
            case 2221007:
            case 10001011: {
                return 3060;
            }
            default: {
                return 330;
            }
        }
    }
    
    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 1092049:
            case 1372039:
            case 1372040:
            case 1372041:
            case 1372042:
            case 1382037:
            case 2040006:
            case 2040007:
            case 2040303:
            case 2040403:
            case 2040506:
            case 2040507:
            case 2040603:
            case 2040709:
            case 2040710:
            case 2040711:
            case 2040806:
            case 2040903:
            case 2041024:
            case 2041025:
            case 2043003:
            case 2043103:
            case 2043203:
            case 2043303:
            case 2043703:
            case 2043803:
            case 2044003:
            case 2044019:
            case 2044103:
            case 2044203:
            case 2044303:
            case 2044403:
            case 2044503:
            case 2044603:
            case 2044703:
            case 2044815:
            case 2044908:
            case 2049000:
            case 2049001:
            case 2049002:
            case 2049100:
            case 2340000: {
                return 2;
            }
            case 1082149:
            case 1082179:
            case 1102041:
            case 1102042:
            case 1102084:
            case 1102086:
            case 1402044:
            case 3010020:
            case 3010041:
            case 3010054:
            case 3010063:
            case 3010064:
            case 3010065:
            case 3010068:
            case 3012001:
            case 3012002: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static boolean isDragonItem(final int itemId) {
        switch (itemId) {
            case 1302059:
            case 1312031:
            case 1322052:
            case 1332049:
            case 1332050:
            case 1342010:
            case 1372032:
            case 1382036:
            case 1402036:
            case 1412026:
            case 1422028:
            case 1432038:
            case 1442045:
            case 1452044:
            case 1462039:
            case 1472051:
            case 1472052: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isReverseItem(final int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1342012:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isTimelessItem(final int itemId) {
        switch (itemId) {
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1032031:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1092057:
            case 1092058:
            case 1092059:
            case 1102172:
            case 1122011:
            case 1122012:
            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1342011:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isRing(final int itemId) {
        return itemId >= 1112000 && itemId < 1113000;
    }
    
    public static boolean isEffectRing(final int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }
    
    public static boolean isFriendshipRing(final int itemId) {
        switch (itemId) {
            case 1049000:
            case 1112015:
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810:
            case 1112811:
            case 1112812:
            case 1112816:
            case 1112817: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isCrushRing(final int itemId) {
        switch (itemId) {
            case 1048000:
            case 1048001:
            case 1048002:
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005:
            case 1112006:
            case 1112007:
            case 1112012:
            case 1112015: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int Equipment_Bonus_EXP(final int itemid) {
        switch (itemid) {
            case 1122017: {
                return 30;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getExpForLevel(final int i, final int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        }
        if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }
    
    public static int getMaxLevel(final int itemId) {
        if (isTimelessItem(itemId)) {
            return 5;
        }
        if (isReverseItem(itemId)) {
            return 3;
        }
        switch (itemId) {
            case 1302108:
            case 1302109:
            case 1312040:
            case 1312041:
            case 1322066:
            case 1322067:
            case 1332082:
            case 1332083:
            case 1372047:
            case 1372048:
            case 1382063:
            case 1382064:
            case 1402054:
            case 1402055:
            case 1412036:
            case 1412037:
            case 1422040:
            case 1422041:
            case 1432051:
            case 1432052:
            case 1442072:
            case 1442073:
            case 1452063:
            case 1452064:
            case 1462057:
            case 1462058:
            case 1472078:
            case 1472079:
            case 1482035:
            case 1482036: {
                return 1;
            }
            case 1072376: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getStatChance() {
        return 25;
    }
    
    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035: {
                return MonsterStatus.ACC;
            }
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036: {
                return MonsterStatus.SPEED;
            }
            default: {
                return null;
            }
        }
    }
    
    public static int getXForStat(final MonsterStatus stat) {
        switch (stat) {
            case ACC: {
                return -70;
            }
            case SPEED: {
                return -50;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getSkillForStat(final MonsterStatus stat) {
        switch (stat) {
            case ACC: {
                return 3221006;
            }
            case SPEED: {
                return 3121007;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 3210:
            case 3310:
            case 3510: {
                return 1;
            }
            case 3211:
            case 3311:
            case 3511: {
                return 2;
            }
            case 3212:
            case 3312:
            case 3512: {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }
    
    public static int getMountItem(final int sourceid) {
        switch (sourceid) {
            case 5221006: {
                return 1932000;
            }
            case 33001001: {
                return 1932015;
            }
            case 35001002:
            case 35120000: {
                return 1932016;
            }
            case 1013:
            case 1046:
            case 10001013:
            case 10001046:
            case 20001013:
            case 20001046:
            case 20011013:
            case 20011046:
            case 30001013:
            case 30001046: {
                return 1932001;
            }
            case 1015:
            case 1048:
            case 10001015:
            case 10001048:
            case 20001015:
            case 20001048:
            case 20011015:
            case 20011048:
            case 30001015:
            case 30001048: {
                return 1932002;
            }
            case 1007:
            case 1016:
            case 1017:
            case 1027:
            case 10001016:
            case 10001017:
            case 10001027:
            case 20001016:
            case 20001017:
            case 20001027:
            case 20011016:
            case 20011017:
            case 20011027:
            case 30001016:
            case 30001017:
            case 30001027: {
                return 1932007;
            }
            case 1018:
            case 10001018:
            case 20001018:
            case 20011018:
            case 30001018: {
                return 1932003;
            }
            case 1019:
            case 10001019:
            case 20001019:
            case 20011019:
            case 30001019: {
                return 1932005;
            }
            case 1025:
            case 10001025:
            case 20001025:
            case 20011025:
            case 30001025: {
                return 1932006;
            }
            case 1028:
            case 10001028:
            case 20001028:
            case 20011028:
            case 30001028: {
                return 1932008;
            }
            case 1029:
            case 10001029:
            case 20001029:
            case 20011029:
            case 30001029: {
                return 1932009;
            }
            case 1030:
            case 10001030:
            case 20001030:
            case 20011030:
            case 30001030: {
                return 1932011;
            }
            case 1031:
            case 10001031:
            case 20001031:
            case 20011031:
            case 30001031: {
                return 1932010;
            }
            case 1034:
            case 10001034:
            case 20001034:
            case 20011034:
            case 30001034: {
                return 1932014;
            }
            case 1035:
            case 10001035:
            case 20001035:
            case 20011035:
            case 30001035: {
                return 1932012;
            }
            case 1036:
            case 10001036:
            case 20001036:
            case 20011036:
            case 30001036: {
                return 1932017;
            }
            case 1037:
            case 10001037:
            case 20001037:
            case 20011037:
            case 30001037: {
                return 1932018;
            }
            case 1038:
            case 10001038:
            case 20001038:
            case 20011038:
            case 30001038: {
                return 1932019;
            }
            case 1039:
            case 10001039:
            case 20001039:
            case 20011039:
            case 30001039: {
                return 1932020;
            }
            case 1040:
            case 10001040:
            case 20001040:
            case 20011040:
            case 30001040: {
                return 1932021;
            }
            case 1042:
            case 10001042:
            case 20001042:
            case 20011042:
            case 30001042: {
                return 1932022;
            }
            case 1044:
            case 10001044:
            case 20001044:
            case 20011044:
            case 30001044: {
                return 1932023;
            }
            case 1045:
            case 10001045:
            case 20001045:
            case 20011045:
            case 30001045: {
                return 1932030;
            }
            case 1049:
            case 10001049:
            case 20001049:
            case 20011049:
            case 30001049: {
                return 1932025;
            }
            case 1050:
            case 10001050:
            case 20001050:
            case 20011050:
            case 30001050: {
                return 1932004;
            }
            case 1051:
            case 10001051:
            case 20001051:
            case 20011051:
            case 30001051: {
                return 1932026;
            }
            case 1052:
            case 10001052:
            case 20001052:
            case 20011052:
            case 30001052: {
                return 1932027;
            }
            case 1053:
            case 10001053:
            case 20001053:
            case 20011053:
            case 30001053: {
                return 1932028;
            }
            case 1054:
            case 10001054:
            case 20001054:
            case 20011054:
            case 30001054: {
                return 1932029;
            }
            case 1069:
            case 10001069:
            case 20001069:
            case 20011069:
            case 30001069: {
                return 1932038;
            }
            case 1096:
            case 10001096:
            case 20001096:
            case 20011096:
            case 30001096: {
                return 1932045;
            }
            case 1101:
            case 10001101:
            case 20001101:
            case 20011101:
            case 30001101: {
                return 1932046;
            }
            case 1102:
            case 10001102:
            case 20001102:
            case 20011102:
            case 30001102: {
                return 1932047;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static boolean isKatara(final int itemId) {
        return itemId / 10000 == 134;
    }
    
    public static boolean isDagger(final int itemId) {
        return itemId / 10000 == 133;
    }
    
    public static boolean isApplicableSkill(final int skil) {
        return skil < 40000000 && (skil % 10000 < 8000 || skil % 10000 > 8003);
    }
    
    public static boolean isApplicableSkill_(final int skil) {
        return skil >= 90000000 || (skil % 10000 >= 8000 && skil % 10000 <= 8003);
    }
    
    public static boolean isTablet(final int itemId) {
        return itemId / 1000 == 2047;
    }
    
    public static int getSuccessTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) {
            switch (level) {
                case 0: {
                    return 70;
                }
                case 1: {
                    return 55;
                }
                case 2: {
                    return 43;
                }
                case 3: {
                    return 33;
                }
                case 4: {
                    return 26;
                }
                case 5: {
                    return 20;
                }
                case 6: {
                    return 16;
                }
                case 7: {
                    return 12;
                }
                case 8: {
                    return 10;
                }
                default: {
                    return 7;
                }
            }
        }
        else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0: {
                    return 70;
                }
                case 1: {
                    return 35;
                }
                case 2: {
                    return 18;
                }
                case 3: {
                    return 12;
                }
                default: {
                    return 7;
                }
            }
        }
        else {
            switch (level) {
                case 0: {
                    return 70;
                }
                case 1: {
                    return 50;
                }
                case 2: {
                    return 36;
                }
                case 3: {
                    return 26;
                }
                case 4: {
                    return 19;
                }
                case 5: {
                    return 14;
                }
                case 6: {
                    return 10;
                }
                default: {
                    return 7;
                }
            }
        }
    }
    
    public static int getCurseTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) {
            switch (level) {
                case 0: {
                    return 10;
                }
                case 1: {
                    return 12;
                }
                case 2: {
                    return 16;
                }
                case 3: {
                    return 20;
                }
                case 4: {
                    return 26;
                }
                case 5: {
                    return 33;
                }
                case 6: {
                    return 43;
                }
                case 7: {
                    return 55;
                }
                case 8: {
                    return 70;
                }
                default: {
                    return 100;
                }
            }
        }
        else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0: {
                    return 12;
                }
                case 1: {
                    return 18;
                }
                case 2: {
                    return 35;
                }
                case 3: {
                    return 70;
                }
                default: {
                    return 100;
                }
            }
        }
        else {
            switch (level) {
                case 0: {
                    return 10;
                }
                case 1: {
                    return 14;
                }
                case 2: {
                    return 19;
                }
                case 3: {
                    return 26;
                }
                case 4: {
                    return 36;
                }
                case 5: {
                    return 50;
                }
                case 6: {
                    return 70;
                }
                default: {
                    return 100;
                }
            }
        }
    }
    
    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113000);
    }
    
    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        if (newstate == 7) {
            return (i == 0 || Randomizer.nextInt(10) == 0) ? (potentialID >= 30000) : (potentialID >= 20000 && potentialID < 30000);
        }
        if (newstate == 6) {
            return (i == 0 || Randomizer.nextInt(10) == 0) ? (potentialID >= 20000 && potentialID < 30000) : (potentialID >= 10000 && potentialID < 20000);
        }
        return newstate == 5 && ((i == 0 || Randomizer.nextInt(10) == 0) ? (potentialID >= 10000 && potentialID < 20000) : (potentialID < 10000));
    }
    
    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: {
                return isWeapon(itemId);
            }
            case 11: {
                return !isWeapon(itemId);
            }
            case 20: {
                return itemId / 10000 == 109;
            }
            case 21: {
                return itemId / 10000 == 180;
            }
            case 40: {
                return isAccessory(itemId);
            }
            case 51: {
                return itemId / 10000 == 100;
            }
            case 52: {
                return itemId / 10000 == 110;
            }
            case 53: {
                return itemId / 10000 == 104 || itemId / 10000 == 105 || itemId / 10000 == 106;
            }
            case 54: {
                return itemId / 10000 == 108;
            }
            case 55: {
                return itemId / 10000 == 107;
            }
            case 90: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            if (isKOC(jobid)) {
                if (mountid < 1902005 || mountid > 1902007) {
                    return false;
                }
            }
            else if (isAdventurer(jobid)) {
                if (mountid < 1902000 || mountid > 1902002) {
                    return false;
                }
            }
            else if (isAran(jobid)) {
                if (mountid < 1902015 || mountid > 1902018) {
                    return false;
                }
            }
            else if (isEvan(jobid) && (mountid < 1902040 || mountid > 1902042)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000;
    }
    
    public static boolean canScroll(final int itemId) {
        return itemId / 100000 != 19 && itemId / 100000 != 16;
    }
    
    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: {
                return false;
            }
            default: {
                return canScroll(itemId);
            }
        }
    }
    
    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        }
        if (job >= 410 && job <= 412) {
            return 4100000;
        }
        if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }
    
    public static int getExpRate_Below10(final int job) {
        if (isEvan(job)) {
            return 1;
        }
        if (isAran(job) || isKOC(job)) {
            return 5;
        }
        return 1;
    }
    
    public static int getExpRate_Quest(final int level) {
        return 1;
    }
    
    public static String getCashBlockedMsg(final int id) {
        switch (id) {
            case 5062000: {
                return "This item may only be purchased at the PlayerNPC in FM.";
            }
            default: {
                return "This item is blocked from the Cash Shop.";
            }
        }
    }
    
    public static boolean isCustomReactItem(final int rid, final int iid, final int original) {
        if (rid == 2008006) {
            return iid == Calendar.getInstance().get(7) + 4001055;
        }
        return iid == original;
    }
    
    public static int getJobNumber(final int jobz) {
        final int job = jobz % 1000;
        if (job / 100 == 0) {
            return 0;
        }
        if (job / 10 == 0) {
            return 1;
        }
        return 2 + job % 10;
    }
    
    public static boolean isForceRespawn(final int mapid) {
        switch (mapid) {
            case 925100100: {
                return true;
            }
            default: {
                return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
            }
        }
    }
    
    public static int getFishingTime(final boolean vip, final boolean gm) {
        return 20000;
    }
    
    public static int getCustomSpawnID(final int summoner, final int def) {
        switch (summoner) {
            case 9400589:
            case 9400748: {
                return 9400706;
            }
            default: {
                return def;
            }
        }
    }
    
    public static boolean canForfeit(final int questid) {
        switch (questid) {
            case 20000:
            case 20010:
            case 20015:
            case 20020: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
    }
    
    public static boolean is豆豆装备(final int itemId) {
        switch (itemId) {
            case 1002448:
            case 1002543:
            case 1002583:
            case 1002609:
            case 1002665:
            case 1002695:
            case 1002760:
            case 1002761:
            case 1002985:
            case 1002986:
            case 1052137:
            case 1092051:
            case 1123200:
            case 1702138:
            case 1702232:
            case 1902031:
            case 1902032:
            case 1902033:
            case 1902034:
            case 1902035:
            case 1902036:
            case 1902037:
            case 1912024:
            case 1912025:
            case 1912026:
            case 1912027:
            case 1912028:
            case 1912029:
            case 1912030: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int getCustomReactItem(final int rid, final int original) {
        if (rid == 2008006) {
            return Calendar.getInstance().get(7) + 4001055;
        }
        return original;
    }
    
    public static boolean Summon_Skill_ID_550(final int SkillID) {
        switch (SkillID) {
            case 3121006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_500(final int SkillID) {
        switch (SkillID) {
            case 3221005:
            case 5220002: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_450(final int SkillID) {
        switch (SkillID) {
            case 5211002: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_300(final int SkillID) {
        switch (SkillID) {
            case 2221005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_270(final int SkillID) {
        switch (SkillID) {
            case 2121005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_250(final int SkillID) {
        switch (SkillID) {
            case 12111004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_230(final int SkillID) {
        switch (SkillID) {
            case 2321003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_200(final int SkillID) {
        switch (SkillID) {
            case 5211001: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_150(final int SkillID) {
        switch (SkillID) {
            case 2311006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_100(final int SkillID) {
        switch (SkillID) {
            case 3111005:
            case 3211005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Summon_Skill_ID_40(final int SkillID) {
        switch (SkillID) {
            case 11001004:
            case 12001004:
            case 13001004:
            case 14001005:
            case 15001004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int Novice_Skill(final int skill, final MapleCharacter c, final int damage) {
        switch (skill) {
            case 1000:
            case 10001000:
            case 20001000: {
                if (c.getStat().getCurrentMaxBaseDamage() <= damage / 13) {
                    return 1;
                }
                break;
            }
        }
        return damage;
    }
    
    public static boolean Novice_Skill(final int skill) {
        switch (skill) {
            case 1000:
            case 10001000:
            case 20001000: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Ares_Skill_140(final int skill) {
        switch (skill) {
            case 21000002:
            case 21100002:
            case 21110006:
            case 21111005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Ares_Skill_350(final int skill) {
        switch (skill) {
            case 21100001:
            case 21110002:
            case 21110003:
            case 21120002: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Ares_Skill_800(final int skill) {
        switch (skill) {
            case 21100004:
            case 21110004:
            case 21120005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Ares_Skill_1500(final int skill) {
        switch (skill) {
            case 21120006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_270(final int skill) {
        switch (skill) {
            case 15001001:
            case 15001002:
            case 15101006:
            case 15110000:
            case 15111004:
            case 15111006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_420(final int skill) {
        switch (skill) {
            case 15101003:
            case 15111001:
            case 15111007: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_650(final int skill) {
        switch (skill) {
            case 15111003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Night_Knight_Skill_220(final int skill) {
        switch (skill) {
            case 14001004:
            case 14100005:
            case 14101006:
            case 14111002:
            case 14111005:
            case 14111006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Wind_Knight_Skill_160(final int skill) {
        switch (skill) {
            case 13001003:
            case 13101005:
            case 13111000:
            case 13111001:
            case 13111002: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Wind_Knight_Skill_550(final int skill) {
        switch (skill) {
            case 13111006:
            case 13111007: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Fire_Knight_Skill_140(final int skill) {
        switch (skill) {
            case 12001003:
            case 12101002:
            case 12101006:
            case 12111005:
            case 12111006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Fire_Knight_Skill_500(final int skill) {
        switch (skill) {
            case 12111003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Ghost_Knight_Skill_320(final int skill) {
        switch (skill) {
            case 11001002:
            case 11001003:
            case 11101004:
            case 11111002:
            case 11111003:
            case 11111004:
            case 11111006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Pirate_Skill_290(final int skill) {
        switch (skill) {
            case 5001001:
            case 5001002:
            case 5001003:
            case 5101002:
            case 5101003:
            case 5121007:
            case 5201001:
            case 5201002:
            case 5201004:
            case 5201006:
            case 5210000:
            case 5211004:
            case 5211005:
            case 5221004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Pirate_Skill_420(final int skill) {
        switch (skill) {
            case 5101004:
            case 5121004:
            case 5211006:
            case 5221007: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Pirate_Skill_700(final int skill) {
        switch (skill) {
            case 5111006:
            case 5121005:
            case 5220011: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Pirate_Skill_810(final int skill) {
        switch (skill) {
            case 5121001:
            case 5221008: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Pirate_Skill_1200(final int skill) {
        switch (skill) {
            case 5221003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_180(final int skill) {
        switch (skill) {
            case 4001334:
            case 4001344:
            case 4101005:
            case 4111004:
            case 4111005:
            case 4121007:
            case 4201004:
            case 4201005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_250(final int skill) {
        switch (skill) {
            case 4211004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Thief_Skill_500(final int skill) {
        switch (skill) {
            case 4211002:
            case 4221001:
            case 4221007: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Bowman_Skill_180(final int skill) {
        switch (skill) {
            case 3001005:
            case 3101005:
            case 3111003:
            case 3111004:
            case 3111006:
            case 3121003:
            case 3121004:
            case 3201005:
            case 3211003:
            case 3211004:
            case 3211006:
            case 3221003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Bowman_Skill_260(final int skill) {
        switch (skill) {
            case 3000001:
            case 3001004:
            case 3101003:
            case 3201003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Bowman_Skill_850(final int skill) {
        switch (skill) {
            case 3221001: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Magician_Skill_90(final int skill) {
        switch (skill) {
            case 2001004:
            case 2001005:
            case 2301005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Magician_Skill_180(final int skill) {
        switch (skill) {
            case 2101004:
            case 2111002:
            case 2111006:
            case 2121003:
            case 2201004:
            case 2211002:
            case 2211003:
            case 2211006:
            case 2221003:
            case 2221006:
            case 2311004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Magician_Skill_240(final int skill) {
        switch (skill) {
            case 2121006:
            case 2201005:
            case 2301002:
            case 2321007: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Magician_Skill_670(final int skill) {
        switch (skill) {
            case 2121001:
            case 2121007:
            case 2221001:
            case 2221007:
            case 2321001:
            case 2321008: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Warrior_Skill_900(final int skill) {
        switch (skill) {
            case 1221011: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Warrior_Skill_550(final int skill) {
        switch (skill) {
            case 1221009: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Warrior_Skill_450(final int skill) {
        switch (skill) {
            case 1001004:
            case 1001005:
            case 1100002:
            case 1100003:
            case 1111002:
            case 1111008:
            case 1121006:
            case 1121008:
            case 1121009:
            case 1211002:
            case 1221007:
            case 1311001:
            case 1311002:
            case 1311003:
            case 1311004:
            case 1311005:
            case 1311006:
            case 1321003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean Warrior_Skill_2000(final int skill) {
        switch (skill) {
            case 1111003:
            case 1111004:
            case 1111005:
            case 1111006: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean No_Mob(final int MobID) {
        switch (MobID) {
            case 8220003:
            case 8220004:
            case 8220005:
            case 8220006:
            case 8500001:
            case 8500002:
            case 8510000:
            case 8510100:
            case 8520000:
            case 8800000:
            case 8800001:
            case 8800002:
            case 8800003:
            case 8800004:
            case 8800005:
            case 8800006:
            case 8800007:
            case 8800008:
            case 8800009:
            case 8800010:
            case 8810000:
            case 8810001:
            case 8810002:
            case 8810003:
            case 8810004:
            case 8810005:
            case 8810006:
            case 8810007:
            case 8810008:
            case 8810009:
            case 8810010:
            case 8810011:
            case 8810012:
            case 8810013:
            case 8810014:
            case 8810015:
            case 8810016:
            case 8810017:
            case 8810018:
            case 8820000:
            case 8820001:
            case 8820002:
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8820007:
            case 8820008:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013:
            case 8820014:
            case 8820015:
            case 8820016:
            case 8820017:
            case 8820018:
            case 8820019:
            case 8820020:
            case 8820021:
            case 9300028:
            case 9420520:
            case 9420521:
            case 9420522:
            case 9420541:
            case 9420542:
            case 9420543:
            case 9420544:
            case 9420545:
            case 9420546:
            case 9420547:
            case 9420548:
            case 9420549:
            case 9420550: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean 不检测技能(final int SkillID) {
        switch (SkillID) {
            case 1111008:
            case 1121001:
            case 1121006:
            case 1221001:
            case 1221007:
            case 1321001:
            case 1321003:
            case 21100002:
            case 21100004:
            case 21110004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int getMonsterHP(final int level) {
        if (level < 0 || level >= GameConstants.mobHpVal.length) {
            return Integer.MAX_VALUE;
        }
        return GameConstants.mobHpVal[level];
    }
    
    public static boolean isMarriageRing(final int itemId) {
        switch (itemId) {
            case 1112013:
            case 1112300:
            case 1112301:
            case 1112302:
            case 1112303:
            case 1112304:
            case 1112305:
            case 1112306:
            case 1112307:
            case 1112308:
            case 1112309:
            case 1112310:
            case 1112311:
            case 1112315:
            case 1112316:
            case 1112317:
            case 1112318:
            case 1112319:
            case 1112320:
            case 1112803:
            case 1112806:
            case 1112807:
            case 1112808:
            case 1112809: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isElseSkill(final int id) {
        switch (id) {
            case 1009:
            case 1020:
            case 4211006:
            case 10001009:
            case 10001020:
            case 20001009:
            case 20001020: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static double getAttackRange(final MapleCharacter chr, final MapleStatEffect def, final AttackInfo attack) {
        final int rangeInc = 50;
        final double base = 450.0;
        double defRange = (base + rangeInc) * (base + rangeInc);
        if (def != null) {
            if (getAttackRangeBySkill(attack) != 0.0) {
                defRange = getAttackRangeBySkill(attack);
            }
        }
        else {
            defRange = getAttackRangeByWeapon(chr);
        }
        return defRange;
    }
    
    private static double getAttackRangeBySkill(final AttackInfo attack) {
        double defRange = 0.0;
        switch (attack.skill) {
            case 21120006: {
                defRange = 800000.0;
                break;
            }
            case 2121007:
            case 2221007:
            case 2321008: {
                defRange = 750000.0;
                break;
            }
            case 2221006:
            case 3101005:
            case 21101003: {
                defRange = 600000.0;
                break;
            }
            case 2111003: {
                defRange = 400000.0;
                break;
            }
            case 1121008:
            case 4001344: {
                defRange = 350000.0;
                break;
            }
            case 2211002: {
                defRange = 300000.0;
                break;
            }
            case 2001005:
            case 2211003:
            case 2311004:
            case 5110001: {
                defRange = 250000.0;
                break;
            }
            case 2321007:
            case 5221004: {
                defRange = 200000.0;
                break;
            }
            case 1000:
            case 20001000: {
                defRange = 130000.0;
                break;
            }
        }
        return defRange;
    }
    
    public static int getMountS(final int s) {
        switch (s) {
            case 1: {
                return 1932003;
            }
            case 2: {
                return 1932004;
            }
            case 3: {
                return 1932005;
            }
            case 4: {
                return 1932006;
            }
            case 5: {
                return 1932007;
            }
            case 6: {
                return 1932008;
            }
            case 7: {
                return 1932009;
            }
            case 8: {
                return 1932010;
            }
            case 9: {
                return 1932011;
            }
            case 10: {
                return 1932012;
            }
            case 11: {
                return 1932013;
            }
            case 12: {
                return 1932014;
            }
            case 13: {
                return 1932015;
            }
            case 14: {
                return 1932016;
            }
            case 15: {
                return 1932017;
            }
            case 16: {
                return 1932018;
            }
            case 17: {
                return 1932020;
            }
            case 18: {
                return 1932021;
            }
            case 19: {
                return 1932022;
            }
            case 20: {
                return 1932023;
            }
            case 21: {
                return 1932025;
            }
            case 22: {
                return 1932026;
            }
            case 23: {
                return 1932027;
            }
            case 24: {
                return 1932028;
            }
            case 25: {
                return 1932029;
            }
            case 26: {
                return 1932030;
            }
            case 27: {
                return 1932031;
            }
            case 28: {
                return 1932032;
            }
            case 29: {
                return 1932033;
            }
            case 30: {
                return 1932034;
            }
            case 31: {
                return 1932035;
            }
            case 32: {
                return 1932036;
            }
            case 33: {
                return 1932038;
            }
            case 34: {
                return 1932041;
            }
            case 35: {
                return 1932043;
            }
            case 36: {
                return 1932044;
            }
            case 37: {
                return 1932045;
            }
            case 38: {
                return 1932046;
            }
            case 39: {
                return 1932047;
            }
            case 40: {
                return 1932048;
            }
            case 41: {
                return 1932049;
            }
            case 42: {
                return 1932050;
            }
            case 43: {
                return 1932051;
            }
            case 44: {
                return 1932052;
            }
            case 45: {
                return 1932053;
            }
            case 46: {
                return 1932054;
            }
            case 47: {
                return 1932055;
            }
            case 48: {
                return 1932056;
            }
            case 49: {
                return 1932057;
            }
            case 50: {
                return 1932058;
            }
            case 51: {
                return 1932059;
            }
            case 52: {
                return 1932060;
            }
            case 53: {
                return 1932061;
            }
            case 54: {
                return 1932062;
            }
            case 55: {
                return 1932063;
            }
            case 56: {
                return 1932064;
            }
            case 57: {
                return 1932065;
            }
            case 58: {
                return 1932066;
            }
            case 59: {
                return 1932071;
            }
            case 60: {
                return 1932072;
            }
            case 61: {
                return 1932078;
            }
            case 62: {
                return 1932080;
            }
            case 63: {
                return 1932081;
            }
            case 64: {
                return 1932083;
            }
            case 65: {
                return 1932084;
            }
            case 66: {
                return 1932001;
            }
            case 67: {
                return 1932086;
            }
            case 68: {
                return 1932087;
            }
            case 69: {
                return 1932088;
            }
            case 70: {
                return 1932089;
            }
            case 71: {
                return 1932090;
            }
            case 72: {
                return 1932091;
            }
            case 73: {
                return 1932092;
            }
            case 74: {
                return 1932093;
            }
            case 75: {
                return 1932094;
            }
            case 76: {
                return 1932095;
            }
            case 77: {
                return 1932096;
            }
            case 78: {
                return 1932097;
            }
            case 79: {
                return 1932098;
            }
            case 80: {
                return 1932099;
            }
            case 81: {
                return 1932100;
            }
            case 82: {
                return 1932102;
            }
            case 83: {
                return 1932103;
            }
            case 84: {
                return 1932105;
            }
            case 85: {
                return 1932106;
            }
            case 86: {
                return 1932107;
            }
            case 87: {
                return 1932108;
            }
            case 88: {
                return 1932109;
            }
            case 89: {
                return 1932110;
            }
            case 90: {
                return 1932112;
            }
            case 91: {
                return 1932113;
            }
            case 92: {
                return 1932114;
            }
            case 93: {
                return 1932115;
            }
            case 94: {
                return 1932116;
            }
            case 95: {
                return 1932117;
            }
            case 96: {
                return 1932118;
            }
            case 97: {
                return 1932119;
            }
            case 98: {
                return 1932120;
            }
            case 99: {
                return 1932121;
            }
            case 100: {
                return 1932122;
            }
            case 101: {
                return 1932123;
            }
            case 102: {
                return 1932124;
            }
            case 103: {
                return 1932126;
            }
            case 104: {
                return 1932127;
            }
            case 105: {
                return 1932128;
            }
            case 106: {
                return 1932129;
            }
            case 107: {
                return 1932130;
            }
            case 108: {
                return 1932131;
            }
            case 109: {
                return 1932132;
            }
            case 110: {
                return 1932133;
            }
            case 111: {
                return 1932134;
            }
            case 112: {
                return 1932135;
            }
            case 113: {
                return 1932136;
            }
            case 114: {
                return 1932137;
            }
            case 115: {
                return 1932138;
            }
            case 116: {
                return 1932139;
            }
            case 117: {
                return 1932140;
            }
            case 118: {
                return 1932141;
            }
            case 119: {
                return 1932142;
            }
            case 120: {
                return 1932143;
            }
            case 121: {
                return 1932144;
            }
            case 122: {
                return 1932145;
            }
            case 123: {
                return 1932146;
            }
            case 124: {
                return 1932147;
            }
            case 125: {
                return 1932148;
            }
            case 126: {
                return 1932149;
            }
            case 127: {
                return 1932150;
            }
            case 128: {
                return 1932151;
            }
            case 129: {
                return 1932152;
            }
            case 130: {
                return 1932153;
            }
            case 131: {
                return 1932154;
            }
            case 132: {
                return 1932155;
            }
            case 133: {
                return 1932156;
            }
            case 134: {
                return 1932157;
            }
            case 135: {
                return 1932158;
            }
            case 136: {
                return 1932159;
            }
            case 137: {
                return 1932002;
            }
            case 138: {
                return 1932161;
            }
            case 139: {
                return 1932162;
            }
            case 140: {
                return 1932163;
            }
            case 141: {
                return 1932164;
            }
            case 142: {
                return 1932165;
            }
            case 143: {
                return 1932166;
            }
            case 144: {
                return 1932167;
            }
            case 145: {
                return 1932168;
            }
            case 146: {
                return 1932169;
            }
            case 147: {
                return 1932170;
            }
            case 148: {
                return 1932171;
            }
            case 149: {
                return 1932172;
            }
            case 150: {
                return 1932173;
            }
            case 151: {
                return 1932174;
            }
            case 152: {
                return 1932175;
            }
            case 153: {
                return 1932176;
            }
            case 154: {
                return 1932177;
            }
            case 155: {
                return 1932178;
            }
            case 156: {
                return 1932179;
            }
            case 157: {
                return 1932180;
            }
            case 158: {
                return 1932181;
            }
            case 159: {
                return 1932182;
            }
            case 160: {
                return 1932183;
            }
            case 161: {
                return 1932184;
            }
            case 162: {
                return 1932185;
            }
            case 163: {
                return 1932186;
            }
            case 164: {
                return 1932187;
            }
            case 165: {
                return 1932188;
            }
            case 166: {
                return 1932189;
            }
            case 167: {
                return 1932190;
            }
            case 168: {
                return 1932191;
            }
            case 169: {
                return 1932192;
            }
            case 170: {
                return 1932193;
            }
            case 171: {
                return 1932194;
            }
            case 172: {
                return 1932195;
            }
            case 173: {
                return 1932196;
            }
            case 174: {
                return 1932197;
            }
            case 175: {
                return 1932198;
            }
            case 176: {
                return 1932199;
            }
            case 177: {
                return 1932200;
            }
            case 178: {
                return 1932201;
            }
            case 179: {
                return 1932202;
            }
            case 180: {
                return 1932203;
            }
            case 181: {
                return 1932204;
            }
            case 182: {
                return 1932205;
            }
            case 183: {
                return 1932206;
            }
            case 184: {
                return 1932207;
            }
            case 185: {
                return 1932208;
            }
            case 186: {
                return 1932211;
            }
            case 187: {
                return 1932212;
            }
            case 188: {
                return 1932213;
            }
            case 189: {
                return 1932214;
            }
            case 190: {
                return 1932215;
            }
            case 191: {
                return 1932216;
            }
            case 192: {
                return 1932217;
            }
            case 193: {
                return 1932218;
            }
            case 194: {
                return 1932219;
            }
            case 195: {
                return 1932220;
            }
            case 196: {
                return 1932221;
            }
            case 197: {
                return 1932222;
            }
            case 198: {
                return 1932223;
            }
            case 199: {
                return 1932224;
            }
            case 200: {
                return 1932225;
            }
            case 201: {
                return 1932226;
            }
            case 202: {
                return 1932227;
            }
            case 203: {
                return 1932228;
            }
            case 204: {
                return 1932230;
            }
            case 205: {
                return 1932231;
            }
            case 206: {
                return 1932232;
            }
            case 207: {
                return 1932234;
            }
            case 208: {
                return 1932235;
            }
            case 209: {
                return 1932236;
            }
            case 210: {
                return 1932237;
            }
            case 211: {
                return 1932238;
            }
            case 212: {
                return 1932239;
            }
            case 213: {
                return 1932240;
            }
            case 214: {
                return 1932241;
            }
            case 215: {
                return 1932242;
            }
            case 216: {
                return 1932243;
            }
            case 217: {
                return 1932244;
            }
            case 218: {
                return 1932245;
            }
            case 219: {
                return 1932246;
            }
            case 220: {
                return 1932247;
            }
            case 221: {
                return 1932248;
            }
            case 222: {
                return 1932249;
            }
            case 223: {
                return 1932250;
            }
            case 224: {
                return 1932251;
            }
            case 225: {
                return 1932252;
            }
            case 226: {
                return 1932253;
            }
            case 227: {
                return 1932254;
            }
            case 228: {
                return 1932255;
            }
            case 229: {
                return 1932256;
            }
            case 230: {
                return 1932258;
            }
            case 231: {
                return 1932259;
            }
            case 232: {
                return 1932260;
            }
            case 233: {
                return 1932261;
            }
            case 234: {
                return 1932262;
            }
            case 235: {
                return 1932263;
            }
            case 236: {
                return 1932264;
            }
            case 237: {
                return 1932265;
            }
            case 238: {
                return 1932266;
            }
            case 239: {
                return 1932267;
            }
            case 240: {
                return 1932268;
            }
            case 241: {
                return 1932269;
            }
            case 242: {
                return 1932270;
            }
            case 243: {
                return 1932271;
            }
            case 244: {
                return 1932272;
            }
            case 245: {
                return 1932273;
            }
            case 246: {
                return 1932274;
            }
            case 247: {
                return 1932275;
            }
            case 248: {
                return 1932276;
            }
            case 249: {
                return 1932277;
            }
            case 250: {
                return 1932279;
            }
            case 251: {
                return 1932280;
            }
            case 252: {
                return 1932281;
            }
            case 253: {
                return 1932282;
            }
            case 254: {
                return 1932286;
            }
            case 255: {
                return 1932287;
            }
            case 256: {
                return 1932288;
            }
            case 257: {
                return 1932289;
            }
            case 258: {
                return 1932290;
            }
            case 259: {
                return 1932291;
            }
            case 260: {
                return 1932292;
            }
            case 261: {
                return 1932293;
            }
            case 262: {
                return 1932294;
            }
            case 263: {
                return 1932295;
            }
            case 264: {
                return 1932296;
            }
            case 265: {
                return 1932297;
            }
            case 266: {
                return 1932298;
            }
            case 267: {
                return 1932299;
            }
            case 268: {
                return 1932300;
            }
            case 269: {
                return 1932301;
            }
            case 270: {
                return 1932302;
            }
            case 271: {
                return 1932303;
            }
            case 272: {
                return 1932304;
            }
            case 273: {
                return 1932305;
            }
            case 274: {
                return 1932306;
            }
            case 275: {
                return 1932307;
            }
            case 276: {
                return 1932308;
            }
            case 277: {
                return 1932310;
            }
            case 278: {
                return 1932311;
            }
            case 279: {
                return 1932313;
            }
            case 280: {
                return 1932314;
            }
            case 281: {
                return 1932315;
            }
            case 282: {
                return 1932316;
            }
            case 283: {
                return 1932317;
            }
            case 284: {
                return 1932318;
            }
            case 285: {
                return 1932319;
            }
            case 286: {
                return 1932320;
            }
            case 287: {
                return 1932321;
            }
            case 288: {
                return 1932322;
            }
            case 289: {
                return 1932323;
            }
            case 290: {
                return 1932324;
            }
            case 291: {
                return 1932325;
            }
            case 292: {
                return 1932326;
            }
            case 293: {
                return 1932327;
            }
            case 294: {
                return 1932329;
            }
            case 295: {
                return 1932330;
            }
            case 296: {
                return 1932332;
            }
            case 297: {
                return 1932334;
            }
            case 298: {
                return 1932335;
            }
            case 299: {
                return 1932336;
            }
            case 300: {
                return 1932337;
            }
            case 301: {
                return 1932338;
            }
            case 302: {
                return 1932339;
            }
            case 303: {
                return 1932341;
            }
            case 304: {
                return 1932342;
            }
            case 305: {
                return 1932350;
            }
            case 306: {
                return 1932351;
            }
            case 307: {
                return 1932352;
            }
            case 308: {
                return 1932353;
            }
            case 309: {
                return 1932355;
            }
            case 310: {
                return 1932366;
            }
            case 311: {
                return 1939000;
            }
            case 312: {
                return 1939001;
            }
            case 313: {
                return 1939002;
            }
            case 314: {
                return 1939003;
            }
            case 315: {
                return 1939004;
            }
            case 316: {
                return 1939005;
            }
            case 317: {
                return 1932374;
            }
            case 318: {
                return 1932376;
            }
            case 319: {
                return 1932378;
            }
            case 320: {
                return 1939006;
            }
            default: {
                return 1930001;
            }
        }
    }
    
    private static double getAttackRangeByWeapon(final MapleCharacter chr) {
        return 100000.0;
    }
    
    static {
        rangedMapobjectTypes = Collections.unmodifiableList((List<? extends MapleMapObjectType>)Arrays.asList(MapleMapObjectType.ITEM, MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.REACTOR, MapleMapObjectType.SUMMON, MapleMapObjectType.NPC, MapleMapObjectType.LOVE, MapleMapObjectType.MIST));
        exp = new int[] { 0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1716, 2360, 3216, 4200, 5460, 7050, 8840, 11040, 13716, 16680, 20216, 24402, 28980, 34320, 40512, 47216, 54900, 63666, 73080, 83720, 95700, 108480, 122760, 138666, 155540, 174216, 194832, 216600, 240500, 266682, 294216, 324240, 356916, 391160, 428280, 468450, 510420, 555680, 604416, 655200, 709716, 748608, 789631, 832902, 878545, 926689, 977471, 1031036, 1087536, 1147132, 1209994, 1276301, 1346242, 1420016, 1497832, 1579913, 1666492, 1757815, 1854143, 1955750, 2062925, 2175973, 2295216, 2410993, 2553663, 2693603, 2841212, 2996910, 3161140, 3334370, 3517093, 3709829, 3913127, 4127566, 4353756, 4592341, 4844001, 5109452, 5389449, 5684790, 5996316, 6324914, 6671519, 7037118, 7422752, 7829518, 8258575, 8711144, 9188514, 9692044, 10223168, 10783397, 11374327, 11997640, 12655110, 13348610, 14080113, 14851703, 15665576, 16524049, 17429566, 18384706, 19392187, 20454878, 21575805, 22758159, 24005306, 25320796, 26708375, 28171993, 29715818, 31344244, 33061908, 34873700, 36784778, 38800583, 40926854, 43169645, 45535341, 48030677, 50662758, 53439077, 56367538, 59456479, 62714694, 66151459, 69776558, 73600313, 77633610, 81887931, 86375389, 91108760, 96101520, 101367883, 106922842, 112782213, 118962678, 125481832, 132358236, 139611467, 147262175, 155332142, 163844343, 172823012, 182293713, 192283408, 202820538, 213935103, 225658746, 238024845, 251068606, 264827165, 279339693, 294647508, 310794191, 327825712, 345790561, 364739883, 384727628, 405810702, 428049128, 451506220, 476248760, 502347192, 529875818, 558913012, 589541445, 621848316, 655925603, 691870326, 729784819, 769777027, 811960808, 856456260, 903390063, 952895838, 1005114529, 1060194805, 1118293480, 1179575962, 1244216724, 1312399800, 1384319309, 1460180007, 1540197871, 1624600714, 1713628833, 1807535693, 1906588648, 2011069705, 2121276324 };
        closeness = new int[] { 0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000 };
        mountexp = new int[] { 0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247, 2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550 };
        itemBlock = new int[] { 2340000, 2049100, 4001129, 2040037, 2040006, 2040007, 2040303, 2040403, 2040506, 2040507, 2040603, 2040709, 2040710, 2040711, 2040806, 2040903, 2041024, 2041025, 2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044503, 2044603, 2044908, 2044815, 2044019, 2044703, 1004001, 4007008, 1004002, 5152053, 5150040 };
        cashBlock = new int[] { 5062000, 5650000, 5431000, 5431001, 5432000, 5450000, 5550000, 5550001, 5640000, 5530013, 5150039, 5150046, 5150054, 1812006, 5650000, 5222000, 5221001, 5220014, 5220015, 5420007, 5451000, 5210000, 5210001, 5210002, 5210003, 5210004, 5210005, 5210006, 5210007, 5210008, 5210009, 5210010, 5210011, 5211000, 5211001, 5211002, 5211003, 5211004, 5211005, 5211006, 5211007, 5211008, 5211009, 5211010, 5211011, 5211012, 5211013, 5211014, 5211015, 5211016, 5211017, 5211018, 5211019, 5211020, 5211021, 5211022, 5211023, 5211024, 5211025, 5211026, 5211027, 5211028, 5211029, 5211030, 5211031, 5211032, 5211033, 5211034, 5211035, 5211036, 5211037, 5211038, 5211039, 5211040, 5211041, 5211042, 5211043, 5211044, 5211045, 5211046, 5211047, 5211048, 5211049, 5211050, 5211051, 5211052, 5211053, 5211054, 5211055, 5211056, 5211057, 5211058, 5211059, 5211060, 5211061, 5360000, 5360001, 5360002, 5360003, 5360004, 5360005, 5360006, 5360007, 5360008, 5360009, 5360010, 5360011, 5360012, 5360013, 5360014, 5360017, 5360050, 5211050, 5360042, 5360052, 5360053, 5360050, 1112810, 1112811, 5530013, 4001431, 4001432, 4032605, 5270000, 5270001, 5270002, 5270003, 5270004, 5270005, 5270006, 9102328, 9102329, 9102330, 9102331, 9102332, 9102333 };
        blockedSkills = new int[] { 4341003 };
        RESERVED = new String[] { "Rental" };
        mobHpVal = new int[] { 0, 15, 20, 25, 35, 50, 65, 80, 95, 110, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 405, 435, 465, 495, 525, 580, 650, 720, 790, 900, 990, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2520, 2640, 2760, 2880, 3000, 3200, 3400, 3600, 3800, 4000, 4300, 4600, 4900, 5200, 5500, 5900, 6300, 6700, 7100, 7500, 8000, 8500, 9000, 9500, 10000, 11000, 12000, 13000, 14000, 15000, 17000, 19000, 21000, 23000, 25000, 27000, 29000, 31000, 33000, 35000, 37000, 39000, 41000, 43000, 45000, 47000, 49000, 51000, 53000, 55000, 57000, 59000, 61000, 63000, 65000, 67000, 69000, 71000, 73000, 75000, 77000, 79000, 81000, 83000, 85000, 89000, 91000, 93000, 95000, 97000, 99000, 101000, 103000, 105000, 107000, 109000, 111000, 113000, 115000, 118000, 120000, 125000, 130000, 135000, 140000, 145000, 150000, 155000, 160000, 165000, 170000, 175000, 180000, 185000, 190000, 195000, 200000, 205000, 210000, 215000, 220000, 225000, 230000, 235000, 240000, 250000, 260000, 270000, 280000, 290000, 300000, 310000, 320000, 330000, 340000, 350000, 360000, 370000, 380000, 390000, 400000, 410000, 420000, 430000, 440000, 450000, 460000, 470000, 480000, 490000, 500000, 510000, 520000, 530000, 550000, 570000, 590000, 610000, 630000, 650000, 670000, 690000, 710000, 730000, 750000, 770000, 790000, 810000, 830000, 850000, 870000, 890000, 910000 };
        GameConstants.单机IP = 1;
        GameConstants.绑定IP = "127.0.0.1";
        goldrewards = new int[] { 2340000, 1, 2070018, 1, 1402037, 1, 2290096, 1, 2290049, 1, 2290041, 1, 2290047, 1, 2290095, 1, 2290017, 1, 2290075, 1, 2290085, 1, 2290116, 1, 1302059, 3, 2049100, 1, 2340000, 1, 1092049, 1, 1102041, 1, 1432018, 3, 1022047, 3, 3010051, 1, 3010020, 1, 2040914, 1, 1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3, 1462018, 3, 1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1472051, 1, 1482013, 1, 1492013, 1, 1382050, 1, 1382045, 1, 1382047, 1, 1382048, 1, 1382046, 1, 1332032, 4, 1482025, 3, 4001011, 4, 4001010, 4, 4001009, 4, 2030008, 5, 1442018, 3, 2040900, 4, 2000005, 10, 2000004, 10, 4280000, 4 };
        silverrewards = new int[] { 3010041, 1, 1002452, 3, 1002455, 3, 2290084, 1, 2290048, 1, 2290040, 1, 2290046, 1, 2290074, 1, 2290064, 1, 2290094, 1, 2290022, 1, 2290056, 1, 2290066, 1, 2290020, 1, 1102082, 1, 1302049, 1, 2340000, 1, 1102041, 1, 1452019, 2, 4001116, 3, 4001012, 3, 1022060, 2, 1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3, 1462018, 3, 1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1002587, 3, 1402044, 1, 2101013, 4, 1442046, 1, 1422031, 1, 1332054, 3, 1012056, 3, 1022047, 3, 3012002, 1, 1442012, 3, 1442018, 3, 1432010, 3, 1432036, 1, 2000005, 10, 2000004, 10, 4280001, 4 };
        GameConstants.eventCommonReward = new int[] { 0, 40, 1, 10, 4031019, 5, 4280000, 3, 4280001, 4, 5490000, 3, 5490001, 4 };
        GameConstants.eventUncommonReward = new int[] { 2, 4, 3, 4, 5160000, 5, 5160001, 5, 5160002, 5, 5160003, 5, 5160004, 5, 5160005, 5, 5160006, 5, 5160007, 5, 5160008, 5, 5160009, 5, 5160010, 5, 5160011, 5, 5160012, 5, 5160013, 5, 5240017, 5, 5240000, 5, 4080000, 5, 4080001, 5, 4080002, 5, 4080003, 5, 4080004, 5, 4080005, 5, 4080006, 5, 4080007, 5, 4080008, 5, 4080009, 5, 4080010, 5, 4080011, 5, 4080100, 5, 4031019, 5, 5121003, 5, 5150000, 5, 5150001, 5, 5150002, 1, 5150003, 1, 5150004, 1, 5150005, 2, 5150006, 2, 5150007, 2, 5150008, 2, 5150009, 14, 2022459, 5, 2022460, 5, 2022461, 5, 2022462, 5, 2022463, 5, 2450000, 2, 5152000, 5, 5152001, 5 };
        GameConstants.eventRareReward = new int[] { 4031019, 5, 2049100, 5, 2049401, 10, 2049301, 20, 2049400, 3, 2340000, 1, 3010130, 5, 3010131, 5, 3010132, 5, 3010133, 5, 3010136, 5, 3010116, 5, 3010117, 5, 3010118, 5, 1112405, 1, 1112413, 1, 1112414, 1, 2040211, 1, 2040212, 1, 2049000, 2, 2049001, 2, 2049002, 2, 2049003, 2, 1012058, 2, 1012059, 2, 1012060, 2, 1012061, 2 };
        GameConstants.eventSuperReward = new int[] { 4031019, 5, 2022121, 10, 4031307, 50, 3010127, 10, 3010128, 10, 3010137, 10, 2049300, 10, 1012139, 10, 1012140, 10, 1012141, 10 };
        GameConstants.fishingReward = new int[] { 0, 90, 1, 70, 4031627, 2, 4031628, 1, 4031630, 1, 4031631, 1, 4031632, 1, 4031633, 2, 4031634, 1, 4031635, 1, 4031636, 1, 4031637, 2, 4031638, 2, 4031639, 1, 4031640, 1, 4031641, 2, 4031642, 2, 4031643, 1, 4031644, 1, 4031645, 2, 4031646, 2, 4031647, 1, 4031648, 1, 4031629, 1 };
        GameConstants.Equipments_Bonus = new int[] { 1122017 };
        GameConstants.blockedMaps = new int[] { 109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010 };
        normalDrops = new int[] { 4001009, 4001010, 4001011, 4001012, 4001013, 4001014, 4001021, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007 };
        rareDrops = new int[] { 2049100, 2049301, 2049401, 2022326, 2022193, 2049000, 2049001, 2049002 };
        superDrops = new int[] { 2040804, 2049400, 2049100 };
        GameConstants.owlItems = new int[] { 1082002, 2070005, 2070006, 1022047, 1102041, 2044705, 2340000, 2040017, 1092030, 2040804 };
    }
}
