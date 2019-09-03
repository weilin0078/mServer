package server.events;

import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.Timer;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.MaplePacketCreator;

public abstract class MapleEvent
{
    protected int[] mapid;
    protected int channel;
    protected boolean isRunning;
    
    public MapleEvent(final int channel, final int[] mapid) {
        this.isRunning = false;
        this.channel = channel;
        this.mapid = mapid;
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public MapleMap getMap(final int i) {
        return this.getChannelServer().getMapFactory().getMap(this.mapid[i]);
    }
    
    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }
    
    public void broadcast(final MaplePacket packet) {
        for (int i = 0; i < this.mapid.length; ++i) {
            this.getMap(i).broadcastMessage(packet);
        }
    }
    
    public void givePrize(final MapleCharacter chr) {
        final int reward = RandomRewards.getInstance().getEventReward();
        if (reward == 0) {
            chr.gainMeso(66666, true, false, false);
            chr.dropMessage(5, "\u4f60\u83b7\u5f97 166666 \u5192\u9669\u5e01");
        }
        else if (reward == 1) {
            chr.gainMeso(399999, true, false, false);
            chr.dropMessage(5, "\u4f60\u83b7\u5f97 399999 \u5192\u9669\u5e01");
        }
        else if (reward == 2) {
            chr.modifyCSPoints(0, 200, false);
            chr.dropMessage(5, "\u4f60\u83b7\u5f97 200 \u70b9\u5377");
        }
        else if (reward == 3) {
            chr.addFame(2);
            chr.dropMessage(5, "\u4f60\u83b7\u5f97 2 \u4eba\u6c14");
        }
        if (MapleInventoryManipulator.checkSpace(chr.getClient(), 4032226, 1, "")) {
            MapleInventoryManipulator.addById(chr.getClient(), 4032226, (short)1, (byte)0);
            chr.dropMessage(5, "\u4f60\u83b7\u5f97 1 \u4e2a\u9ec4\u91d1\u732a\u732a");
        }
        else {
            chr.gainMeso(100000, true, false, false);
            chr.dropMessage(5, "\u7531\u4e8e\u4f60\u80cc\u5305\u6ee1\u4e86\u3002\u6240\u4ee5\u53ea\u80fd\u7ed9\u4e88\u4f60\u5192\u9669\u5e01\uff01");
        }
    }
    
    public void finished(final MapleCharacter chr) {
    }
    
    public void onMapLoad(final MapleCharacter chr) {
    }
    
    public void startEvent() {
    }
    
    public void warpBack(final MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104000000;
        }
        final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }
    
    public void reset() {
        this.isRunning = true;
    }
    
    public void unreset() {
        this.isRunning = false;
    }
    
    public static final void setEvent(final ChannelServer cserv, final boolean auto) {
        if (auto) {
            for (final MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (e.isRunning) {
                    for (final int i : e.mapid) {
                        if (cserv.getEvent() == i) {
                            e.broadcast(MaplePacketCreator.serverNotice(0, "\u8ddd\u79bb\u6d3b\u52a8\u5f00\u59cb\u53ea\u5269\u4e0b\u4e00\u5206\u949f!"));
                            e.broadcast(MaplePacketCreator.getClock(60));
                            Timer.EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    e.startEvent();
                                }
                            }, 60000L);
                            break;
                        }
                    }
                }
            }
        }
        cserv.setEvent(-1);
    }
    
    public static final void mapLoad(final MapleCharacter chr, final int channel) {
        if (chr == null) {
            return;
        }
        for (final MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
                if (chr.getMapId() == 109050000) {
                    e.finished(chr);
                }
                for (final int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.onMapLoad(chr);
                    }
                }
            }
        }
    }
    
    public static final void onStartEvent(final MapleCharacter chr) {
        for (final MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (e.isRunning) {
                for (final int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.startEvent();
                        chr.dropMessage(5, String.valueOf(t) + " \u6d3b\u52a8\u5f00\u59cb");
                    }
                }
            }
        }
    }
    
    public static final String scheduleEvent(final MapleEventType event, final ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "\u6539\u6d3b\u52a8\u5df2\u7ecf\u88ab\u7981\u6b62\u5b89\u6392\u4e86.";
        }
        for (final int i : cserv.getEvent(event).mapid) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
                return "\u8be5\u6d3b\u52a8\u5df2\u7ecf\u5728\u6267\u884c\u4e2d.";
            }
        }
        cserv.setEvent(cserv.getEvent(event).mapid[0]);
        cserv.getEvent(event).reset();
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "\u6d3b\u52a8 " + String.valueOf(event) + " \u5373\u5c06\u5728\u9891\u9053 " + cserv.getChannel() + " \u4e3e\u884c , \u8981\u53c2\u52a0\u7684\u73a9\u5bb6\u8bf7\u5230\u9891\u9053 " + cserv.getChannel() + ".\u8bf7\u627e\u5230\u81ea\u7531\u5e02\u573a\u76f8\u6846\u6d3b\u52a8npc\u5e76\u8fdb\u5165\uff01").getBytes());
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "\u6d3b\u52a8 " + String.valueOf(event) + " \u5373\u5c06\u5728\u9891\u9053 " + cserv.getChannel() + " \u4e3e\u884c , \u8981\u53c2\u52a0\u7684\u73a9\u5bb6\u8bf7\u5230\u9891\u9053 " + cserv.getChannel() + ".\u8bf7\u627e\u5230\u81ea\u7531\u5e02\u573a\u76f8\u6846\u6d3b\u52a8npc\u5e76\u8fdb\u5165\uff01").getBytes());
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "\u6d3b\u52a8 " + String.valueOf(event) + " \u5373\u5c06\u5728\u9891\u9053 " + cserv.getChannel() + " \u4e3e\u884c , \u8981\u53c2\u52a0\u7684\u73a9\u5bb6\u8bf7\u5230\u9891\u9053 " + cserv.getChannel() + ".\u8bf7\u627e\u5230\u81ea\u7531\u5e02\u573a\u76f8\u6846\u6d3b\u52a8npc\u5e76\u8fdb\u5165\uff01").getBytes());
        return "";
    }
}
