package handling.mina;

import java.util.concurrent.locks.Lock;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.MapleClient;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.MapleCustomEncryption;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketEncoder implements ProtocolEncoder
{
    private static Logger log;
    
    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput out) throws Exception {
        final MapleClient client = (MapleClient)session.getAttribute((Object)"CLIENT");
        if (client != null) {
            final MapleAESOFB send_crypto = client.getSendCrypto();
            final byte[] inputInitialPacket = ((MaplePacket)message).getBytes();
            if (ServerConstants.\u5c01\u5305\u663e\u793a) {
                final int packetLen = inputInitialPacket.length;
                final int pHeader = this.readFirstShort(inputInitialPacket);
                final String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                final String op = this.lookupRecv(pHeader);
                boolean show = false;
                final String s = op;
                switch (s) {
                    case "WARP_TO_MAP":
                    case "PING":
                    case "NPC_ACTION":
                    case "UPDATE_STATS":
                    case "MOVE_PLAYER":
                    case "SPAWN_NPC":
                    case "SPAWN_NPC_REQUEST_CONTROLLER":
                    case "REMOVE_NPC":
                    case "MOVE_LIFE":
                    case "MOVE_MONSTER":
                    case "MOVE_MONSTER_RESPONSE":
                    case "SPAWN_MONSTER":
                    case "SPAWN_MONSTER_CONTROL":
                    case "ANDROID_MOVE": {
                        show = false;
                        break;
                    }
                }
                final String Recv = "\u670d\u52a1\u7aef\u53d1\u9001 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 50000) {
                    final String RecvTo = Recv + HexTool.toString(inputInitialPacket) + "\r\n" + HexTool.toStringFromAscii(inputInitialPacket);
                    if (show) {
                        FileoutputUtil.packetLog("log\\\u670d\u52a1\u7aef\u5c01\u5305.log", RecvTo);
                        System.out.println(RecvTo);
                    }
                }
                else {
                    MaplePacketEncoder.log.info(HexTool.toString(new byte[] { inputInitialPacket[0], inputInitialPacket[1] }) + " ...");
                }
            }
            final byte[] unencrypted = new byte[inputInitialPacket.length];
            System.arraycopy(inputInitialPacket, 0, unencrypted, 0, inputInitialPacket.length);
            final byte[] ret = new byte[unencrypted.length + 4];
            final Lock mutex = client.getLock();
            mutex.lock();
            try {
                final byte[] header = send_crypto.getPacketHeader(unencrypted.length);
                MapleCustomEncryption.encryptData(unencrypted);
                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
            }
            finally {
                mutex.unlock();
            }
            System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
            out.write((Object)IoBuffer.wrap(ret));
        }
        else {
            out.write((Object)IoBuffer.wrap(((MaplePacket)message).getBytes()));
        }
    }
    
    public void dispose(final IoSession session) throws Exception {
    }
    
    private String lookupRecv(final int val) {
        for (final SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }
    
    private int readFirstShort(final byte[] arr) {
        return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }
    
    static {
        MaplePacketEncoder.log = LoggerFactory.getLogger((Class)MaplePacketEncoder.class);
    }
}
