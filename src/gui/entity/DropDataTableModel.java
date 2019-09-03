package gui.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import database.DatabaseConnection;

public class DropDataTableModel implements TableModel
{
    private List<DropData> dropData;
    
    public DropDataTableModel(final List<DropData> dropData) {
        this.dropData = dropData;
    }
    
    @Override
    public int getRowCount() {
        return this.dropData.size();
    }
    
    @Override
    public int getColumnCount() {
        return 5;
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return "\u602a\u7269ID";
            }
            case 1: {
                return "\u7269\u54c1ID";
            }
            case 2: {
                return "\u6700\u5c0f\u6389\u843d";
            }
            case 3: {
                return "\u6700\u5927\u6389\u843d";
            }
            case 4: {
                return "\u7206\u7387(%)";
            }
            default: {
                return "\u51fa\u9519";
            }
        }
    }
    
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return Object.class;
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex != 0;
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final DropData drop = this.dropData.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return drop.getDropperid();
            }
            case 1: {
                return drop.getItemid();
            }
            case 2: {
                return drop.getMinimum_quantity();
            }
            case 3: {
                return drop.getMaximum_quantity();
            }
            case 4: {
                return drop.getChance();
            }
            default: {
                return "\u51fa\u9519";
            }
        }
    }
    
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        final DropData drop = this.dropData.get(rowIndex);
        String sql = "";
        switch (columnIndex) {
            case 0: {
                drop.setDropperid(Integer.parseInt(aValue.toString()));
                sql = "UPDATE drop_data SET dropperid = " + String.valueOf(aValue) + " where id = " + drop.getId();
                break;
            }
            case 1: {
                drop.setItemid(Integer.parseInt(aValue.toString()));
                sql = "UPDATE drop_data SET itemid = " + String.valueOf(aValue) + " where id = " + drop.getId();
                break;
            }
            case 2: {
                drop.setMinimum_quantity(Integer.parseInt(aValue.toString()));
                sql = "UPDATE drop_data SET minimum_quantity = " + String.valueOf(aValue) + " where id = " + drop.getId();
                break;
            }
            case 3: {
                drop.setMaximum_quantity(Integer.parseInt(aValue.toString()));
                sql = "UPDATE drop_data SET maximum_quantity = " + String.valueOf(aValue) + " where id = " + drop.getId();
                break;
            }
            case 4: {
                drop.setChance(Double.parseDouble(aValue.toString()));
                sql = "UPDATE drop_data SET chance = " + Double.parseDouble(String.valueOf(aValue)) * 10000.0 + " where id = " + drop.getId();
                break;
            }
        }
        this.updateData(sql);
    }
    
    @Override
    public void addTableModelListener(final TableModelListener l) {
    }
    
    @Override
    public void removeTableModelListener(final TableModelListener l) {
    }
    
    private void updateData(final String sql) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "\u9519\u8bef!\r\n" + ex);
        }
    }
    
    public void removeRow(final int index) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("DELETE FROM drop_data where id = ?");
            ps.setInt(1, this.dropData.get(index).getId());
            ps.executeUpdate();
            ps.close();
            this.dropData.remove(index);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "\u9519\u8bef!\r\n" + e);
        }
    }
}
