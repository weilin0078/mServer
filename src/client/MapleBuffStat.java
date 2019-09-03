package client;

import java.io.Serializable;

public enum MapleBuffStat implements Serializable
{
    ENHANCED_WDEF(1L, true), 
    ENHANCED_MDEF(2L, true), 
    PERFECT_ARMOR(4L, true), 
    SATELLITESAFE_PROC(8L, true), 
    SATELLITESAFE_ABSORB(16L, true), 
    CRITICAL_RATE_BUFF(64L, true), 
    MP_BUFF(128L, true), 
    DAMAGE_TAKEN_BUFF(256L, true), 
    DODGE_CHANGE_BUFF(512L, true), 
    CONVERSION(1024L, true), 
    REAPER(2048L, true), 
    MECH_CHANGE(8192L, true), 
    DARK_AURA(32768L, true), 
    BLUE_AURA(65536L, true), 
    YELLOW_AURA(131072L, true), 
    ENERGY_CHARGE(137438953472L, true), 
    DASH_SPEED(549755813888L), 
    DASH_JUMP(274877906944L), 
    MONSTER_RIDING(1099511627776L, true), 
    SPEED_INFUSION(140737488355328L, true), 
    HOMING_BEACON(8796093022208L, true), 
    ELEMENT_RESET(144115188075855872L, true), 
    ARAN_COMBO(1152921504606846976L, true), 
    COMBO_DRAIN(2305843009213693952L, true), 
    COMBO_BARRIER(4611686018427387904L, true), 
    BODY_PRESSURE(17592186044416L, true), 
    SMART_KNOCKBACK(1L, false), 
    PYRAMID_PQ(2L, false), 
    LIGHTNING_CHARGE(4L, false), 
    SOUL_STONE(2199023255552L, true), 
    MAGIC_SHIELD(140737488355328L, true), 
    MAGIC_RESISTANCE(281474976710656L, true), 
    SOARING(1125899906842624L, true), 
    MIRROR_IMAGE(9007199254740992L, true), 
    OWL_SPIRIT(18014398509481984L, true), 
    FINAL_CUT(72057594037927936L, true), 
    THORNS(144115188075855872L, true), 
    DAMAGE_BUFF(288230376151711744L, true), 
    RAINING_MINES(1152921504606846976L, true), 
    ENHANCED_MAXHP(2305843009213693952L, true), 
    ENHANCED_MAXMP(4611686018427387904L, true), 
    ENHANCED_WATK(Long.MIN_VALUE, true), 
    MORPH(2L), 
    RECOVERY(4L), 
    MAPLE_WARRIOR(8L), 
    STANCE(16L), 
    SHARP_EYES(32L), 
    MANA_REFLECTION(64L), 
    DRAGON_ROAR(128L), 
    SPIRIT_CLAW(256L), 
    INFINITY(512L), 
    HOLY_SHIELD(1024L), 
    HAMSTRING(2048L), 
    BLIND(4096L), 
    CONCENTRATE(8192L), 
    ECHO_OF_HERO(32768L), 
    UNKNOWN3(65536L), 
    GHOST_MORPH(131072L), 
    ARIANT_COSS_IMU(262144L), 
    DROP_RATE(1048576L), 
    MESO_RATE(2097152L), 
    EXPRATE(4194304L), 
    ACASH_RATE(8388608L), 
    GM_HIDE(16777216L), 
    UNKNOWN7(33554432L), 
    ILLUSION(67108864L), 
    BERSERK_FURY(134217728L), 
    DIVINE_BODY(268435456L), 
    SPARK(536870912L), 
    ARIANT_COSS_IMU2(1073741824L), 
    FINALATTACK(2147483648L), 
    WATK(4294967296L), 
    WDEF(8589934592L), 
    MATK(17179869184L), 
    MDEF(34359738368L), 
    ACC(68719476736L), 
    AVOID(137438953472L), 
    HANDS(274877906944L), 
    SPEED(549755813888L), 
    JUMP(1099511627776L), 
    MAGIC_GUARD(2199023255552L), 
    DARKSIGHT(4398046511104L), 
    BOOSTER(8796093022208L), 
    POWERGUARD(17592186044416L), 
    MAXHP(35184372088832L), 
    MAXMP(70368744177664L), 
    INVINCIBLE(140737488355328L), 
    SOULARROW(281474976710656L), 
    COMBO(9007199254740992L), 
    SUMMON(9007199254740992L), 
    WK_CHARGE(18014398509481984L), 
    DRAGONBLOOD(36028797018963968L), 
    HOLY_SYMBOL(72057594037927936L), 
    MESOUP(144115188075855872L), 
    SHADOWPARTNER(288230376151711744L), 
    PICKPOCKET(576460752303423488L), 
    PUPPET(576460752303423488L), 
    \u80fd\u91cf(137438953472L, true), 
    \u80fd\u91cf\u83b7\u53d6(137438953472L, true), 
    \u9a91\u5ba0\u6280\u80fd(1099511627776L), 
    MESOGUARD(1152921504606846976L), 
    \u77db\u8fde\u51fb\u5f3a\u5316(4294967296L, 3L), 
    \u77db\u8fde\u51fb\u5f3a\u53162(8589934592L, true), 
    \u77db\u8fde\u51fb\u5f3a\u5316\u9632\u5fa1(8589934592L, true), 
    \u77db\u8fde\u51fb\u5f3a\u5316\u9b54\u6cd5\u9632\u5fa1(34359738368L, true), 
    \u8fde\u73af\u5438\u8840(18014398509481984L), 
    \u7075\u5de7\u51fb\u9000(140737488355328L), 
    \u6218\u795e\u4e4b\u76fe(140737488355328L), 
    WEAKEN(4611686018427387904L);
    
    private static final long serialVersionUID = 0L;
    private final long buffstat;
    private final long maskPos;
    private final boolean first;
    
    private MapleBuffStat(final long buffstat) {
        this.buffstat = buffstat;
        this.maskPos = 4L;
        this.first = false;
    }
    
    private MapleBuffStat(final long buffstat, final long maskPos) {
        this.buffstat = buffstat;
        this.maskPos = maskPos;
        this.first = false;
    }
    
    private MapleBuffStat(final long buffstat, final boolean first) {
        this.buffstat = buffstat;
        this.maskPos = 4L;
        this.first = first;
    }
    
    public long getMaskPos() {
        return this.maskPos;
    }
    
    public final boolean isFirst() {
        return this.first;
    }
    
    public final long getValue() {
        return this.buffstat;
    }
}
