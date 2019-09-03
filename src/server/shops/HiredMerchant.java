package server.shops;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import constants.GameConstants;
import handling.channel.ChannelServer;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Timer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;
import tools.packet.PlayerShopPacket;

public class HiredMerchant extends AbstractPlayerStore
{
    public ScheduledFuture<?> schedule;
    private List<String> blacklist;
    private int storeid;
    private long start;
    
    public HiredMerchant(final MapleCharacter owner, final int itemId, final String desc) {
        super(owner, itemId, desc, "", 3);
        this.start = System.currentTimeMillis();
        this.blacklist = new LinkedList<String>();
        this.schedule = Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                HiredMerchant.this.closeShop(true, true);
            }
        }, 86400000L);
    }
    
    @Override
    public byte getShopType() {
        return 1;
    }
    
    public final void setStoreid(final int storeid) {
        this.storeid = storeid;
    }
    
    public List<MaplePlayerShopItem> searchItem(final int itemSearch) {
        final List<MaplePlayerShopItem> itemz = new LinkedList<MaplePlayerShopItem>();
        for (final MaplePlayerShopItem item : this.items) {
            if (item.item.getItemId() == itemSearch && item.bundles > 0) {
                itemz.add(item);
            }
        }
        return itemz;
    }
    
    @Override
    public void buy(final MapleClient c, final int item, final short quantity) {
        final MaplePlayerShopItem pItem = this.items.get(item);
        final IItem shopItem = pItem.item;
        final IItem newItem = shopItem.copy();
        final short perbundle = newItem.getQuantity();
        final int theQuantity = pItem.price * quantity;
        newItem.setQuantity((short)(quantity * perbundle));
        final byte flag = newItem.getFlag();
        if (ItemFlag.KARMA_EQ.check(flag)) {
            newItem.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
        }
        else if (ItemFlag.KARMA_USE.check(flag)) {
            newItem.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
        }
        if (!c.getPlayer().canHold(newItem.getItemId())) {
            c.getPlayer().dropMessage(1, "\u80cc\u5305\u5df2\u6ee1");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner())) {
            final int gainmeso = this.getMeso() + theQuantity - GameConstants.EntrustedStoreTax(theQuantity);
            if (gainmeso > 0) {
                this.setMeso(gainmeso);
                final MaplePlayerShopItem tmp167_165 = pItem;
                tmp167_165.bundles -= quantity;
                MapleInventoryManipulator.addFromDrop(c, newItem, false);
                this.bought.add(new BoughtItem(newItem.getItemId(), quantity, theQuantity, c.getPlayer().getName()));
                c.getPlayer().gainMeso(-theQuantity, false);
                this.saveItems();
                final MapleCharacter chr = this.getMCOwnerWorld();
                final String itemText = MapleItemInformationProvider.getInstance().getName(newItem.getItemId()) + " (" + perbundle + ") x " + quantity + " \u5df2\u7ecf\u88ab\u5356\u51fa\u3002 \u5269\u4f59\u6570\u91cf: " + pItem.bundles + " \u8d2d\u4e70\u8005: " + c.getPlayer().getName();
                if (chr != null) {
                    chr.dropMessage(-5, "\u60a8\u96c7\u4f63\u5546\u5e97\u91cc\u9762\u7684\u9053\u5177: " + itemText);
                }
                System.out.println("[\u96c7\u4f63] " + ((chr != null) ? chr.getName() : this.getOwnerName()) + " \u96c7\u4f63\u5546\u5e97\u5356\u51fa: " + newItem.getItemId() + " - " + itemText + " \u4ef7\u683c: " + theQuantity);
            }
            else {
                c.getPlayer().dropMessage(1, "\u91d1\u5e01\u4e0d\u8db3.");
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
        else {
            c.getPlayer().dropMessage(1, "\u80cc\u5305\u5df2\u6ee1\r\n\u8bf7\u75591\u683c\u4ee5\u4e0a\u4f4d\u7f6e\r\n\u5728\u8fdb\u884c\u8d2d\u4e70\u7269\u54c1\r\n\u9632\u6b62\u975e\u6cd5\u590d\u5236");
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }
    
    @Override
    public void closeShop(final boolean saveItems, final boolean remove) {
        if (this.schedule != null) {
            this.schedule.cancel(false);
        }
        if (saveItems) {
            this.saveItems();
            this.items.clear();
        }
        if (remove) {
            ChannelServer.getInstance(this.channel).removeMerchant(this);
            this.getMap().broadcastMessage(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
        }
        this.getMap().removeMapObject(this);
        try {
            for (final ChannelServer ch : ChannelServer.getAllInstances()) {
                MapleMap map = null;
                for (int i = 910000001; i <= 910000022; ++i) {
                    map = ch.getMapFactory().getMap(i);
                    if (map != null) {
                        final List<MapleMapObject> HMS = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT));
                        for (final MapleMapObject HM : HMS) {
                            final HiredMerchant HMM = (HiredMerchant)HM;
                            if (HMM.getOwnerId() == this.getOwnerId()) {
                                map.removeMapObject(this);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {}
        this.schedule = null;
    }
    
    public int getTimeLeft() {
        return (int)((System.currentTimeMillis() - this.start) / 1000L);
    }
    
    public final int getStoreId() {
        return this.storeid;
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.HIRED_MERCHANT;
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write((Object)PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
        }
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
        if (this.isAvailable()) {
            client.getSession().write((Object)PlayerShopPacket.spawnHiredMerchant(this));
        }
    }
    
    public final boolean isInBlackList(final String bl) {
        return this.blacklist.contains(bl);
    }
    
    public final void addBlackList(final String bl) {
        this.blacklist.add(bl);
    }
    
    public final void removeBlackList(final String bl) {
        this.blacklist.remove(bl);
    }
    
    public final void sendBlackList(final MapleClient c) {
        c.getSession().write((Object)PlayerShopPacket.MerchantBlackListView(this.blacklist));
    }
    
    public final void sendVisitor(final MapleClient c) {
        c.getSession().write((Object)PlayerShopPacket.MerchantVisitorView(this.visitors));
    }
}
