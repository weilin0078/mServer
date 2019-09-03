package scripting;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager
{
    private final Map<MapleClient, NPCConversationManager> cms;
    private static final NPCScriptManager instance;
    
    public NPCScriptManager() {
        this.cms = new WeakHashMap<MapleClient, NPCConversationManager>();
    }
    
    public static final NPCScriptManager getInstance() {
        return NPCScriptManager.instance;
    }
    
    public void start(final MapleClient c, final int npc) {
        this.start(c, npc, 0);
    }
    
    public final void start(final MapleClient c, final int npc, final int wh) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isGM()) {
                if (wh == 0) {
                    c.getPlayer().dropMessage("[\u7cfb\u7edf\u63d0\u793a]\u60a8\u5df2\u7ecf\u5efa\u7acb\u4e0eNPC:" + npc + "\u7684\u5bf9\u8bdd\u3002");
                }
                else {
                    c.getPlayer().dropMessage("[\u7cfb\u7edf\u63d0\u793a]\u60a8\u5df2\u7ecf\u5efa\u7acb\u4e0eNPC:" + npc + "_" + wh + "\u7684\u5bf9\u8bdd\u3002");
                }
            }
            if (!this.cms.containsKey(c)) {
                Invocable iv;
                if (wh == 0) {
                    iv = this.getInvocable("npc/" + npc + ".js", c, true);
                }
                else {
                    iv = this.getInvocable("npc/" + npc + "_" + wh + ".js", c, true);
                }
                final ScriptEngine scriptengine = (ScriptEngine)iv;
                NPCConversationManager cm;
                if (wh == 0) {
                    cm = new NPCConversationManager(c, npc, -1, (byte)(-1), iv, 0);
                }
                else {
                    cm = new NPCConversationManager(c, npc, -1, (byte)(-1), iv, wh);
                }
                this.cms.put(c, cm);
                if (iv == null || getInstance() == null) {
                    if (wh == 0) {
                        cm.sendOk("\u6b22\u8fce\u6765\u5230#b\u5192\u9669\u5c9b#k\u3002\u5bf9\u4e0d\u8d77\u6682\u65f6\u65e0\u6cd5\u67e5\u8be2\u5230\u529f\u80fd\u3002\r\n\u6211\u7684ID\u662f: #r" + npc + "#k.\r\n ");
                    }
                    else {
                        cm.sendOk("\u6b22\u8fce\u6765\u5230#b\u5192\u9669\u5c9b#k\u3002\u5bf9\u4e0d\u8d77\u6682\u65f6\u65e0\u6cd5\u67e5\u8be2\u5230\u529f\u80fd\u3002\r\n\u6211\u7684ID\u662f: #r" + npc + "_" + wh + "#k.\r\n ");
                    }
                    cm.dispose();
                    return;
                }
                scriptengine.put("cm", cm);
                scriptengine.put("npcid", npc);
                c.getPlayer().setConversation(1);
                try {
                    iv.invokeFunction("start", new Object[0]);
                }
                catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", 1, 0, 0);
                }
            }
            else {
                c.getPlayer().dropMessage(5, "\u4f60\u73b0\u5728\u5df2\u7ecf\u5047\u6b7b\u8bf7\u4f7f\u7528@ea");
            }
        }
        catch (Exception e) {
            System.err.println("NPC \u8173\u672c\u932f\u8aa4, \u5b83ID\u70ba : " + npc + "_" + wh + "." + e);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a] NPC " + npc + "_" + wh + "\u8173\u672c\u932f\u8aa4 " + e + "");
            }
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing NPC script, NPC ID : " + npc + "_" + wh + "." + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }
    
    public void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        this.action(c, mode, type, selection, 0);
    }
    
    public final void action(final MapleClient c, final byte mode, final byte type, final int selection, final int wh) {
        if (mode != -1) {
            final NPCConversationManager cm = this.cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    this.dispose(c);
                }
                else if (wh == 0) {
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
                else {
                    cm.getIv().invokeFunction("action", mode, type, selection, wh);
                }
            }
            catch (Exception e) {
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a] NPC " + cm.getNpc() + "_" + wh + "\u8173\u672c\u932f\u8aa4 " + e + "");
                }
                System.err.println("NPC \u8173\u672c\u932f\u8aa4. \u5b83ID\u70ba : " + cm.getNpc() + "_" + wh + ":" + e);
                this.dispose(c);
                FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing NPC script, NPC ID : " + cm.getNpc() + "_" + wh + "." + e);
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!this.cms.containsKey(c)) {
                final Invocable iv = this.getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    this.dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine)iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)0, iv, 0);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);
                c.getPlayer().setConversation(1);
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u60a8\u5df2\u7d93\u5efa\u7acb\u8207\u4efb\u52d9\u8173\u672c:" + quest + "\u7684\u5f80\u4f86\u3002");
                }
                iv.invokeFunction("start", 1, 0, 0);
            }
            else {
                this.dispose(c);
            }
        }
        catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }
    
    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = this.cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                this.dispose(c);
            }
            else {
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + cm.getQuest() + "\u932f\u8aa4...NPC: " + cm.getNpc() + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }
    
    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!this.cms.containsKey(c)) {
                final Invocable iv = this.getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    this.dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine)iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)1, iv, 0);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);
                c.getPlayer().setConversation(1);
                iv.invokeFunction("end", 1, 0, 0);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + quest + "\u932f\u8aa4...NPC: " + quest + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }
    
    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = this.cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                this.dispose(c);
            }
            else {
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + cm.getQuest() + "\u932f\u8aa4...NPC: " + cm.getNpc() + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_\u811a\u672c\u5f02\u5e38.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }
    
    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = this.cms.get(c);
        if (npccm != null) {
            this.cms.remove(c);
            if (npccm.getType() == -1) {
                if (npccm.getwh() == 0) {
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                }
                else {
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + "_" + npccm.getwh() + ".js");
                }
                c.removeScriptEngine("scripts/npc/notcoded.js");
            }
            else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }
    
    public final NPCConversationManager getCM(final MapleClient c) {
        return this.cms.get(c);
    }
    
    static {
        instance = new NPCScriptManager();
    }
}
