package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import tools.MaplePacketCreator;

public class MapleLove extends AbstractMapleMapObject
{
    private Point pos;
    private MapleCharacter owner;
    private String text;
    private int ft;
    private int itemid;
    
    public MapleLove(final MapleCharacter owner, final Point pos, final int ft, final String text, final int itemid) {
        this.owner = owner;
        this.pos = pos;
        this.text = text;
        this.ft = ft;
        this.itemid = itemid;
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.LOVE;
    }
    
    @Override
    public Point getPosition() {
        return this.pos.getLocation();
    }
    
    public MapleCharacter getOwner() {
        return this.owner;
    }
    
    @Override
    public void setPosition(final Point position) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
        client.getSession().write((Object)this.makeDestroyData());
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
        client.getSession().write((Object)this.makeSpawnData());
    }
    
    public MaplePacket makeSpawnData() {
        return MaplePacketCreator.spawnLove(this.getObjectId(), this.itemid, this.owner.getName(), this.text, this.pos, this.ft);
    }
    
    public MaplePacket makeDestroyData() {
        return MaplePacketCreator.removeLove(this.getObjectId());
    }
}
