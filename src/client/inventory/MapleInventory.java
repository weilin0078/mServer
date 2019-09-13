package client.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import constants.GameConstants;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

public class MapleInventory implements Iterable<IItem>, Serializable
{
    private Map<Short, IItem> inventory;
    private byte slotLimit;
    private MapleInventoryType type;
    
    public MapleInventory(final MapleInventoryType type, final byte slotLimit) {
        this.slotLimit = 0;
        this.inventory = new LinkedHashMap<Short, IItem>();
        this.slotLimit = slotLimit;
        this.type = type;
    }
    
    public void addSlot(final byte slot) {
        this.slotLimit += slot;
        if (this.slotLimit > 96) {
            this.slotLimit = 96;
        }
    }
    
    public byte getSlotLimit() {
        return this.slotLimit;
    }
    
    public void setSlotLimit(byte slot) {
        if (slot > 96) {
            slot = 96;
        }
        this.slotLimit = slot;
    }
    
    public IItem findById(final int itemId) {
        for (final IItem item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public IItem findByUniqueId(final int itemId) {
        for (final IItem item : this.inventory.values()) {
            if (item.getUniqueId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public int countById(final int itemId) {
        int possesed = 0;
        for (final IItem item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                possesed += item.getQuantity();
            }
        }
        return possesed;
    }
    
    public List<IItem> listById(final int itemId) {
        final List<IItem> ret = new ArrayList<IItem>();
        for (final IItem item : this.inventory.values()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }
        if (ret.size() > 1) {
            Collections.sort(ret);
        }
        return ret;
    }
    
    public Collection<IItem> list() {
        return this.inventory.values();
    }
    
    public short addItem(final IItem item) {
        final short slotId = this.getNextFreeSlot();
        if (slotId < 0) {
            return -1;
        }
        this.inventory.put(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }
    
    public void addFromDB(final IItem item) {
        if (item.getPosition() < 0 && !this.type.equals(MapleInventoryType.EQUIPPED)) {
            return;
        }
        this.inventory.put(item.getPosition(), item);
    }
    
    public boolean move2(final byte sSlot, final byte dSlot, final short slotMax) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final IItem source = this.inventory.get(sSlot);
        final IItem target = this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            source.setPosition(dSlot);
            this.inventory.put((short)dSlot, source);
            this.inventory.remove(sSlot);
        }
        else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId())) {
            if (this.type.getType() == MapleInventoryType.EQUIP.getType()) {
                this.swap(target, source);
            }
            if (source.getQuantity() + target.getQuantity() > slotMax) {
                final short rest = (short)(source.getQuantity() + target.getQuantity() - slotMax);
                if (rest + slotMax != source.getQuantity() + target.getQuantity()) {
                    return false;
                }
                source.setQuantity(rest);
                target.setQuantity(slotMax);
            }
            else {
                target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        }
        else {
            this.swap(target, source);
        }
        return true;
    }
    
    public void move(final short sSlot, final short dSlot, final short slotMax) {
        if (dSlot > this.slotLimit) {
            return;
        }
        final Item source = (Item)this.inventory.get(sSlot);
        final Item target = (Item)this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            source.setPosition(dSlot);
            this.inventory.put(dSlot, source);
            this.inventory.remove(sSlot);
        }
        else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration()) {
            if (this.type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType()) {
                this.swap(target, source);
            }
            else if (source.getQuantity() + target.getQuantity() > slotMax) {
                source.setQuantity((short)(source.getQuantity() + target.getQuantity() - slotMax));
                target.setQuantity(slotMax);
            }
            else {
                target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        }
        else {
            this.swap(target, source);
        }
    }
    
    private void swap(final IItem source, final IItem target) {
        this.inventory.remove(source.getPosition());
        this.inventory.remove(target.getPosition());
        final short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        this.inventory.put(source.getPosition(), source);
        this.inventory.put(target.getPosition(), target);
    }
    
    public IItem getItem(final short slot) {
        return this.inventory.get(slot);
    }
    
    public void removeItem(final short slot) {
        this.removeItem(slot, (short)1, false);
    }
    
    public void removeItem(final short slot, final short quantity, final boolean allowZero) {
        this.removeItem(slot, quantity, allowZero, null);
    }
    
    public void removeItem(final short slot, final short quantity, final boolean allowZero, final MapleCharacter chr) {
        final IItem item = this.inventory.get(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short)(item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short)0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            this.removeSlot(slot);
        }
        if (chr != null) {
            chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(3, item)));
            chr.dropMessage(5, "期限道具[" + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "]已经过期");
        }
    }
    
    public void removeSlot(final short slot) {
        this.inventory.remove(slot);
    }
    
    public boolean isFull() {
        return this.inventory.size() >= this.slotLimit;
    }
    
    public boolean isFull(final int margin) {
        return this.inventory.size() + margin >= this.slotLimit;
    }
    
    public short getNextFreeSlot() {
        if (this.isFull()) {
            return -1;
        }
        for (short i = 1; i <= this.slotLimit; ++i) {
            if (!this.inventory.keySet().contains(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public short getNumFreeSlot() {
        if (this.isFull()) {
            return 0;
        }
        byte free = 0;
        for (short i = 1; i <= this.slotLimit; ++i) {
            if (!this.inventory.keySet().contains(i)) {
                ++free;
            }
        }
        return free;
    }
    
    public MapleInventoryType getType() {
        return this.type;
    }
    
    @Override
    public Iterator<IItem> iterator() {
        return Collections.unmodifiableCollection(this.inventory.values()).iterator();
    }
}
