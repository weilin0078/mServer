package handling.channel.handler;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BeanGame
{
    public static void BeanGame1(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        final List<Beans> beansInfo = new ArrayList<Beans>();
        final int type = slea.readByte();
        int \u529b\u5ea6 = 0;
        int \u8c46\u8c46\u5e8f\u865f = 0;
        if (type == 1) {
            \u529b\u5ea6 = slea.readShort();
            chr.setBeansRange(\u529b\u5ea6);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
        else if (type == 0) {
            \u529b\u5ea6 = slea.readShort();
            \u8c46\u8c46\u5e8f\u865f = slea.readInt() + 1;
            chr.setBeansRange(\u529b\u5ea6);
            chr.setBeansNum(\u8c46\u8c46\u5e8f\u865f);
            if (\u8c46\u8c46\u5e8f\u865f == 1) {
                chr.setCanSetBeansNum(false);
            }
        }
        else if (type == 2) {
            if (type == 11 || type == 0) {
                \u529b\u5ea6 = slea.readShort();
                \u8c46\u8c46\u5e8f\u865f = slea.readInt() + 1;
                chr.setBeansRange(\u529b\u5ea6);
                chr.setBeansNum(\u8c46\u8c46\u5e8f\u865f);
                if (\u8c46\u8c46\u5e8f\u865f == 1) {
                    chr.setCanSetBeansNum(false);
                }
            }
        }
        else if (type == 6) {
            slea.skip(1);
            final int \u5faa\u74b0\u6b21\u6578 = slea.readByte();
            if (\u5faa\u74b0\u6b21\u6578 == 0) {
                return;
            }
            if (\u5faa\u74b0\u6b21\u6578 != 1) {
                slea.skip((\u5faa\u74b0\u6b21\u6578 - 1) * 8);
            }
            if (chr.isCanSetBeansNum()) {
                chr.setBeansNum(chr.getBeansNum() + \u5faa\u74b0\u6b21\u6578);
            }
            chr.gainBeans(-\u5faa\u74b0\u6b21\u6578);
            chr.setCanSetBeansNum(true);
        }
        else if (type == 11 || type == 6) {
            \u529b\u5ea6 = slea.readShort();
            chr.setBeansRange(\u529b\u5ea6);
            final byte size = (byte)(slea.readByte() + 1);
            final short Pos = slea.readShort();
            final byte Type = (byte)(slea.readByte() + 1);
            c.getSession().write((Object)MaplePacketCreator.showBeans(\u529b\u5ea6, size, Pos, Type));
        }
        else {
            System.out.println("\u672a\u8655\u7406\u7684\u985e\u578b\u3010" + type + "\u3011\n\u5305" + slea.toString());
        }
    }
    
    private static int getBeanType() {
        final int random = rand(1, 100);
        int beanType = 0;
        switch (random) {
            case 2: {
                beanType = 1;
                break;
            }
            case 49: {
                beanType = 2;
                break;
            }
            case 99: {
                beanType = 3;
                break;
            }
        }
        return beanType;
    }
    
    private static int rand(final int lbound, final int ubound) {
        return (int)(Math.random() * (ubound - lbound + 1) + lbound);
    }
    
    public static final void BeanGame2(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getSession().write((Object)MaplePacketCreator.updateBeans(c.getPlayer().getId(), c.getPlayer().getBeans()));
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public class Beans
    {
        private int number;
        private int type;
        private int pos;
        
        public Beans(final int pos, final int type, final int number) {
            this.pos = pos;
            this.number = number;
            this.type = type;
        }
        
        public int getType() {
            return this.type;
        }
        
        public int getNumber() {
            return this.number;
        }
        
        public int getPos() {
            return this.pos;
        }
    }
}
