package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import client.MapleCharacter;

public class FileoutputUtil
{
    private static final SimpleDateFormat sdfT;
    public static final String fixdam_mg = "Logs/\u9b54\u6cd5\u4f24\u5bb3\u4fee\u6b63.rtf";
    public static final String fixdam_ph = "Logs/\u7269\u7406\u4f24\u5bb3\u4fee\u6b63.rtf";
    public static final String MobVac_log = "Logs/Log_\u5438\u602a.txt";
    public static final String hack_log = "Logs/Log_\u6000\u7591\u5916\u6302.rtf";
    public static final String ban_log = "Logs/Log_\u5c01\u53f7.rtf";
    public static final String Acc_Stuck = "Logs/Log_\u5361\u8d26\u53f7.rtf";
    public static final String Login_Error = "Logs/Log_\u767b\u5f55\u9519\u8bef.rtf";
    public static final String IP_Log = "Logs/Log_\u8d26\u53f7IP.rtf";
    public static final String Zakum_Log = "Logs/Log_\u624e\u6606.rtf";
    public static final String Horntail_Log = "Logs/Log_\u6697\u9ed1\u9f99\u738b.rtf";
    public static final String Pinkbean_Log = "Logs/Log_\u54c1\u514b\u7f24.rtf";
    public static final String ScriptEx_Log = "Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf";
    public static final String PacketEx_Log = "Logs/Log_Packet_\u5c01\u5305\u5f02\u5e38.rtf";
    private static final SimpleDateFormat sdf;
    private static final SimpleDateFormat sdf_;
    
    public static void logToFile_chr(final MapleCharacter chr, final String file, final String msg) {
        logToFile(file, "\r\n" + CurrentReadable_Time() + " \u8d26\u53f7 " + chr.getClient().getAccountName() + " \u540d\u79f0 " + chr.getName() + " (" + chr.getId() + ") \u7b49\u7ea7 " + chr.getLevel() + " \u5730\u56fe " + chr.getMapId() + " " + msg, false);
    }
    
    public static void logToFile(final String file, final String msg) {
        logToFile(file, msg, false);
    }
    
    public static void logToFile(final String file, final String msg, final boolean notExists) {
        FileOutputStream out = null;
        try {
            File outputFile = new File(file);
            if (outputFile.exists() && outputFile.isFile() && outputFile.length() >= 10240000L) {
                outputFile.renameTo(new File(file.substring(0, file.length() - 4) + "_" + FileoutputUtil.sdfT.format(Calendar.getInstance().getTime()) + file.substring(file.length() - 4, file.length())));
                outputFile = new File(file);
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            if (!out.toString().contains(msg) || !notExists) {
                final OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(msg);
                osw.flush();
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void packetLog(final String file, final String msg) {
        final boolean notExists = false;
        FileOutputStream out = null;
        try {
            File outputFile = new File(file);
            if (outputFile.exists() && outputFile.isFile() && outputFile.length() >= 1024000L) {
                outputFile.renameTo(new File(file.substring(0, file.length() - 4) + "_" + FileoutputUtil.sdfT.format(Calendar.getInstance().getTime()) + file.substring(file.length() - 4, file.length())));
                outputFile = new File(file);
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            if (!out.toString().contains(msg) || !notExists) {
                final OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(msg);
                osw.flush();
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void log(final String file, final String msg) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(msg.getBytes());
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void outputFileError(final String file, final Throwable t) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(getString(t).getBytes());
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static String CurrentReadable_Date() {
        return FileoutputUtil.sdf_.format(Calendar.getInstance().getTime());
    }
    
    public static String CurrentReadable_Time() {
        return FileoutputUtil.sdf.format(Calendar.getInstance().getTime());
    }
    
    public static String getString(final Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        }
        finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            }
            catch (IOException ex) {}
        }
        return retValue;
    }
    
    public static String NowTime() {
        final Date now = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final String hehe = dateFormat.format(now);
        return hehe;
    }
    
    static {
        sdfT = new SimpleDateFormat("yyyy\u5e74MM\u6708dd\u65e5HH\u6642mm\u5206ss\u79d2");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    }
}
