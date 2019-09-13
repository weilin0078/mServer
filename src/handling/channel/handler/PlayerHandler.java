package handling.channel.handler;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import client.SkillMacro;
import client.anticheat.CheatingOffense;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.MapConstants;
import handling.channel.ChannelServer;
import server.AutobanManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer;
import server.events.MapleSnowball;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobAttackInfoFactory;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;

public class PlayerHandler
{
    private static boolean isFinisher(final int skillid) {
        switch (skillid) {
            case 1111003:
            case 1111004:
            case 1111005:
            case 1111006:
            case 11111002:
            case 11111003: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static void ChangeMonsterBookCover(final int bookid, final MapleClient c, final MapleCharacter chr) {
        if (bookid == 0 || GameConstants.isMonsterCard(bookid)) {
            chr.setMonsterBookCover(bookid);
            chr.getMonsterBook().updateCard(c, bookid);
        }
    }
    
    public static void ChangeSkillMacro(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        for (int num = slea.readByte(), i = 0; i < num; ++i) {
            final String name = slea.readMapleAsciiString();
            final int shout = slea.readByte();
            final int skill1 = slea.readInt();
            final int skill2 = slea.readInt();
            final int skill3 = slea.readInt();
            final SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }
    
    public static final void ChangeKeymap(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        if (slea.available() > 8L && chr != null) {
            chr.updateTick(slea.readInt());
            for (int numChanges = slea.readInt(), i = 0; i < numChanges; ++i) {
                chr.changeKeybinding(slea.readInt(), slea.readByte(), slea.readInt());
            }
        }
        else if (chr != null) {
            final int type = slea.readInt();
            final int data = slea.readInt();
            switch (type) {
                case 1: {
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122221));
                        break;
                    }
                    chr.getQuestNAdd(MapleQuest.getInstance(122221)).setCustomData(String.valueOf(data));
                    break;
                }
                case 2: {
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122222));
                        break;
                    }
                    chr.getQuestNAdd(MapleQuest.getInstance(122222)).setCustomData(String.valueOf(data));
                    break;
                }
                case 3: {
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122224));
                        break;
                    }
                    chr.getQuestNAdd(MapleQuest.getInstance(122224)).setCustomData(String.valueOf(data));
                    break;
                }
            }
        }
    }
    
    public static final void UseChair(final int itemId, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final IItem toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.使用不存在道具, Integer.toString(itemId));
            return;
        }
        if (itemId == 3011000) {
            boolean haz = false;
            for (final IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH).list()) {
                if (item.getItemId() == 5340000) {
                    haz = true;
                }
                else {
                    if (item.getItemId() == 5340001) {
                        haz = false;
                        chr.startFishingTask(true);
                        break;
                    }
                    continue;
                }
            }
            if (haz) {
                chr.startFishingTask(false);
            }
        }
        chr.setChair(itemId);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), itemId), false);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public static final void CancelChair(final short id, final MapleClient c, final MapleCharacter chr) {
        if (id == -1) {
            if (chr.getChair() == 3011000) {
                chr.cancelFishingTask();
            }
            chr.setChair(0);
            c.getSession().write((Object)MaplePacketCreator.cancelChair(-1));
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), 0), false);
        }
        else {
            chr.setChair(id);
            c.getSession().write((Object)MaplePacketCreator.cancelChair(id));
        }
    }
    
    public static final void TrockAddMap(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte addrem = slea.readByte();
        final byte vip = slea.readByte();
        if (vip == 1) {
            if (addrem == 0) {
                chr.deleteFromRocks(slea.readInt());
            }
            else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRockMap();
                }
                else {
                    chr.dropMessage(1, "你可能不能添加此地图.");
                }
            }
        }
        else if (addrem == 0) {
            chr.deleteFromRegRocks(slea.readInt());
        }
        else if (addrem == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                chr.addRegRockMap();
            }
            else {
                chr.dropMessage(1, "你可能不能添加此地图.");
            }
        }
        c.getSession().write((Object)MTSCSPacket.getTrockRefresh(chr, vip == 1, addrem == 3));
    }
    
    public static final void CharInfoRequest(final int objectid, final MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
        if (player != null && !player.isClone() && (!player.isGM() || c.getPlayer().isGM())) {
            c.getSession().write((Object)MaplePacketCreator.charInfo(player, c.getPlayer().getId() == objectid));
        }
    }
    
    public static final void TakeDamage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        final byte type = slea.readByte();
        slea.skip(1);
        int damage = slea.readInt();
        int oid = 0;
        int monsteridfrom = 0;
        final int reflect = 0;
        byte direction = 0;
        final int pos_x = 0;
        final int pos_y = 0;
        int fake = 0;
        int mpattack = 0;
        boolean is_pg = false;
        boolean isDeadlyAttack = false;
        MapleMonster attacker = null;
        if (chr == null || chr.isHidden() || chr.getMap() == null) {
            return;
        }
        if (chr.isGM() && chr.isInvincible()) {
            return;
        }
        final PlayerStats stats = chr.getStat();
        if (type != -2 && type != -3 && type != -4) {
            monsteridfrom = slea.readInt();
            oid = slea.readInt();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = slea.readByte();
            if (attacker == null) {
                return;
            }
            if (type != -1) {
                final MobAttackInfo attackInfo = MobAttackInfoFactory.getInstance().getMobAttackInfo(attacker, type);
                if (attackInfo != null) {
                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        mpattack = stats.getMp() - 1;
                    }
                    else {
                        mpattack += attackInfo.getMpBurn();
                    }
                    final MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                    if (skill != null && (damage == -1 || damage > 0)) {
                        skill.applyEffect(chr, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
        }
        if (damage == -1) {
            fake = 4020002 + (chr.getJob() / 10 - 40) * 100000;
        }
        else if (damage < -1 || damage > 60000) {
            AutobanManager.getInstance().addPoints(c, 1000, 60000L, "Taking abnormal amounts of damge from " + monsteridfrom + ": " + damage);
            return;
        }
        chr.getCheatTracker().checkTakeDamage(damage);
        if (damage > 0) {
            chr.getCheatTracker().setAttacksWithoutHit(false);
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                chr.cancelMorphs();
            }
            if (slea.available() == 3L) {
                final byte level = slea.readByte();
                if (level > 0) {
                    final MobSkill skill = MobSkillFactory.getMobSkill(slea.readShort(), level);
                    if (skill != null) {
                        skill.applyEffect(chr, attacker, false);
                    }
                }
            }
            if (type != -2 && type != -3 && type != -4) {
                final int bouncedam_ = ((Randomizer.nextInt(100) < chr.getStat().DAMreflect_rate) ? chr.getStat().DAMreflect : 0) + ((type == -1 && chr.getBuffedValue(MapleBuffStat.POWERGUARD) != null) ? chr.getBuffedValue(MapleBuffStat.POWERGUARD) : 0) + ((type == -1 && chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) != null) ? chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) : 0);
                if (bouncedam_ > 0 && attacker != null) {
                    long bouncedamage = damage * bouncedam_ / 100;
                    bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10L);
                    attacker.damage(chr, bouncedamage, true);
                    damage -= (int)bouncedamage;
                    chr.getMap().broadcastMessage(chr, MobPacket.damageMonster(oid, bouncedamage), chr.getPosition());
                    is_pg = true;
                }
            }
            if (type != -1 && type != -2 && type != -3 && type != -4) {
                switch (chr.getJob()) {
                    case 112: {
                        final ISkill skill2 = SkillFactory.getSkill(1120004);
                        if (chr.getSkillLevel(skill2) > 0) {
                            damage *= (int)(skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0);
                            break;
                        }
                        break;
                    }
                    case 122: {
                        final ISkill skill2 = SkillFactory.getSkill(1220005);
                        if (chr.getSkillLevel(skill2) > 0) {
                            damage *= (int)(skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0);
                            break;
                        }
                        break;
                    }
                    case 132: {
                        final ISkill skill2 = SkillFactory.getSkill(1320005);
                        if (chr.getSkillLevel(skill2) > 0) {
                            damage *= (int)(skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0);
                            break;
                        }
                        break;
                    }
                }
            }
            final MapleStatEffect bouncedam_A = chr.getStatForBuff(MapleBuffStat.BODY_PRESSURE);
            if (attacker != null && bouncedam_A != null && damage > 0) {
                final ISkill 抗压 = SkillFactory.getSkill(21101003);
                final int 抗压伤害 = (int)(抗压.getEffect(chr.getSkillLevel(21101003)).getDamage() / 100.0 * damage);
                attacker.damage(chr, 抗压伤害, true);
                damage -= 抗压伤害;
                chr.getMap().broadcastMessage(chr, MobPacket.damageMonster(oid, 抗压伤害), chr.getPosition());
                chr.checkMonsterAggro(attacker);
                chr.setHp(chr.getHp() - damage);
            }
            final MapleStatEffect magicShield = chr.getStatForBuff(MapleBuffStat.MAGIC_SHIELD);
            if (magicShield != null) {
                damage -= (int)(magicShield.getX() / 100.0 * damage);
            }
            final MapleStatEffect blueAura = chr.getStatForBuff(MapleBuffStat.BLUE_AURA);
            if (blueAura != null) {
                damage -= (int)(blueAura.getY() / 100.0 * damage);
            }
            if (chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC) != null && chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB) != null) {
                final double buff = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC);
                final double buffz = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB);
                if ((int)(buff / 100.0 * chr.getStat().getMaxHp()) <= damage) {
                    damage -= (int)(buffz / 100.0 * damage);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
                }
            }
            if (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null) {
                int hploss = 0;
                int mploss = 0;
                if (isDeadlyAttack) {
                    if (stats.getHp() > 1) {
                        hploss = stats.getHp() - 1;
                    }
                    if (stats.getMp() > 1) {
                        mploss = stats.getMp() - 1;
                    }
                    if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                        mploss = 0;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }
                else {
                    mploss = (int)(damage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) / 100.0)) + mpattack;
                    hploss = damage - mploss;
                    if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                        mploss = 0;
                    }
                    else if (mploss > stats.getMp()) {
                        mploss = stats.getMp();
                        hploss = damage - mploss + mpattack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }
            }
            else if (chr.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
                damage = ((damage % 2 == 0) ? (damage / 2) : (damage / 2 + 1));
                final int mesoloss = (int)(damage * (chr.getBuffedValue(MapleBuffStat.MESOGUARD) / 100.0));
                if (chr.getMeso() < mesoloss) {
                    chr.gainMeso(-chr.getMeso(), false);
                    chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
                }
                else {
                    chr.gainMeso(-mesoloss, false);
                }
                if (isDeadlyAttack && stats.getMp() > 1) {
                    mpattack = stats.getMp() - 1;
                }
                chr.addMPHP(-damage, -mpattack);
            }
            else if (isDeadlyAttack) {
                chr.addMPHP((stats.getHp() > 1) ? (-(stats.getHp() - 1)) : 0, (stats.getMp() > 1) ? (-(stats.getMp() - 1)) : 0);
            }
            else {
                chr.addMPHP(-damage, -mpattack);
            }
            chr.handleBattleshipHP(-damage);
        }
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.damagePlayer(type, monsteridfrom, chr.getId(), damage, fake, direction, reflect, is_pg, oid, pos_x, pos_y), false);
        }
    }
    
    public static final void AranCombo(final MapleClient c, final MapleCharacter chr) {
        if (chr != null && chr.getJob() >= 2000 && chr.getJob() <= 2112) {
            short combo = chr.getCombo();
            final long curr = System.currentTimeMillis();
            if (combo > 0 && curr - chr.getLastCombo() > 7000L) {
                combo = 0;
                final ISkill skill = SkillFactory.getSkill(21000000);
                final ISkill skillA = SkillFactory.getSkill(21110000);
                if (combo <= 1 && skill != null) {
                    SkillFactory.getSkill(21000000).getEffect(0).applyComboBuff(chr, (short)0);
                }
                if (combo <= 1 && skillA != null) {
                    SkillFactory.getSkill(21110000).getEffect(0).applyComboBuffA(chr, (short)0);
                }
            }
            if (combo < 30000) {
                ++combo;
            }
            chr.setLastCombo(curr);
            chr.setCombo(combo);
            switch (combo) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100: {
                    if (chr.getSkillLevel(21000000) >= combo / 10) {
                        SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
                    }
                    if (chr.getSkillLevel(21110000) >= combo / 10) {
                        SkillFactory.getSkill(21110000).getEffect(combo / 10).applyComboBuffA(chr, combo);
                        break;
                    }
                    break;
                }
            }
            c.getSession().write((Object)MaplePacketCreator.testCombo(combo));
            chr.setLastCombo(curr);
        }
    }
    
    public static final void UseItemEffect(final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        final byte flag = toUse.getFlag();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.itemEffects(chr.getId(), itemId), false);
        if (ItemFlag.KARMA_EQ.check(flag)) {
            toUse.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
            c.getSession().write((Object)MaplePacketCreator.getCharInfo(chr));
            chr.getMap().removePlayer(chr);
            chr.getMap().addPlayer(chr);
        }
        else if (ItemFlag.KARMA_USE.check(flag)) {
            toUse.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
            c.getSession().write((Object)MaplePacketCreator.getCharInfo(chr));
            chr.getMap().removePlayer(chr);
            chr.getMap().addPlayer(chr);
        }
    }
    
    public static final void CancelItemEffect(final int id, final MapleCharacter chr) {
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1L);
    }
    
    public static final void CancelBuffHandler(final int sourceid, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (sourceid != 1013 || chr.getMountId() != 0) {}
        final ISkill skill = SkillFactory.getSkill1(sourceid);
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        }
        else {
            chr.cancelEffect(skill.getEffect(1), false, -1L);
        }
    }
    
    public static final void SkillEffect(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int skillId = slea.readInt();
        final byte level = slea.readByte();
        final byte flags = slea.readByte();
        final byte speed = slea.readByte();
        final byte unk = slea.readByte();
        final ISkill skill = SkillFactory.getSkill(skillId);
        if (chr == null) {
            return;
        }
        final int skilllevel_serv = chr.getSkillLevel(skill);
        if (skilllevel_serv > 0 && skilllevel_serv == level && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillEffect(chr, skillId, level, flags, speed, unk), false);
        }
    }
    
    public static final void SpecialMove(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        final int skillid = slea.readInt();
        final int skillLevel = slea.readByte();
        final ISkill skill = SkillFactory.getSkill(skillid);
        if (chr.getSkillLevel(skill) <= 0 || chr.getSkillLevel(skill) != skillLevel) {
            if (!GameConstants.isMulungSkill(skillid) && !GameConstants.isPyramidSkill(skillid)) {
                return;
            }
            if (GameConstants.isMulungSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92502) {
                    return;
                }
                chr.mulung_EnergyModify(false);
            }
            else if (GameConstants.isPyramidSkill(skillid) && chr.getMapId() / 10000 != 92602) {
                return;
            }
        }
        final MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(GameConstants.getLinkedAranSkill(skillid)));
        if (effect.getCooldown() > 0 && !chr.isGM()) {
            if (chr.skillisCooling(skillid)) {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            if (skillid != 5221006) {
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020: {
                final byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; ++i) {
                    final int mobId = slea.readInt();
                    final MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob != null) {
                        mob.switchController(chr, mob.isControllerHasAggro());
                    }
                }
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), skillid, 1, slea.readByte()), chr.getPosition());
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            default: {
                Point pos = null;
                if (slea.available() == 7L || skill.getId() == 3111002 || skill.getId() == 3211002) {
                    pos = slea.readPos();
                }
                if (effect.isMagicDoor()) {
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(c.getPlayer(), pos);
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    break;
                }
                else {
                    final int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());
                    if (mountid != 0 && mountid != GameConstants.getMountItem(skill.getId()) && !c.getPlayer().isGM() && c.getPlayer().getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null && c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-118)) == null && !GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob())) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    effect.applyTo(c.getPlayer(), pos);
                    break;
                }
                
            }
        }
    }
    
    public static final void closeRangeAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean energy) {
        if (chr == null || (energy && chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null && chr.getBuffedValue(MapleBuffStat.BODY_PRESSURE) == null && !GameConstants.isKOC(chr.getJob()))) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.人物死亡攻击);
            return;
        }
        final AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgM(slea, chr), chr, 1);
        final boolean mirror = chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        int attackCount = (chr.getJob() >= 430 && chr.getJob() <= 434) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        ISkill skill = null;
        if (attack.skill == 21100004 || attack.skill == 21100005 || attack.skill == 21110003 || attack.skill == 21110004 || attack.skill == 21120006 || attack.skill == 21120007) {
            chr.setCombo((short)1);
        }
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            skillLevel = chr.getSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            maxdamage *= effect.getDamage() / 100.0;
            attackCount = effect.getAttackCount();
            if (effect.getCooldown() > 0 && !chr.isGM()) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        attackCount *= (mirror ? 2 : 1);
        if (!energy) {
            if ((chr.getMapId() == 109060000 || chr.getMapId() == 109060002 || chr.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowball.MapleSnowballs.hitSnowball(chr);
            }
            int numFinisherOrbs = 0;
            final Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);
            if (isFinisher(attack.skill)) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                chr.handleOrbconsume();
            }
            else if (attack.targets > 0 && comboBuff != null) {
                switch (chr.getJob()) {
                    case 111:
                    case 112:
                    case 1110:
                    case 1111: {
                        if (attack.skill != 1111008) {
                            chr.handleOrbgain();
                            break;
                        }
                        break;
                    }
                }
            }
            switch (chr.getJob()) {
                case 511:
                case 512: {
                    chr.handleEnergyCharge(5110001, attack.targets * attack.hits);
                    break;
                }
                case 1510:
                case 1511:
                case 1512: {
                    chr.handleEnergyCharge(15100004, attack.targets * attack.hits);
                    break;
                }
            }
            if (attack.targets > 0 && attack.skill == 1211002) {
                final int advcharge_level = chr.getSkillLevel(SkillFactory.getSkill(1220010));
                if (advcharge_level > 0) {
                    if (!SkillFactory.getSkill(1220010).getEffect(advcharge_level).makeChanceResult()) {
                        chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
                        chr.cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
                    }
                }
                else {
                    chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
                }
            }
            if (numFinisherOrbs > 0) {
                maxdamage *= numFinisherOrbs;
            }
            else if (comboBuff != null) {
                ISkill combo;
                if (c.getPlayer().getJob() == 1110 || c.getPlayer().getJob() == 1111) {
                    combo = SkillFactory.getSkill(11111001);
                }
                else {
                    combo = SkillFactory.getSkill(1111002);
                }
                if (c.getPlayer().getSkillLevel(combo) > 0) {
                    maxdamage *= 1.0 + (combo.getEffect(c.getPlayer().getSkillLevel(combo)).getDamage() / 100.0 - 1.0) * (comboBuff - 1);
                }
            }
            if (isFinisher(attack.skill)) {
                if (numFinisherOrbs == 0) {
                    return;
                }
                maxdamage = 199999.0;
            }
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), chr.getPosition());
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        final WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final ISkill skil2 = skill;
                final int skillLevel2 = skillLevel;
                final int attackCount2 = attackCount;
                final double maxdamage2 = maxdamage;
                final MapleStatEffect eff2 = effect;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                Timer.CloneTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        clone.getMap().broadcastMessage(MaplePacketCreator.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge));
                        DamageParse.applyAttack(attack2, skil2, chr, attackCount2, maxdamage2, eff2, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
                    }
                }, 500 * i + 500);
            }
        }
    }
    
    public static final void rangedAttack(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.人物死亡攻击);
            return;
        }
        final AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgR(slea, chr), chr, 2);
        int bulletCount = 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        ISkill skill = null;
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            skillLevel = chr.getSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            switch (attack.skill) {
                case 13111007:
                case 14101006:
                case 21110004:
                case 21120006: {
                    bulletCount = effect.getAttackCount();
                    break;
                }
                default: {
                    bulletCount = effect.getBulletCount();
                    break;
                }
            }
            if (effect.getCooldown() > 0 && !chr.isGM()) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        final Integer ShadowPartner = chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER);
        if (ShadowPartner != null) {
            bulletCount *= 2;
        }
        int projectile = 0;
        int visProjectile = 0;
        if (attack.AOE != 0 && chr.getBuffedValue(MapleBuffStat.SOULARROW) == null && attack.skill != 4111004) {
            if (chr.getInventory(MapleInventoryType.USE).getItem(attack.slot) == null) {
                return;
            }
            projectile = chr.getInventory(MapleInventoryType.USE).getItem(attack.slot).getItemId();
            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar) == null) {
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar).getItemId();
            }
            else {
                visProjectile = projectile;
            }
            if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) == null) {
                int bulletConsume = bulletCount;
                if (effect != null && effect.getBulletConsume() != 0) {
                    bulletConsume = effect.getBulletConsume() * ((ShadowPartner != null) ? 2 : 1);
                }
                if (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true)) {
                    chr.dropMessage(5, "You do not have enough arrows/bullets/stars.");
                    return;
                }
            }
        }
        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        final PlayerStats statst = chr.getStat();
        double basedamage = 0.0;
        Label_0647: {
            switch (attack.skill) {
                case 4001344:
                case 4121007:
                case 14001004:
                case 14111005: {
                    basedamage = statst.getTotalLuk() * 5.0f * (statst.getTotalWatk() + projectileWatk) / 100.0f;
                    break;
                }
                case 4111004: {
                    basedamage = 13000.0;
                    break;
                }
                default: {
                    if (projectileWatk != 0) {
                        basedamage = statst.calculateMaxBaseDamage(statst.getTotalWatk() + projectileWatk);
                    }
                    else {
                        basedamage = statst.getCurrentMaxBaseDamage();
                    }
                    switch (attack.skill) {
                        case 3101005: {
                            basedamage *= effect.getX() / 100.0;
                            break Label_0647;
                        }
                    }
                    break;
                }
            }
        }
        if (effect != null) {
            basedamage *= effect.getDamage() / 100.0;
            int money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, (ShadowPartner != null) ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
        final WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final ISkill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final double basedamage2 = basedamage;
                final int bulletCount2 = bulletCount;
                final int visProjectile2 = visProjectile;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                Timer.CloneTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        clone.getMap().broadcastMessage(MaplePacketCreator.rangedAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk));
                        DamageParse.applyAttack(attack2, skil2, chr, bulletCount2, basedamage2, eff2, AttackType.RANGED);
                    }
                }, 500 * i + 500);
            }
        }
    }
    
    public static final void MagicDamage(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.人物死亡攻击);
            return;
        }
        final AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgMa(slea, chr), chr, 3);
        final ISkill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
        final int skillLevel = chr.getSkillLevel(skill);
        final MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            return;
        }
        if (effect.getCooldown() > 0 && !chr.isGM()) {
            if (chr.skillisCooling(attack.skill)) {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.unk), chr.getPosition());
        DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect);
        final WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final ISkill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                Timer.CloneTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        clone.getMap().broadcastMessage(MaplePacketCreator.magicAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, attack2.allDamage, attack2.charge, clone.getLevel(), attack2.unk));
                        DamageParse.applyAttackMagic(attack2, skil2, chr, eff2);
                    }
                }, 500 * i + 500);
            }
        }
    }
    
    public static final void DropMeso(final int meso, final MapleCharacter chr) {
        if (!chr.isAlive() || meso < 10 || meso > 50000 || meso > chr.getMeso()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getPosition(), chr, chr, true, (byte)0);
        chr.getCheatTracker().checkDrop(true);
    }
    
    public static final void ChangeEmotion(final int emote, final MapleCharacter chr) {
        if (emote > 7) {
            final int emoteid = 5159992 + emote;
            final MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.使用不存在道具, Integer.toString(emoteid));
                return;
            }
        }
        if (emote > 0 && chr != null && chr.getMap() != null) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.facialExpression(chr, emote), false);
            final WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    Timer.CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            clone.getMap().broadcastMessage(MaplePacketCreator.facialExpression(clone, emote));
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }
    
    public static final void Heal(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        final int healHP = slea.readShort();
        final int healMP = slea.readShort();
        final PlayerStats stats = chr.getStat();
        int check_hp = (int)stats.getHealHP();
        final int check_mp = (int)stats.getHealMP();
        if (stats.getHp() <= 0) {
            return;
        }
        if (chr.canHP() && healHP != 0) {
            if (chr.getChair() != 0) {
                check_hp += 150;
            }
            if (healHP > check_hp * 2 && healHP > 20) {
                chr.getCheatTracker().registerOffense(CheatingOffense.回复过多HP, String.valueOf(healHP) + " 服务器:" + check_hp);
            }
            chr.addHP(healHP);
        }
        if (chr.canMP() && healMP != 0) {
            if (healMP > check_mp * 2 && healMP > 20) {
                chr.getCheatTracker().registerOffense(CheatingOffense.回复过多MP, String.valueOf(healMP) + "服务器:" + check_mp);
            }
            chr.addMP(healMP);
        }
    }
    
    public static final void MovePlayer(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final Point Original_Pos = chr.getPosition();
        slea.skip(33);
        List<LifeMovementFragment> res;
        try {
            res = MovementParse.parseMovement(slea, 1);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("AIOBE Type1:\n" + slea.toString(true));
            return;
        }
        if (res != null && c.getPlayer().getMap() != null) {
            if (slea.available() < 13L || slea.available() > 26L) {
                System.out.println("slea.available != 13-26 (movement parsing error)\n" + slea.toString(true));
                return;
            }
            final List<LifeMovementFragment> res2 = new ArrayList<LifeMovementFragment>(res);
            final MapleMap map = c.getPlayer().getMap();
            if (chr.isHidden()) {
                chr.setLastRes(res2);
                c.getPlayer().getMap().broadcastGMMessage(chr, MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            }
            else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            }
            MovementParse.updatePosition(res, chr, 0);
            final Point pos = chr.getPosition();
            map.movePlayer(chr, pos);
            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
                final MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    final Point original_pos = fol.getPosition();
                    MovementParse.updatePosition(res, fol, 0);
                }
                else {
                    chr.checkFollow();
                }
            }
            final WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    final List<LifeMovementFragment> res3 = new ArrayList<LifeMovementFragment>(res2);
                    Timer.CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map) {
                                    if (clone.isHidden()) {
                                        clone.setLastRes(res3);
                                    }
                                    else {
                                        map.broadcastMessage(clone, MaplePacketCreator.movePlayer(clone.getId(), res3, Original_Pos), false);
                                    }
                                    MovementParse.updatePosition(res3, clone, 0);
                                    map.movePlayer(clone, pos);
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }, 500 * i + 500);
                }
            }
            int count = c.getPlayer().getFallCounter();
            try {
                if (map.getFootholds().findBelow(c.getPlayer().getPosition()) == null && c.getPlayer().getPosition().y > c.getPlayer().getOldPosition().y && c.getPlayer().getPosition().x == c.getPlayer().getOldPosition().x) {
                    if (count > 10) {
                        c.getPlayer().changeMap(map, map.getPortal(0));
                        c.getPlayer().setFallCounter(0);
                    }
                    else {
                        c.getPlayer().setFallCounter(++count);
                    }
                }
                else if (count > 0) {
                    c.getPlayer().setFallCounter(0);
                }
            }
            catch (Exception ex) {}
            c.getPlayer().setOldPosition(new Point(c.getPlayer().getPosition()));
        }
    }
    
    public static final void UpdateHandler(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().saveToDB(true, true);
    }
    
    public static final void ChangeMapSpecial(final SeekableLittleEndianAccessor slea, final String portal_name, final MapleClient c, final MapleCharacter chr) {
        slea.readShort();
        final MaplePortal portal = chr.getMap().getPortal(portal_name);
        if (portal != null) {
            portal.enterPortal(c);
        }
        else {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }
    
    public static final void ChangeMap(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            chr.dropMessage(5, "你现在已经假死请使用@ea");
            return;
        }
        if (slea.available() == 0L) {
            final String[] socket = c.getChannelServer().getIP().split(":");
            chr.saveToDB(false, false);
            c.getChannelServer().removePlayer(c.getPlayer());
            c.updateLoginState(1);
            try {
                c.getSession().write((Object)MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (slea.available() != 0L) {
            slea.readByte();
            int targetid = slea.readInt();
            if (targetid == 0) {
                targetid = 1000000;
            }
            final MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            final boolean wheel = slea.readShort() > 0 && !MapConstants.isEventMap(chr.getMapId()) && chr.haveItem(5510000, 1, false, true);
            if (targetid != -1 && !chr.isAlive()) {
                chr.setStance(0);
                if (chr.getEventInstance() != null && chr.getEventInstance().revivePlayer(chr) && chr.isAlive()) {
                    return;
                }
                if (chr.getPyramidSubway() != null) {
                    chr.getStat().setHp(50);
                    chr.getPyramidSubway().fail(chr);
                    return;
                }
                if (!wheel) {
                    chr.getStat().setHp(50);
                    final MapleMap to = chr.getMap().getReturnMap();
                    if (to == null) {
                        chr.setHp(50);
                        chr.updateSingleStat(MapleStat.HP, 50);
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    chr.changeMap(to, to.getPortal(0));
                }
                else {
                    c.getSession().write((Object)MTSCSPacket.useWheel((byte)(chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1)));
                    chr.getStat().setHp(chr.getStat().getMaxHp() / 100 * 40);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                    final MapleMap to = chr.getMap();
                    chr.changeMap(to, to.getPortal(0));
                }
            }
            else if (targetid != -1 && chr.isGM()) {
                final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                if (to != null && to.getPortal(0) != null) {
                    chr.changeMap(to, to.getPortal(0));
                }
            }
            else if (targetid != -1 && !chr.isGM()) {
                final int divi = chr.getMapId() / 100;
                if (divi == 9130401) {
                    if (targetid == 130000000 || targetid / 100 == 9130401) {
                        final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to2, to2.getPortal(0));
                    }
                }
                else if (divi == 9140900) {
                    if (targetid == 914090011 || targetid == 914090012 || targetid == 914090013 || targetid == 140090000) {
                        final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to2, to2.getPortal(0));
                    }
                }
                else if (divi == 9140901 && targetid == 140000000) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (divi == 9140902 && (targetid == 140030000 || targetid == 140000000)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (divi == 9000900 && targetid / 100 == 9000900 && targetid > chr.getMapId()) {
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (divi / 1000 == 9000 && targetid / 100000 == 9000) {
                    if (targetid < 900090000 || targetid > 900090004) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                    }
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (divi / 10 == 1020 && targetid == 1020000) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (chr.getMapId() == 900090101 && targetid == 100030100) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (chr.getMapId() == 2010000 && targetid == 104000000) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
                else if (chr.getMapId() == 106020001 || chr.getMapId() == 106020502) {
                    if (targetid == chr.getMapId() - 1) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to2, to2.getPortal(0));
                    }
                }
                else if (chr.getMapId() == 0 && targetid == 10000) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    final MapleMap to2 = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to2, to2.getPortal(0));
                }
            }
            else if (portal != null) {
                portal.enterPortal(c);
            }
            else {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
    }
    
    public static final void InnerPortal(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        final Point Original_Pos = chr.getPosition();
        final int toX = slea.readShort();
        final int toY = slea.readShort();
        if (portal == null) {
            return;
        }
        if (portal.getPosition().distanceSq(chr.getPosition()) > 22500.0) {
            chr.getCheatTracker().registerOffense(CheatingOffense.使用过远传送点);
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
    }
    
    public static final void snowBall(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public static final void leftKnockBack(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) {
            c.getSession().write((Object)MaplePacketCreator.leftKnockBack());
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }
    
    public static void Rabbit(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int arrackfrom = slea.readInt();
        final int damage = slea.readInt() + 100;
        final int attackto = slea.readInt();
        final MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(attackto);
        if (mob != null && mob.getHp() > 0L) {
            mob.damage(c.getPlayer(), damage, true);
        }
    }
}
