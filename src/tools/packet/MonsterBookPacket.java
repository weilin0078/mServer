package tools.packet;

import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;

public class MonsterBookPacket
{
    public static MaplePacket addCard(final boolean full, final int cardid, final int level) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("addCard--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTERBOOK_ADD.getValue());
        if (!full) {
            mplew.write(1);
            mplew.writeInt(cardid);
            mplew.writeInt(level);
        }
        else {
            mplew.write(0);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showGainCard(final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showGainCard--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(15);
        return mplew.getPacket();
    }
    
    public static MaplePacket showForeginCardEffect(final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showForeginCardEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(id);
        mplew.write(13);
        return mplew.getPacket();
    }
    
    public static MaplePacket changeCover(final int cardid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("changeCover--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTERBOOK_CHANGE_COVER.getValue());
        mplew.writeInt(cardid);
        return mplew.getPacket();
    }
}
