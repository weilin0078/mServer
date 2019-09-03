package handling.channel.handler;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.anticheat.CheatingOffense;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import server.MapleStatEffect;
import server.Timer;
import server.life.MapleMonster;
import server.life.SummonAttackEntry;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MobPacket;

public class SummonHandler
{
    public static final void MoveDragon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        slea.skip(8);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 5);
        if (chr != null && chr.getDragon() != null) {
            final Point pos = chr.getDragon().getPosition();
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveDragon(chr.getDragon(), pos, res), chr.getPosition());
            }
            final WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() != null) {
                    final MapleMap map = chr.getMap();
                    final MapleCharacter clone = clones[i].get();
                    final List<LifeMovementFragment> res2 = new ArrayList<LifeMovementFragment>(res);
                    Timer.CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map && clone.getDragon() != null) {
                                    final Point startPos = clone.getDragon().getPosition();
                                    MovementParse.updatePosition(res2, clone.getDragon(), 0);
                                    if (!clone.isHidden()) {
                                        map.broadcastMessage(clone, MaplePacketCreator.moveDragon(clone.getDragon(), startPos, res2), clone.getPosition());
                                    }
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }
    
    public static final void MoveSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int oid = slea.readInt();
        final Point startPos = new Point(slea.readShort(), slea.readShort());
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 4);
        if (chr == null) {
            return;
        }
        for (final MapleSummon sum : chr.getSummons().values()) {
            if (sum.getObjectId() == oid && sum.getMovementType() != SummonMovementType.STATIONARY) {
                final Point pos = sum.getPosition();
                MovementParse.updatePosition(res, sum, 0);
                if (!sum.isChangedMap()) {
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveSummon(chr.getId(), oid, startPos, res), sum.getPosition());
                    break;
                }
                break;
            }
        }
    }
    
    public static final void DamageSummon(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int skillid = slea.readInt();
        final int unkByte = slea.readByte();
        final int damage = slea.readInt();
        final int monsterIdFrom = slea.readInt();
        final Iterator<MapleSummon> iter = chr.getSummons().values().iterator();
        if (SkillFactory.getSkill(skillid) != null) {
            while (iter.hasNext()) {
                final MapleSummon summon = iter.next();
                if (summon.isPuppet() && summon.getOwnerId() == chr.getId()) {
                    summon.addHP((short)(-damage));
                    if (summon.getHP() <= 0) {
                        chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
                    }
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.damageSummon(chr.getId(), skillid, damage, unkByte, monsterIdFrom), summon.getPosition());
                    break;
                }
            }
        }
    }
    
    public static void SummonAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive()) {
            return;
        }
        final MapleMap map = chr.getMap();
        final MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        final MapleSummon summon = (MapleSummon)obj;
        if (summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0) {
            return;
        }
        final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if (sse == null) {
            return;
        }
        slea.skip(8);
        final int tick = slea.readInt();
        chr.updateTick(tick);
        summon.CheckSummonAttackFrequency(chr, tick);
        slea.skip(8);
        final byte animation = slea.readByte();
        slea.skip(8);
        final byte numAttacked = slea.readByte();
        if (numAttacked > sse.mobCount) {
            chr.getCheatTracker().registerOffense(CheatingOffense.\u53ec\u5524\u517d\u653b\u51fb\u602a\u7269\u6570\u91cf\u5f02\u5e38);
            return;
        }
        final List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
        chr.getCheatTracker().checkSummonAttack();
        for (int i = 0; i < numAttacked; ++i) {
            final MapleMonster mob = map.getMonsterByOid(slea.readInt());
            if (mob != null) {
                if (chr.getPosition().distanceSq(mob.getPosition()) > 400000.0) {
                    chr.getCheatTracker().registerOffense(CheatingOffense.\u53ec\u5524\u517d\u653b\u51fb\u8303\u56f4\u8fc7\u5927);
                }
                slea.skip(14);
                final int damage = slea.readInt();
                allDamage.add(new SummonAttackEntry(mob, damage));
            }
        }
        if (!summon.isChangedMap()) {}
        final ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
        final MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        if (summonEffect == null) {
            return;
        }
        for (final SummonAttackEntry attackEntry : allDamage) {
            final int toDamage = attackEntry.getDamage();
            final MapleMonster mob2 = attackEntry.getMonster();
            if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0 && summonEffect.makeChanceResult()) {
                for (final Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                    mob2.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000L, false);
                }
            }
            if (chr.isGM() || toDamage < 120000) {
                mob2.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob2);
                if (mob2.isAlive()) {
                    continue;
                }
                chr.getClient().getSession().write((Object)MobPacket.killMonster(mob2.getObjectId(), 1));
            }
        }
        if (summon.isGaviota()) {
            chr.getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
        }
    }
}
