package constants;

import server.ServerProperties;

public class ServerConstants
{
    public static final boolean PollEnabled = false;
    public static final String Poll_Question = "Are you mudkiz?";
    public static final String[] Poll_Answers;
    public static final short MAPLE_VERSION = 79;
    public static final String MAPLE_PATCH = "1";
    public static final boolean Use_Fixed_IV = false;
    public static final int MIN_MTS = 110;
    public static final int MTS_BASE = 100;
    public static final int MTS_TAX = 10;
    public static final int MTS_MESO = 5000;
    public static final int CHANNEL_COUNT = 200;
    public static String PACKET_ERROR;
    public static boolean 封包显示;
    public static boolean 调试输出封包;
    public static final boolean PACKET_ERROR_OFF;
    public static boolean 自动注册;
    public static boolean Super_password;
    public static String superpw;
    
    public static boolean getAutoReg() {
        return ServerConstants.自动注册;
    }
    
    public static String ChangeAutoReg() {
        ServerConstants.自动注册 = !getAutoReg();
        return ServerConstants.自动注册 ? "开启" : "关闭";
    }
    
    public void setPACKET_ERROR(final String ERROR) {
        ServerConstants.PACKET_ERROR = ERROR;
    }
    
    public static final byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 3000:
            case 3200:
            case 3210:
            case 3211:
            case 3212:
            case 3300:
            case 3310:
            case 3311:
            case 3312:
            case 3500:
            case 3510:
            case 3511:
            case 3512: {
                return 10;
            }
            default: {
                return 0;
            }
        }
    }
    
    static {
        Poll_Answers = new String[] { "test1", "test2", "test3" };
        ServerConstants.PACKET_ERROR = "";
        ServerConstants.封包显示 = Boolean.parseBoolean(ServerProperties.getProperty("tms.封包显示", "false"));
        ServerConstants.调试输出封包 = Boolean.parseBoolean(ServerProperties.getProperty("tms.调试输出封包", "false"));
        PACKET_ERROR_OFF = Boolean.parseBoolean(ServerProperties.getProperty("KinMS.记录38错误", "false"));
        ServerConstants.自动注册 = false;
        ServerConstants.Super_password = false;
        ServerConstants.superpw = "";
    }
    
    public enum PlayerGMRank
    {
        NORMAL('@', 0), 
        INTERN('!', 1), 
        GM('!', 2), 
        ADMIN('!', 3);
        
        private char commandPrefix;
        private int level;
        
        private PlayerGMRank(final char ch, final int level) {
            this.commandPrefix = ch;
            this.level = level;
        }
        
        public char getCommandPrefix() {
            return this.commandPrefix;
        }
        
        public int getLevel() {
            return this.level;
        }
    }
    
    public enum CommandType
    {
        NORMAL(0), 
        TRADE(1);
        
        private int level;
        
        private CommandType(final int level) {
            this.level = level;
        }
        
        public int getType() {
            return this.level;
        }
    }
}
