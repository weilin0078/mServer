package server.shops;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import tools.Pair;
import tools.packet.PlayerShopPacket;

public abstract class AbstractPlayerStore extends AbstractMapleMapObject implements IMaplePlayerShop
{
    protected boolean open;
    protected boolean available;
    protected String ownerName;
    protected String des;
    protected String pass;
    protected int ownerId;
    protected int owneraccount;
    protected int itemId;
    protected int channel;
    protected int map;
    protected AtomicInteger meso;
    protected WeakReference<MapleCharacter>[] chrs;
    protected List<String> visitors;
    protected List<BoughtItem> bought;
    protected List<MaplePlayerShopItem> items;
    
    public AbstractPlayerStore(final MapleCharacter owner, final int itemId, final String desc, final String pass, final int slots) {
        this.open = false;
        this.available = false;
        this.meso = new AtomicInteger(0);
        this.visitors = new LinkedList<String>();
        this.bought = new LinkedList<BoughtItem>();
        this.items = new LinkedList<MaplePlayerShopItem>();
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.owneraccount = owner.getAccountID();
        this.itemId = itemId;
        this.des = desc;
        this.pass = pass;
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannel();
        this.chrs = (WeakReference<MapleCharacter>[])new WeakReference[slots];
        for (int i = 0; i < this.chrs.length; ++i) {
            this.chrs[i] = new WeakReference<MapleCharacter>(null);
        }
    }
    
    @Override
    public int getMaxSize() {
        return this.chrs.length + 1;
    }
    
    @Override
    public int getSize() {
        return (this.getFreeSlot() == -1) ? this.getMaxSize() : this.getFreeSlot();
    }
    
    @Override
    public void broadcastToVisitors(final MaplePacket packet) {
        this.broadcastToVisitors(packet, true);
    }
    
    public void broadcastToVisitors(final MaplePacket packet, final boolean owner) {
        for (final WeakReference<MapleCharacter> chr : this.chrs) {
            if (chr != null && chr.get() != null) {
                chr.get().getClient().getSession().write((Object)packet);
            }
        }
        if (this.getShopType() != 1 && owner && this.getMCOwner() != null) {
            this.getMCOwner().getClient().getSession().write((Object)packet);
        }
    }
    
    public void broadcastToVisitors(final MaplePacket packet, final int exception) {
        for (final WeakReference<MapleCharacter> chr : this.chrs) {
            if (chr != null && chr.get() != null && this.getVisitorSlot(chr.get()) != exception) {
                chr.get().getClient().getSession().write((Object)packet);
            }
        }
        if (this.getShopType() != 1 && this.getMCOwner() != null && exception != this.ownerId) {
            this.getMCOwner().getClient().getSession().write((Object)packet);
        }
    }
    
    @Override
    public int getMeso() {
        return this.meso.get();
    }
    
    @Override
    public void setMeso(final int meso) {
        this.meso.set(meso);
    }
    
    @Override
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
    
    public boolean saveItems() {
        if (this.getShopType() != 1) {
            return false;
        }
        final Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM hiredmerch WHERE accountid = ? OR characterid = ?");
            ps.setInt(1, this.owneraccount);
            ps.setInt(2, this.ownerId);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO hiredmerch (characterid, accountid, Mesos, map, channel, time) VALUES (?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, this.ownerId);
            ps.setInt(2, this.owneraccount);
            ps.setInt(3, this.meso.get());
            ps.setInt(4, this.map);
            ps.setInt(5, this.channel);
            ps.setLong(6, System.currentTimeMillis());
            ps.executeUpdate();
            final ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                rs.close();
                ps.close();
                System.out.println("[SaveItems] 保存雇佣商店信息出错 - 1");
                throw new RuntimeException("保存雇佣商店信息出错.");
            }
            final int packageid = rs.getInt(1);
            rs.close();
            ps.close();
            final List<Pair<IItem, MapleInventoryType>> iters = new ArrayList<Pair<IItem, MapleInventoryType>>();
            for (final MaplePlayerShopItem pItems : this.items) {
                if (pItems.item != null && pItems.bundles > 0) {
                    if (pItems.item.getQuantity() <= 0 && !GameConstants.isRechargable(pItems.item.getItemId())) {
                        continue;
                    }
                    final IItem item = pItems.item.copy();
                    item.setQuantity((short)(item.getQuantity() * pItems.bundles));
                    iters.add(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
                }
            }
            ItemLoader.HIRED_MERCHANT.saveItems(iters, packageid, this.owneraccount, this.ownerId);
            return true;
        }
        catch (Exception se) {
            System.out.println("[SaveItems] 保存雇佣商店信息出错 - 2 " + se);
            return false;
        }
    }
    
    public MapleCharacter getVisitor(final int num) {
        return this.chrs[num].get();
    }
    
    @Override
    public void update() {
        if (this.isAvailable()) {
            if (this.getShopType() == 1) {
                this.getMap().broadcastMessage(PlayerShopPacket.updateHiredMerchant((HiredMerchant)this));
            }
            else if (this.getMCOwner() != null) {
                this.getMap().broadcastMessage(PlayerShopPacket.sendPlayerShopBox(this.getMCOwner()));
            }
        }
    }
    
    @Override
    public void addVisitor(final MapleCharacter visitor) {
        final int i = this.getFreeSlot();
        if (i > 0) {
            if (this.getShopType() >= 3) {
                this.broadcastToVisitors(PlayerShopPacket.getMiniGameNewVisitor(visitor, i, (MapleMiniGame)this));
            }
            else {
                this.broadcastToVisitors(PlayerShopPacket.shopVisitorAdd(visitor, i));
            }
            this.chrs[i - 1] = new WeakReference<MapleCharacter>(visitor);
            if (!this.isOwner(visitor)) {
                this.visitors.add(visitor.getName());
            }
            if (i == 3) {
                this.update();
            }
        }
    }
    
    @Override
    public void removeVisitor(final MapleCharacter visitor) {
        final byte slot = this.getVisitorSlot(visitor);
        final boolean shouldUpdate = this.getFreeSlot() == -1;
        if (slot > 0) {
            this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(slot), slot);
            this.chrs[slot - 1] = new WeakReference<MapleCharacter>(null);
            if (shouldUpdate) {
                this.update();
            }
        }
    }
    
    @Override
    public byte getVisitorSlot(final MapleCharacter visitor) {
        for (byte i = 0; i < this.chrs.length; ++i) {
            if (this.chrs[i] != null && this.chrs[i].get() != null && this.chrs[i].get().getId() == visitor.getId()) {
                return (byte)(i + 1);
            }
        }
        if (visitor.getId() == this.ownerId) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public void removeAllVisitors(final int error, final int type) {
        for (int i = 0; i < this.chrs.length; ++i) {
            final MapleCharacter visitor = this.getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.getClient().getSession().write((Object)PlayerShopPacket.shopErrorMessage(error, type));
                }
                this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(this.getVisitorSlot(visitor)), this.getVisitorSlot(visitor));
                visitor.setPlayerShop(null);
                this.chrs[i] = new WeakReference<MapleCharacter>(null);
            }
        }
        this.update();
    }
    
    @Override
    public String getOwnerName() {
        return this.ownerName;
    }
    
    @Override
    public int getOwnerId() {
        return this.ownerId;
    }
    
    @Override
    public int getOwnerAccId() {
        return this.owneraccount;
    }
    
    @Override
    public String getDescription() {
        if (this.des == null) {
            return "";
        }
        return this.des;
    }
    
    @Override
    public List<Pair<Byte, MapleCharacter>> getVisitors() {
        final List<Pair<Byte, MapleCharacter>> chrz = new LinkedList<Pair<Byte, MapleCharacter>>();
        for (byte i = 0; i < this.chrs.length; ++i) {
            if (this.chrs[i] != null && this.chrs[i].get() != null) {
                chrz.add(new Pair<Byte, MapleCharacter>((byte)(i + 1), this.chrs[i].get()));
            }
        }
        return chrz;
    }
    
    @Override
    public List<MaplePlayerShopItem> getItems() {
        return this.items;
    }
    
    @Override
    public void addItem(final MaplePlayerShopItem item) {
        this.items.add(item);
    }
    
    @Override
    public boolean removeItem(final int item) {
        return false;
    }
    
    @Override
    public void removeFromSlot(final int slot) {
        this.items.remove(slot);
    }
    
    @Override
    public byte getFreeSlot() {
        for (byte i = 0; i < this.chrs.length; ++i) {
            if (this.chrs[i] == null || this.chrs[i].get() == null) {
                return (byte)(i + 1);
            }
        }
        return -1;
    }
    
    @Override
    public int getItemId() {
        return this.itemId;
    }
    
    @Override
    public boolean isOwner(final MapleCharacter chr) {
        return chr.getId() == this.ownerId && chr.getName().equals(this.ownerName);
    }
    
    @Override
    public String getPassword() {
        if (this.pass == null) {
            return "";
        }
        return this.pass;
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SHOP;
    }
    
    public MapleCharacter getMCOwner() {
        return this.getMap().getCharacterById(this.ownerId);
    }
    
    public MapleCharacter getMCOwnerWorld() {
        final int ourChannel = World.Find.findChannel(this.ownerId);
        if (ourChannel <= 0) {
            return null;
        }
        return ChannelServer.getInstance(ourChannel).getPlayerStorage().getCharacterById(this.ownerId);
    }
    
    public MapleMap getMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.map);
    }
    
    @Override
    public int getGameType() {
        if (this.getShopType() == 1) {
            return 5;
        }
        if (this.getShopType() == 2) {
            return 4;
        }
        if (this.getShopType() == 3) {
            return 1;
        }
        if (this.getShopType() == 4) {
            return 2;
        }
        return 0;
    }
    
    @Override
    public boolean isAvailable() {
        return this.available;
    }
    
    @Override
    public void setAvailable(final boolean b) {
        this.available = b;
    }
    
    @Override
    public List<BoughtItem> getBoughtItems() {
        return this.bought;
    }
    
    public static final class BoughtItem
    {
        public int id;
        public int quantity;
        public int totalPrice;
        public String buyer;
        
        public BoughtItem(final int id, final int quantity, final int totalPrice, final String buyer) {
            this.id = id;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.buyer = buyer;
        }
    }
}
