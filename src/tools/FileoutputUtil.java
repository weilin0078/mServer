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
    public static final String fixdam_mg = "Logs/ƒß∑®…À∫¶–ﬁ’˝.rtf";
    public static final String fixdam_ph = "Logs/ŒÔ¿Ì…À∫¶–ﬁ’˝.rtf";
    public static final String MobVac_log = "Logs/Log_Œ¸π÷.txt";
    public static final String hack_log = "Logs/Log_ª≥“…Õ‚π“.rtf";
    public static final String ban_log = "Logs/Log_∑‚∫≈.rtf";
    public static final String Acc_Stuck = "Logs/Log_ø®’À∫≈.rtf";
    public static final String Login_Error = "Logs/Log_µ«¬º¥ÌŒÛ.rtf";
    public static final String IP_Log = "Logs/Log_’À∫≈IP.rtf";
    public static final String Zakum_Log = "Logs/Log_‘˙¿•.rtf";
    public static final String Horntail_Log = "Logs/Log_∞µ∫⁄¡˙Õı.rtf";
    public static final String Pinkbean_Log = "Logs/Log_∆∑øÀÁÕ.rtf";
    public static final String ScriptEx_Log = "Logs/Log_Script_Ω≈±æ“Ï≥£.rtf";
    public static final String PacketEx_Log = "Logs/Log_Packet_∑‚∞¸“Ï≥£.rtf";
    private static final SimpleDateFormat sdf;
    private static final SimpleDateFormat sdf_;
    
    public static void logToFile_chr(final MapleCharacter chr, final String file, final String msg) {
        logToFile(file, "\r\n" + CurrentReadable_Time() + " ’À∫≈ " + chr.getClient().getAccountName() + " √˚≥∆ " + chr.getName() + " (" + chr.getId() + ") µ»º∂ " + chr.getLevel() + " µÿÕº " + chr.getMapId() + " " + msg, false);
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
        sdfT = new SimpleDateFormat("yyyyƒÍMM‘¬dd»’HHïrmm∑÷ss√Î");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    }
}
