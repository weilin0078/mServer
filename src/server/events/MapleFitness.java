package server.events;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import server.Timer;
import tools.MaplePacketCreator;

public class MapleFitness extends MapleEvent
{
    private static final long serialVersionUID = 845748950824L;
    private long time;
    private long timeStarted;
    private ScheduledFuture<?> fitnessSchedule;
    private ScheduledFuture<?> msgSchedule;
    
    public MapleFitness(final int channel, final int[] mapid) {
        super(channel, mapid);
        this.time = 600000L;
        this.timeStarted = 0L;
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
        this.checkAndMessage();
        this.fitnessSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < MapleFitness.this.mapid.length; ++i) {
                    for (final MapleCharacter chr : MapleFitness.this.getMap(i).getCharactersThreadsafe()) {
                        MapleFitness.this.warpBack(chr);
                    }
                }
                MapleFitness.this.unreset();
            }
        }, this.time);
        this.broadcast(MaplePacketCreator.serverNotice(0, "\u95e8\u5df2\u6253\u5f00\u3002\u8bb0\u5f97\u5728\u5149\u67f1\u95e8\u2191\u952e\u8fdb\u5165\u3002"));
    }
    
    public boolean isTimerStarted() {
        return this.timeStarted > 0L;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public void resetSchedule() {
        this.timeStarted = 0L;
        if (this.fitnessSchedule != null) {
            this.fitnessSchedule.cancel(false);
        }
        this.fitnessSchedule = null;
        if (this.msgSchedule != null) {
            this.msgSchedule.cancel(false);
        }
        this.msgSchedule = null;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(false);
    }
    
    @Override
    public void unreset() {
        super.unreset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(true);
    }
    
    public long getTimeLeft() {
        return this.time - (System.currentTimeMillis() - this.timeStarted);
    }
    
    public void checkAndMessage() {
        this.msgSchedule = Timer.EventTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                final long timeLeft = MapleFitness.this.getTimeLeft();
                if (timeLeft > 9000L && timeLeft < 11000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u5bf9\u4e8e\u4f60\u4eec\u65e0\u6cd5\u6218\u80dc\u7684\u6bd4\u8d5b\uff0c\u6211\u4eec\u5e0c\u671b\u60a8\u4e0b\u4e00\u6b21\u6218\u80dc\u5b83\uff01\u4e0b\u6b21\u518d\u89c1~"));
                }
                else if (timeLeft > 11000L && timeLeft < 101000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u597d\u5427\uff0c\u4f60\u5269\u4e0b\u7684\u65f6\u95f4\u6ca1\u6709\u591a\u5c11\u4e86\u3002\u8bf7\u6293\u7d27\u65f6\u95f4!"));
                }
                else if (timeLeft > 101000L && timeLeft < 241000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u7b2c4\u9636\u6bb5\uff0c\u662f\u6700\u540e\u4e00\u9879 [\u5192\u9669\u5c9b\u4f53\u80fd\u6d4b\u8bd5]. \u8bf7\u4e0d\u8981\u5728\u6700\u540e\u4e00\u523b\u653e\u5f03\uff0c\u62fc\u5c3d\u5168\u529b. \u4e30\u539a\u7684\u5956\u52b1\u5728\u6700\u9ad8\u5c42\u7b49\u7740\u4f60\u54e6!"));
                }
                else if (timeLeft > 241000L && timeLeft < 301000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u7b2c\u4e09\u9636\u6bb5\uff0c\u6709\u5f88\u591a\u9677\u9631\uff0c\u4f60\u53ef\u80fd\u4f1a\u770b\u5230\u4ed6\u4eec\uff0c\u4f46\u4f60\u4e0d\u80fd\u8e29\u4ed6\u4eec.\u6309\u7167\u4f60\u7684\u65b9\u5f0f\u8fdb\u884c\u4e0b\u53bb\u5427."));
                }
                else if (timeLeft > 301000L && timeLeft < 361000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u8bf7\u52a1\u5fc5\u6162\u6162\u5730\u79fb\u52a8\uff0c\u5c0f\u5fc3\u6389\u5230\u4e0b\u9762\u53bb\u3002"));
                }
                else if (timeLeft > 361000L && timeLeft < 501000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u8bf7\u8bb0\u4f4f\uff0c\u5982\u679c\u4f60\u5728\u6d3b\u52a8\u671f\u95f4\u6b7b\u6389\u4e86\uff0c\u4f60\u4f1a\u4ece\u6e38\u620f\u4e2d\u6dd8\u6c70. \u5982\u679c\u4f60\u60f3\u8865\u5145HP\uff0c\u4f7f\u7528\u836f\u6c34\u6216\u79fb\u52a8\u4e4b\u524d\u5148\u6062\u590dHP\u3002"));
                }
                else if (timeLeft > 501000L && timeLeft < 601000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u6700\u91cd\u8981\u7684\u662f\u4f60\u9700\u8981\u77e5\u9053\uff0c\u4e0d\u8981\u88ab\u7334\u5b50\u6254\u7684\u9999\u8549\u6253\u4e2d\uff0c\u8bf7\u5728\u89c4\u5b9a\u7684\u65f6\u95f4\u5b8c\u6210\u4e00\u5207\uff01"));
                }
                else if (timeLeft > 601000L && timeLeft < 661000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u7b2c\u4e8c\u9636\u6bb5\u969c\u788d\u662f\u7334\u5b50\u6254\u9999\u8549. \u8bf7\u786e\u4fdd\u6cbf\u7740\u6b63\u786e\u7684\u8def\u7ebf\u79fb\u52a8\uff0c\u8eb2\u907f\u5b83\u4eec\u7684\u653b\u51fb\u3002"));
                }
                else if (timeLeft > 661000L && timeLeft < 701000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u8bf7\u8bb0\u4f4f\uff0c\u5982\u679c\u4f60\u5728\u6d3b\u52a8\u671f\u95f4\u6b7b\u6389\u4e86\uff0c\u4f60\u4f1a\u4ece\u6e38\u620f\u4e2d\u6dd8\u6c70. \u5982\u679c\u4f60\u60f3\u8865\u5145HP\uff0c\u4f7f\u7528\u836f\u6c34\u6216\u79fb\u52a8\u4e4b\u524d\u5148\u6062\u590dHP\u3002"));
                }
                else if (timeLeft > 701000L && timeLeft < 781000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u6bcf\u4e2a\u4eba\u90fd\u8be5\u53c2\u52a0 [\u5192\u9669\u5c9b\u4f53\u80fd\u6d4b\u8bd5] \u53c2\u52a0\u8fd9\u4e2a\u9879\u76ee\uff0c\u65e0\u8bba\u5b8c\u6210\u7684\u987a\u5e8f\uff0c\u90fd\u4f1a\u83b7\u5f97\u5956\u52b1\uff0c\u6240\u4ee5\u53ea\u662f\u5a31\u4e50\uff0c\u6162\u6162\u6765\uff0c\u6e05\u9664\u4e864\u4e2a\u9636\u6bb5\u3002"));
                }
                else if (timeLeft > 781000L && timeLeft < 841000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "\u4e0d\u8981\u754f\u60e7\u82e6\u96be\uff0c\u7ee7\u7eed\u4e0b\u53bb\uff0c\u4eab\u53d7\u6e38\u620f\uff0c\u91cd\u5728\u5a31\u4e50\u561b."));
                }
                else if (timeLeft > 841000L) {
                    MapleFitness.this.broadcast(MaplePacketCreator.serverNotice(0, "[\u5192\u9669\u5c9b\u4f53\u80fd\u6d4b\u8bd5]\u67094\u4e2a\u9636\u6bb5\uff0c\u5982\u679c\u4f60\u78b0\u5de7\u5728\u6e38\u620f\u8fc7\u7a0b\u4e2d\u6b7b\u4ea1\uff0c\u4f60\u4f1a\u4ece\u6bd4\u8d5b\u88ab\u6dd8\u6c70\uff0c\u6240\u4ee5\u8bf7\u6ce8\u610f\u8fd9\u4e00\u70b9\u3002"));
                }
            }
        }, 90000L);
    }
}
