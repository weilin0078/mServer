package tools.packet;

import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;

public class UIPacket
{
    public static final MaplePacket EarnTitleMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("EarnTitleMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.EARN_TITLE_MSG.getValue());
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }
    
    public static MaplePacket getSPMsg(final byte sp, final short job) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("getSPMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.writeShort(job);
        mplew.write(sp);
        return mplew.getPacket();
    }
    
    public static MaplePacket getGPMsg(final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("getGPMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(6);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }
    
    public static MaplePacket getTopMsg(final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("getTopMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.TOP_MSG.getValue());
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }
    
    public static MaplePacket getStatusMsg(final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("getStatusMsg--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(7);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }
    
    public static final MaplePacket MapEff(final String path) {
        if (ServerConstants.调试输出封包) {
            System.out.println("MapEff--------------------");
        }
        return MaplePacketCreator.environmentChange(path, 3);
    }
    
    public static final MaplePacket MapNameDisplay(final int mapid) {
        if (ServerConstants.调试输出封包) {
            System.out.println("MapNameDisplay--------------------");
        }
        return MaplePacketCreator.environmentChange("maplemap/enter/" + mapid, 3);
    }
    
    public static final MaplePacket Aran_Start() {
        if (ServerConstants.调试输出封包) {
            System.out.println("Aran_Start--------------------");
        }
        return MaplePacketCreator.environmentChange("Aran/balloon", 4);
    }
    
    public static final MaplePacket AranTutInstructionalBalloon(final String data) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("AranTutInstructionalBalloon--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(21);
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(1);
        return mplew.getPacket();
    }
    
    public static final MaplePacket ShowWZEffect(final String data, final int info) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("ShowWZEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        if (info == -1) {
            mplew.write(18);
        }
        else {
            mplew.write(23);
        }
        mplew.writeMapleAsciiString(data);
        if (info > -1) {
            mplew.writeInt(info);
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket summonHelper(final boolean summon) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("summonHelper--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SUMMON_HINT.getValue());
        mplew.write(summon ? 1 : 0);
        return mplew.getPacket();
    }
    
    public static MaplePacket summonMessage(final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("summonMessageA--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
        mplew.write(1);
        mplew.writeInt(type);
        mplew.writeInt(7000);
        return mplew.getPacket();
    }
    
    public static MaplePacket summonMessage(final String message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("summonMessageB--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(200);
        mplew.writeShort(0);
        mplew.writeInt(10000);
        return mplew.getPacket();
    }
    
    public static MaplePacket IntroLock(final boolean enable) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("IntroLock--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_LOCK.getValue());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }
    
    public static MaplePacket IntroDisableUI(final boolean enable) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("IntroDisableUI--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_DISABLE_UI.getValue());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }
    
    public static MaplePacket fishingUpdate(final byte type, final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("fishingUpdate--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FISHING_BOARD_UPDATE.getValue());
        mplew.write(type);
        mplew.writeInt(id);
        return mplew.getPacket();
    }
    
    public static MaplePacket fishingCaught(final int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("fishingCaught--------------------");
        }
        mplew.writeShort(SendPacketOpcode.FISHING_CAUGHT.getValue());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }
}
