package handling.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.MapleClient;
import constants.ServerConstants;
import handling.RecvPacketOpcode;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.MapleCustomEncryption;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericLittleEndianAccessor;

public class MaplePacketDecoder extends CumulativeProtocolDecoder
{
    public static final String DECODER_STATE_KEY;
    private static Logger log;
    
    protected boolean doDecode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out) throws Exception {
        DecoderState decoderState = (DecoderState)session.getAttribute((Object)MaplePacketDecoder.DECODER_STATE_KEY);
        if (decoderState == null) {
            decoderState = new DecoderState();
            session.setAttribute((Object)MaplePacketDecoder.DECODER_STATE_KEY, (Object)decoderState);
        }
        final MapleClient client = (MapleClient)session.getAttribute((Object)"CLIENT");
        if (decoderState.packetlength == -1) {
            if (in.remaining() >= 4) {
                final int packetHeader = in.getInt();
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    session.close();
                    return false;
                }
                decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
            }
            else if (in.remaining() < 4 && decoderState.packetlength == -1) {
                MaplePacketDecoder.log.trace("\u89e3\u7801\u2026\u6ca1\u6709\u8db3\u591f\u7684\u6570\u636e/\u5c31\u662f\u6240\u8c13\u7684\u5305\u4e0d\u5b8c\u6574");
                return false;
            }
        }
        if (in.remaining() >= decoderState.packetlength) {
            final byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.get(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            MapleCustomEncryption.decryptData(decryptedPacket);
            out.write((Object)decryptedPacket);
            if (ServerConstants.\u5c01\u5305\u663e\u793a) {
                final int packetLen = decryptedPacket.length;
                final int pHeader = this.readFirstShort(decryptedPacket);
                final String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                final String op = this.lookupSend(pHeader);
                boolean show = true;
                final String s = op;
                switch (s) {
                    case "PONG":
                    case "NPC_ACTION":
                    case "MOVE_LIFE":
                    case "MOVE_PLAYER":
                    case "MOVE_ANDROID":
                    case "MOVE_SUMMON":
                    case "AUTO_AGGRO":
                    case "HEAL_OVER_TIME":
                    case "BUTTON_PRESSED":
                    case "STRANGE_DATA": {
                        show = false;
                        break;
                    }
                }
                final String Send = "\u5ba2\u6237\u7aef\u53d1\u9001 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 3000) {
                    final String SendTo = Send + HexTool.toString(decryptedPacket) + "\r\n" + HexTool.toStringFromAscii(decryptedPacket);
                    if (show) {
                        FileoutputUtil.packetLog("log\\\u5ba2\u6237\u7aef\u5c01\u5305.log", SendTo);
                        System.out.println(SendTo);
                    }
                    final String SendTos = "\r\n\u65f6\u95f4\uff1a" + FileoutputUtil.CurrentReadable_Time() + "  ";
                    if (op.equals("UNKNOWN")) {
                        FileoutputUtil.packetLog("log\\\u672a\u77e5\u5ba2\u670d\u7aef\u5c01\u5305.log", SendTos + SendTo);
                    }
                }
                else {
                    MaplePacketDecoder.log.info(HexTool.toString(new byte[] { decryptedPacket[0], decryptedPacket[1] }) + "...");
                }
            }
            return true;
        }
        return false;
    }
    
    private String lookupSend(final int val) {
        for (final RecvPacketOpcode op : RecvPacketOpcode.values()) {
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
        DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";
        MaplePacketDecoder.log = LoggerFactory.getLogger((Class)MaplePacketDecoder.class);
    }
    
    public static class DecoderState
    {
        public int packetlength;
        
        public DecoderState() {
            this.packetlength = -1;
        }
    }
}
