package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import database.DatabaseConnection;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.MaplePacketCreator;

public class MapleShop
{
    private static final Set<Integer> rechargeableItems;
    private int id;
    private int npcId;
    private List<MapleShopItem> items;
    
    private MapleShop(final int id, final int npcId) {
        this.id = id;
        this.npcId = npcId;
        this.items = new LinkedList<MapleShopItem>();
    }
    
    public void addItem(final MapleShopItem item) {
        this.items.add(item);
    }
    
    public void sendShop(final MapleClient c) {
        final MapleNPC npc = MapleLifeFactory.getNPC(this.getNpcId());
        if (npc == null || npc.getName().equals("MISSINGNO")) {
            c.getPlayer().dropMessage(1, "商店" + this.id + "找不到此代码为" + this.getNpcId() + "的Npc");
            return;
        }
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage("您已建立与商店" + this.id + "的连接");
        }
        c.getPlayer().setShop(this);
        c.getSession().write((Object)MaplePacketCreator.getNPCShop(c, this.getNpcId(), this.items));
    }
    
    public void buy(final MapleClient c, final int itemId, short quantity) {
        if (quantity <= 0) {
            AutobanManager.getInstance().addPoints(c, 1000, 0L, "Buying " + quantity + " " + itemId);
            return;
        }
        if (!GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob())) {
            c.getPlayer().dropMessage(1, "You may not buy this item.");
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        final MapleShopItem item = this.findById(itemId);
        if (item != null && item.getPrice() > 0) {
            final int price = GameConstants.isRechargable(itemId) ? item.getPrice() : (item.getPrice() * quantity);
            if (price >= 0 && c.getPlayer().getMeso() >= price) {
                if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                    c.getPlayer().gainMeso(-price, false);
                    if (GameConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1L, (byte)0);
                    }
                    else {
                        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        if (GameConstants.isRechargable(itemId)) {
                            quantity = ii.getSlotMax(c, item.getItemId());
                        }
                        MapleInventoryManipulator.addById(c, itemId, quantity, (byte)0);
                    }
                }
                else {
                    c.getPlayer().dropMessage(1, "Your Inventory is full");
                }
                c.getSession().write((Object)MaplePacketCreator.confirmShopTransaction((byte)0));
            }
        }
    }
    
    public void sell(final MapleClient c, final MapleInventoryType type, final byte slot, short quantity) {
        if (quantity == 65535 || quantity == 0) {
            quantity = 1;
        }
        final IItem item = c.getPlayer().getInventory(type).getItem(slot);
        if (item == null) {
            return;
        }
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            quantity = item.getQuantity();
        }
        if (quantity < 0) {
            AutobanManager.getInstance().addPoints(c, 1000, 0L, "Selling " + quantity + " " + item.getItemId() + " (" + type.name() + "/" + slot + ")");
            return;
        }
        short iQuant = item.getQuantity();
        if (iQuant == 65535) {
            iQuant = 1;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.cantSell(item.getItemId())) {
            return;
        }
        if (quantity <= iQuant && iQuant > 0) {
            MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            double price;
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double)ii.getSlotMax(c, item.getItemId());
            }
            else {
                price = ii.getPrice(item.getItemId());
            }
            final int recvMesos = (int)Math.max(Math.ceil(price * quantity), 0.0);
            if (price != -1.0 && recvMesos > 0) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.getSession().write((Object)MaplePacketCreator.confirmShopTransaction((byte)8));
        }
    }
    
    public void recharge(final MapleClient c, final byte slot) {
        final IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(c, item.getItemId());
        final int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());
        if (skill != 0) {
            slotMax += (short)(c.getPlayer().getSkillLevel(SkillFactory.getSkill(skill)) * 10);
        }
        if (item.getQuantity() < slotMax) {
            final int price = (int)Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, item, false));
                c.getPlayer().gainMeso(-price, false, true, false);
                c.getSession().write((Object)MaplePacketCreator.confirmShopTransaction((byte)8));
            }
        }
    }
    
    protected MapleShopItem findById(final int itemId) {
        for (final MapleShopItem item : this.items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public static MapleShop createFromDB(final int id, final boolean isShopId) {
        MapleShop ret = null;
        try {
            final Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            final int shopId = rs.getInt("shopid");
            ret = new MapleShop(shopId, rs.getInt("npcid"));
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            final List<Integer> recharges = new ArrayList<Integer>(MapleShop.rechargeableItems);
            while (rs.next()) {
                if (GameConstants.isThrowingStar(rs.getInt("itemid")) || GameConstants.isBullet(rs.getInt("itemid"))) {
                    final MapleShopItem starItem = new MapleShopItem((short)1, rs.getInt("itemid"), rs.getInt("price"));
                    ret.addItem(starItem);
                    if (!MapleShop.rechargeableItems.contains(starItem.getItemId())) {
                        continue;
                    }
                    recharges.remove((Object)starItem.getItemId());
                }
                else {
                    ret.addItem(new MapleShopItem((short)1000, rs.getInt("itemid"), rs.getInt("price")));
                }
            }
            for (final Integer recharge : recharges) {
                ret.addItem(new MapleShopItem((short)1000, recharge, 0));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Could not load shop" + e);
        }
        return ret;
    }
    
    public int getNpcId() {
        return this.npcId;
    }
    
    public int getId() {
        return this.id;
    }
    
    static {
        rechargeableItems = new LinkedHashSet<Integer>();
        for (int i = 2070000; i <= 2070021; ++i) {
            MapleShop.rechargeableItems.add(i);
        }
        for (int i = 2070023; i <= 2070026; ++i) {
            MapleShop.rechargeableItems.add(i);
        }
        MapleShop.rechargeableItems.remove(2070014);
        MapleShop.rechargeableItems.remove(2070015);
        MapleShop.rechargeableItems.remove(2070016);
        MapleShop.rechargeableItems.remove(2070017);
        MapleShop.rechargeableItems.remove(2070018);
        MapleShop.rechargeableItems.remove(2070019);
        MapleShop.rechargeableItems.remove(2070020);
        MapleShop.rechargeableItems.remove(2070021);
        MapleShop.rechargeableItems.remove(2070023);
        MapleShop.rechargeableItems.remove(2070024);
        MapleShop.rechargeableItems.remove(2070025);
        MapleShop.rechargeableItems.remove(2070026);
        for (int i = 2330000; i <= 2330006; ++i) {
            MapleShop.rechargeableItems.add(i);
        }
        MapleShop.rechargeableItems.add(2331000);
        MapleShop.rechargeableItems.add(2332000);
    }
}
