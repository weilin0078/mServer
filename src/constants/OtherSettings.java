package constants;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherSettings
{
    private static OtherSettings instance;
    private static boolean CANLOG;
    private Properties itempb_cfg;
    private String[] itempb_id;
    private String[] itemjy_id;
    private String[] itemgy_id;
    private String[] mappb_id;
    private static Logger log;
    
    public OtherSettings() {
        this.itempb_cfg = new Properties();
        try {
            final InputStreamReader is = new FileReader("OtherSettings.ini");
            this.itempb_cfg.load(is);
            is.close();
            this.itempb_id = this.itempb_cfg.getProperty("cashban").split(",");
            this.itemjy_id = this.itempb_cfg.getProperty("cashjy", "0").split(",");
            this.itemgy_id = this.itempb_cfg.getProperty("gysj", "0").split(",");
        }
        catch (Exception e) {
            OtherSettings.log.error("Could not configuration", (Throwable)e);
        }
    }
    
    public String[] getItempb_id() {
        return this.itempb_id;
    }
    
    public String[] getItemgy_id() {
        return this.itemgy_id;
    }
    
    public String[] getItemjy_id() {
        return this.itemjy_id;
    }
    
    public String[] getMappb_id() {
        return this.mappb_id;
    }
    
    public boolean isCANLOG() {
        return OtherSettings.CANLOG;
    }
    
    public void setCANLOG(final boolean CANLOG) {
        OtherSettings.CANLOG = CANLOG;
    }
    
    public static OtherSettings getInstance() {
        if (OtherSettings.instance == null) {
            OtherSettings.instance = new OtherSettings();
        }
        return OtherSettings.instance;
    }
    
    static {
        OtherSettings.instance = null;
        OtherSettings.log = LoggerFactory.getLogger((Class)OtherSettings.class);
    }
}
