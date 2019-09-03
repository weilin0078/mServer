package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class ChangeEquipSpecialAwesome implements LifeMovementFragment
{
    private int type;
    private int wui;
    
    public ChangeEquipSpecialAwesome(final int type, final int wui) {
        this.type = type;
        this.wui = wui;
    }
    
    @Override
    public void serialize(final LittleEndianWriter lew) {
        lew.write(this.type);
        lew.write(this.wui);
    }
    
    @Override
    public Point getPosition() {
        return new Point(0, 0);
    }
}
