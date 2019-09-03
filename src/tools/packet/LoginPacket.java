package tools.packet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import handling.login.LoginServer;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;

public class LoginPacket
{
    public static final MaplePacket getHello(final short mapleVersion, final byte[] sendIv, final byte[] recvIv) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getHello--------------------");
        }
        mplew.writeShort(13);
        mplew.writeShort(mapleVersion);
        mplew.write(new byte[] { 0, 0 });
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(4);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getPing--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PING.getValue());
        return mplew.getPacket();
    }
    
    public static final MaplePacket StrangeDATA() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("StrangeDATA--------------------");
        }
        mplew.writeShort(18);
        mplew.writeMapleAsciiString("30819F300D06092A864886F70D010101050003818D0030818902818100994F4E66B003A7843C944E67BE4375203DAA203C676908E59839C9BADE95F53E848AAFE61DB9C09E80F48675CA2696F4E897B7F18CCB6398D221C4EC5823D11CA1FB9764A78F84711B8B6FCA9F01B171A51EC66C02CDA9308887CEE8E59C4FF0B146BF71F697EB11EDCEBFCE02FB0101A7076A3FEB64F6F6022C8417EB6B87270203010001");
        return mplew.getPacket();
    }
    
    public static MaplePacket genderNeeded(final MapleClient c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("genderNeeded--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(c.getAccountName());
        return mplew.getPacket();
    }
    
    public static final MaplePacket getLoginFailed(final int reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getLoginFailed--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeInt(reason);
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getPermBan--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2);
        mplew.write(0);
        mplew.write(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));
        return mplew.getPacket();
    }
    
    public static final MaplePacket getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getTempBan--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
        mplew.write(reason);
        mplew.writeLong(timestampTill);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getGenderChanged(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getGenderChanged--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));
        return mplew.getPacket();
    }
    
    public static final MaplePacket getGenderNeeded(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getGenderNeeded--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());
        return mplew.getPacket();
    }
    
    public static final MaplePacket getAuthSuccessRequest(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getAuthSuccessRequest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.writeShort(client.isGm() ? 1 : 0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 03 01 00 00 00 E2 ED A3 7A FA C9 01"));
        mplew.writeInt(0);
        mplew.writeLong(0L);
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(1);
        return mplew.getPacket();
    }
    
    public static final MaplePacket deleteCharResponse(final int cid, final int state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("deleteCharResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);
        return mplew.getPacket();
    }
    
    public static final MaplePacket secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("secondPwError--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(mode);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getServerList(final int serverId, final String serverName, final Map<Integer, Integer> channelLoad) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getServerList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverId);
        mplew.writeMapleAsciiString(serverName);
        mplew.write(LoginServer.getFlag());
        mplew.writeMapleAsciiString(LoginServer.getEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);
        int lastChannel = 1;
        final Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; --i) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);
        mplew.writeInt(500);
        for (int j = 1; j <= lastChannel; ++j) {
            int load;
            if (channels.contains(j)) {
                load = channelLoad.get(j);
            }
            else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(serverName + "-" + j);
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.writeShort(j - 1);
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getEndOfServerList() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getEndOfServerList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(255);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getServerStatus--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);
        return mplew.getPacket();
    }
    
    public static final MaplePacket getCharList(final boolean secondpw, final List<MapleCharacter> chars, final int charslots) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("getCharList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(chars.size());
        for (final MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 10, false);
        }
        mplew.writeShort(3);
        mplew.writeInt(charslots);
        return mplew.getPacket();
    }
    
    public static final MaplePacket addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("addNewCharEntry--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);
        return mplew.getPacket();
    }
    
    public static final MaplePacket charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("charNameResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);
        return mplew.getPacket();
    }
    
    private static final void addCharEntry(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, final boolean ranking, final boolean viewAll) {
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("addCharEntry--------------------");
        }
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, viewAll);
        mplew.write(0);
        if (chr.getJob() == 900) {
            mplew.write(2);
        }
    }
}
