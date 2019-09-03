package server.events;

public enum MapleEventType
{
    \u6253\u6930\u5b50\u6bd4\u8d5b("\u6930\u5b50\u6bd4\u8d5b", new int[] { 109080000 }), 
    \u6253\u74f6\u76d6\u6bd4\u8d5b("\u6253\u74f6\u76d6", new int[] { 109080010 }), 
    \u5411\u9ad8\u5730("\u5411\u9ad8\u5730", new int[] { 109040000, 109040001, 109040002, 109040003, 109040004 }), 
    \u4e0a\u697c\u4e0a\u697c("\u4e0a\u697c~\u4e0a\u697c~", new int[] { 109030001, 109030002, 109030003 }), 
    \u5feb\u901f0X\u731c\u9898("\u5feb\u901fOX\u731c\u9898", new int[] { 109020001 }), 
    \u96ea\u7403\u8d5b("\u96ea\u7403\u8d5b", new int[] { 109060000 });
    
    public String command;
    public int[] mapids;
    
    private MapleEventType(final String comm, final int[] mapids) {
        this.command = comm;
        this.mapids = mapids;
    }
    
    public static final MapleEventType getByString(final String splitted) {
        for (final MapleEventType t : values()) {
            if (t.command.equalsIgnoreCase(splitted)) {
                return t;
            }
        }
        return null;
    }
}
