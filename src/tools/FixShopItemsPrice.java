package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseConnection;
import server.MapleItemInformationProvider;

public class FixShopItemsPrice
{
    private final Connection con;
    
    public FixShopItemsPrice() {
        this.con = DatabaseConnection.getConnection();
    }
    
    private List<Integer> loadFromDB() {
        final List<Integer> shopItemsId = new ArrayList<Integer>();
        try {
            final PreparedStatement ps = this.con.prepareStatement("SELECT itemid FROM shopitems ORDER BY itemid");
            final ResultSet rs = ps.executeQuery();
            int itemId = 0;
            while (rs.next()) {
                if (itemId != rs.getInt("itemid")) {
                    itemId = rs.getInt("itemid");
                    shopItemsId.add(itemId);
                }
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("\u65e0\u6cd5\u8f7d\u5165\u5546\u5e97");
        }
        return shopItemsId;
    }
    
    private void changePrice(final int itemId) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            final PreparedStatement ps = this.con.prepareStatement("SELECT shopid, price FROM shopitems WHERE itemid = ? ORDER BY price");
            ps.setInt(1, itemId);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (ii.getPrice(itemId) > rs.getLong("price")) {
                    System.out.println("\u9053\u5177: " + MapleItemInformationProvider.getInstance().getName(itemId) + "\u9053\u5177ID: " + itemId + " \u5546\u5e97: " + rs.getInt("shopid") + " \u4ef7\u683c: " + rs.getLong("price") + " \u65b0\u4ef7\u683c:" + (long)ii.getPrice(itemId));
                    final PreparedStatement pp = this.con.prepareStatement("UPDATE shopitems SET price = ? WHERE itemid = ? AND shopid = ?");
                    pp.setLong(1, (long)ii.getPrice(itemId));
                    pp.setInt(2, itemId);
                    pp.setInt(3, rs.getInt("shopid"));
                    pp.execute();
                    pp.close();
                }
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.out.println("\u8655\u7406\u5546\u54c1\u5931\u6557, \u9053\u5177ID:" + itemId);
        }
    }
    
    public static void main(final String[] args) {
        System.setProperty("net.sf.odinms.wzpath", System.getProperty("net.sf.odinms.wzpath"));
        final FixShopItemsPrice i = new FixShopItemsPrice();
        System.out.println("\u6b63\u5728\u52a0\u8f7d\u9053\u5177\u6570\u636e......");
        MapleItemInformationProvider.getInstance().load();
        System.out.println("\u6b63\u5728\u8bfb\u53d6\u5546\u5e97\u5185\u5546\u54c1......");
        final List<Integer> list = i.loadFromDB();
        System.out.println("\u6b63\u5728\u5904\u7406\u5546\u5e97\u5185\u5546\u54c1\u4ef7\u683c......");
        for (final int ii : list) {
            i.changePrice(ii);
        }
        System.out.println("\u5904\u7406\u5546\u54c1\u4ef7\u683c\u7ed3\u675f\u3002");
    }
}
