package tools.data.output;

import java.io.ByteArrayOutputStream;

import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import server.ServerProperties;
import tools.HexTool;

public class MaplePacketLittleEndianWriter extends GenericLittleEndianWriter
{
    private final ByteArrayOutputStream baos;
    private static boolean debugMode;
    
    public MaplePacketLittleEndianWriter() {
        this(32);
    }
    
    public MaplePacketLittleEndianWriter(final int size) {
        this.baos = new ByteArrayOutputStream(size);
        this.setByteOutputStream(new BAOSByteOutputStream(this.baos));
    }
    
    public final MaplePacket getPacket() {
        if (MaplePacketLittleEndianWriter.debugMode) {
            final MaplePacket packet = new ByteArrayMaplePacket(this.baos.toByteArray());
            System.out.println("Packet to be sent:\n" + packet.toString());
        }
        return new ByteArrayMaplePacket(this.baos.toByteArray());
    }
    
    @Override
    public final String toString() {
        return HexTool.toString(this.baos.toByteArray());
    }
    
    static {
        MaplePacketLittleEndianWriter.debugMode = Boolean.parseBoolean(ServerProperties.getProperty("tms.Debug", "false"));
    }
}
