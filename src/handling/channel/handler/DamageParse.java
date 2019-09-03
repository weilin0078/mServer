package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.PlayerStats;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.world.World;
import server.MapleStatEffect;
import server.Randomizer;
import server.ServerProperties;
import server.life.Element;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.LittleEndianAccessor;

public class DamageParse
{
    private static final int[] charges;
    public static MapleMonster pvpMob;
    
    public static void applyAttack(final AttackInfo attack, final ISkill theSkill, final MapleCharacter player, int attackCount, final double maxDamagePerMonster, final MapleStatEffect effect, final AttackType attack_type) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.\u4eba\u7269\u6b7b\u4ea1\u653b\u51fb);
            return;
        }
        if (attack.real) {}
        if (attack.skill != 0) {
            boolean ban = false;
            String lastReason = "";
            String reason = "";
            if (effect == null) {
                player.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            reason = Damage_MobCount(player, effect, attack);
            if (!"null".equals(reason)) {
                lastReason = reason;
                ban = true;
            }
            if (!ban) {
                reason = Damage_HighDamage(player, effect, attack);
            }
            if (!"null".equals(reason)) {
                lastReason = reason;
                ban = true;
            }
            if (ban && !player.isAdmin()) {
                player.ban(lastReason, true, true, false);
                FileoutputUtil.logToFile_chr(player, "Logs/Log_\u5c01\u53f7.rtf", lastReason);
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[\u5c01\u53f7\u7cfb\u7edf]" + player.getName() + " \u8be5\u73a9\u5bb6\u653b\u51fb\u5f02\u5e38\u88ab\u7cfb\u7edf\u81ea\u52a8\u5c01\u53f7\u5904\u7406\u3002").getBytes());
                return;
            }
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (player.getMapId() / 10000 != 92502) {
                    return;
                }
                player.mulung_EnergyModify(false);
            }
            if (GameConstants.isPyramidSkill(attack.skill)) {
                if (player.getMapId() / 1000000 != 926) {
                    return;
                }
                if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                    return;
                }
            }
        }
        int totDamage = 0;
        final MapleMap map = player.getMap();
        if (attack.skill == 4211006) {
            for (final AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    continue;
                }
                final MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);
                if (mapobject == null) {
                    player.getCheatTracker().registerOffense(CheatingOffense.\u91d1\u94b1\u70b8\u5f39_\u4e0d\u5b58\u5728\u9053\u5177);
                    return;
                }
                final MapleMapItem mapitem = (MapleMapItem)mapobject;
                mapitem.getLock().lock();
                try {
                    if (mapitem.getMeso() <= 0) {
                        player.getCheatTracker().registerOffense(CheatingOffense.\u5176\u4ed6\u5f02\u5e38);
                        return;
                    }
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    map.removeMapObject(mapitem);
                    map.broadcastMessage(MaplePacketCreator.explodeDrop(mapitem.getObjectId()));
                    mapitem.setPickedUp(true);
                }
                finally {
                    mapitem.getLock().unlock();
                }
            }
        }
        int totDamageToOneMonster = 0;
        long hpMob = 0L;
        final PlayerStats stats = player.getStat();
        final int CriticalDamage = stats.passive_sharpeye_percent();
        byte ShdowPartnerAttackPercentage = 0;
        if (attack_type == AttackType.RANGED_WITH_SHADOWPARTNER || attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
            MapleStatEffect shadowPartnerEffect;
            if (attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
                shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.MIRROR_IMAGE);
            }
            else {
                shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.SHADOWPARTNER);
            }
            if (shadowPartnerEffect != null) {
                if (attack.skill != 0 && attack_type != AttackType.NON_RANGED_WITH_MIRROR) {
                    ShdowPartnerAttackPercentage = (byte)shadowPartnerEffect.getY();
                }
                else {
                    ShdowPartnerAttackPercentage = (byte)shadowPartnerEffect.getX();
                }
            }
            attackCount /= 2;
        }
        double maxDamagePerHit = 0.0;
        for (final AttackPair oned2 : attack.allDamage) {
            final MapleMonster monster = map.getMonsterByOid(oned2.objectid);
            if (monster != null) {
                totDamageToOneMonster = 0;
                hpMob = monster.getHp();
                final MapleMonsterStats monsterstats = monster.getStats();
                final int fixeddmg = monsterstats.getFixedDamage();
                final boolean Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006;
                maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, CriticalDamage);
                byte overallAttackCount = 0;
                for (final Pair<Integer, Boolean> eachde : oned2.attack) {
                    Integer eachd = eachde.left;
                    ++overallAttackCount;
                    if (overallAttackCount - 1 == attackCount) {
                        maxDamagePerHit = maxDamagePerHit / 100.0 * ShdowPartnerAttackPercentage;
                    }
                    if (fixeddmg != -1) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = ((attack.skill != 0) ? 0 : fixeddmg);
                        }
                        else {
                            eachd = fixeddmg;
                        }
                    }
                    else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = ((attack.skill != 0) ? 0 : Math.min(eachd, (int)maxDamagePerHit));
                    }
                    else if (!player.isGM()) {
                        if (Tempest) {
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int)Math.min(monster.getMobMaxHp(), 2147483647L);
                                player.getCheatTracker().registerOffense(CheatingOffense.\u653b\u51fb\u529b\u8fc7\u9ad8);
                            }
                        }
                        else if (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                            if (eachd > maxDamagePerHit) {
                                player.getCheatTracker().registerOffense(CheatingOffense.\u653b\u51fb\u529b\u8fc7\u9ad8);
                                if (eachd > maxDamagePerHit * 2.0) {
                                    FileoutputUtil.logToFile_chr(player, "Logs/\u7269\u7406\u4f24\u5bb3\u4fee\u6b63.rtf", " \u6280\u80fd " + attack.skill + " \u602a\u7269 " + monster.getId() + " \u9884\u8ba1\u4f24\u5bb3:" + (long)maxDamagePerHit + "  \u5b9e\u9645" + eachd);
                                    eachd = (int)(maxDamagePerHit * 2.0);
                                    player.getCheatTracker().registerOffense(CheatingOffense.\u653b\u51fb\u8fc7\u9ad82);
                                }
                            }
                        }
                        else if (eachd > maxDamagePerHit * 2.0) {
                            FileoutputUtil.logToFile_chr(player, "Logs/\u7269\u7406\u4f24\u5bb3\u4fee\u6b63.rtf", " \u6280\u80fd " + attack.skill + " \u602a\u7269 " + monster.getId() + " \u9884\u8ba1\u4f24\u5bb3:" + (long)maxDamagePerHit + "  \u5b9e\u9645" + eachd);
                            eachd = (int)maxDamagePerHit;
                        }
                    }
                    if (player == null) {
                        return;
                    }
                    totDamageToOneMonster += eachd;
                    if (monster.getId() != 9300021 || player.getPyramidSubway() == null) {
                        continue;
                    }
                    player.getPyramidSubway().onMiss(player);
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (attack.skill == 2301002 && !monsterstats.getUndead()) {
                    player.ban("\u4fee\u6539WZ", true, true, false);
                    FileoutputUtil.logToFile_chr(player, "Logs/Log_\u5c01\u53f7.rtf", "\u4f7f\u7528\u7fa4\u4f53\u6cbb\u6108\u4f24\u5bb3\u602a\u7269 " + monster.getId());
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[\u5c01\u53f7\u7cfb\u7edf] " + player.getName() + " \u8be5\u73a9\u5bb6\u653b\u51fb\u5f02\u5e38\u88ab\u7cfb\u7edf\u81ea\u52a8\u5c01\u53f7\u5904\u7406\u3002"));
                    return;
                }
                final double Position_range = player.getPosition().distanceSq(monster.getPosition());
                final double Count_range = 700000.0;
                if (Position_range > Count_range) {
                    player.getCheatTracker().registerOffense(CheatingOffense.\u653b\u51fb\u8303\u56f4\u8fc7\u5927, " \u6280\u80fd " + attack.skill + " \u8303\u56f4 : " + (long)Position_range + "\u6b63\u5e38\u8303\u56f4 " + (long)Count_range);
                    return;
                }
                if (player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                    switch (attack.skill) {
                        case 0:
                        case 4001334:
                        case 4201005:
                        case 4211002:
                        case 4211004:
                        case 4221003:
                        case 4221007: {
                            handlePickPocket(player, monster, oned2);
                            break;
                        }
                    }
                }
                final MapleStatEffect ds = player.getStatForBuff(MapleBuffStat.DARKSIGHT);
                if (ds != null && !player.isGM() && (ds.getSourceId() != 4330001 || !ds.makeChanceResult())) {
                    player.cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
                }
                if (totDamageToOneMonster <= 0) {
                    continue;
                }
                if (attack.skill != 1221011) {
                    monster.damage(player, totDamageToOneMonster, true, attack.skill);
                }
                else {
                    monster.damage(player, monster.getStats().isBoss() ? 199999L : (monster.getHp() - 1L), true, attack.skill);
                }
                if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                    player.addHP(-(7000 + Randomizer.nextInt(8000)));
                }
                if (stats.hpRecoverProp > 0 && Randomizer.nextInt(100) <= stats.hpRecoverProp) {
                    player.healHP(stats.hpRecover);
                }
                if (stats.mpRecoverProp > 0 && Randomizer.nextInt(100) <= stats.mpRecoverProp) {
                    player.healMP(stats.mpRecover);
                }
                if (player.getBuffedValue(MapleBuffStat.COMBO_DRAIN) != null) {
                    stats.setHp(stats.getHp() + (int)Math.min(monster.getMobMaxHp(), Math.min((int)(totDamage * (double)player.getStatForBuff(MapleBuffStat.COMBO_DRAIN).getX() / 100.0), stats.getMaxHp() / 2)), true);
                }
                switch (attack.skill) {
                    case 4101005:
                    case 5111004: {
                        stats.setHp(stats.getHp() + (int)Math.min(monster.getMobMaxHp(), Math.min((int)(totDamage * (double)theSkill.getEffect(player.getSkillLevel(theSkill)).getX() / 100.0), stats.getMaxHp() / 2)), true);
                        break;
                    }
                    case 5211006:
                    case 5220011:
                    case 22151002: {
                        player.setLinkMid(monster.getObjectId());
                        break;
                    }
                    case 1311005: {
                        final int remainingHP = stats.getHp() - totDamage * effect.getX() / 100;
                        stats.setHp((remainingHP < 1) ? 1 : remainingHP);
                        break;
                    }
                    case 4001002:
                    case 4001334:
                    case 4001344:
                    case 4111005:
                    case 4121007:
                    case 4201005:
                    case 4211002:
                    case 4221001:
                    case 4221007:
                    case 4301001:
                    case 4311002:
                    case 4311003:
                    case 4331000:
                    case 4331004:
                    case 4331005:
                    case 4341005: {
                        final ISkill skill = SkillFactory.getSkill(4120005);
                        final ISkill skill2 = SkillFactory.getSkill(4220005);
                        final ISkill skill3 = SkillFactory.getSkill(4340001);
                        if (player.getSkillLevel(skill) > 0) {
                            final MapleStatEffect venomEffect = skill.getEffect(player.getSkillLevel(skill));
                            for (int i = 0; i < attackCount; ++i) {
                                if (venomEffect.makeChanceResult() && monster.getVenomMulti() < 3) {
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4120005, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                }
                            }
                            break;
                        }
                        if (player.getSkillLevel(skill2) > 0) {
                            final MapleStatEffect venomEffect = skill2.getEffect(player.getSkillLevel(skill2));
                            for (int i = 0; i < attackCount; ++i) {
                                if (venomEffect.makeChanceResult() && monster.getVenomMulti() < 3) {
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4220005, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                }
                            }
                            break;
                        }
                        if (player.getSkillLevel(skill3) > 0) {
                            final MapleStatEffect venomEffect = skill3.getEffect(player.getSkillLevel(skill3));
                            for (int i = 0; i < attackCount; ++i) {
                                if (venomEffect.makeChanceResult() && monster.getVenomMulti() < 3) {
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4340001, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                }
                            }
                            break;
                        }
                        break;
                    }
                    case 4201004: {
                        monster.handleSteal(player);
                        break;
                    }
                    case 21000002:
                    case 21100001:
                    case 21100002:
                    case 21100004:
                    case 21110002:
                    case 21110003:
                    case 21110004:
                    case 21110006:
                    case 21110007:
                    case 21110008:
                    case 21120002:
                    case 21120005:
                    case 21120006:
                    case 21120009:
                    case 21120010: {
                        if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null && !monster.getStats().isBoss()) {
                            final MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.WK_CHARGE);
                            if (eff != null && eff.getSourceId() == 21111005) {
                                monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, false);
                            }
                        }
                        if (player.getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null && !monster.getStats().isBoss()) {
                            final MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.BODY_PRESSURE);
                            if (eff != null && eff.makeChanceResult() && !monster.isBuffed(MonsterStatus.NEUTRALISE)) {
                                monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, 1, eff.getSourceId(), null, false), false, eff.getX() * 1000, false);
                            }
                            break;
                        }
                        break;
                    }
                }
                if (totDamageToOneMonster > 0) {
                    final IItem weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-11));
                    if (weapon_ != null) {
                        final MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
                        if (stat != null && Randomizer.nextInt(100) < GameConstants.getStatChance()) {
                            final MonsterStatusEffect monsterStatusEffect2 = new MonsterStatusEffect(stat, GameConstants.getXForStat(stat), GameConstants.getSkillForStat(stat), null, false);
                            monster.applyStatus(player, monsterStatusEffect2, false, 10000L, false, false);
                        }
                    }
                    if (player.getBuffedValue(MapleBuffStat.BLIND) != null) {
                        final MapleStatEffect eff2 = player.getStatForBuff(MapleBuffStat.BLIND);
                        if (eff2.makeChanceResult()) {
                            final MonsterStatusEffect monsterStatusEffect2 = new MonsterStatusEffect(MonsterStatus.ACC, eff2.getX(), eff2.getSourceId(), null, false);
                            monster.applyStatus(player, monsterStatusEffect2, false, eff2.getY() * 1000, false);
                        }
                    }
                    if (player.getBuffedValue(MapleBuffStat.HAMSTRING) != null) {
                        final ISkill skill4 = SkillFactory.getSkill(3121007);
                        final MapleStatEffect eff3 = skill4.getEffect(player.getSkillLevel(skill4));
                        if (eff3.makeChanceResult()) {
                            final MonsterStatusEffect monsterStatusEffect3 = new MonsterStatusEffect(MonsterStatus.SPEED, eff3.getX(), 3121007, null, false);
                            monster.applyStatus(player, monsterStatusEffect3, false, eff3.getY() * 1000, false);
                        }
                    }
                    if (player.getJob() == 121) {
                        for (final int charge : DamageParse.charges) {
                            final ISkill skill5 = SkillFactory.getSkill(charge);
                            if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, skill5)) {
                                final MonsterStatusEffect monsterStatusEffect4 = new MonsterStatusEffect(MonsterStatus.FREEZE, 1, charge, null, false);
                                monster.applyStatus(player, monsterStatusEffect4, false, skill5.getEffect(player.getSkillLevel(skill5)).getY() * 2000, false);
                                break;
                            }
                        }
                    }
                }
                if (effect == null || effect.getMonsterStati().size() <= 0 || !effect.makeChanceResult()) {
                    continue;
                }
                for (final Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                    monster.applyStatus(player, new MonsterStatusEffect(z.getKey(), z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), false);
                }
            }
        }
        if (attack.skill == 4331003 && totDamageToOneMonster < hpMob) {
            return;
        }
        if (attack.skill != 0 && (attack.targets > 0 || (attack.skill != 4331003 && attack.skill != 4341002)) && attack.skill != 21101003 && attack.skill != 5110001 && attack.skill != 15100004 && attack.skill != 11101002 && attack.skill != 13101002) {
            effect.applyTo(player, attack.position);
        }
        if (totDamage > 1) {
            final CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.\u4eba\u7269\u65e0\u654c, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }
    
    public static final void applyAttackMagic(final AttackInfo attack, final ISkill theSkill, final MapleCharacter player, final MapleStatEffect effect) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.\u4eba\u7269\u6b7b\u4ea1\u653b\u51fb);
            return;
        }
        if (effect == null) {
            player.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (attack.real) {}
        final int last = (effect.getAttackCount() > effect.getBulletCount()) ? effect.getAttackCount() : effect.getBulletCount();
        boolean ban = false;
        String lastReason = "";
        String reason = "";
        reason = Damage_MobCount(player, effect, attack);
        if (!"null".equals(reason)) {
            lastReason = reason;
            ban = true;
        }
        reason = Damage_HighDamage(player, effect, attack);
        if (!"null".equals(reason)) {
            lastReason = reason;
            ban = true;
        }
        if (ban && !player.isAdmin()) {
            player.ban(lastReason, true, true, false);
            FileoutputUtil.logToFile_chr(player, "Logs/Log_\u5c01\u53f7.rtf", lastReason);
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[\u5c01\u53f7\u7cfb\u7edf]" + player.getName() + " \u8be5\u73a9\u5bb6\u653b\u51fb\u5f02\u5e38\u88ab\u7cfb\u7edf\u81ea\u52a8\u5c01\u53f7\u5904\u7406\u3002").getBytes());
            return;
        }
        if (GameConstants.isMulungSkill(attack.skill)) {
            if (player.getMapId() / 10000 != 92502) {
                return;
            }
            player.mulung_EnergyModify(false);
        }
        if (GameConstants.isPyramidSkill(attack.skill)) {
            if (player.getMapId() / 1000000 != 926) {
                return;
            }
            if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                return;
            }
        }
        final PlayerStats stats = player.getStat();
        double maxDamagePerHit;
        if (attack.skill == 1000 || attack.skill == 10001000 || attack.skill == 20001000 || attack.skill == 20011000 || attack.skill == 30001000) {
            maxDamagePerHit = 40.0;
        }
        else if (GameConstants.isPyramidSkill(attack.skill)) {
            maxDamagePerHit = 1.0;
        }
        else {
            final double v75 = effect.getMatk() * 0.058;
            maxDamagePerHit = stats.getTotalMagic() * (stats.getInt() * 0.5 + v75 * v75 + effect.getMatk() * 3.3) / 100.0;
        }
        maxDamagePerHit *= 1.04;
        final Element element = (player.getBuffedValue(MapleBuffStat.ELEMENT_RESET) != null) ? Element.NEUTRAL : theSkill.getElement();
        double MaxDamagePerHit = 0.0;
        int totDamage = 0;
        final int CriticalDamage = stats.passive_sharpeye_percent();
        final ISkill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(player.getJob()));
        final int eaterLevel = player.getSkillLevel(eaterSkill);
        final MapleMap map = player.getMap();
        for (final AttackPair oned : attack.allDamage) {
            final MapleMonster monster = map.getMonsterByOid(oned.objectid);
            if (monster != null) {
                final boolean Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006 && !monster.getStats().isBoss();
                int totDamageToOneMonster = 0;
                final MapleMonsterStats monsterstats = monster.getStats();
                final int fixeddmg = monsterstats.getFixedDamage();
                MaxDamagePerHit = CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats, element, CriticalDamage, maxDamagePerHit);
                byte overallAttackCount = 0;
                for (final Pair<Integer, Boolean> eachde : oned.attack) {
                    Integer eachd = eachde.left;
                    ++overallAttackCount;
                    if (fixeddmg != -1) {
                        eachd = (monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg);
                    }
                    else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = 0;
                    }
                    else if (!player.isGM()) {
                        if (Tempest) {
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int)Math.min(monster.getMobMaxHp(), 2147483647L);
                                player.getCheatTracker().registerOffense(CheatingOffense.\u9b54\u6cd5\u4f24\u5bb3\u8fc7\u9ad8);
                            }
                        }
                        else if (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
                            if (eachd > maxDamagePerHit) {
                                player.getCheatTracker().registerOffense(CheatingOffense.\u9b54\u6cd5\u4f24\u5bb3\u8fc7\u9ad8);
                                if (eachd > MaxDamagePerHit * 2.0) {
                                    eachd = (int)(MaxDamagePerHit * 2.0);
                                    FileoutputUtil.logToFile_chr(player, "Logs/\u7269\u7406\u4f24\u5bb3\u4fee\u6b63.rtf", " \u6280\u80fd " + attack.skill + " \u602a\u7269 " + monster.getId() + " \u9884\u8ba1\u4f24\u5bb3:" + (long)MaxDamagePerHit + "  \u5b9e\u9645" + eachd);
                                    player.getCheatTracker().registerOffense(CheatingOffense.\u9b54\u6cd5\u4f24\u5bb3\u8fc7\u9ad82);
                                }
                            }
                        }
                        else if (eachd > maxDamagePerHit * 2.0) {
                            FileoutputUtil.logToFile_chr(player, "Logs/\u7269\u7406\u4f24\u5bb3\u4fee\u6b63.rtf", " \u6280\u80fd " + attack.skill + " \u602a\u7269 " + monster.getId() + " \u9884\u8ba1\u4f24\u5bb3:" + (long)MaxDamagePerHit + "  \u5b9e\u9645" + eachd);
                            eachd = (int)maxDamagePerHit;
                        }
                    }
                    totDamageToOneMonster += eachd;
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                final double Position_range = player.getPosition().distanceSq(monster.getPosition());
                final double Count_range = 700000.0;
                if (Position_range > Count_range) {
                    player.getCheatTracker().registerOffense(CheatingOffense.\u653b\u51fb\u8303\u56f4\u8fc7\u5927, " \u6280\u80fd " + attack.skill + " \u8303\u56f4 : " + (long)Position_range + "\u6b63\u5e38\u8303\u56f4 " + (long)Count_range);
                    return;
                }
                if (attack.skill == 2301002 && !monsterstats.getUndead()) {
                    player.getCheatTracker().registerOffense(CheatingOffense.\u6cbb\u6108\u672f\u653b\u51fb\u975e\u4e0d\u6b7b\u7cfb\u602a\u7269);
                    return;
                }
                if (totDamageToOneMonster <= 0) {
                    continue;
                }
                monster.damage(player, totDamageToOneMonster, true, attack.skill);
                if (monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
                    player.addHP(-(7000 + Randomizer.nextInt(8000)));
                }
                switch (attack.skill) {
                    case 2221003: {
                        monster.setTempEffectiveness(Element.FIRE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration());
                        break;
                    }
                    case 2121003: {
                        monster.setTempEffectiveness(Element.ICE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration());
                        break;
                    }
                }
                if (effect != null && effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                    for (final Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                        monster.applyStatus(player, new MonsterStatusEffect(z.getKey(), z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), false);
                    }
                }
                if (eaterLevel <= 0) {
                    continue;
                }
                eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
            }
        }
        if (attack.skill != 2301002) {
            effect.applyTo(player);
        }
        if (totDamage > 1) {
            final CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.\u4eba\u7269\u65e0\u654c, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }
    
    private static final double CalculateMaxMagicDamagePerHit(final MapleCharacter chr, final ISkill skill, final MapleMonster monster, final MapleMonsterStats mobstats, final PlayerStats stats, final Element elem, final Integer sharpEye, final double maxDamagePerMonster) {
        final int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0);
        final int Accuracy = (int)(Math.floor(stats.getTotalInt() / 10.0) + Math.floor(stats.getTotalLuk() / 10.0));
        final int MinAccuracy = mobstats.getEva() * (dLevel * 2 + 51) / 120;
        if (MinAccuracy > Accuracy && skill.getId() != 1000 && skill.getId() != 10001000 && skill.getId() != 20001000 && skill.getId() != 20011000 && skill.getId() != 30001000 && !GameConstants.isPyramidSkill(skill.getId())) {
            return 0.0;
        }
        double elemMaxDamagePerMob = 0.0;
        switch (monster.getEffectiveness(elem)) {
            case IMMUNE: {
                elemMaxDamagePerMob = 1.0;
                break;
            }
            case NORMAL: {
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster, stats);
                break;
            }
            case WEAK: {
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * 1.5, stats);
                break;
            }
            case STRONG: {
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * 0.5, stats);
                break;
            }
            default: {
                throw new RuntimeException("Unknown enum constant");
            }
        }
        elemMaxDamagePerMob -= mobstats.getMagicDefense() * 0.5;
        elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0 * sharpEye;
        elemMaxDamagePerMob += elemMaxDamagePerMob * (mobstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100.0;
        switch (skill.getId()) {
            case 1000:
            case 10001000:
            case 20001000:
            case 20011000:
            case 30001000: {
                elemMaxDamagePerMob = 40.0;
                break;
            }
            case 1020:
            case 10001020:
            case 20001020:
            case 20011020:
            case 30001020: {
                elemMaxDamagePerMob = 1.0;
                break;
            }
        }
        if (skill.getId() == 2301002) {
            elemMaxDamagePerMob *= 2.0;
        }
        if (elemMaxDamagePerMob > 199999.0) {
            elemMaxDamagePerMob = 1.9999999E7;
        }
        else if (elemMaxDamagePerMob < 0.0) {
            elemMaxDamagePerMob = 1.0;
        }
        return elemMaxDamagePerMob;
    }
    
    private static final double ElementalStaffAttackBonus(final Element elem, final double elemMaxDamagePerMob, final PlayerStats stats) {
        switch (elem) {
            case FIRE: {
                return elemMaxDamagePerMob / 100.0 * stats.element_fire;
            }
            case ICE: {
                return elemMaxDamagePerMob / 100.0 * stats.element_ice;
            }
            case LIGHTING: {
                return elemMaxDamagePerMob / 100.0 * stats.element_light;
            }
            case POISON: {
                return elemMaxDamagePerMob / 100.0 * stats.element_psn;
            }
            default: {
                return elemMaxDamagePerMob / 100.0 * stats.def;
            }
        }
    }
    
    private static void handlePickPocket(final MapleCharacter player, final MapleMonster mob, final AttackPair oned) {
        final int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET);
        final ISkill skill = SkillFactory.getSkill(4211003);
        final MapleStatEffect s = skill.getEffect(player.getSkillLevel(skill));
        for (final Pair eachde : oned.attack) {
            final Integer eachd = (Integer)eachde.left;
            if (s.makeChanceResult()) {
                player.getMap().spawnMesoDrop(Math.min((int)Math.max(eachd / 20000.0 * maxmeso, 1.0), maxmeso), new Point((int)(mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0), (int)mob.getTruePosition().getY()), mob, player, false, (byte)0);
            }
        }
    }
    
    private static double CalculateMaxWeaponDamagePerHit(final MapleCharacter player, final MapleMonster monster, final AttackInfo attack, final ISkill theSkill, final MapleStatEffect attackEffect, double maximumDamageToMonster, final Integer CriticalDamagePercent) {
        if (player.getMapId() / 1000000 == 914) {
            return 199999.0;
        }
        final List<Element> elements = new ArrayList<Element>();
        boolean defined = false;
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            switch (theSkill.getId()) {
                case 3001004:
                case 33101001: {
                    defined = true;
                    break;
                }
                case 1000:
                case 10001000:
                case 20001000:
                case 20011000:
                case 30001000: {
                    maximumDamageToMonster = 40.0;
                    defined = true;
                    break;
                }
                case 1020:
                case 10001020:
                case 20001020:
                case 20011020:
                case 30001020: {
                    maximumDamageToMonster = 1.0;
                    defined = true;
                    break;
                }
                case 4331003: {
                    maximumDamageToMonster = (double)(monster.getStats().isBoss() ? 199999L : monster.getHp());
                    defined = true;
                    break;
                }
                case 3221007: {
                    maximumDamageToMonster = (double)(monster.getStats().isBoss() ? 199999L : monster.getMobMaxHp());
                    defined = true;
                    break;
                }
                case 1221011: {
                    maximumDamageToMonster = (double)(monster.getStats().isBoss() ? 199999L : (monster.getHp() - 1L));
                    defined = true;
                    break;
                }
                case 4211006: {
                    maximumDamageToMonster = 750000.0;
                    defined = true;
                    break;
                }
                case 1009:
                case 10001009:
                case 20001009:
                case 20011009:
                case 30001009: {
                    defined = true;
                    maximumDamageToMonster = (double)(monster.getStats().isBoss() ? (monster.getMobMaxHp() / 30L * 100L) : monster.getMobMaxHp());
                    break;
                }
                case 3211006: {
                    if (monster.getStatusSourceID(MonsterStatus.FREEZE) == 3211003) {
                        defined = true;
                        maximumDamageToMonster = (double)monster.getHp();
                        break;
                    }
                    break;
                }
            }
        }
        if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            final int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);
            switch (chargeSkillId) {
                case 1211003:
                case 1211004: {
                    elements.add(Element.FIRE);
                    break;
                }
                case 1211005:
                case 1211006:
                case 21111005: {
                    elements.add(Element.ICE);
                    break;
                }
                case 1211007:
                case 1211008:
                case 15101006: {
                    elements.add(Element.LIGHTING);
                    break;
                }
                case 1221003:
                case 1221004:
                case 11111007: {
                    elements.add(Element.HOLY);
                    break;
                }
                case 12101005: {
                    elements.clear();
                    break;
                }
            }
        }
        if (player.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
            elements.add(Element.LIGHTING);
        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if (elements.size() > 0) {
            double elementalEffect = 0.0;
            switch (attack.skill) {
                case 3111003:
                case 3211003: {
                    elementalEffect = attackEffect.getX() / 200.0;
                    break;
                }
                default: {
                    elementalEffect = 0.5;
                    break;
                }
            }
            for (final Element element : elements) {
                switch (monster.getEffectiveness(element)) {
                    case IMMUNE: {
                        elementalMaxDamagePerMonster = 1.0;
                        continue;
                    }
                    case WEAK: {
                        elementalMaxDamagePerMonster *= 1.0 + elementalEffect;
                        continue;
                    }
                    case STRONG: {
                        elementalMaxDamagePerMonster *= 1.0 - elementalEffect;
                        continue;
                    }
                }
            }
        }
        final short moblevel = monster.getStats().getLevel();
        final short d = (short)((moblevel > player.getLevel()) ? ((short)(moblevel - player.getLevel())) : 0);
        elementalMaxDamagePerMonster = elementalMaxDamagePerMonster * (1.0 - 0.01 * d) - monster.getStats().getPhysicalDefense() * 0.5;
        elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0 * CriticalDamagePercent;
        if (theSkill != null && theSkill.isChargeSkill() && player.getKeyDownSkill_Time() == 0L) {
            return 0.0;
        }
        final MapleStatEffect homing = player.getStatForBuff(MapleBuffStat.HOMING_BEACON);
        if (homing != null && player.getLinkMid() == monster.getObjectId() && homing.getSourceId() == 5220011) {
            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * homing.getX();
        }
        final PlayerStats stat = player.getStat();
        elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * (monster.getStats().isBoss() ? stat.bossdam_r : stat.dam_r) / 100.0;
        if (player.getDebugMessage()) {
            player.dropMessage("[\u4f24\u5bb3\u8ba1\u7b97] \u5c5e\u6027\u4f24\u5bb3:" + (int)elementalMaxDamagePerMonster);
        }
        if (elementalMaxDamagePerMonster >= 199999.0) {
            if (!defined) {
                elementalMaxDamagePerMonster = 1.9999999E7;
            }
        }
        else if (elementalMaxDamagePerMonster < 0.0) {
            elementalMaxDamagePerMonster = 1.0;
        }
        return elementalMaxDamagePerMonster;
    }
    
    public static final AttackInfo DivideAttack(final AttackInfo attack, final int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack;
        }
        for (final AttackPair p : attack.allDamage) {
            if (p.attack != null) {
                for (final Pair<Integer, Boolean> pair : p.attack) {
                    final Pair<Integer, Boolean> eachd = pair;
                    pair.left /= rate;
                }
            }
        }
        return attack;
    }
    
    public static final AttackInfo Modify_AttackCrit(final AttackInfo attack, final MapleCharacter chr, final int type) {
        final int CriticalRate = chr.getStat().passive_sharpeye_rate();
        final boolean shadow = (type == 2 && chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) || (type == 1 && chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null);
        if (attack.skill != 4211006 && attack.skill != 3211003 && attack.skill != 4111004 && (CriticalRate > 0 || attack.skill == 4221001 || attack.skill == 3221007)) {
            for (final AttackPair p : attack.allDamage) {
                if (p.attack != null) {
                    int hit = 0;
                    final int mid_att = p.attack.size() / 2;
                    final List<Pair<Integer, Boolean>> eachd_copy = new ArrayList<Pair<Integer, Boolean>>(p.attack);
                    for (final Pair<Integer, Boolean> eachd : p.attack) {
                        ++hit;
                        if (!eachd.right) {
                            if (attack.skill == 4221001) {
                                eachd.right = (hit == 4 && Randomizer.nextInt(100) < 90);
                            }
                            else if (attack.skill == 3221007 || eachd.left > 199999) {
                                eachd.right = true;
                            }
                            else if (shadow && hit > mid_att) {
                                eachd.right = eachd_copy.get(hit - 1 - mid_att).right;
                            }
                            else {
                                eachd.right = (Randomizer.nextInt(100) < CriticalRate);
                            }
                            eachd_copy.get(hit - 1).right = eachd.right;
                        }
                    }
                }
            }
        }
        return attack;
    }
    
    public static final AttackInfo parseDmgMa(final LittleEndianAccessor lea, final MapleCharacter chr) {
        final AttackInfo ret = new AttackInfo();
        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 22121000:
            case 22151001: {
                ret.charge = lea.readInt();
                break;
            }
            default: {
                ret.charge = -1;
                break;
            }
        }
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        ret.allDamage = new ArrayList<AttackPair>();
        for (int i = 0; i < ret.targets; ++i) {
            final int oid = lea.readInt();
            lea.skip(14);
            final List<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            final MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                damage = Damage_PG(chr, damage, ret);
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }
    
    public static final AttackInfo parseDmgM(final LittleEndianAccessor lea, final MapleCharacter chr) {
        final AttackInfo ret = new AttackInfo();
        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 4341002:
            case 4341003:
            case 5101004:
            case 5201002:
            case 14111006:
            case 15101003: {
                ret.charge = lea.readInt();
                break;
            }
            default: {
                ret.charge = 0;
                break;
            }
        }
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        ret.allDamage = new ArrayList<AttackPair>();
        if (ret.skill == 4211006) {
            return parseMesoExplosion(lea, ret, chr);
        }
        for (int i = 0; i < ret.targets; ++i) {
            final int oid = lea.readInt();
            lea.skip(14);
            final List<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            final MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                damage = Damage_PG(chr, damage, ret);
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }
    
    public static final AttackInfo parseDmgR(final LittleEndianAccessor lea, final MapleCharacter chr) {
        final AttackInfo ret = new AttackInfo();
        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 3121004:
            case 3221001:
            case 5221004:
            case 13111002: {
                lea.skip(4);
                break;
            }
        }
        ret.charge = -1;
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        ret.slot = (byte)lea.readShort();
        ret.csstar = (byte)lea.readShort();
        ret.AOE = lea.readByte();
        ret.allDamage = new ArrayList<AttackPair>();
        for (int i = 0; i < ret.targets; ++i) {
            final int oid = lea.readInt();
            lea.skip(14);
            final MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            final List<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                damage = Damage_PG(chr, damage, ret);
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        lea.skip(4);
        ret.position = lea.readPos();
        return ret;
    }
    
    public static final AttackInfo parseMesoExplosion(final LittleEndianAccessor lea, final AttackInfo ret, final MapleCharacter chr) {
        if (ret.hits == 0) {
            lea.skip(4);
            final byte bullets = lea.readByte();
            for (int j = 0; j < bullets; ++j) {
                ret.allDamage.add(new AttackPair(lea.readInt(), null));
                lea.skip(1);
            }
            lea.skip(2);
            return ret;
        }
        for (int i = 0; i < ret.targets; ++i) {
            final int oid = lea.readInt();
            lea.skip(12);
            final byte bullets = lea.readByte();
            final List<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int k = 0; k < bullets; ++k) {
                int damage = lea.readInt();
                damage = Damage_PG(chr, damage, ret);
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
            lea.skip(4);
        }
        lea.skip(4);
        final byte bullets = lea.readByte();
        for (int l = 0; l < bullets; ++l) {
            ret.allDamage.add(new AttackPair(lea.readInt(), null));
            lea.skip(1);
        }
        lea.skip(2);
        return ret;
    }
    
    public static void Damage_Position(final MapleCharacter c, final MapleMonster monster, final AttackInfo ret) {
        try {
            if (!GameConstants.\u4e0d\u68c0\u6d4b\u6280\u80fd(ret.skill)) {
                if ((c.getJob() >= 1300 && c.getJob() <= 1311) || (c.getJob() >= 1400 && c.getJob() <= 1411) || (c.getJob() >= 400 && c.getJob() <= 422) || (c.getJob() >= 300 && c.getJob() <= 322) || c.getJob() == 500 || (c.getJob() >= 520 && c.getJob() <= 522)) {
                    if (c.getPosition().y - monster.getPosition().y >= 800) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7A\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().y - monster.getPosition().y <= -800) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7B\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().x - monster.getPosition().x >= 800) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7C\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().x - monster.getPosition().x <= -900) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7D\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                }
                else if (c.getJob() >= 200 && c.getJob() < 300) {
                    if (c.getPosition().y - monster.getPosition().y >= 800) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7E\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().y - monster.getPosition().y <= -800) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7F\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().x - monster.getPosition().x >= 550) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7G\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                    else if (c.getPosition().x - monster.getPosition().x <= -550) {
                        final String \u5168\u5c4f = "\u7b49\u7ea7H\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                    }
                }
                else if (c.getPosition().y - monster.getPosition().y >= 350) {
                    final String \u5168\u5c4f = "\u7b49\u7ea7I\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                }
                else if (c.getPosition().y - monster.getPosition().y <= -350) {
                    final String \u5168\u5c4f = "\u7b49\u7ea7J\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                }
                else if (c.getPosition().x - monster.getPosition().x >= 500) {
                    final String \u5168\u5c4f = "\u7b49\u7ea7K\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                }
                else if (c.getPosition().x - monster.getPosition().x <= -500) {
                    final String \u5168\u5c4f = "\u7b49\u7ea7L\uff1a" + c.getLevel() + "\r\n" + "\u804c\u4e1a\uff1a" + c.getJob() + "\r\n" + "\u5730\u56fe\uff1a" + c.getMapId() + "\r\n" + "\u4eba\u7269\u5750\u6807\uff1aX:" + c.getPosition().x + " Y:" + c.getPosition().y + "\r\n" + "\u602a\u7269\u5750\u6807\uff1a" + monster.getPosition().x + " Y:" + monster.getPosition().y + "\r\n" + "\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "\r\n" + "IP\uff1a" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\\u5168\u5c4f\u68c0\u6d4b\\" + c.getName() + ".log", \u5168\u5c4f);
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static final int Damage_PG(final MapleCharacter c, int damage, final AttackInfo ret) {
        if (Boolean.parseBoolean(ServerProperties.getProperty("KinMS.\u7834\u529f", "false"))) {
            if (ret.skill != 14101006 && damage >= 199999) {
                final int sj = Randomizer.nextInt(10000);
                if (c.isGM()) {
                    damage = 19999999;
                }
                else {
                    damage = 199999 + (c.getStat().getTotalLuk() + c.getStat().getTotalDex() + c.getStat().getTotalStr() + c.getStat().getTotalInt()) * 3 + (c.getStat().getTotalWatk() + c.getStat().getTotalMagic()) * 6;
                    if (damage > sj) {
                        damage += sj;
                    }
                }
                if (damage >= 1999999) {
                    if (c.isGM()) {
                        damage = 19999999;
                    }
                    else {
                        damage = 1999999;
                    }
                }
                DamageParse.pvpMob = MapleLifeFactory.getMonster(9400711);
                c.getClient().getSession().write((Object)MaplePacketCreator.damagePlayer(ret.skill, DamageParse.pvpMob.getId(), c.getId(), damage));
                c.getClient().getSession().write((Object)MaplePacketCreator.sendHint("#r\u7834\u529f\u4f24\u5bb3#k:" + damage, 200, 5));
            }
            return damage;
        }
        return damage;
    }
    
    public static final String Damage_AttackCount(final MapleCharacter player, final MapleStatEffect effect, final AttackInfo attack, final int attackCount) {
        String reason = "null";
        int last = attackCount;
        boolean mirror_fix = false;
        if (player.getJob() >= 411 && player.getJob() <= 412) {
            mirror_fix = true;
        }
        if (mirror_fix) {
            last *= 2;
        }
        if (attack.hits > last) {
            reason = "\u5c01\u5305\u4f24\u5bb3\u6b21\u6570 : " + last + " \u5c01\u5305\u4f24\u5bb3\u6b21\u6570: " + attack.skill;
        }
        return reason;
    }
    
    public static final String Damage_HighDamage(final MapleCharacter player, final MapleStatEffect effect, final AttackInfo attack) {
        final boolean BeginnerJob = player.getJob() == 0 || player.getJob() == 1000;
        String reason = "null";
        int check = 2000001;
        if (player.getLevel() <= 15) {
            check = 6000;
        }
        else if (player.getLevel() <= 20) {
            check = 10000;
        }
        else if (player.getLevel() <= 30) {
            check = 25000;
        }
        else if (player.getLevel() <= 60) {
            check = 40000;
        }
        else if (player.getLevel() <= 100) {
            check = 80000;
        }
        else if (player.getLevel() <= 150) {
            check = 199999;
        }
        for (final AttackPair oned : attack.allDamage) {
            if (player.getMap().getMonsterByOid(oned.objectid) != null) {
                for (final Pair<Integer, Boolean> eachde : oned.attack) {
                    if (eachde.left >= 2000001) {
                        reason = "\u6280\u80fd " + attack.skill + " \u6253\u602a\u4f24\u5bb3 " + eachde.left;
                    }
                    if (GameConstants.Novice_Skill(attack.skill) && eachde.left > 40) {
                        reason = "\u6280\u80fd " + attack.skill + " \u6253\u602a\u4f24\u5bb3 " + eachde.left;
                    }
                    if (BeginnerJob) {
                        if (eachde.left <= 40) {
                            continue;
                        }
                        reason = "\u6280\u80fd " + attack.skill + " \u6253\u602a\u4f24\u5bb3 " + eachde.left;
                    }
                    else {
                        if (eachde.left < check) {
                            continue;
                        }
                        reason = "\u6280\u80fd " + attack.skill + " \u6253\u602a\u4f24\u5bb3 " + eachde.left;
                    }
                }
            }
        }
        if (GameConstants.isElseSkill(attack.skill)) {
            reason = "null";
        }
        return reason;
    }
    
    public static final String Damage_MobCount(final MapleCharacter player, final MapleStatEffect effect, final AttackInfo attack) {
        final String reason = "null";
        return reason;
    }
    
    static {
        charges = new int[] { 1211005, 1211006 };
    }
}
