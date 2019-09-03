package server.quest;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import client.ISkill;
import client.MapleCharacter;
import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataTool;
import tools.Pair;

public class MapleQuestRequirement implements Serializable
{
    private static final long serialVersionUID = 9179541993413738569L;
    private MapleQuest quest;
    private MapleQuestRequirementType type;
    private int intStore;
    private String stringStore;
    private List<Pair<Integer, Integer>> dataStore;
    
    public MapleQuestRequirement(final MapleQuest quest, final MapleQuestRequirementType type, final MapleData data) {
        this.type = type;
        this.quest = quest;
        switch (type) {
            case job: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    this.dataStore.add(new Pair<Integer, Integer>(i, MapleDataTool.getInt(child.get(i), -1)));
                }
                break;
            }
            case skill: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    final MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("acquire"), 0)));
                }
                break;
            }
            case quest: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    final MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id")), MapleDataTool.getInt(childdata.getChildByPath("state"), 0)));
                }
                break;
            }
            case item: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    final MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id")), MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case pettamenessmin:
            case npc:
            case questComplete:
            case pop:
            case interval:
            case mbmin:
            case lvmax:
            case lvmin: {
                this.intStore = MapleDataTool.getInt(data, -1);
                break;
            }
            case end: {
                this.stringStore = MapleDataTool.getString(data, null);
                break;
            }
            case mob: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    final MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case fieldEnter: {
                final MapleData zeroField = data.getChildByPath("0");
                if (zeroField != null) {
                    this.intStore = MapleDataTool.getInt(zeroField);
                    break;
                }
                this.intStore = -1;
                break;
            }
            case mbcard: {
                final List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    final MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("min"), 0)));
                }
                break;
            }
            case pet: {
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (final MapleData child2 : data) {
                    this.dataStore.add(new Pair<Integer, Integer>(-1, MapleDataTool.getInt("id", child2, 0)));
                }
                break;
            }
        }
    }
    
    public boolean check(final MapleCharacter c, final Integer npcid) {
        int skill;
        IItem item;
        MaplePet pet;
        switch (this.type) {
            case job: {
                for (final Pair<Integer, Integer> a : this.dataStore) {
                    if (a.getRight() == c.getJob() || c.isGM()) {
                        return true;
                    }
                }
                return false;
            }
            case skill: {
                for (final Pair<Integer, Integer> a : this.dataStore) {
                    final boolean acquire = a.getRight() > 0;
                    skill = a.getLeft();
                    final ISkill skil = SkillFactory.getSkill(skill);
                    if (acquire) {
                        if (skil.isFourthJob()) {
                            if (c.getMasterLevel(skil) == 0) {
                                return false;
                            }
                            continue;
                        }
                        else {
                            if (c.getSkillLevel(skil) == 0) {
                                return false;
                            }
                            continue;
                        }
                    }
                    else {
                        if (c.getSkillLevel(skil) > 0 || c.getMasterLevel(skil) > 0) {
                            return false;
                        }
                        continue;
                    }
                }
                return true;
            }
            case quest: {
                for (final Pair<Integer, Integer> a : this.dataStore) {
                    final MapleQuestStatus q = c.getQuest(MapleQuest.getInstance(a.getLeft()));
                    final int state = a.getRight();
                    if (state != 0) {
                        if (q == null && state == 0) {
                            continue;
                        }
                        if (q == null || q.getStatus() != state) {
                            return false;
                        }
                        continue;
                    }
                }
                return true;
            }
            case item: {
                for (final Pair<Integer, Integer> a2 : this.dataStore) {
                    final int itemId = a2.getLeft();
                    short quantity = 0;
                    final MapleInventoryType iType = GameConstants.getInventoryType(itemId);
                    final Iterator i$3 = c.getInventory(iType).listById(itemId).iterator();
                    while (i$3.hasNext()) {
                        item = (IItem) i$3.next();
                        quantity += item.getQuantity();
                    }
                    final int count = a2.getRight();
                    if (quantity < count || (count <= 0 && quantity > 0)) {
                        return false;
                    }
                }
                return true;
            }
            case lvmin: {
                return c.getLevel() >= this.intStore;
            }
            case lvmax: {
                return c.getLevel() <= this.intStore;
            }
            case end: {
                final String timeStr = this.stringStore;
                final Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)), Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
                return cal.getTimeInMillis() >= System.currentTimeMillis();
            }
            case mob: {
                for (final Pair<Integer, Integer> a3 : this.dataStore) {
                    final int mobId = a3.getLeft();
                    final int killReq = a3.getRight();
                    if (c.getQuest(this.quest).getMobKills(mobId) < killReq) {
                        return false;
                    }
                }
                return true;
            }
            case npc: {
                return npcid == null || npcid == this.intStore;
            }
            case fieldEnter: {
                return this.intStore != -1 && this.intStore == c.getMapId();
            }
            case mbmin: {
                return c.getMonsterBook().getTotalCards() >= this.intStore;
            }
            case mbcard: {
                for (final Pair<Integer, Integer> a3 : this.dataStore) {
                    final int cardId = a3.getLeft();
                    final int killReq = a3.getRight();
                    if (c.getMonsterBook().getLevelByCard(cardId) < killReq) {
                        return false;
                    }
                }
                return true;
            }
            case pop: {
                return c.getFame() <= this.intStore;
            }
            case questComplete: {
                return c.getNumQuest() >= this.intStore;
            }
            case interval: {
                return c.getQuest(this.quest).getStatus() != 2 || c.getQuest(this.quest).getCompletionTime() <= System.currentTimeMillis() - this.intStore * 60 * 1000L;
            }
            case pet: {
                for (final Pair<Integer, Integer> a3 : this.dataStore) {
                    if (c.getPetIndexById(a3.getRight()) == -1) {
                        return false;
                    }
                }
                return true;
            }
            case pettamenessmin: {
                final Iterator i$3 = c.getPets().iterator();
                while (i$3.hasNext()) {
                    pet = (MaplePet) i$3.next();
                    if (pet.getSummoned() && pet.getCloseness() >= this.intStore) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public MapleQuestRequirementType getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
}
