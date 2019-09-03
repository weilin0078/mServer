package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import server.MapleCarnivalFactory;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import tools.MaplePacketCreator;

public class SpawnPoint extends Spawns
{
    private MapleMonster monster;
    private Point pos;
    private long nextPossibleSpawn;
    private int mobTime;
    private int carnival;
    private int level;
    private AtomicInteger spawnedMonsters;
    private boolean immobile;
    private String msg;
    private byte carnivalTeam;
    
    public SpawnPoint(final MapleMonster monster, final Point pos, final int mobTime, final byte carnivalTeam, final String msg) {
        this.carnival = -1;
        this.level = -1;
        this.spawnedMonsters = new AtomicInteger(0);
        this.monster = monster;
        this.pos = pos;
        this.mobTime = ((mobTime < 0) ? -1 : (mobTime * 1000));
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;
        this.immobile = !monster.getStats().getMobile();
        this.nextPossibleSpawn = System.currentTimeMillis();
    }
    
    public final void setLevel(final int c) {
        this.level = c;
    }
    
    public final void setCarnival(final int c) {
        this.carnival = c;
    }
    
    @Override
    public final Point getPosition() {
        return this.pos;
    }
    
    @Override
    public final MapleMonster getMonster() {
        return this.monster;
    }
    
    @Override
    public final byte getCarnivalTeam() {
        return this.carnivalTeam;
    }
    
    @Override
    public final int getCarnivalId() {
        return this.carnival;
    }
    
    @Override
    public final boolean shouldSpawn() {
        return this.mobTime >= 0 && ((this.mobTime == 0 && !this.immobile) || this.spawnedMonsters.get() <= 0) && this.spawnedMonsters.get() <= 1 && this.nextPossibleSpawn <= System.currentTimeMillis();
    }
    
    @Override
    public final MapleMonster spawnMonster(final MapleMap map) {
        final MapleMonster mob = new MapleMonster(this.monster);
        mob.setPosition(this.pos);
        mob.setCarnivalTeam(this.carnivalTeam);
        if (this.level > -1) {
            mob.changeLevel(this.level, true);
        }
        this.spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {
            @Override
            public void monsterKilled() {
                SpawnPoint.this.nextPossibleSpawn = System.currentTimeMillis();
                if (SpawnPoint.this.mobTime > 0) {
                    SpawnPoint.this.nextPossibleSpawn += SpawnPoint.this.mobTime;
                }
                SpawnPoint.this.spawnedMonsters.decrementAndGet();
            }
        });
        map.spawnMonster(mob, -2);
        if (this.carnivalTeam > -1) {
            for (final MapleReactor r : map.getAllReactorsThreadsafe()) {
                if (r.getName().startsWith(String.valueOf(this.carnivalTeam)) && r.getReactorId() == 9980000 + this.carnivalTeam && r.getState() < 5) {
                    final int num = Integer.parseInt(r.getName().substring(1, 2));
                    final MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                    if (skil == null) {
                        continue;
                    }
                    skil.getSkill().applyEffect(null, mob, false);
                }
            }
        }
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
