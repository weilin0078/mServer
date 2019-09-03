package handling.world;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MapleParty implements Serializable
{
    private static final long serialVersionUID = 9179541993413738569L;
    private MaplePartyCharacter leader;
    private List<MaplePartyCharacter> members;
    private int id;
    
    public MapleParty(final int id, final MaplePartyCharacter chrfor) {
        this.members = new LinkedList<MaplePartyCharacter>();
        this.leader = chrfor;
        this.members.add(this.leader);
        this.id = id;
    }
    
    public boolean containsMembers(final MaplePartyCharacter member) {
        return this.members.contains(member);
    }
    
    public void addMember(final MaplePartyCharacter member) {
        this.members.add(member);
    }
    
    public void removeMember(final MaplePartyCharacter member) {
        this.members.remove(member);
    }
    
    public void updateMember(final MaplePartyCharacter member) {
        for (int i = 0; i < this.members.size(); ++i) {
            final MaplePartyCharacter chr = this.members.get(i);
            if (chr.equals(member)) {
                this.members.set(i, member);
            }
        }
    }
    
    public MaplePartyCharacter getMemberById(final int id) {
        for (final MaplePartyCharacter chr : this.members) {
            if (chr.getId() == id) {
                return chr;
            }
        }
        return null;
    }
    
    public MaplePartyCharacter getMemberByIndex(final int index) {
        return this.members.get(index);
    }
    
    public Collection<MaplePartyCharacter> getMembers() {
        return new LinkedList<MaplePartyCharacter>(this.members);
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public MaplePartyCharacter getLeader() {
        return this.leader;
    }
    
    public void setLeader(final MaplePartyCharacter nLeader) {
        this.leader = nLeader;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.id;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MapleParty other = (MapleParty)obj;
        return this.id == other.id;
    }
}
