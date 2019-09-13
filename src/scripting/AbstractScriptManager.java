package scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import tools.FileoutputUtil;

public abstract class AbstractScriptManager
{
    private static final ScriptEngineManager sem;
    
    protected Invocable getInvocable(final String path, final MapleClient c) {
        return this.getInvocable(path, c, false);
    }
    
    protected Invocable getInvocable(String path, final MapleClient c, final boolean npc) {
        InputStream fr = null;
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;
            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                final File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = AbstractScriptManager.sem.getEngineByName("javascript");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                fr = new FileInputStream(scriptFile);
                final BufferedReader bf = new BufferedReader(new InputStreamReader(fr, EncodingDetect.getJavaEncode(scriptFile)));
                engine.eval(bf);
            }
            else if (c != null && npc) {
                c.getPlayer().dropMessage(5, "你现在已经假死请使用@ea");
            }
            return (Invocable)engine;
        }
        catch (Exception e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log("Logs/Log_Script_脚本异常.rtf", "Error executing script. Path: " + path + "\nException " + e);
            return null;
        }
        finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            }
            catch (IOException ex) {}
        }
    }
    
    static {
        sem = new ScriptEngineManager();
    }
}
