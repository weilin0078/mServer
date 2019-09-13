package tools.packet;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.movement.LifeMovementFragment;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;

public class MobPacket
{
    public static MaplePacket damageMonster(final int oid, final long damage) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("damageMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        if (damage > 2147483647L) {
            mplew.writeInt(Integer.MAX_VALUE);
        }
        else {
            mplew.writeInt((int)damage);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket damageFriendlyMob(final MapleMonster mob, final long damage, final boolean display) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("damageFriendlyMob--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2);
        mplew.writeInt((damage > 2147483647L) ? Integer.MAX_VALUE : ((int)damage));
        mplew.writeInt((mob.getHp() > 2147483647L) ? ((int)(mob.getHp() / mob.getMobMaxHp() * 2.147483647E9)) : ((int)mob.getHp()));
        mplew.writeInt((mob.getMobMaxHp() > 2147483647L) ? Integer.MAX_VALUE : ((int)mob.getMobMaxHp()));
        return mplew.getPacket();
    }
    
    public static MaplePacket killMonster(final int oid, final int animation) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("killMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation);
        if (animation == 4) {
            mplew.writeInt(-1);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket healMonster(final int oid, final int heal) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("healMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);
        return mplew.getPacket();
    }
    
    public static MaplePacket showMonsterHP(final int oid, final int remhppercentage) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showMonsterHP--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);
        return mplew.getPacket();
    }
    
    public static MaplePacket showBossHP(final MapleMonster mob) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showBossHPA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(mob.getId());
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int)(mob.getHp() / (double)mob.getMobMaxHp() * 2.147483647E9));
        }
        else {
            mplew.writeInt((int)mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(Integer.MAX_VALUE);
        }
        else {
            mplew.writeInt((int)mob.getMobMaxHp());
        }
        mplew.write(mob.getStats().getTagColor());
        mplew.write(mob.getStats().getTagBgColor());
        return mplew.getPacket();
    }
    
    public static MaplePacket showBossHP(final int monsterId, final long currentHp, final long maxHp) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showBossHPB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId);
        if (currentHp > 2147483647L) {
            mplew.writeInt((int)(currentHp / (double)maxHp * 2.147483647E9));
        }
        else {
            mplew.writeInt((int)((currentHp <= 0L) ? -1L : currentHp));
        }
        if (maxHp > 2147483647L) {
            mplew.writeInt(Integer.MAX_VALUE);
        }
        else {
            mplew.writeInt((int)maxHp);
        }
        mplew.write(6);
        mplew.write(5);
        return mplew.getPacket();
    }
    
    public static MaplePacket moveMonster(final boolean useskill, final int skill, final int skill1, final int skill2, final int skill3, final int skill4, final int oid, final Point startPos, final Point endPos, final List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("moveMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.write(useskill ? 1 : 0);
        mplew.write(skill);
        mplew.write(skill1);
        mplew.write(skill2);
        mplew.write(skill3);
        mplew.write(skill4);
        mplew.writePos(startPos);
        serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }
    
    private static void serializeMovementList(final LittleEndianWriter lew, final List<LifeMovementFragment> moves) {
        if (ServerConstants.调试输出封包) {
            System.out.println("serializeMovementList--------------------");
        }
        lew.write(moves.size());
        for (final LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }
    
    public static MaplePacket spawnMonster(final MapleMonster life, final int spawnType, final int effect, final int link) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("spawnMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        if (effect != 0 || link != 0) {
            mplew.write((effect != 0) ? effect : -3);
            mplew.writeInt(link);
        }
        else {
            if (spawnType == 0) {
                mplew.writeInt(effect);
            }
            mplew.write(spawnType);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    public static void addMonsterStatus(final MaplePacketLittleEndianWriter mplew, final MapleMonster life) {
        if (ServerConstants.调试输出封包) {
            System.out.println("addMonsterStatus--------------------");
        }
        if (life.getStati().size() <= 0) {
            life.addEmpty();
        }
        mplew.writeLong(getSpecialLongMask(life.getStati().keySet()));
        mplew.writeLong(getLongMask_NoRef(life.getStati().keySet()));
        boolean ignore_imm = false;
        for (final MonsterStatusEffect buff : life.getStati().values()) {
            if (buff.getStati() == MonsterStatus.MAGIC_DAMAGE_REFLECT || buff.getStati() == MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                ignore_imm = true;
                break;
            }
        }
        for (final MonsterStatusEffect buff : life.getStati().values()) {
            if (buff.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT && buff.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                if (ignore_imm) {
                    if (buff.getStati() == MonsterStatus.MAGIC_IMMUNITY) {
                        continue;
                    }
                    if (buff.getStati() == MonsterStatus.WEAPON_IMMUNITY) {
                        continue;
                    }
                }
                mplew.writeShort(buff.getX().shortValue());
                if (buff.getStati() == MonsterStatus.SUMMON) {
                    continue;
                }
                if (buff.getMobSkill() != null) {
                    mplew.writeShort(buff.getMobSkill().getSkillId());
                    mplew.writeShort(buff.getMobSkill().getSkillLevel());
                }
                else if (buff.getSkill() > 0) {
                    mplew.writeInt(buff.getSkill());
                }
                mplew.writeShort(buff.getStati().isEmpty() ? 0 : 1);
            }
        }
    }
    
    public static MaplePacket controlMonster(final MapleMonster life, final boolean newSpawn, final boolean aggro) {
        return spawnMonsterInternal(life, true, newSpawn, aggro, 0, false);
    }
    
    private static MaplePacket spawnMonsterInternal(final MapleMonster life, final boolean requestController, final boolean newSpawn, final boolean aggro, final int effect, final boolean makeInvis) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (makeInvis) {
            mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
            mplew.write(0);
            mplew.writeInt(life.getObjectId());
            return mplew.getPacket();
        }
        if (requestController) {
            mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
            if (aggro) {
                mplew.write(2);
            }
            else {
                mplew.write(1);
            }
        }
        else {
            mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        }
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeLong(0L);
        mplew.writeInt(0);
        mplew.write(136);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        if (effect > 0) {
            mplew.write(effect);
            mplew.write(0);
            mplew.writeShort(0);
            if (effect == 15) {
                mplew.write(0);
            }
        }
        if (newSpawn) {
            mplew.write(-2);
        }
        else {
            mplew.write(-1);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket stopControllingMonster(final int oid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("stopControllingMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);
        return mplew.getPacket();
    }
    
    public static MaplePacket makeMonsterInvisible(final MapleMonster life) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("makeMonsterInvisible--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(life.getObjectId());
        return mplew.getPacket();
    }
    
    public static MaplePacket makeMonsterReal(final MapleMonster life) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("makeMonsterReal--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.writeShort(-1);
        mplew.writeInt(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket moveMonsterResponse(final int objectid, final short moveid, final int currentMp, final boolean useSkills, final int skillId, final int skillLevel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("moveMonsterResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);
        return mplew.getPacket();
    }
    
    private static long getSpecialLongMask(final Collection<MonsterStatus> statups) {
        if (ServerConstants.调试输出封包) {
            System.out.println("getSpecialLongMask--------------------");
        }
        long mask = 0L;
        for (final MonsterStatus statup : statups) {
            if (statup.isFirst()) {
                mask |= statup.getValue();
            }
        }
        return mask;
    }
    
    private static long getLongMask(final Collection<MonsterStatus> statups) {
        if (ServerConstants.调试输出封包) {
            System.out.println("getLongMask--------------------");
        }
        long mask = 0L;
        for (final MonsterStatus statup : statups) {
            if (!statup.isFirst()) {
                mask |= statup.getValue();
            }
        }
        return mask;
    }
    
    private static long getLongMask_NoRef(final Collection<MonsterStatus> statups) {
        if (ServerConstants.调试输出封包) {
            System.out.println("getLongMask_NoRef--------------------");
        }
        long mask = 0L;
        boolean ignore_imm = false;
        for (final MonsterStatus statup : statups) {
            if (statup == MonsterStatus.MAGIC_DAMAGE_REFLECT || statup == MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                ignore_imm = true;
                break;
            }
        }
        for (final MonsterStatus statup : statups) {
            if (statup != MonsterStatus.MAGIC_DAMAGE_REFLECT && statup != MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                if (ignore_imm) {
                    if (statup == MonsterStatus.MAGIC_IMMUNITY) {
                        continue;
                    }
                    if (statup == MonsterStatus.WEAPON_IMMUNITY) {
                        continue;
                    }
                }
                if (statup.isFirst()) {
                    continue;
                }
                mask |= statup.getValue();
            }
        }
        return mask;
    }
    
    public static MaplePacket applyMonsterStatus(final int oid, final MonsterStatus mse, final int x, final MobSkill skil) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("applyMonsterStatus--------------------");
        }
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeLong(getSpecialLongMask(Collections.singletonList(mse)));
        mplew.writeLong(getLongMask(Collections.singletonList(mse)));
        mplew.writeShort(x);
        mplew.writeShort(skil.getSkillId());
        mplew.writeShort(skil.getSkillLevel());
        mplew.writeShort(mse.isEmpty() ? 1 : 0);
        mplew.writeShort(0);
        mplew.write(2);
        return mplew.getPacket();
    }
    
    public static MaplePacket applyMonsterStatus(final int oid, final MonsterStatusEffect mse) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("applyMonsterStatusA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeLong(getSpecialLongMask(Collections.singletonList(mse.getStati())));
        mplew.writeLong(getLongMask(Collections.singletonList(mse.getStati())));
        mplew.writeShort(mse.getX());
        if (mse.isMonsterSkill()) {
            mplew.writeShort(mse.getMobSkill().getSkillId());
            mplew.writeShort(mse.getMobSkill().getSkillLevel());
        }
        else if (mse.getSkill() > 0) {
            mplew.writeInt(mse.getSkill());
        }
        mplew.writeShort(mse.getStati().isEmpty() ? 1 : 0);
        mplew.writeShort(0);
        mplew.write(2);
        return mplew.getPacket();
    }
    
    public static MaplePacket applyMonsterStatus(final int oid, final Map<MonsterStatus, Integer> stati, final List<Integer> reflection, final MobSkill skil) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("applyMonsterStatusB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeLong(getSpecialLongMask(stati.keySet()));
        mplew.writeLong(getLongMask(stati.keySet()));
        for (final Map.Entry<MonsterStatus, Integer> mse : stati.entrySet()) {
            mplew.writeShort(mse.getValue());
            mplew.writeShort(skil.getSkillId());
            mplew.writeShort(skil.getSkillLevel());
            mplew.writeShort(mse.getKey().isEmpty() ? 1 : 0);
        }
        for (final Integer ref : reflection) {
            mplew.writeInt(ref);
        }
        mplew.writeInt(0);
        mplew.writeShort(0);
        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        mplew.write(size);
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelMonsterStatus(final int oid, final MonsterStatus stat) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("cancelMonsterStatus--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeLong(getSpecialLongMask(Collections.singletonList(stat)));
        mplew.writeLong(getLongMask(Collections.singletonList(stat)));
        mplew.write(3);
        return mplew.getPacket();
    }
    
    public static MaplePacket talkMonster(final int oid, final int itemId, final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("talkMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(500);
        mplew.writeInt(itemId);
        mplew.write((itemId > 0) ? 1 : 0);
        mplew.write((msg != null && msg.length() > 0) ? 1 : 0);
        if (msg != null && msg.length() > 0) {
            mplew.writeMapleAsciiString(msg);
        }
        mplew.writeInt(1);
        return mplew.getPacket();
    }
    
    public static MaplePacket removeTalkMonster(final int oid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("removeTalkMonster--------------------");
        }
        mplew.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        return mplew.getPacket();
    }
}
