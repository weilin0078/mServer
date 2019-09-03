package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import server.MapleStatEffect;
import tools.MaplePacketCreator;

public class MapleSummon extends AbstractAnimatedMapleMapObject
{
    private final int ownerid;
    private final int skillLevel;
    private final int ownerLevel;
    private final int skill;
    private int fh;
    private MapleMap map;
    private short hp;
    private boolean changedMap;
    private SummonMovementType movementType;
    private int lastSummonTickCount;
    private byte Summon_tickResetCount;
    private long Server_ClientSummonTickDiff;
    
    public MapleSummon(final MapleCharacter owner, final MapleStatEffect skill, final Point pos, final SummonMovementType movementType) {
        this.changedMap = false;
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = skill.getSourceId();
        this.map = owner.getMap();
        this.skillLevel = skill.getLevel();
        this.movementType = movementType;
        this.setPosition(pos);
        try {
            this.fh = owner.getMap().getFootholds().findBelow(pos).getId();
        }
        catch (NullPointerException e) {
            this.fh = 0;
        }
        if (!this.isPuppet()) {
            this.lastSummonTickCount = 0;
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = 0L;
        }
    }
    
    @Override
    public final void sendSpawnData(final MapleClient client) {
    }
    
    @Override
    public final void sendDestroyData(final MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeSummon(this, false));
    }
    
    public final void updateMap(final MapleMap map) {
        this.map = map;
    }
    
    public final MapleCharacter getOwner() {
        return this.map.getCharacterById(this.ownerid);
    }
    
    public final int getFh() {
        return this.fh;
    }
    
    public final void setFh(final int fh) {
        this.fh = fh;
    }
    
    public final int getOwnerId() {
        return this.ownerid;
    }
    
    public final int getOwnerLevel() {
        return this.ownerLevel;
    }
    
    public final int getSkill() {
        return this.skill;
    }
    
    public final short getHP() {
        return this.hp;
    }
    
    public final void addHP(final short delta) {
        this.hp += delta;
    }
    
    public final SummonMovementType getMovementType() {
        return this.movementType;
    }
    
    public final boolean isPuppet() {
        switch (this.skill) {
            case 3111002:
            case 3211002:
            case 4341006:
            case 13111004:
            case 33111003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isGaviota() {
        return this.skill == 5211002;
    }
    
    public final boolean isBeholder() {
        return this.skill == 1321007;
    }
    
    public final boolean isMultiSummon() {
        return this.skill == 5211002 || this.skill == 5211001 || this.skill == 5220002 || this.skill == 32111006;
    }
    
    public final boolean isSummon() {
        switch (this.skill) {
            case 1321007:
            case 2121005:
            case 2221005:
            case 2311006:
            case 2321003:
            case 5211001:
            case 5211002:
            case 5220002:
            case 11001004:
            case 12001004:
            case 12111004:
            case 13001004:
            case 13111004:
            case 14001005:
            case 15001004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public final int getSkillLevel() {
        return this.skillLevel;
    }
    
    public final int getSummonType() {
        if (this.isPuppet()) {
            return 0;
        }
        switch (this.skill) {
            case 1321007: {
                return 2;
            }
            case 35111001:
            case 35111009:
            case 35111010: {
                return 3;
            }
            case 35121009: {
                return 4;
            }
            default: {
                return 1;
            }
        }
    }
    
    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }
    
    public final void CheckSummonAttackFrequency(final MapleCharacter chr, final int tickcount) {
        final int tickdifference = tickcount - this.lastSummonTickCount;
        if (tickdifference < GameConstants.getSummonAttackDelay(this.skill)) {
            chr.getCheatTracker().registerOffense(CheatingOffense.\u53ec\u5524\u517d\u5feb\u901f\u653b\u51fb);
        }
        final long STime_TC = System.currentTimeMillis() - tickcount;
        final long S_C_Difference = this.Server_ClientSummonTickDiff - STime_TC;
        if (S_C_Difference > 200L) {
            chr.getCheatTracker().registerOffense(CheatingOffense.\u53ec\u5524\u517d\u5feb\u901f\u653b\u51fb);
        }
        ++this.Summon_tickResetCount;
        if (this.Summon_tickResetCount > 4) {
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = STime_TC;
        }
        this.lastSummonTickCount = tickcount;
    }
    
    public final boolean isChangedMap() {
        return this.changedMap;
    }
    
    public final void setChangedMap(final boolean cm) {
        this.changedMap = cm;
    }
}
