package server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.InventoryException;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import tools.MaplePacketCreator;
import tools.packet.MTSCSPacket;

public class MapleInventoryManipulator
{
    public static void addRing(final MapleCharacter chr, final int itemId, final int ringId, final int sn) {
        final CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        final IItem ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().getSession().write((Object)MTSCSPacket.showBoughtCSItem(ring, sn, chr.getClient().getAccID()));
    }
    
    public static boolean addbyItem(final MapleClient c, final IItem item) {
        return addbyItem(c, item, false) >= 0;
    }
    
    public static short addbyItem(final MapleClient c, final IItem item, final boolean fromcs) {
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
            }
            return newSlot;
        }
        if (!fromcs) {
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item));
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }
    
    public static int getUniqueId(final int itemId, final MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            }
            else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        }
        else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final byte Flag) {
        return addById(c, itemId, quantity, null, null, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final byte Flag) {
        return addById(c, itemId, quantity, owner, null, 0L, Flag);
    }
    
    public static byte addId(final MapleClient c, final int itemId, final short quantity, final String owner, final byte Flag) {
        return addId(c, itemId, quantity, owner, null, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final byte Flag) {
        return addById(c, itemId, quantity, owner, pet, 0L, Flag);
    }
    
    public static boolean addById(final MapleClient c, final int itemId, final short quantity, final String owner, final MaplePet pet, final long period, final byte Flag) {
        return addId(c, itemId, quantity, owner, pet, period, Flag) >= 0;
    }
    
    public static byte addId(final MapleClient c, final int itemId, short quantity, final String owner, final MaplePet pet, final long period, final byte Flag) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        final int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemId);
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) {
                    final Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = (Item) i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1L) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    if (newQ2 == 0) {
                        c.getPlayer().havePartyQuest(itemId);
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        return (byte)newSlot;
                    }
                    quantity -= newQ2;
                    final Item nItem = new Item(itemId, (short)0, newQ2, (byte)0, uniqueid);
                    newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        return -1;
                    }
                    if (owner != null) {
                        nItem.setOwner(owner);
                    }
                    if (Flag > 0 && ii.isCash(nItem.getItemId())) {
                        byte flag = nItem.getFlag();
                        flag |= (byte)ItemFlag.KARMA_EQ.getValue();
                        nItem.setFlag(flag);
                    }
                    if (period > 0L) {
                        nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                    }
                    if (pet != null) {
                        nItem.setPet(pet);
                        pet.setInventoryPosition(newSlot);
                        c.getPlayer().addPet(pet);
                    }
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                    if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                    }
                }
            }
            else {
                final Item nItem = new Item(itemId, (short)0, quantity, (byte)0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
            final IItem nEquip = ii.getEquipById(itemId);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            nEquip.setUniqueId(uniqueid);
            if (Flag > 0 && ii.isCash(nEquip.getItemId())) {
                byte flag2 = nEquip.getFlag();
                flag2 |= (byte)ItemFlag.KARMA_USE.getValue();
                nEquip.setFlag(flag2);
            }
            if (period > 0L) {
                nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
            if (newSlot == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return -1;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nEquip));
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte)newSlot;
    }
    
    public static IItem addbyId_Gachapon(final MapleClient c, final int itemId, short quantity) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return null;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemId);
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                IItem nItem = null;
                boolean recieved = false;
                if (existing.size() > 0) {
                    final Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        nItem = i.next();
                        final short oldQ = nItem.getQuantity();
                        if (oldQ < slotMax) {
                            recieved = true;
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            nItem.setQuantity(newQ);
                            c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, nItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    if (newQ2 == 0) {
                        break;
                    }
                    quantity -= newQ2;
                    nItem = new Item(itemId, (short)0, newQ2, (byte)0);
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1 && recieved) {
                        return nItem;
                    }
                    if (newSlot == -1) {
                        return null;
                    }
                    recieved = true;
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem));
                    if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                    }
                }
                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
                return null;
            }
            else {
                final Item nItem2 = new Item(itemId, (short)0, quantity, (byte)0);
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    return null;
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem2));
                c.getPlayer().havePartyQuest(nItem2.getItemId());
                return nItem2;
            }
        }
        else {
            if (quantity != 1) {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
            final IItem item = ii.randomizeStats((Equip)ii.getEquipById(itemId));
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                return null;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, true));
            c.getPlayer().havePartyQuest(item.getItemId());
            return item;
        }
    }
    
    public static boolean addFromDrop(final MapleClient c, final IItem item, final boolean show) {
        return addFromDrop(c, item, show, false);
    }
    
    public static boolean addFromDrop(final MapleClient c, IItem item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = (Item) i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, true));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem2));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, true));
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write((Object)MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    public static boolean \u5546\u5e97\u9632\u6b62\u590d\u5236(final MapleClient c, final IItem item, final boolean show) {
        return \u5546\u5e97\u9632\u6b62\u590d\u5236(c, item, show, false);
    }
    
    public static boolean \u5546\u5e97\u9632\u6b62\u590d\u5236(final MapleClient c, IItem item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = (Item) i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration() && slotMax <= slotMax - oldQ) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, true));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem2));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, true));
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write((Object)MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    public static boolean pet_addFromDrop(final MapleClient c, IItem item, final boolean show, final boolean enhance) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, item.getItemId());
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    final Iterator<IItem> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        final Item eItem = (Item) i.next();
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                            final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                            quantity -= (short)(newQ - oldQ);
                            eItem.setQuantity(newQ);
                            c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ2 = (short)Math.min(quantity, slotMax);
                    quantity -= newQ2;
                    final Item nItem = new Item(item.getItemId(), (short)0, newQ2, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ2));
                        return false;
                    }
                    c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem, false));
                }
            }
            else {
                final Item nItem2 = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
                nItem2.setExpiration(item.getExpiration());
                nItem2.setOwner(item.getOwner());
                nItem2.setPet(item.getPet());
                final short newSlot2 = c.getPlayer().getInventory(type).addItem(nItem2);
                if (newSlot2 == -1) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, nItem2));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
        else {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                item = checkEnhanced(item, c.getPlayer());
            }
            final short newSlot3 = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot3 == -1) {
                c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                return false;
            }
            c.getSession().write((Object)MaplePacketCreator.addInventorySlot(type, item, false));
        }
        if (item.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4031875: {
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                }
                case 4001246: {
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                }
                case 4001473: {
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write((Object)MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }
    
    private static final IItem checkEnhanced(final IItem before, final MapleCharacter chr) {
        if (before instanceof Equip) {
            final Equip eq = (Equip)before;
            if (eq.getState() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && Randomizer.nextInt(100) > 80) {
                eq.resetPotential();
            }
        }
        return before;
    }
    
    private static int rand(final int min, final int max) {
        return Math.abs(Randomizer.rand(min, max));
    }
    
    public static boolean checkSpace(final MapleClient c, final int itemid, int quantity, final String owner) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) || !ii.itemExists(itemid)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (c == null || c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(c, itemid);
            final List<IItem> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid) && existing.size() > 0) {
                for (final IItem eItem : existing) {
                    final short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        final short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                    }
                    if (quantity <= 0) {
                        break;
                    }
                }
            }
            int numSlotsNeeded;
            if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                numSlotsNeeded = (int)Math.ceil(quantity / (double)slotMax);
            }
            else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !c.getPlayer().getInventory(type).isFull();
    }
    
    public static void removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop) {
        removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }
    
    public static void removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return;
        }
        final IItem item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (item.getQuantity() == 0 && !allowZero) {
                c.getSession().write((Object)MaplePacketCreator.clearInventoryItem(type, item.getPosition(), fromDrop));
            }
            else {
                c.getSession().write((Object)MaplePacketCreator.updateInventorySlot(type, item, fromDrop));
            }
        }
    }
    
    public static boolean removeById(final MapleClient c, final MapleInventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        for (final IItem item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (remremove <= item.getQuantity()) {
                removeFromSlot(c, type, item.getPosition(), (short)remremove, fromDrop, consume);
                remremove = 0;
                break;
            }
            remremove -= item.getQuantity();
            removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
        }
        return remremove <= 0;
    }
    
    public static void move(final MapleClient c, final MapleInventoryType type, final short src, final short dst) {
        if (src < 0 || dst < 0) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final IItem source = c.getPlayer().getInventory(type).getItem(src);
        final IItem initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(c, source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        final List<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null && initialTarget.getItemId() == source.getItemId() && !GameConstants.isRechargable(source.getItemId())) {
            if (olddstQ + oldsrcQ > slotMax) {
                mods.add(new ModifyInventory(1, source));
                mods.add(new ModifyInventory(1, initialTarget));
            }
            else {
                mods.add(new ModifyInventory(3, source));
                mods.add(new ModifyInventory(1, initialTarget));
            }
        }
        else {
            mods.add(new ModifyInventory(2, source, src));
        }
        c.sendPacket(MaplePacketCreator.modifyInventory(true, mods));
    }
    
    public static void equip(final MapleClient c, final short src, short dst) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        final PlayerStats statst = c.getPlayer().getStat();
        Equip source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null || source.getDurability() == 0) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
        if (ii.isCash(source.getItemId()) && source.getUniqueId() <= 0) {
            source.setUniqueId(1);
            c.getSession().write((Object)MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && !GameConstants.is\u8c46\u8c46\u88c5\u5907(source.getItemId())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (dst >= -999 && dst < -99 && stats.get("cash") == 0 && !GameConstants.is\u8c46\u8c46\u88c5\u5907(source.getItemId())) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11) {
            AutobanManager.getInstance().autoban(c, "Equipment hack, itemid " + source.getItemId() + " to slot " + dst);
            return;
        }
        if (GameConstants.isKatara(source.getItemId())) {
            dst = -10;
        }
        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        switch (dst) {
            case -6: {
                final IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-5));
                if (top == null || !GameConstants.isOverall(top.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-5), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -5: {
                final IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-5));
                final IItem bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-6));
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull((bottom != null && GameConstants.isOverall(source.getItemId())) ? 1 : 0)) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short)(-5), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom == null || !GameConstants.isOverall(source.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-6), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -10: {
                final IItem weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-11));
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    break;
                }
                else {
                    if (weapon == null || !GameConstants.isTwoHanded(weapon.getItemId())) {
                        break;
                    }
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                        c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short)(-11), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                    break;
                }
                
            }
            case -11: {
                final IItem shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-10));
                if (shield == null || !GameConstants.isTwoHanded(source.getItemId())) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    c.getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short)(-10), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
        }
        source = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        byte flag = source.getFlag();
        if (stats.get("equipTradeBlock") == 1) {
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= (byte)ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                c.getSession().write((Object)MaplePacketCreator.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
            }
        }
        else if (ItemFlag.KARMA_EQ.check(flag)) {
            source.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
            c.getSession().write((Object)MaplePacketCreator.updateSpecialItemUse(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        else if (ItemFlag.KARMA_USE.check(flag)) {
            source.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
            c.getSession().write((Object)MaplePacketCreator.updateSpecialItemUse(source, GameConstants.getInventoryType(source.getItemId()).getType()));
        }
        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (chr.getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                chr.cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                chr.cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
            if (chr.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
                chr.cancelBuffStats(MapleBuffStat.LIGHTNING_CHARGE);
            }
        }
        if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }
        c.getSession().write((Object)MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)2));
        chr.equipChanged();
    }
    
    public static void unequip(final MapleClient c, final short src, final short dst) {
        final Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        final Equip target = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        if (dst < 0 || source == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (target != null && src <= 0) {
            c.getSession().write((Object)MaplePacketCreator.getInventoryFull());
            return;
        }
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            if (c.getPlayer().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.BOOSTER);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SPIRIT_CLAW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SPIRIT_CLAW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SOULARROW) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.SOULARROW);
            }
            if (c.getPlayer().getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                c.getPlayer().cancelBuffStats(MapleBuffStat.WK_CHARGE);
            }
        }
        if (source.getItemId() == 1122017) {
            c.getPlayer().cancelFairySchedule(true);
        }
        c.getSession().write((Object)MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short)1));
        c.getPlayer().equipChanged();
    }
    
    public static boolean drop(final MapleClient c, final MapleInventoryType type, final short src, final short quantity) {
        return drop(c, type, src, quantity, false);
    }
    
    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, final short quantity, final boolean npcInduced) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null) {
            return false;
        }
        final IItem source = c.getPlayer().getInventory(type).getItem(src);
        if (source == null || (!npcInduced && GameConstants.isPet(source.getItemId()))) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (ii.isCash(source.getItemId()) || source.getExpiration() > 0L) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        final byte flag = source.getFlag();
        if (quantity > source.getQuantity()) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        c.getPlayer().setCurrenttime(System.currentTimeMillis());
        if (c.getPlayer().getCurrenttime() - c.getPlayer().getLasttime() < 3000L) {
            c.getPlayer().dropMessage(1, "<\u6e29\u99a8\u63d0\u9192>\uff1a\u8bf7\u60a8\u6162\u70b9\u4f7f\u7528.");
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return false;
        }
        c.getPlayer().setLasttime(System.currentTimeMillis());
        final Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final IItem target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short)(source.getQuantity() - quantity));
            c.getSession().write((Object)MaplePacketCreator.dropInventoryItemUpdate(type, source));
            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
                else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
                else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            }
            else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
            }
            else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
            }
        }
        else {
            c.getPlayer().getInventory(type).removeSlot(src);
            c.getSession().write((Object)MaplePacketCreator.dropInventoryItem((src < 0) ? MapleInventoryType.EQUIP : type, src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
                else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte)(flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
                else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            }
            else if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
            }
            else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
            }
        }
        return true;
    }
}
