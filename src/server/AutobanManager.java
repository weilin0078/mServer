package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import client.MapleClient;
import handling.world.World;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;

public class AutobanManager implements Runnable
{
    private Map<Integer, Integer> points;
    private Map<Integer, List<String>> reasons;
    private Set<ExpirationEntry> expirations;
    private static final int AUTOBAN_POINTS = 5000;
    private static AutobanManager instance;
    private final ReentrantLock lock;
    
    public AutobanManager() {
        this.points = new HashMap<Integer, Integer>();
        this.reasons = new HashMap<Integer, List<String>>();
        this.expirations = new TreeSet<ExpirationEntry>();
        this.lock = new ReentrantLock(true);
    }
    
    public static final AutobanManager getInstance() {
        return AutobanManager.instance;
    }
    
    public final void autoban(final MapleClient c, final String reason) {
        if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
            c.getPlayer().dropMessage(5, "[WARNING] A/b triggled : " + reason);
            return;
        }
        this.addPoints(c, 5000, 0L, reason);
    }
    
    public final void addPoints(final MapleClient c, final int points, final long expiration, final String reason) {
        this.lock.lock();
        try {
            final int acc = c.getPlayer().getAccountID();
            if (this.points.containsKey(acc)) {
                final int SavedPoints = this.points.get(acc);
                if (SavedPoints >= 5000) {
                    return;
                }
                this.points.put(acc, SavedPoints + points);
                final List<String> reasonList = this.reasons.get(acc);
                reasonList.add(reason);
            }
            else {
                this.points.put(acc, points);
                final List<String> reasonList = new LinkedList<String>();
                reasonList.add(reason);
                this.reasons.put(acc, reasonList);
            }
            if (this.points.get(acc) >= 5000) {
                if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
                    c.getPlayer().dropMessage(5, "[WARNING] A/b triggled : " + reason);
                    return;
                }
                final StringBuilder sb = new StringBuilder("a/b ");
                sb.append(c.getPlayer().getName());
                sb.append(" (IP ");
                sb.append(c.getSession().getRemoteAddress().toString());
                sb.append("): ");
                sb.append(" (MAC ");
                sb.append(c.getMac());
                sb.append("): ");
                for (final String s : this.reasons.get(acc)) {
                    sb.append(s);
                    sb.append(", ");
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "'" + c.getPlayer().getName() + "'自动封号系统非法使用外挂程序！永久封停处理！").getBytes());
                FileoutputUtil.logToFile_chr(c.getPlayer(), "Logs/Log_封号.rtf", sb.toString());
                c.getPlayer().ban(sb.toString(), false, true, false);
                c.disconnect(true, false);
            }
            else if (expiration > 0L) {
                this.expirations.add(new ExpirationEntry(System.currentTimeMillis() + expiration, acc, points));
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public final void run() {
        final long now = System.currentTimeMillis();
        for (final ExpirationEntry e : this.expirations) {
            if (e.time > now) {
                return;
            }
            this.points.put(e.acc, this.points.get(e.acc) - e.points);
        }
    }
    
    static {
        AutobanManager.instance = new AutobanManager();
    }
    
    private static class ExpirationEntry implements Comparable<ExpirationEntry>
    {
        public long time;
        public int acc;
        public int points;
        
        public ExpirationEntry(final long time, final int acc, final int points) {
            this.time = time;
            this.acc = acc;
            this.points = points;
        }
        
        @Override
        public int compareTo(final ExpirationEntry o) {
            return (int)(this.time - o.time);
        }
        
        @Override
        public boolean equals(final Object oth) {
            if (!(oth instanceof ExpirationEntry)) {
                return false;
            }
            final ExpirationEntry ee = (ExpirationEntry)oth;
            return this.time == ee.time && this.points == ee.points && this.acc == ee.acc;
        }
    }
}
