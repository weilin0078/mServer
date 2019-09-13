package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import tools.MaplePacketCreator;

public class MapleDragon extends AbstractAnimatedMapleMapObject
{
    private int owner;
    private int jobid;
    
    public MapleDragon(final MapleCharacter owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        if (this.jobid < 2200 || this.jobid > 2218) {
            throw new RuntimeException("Trying to create a dragon for a non-Evan");
        }
        this.setPosition(owner.getPosition());
        this.setStance(4);
    }
    
    @Override
    public void sendSpawnData(final MapleClient client) {
    }
    
    @Override
    public void sendDestroyData(final MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeDragon(this.owner));
    }
    
    public int getOwner() {
        return this.owner;
    }
    
    public int getJobId() {
        return this.jobid;
    }
    
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }
}
