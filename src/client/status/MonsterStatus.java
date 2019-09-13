package client.status;

import java.io.Serializable;

public enum MonsterStatus implements Serializable
{
    NEUTRALISE(2L), 
    WATK(4294967296L), 
    WDEF(8589934592L), 
    MATK(17179869184L), 
    MDEF(34359738368L), 
    ACC(68719476736L), 
    AVOID(137438953472L), 
    SPEED(274877906944L), 
    STUN(549755813888L), 
    FREEZE(1099511627776L), 
    POISON(2199023255552L), 
    SEAL(4398046511104L), 
    SHOWDOWN(8796093022208L), 
    WEAPON_ATTACK_UP(17592186044416L), 
    WEAPON_DEFENSE_UP(35184372088832L), 
    MAGIC_ATTACK_UP(70368744177664L), 
    MAGIC_DEFENSE_UP(140737488355328L), 
    DOOM(281474976710656L), 
    SHADOW_WEB(562949953421312L), 
    WEAPON_IMMUNITY(1125899906842624L), 
    MAGIC_IMMUNITY(2251799813685248L), 
    DAMAGE_IMMUNITY(9007199254740992L), 
    NINJA_AMBUSH(18014398509481984L), 
    VENOMOUS_WEAPON(72057594037927936L), 
    DARKNESS(144115188075855872L), 
    EMPTY(576460752303423488L), 
    HYPNOTIZE(1152921504606846976L), 
    WEAPON_DAMAGE_REFLECT(2305843009213693952L), 
    MAGIC_DAMAGE_REFLECT(4611686018427387904L), 
    SUMMON(Long.MIN_VALUE);
    
    static final long serialVersionUID = 0L;
    private final long i;
    private final boolean first;
    
    private MonsterStatus(final long i) {
        this.i = i;
        this.first = false;
    }
    
    private MonsterStatus(final int i, final boolean first) {
        this.i = i;
        this.first = first;
    }
    
    public boolean isFirst() {
        return this.first;
    }
    
    public boolean isEmpty() {
        return this == MonsterStatus.SUMMON || this == MonsterStatus.EMPTY;
    }
    
    public long getValue() {
        return this.i;
    }
}
