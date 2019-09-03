package handling.channel.handler;

import java.awt.Point;
import java.util.List;

import client.ISkill;
import client.MapleCharacter;
import client.SkillFactory;
import constants.GameConstants;
import server.AutobanManager;
import server.MapleStatEffect;
import tools.AttackPair;

public class AttackInfo
{
    public int skill;
    public int charge;
    public int lastAttackTickCount;
    public List<AttackPair> allDamage;
    public Point position;
    public byte hits;
    public byte targets;
    public byte tbyte;
    public byte display;
    public byte animation;
    public byte speed;
    public byte csstar;
    public byte AOE;
    public byte slot;
    public byte unk;
    public boolean real;
    
    public AttackInfo() {
        this.real = true;
    }
    
    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final ISkill skill_) {
        if (GameConstants.isMulungSkill(this.skill) || GameConstants.isPyramidSkill(this.skill)) {
            skillLevel = 1;
        }
        else if (skillLevel <= 0) {
            return null;
        }
        if (GameConstants.isLinkedAranSkill(this.skill)) {
            final ISkill skillLink = SkillFactory.getSkill(this.skill);
            if (this.display > 80 && !skillLink.getAction()) {
                AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + this.skill);
                return null;
            }
            return skillLink.getEffect(skillLevel);
        }
        else {
            if (this.display > 80 && !skill_.getAction()) {
                AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + this.skill);
                return null;
            }
            return skill_.getEffect(skillLevel);
        }
    }
}
