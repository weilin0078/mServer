package tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class GetInfo
{
    public static void main(final String[] args) {
        Config();
        getConfig();
        all();
    }
    
    public static void getIpconfig() {
        final Map<String, String> map = System.getenv();
        System.out.println(map);
        System.out.println(map.get("USERNAME"));
        System.out.println(map.get("COMPUTERNAME"));
        System.out.println(map.get("USERDOMAIN"));
        System.out.println(map.get("USER"));
    }
    
    public static void all() {
        final Properties \u8a2d\u5b9a\u6a94 = System.getProperties();
        System.out.println("Java\u7684\u904b\u884c\u74b0\u5883\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.version"));
        System.out.println("Java\u7684\u904b\u884c\u74b0\u5883\u4f9b\u61c9\u5546\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vendor"));
        System.out.println("Java\u4f9b\u61c9\u5546\u7684URL\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vendor.url"));
        System.out.println("Java\u7684\u5b89\u88dd\u8def\u5f91\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.home"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u898f\u7bc4\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.specification.version"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u898f\u7bc4\u4f9b\u61c9\u5546\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.specification.vendor"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u898f\u7bc4\u540d\u7a31\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.specification.name"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u5be6\u73fe\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.version"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u5be6\u73fe\u4f9b\u61c9\u5546\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.vendor"));
        System.out.println("Java\u7684\u865b\u64ec\u6a5f\u5be6\u73fe\u540d\u7a31\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.vm.name"));
        System.out.println("Java\u904b\u884c\u6642\u74b0\u5883\u898f\u7bc4\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.specification.version"));
        System.out.println("Java\u904b\u884c\u6642\u74b0\u5883\u898f\u7bc4\u540d\u7a31\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.specification.name"));
        System.out.println("Java\u7684\u985e\u683c\u5f0f\u7248\u672c\u865f\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.class.version"));
        System.out.println("Java\u7684\u985e\u8def\u5f91\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.class.path"));
        System.out.println("\u52a0\u8f09\u5eab\u6642\u641c\u7d22\u7684\u8def\u5f91\u5217\u8868\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.library.path"));
        System.out.println("\u9ed8\u8a8d\u7684\u81e8\u6642\u6587\u4ef6\u8def\u5f91\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.io.tmpdir"));
        System.out.println("\u4e00\u500b\u6216\u591a\u500b\u64f4\u5c55\u76ee\u9304\u7684\u8def\u5f91\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("java.ext.dirs"));
        System.out.println("\u64cd\u4f5c\u7cfb\u7d71\u7684\u69cb\u67b6\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("os.arch"));
        System.out.println("\u64cd\u4f5c\u7cfb\u7d71\u7684\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("os.version"));
        System.out.println("\u6587\u4ef6\u5206\u9694\u7b26\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("file.separator"));
        System.out.println("\u8def\u5f91\u5206\u9694\u7b26\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("path.separator"));
        System.out.println("\u884c\u5206\u9694\u7b26\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("line.separator"));
        System.out.println("\u7528\u6236\u7684\u8cec\u6236\u540d\u7a31\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("user.name"));
        System.out.println("\u7528\u6236\u7684\u4e3b\u76ee\u9304\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("user.home"));
        System.out.println("\u7528\u6236\u7684\u7576\u524d\u5de5\u4f5c\u76ee\u9304\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("user.dir"));
    }
    
    public static void Config() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            final String ip = addr.getHostAddress().toString();
            final String hostName = addr.getHostName().toString();
            System.out.println("\u672c\u6a5fIP\uff1a" + ip + "\n\u672c\u6a5f\u540d\u7a31:" + hostName);
            final Properties \u8a2d\u5b9a\u6a94 = System.getProperties();
            System.out.println("\u64cd\u4f5c\u7cfb\u7d71\u7684\u540d\u7a31\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("os.name"));
            System.out.println("\u64cd\u4f5c\u7cfb\u7d71\u7684\u7248\u672c\uff1a" + \u8a2d\u5b9a\u6a94.getProperty("os.version"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getConfig() {
        try {
            final InetAddress address = InetAddress.getLocalHost();
            final NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            byte[] mac = ni.getHardwareAddress();
            if (mac == null) {
                mac = ni.getInetAddresses().nextElement().getAddress();
            }
            final String sIP = address.getHostAddress();
            String sMAC = "";
            final Formatter formatter = new Formatter();
            for (int i = 0; i < mac.length; ++i) {
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
            }
            System.out.println("IP\uff1a" + sIP);
            System.out.println("MAC\uff1a" + sMAC);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
