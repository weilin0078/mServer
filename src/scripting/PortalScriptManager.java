package scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import server.MaplePortal;
import tools.FileoutputUtil;

public class PortalScriptManager
{
    private static final PortalScriptManager instance;
    private final Map<String, PortalScript> scripts;
    private static final ScriptEngineFactory sef;
    
    public PortalScriptManager() {
        this.scripts = new HashMap<String, PortalScript>();
    }
    
    public static final PortalScriptManager getInstance() {
        return PortalScriptManager.instance;
    }
    
    private PortalScript getPortalScript(final MapleClient c, final String scriptName) {
        if (this.scripts.containsKey(scriptName)) {
            this.scripts.clear();
            return this.scripts.get(scriptName);
        }
        final File scriptFile = new File("scripts/portal/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            return null;
        }
        InputStream fr = null;
        final ScriptEngine portal = PortalScriptManager.sef.getScriptEngine();
        try {
            fr = new FileInputStream(scriptFile);
            final BufferedReader bf = new BufferedReader(new InputStreamReader(fr, EncodingDetect.getJavaEncode(scriptFile)));
            final CompiledScript compiled = ((Compilable)portal).compile(bf);
            compiled.eval();
        }
        catch (Exception e) {
            System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing Portal script. (" + scriptName + ") " + e);
            if (fr != null) {
                try {
                    fr.close();
                }
                catch (IOException e2) {
                    System.err.println("ERROR CLOSING" + e2);
                }
            }
        }
        finally {
            if (fr != null) {
                try {
                    fr.close();
                }
                catch (IOException e3) {
                    System.err.println("ERROR CLOSING" + e3);
                }
            }
        }
        final PortalScript script = ((Invocable)portal).getInterface(PortalScript.class);
        this.scripts.put(scriptName, script);
        return script;
    }
    
    public final void executePortalScript(final MaplePortal portal, final MapleClient c) {
        final PortalScript script = this.getPortalScript(c, portal.getScriptName());
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("[\u7cfb\u7edf\u63d0\u793a]\u60a8\u5df2\u7ecf\u5efa\u7acb\u4e0ePortalScript:[" + portal.getScriptName() + ".js]\u7684\u5bf9\u8bdd\u3002" + ((script != null) ? "" : "(\u811a\u672c\u4e0d\u5b58\u5728\u6216\u5f02\u5e38)"));
        }
        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
            }
            catch (Exception e) {
                System.err.println("Error entering Portalscript: " + portal.getScriptName() + ":" + e);
            }
        }
        else {
            System.out.println("Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
        }
    }
    
    public final void clearScripts() {
        this.scripts.clear();
    }
    
    static {
        instance = new PortalScriptManager();
        sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();
    }
}
