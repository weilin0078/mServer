package client.anticheat;

public enum CheatingOffense
{
    \u53ec\u5524\u517d\u5feb\u901f\u653b\u51fb((byte)5, 6000L, 10, (byte)1), 
    \u5feb\u901f\u653b\u51fb((byte)5, 6000L, 50, (byte)2), 
    \u5feb\u901f\u653b\u51fb2((byte)5, 9000L, 20, (byte)2), 
    \u602a\u7269\u79fb\u52a8((byte)1, 30000L, 20, (byte)2), 
    \u4f24\u5bb3\u76f8\u540c((byte)2, 30000L, 150, (byte)2), 
    \u4eba\u7269\u65e0\u654c((byte)1, 30000L, 1200, (byte)0), 
    \u9b54\u6cd5\u4f24\u5bb3\u8fc7\u9ad8((byte)5, 30000L, -1, (byte)0), 
    \u9b54\u6cd5\u4f24\u5bb3\u8fc7\u9ad82((byte)10, 30000L, -1, (byte)0), 
    \u653b\u51fb\u529b\u8fc7\u9ad8((byte)5, 30000L, -1, (byte)0), 
    \u602a\u7269\u78b0\u649e\u8fc7\u5feb((byte)1, 60000L, 100, (byte)2), 
    \u653b\u51fb\u8fc7\u9ad82((byte)10, 30000L, -1, (byte)0), 
    \u653b\u51fb\u8303\u56f4\u8fc7\u5927((byte)5, 60000L, 1500), 
    \u53ec\u5524\u517d\u653b\u51fb\u8303\u56f4\u8fc7\u5927((byte)5, 60000L, 200), 
    \u56de\u590d\u8fc7\u591aHP((byte)1, 30000L, 1000, (byte)0), 
    \u56de\u590d\u8fc7\u591aMP((byte)1, 30000L, 1000, (byte)0), 
    \u5168\u56fe\u5438\u7269_\u5ba2\u6237\u7aef((byte)5, 5000L, 10), 
    \u5168\u56fe\u5438\u7269_\u670d\u52a1\u7aef((byte)3, 5000L, 100), 
    \u5ba0\u7269\u5168\u56fe\u5438\u7269_\u5ba2\u6237\u7aef((byte)5, 10000L, 20), 
    \u5ba0\u7269\u5168\u56fe\u5438\u7269_\u670d\u52a1\u7aef((byte)3, 10000L, 100, (byte)0), 
    \u4f7f\u7528\u8fc7\u8fdc\u4f20\u9001\u70b9((byte)1, 60000L, 100, (byte)0), 
    \u56de\u907f\u7387\u8fc7\u9ad8((byte)20, 180000L, 100, (byte)2), 
    \u5176\u4ed6\u5f02\u5e38((byte)1, 300000L), 
    \u4eba\u7269\u6b7b\u4ea1\u653b\u51fb((byte)1, 300000L, -1, (byte)0), 
    \u4f7f\u7528\u4e0d\u5b58\u5728\u9053\u5177((byte)1, 300000L), 
    \u6dfb\u52a0\u81ea\u5df1\u58f0\u671b((byte)1, 1000L, 1), 
    \u58f0\u671b\u5341\u4e94\u7ea7\u4ee5\u4e0b\u6dfb\u52a0((byte)1, 1000L, 1), 
    \u91d1\u94b1\u70b8\u5f39_\u4e0d\u5b58\u5728\u9053\u5177((byte)1, 300000L), 
    \u53ec\u5524\u517d\u653b\u51fb\u602a\u7269\u6570\u91cf\u5f02\u5e38((byte)1, 10000L, 3), 
    \u6cbb\u6108\u672f\u653b\u51fb\u975e\u4e0d\u6b7b\u7cfb\u602a\u7269((byte)20, 10000L, 3), 
    HEAL_ATTACKING_UNDEAD((byte)20, 30000L, 100), 
    \u5438\u602a((byte)1, 7000L, 5);
    
    private final byte points;
    private final long validityDuration;
    private final int autobancount;
    private byte bantype;
    
    public final byte getPoints() {
        return this.points;
    }
    
    public final long getValidityDuration() {
        return this.validityDuration;
    }
    
    public final boolean shouldAutoban(final int count) {
        return this.autobancount != -1 && count >= this.autobancount;
    }
    
    public final byte getBanType() {
        return this.bantype;
    }
    
    public final void setEnabled(final boolean enabled) {
        this.bantype = (byte)(enabled ? 1 : 0);
    }
    
    public final boolean isEnabled() {
        return this.bantype >= 1;
    }
    
    private CheatingOffense(final byte points, final long validityDuration) {
        this(points, validityDuration, -1, (byte)1);
    }
    
    private CheatingOffense(final byte points, final long validityDuration, final int autobancount) {
        this(points, validityDuration, autobancount, (byte)1);
    }
    
    private CheatingOffense(final byte points, final long validityDuration, final int autobancount, final byte bantype) {
        this.bantype = 0;
        this.points = points;
        this.validityDuration = validityDuration;
        this.autobancount = autobancount;
        this.bantype = bantype;
    }
}
