package client.inventory;

import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DatabaseConnection;
import server.MapleItemInformationProvider;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;

public class MaplePet implements Serializable
{
    private static final long serialVersionUID = 9179541993413738569L;
    private String name;
    private int Fh;
    private int stance;
    private int uniqueid;
    private int petitemid;
    private int secondsLeft;
    private Point pos;
    private byte fullness;
    private byte level;
    private byte summoned;
    private short inventorypos;
    private short closeness;
    private short flags;
    private boolean changed;
    
    private MaplePet(final int petitemid, final int uniqueid) {
        this.Fh = 0;
        this.stance = 0;
        this.secondsLeft = 0;
        this.fullness = 100;
        this.level = 1;
        this.summoned = 0;
        this.inventorypos = 0;
        this.closeness = 0;
        this.flags = 0;
        this.changed = false;
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
    }
    
    private MaplePet(final int petitemid, final int uniqueid, final short inventorypos) {
        this.Fh = 0;
        this.stance = 0;
        this.secondsLeft = 0;
        this.fullness = 100;
        this.level = 1;
        this.summoned = 0;
        this.inventorypos = 0;
        this.closeness = 0;
        this.flags = 0;
        this.changed = false;
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
        this.inventorypos = inventorypos;
    }
    
    public static final MaplePet loadFromDb(final int itemid, final int petid, final short inventorypos) {
        try {
            final MaplePet ret = new MaplePet(itemid, petid, inventorypos);
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?");
            ps.setInt(1, petid);
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            ret.setName(rs.getString("name"));
            ret.setCloseness(rs.getShort("closeness"));
            ret.setLevel(rs.getByte("level"));
            ret.setFullness(rs.getByte("fullness"));
            ret.setSecondsLeft(rs.getInt("seconds"));
            ret.setFlags(rs.getShort("flags"));
            ret.changed = false;
            rs.close();
            ps.close();
            return ret;
        }
        catch (SQLException ex) {
            Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public final void saveToDb() {
        if (!this.changed) {
            return;
        }
        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ? WHERE petid = ?");
            ps.setString(1, this.name);
            ps.setByte(2, this.level);
            ps.setShort(3, this.closeness);
            ps.setByte(4, this.fullness);
            ps.setInt(5, this.secondsLeft);
            ps.setShort(6, this.flags);
            ps.setInt(7, this.uniqueid);
            ps.executeUpdate();
            ps.close();
            this.changed = false;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static final MaplePet createPet(final int itemid, final int uniqueid) {
        return createPet(itemid, MapleItemInformationProvider.getInstance().getName(itemid), 1, 0, 100, uniqueid, (itemid == 5000054) ? 18000 : 0);
    }
    
    public static final MaplePet createPet(final int itemid, final String name, final int level, final int closeness, final int fullness, int uniqueid, final int secondsLeft) {
        if (uniqueid <= -1) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        final short ret1 = MapleItemInformationProvider.getInstance().getPetFlagInfo(itemid);
        try {
            final PreparedStatement pse = DatabaseConnection.getConnection().prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags) VALUES (?, ?, ?, ?, ?, ?, ?)");
            pse.setInt(1, uniqueid);
            pse.setString(2, name);
            pse.setByte(3, (byte)level);
            pse.setShort(4, (short)closeness);
            pse.setByte(5, (byte)fullness);
            pse.setInt(6, secondsLeft);
            pse.setShort(7, ret1);
            pse.executeUpdate();
            pse.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        final MaplePet pet = new MaplePet(itemid, uniqueid);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setFlags(ret1);
        pet.setSecondsLeft(secondsLeft);
        return pet;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final void setName(final String name) {
        this.name = name;
        this.changed = true;
    }
    
    public final boolean getSummoned() {
        return this.summoned > 0;
    }
    
    public final byte getSummonedValue() {
        return this.summoned;
    }
    
    public final void setSummoned(final int summoned) {
        this.summoned = (byte)summoned;
    }
    
    public final short getInventoryPosition() {
        return this.inventorypos;
    }
    
    public final void setInventoryPosition(final short inventorypos) {
        this.inventorypos = inventorypos;
    }
    
    public int getUniqueId() {
        return this.uniqueid;
    }
    
    public final short getCloseness() {
        return this.closeness;
    }
    
    public final void setCloseness(int closeness) {
        if (closeness >= Integer.MAX_VALUE || closeness <= 0) {
            closeness = 1;
        }
        this.closeness = (short)closeness;
        this.changed = true;
    }
    
    public final byte getLevel() {
        return this.level;
    }
    
    public final void setLevel(final int level) {
        this.level = (byte)level;
        this.changed = true;
    }
    
    public final byte getFullness() {
        return this.fullness;
    }
    
    public final void setFullness(final int fullness) {
        this.fullness = (byte)fullness;
        this.changed = true;
    }
    
    public final short getFlags() {
        return this.flags;
    }
    
    public final void setFlags(final int fffh) {
        this.flags = (short)fffh;
        this.changed = true;
    }
    
    public final int getFh() {
        return this.Fh;
    }
    
    public final void setFh(final int Fh) {
        this.Fh = Fh;
    }
    
    public final Point getPos() {
        return this.pos;
    }
    
    public final void setPos(final Point pos) {
        this.pos = pos;
    }
    
    public final int getStance() {
        return this.stance;
    }
    
    public final void setStance(final int stance) {
        this.stance = stance;
    }
    
    public final int getPetItemId() {
        return this.petitemid;
    }
    
    public final boolean canConsume(final int itemId) {
        final MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        for (final int petId : mii.petsCanConsume(itemId)) {
            if (petId == this.petitemid) {
                return true;
            }
        }
        return false;
    }
    
    public final void updatePosition(final List<LifeMovementFragment> movement) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    this.setPos(((LifeMovement)move).getPosition());
                }
                this.setStance(((LifeMovement)move).getNewstate());
            }
        }
    }
    
    public final int getSecondsLeft() {
        return this.secondsLeft;
    }
    
    public final void setSecondsLeft(final int sl) {
        this.secondsLeft = sl;
        this.changed = true;
    }
    
    public enum PetFlag
    {
        ITEM_PICKUP(1, 5190000, 5191000), 
        EXPAND_PICKUP(2, 5190002, 5191002), 
        AUTO_PICKUP(4, 5190003, 5191003), 
        UNPICKABLE(8, 5190005, -1), 
        LEFTOVER_PICKUP(16, 5190004, 5191004), 
        HP_CHARGE(32, 5190001, 5191001), 
        MP_CHARGE(64, 5190006, -1), 
        PET_BUFF(128, -1, -1), 
        PET_DRAW(256, 5190007, -1), 
        PET_DIALOGUE(512, 5190008, -1);
        
        private final int i;
        private final int item;
        private final int remove;
        
        private PetFlag(final int i, final int item, final int remove) {
            this.i = i;
            this.item = item;
            this.remove = remove;
        }
        
        public final int getValue() {
            return this.i;
        }
        
        public final boolean check(final int flag) {
            return (flag & this.i) == this.i;
        }
        
        public static final PetFlag getByAddId(final int itemId) {
            for (final PetFlag flag : values()) {
                if (flag.item == itemId) {
                    return flag;
                }
            }
            return null;
        }
        
        public static final PetFlag getByDelId(final int itemId) {
            for (final PetFlag flag : values()) {
                if (flag.remove == itemId) {
                    return flag;
                }
            }
            return null;
        }
    }
}
