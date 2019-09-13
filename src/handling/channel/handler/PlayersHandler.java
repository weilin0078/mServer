package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.anticheat.CheatingOffense;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import scripting.NPCScriptManager;
import scripting.ReactorScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.events.MapleCoconut;
import server.events.MapleEventType;
import server.maps.MapleDoor;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class PlayersHandler
{
    public static void Note(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final byte type = slea.readByte();
        switch (type) {
            case 0: {
                final String name = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                final boolean fame = slea.readByte() > 0;
                slea.readInt();
                final IItem itemz = chr.getCashInventory().findByCashId((int)slea.readLong());
                if (itemz == null || !itemz.getGiftFrom().equalsIgnoreCase(name) || !chr.getCashInventory().canSendNote(itemz.getUniqueId())) {
                    return;
                }
                try {
                    chr.sendNote(name, msg, fame ? 1 : 0);
                    chr.getCashInventory().sendedNote(itemz.getUniqueId());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                final byte num = slea.readByte();
                slea.readByte();
                final byte 人气 = slea.readByte();
                for (int i = 0; i < num; ++i) {
                    final int id = slea.readInt();
                    chr.deleteNote(id, (人气 > 0) ? 人气 : 0);
                }
                break;
            }
            default: {
                System.out.println("Unhandled note action, " + type + "");
                break;
            }
        }
    }
    
    public static void GiveFame(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int who = slea.readInt();
        final int mode = slea.readByte();
        final int famechange = (mode == 0) ? -1 : 1;
        final MapleCharacter target = (MapleCharacter)chr.getMap().getMapObject(who, MapleMapObjectType.PLAYER);
        if (target == chr) {
            chr.getCheatTracker().registerOffense(CheatingOffense.添加自己声望);
            return;
        }
        if (chr.getLevel() < 15) {
            chr.getCheatTracker().registerOffense(CheatingOffense.声望十五级以下添加);
            return;
        }
        switch (chr.canGiveFame(target)) {
            case OK: {
                if (Math.abs(target.getFame() + famechange) <= 30000) {
                    target.addFame(famechange);
                    target.updateSingleStat(MapleStat.FAME, target.getFame());
                }
                if (!chr.isGM()) {
                    chr.hasGivenFame(target);
                }
                c.getSession().write((Object)MaplePacketCreator.giveFameResponse(mode, target.getName(), target.getFame()));
                target.getClient().getSession().write((Object)MaplePacketCreator.receiveFame(mode, chr.getName()));
                break;
            }
            case NOT_TODAY: {
                c.getSession().write((Object)MaplePacketCreator.giveFameErrorResponse(3));
                break;
            }
            case NOT_THIS_MONTH: {
                c.getSession().write((Object)MaplePacketCreator.giveFameErrorResponse(4));
                break;
            }
        }
    }
    
    public static void ChatRoomHandler(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        NPCScriptManager.getInstance().dispose(c);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
        c.getPlayer().dropMessage(1, "解卡完毕.");
        c.getPlayer().dropMessage(6, "当前时间是" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 | 经验倍率 " + Math.round((float)c.getPlayer().getEXPMod()) * 100 * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, 爆率 " + Math.round((float)c.getPlayer().getDropMod()) * 100 * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, 金币倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100L + "%");
        c.getPlayer().dropMessage(6, "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒");
    }
    
    public static void UseDoor(final SeekableLittleEndianAccessor slea, final MapleCharacter chr) {
        final int oid = slea.readInt();
        final boolean mode = slea.readByte() == 0;
        for (final MapleMapObject obj : chr.getMap().getAllDoorsThreadsafe()) {
            final MapleDoor door = (MapleDoor)obj;
            if (door.getOwnerId() == oid) {
                door.warp(chr, mode);
                break;
            }
        }
    }
    
    public static void TransformPlayer(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        final byte slot = (byte)slea.readShort();
        final int itemId = slea.readInt();
        final String target = slea.readMapleAsciiString().toLowerCase();
        final IItem toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        switch (itemId) {
            case 2212000: {
                for (final MapleCharacter search_chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    if (search_chr.getName().toLowerCase().equals(target)) {
                        MapleItemInformationProvider.getInstance().getItemEffect(2210023).applyTo(search_chr);
                        search_chr.dropMessage(6, chr.getName() + " has played a prank on you!");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                    }
                }
                break;
            }
        }
    }
    
    public static void HitReactor(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int oid = slea.readInt();
        final int charPos = slea.readInt();
        final short stance = slea.readShort();
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
        if (reactor == null || !reactor.isAlive()) {
            return;
        }
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("[系统提示]你已攻击反应物" + reactor.getReactorId());
        }
        reactor.hitReactor(charPos, stance, c);
    }
    
    public static void TouchReactor(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int oid = slea.readInt();
        final boolean touched = slea.readByte() > 0;
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
        if (!touched || reactor == null || !reactor.isAlive() || reactor.getReactorId() < 6109013 || reactor.getReactorId() > 6109027 || reactor.getTouch() == 0) {
            return;
        }
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(5, "反应堆信息 - oid: " + oid + " Touch: " + reactor.getTouch() + " isTimerActive: " + reactor.isTimerActive() + " ReactorType: " + reactor.getReactorType());
        }
        if (reactor.getTouch() == 2) {
            ReactorScriptManager.getInstance().act(c, reactor);
        }
        else if (reactor.getTouch() == 1 && !reactor.isTimerActive()) {
            if (reactor.getReactorType() == 100) {
                final int itemid = GameConstants.getCustomReactItem(reactor.getReactorId(), reactor.getReactItem().getLeft());
                if (c.getPlayer().haveItem(itemid, reactor.getReactItem().getRight())) {
                    if (reactor.getArea().contains(c.getPlayer().getTruePosition())) {
                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, reactor.getReactItem().getRight(), true, false);
                        reactor.hitReactor(c);
                    }
                    else {
                        c.getPlayer().dropMessage(5, "距离太远。请靠近后重新尝试。");
                    }
                }
                else {
                    c.getPlayer().dropMessage(5, "你没有所需的物品.");
                }
            }
            else {
                reactor.hitReactor(c);
            }
        }
    }
    
    public static void hitCoconut(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int id = slea.readShort();
        String co = "椰子";
        MapleCoconut map = (MapleCoconut)c.getChannelServer().getEvent(MapleEventType.打椰子比赛);
        if (map == null || !map.isRunning()) {
            map = (MapleCoconut)c.getChannelServer().getEvent(MapleEventType.打瓶盖比赛);
            co = "瓶盖";
            if (map == null || !map.isRunning()) {
                return;
            }
        }
        final MapleCoconut.MapleCoconuts nut = map.getCoconut(id);
        if (nut == null || !nut.isHittable()) {
            return;
        }
        if (System.currentTimeMillis() < nut.getHitTime()) {
            return;
        }
        if (nut.getHits() > 2 && Math.random() < 0.4 && !nut.isStopped()) {
            nut.setHittable(false);
            if (Math.random() < 0.01 && map.getStopped() > 0) {
                nut.setStopped(true);
                map.stopCoconut();
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 1));
                return;
            }
            nut.resetHits();
            if (Math.random() < 0.05 && map.getBombings() > 0) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 2));
                map.bombCoconut();
            }
            else if (map.getFalling() > 0) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 3));
                map.fallCoconut();
                if (c.getPlayer().getTeam() == 0) {
                    map.addMapleScore();
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " 彩虹队成功打掉了一个 " + co + "."));
                }
                else {
                    map.addStoryScore();
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " 神秘队成功打掉一个 " + co + "."));
                }
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.coconutScore(map.getCoconutScore()));
            }
        }
        else {
            nut.hit();
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 1));
        }
    }
    
    public static void FollowRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        MapleCharacter tt = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (slea.readByte() > 0) {
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId()) {
                tt.setFollowOn(true);
                c.getPlayer().setFollowOn(true);
            }
            else {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (slea.readByte() > 0) {
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId() && c.getPlayer().isFollowOn()) {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000.0 && tt.getFollowId() == 0 && c.getPlayer().getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) {
            tt.setFollowId(c.getPlayer().getId());
            tt.setFollowOn(false);
            tt.setFollowInitiator(false);
            c.getPlayer().setFollowOn(false);
            c.getPlayer().setFollowInitiator(false);
        }
        else {
            c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "距离太远了."));
        }
    }
    
    public static void FollowReply(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getFollowId() > 0 && c.getPlayer().getFollowId() == slea.readInt()) {
            final MapleCharacter tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000.0 && tt.getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) {
                final boolean accepted = slea.readByte() > 0;
                if (accepted) {
                    tt.setFollowId(c.getPlayer().getId());
                    tt.setFollowOn(true);
                    tt.setFollowInitiator(true);
                    c.getPlayer().setFollowOn(true);
                    c.getPlayer().setFollowInitiator(false);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.followEffect(tt.getId(), c.getPlayer().getId(), null));
                }
                else {
                    c.getPlayer().setFollowId(0);
                    tt.setFollowId(0);
                }
            }
            else {
                if (tt != null) {
                    tt.setFollowId(0);
                    c.getPlayer().setFollowId(0);
                }
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "距离太远了."));
            }
        }
        else {
            c.getPlayer().setFollowId(0);
        }
    }
    
    public static void RingAction(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        if (mode == 0) {
            final String name = slea.readMapleAsciiString();
            final int itemid = slea.readInt();
            final int newItemId = 1112300 + (itemid - 2240004);
            final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
            int errcode = 0;
            if (c.getPlayer().getMarriageId() > 0) {
                errcode = 23;
            }
            else if (chr == null) {
                errcode = 18;
            }
            else if (chr.getMapId() != c.getPlayer().getMapId()) {
                errcode = 19;
            }
            else if (!c.getPlayer().haveItem(itemid, 1) || itemid < 2240004 || itemid > 2240015) {
                errcode = 13;
            }
            else if (chr.getMarriageId() > 0 || chr.getMarriageItemId() > 0) {
                errcode = 24;
            }
            else if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) {
                errcode = 20;
            }
            else if (!MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
                errcode = 21;
            }
            if (errcode > 0) {
                c.getSession().write((Object)MaplePacketCreator.sendEngagement((byte)errcode, 0, null, null));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            c.getPlayer().setMarriageItemId(itemid);
            chr.getClient().getSession().write((Object)MaplePacketCreator.sendEngagementRequest(c.getPlayer().getName(), c.getPlayer().getId()));
        }
        else if (mode == 1) {
            c.getPlayer().setMarriageItemId(0);
        }
        else if (mode == 2) {
            final boolean accepted = slea.readByte() > 0;
            final String name2 = slea.readMapleAsciiString();
            final int id = slea.readInt();
            final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name2);
            if (c.getPlayer().getMarriageId() > 0 || chr == null || chr.getId() != id || chr.getMarriageItemId() <= 0 || !chr.haveItem(chr.getMarriageItemId(), 1) || chr.getMarriageId() > 0) {
                c.getSession().write((Object)MaplePacketCreator.sendEngagement((byte)29, 0, null, null));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            if (accepted) {
                final int newItemId2 = 1112300 + (chr.getMarriageItemId() - 2240004);
                if (!MapleInventoryManipulator.checkSpace(c, newItemId2, 1, "") || !MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId2, 1, "")) {
                    c.getSession().write((Object)MaplePacketCreator.sendEngagement((byte)21, 0, null, null));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                MapleInventoryManipulator.addById(c, newItemId2, (short)1, (byte)0);
                MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, chr.getMarriageItemId(), 1, false, false);
                MapleInventoryManipulator.addById(chr.getClient(), newItemId2, (short)1, (byte)0);
                chr.getClient().getSession().write((Object)MaplePacketCreator.sendEngagement((byte)16, newItemId2, chr, c.getPlayer()));
                chr.setMarriageId(c.getPlayer().getId());
                c.getPlayer().setMarriageId(chr.getId());
            }
            else {
                chr.getClient().getSession().write((Object)MaplePacketCreator.sendEngagement((byte)30, 0, null, null));
            }
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            chr.setMarriageItemId(0);
        }
        else if (mode == 3) {
            final int itemId = slea.readInt();
            final MapleInventoryType type = GameConstants.getInventoryType(itemId);
            final IItem item = c.getPlayer().getInventory(type).findById(itemId);
            if (item != null && type == MapleInventoryType.ETC && itemId / 10000 == 421) {
                MapleInventoryManipulator.drop(c, type, item.getPosition(), item.getQuantity());
            }
        }
    }
    
    public static void UpdateCharInfo(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        if (type == 0) {
            final String charmessage = slea.readMapleAsciiString();
            c.getPlayer().setcharmessage(charmessage);
        }
        else if (type == 1) {
            final int expression = slea.readByte();
            c.getPlayer().setexpression(expression);
        }
        else if (type == 2) {
            final int blood = slea.readByte();
            final int month = slea.readByte();
            final int day = slea.readByte();
            final int constellation = slea.readByte();
            c.getPlayer().setblood(blood);
            c.getPlayer().setmonth(month);
            c.getPlayer().setday(day);
            c.getPlayer().setconstellation(constellation);
        }
    }
}
