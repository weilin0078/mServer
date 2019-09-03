package server.events;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import server.Randomizer;
import server.Timer;
import tools.MaplePacketCreator;

public class MapleOla extends MapleEvent
{
    private static final long serialVersionUID = 845748150824L;
    private long time;
    private long timeStarted;
    private transient ScheduledFuture<?> olaSchedule;
    private int[] stages;
    
    public MapleOla(final int channel, final int[] mapid) {
        super(channel, mapid);
        this.time = 600000L;
        this.timeStarted = 0L;
        this.stages = new int[3];
    }
    
    @Override
    public void finished(final MapleCharacter chr) {
        this.givePrize(chr);
    }
    
    @Override
    public void onMapLoad(final MapleCharacter chr) {
        if (this.isTimerStarted()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(this.getTimeLeft() / 1000L)));
        }
    }
    
    @Override
    public void startEvent() {
        this.unreset();
        super.reset();
        this.broadcast(MaplePacketCreator.getClock((int)(this.time / 1000L)));
        this.timeStarted = System.currentTimeMillis();
        this.olaSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < MapleOla.this.mapid.length; ++i) {
                    for (final MapleCharacter chr : MapleOla.this.getMap(i).getCharactersThreadsafe()) {
                        MapleOla.this.warpBack(chr);
                    }
                    MapleOla.this.unreset();
                }
            }
        }, this.time);
        this.broadcast(MaplePacketCreator.serverNotice(0, "\u95e8\u5df2\u6253\u5f00\u3002\u6309\u7bad\u5934\u2191\u952e\u8fdb\u5165\u5165\u53e3."));
    }
    
    public boolean isTimerStarted() {
        return this.timeStarted > 0L;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public void resetSchedule() {
        this.timeStarted = 0L;
        if (this.olaSchedule != null) {
            this.olaSchedule.cancel(false);
        }
        this.olaSchedule = null;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(false);
        this.stages = new int[] { 0, 0, 0 };
    }
    
    @Override
    public void unreset() {
        super.unreset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(true);
        this.stages = new int[] { Randomizer.nextInt(5), Randomizer.nextInt(8), Randomizer.nextInt(15) };
    }
    
    public long getTimeLeft() {
        return this.time - (System.currentTimeMillis() - this.timeStarted);
    }
    
    public boolean isCharCorrect(final String portalName, final int mapid) {
        final int st = this.stages[mapid % 10 - 1];
        return portalName.equals("ch" + ((st < 10) ? "0" : "") + st);
    }
}
