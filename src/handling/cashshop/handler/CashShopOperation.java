package handling.cashshop.handler;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.OtherSettings;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;

public class CashShopOperation
{
    public static void LeaveCS(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final String[] socket = c.getChannelServer().getIP().split(":");
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        final String ip = c.getSessionIPAddress();
        LoginServer.putLoginAuth(chr.getId(), ip.substring(ip.indexOf(47) + 1, ip.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(1, ip);
        try {
            chr.saveToDB(false, true);
            c.setReceiving(false);
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write((Object)MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void EnterCS(final int playerid, final MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                c.getSession().close();
                return;
            }
        }
        final MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) {
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if ((state == 1 || state == 6) && !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        if (mts) {
            CashShopServer.getPlayerStorageMTS().registerPlayer(chr);
            c.getSession().write((Object)MTSCSPacket.startMTS(chr, c));
            MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
        }
        else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write((Object)MTSCSPacket.warpCS(c));
            CSUpdate(c);
        }
    }
    
    public static void CSUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashInventory(c));
        c.getSession().write((Object)MTSCSPacket.sendWishList(c.getPlayer(), false));
        c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write((Object)MTSCSPacket.getCSGifts(c));
    }
    
    public static void TouchingCashShop(final MapleClient c) {
        c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
    }
    
    public static void CouponCode(final String code, final MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;
        try {
            validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (validcode) {
            try {
                type = MapleCharacterUtil.getNXCodeType(code);
                item = MapleCharacterUtil.getNXCodeItem(code);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            if (type != 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            final Map<Integer, IItem> itemz = new HashMap<Integer, IItem>();
            int maplePoints = 0;
            int mesos = 0;
            switch (type) {
                case 1:
                case 2: {
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                }
                case 3: {
                    final CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    }
                    final byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short)1, "", (byte)0);
                    if (slot <= -1) {
                        c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    }
                    itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    break;
                }
                case 4: {
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    break;
                }
                case 5: {
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
                }
            }
            c.getSession().write((Object)MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        }
        else {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(validcode ? 165 : 167));
        }
        doCSPackets(c);
    }
    
    public static void BuyCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final OtherSettings item_id = new OtherSettings();
        final String[] itembp_id = item_id.getItempb_id();
        final String[] itemjy_id = item_id.getItemjy_id();
        final int action = slea.readByte();
        if (action == 3) {
            final int useNX = slea.readByte() + 1;
            final int snCS = slea.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(snCS);
            if (item == null) {
                chr.dropMessage(1, "该物品暂未开放！");
                doCSPackets(c);
                return;
            }
            for (int i = 0; i < itembp_id.length; ++i) {
                if (item.getId() == Integer.parseInt(itembp_id[i])) {
                    c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
            }
            if (item.getPrice() < 100) {
                c.getPlayer().dropMessage(1, "价格(" + item.getPrice() + ")低于100点卷的物品是禁止购买的.");
                doCSPackets(c);
                return;
            }
            if (item != null && chr.getCSPoints(useNX) >= item.getPrice()) {
                chr.modifyCSPoints(useNX, -item.getPrice(), false);
                final IItem itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    if (useNX == 1) {
                        byte flag = itemz.getFlag();
                        boolean 交易 = true;
                        for (int j = 0; j < itemjy_id.length; ++j) {
                            if (itemz.getItemId() == Integer.parseInt(itemjy_id[j])) {
                                交易 = false;
                            }
                        }
                        if (交易) {
                            if (itemz.getType() == MapleInventoryType.EQUIP.getType()) {
                                flag |= (byte)ItemFlag.KARMA_EQ.getValue();
                            }
                            else {
                                flag |= (byte)ItemFlag.KARMA_USE.getValue();
                            }
                            itemz.setFlag(flag);
                        }
                    }
                    chr.getCashInventory().addToInventory(itemz);
                    c.getSession().write((Object)MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                }
                else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                }
            }
            else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            }
            c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
        else if (action == 4 || action == 32) {
            final int snCS2 = slea.readInt();
            final int type = slea.readByte() + 1;
            final String recipient = slea.readMapleAsciiString();
            final String message = slea.readMapleAsciiString();
            final CashItemInfo item2 = CashItemFactory.getInstance().getItem(snCS2);
            final IItem itemz2 = chr.getCashInventory().toItem(item2);
            if (c.getPlayer().isAdmin()) {
                System.out.println("包裹购买 ID: " + snCS2);
            }
            if (item2.getPrice() < 100) {
                c.getPlayer().dropMessage(1, "价格低于100点卷的物品是禁止购买的.");
                doCSPackets(c);
                return;
            }
            if (itemz2 != null && itemz2.getUniqueId() > 0 && itemz2.getItemId() == item2.getId() && itemz2.getQuantity() == item2.getCount()) {
                if (item2 == null || c.getPlayer().getCSPoints(type) < item2.getPrice() || message.length() > 73 || message.length() < 1) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                    doCSPackets(c);
                    return;
                }
                final Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(recipient, c.getPlayer().getWorld());
                if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getRight().getLeft() == c.getAccID()) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(162));
                    doCSPackets(c);
                    return;
                }
                if (!item2.genderEquals(info.getRight().getRight())) {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(163));
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), message, item2.getSN(), MapleInventoryIdentifier.getInstance());
                c.getPlayer().modifyCSPoints(type, -item2.getPrice(), false);
                c.getSession().write((Object)MTSCSPacket.sendGift(item2.getId(), item2.getCount(), recipient));
            }
            else {
                c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                doCSPackets(c);
            }
        }
        else if (action == 5) {
            chr.clearWishlist();
            if (slea.available() < 40L) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            final int[] wishlist = new int[10];
            for (int k = 0; k < 10; ++k) {
                wishlist[k] = slea.readInt();
            }
            chr.setWishlist(wishlist);
            c.getSession().write((Object)MTSCSPacket.sendWishList(chr, true));
        }
        else if (action == 6) {
            final int useNX = slea.readByte() + 1;
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type2 = getInventoryType(slea.readInt());
                if (chr.getCSPoints(useNX) >= 4800 && chr.getInventory(type2).getSlotLimit() < 89) {
                    chr.modifyCSPoints(useNX, -4800, false);
                    chr.getInventory(type2).addSlot((byte)8);
                    chr.dropMessage(1, "背包已增加到 " + chr.getInventory(type2).getSlotLimit());
                }
                else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
                }
            }
            else {
                final MapleInventoryType type2 = MapleInventoryType.getByType(slea.readByte());
                if (chr.getCSPoints(useNX) >= 600 && chr.getInventory(type2).getSlotLimit() < 93) {
                    chr.modifyCSPoints(useNX, -600, false);
                    chr.getInventory(type2).addSlot((byte)4);
                    chr.dropMessage(1, "背包已增加到 " + chr.getInventory(type2).getSlotLimit());
                }
                else {
                    c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
                }
            }
        }
        else if (action == 7) {
            if (chr.getCSPoints(1) >= 600 && chr.getStorage().getSlots() < 45) {
                chr.modifyCSPoints(1, -600, false);
                chr.getStorage().increaseSlots((byte)4);
                chr.getStorage().saveToDB();
            }
            else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(164));
            }
            RefreshCashShop(c);
        }
        else if (action == 8) {
            final int useNX = slea.readByte() + 1;
            final CashItemInfo item3 = CashItemFactory.getInstance().getItem(slea.readInt());
            final int slots = c.getCharacterSlots();
            if (slots > 15) {
                chr.dropMessage(1, "角色列表已满无法增加！");
            }
            if (item3 == null || c.getPlayer().getCSPoints(useNX) < item3.getPrice() || slots > 15) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            c.getPlayer().modifyCSPoints(useNX, -item3.getPrice(), false);
            if (c.gainCharacterSlot()) {
                c.getSession().write((Object)MTSCSPacket.increasedStorageSlots(slots + 1));
                chr.dropMessage(1, "角色列表已增加到：" + c.getCharacterSlots() + "个");
            }
            else {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
            }
        }
        else if (action == 13) {
            final int uniqueid = slea.readInt();
            slea.readInt();
            slea.readByte();
            final byte type3 = slea.readByte();
            final byte unknown = slea.readByte();
            final IItem item4 = c.getPlayer().getCashInventory().findByCashId(uniqueid);
            if (item4 != null && item4.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item4.getItemId(), item4.getQuantity(), item4.getOwner())) {
                final IItem item_ = item4.copy();
                final byte slot = (byte)MapleInventoryManipulator.addbyItem(c, item_, true);
                if (slot >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(type3);
                        c.getPlayer().addPet(item_.getPet());
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item4);
                    c.getSession().write((Object)MTSCSPacket.confirmFromCSInventory(item_, type3));
                }
                else {
                    c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "您的包裹已满."));
                }
            }
            else {
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "放入背包错误A."));
            }
        }
        else if (action == 14) {
            final int uniqueid = (int)slea.readLong();
            final MapleInventoryType type4 = MapleInventoryType.getByType(slea.readByte());
            final IItem item5 = c.getPlayer().getInventory(type4).findByUniqueId(uniqueid);
            if (item5 != null && item5.getQuantity() > 0 && item5.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                final IItem item_2 = item5.copy();
                c.getPlayer().getInventory(type4).removeItem(item5.getPosition(), item5.getQuantity(), false);
                final int sn = CashItemFactory.getInstance().getItemSN(item_2.getItemId());
                if (item_2.getPet() != null) {
                    c.getPlayer().removePet(item_2.getPet());
                }
                item_2.setPosition((short)0);
                item_2.setGMLog("购物商场购买: " + FileoutputUtil.CurrentReadable_Time());
                c.getPlayer().getCashInventory().addToInventory(item_2);
                c.sendPacket(MTSCSPacket.confirmToCSInventory(item5, c.getAccID(), sn));
            }
            else {
                c.sendPacket(MTSCSPacket.sendCSFail(177));
            }
            RefreshCashShop(c);
        }
        else if (action == 36 || action == 29) {
            int sn2 = slea.readInt();
            if (sn2 == 209000310) {
                sn2 = 20900026;
            }
            final CashItemInfo item3 = CashItemFactory.getInstance().getItem(sn2);
            final String partnerName = slea.readMapleAsciiString();
            final String msg = slea.readMapleAsciiString();
            final IItem itemz3 = chr.getCashInventory().toItem(item3);
            for (int l = 0; l < itembp_id.length; ++l) {
                if (item3.getId() == Integer.parseInt(itembp_id[l])) {
                    c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
            }
            if (item3 == null || !GameConstants.isEffectRing(item3.getId()) || c.getPlayer().getCSPoints(1) < item3.getPrice() || msg.length() > 73 || msg.length() < 1) {
                chr.dropMessage(1, "购买戒指错误：\r\n你没有足够的点卷或者该物品不存在。。");
                doCSPackets(c);
                return;
            }
            if (!item3.genderEquals(c.getPlayer().getGender())) {
                chr.dropMessage(1, "购买戒指错误：B\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                chr.dropMessage(1, "购买戒指错误：C\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            if (item3.getPrice() == 2990) {}
            final Pair<Integer, Pair<Integer, Integer>> info2 = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info2 == null || info2.getLeft() <= 0 || info2.getLeft() == c.getPlayer().getId()) {
                chr.dropMessage(1, "购买戒指错误：D\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            if (info2.getRight().getLeft() == c.getAccID()) {
                chr.dropMessage(1, "购买戒指错误：E\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            if (info2.getRight().getRight() == c.getPlayer().getGender() && action == 29) {
                chr.dropMessage(1, "购买戒指错误：F\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            final int err = MapleRing.createRing(item3.getId(), c.getPlayer(), partnerName, msg, info2.getLeft(), item3.getSN());
            if (err != 1) {
                chr.dropMessage(1, "购买戒指错误：G\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            c.getPlayer().modifyCSPoints(1, -item3.getPrice(), false);
            doCSPackets(c);
            return;
        }
        else if (action == 31) {
            final int type5 = slea.readByte() + 1;
            final int snID = slea.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(snID);
            for (int i = 0; i < itembp_id.length; ++i) {
                if (snID == Integer.parseInt(itembp_id[i])) {
                    c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                    doCSPackets(c);
                    return;
                }
            }
            if (c.getPlayer().isAdmin()) {
                System.out.println("礼包购买 ID: " + snID);
            }
            switch (snID) {
                case 10001818: {
                    c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
                    doCSPackets(c);
                    break;
                }
            }
            List<CashItemInfo> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getPlayer().getCSPoints(type5) < item.getPrice()) {
                chr.dropMessage(1, "购买礼包错误：\r\n你没有足够的点卷或者该物品不存在。");
                doCSPackets(c);
                return;
            }
            if (!item.genderEquals(c.getPlayer().getGender())) {
                chr.dropMessage(1, "购买礼包错误：B\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getCashInventory().getItemsSize() >= 100 - ccc.size()) {
                chr.dropMessage(1, "购买礼包错误：C\r\n请联系GM！。");
                doCSPackets(c);
                return;
            }
            final Map<Integer, IItem> ccz = new HashMap<Integer, IItem>();
            for (final CashItemInfo m : ccc) {
                for (final int iz : GameConstants.cashBlock) {
                    if (m.getId() == iz) {}
                }
                final IItem itemz4 = chr.getCashInventory().toItem(m, chr, MapleInventoryManipulator.getUniqueId(m.getId(), null), "");
                if (itemz4 != null && itemz4.getUniqueId() > 0) {
                    if (itemz4.getItemId() != m.getId()) {
                        continue;
                    }
                    ccz.put(m.getSN(), itemz4);
                    c.getPlayer().getCashInventory().addToInventory(itemz4);
                    c.getSession().write((Object)MTSCSPacket.showBoughtCSItem(itemz4, item.getSN(), c.getAccID()));
                }
            }
            chr.modifyCSPoints(type5, -item.getPrice(), false);
        }
        else if (action == 42) {
            final int snCS2 = slea.readInt();
            if (snCS2 == 50200031 && c.getPlayer().getCSPoints(1) >= 500) {
                c.getPlayer().modifyCSPoints(1, -500);
                c.getPlayer().modifyCSPoints(2, 500);
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "兑换500抵用卷成功"));
            }
            else if (snCS2 == 50200032 && c.getPlayer().getCSPoints(1) >= 1000) {
                c.getPlayer().modifyCSPoints(1, -1000);
                c.getPlayer().modifyCSPoints(2, 1000);
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "兑换抵1000用卷成功"));
            }
            else if (snCS2 == 50200033 && c.getPlayer().getCSPoints(1) >= 5000) {
                c.getPlayer().modifyCSPoints(1, -5000);
                c.getPlayer().modifyCSPoints(2, 5000);
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "兑换5000抵用卷成功"));
            }
            else {
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "没有找到这个道具的信息！\r\n或者你点卷不足无法兑换！"));
            }
            c.getSession().write((Object)MTSCSPacket.enableCSorMTS());
            c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
        else if (action == 33) {
            final int 关闭 = 1;
            if (关闭 == 1) {
                chr.dropMessage(1, "暂不支持。");
                c.getPlayer().saveToDB(true, true);
                c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            final CashItemInfo item3 = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item3 == null || !MapleItemInformationProvider.getInstance().isQuestItem(item3.getId())) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getMeso() < item3.getPrice()) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(184));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getInventory(GameConstants.getInventoryType(item3.getId())).getNextFreeSlot() < 0) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(177));
                doCSPackets(c);
                return;
            }
            for (final int iz2 : GameConstants.cashBlock) {
                if (item3.getId() == iz2) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item3.getId()));
                    doCSPackets(c);
                    return;
                }
            }
            final byte pos = MapleInventoryManipulator.addId(c, item3.getId(), (short)item3.getCount(), null, (byte)0);
            if (pos < 0) {
                c.getSession().write((Object)MTSCSPacket.sendCSFail(177));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item3.getPrice(), false);
            c.getSession().write((Object)MTSCSPacket.showBoughtCSQuestItem(item3.getPrice(), (short)item3.getCount(), pos, item3.getId()));
        }
        else {
            c.getSession().write((Object)MTSCSPacket.sendCSFail(0));
        }
        doCSPackets(c);
    }
    
    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200075: {
                return MapleInventoryType.EQUIP;
            }
            case 50200074: {
                return MapleInventoryType.USE;
            }
            case 50200073: {
                return MapleInventoryType.ETC;
            }
            default: {
                return MapleInventoryType.UNDEFINED;
            }
        }
    }
    
    private static final void RefreshCashShop(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashInventory(c));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
    
    private static final void doCSPackets(final MapleClient c) {
        c.getSession().write((Object)MTSCSPacket.getCSInventory(c));
        c.getSession().write((Object)MTSCSPacket.enableCSorMTS());
        c.getSession().write((Object)MTSCSPacket.sendWishList(c.getPlayer(), false));
        c.getSession().write((Object)MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write((Object)MaplePacketCreator.enableActions());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
