package gui.entity;

public class DropData
{
    private Integer id;
    private Integer dropperid;
    private Integer itemid;
    private Integer minimum_quantity;
    private Integer maximum_quantity;
    private Double chance;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDropperid() {
        return this.dropperid;
    }
    
    public void setDropperid(final Integer dropperid) {
        this.dropperid = dropperid;
    }
    
    public Integer getItemid() {
        return this.itemid;
    }
    
    public void setItemid(final Integer itemid) {
        this.itemid = itemid;
    }
    
    public Integer getMinimum_quantity() {
        return this.minimum_quantity;
    }
    
    public void setMinimum_quantity(final Integer minimum_quantity) {
        this.minimum_quantity = minimum_quantity;
    }
    
    public Integer getMaximum_quantity() {
        return this.maximum_quantity;
    }
    
    public void setMaximum_quantity(final Integer maximum_quantity) {
        this.maximum_quantity = maximum_quantity;
    }
    
    public Double getChance() {
        return this.chance;
    }
    
    public void setChance(final Double chance) {
        this.chance = chance;
    }
}
