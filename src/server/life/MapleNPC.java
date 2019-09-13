package server.life;

import client.MapleClient;
import server.MapleShopFactory;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;

public class MapleNPC extends AbstractLoadedMapleLife
{
    private String name;
    private boolean custom;
    
    public MapleNPC(final int id, final String name) {
        super(id);
        this.name = "MISSINGNO";
        this.custom = false;
        this.name = name;
    }
    
    public final boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(this.getId()) != null;
    }
    
    public final void sendShop(final MapleClient c) {
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("您已经建立与商店npc[" + this.getId() + "]的连接");
        }
        MapleShopFactory.getInstance().getShopForNPC(this.getId()).sendShop(c);
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
        if (this.getId() >= 9901000) {
            return;
        }
        client.getSession().write((Object)MaplePacketCreator.spawnNPC(this, true));
        client.getSession().write((Object)MaplePacketCreator.spawnNPCRequestController(this, true));
    }
    
    @Override
    public final void sendDestroyData(final MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeNPC(this.getObjectId()));
    }
    
    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.NPC;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public void setName(final String n) {
        this.name = n;
    }
    
    public final boolean isCustom() {
        return this.custom;
    }
    
    public final void setCustom(final boolean custom) {
        this.custom = custom;
    }
}
