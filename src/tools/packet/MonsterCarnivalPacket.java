package tools.packet;

import client.MapleCharacter;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import server.MapleCarnivalParty;
import tools.data.output.MaplePacketLittleEndianWriter;

public class MonsterCarnivalPacket
{
    public static MaplePacket startMonsterCarnival(final MapleCharacter chr, final int enemyavailable, final int enemytotal) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("startMonsterCarnival--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_START.getValue());
        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        mplew.write(friendly.getTeam());
        mplew.writeShort(chr.getAvailableCP());
        mplew.writeShort(chr.getTotalCP());
        mplew.writeShort(friendly.getAvailableCP());
        mplew.writeShort(friendly.getTotalCP());
        mplew.writeShort(enemyavailable);
        mplew.writeShort(enemytotal);
        mplew.writeLong(0L);
        mplew.writeShort(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket playerDiedMessage(final String name, final int lostCP, final int team) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("playerDiedMessage--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_DIED.getValue());
        mplew.write(team);
        mplew.write(lostCP);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }
    
    public static MaplePacket CPUpdate(final boolean party, final int curCP, final int totalCP, final int team) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("CPUpdate--------------------");
        }
        if (!party) {
            mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        }
        else {
            mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_PARTY_CP.getValue());
            mplew.write(team);
        }
        mplew.writeShort(curCP);
        mplew.writeShort(totalCP);
        return mplew.getPacket();
    }
    
    public static MaplePacket playerSummoned(final String name, final int tab, final int number) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("playerSummoned--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        mplew.write(tab);
        mplew.write(number);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }
    
    public static MaplePacket playerSummoned1(final String name, final int tab, final int number) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.\u8c03\u8bd5\u8f93\u51fa\u5c01\u5305) {
            System.out.println("playerSummoned--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON1.getValue());
        mplew.write(tab);
        mplew.writeShort(number);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }
}
