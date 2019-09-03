package handling.channel.handler;

import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.OtherSettings;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.maps.FieldLimitType;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShop;
import server.shops.MaplePlayerShopItem;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.PlayerShopPacket;

public class PlayerInteractionHandler
{
    private static final byte CREATE = 0;
    private static final byte INVITE_TRADE = 2;
    private static final byte DENY_TRADE = 3;
    private static final byte VISIT = 4;
    private static final byte CHAT = 6;
    private static final byte EXIT = 10;
    private static final byte OPEN = 11;
    private static final byte CASH_ITEM_INTER = 13;
    private static final byte SET_ITEMS = 14;
    private static final byte SET_MESO = 15;
    private static final byte CONFIRM_TRADE = 16;
    private static final byte TRADE_SOMETHING = 18;
    private static final byte PLAYER_SHOP_ADD_ITEM = 20;
    private static final byte BUY_ITEM_PLAYER_SHOP = 21;
    private static final byte MERCHANT_EXIT = 27;
    private static final byte ADD_ITEM = 31;
    private static final byte BUY_ITEM_HIREDMERCHANT = 32;
    private static final byte BUY_ITEM_STORE = 33;
    private static final byte REMOVE_ITEM = 35;
    private static final byte TAKE_ITEM_BACK = 36;
    private static final byte MAINTANCE_OFF = 37;
    private static final byte MAINTANCE_ORGANISE = 38;
    private static final byte CLOSE_MERCHANT = 39;
    private static final byte ADMIN_STORE_NAMECHANGE = 43;
    private static final byte VIEW_MERCHANT_VISITOR = 44;
    private static final byte VIEW_MERCHANT_BLACKLIST = 45;
    private static final byte MERCHANT_BLACKLIST_ADD = 46;
    private static final byte MERCHANT_BLACKLIST_REMOVE = 47;
    private static final byte REQUEST_TIE = 48;
    private static final byte ANSWER_TIE = 49;
    private static final byte GIVE_UP = 50;
    private static final byte REQUEST_REDO = 53;
    private static final byte ANSWER_REDO = 54;
    private static final byte EXIT_AFTER_GAME = 55;
    private static final byte CANCEL_EXIT = 56;
    private static final byte READY = 57;
    private static final byte UN_READY = 58;
    private static final byte EXPEL = 59;
    private static final byte START = 60;
    private static final byte SKIP = 62;
    private static final byte MOVE_OMOK = 63;
    private static final byte SELECT_CARD = 67;
    
    public static final void PlayerInteraction(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final byte action = slea.readByte();
        switch (action) {
            case 0: {
                final byte createType = slea.readByte();
                if (createType == 3) {
                    MapleTrade.startTrade(chr);
                    break;
                }
                if (createType != 1 && createType != 2 && createType != 4 && createType != 5) {
                    break;
                }
                if (createType == 4 && !chr.isAdmin()) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if (chr.getMap().getMapObjectsInRange(chr.getPosition(), 20000.0, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).size() != 0) {
                    chr.dropMessage(1, "\u4f60\u4e0d\u53ef\u80fd\u5728\u8fd9\u91cc\u5efa\u7acb\u4e00\u4e2a\u5546\u5e97.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if ((createType == 1 || createType == 2) && FieldLimitType.Minigames.check(chr.getMap().getFieldLimit())) {
                    chr.dropMessage(1, "\u4f60\u4e0d\u53ef\u4ee5\u5728\u8fd9\u91cc\u4f7f\u7528\u7684\u8ff7\u4f60\u6e38\u620f\u3002");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                final String desc = slea.readMapleAsciiString();
                String pass = "";
                if (slea.readByte() > 0 && (createType == 1 || createType == 2)) {
                    pass = slea.readMapleAsciiString();
                }
                if (createType == 1 || createType == 2) {
                    final int piece = slea.readByte();
                    final int itemId = (createType == 1) ? (4080000 + piece) : 4080100;
                    if (!chr.haveItem(itemId) || (c.getPlayer().getMapId() >= 910000001 && c.getPlayer().getMapId() <= 910000022)) {
                        return;
                    }
                    final MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType);
                    game.setPieceType(piece);
                    chr.setPlayerShop(game);
                    game.setAvailable(true);
                    game.setOpen(true);
                    game.send(c);
                    chr.getMap().addMapObject(game);
                    game.update();
                }
                else {
                    final IItem shop = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)slea.readShort());
                    if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != slea.readInt() || c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022) {
                        return;
                    }
                    if (createType == 4) {
                        final MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                        chr.setPlayerShop(mps);
                        chr.getMap().addMapObject(mps);
                        c.getSession().write((Object)PlayerShopPacket.getPlayerStore(chr, true));
                    }
                    else {
                        final HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                        chr.setPlayerShop(merch);
                        chr.getMap().addMapObject(merch);
                        c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merch, true));
                    }
                }
                break;
            }
            case 2: {
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById(slea.readInt()));
                break;
            }
            case 3: {
                MapleTrade.declineTrade(chr);
                break;
            }
            case 13: {
                final byte \u7c7b\u578b = slea.readByte();
                final byte \u73b0\u91d1\u4ea4\u6613 = slea.readByte();
                if (\u7c7b\u578b == 11 && \u73b0\u91d1\u4ea4\u6613 == 5) {
                    c.getPlayer().dropMessage(1, "\u8bf7\u5148\u653e\u5165\u4e00\u4e2a\u4e0d\u662f\u73b0\u91d1\u7269\u54c1\u7684\u4e1c\u897f\u8d29\u5356\r\n\u5f00\u542f\u5546\u5e97\u540e\u7ba1\u7406\u5546\u5e97\u653e\u5165\u73b0\u91d1\u7269\u54c1\uff01");
                    return;
                }
                final int \u672a\u77e5\u7c7b\u578b = slea.readInt();
                final int obid = slea.readInt();
                final MapleCharacter otherChar = c.getPlayer().getMap().getCharacterById(obid);
                MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                if (\u73b0\u91d1\u4ea4\u6613 == 6 && \u7c7b\u578b == 4 && c.getPlayer().getTrade() != null && c.getPlayer().getTrade().getPartner() != null) {
                    MapleTrade.visit\u73b0\u91d1\u4ea4\u6613(chr, chr.getTrade().getPartner().getChr());
                    try {
                        c.getPlayer().dropMessage(6, "\u73a9\u5bb6 " + otherChar.getName() + "  \u63a5\u53d7\u73b0\u91d1\u4ea4\u6613\u9080\u8bf7!");
                    }
                    catch (Exception ex) {}
                    break;
                }
                if (\u73b0\u91d1\u4ea4\u6613 == 6 && \u7c7b\u578b != 4) {
                    MapleTrade.start\u73b0\u91d1\u4ea4\u6613(chr);
                    MapleTrade.invite\u73b0\u91d1\u4ea4\u6613(chr, otherChar);
                    c.getPlayer().dropMessage(6, "\u5411\u73a9\u5bb6 " + otherChar.getName() + "  \u53d1\u9001\u73b0\u91d1\u4ea4\u6613\u9080\u8bf7!");
                    break;
                }
                if (chr.getMap() == null) {
                    break;
                }
                if (ob == null) {
                    ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                }
                if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                    final IMaplePlayerShop ips = (IMaplePlayerShop)ob;
                    if (ob instanceof HiredMerchant) {
                        final HiredMerchant merchant = (HiredMerchant)ips;
                        if (merchant.isOwner(chr)) {
                            merchant.setOpen(false);
                            merchant.broadcastToVisitors(PlayerShopPacket.shopErrorMessage(13, 1), false);
                            merchant.removeAllVisitors(16, 0);
                            chr.setPlayerShop(ips);
                            c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant, false));
                        }
                        else if (!merchant.isOpen() || !merchant.isAvailable()) {
                            chr.dropMessage(1, "\u4e3b\u4eba\u6b63\u5728\u6574\u7406\u5546\u5e97\u7269\u54c1\r\n\u8bf7\u7a0d\u540e\u518d\u5ea6\u5149\u4e34\uff01");
                        }
                        else if (ips.getFreeSlot() == -1) {
                            chr.dropMessage(1, "\u5546\u5e97\u4eba\u6570\u5df2\u7ecf\u6ee1\u4e86,\u8bf7\u7a0d\u540e\u518d\u8fdb\u5165");
                        }
                        else if (merchant.isInBlackList(chr.getName())) {
                            chr.dropMessage(1, "\u4f60\u88ab\u8fd9\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u5355\u4e86,\u6240\u4ee5\u4e0d\u80fd\u8fdb\u5165");
                        }
                        else {
                            chr.setPlayerShop(ips);
                            merchant.addVisitor(chr);
                            c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant, false));
                        }
                    }
                    else {
                        if (ips instanceof MaplePlayerShop && ((MaplePlayerShop)ips).isBanned(chr.getName())) {
                            chr.dropMessage(1, "\u4f60\u88ab\u8fd9\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u5355\u4e86,\u6240\u4ee5\u4e0d\u80fd\u8fdb\u5165.");
                            return;
                        }
                        if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                            c.getSession().write((Object)PlayerShopPacket.getMiniGameFull());
                        }
                        else {
                            if (slea.available() > 0L && slea.readByte() > 0) {
                                final String pass2 = slea.readMapleAsciiString();
                                if (!pass2.equals(ips.getPassword())) {
                                    c.getPlayer().dropMessage(1, "\u4f60\u8f93\u5165\u7684\u5bc6\u7801\u9519\u8bef.\u8bf7\u4ece\u65b0\u5728\u8bd5\u4e00\u6b21.");
                                    return;
                                }
                            }
                            else if (ips.getPassword().length() > 0) {
                                c.getPlayer().dropMessage(1, "\u4f60\u8f93\u5165\u7684\u5bc6\u7801\u9519\u8bef.\u8bf7\u4ece\u65b0\u5728\u8bd5\u4e00\u6b21.");
                                return;
                            }
                            chr.setPlayerShop(ips);
                            ips.addVisitor(chr);
                            if (ips instanceof MapleMiniGame) {
                                ((MapleMiniGame)ips).send(c);
                            }
                            else {
                                c.getSession().write((Object)PlayerShopPacket.getPlayerStore(chr, false));
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 4: {
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                    break;
                }
                if (chr.getMap() != null) {
                    final int obid2 = slea.readInt();
                    MapleMapObject ob2 = chr.getMap().getMapObject(obid2, MapleMapObjectType.HIRED_MERCHANT);
                    if (ob2 == null) {
                        ob2 = chr.getMap().getMapObject(obid2, MapleMapObjectType.SHOP);
                    }
                    if (ob2 instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        final IMaplePlayerShop ips2 = (IMaplePlayerShop)ob2;
                        if (ob2 instanceof HiredMerchant) {
                            final HiredMerchant merchant2 = (HiredMerchant)ips2;
                            if (merchant2.isOwner(chr)) {
                                merchant2.setOpen(false);
                                merchant2.removeAllVisitors(16, 0);
                                chr.setPlayerShop(ips2);
                                c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant2, false));
                            }
                            else if (!merchant2.isOpen() || !merchant2.isAvailable()) {
                                chr.dropMessage(1, "\u8fd9\u4e2a\u5546\u5e97\u6b63\u5728\u6574\u7406\u6216\u8005\u662f\u6ca1\u6709\u518d\u8d29\u5356\u4e1c\u897f");
                            }
                            else if (ips2.getFreeSlot() == -1) {
                                chr.dropMessage(1, "\u5546\u5e97\u4eba\u6570\u5df2\u7ecf\u6ee1\u4e86\uff0c\u8bf7\u7a0d\u540e\u5728\u8fdb\u5165");
                            }
                            else if (merchant2.isInBlackList(chr.getName())) {
                                chr.dropMessage(1, "\u4f60\u5df2\u7ecf\u88ab\u8fd9\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u5355\uff0c\u6240\u4ee5\u4e0d\u80fd\u8fdb\u5165");
                            }
                            else {
                                chr.setPlayerShop(ips2);
                                merchant2.addVisitor(chr);
                                c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant2, false));
                            }
                        }
                        else {
                            if (ips2 instanceof MaplePlayerShop && ((MaplePlayerShop)ips2).isBanned(chr.getName())) {
                                chr.dropMessage(1, "\u4f60\u88ab\u8fd9\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u5355\u4e86,\u6240\u4ee5\u4e0d\u80fd\u8fdb\u5165.");
                                return;
                            }
                            if (ips2.getFreeSlot() < 0 || ips2.getVisitorSlot(chr) > -1 || !ips2.isOpen() || !ips2.isAvailable()) {
                                c.getSession().write((Object)PlayerShopPacket.getMiniGameFull());
                            }
                            else {
                                if (slea.available() > 0L && slea.readByte() > 0) {
                                    final String pass3 = slea.readMapleAsciiString();
                                    if (!pass3.equals(ips2.getPassword())) {
                                        c.getPlayer().dropMessage(1, "\u4f60\u8f93\u5165\u7684\u5bc6\u7801\u9519\u8bef.\u8bf7\u4ece\u65b0\u5728\u8bd5\u4e00\u6b21");
                                        return;
                                    }
                                }
                                else if (ips2.getPassword().length() > 0) {
                                    c.getPlayer().dropMessage(1, "\u4f60\u8f93\u5165\u7684\u5bc6\u7801\u9519\u8bef.\u8bf7\u4ece\u65b0\u5728\u8bd5\u4e00\u6b21.");
                                    return;
                                }
                                chr.setPlayerShop(ips2);
                                ips2.addVisitor(chr);
                                if (ips2 instanceof MapleMiniGame) {
                                    ((MapleMiniGame)ips2).send(c);
                                }
                                else {
                                    c.getSession().write((Object)PlayerShopPacket.getPlayerStore(chr, false));
                                }
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 6: {
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(slea.readMapleAsciiString());
                    break;
                }
                if (chr.getPlayerShop() != null) {
                    final IMaplePlayerShop ips3 = chr.getPlayerShop();
                    ips3.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + slea.readMapleAsciiString(), ips3.getVisitorSlot(chr)));
                    break;
                }
                break;
            }
            case 10: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                    break;
                }
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null) {
                    return;
                }
                if (!ips3.isAvailable() || (ips3.isOwner(chr) && ips3.getShopType() != 1)) {
                    ips3.closeShop(false, ips3.isAvailable());
                }
                else {
                    ips3.removeVisitor(chr);
                }
                chr.setPlayerShop(null);
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case 11: {
                final IMaplePlayerShop shop2 = chr.getPlayerShop();
                if (shop2 == null || !shop2.isOwner(chr) || shop2.getShopType() >= 3) {
                    break;
                }
                if (!chr.getMap().allowPersonalShop()) {
                    c.getSession().close();
                    break;
                }
                if (c.getChannelServer().isShutdown()) {
                    chr.dropMessage(1, "\u4f3a\u670d\u5668\u5373\u5c06\u5173\u95ed\u6240\u4ee5\u4e0d\u80fd\u6574\u7406\u5546\u5e97.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    shop2.closeShop(shop2.getShopType() == 1, false);
                    return;
                }
                if (shop2.getShopType() == 1) {
                    final HiredMerchant merchant3 = (HiredMerchant)shop2;
                    merchant3.setStoreid(c.getChannelServer().addMerchant(merchant3));
                    merchant3.setOpen(true);
                    merchant3.setAvailable(true);
                    chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant3));
                    chr.setPlayerShop(null);
                    chr.setLastHM(System.currentTimeMillis());
                    break;
                }
                if (shop2.getShopType() == 2) {
                    shop2.setOpen(true);
                    shop2.setAvailable(true);
                    shop2.update();
                    break;
                }
                break;
            }
            case 14: {
                final OtherSettings item_id = new OtherSettings();
                final String[] itemgy_id = item_id.getItempb_id();
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                final IItem item = chr.getInventory(ivType).getItem((byte)slea.readShort());
                final short quantity = slea.readShort();
                final byte targetSlot = slea.readByte();
                for (int i = 0; i < itemgy_id.length; ++i) {
                    if (item.getItemId() == Integer.parseInt(itemgy_id[i])) {
                        c.getPlayer().dropMessage(1, "\u8fd9\u4e2a\u7269\u54c1\u662f\u7981\u6b62\u96c7\u4f63\u8d29\u5356\u7684.");
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                }
                if (chr.getTrade() != null && item != null && ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()))) {
                    chr.getTrade().setItems(c, item, targetSlot, quantity);
                    break;
                }
                break;
            }
            case 15: {
                final MapleTrade trade = chr.getTrade();
                if (trade != null) {
                    trade.setMeso(slea.readInt());
                    break;
                }
                break;
            }
            case 16: {
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                    break;
                }
                break;
            }
            case 20:
            case 31: {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                final byte slot = (byte)slea.readShort();
                final short bundles = slea.readShort();
                final short perBundle = slea.readShort();
                final int price = slea.readInt();
                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return;
                }
                final IMaplePlayerShop shop3 = chr.getPlayerShop();
                if (shop3 == null || !shop3.isOwner(chr) || shop3 instanceof MapleMiniGame) {
                    return;
                }
                final IItem ivItem = chr.getInventory(type).getItem(slot);
                final MapleItemInformationProvider ii2 = MapleItemInformationProvider.getInstance();
                if (ivItem == null) {
                    break;
                }
                final long check = bundles * perBundle;
                if (check > 32767L || check <= 0L) {
                    return;
                }
                final short bundles_perbundle = (short)(bundles * perBundle);
                if (ivItem.getQuantity() >= bundles_perbundle) {
                    final byte flag = ivItem.getFlag();
                    if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    if ((ii2.isDropRestricted(ivItem.getItemId()) || ii2.isAccountShared(ivItem.getItemId())) && !ItemFlag.KARMA_EQ.check(flag) && !ItemFlag.KARMA_USE.check(flag)) {
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return;
                    }
                    if (bundles_perbundle >= 50 && GameConstants.isUpgradeScroll(ivItem.getItemId())) {
                        c.setMonitored(true);
                    }
                    if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);
                        final IItem sellItem = ivItem.copy();
                        shop3.addItem(new MaplePlayerShopItem(sellItem, (short)1, price, sellItem.getFlag()));
                    }
                    else {
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);
                        final IItem sellItem = ivItem.copy();
                        sellItem.setQuantity(perBundle);
                        shop3.addItem(new MaplePlayerShopItem(sellItem, bundles, price, sellItem.getFlag()));
                    }
                    c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(shop3));
                }
                break;
            }
            case 21:
            case 32:
            case 33: {
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                    break;
                }
                final int item2 = slea.readByte();
                final short quantity2 = slea.readShort();
                final IMaplePlayerShop shop4 = chr.getPlayerShop();
                if (shop4 == null || shop4.isOwner(chr) || shop4 instanceof MapleMiniGame || item2 >= shop4.getItems().size()) {
                    return;
                }
                final MaplePlayerShopItem tobuy = shop4.getItems().get(item2);
                if (tobuy == null) {
                    return;
                }
                final long check2 = tobuy.bundles * quantity2;
                final long check3 = tobuy.price * quantity2;
                final long check4 = tobuy.item.getQuantity() * quantity2;
                if (check2 <= 0L || check3 > 2147483647L || check3 <= 0L || check4 > 32767L || check4 < 0L) {
                    return;
                }
                if (tobuy.bundles < quantity2 || (tobuy.bundles % quantity2 != 0 && GameConstants.isEquip(tobuy.item.getItemId())) || chr.getMeso() - check3 < 0L || chr.getMeso() - check3 > 2147483647L || shop4.getMeso() + check3 < 0L || shop4.getMeso() + check3 > 2147483647L) {
                    return;
                }
                if (quantity2 < 50 || tobuy.item.getItemId() == 2340000) {}
                shop4.buy(c, item2, quantity2);
                shop4.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop4));
                break;
            }
            case 35:
            case 36: {
                final int slot2 = slea.readShort();
                final IMaplePlayerShop shop5 = chr.getPlayerShop();
                if (shop5 == null || !shop5.isOwner(chr) || shop5 instanceof MapleMiniGame || shop5.getItems().size() <= 0 || shop5.getItems().size() <= slot2 || slot2 < 0) {
                    return;
                }
                final MaplePlayerShopItem item3 = shop5.getItems().get(slot2);
                if (item3 != null && item3.bundles > 0) {
                    final IItem item_get = item3.item.copy();
                    final long check2 = item3.bundles * item3.item.getQuantity();
                    if (check2 <= 0L || check2 > 32767L) {
                        return;
                    }
                    item_get.setQuantity((short)check2);
                    if (item_get.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item3.item.getItemId())) {
                        c.setMonitored(true);
                    }
                    if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                        MapleInventoryManipulator.addFromDrop(c, item_get, false);
                        item3.bundles = 0;
                        shop5.removeFromSlot(slot2);
                    }
                }
                c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(shop5));
                break;
            }
            case 37: {
                final IMaplePlayerShop shop2 = chr.getPlayerShop();
                if (shop2 != null && shop2 instanceof HiredMerchant && shop2.isOwner(chr)) {
                    shop2.setOpen(true);
                    chr.setPlayerShop(null);
                    break;
                }
                break;
            }
            case 38: {
                final IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps == null || !imps.isOwner(chr) || imps instanceof MapleMiniGame) {
                    c.sendPacket(MaplePacketCreator.enableActions());
                    break;
                }
                for (int j = 0; j < imps.getItems().size(); ++j) {
                    if (imps.getItems().get(j).bundles == 0) {
                        imps.getItems().remove(j);
                    }
                }
                if (chr.getMeso() + imps.getMeso() < 0) {
                    c.sendPacket(PlayerShopPacket.shopItemUpdate(imps));
                    break;
                }
                chr.gainMeso(imps.getMeso(), false);
                imps.setMeso(0);
                c.sendPacket(PlayerShopPacket.shopItemUpdate(imps));
                break;
            }
            case 39: {
                final IMaplePlayerShop merchant4 = chr.getPlayerShop();
                if (merchant4 != null && merchant4.getShopType() == 1 && merchant4.isOwner(chr) && merchant4.isAvailable()) {
                    merchant4.removeAllVisitors(-1, -1);
                    merchant4.closeShop(true, true);
                    chr.setPlayerShop(null);
                    c.getPlayer().dropMessage(1, "\u8bf7\u901a\u8fc7\u5f17\u5170\u5fb7\u91cc\u62ff\u56de\u5269\u4f59\u7269\u54c1\u3002");
                    break;
                }
                break;
            }
            case 44: {
                final IMaplePlayerShop merchant4 = chr.getPlayerShop();
                if (merchant4 != null && merchant4.getShopType() == 1 && merchant4.isOwner(chr)) {
                    ((HiredMerchant)merchant4).sendVisitor(c);
                    break;
                }
                break;
            }
            case 45: {
                final IMaplePlayerShop merchant4 = chr.getPlayerShop();
                if (merchant4 != null && merchant4.getShopType() == 1 && merchant4.isOwner(chr)) {
                    ((HiredMerchant)merchant4).sendBlackList(c);
                    break;
                }
                break;
            }
            case 46: {
                final IMaplePlayerShop merchant4 = chr.getPlayerShop();
                if (merchant4 != null && merchant4.getShopType() == 1 && merchant4.isOwner(chr)) {
                    ((HiredMerchant)merchant4).addBlackList(slea.readMapleAsciiString());
                    break;
                }
                break;
            }
            case 47: {
                final IMaplePlayerShop merchant4 = chr.getPlayerShop();
                if (merchant4 != null && merchant4.getShopType() == 1 && merchant4.isOwner(chr)) {
                    ((HiredMerchant)merchant4).removeBlackList(slea.readMapleAsciiString());
                    break;
                }
                break;
            }
            case 50: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                game2.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game2, 0, game2.getVisitorSlot(chr)));
                game2.nextLoser();
                game2.setOpen(true);
                game2.update();
                game2.checkExitAfterGame();
                break;
            }
            case 59: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                if (!((MapleMiniGame)ips3).isOpen()) {
                    break;
                }
                ips3.removeAllVisitors(3, 1);
                break;
            }
            case 57:
            case 58: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 != null && ips3 instanceof MapleMiniGame) {
                    final MapleMiniGame game2 = (MapleMiniGame)ips3;
                    if (!game2.isOwner(chr) && game2.isOpen()) {
                        game2.setReady(game2.getVisitorSlot(chr));
                        game2.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(game2.isReady(game2.getVisitorSlot(chr))));
                    }
                    break;
                }
                break;
            }
            case 60: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 != null && ips3 instanceof MapleMiniGame) {
                    final MapleMiniGame game2 = (MapleMiniGame)ips3;
                    if (game2.isOwner(chr) && game2.isOpen()) {
                        for (int k = 1; k < ips3.getSize(); ++k) {
                            if (!game2.isReady(k)) {
                                return;
                            }
                        }
                        game2.setGameType();
                        game2.shuffleList();
                        if (game2.getGameType() == 1) {
                            game2.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game2.getLoser()));
                        }
                        else {
                            game2.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game2, game2.getLoser()));
                        }
                        game2.setOpen(false);
                        game2.update();
                    }
                    break;
                }
                break;
            }
            case 48: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                if (game2.isOwner(chr)) {
                    game2.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
                }
                else {
                    game2.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMiniGameRequestTie());
                }
                game2.setRequestedTie(game2.getVisitorSlot(chr));
                break;
            }
            case 49: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                if (game2.getRequestedTie() > -1 && game2.getRequestedTie() != game2.getVisitorSlot(chr)) {
                    if (slea.readByte() > 0) {
                        game2.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game2, 1, game2.getRequestedTie()));
                        game2.nextLoser();
                        game2.setOpen(true);
                        game2.update();
                        game2.checkExitAfterGame();
                    }
                    else {
                        game2.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                    }
                    game2.setRequestedTie(-1);
                }
                break;
            }
            case 53: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                if (game2.isOwner(chr)) {
                    game2.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestREDO(), false);
                }
                else {
                    game2.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMiniGameRequestREDO());
                }
                game2.setRequestedTie(game2.getVisitorSlot(chr));
                break;
            }
            case 54: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                if (slea.readByte() > 0) {
                    ips3.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip1(ips3.getVisitorSlot(chr)));
                    game2.nextLoser();
                }
                else {
                    game2.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                }
                game2.setRequestedTie(-1);
                break;
            }
            case 62: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                ips3.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ips3.getVisitorSlot(chr)));
                game2.nextLoser();
                break;
            }
            case 63: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                game2.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
                break;
            }
            case 67: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                if (slea.readByte() != game2.getTurn()) {
                    game2.broadcastToVisitors(PlayerShopPacket.shopChat("\u4e0d\u80fd\u653e\u5728\u901a\u8fc7 " + chr.getName() + ". \u5931\u8d25\u8005: " + game2.getLoser() + " \u6e38\u5ba2: " + game2.getVisitorSlot(chr) + " \u662f\u5426\u70ba\u771f: " + game2.getTurn(), game2.getVisitorSlot(chr)));
                    return;
                }
                final int slot3 = slea.readByte();
                final int turn = game2.getTurn();
                final int fs = game2.getFirstSlot();
                if (turn == 1) {
                    game2.setFirstSlot(slot3);
                    if (game2.isOwner(chr)) {
                        game2.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot3, fs, turn), false);
                    }
                    else {
                        game2.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMatchCardSelect(turn, slot3, fs, turn));
                    }
                    game2.setTurn(0);
                    return;
                }
                if (fs > 0 && game2.getCardId(fs + 1) == game2.getCardId(slot3 + 1)) {
                    game2.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot3, fs, game2.isOwner(chr) ? 2 : 3));
                    game2.setPoints(game2.getVisitorSlot(chr));
                }
                else {
                    game2.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot3, fs, (int)(game2.isOwner(chr) ? 0 : 1)));
                    game2.nextLoser();
                }
                game2.setTurn(1);
                game2.setFirstSlot(0);
                break;
            }
            case 55: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                game2.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game2, 0, game2.getVisitorSlot(chr)));
                game2.nextLoser();
                game2.setOpen(true);
                game2.update();
                game2.checkExitAfterGame();
                break;
            }
            case 56: {
                final IMaplePlayerShop ips3 = chr.getPlayerShop();
                if (ips3 == null || !(ips3 instanceof MapleMiniGame)) {
                    break;
                }
                final MapleMiniGame game2 = (MapleMiniGame)ips3;
                if (game2.isOpen()) {
                    break;
                }
                game2.setExitAfter(chr);
                game2.broadcastToVisitors(PlayerShopPacket.getMiniGameExitAfter(game2.isExitAfter(chr)));
                break;
            }
        }
    }
}
