package client.status;

import java.util.concurrent.ScheduledFuture;

import server.life.MobSkill;

public class MonsterStatusEffect
{
    private MonsterStatus stati;
    private final int skill;
    private final MobSkill mobskill;
    private final boolean monsterSkill;
    private Integer x;
    private ScheduledFuture<?> cancelTask;
    private ScheduledFuture<?> poisonSchedule;
    
    public MonsterStatusEffect(final MonsterStatus stat, final Integer x, final int skillId, final MobSkill mobskill, final boolean monsterSkill) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
    }
    
    public final MonsterStatus getStati() {
        return this.stati;
    }
    
    public final Integer getX() {
        return this.x;
    }
    
    public final void setValue(final MonsterStatus status, final Integer newVal) {
        this.stati = status;
        this.x = newVal;
    }
    
    public final int getSkill() {
        return this.skill;
    }
    
    public final MobSkill getMobSkill() {
        return this.mobskill;
    }
    
    public final boolean isMonsterSkill() {
        return this.monsterSkill;
    }
    
    public final void setCancelTask(final ScheduledFuture<?> cancelTask) {
        this.cancelTask = cancelTask;
    }
    
    public final void setPoisonSchedule(final ScheduledFuture<?> poisonSchedule) {
        this.poisonSchedule = poisonSchedule;
    }
    
    public final void cancelTask() {
        if (this.cancelTask != null) {
            this.cancelTask.cancel(false);
        }
        this.cancelTask = null;
    }
    
    public final void cancelPoisonSchedule() {
        if (this.poisonSchedule != null) {
            try {
                this.poisonSchedule.cancel(false);
            }
            catch (NullPointerException ex) {}
        }
        this.poisonSchedule = null;
    }
}
