package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;

import server.Randomizer;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class SpawnPointAreaBoss extends Spawns
{
    private MapleMonster monster;
    private Point pos1;
    private Point pos2;
    private Point pos3;
    private long nextPossibleSpawn;
    private int mobTime;
    private AtomicBoolean spawned;
    private String msg;
    
    public SpawnPointAreaBoss(final MapleMonster monster, final Point pos1, final Point pos2, final Point pos3, final int mobTime, final String msg) {
        this.spawned = new AtomicBoolean(false);
        this.monster = monster;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.mobTime = ((mobTime < 0) ? -1 : (mobTime * 1000));
        this.msg = msg;
        this.nextPossibleSpawn = System.currentTimeMillis();
    }
    
    @Override
    public final MapleMonster getMonster() {
        return this.monster;
    }
    
    @Override
    public final byte getCarnivalTeam() {
        return -1;
    }
    
    @Override
    public final int getCarnivalId() {
        return -1;
    }
    
    @Override
    public final boolean shouldSpawn() {
        return this.mobTime >= 0 && !this.spawned.get() && this.nextPossibleSpawn <= System.currentTimeMillis();
    }
    
    @Override
    public final Point getPosition() {
        final int rand = Randomizer.nextInt(3);
        return (rand == 0) ? this.pos1 : ((rand == 1) ? this.pos2 : this.pos3);
    }
    
    @Override
    public final MapleMonster spawnMonster(final MapleMap map) {
        final MapleMonster mob = new MapleMonster(this.monster);
        mob.setPosition(this.getPosition());
        this.spawned.set(true);
        mob.addListener(new MonsterListener() {
            @Override
            public void monsterKilled() {
                SpawnPointAreaBoss.this.nextPossibleSpawn = System.currentTimeMillis();
                if (SpawnPointAreaBoss.this.mobTime > 0) {
                    SpawnPointAreaBoss.this.nextPossibleSpawn += SpawnPointAreaBoss.this.mobTime;
                }
                SpawnPointAreaBoss.this.spawned.set(false);
            }
        });
        map.spawnMonster(mob, -2);
        if (this.msg != null) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.msg));
        }
        return mob;
    }
    
    @Override
    public final int getMobTime() {
        return this.mobTime;
    }
}
