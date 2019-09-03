package scripting;

import client.MapleClient;
import server.MaplePortal;

public class PortalPlayerInteraction extends AbstractPlayerInteraction
{
    private final MaplePortal portal;
    
    public PortalPlayerInteraction(final MapleClient c, final MaplePortal portal) {
        super(c);
        this.portal = portal;
    }
    
    public final MaplePortal getPortal() {
        return this.portal;
    }
    
    public final void inFreeMarket() {
        if (this.getPlayer().getLevel() >= 10) {
            this.saveLocation("FREE_MARKET");
            this.playPortalSE();
            this.warp(910000000, "st00");
        }
        else {
            this.playerMessage(5, "\u4f60\u9700\u898110\u7ea7\u624d\u53ef\u4ee5\u8fdb\u5165\u81ea\u7531\u5e02\u573a");
        }
    }
    
    @Override
    public void spawnMonster(final int id) {
        this.spawnMonster(id, 1, this.portal.getPosition());
    }
    
    @Override
    public void spawnMonster(final int id, final int qty) {
        this.spawnMonster(id, qty, this.portal.getPosition());
    }
}
