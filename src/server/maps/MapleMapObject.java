package server.maps;

import java.awt.Point;

import client.MapleClient;

public interface MapleMapObject
{
    int getObjectId();
    
    void setObjectId(final int p0);
    
    MapleMapObjectType getType();
    
    Point getPosition();
    
    void setPosition(final Point p0);
    
    void sendSpawnData(final MapleClient p0);
    
    void sendDestroyData(final MapleClient p0);
}
