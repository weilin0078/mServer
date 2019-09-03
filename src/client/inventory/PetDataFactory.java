package client.inventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;

public class PetDataFactory
{
    private static MapleDataProvider dataRoot;
    private static Map<Pair<Integer, Integer>, PetCommand> petCommands;
    private static Map<Integer, Integer> petHunger;
    
    public static final PetCommand getPetCommand(final int petId, final int skillId) {
        PetCommand ret = PetDataFactory.petCommands.get(new Pair(petId, skillId));
        if (ret != null) {
            return ret;
        }
        final MapleData skillData = PetDataFactory.dataRoot.getData("Pet/" + petId + ".img");
        int prob = 0;
        int inc = 0;
        if (skillData != null) {
            prob = MapleDataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
            inc = MapleDataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
        }
        ret = new PetCommand(petId, skillId, prob, inc);
        PetDataFactory.petCommands.put(new Pair<Integer, Integer>(petId, skillId), ret);
        return ret;
    }
    
    public static final int getHunger(final int petId) {
        Integer ret = PetDataFactory.petHunger.get(petId);
        if (ret != null) {
            return ret;
        }
        final MapleData hungerData = PetDataFactory.dataRoot.getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        ret = MapleDataTool.getInt(hungerData, 1);
        PetDataFactory.petHunger.put(petId, ret);
        return ret;
    }
    
    static {
        PetDataFactory.dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
        PetDataFactory.petCommands = new HashMap<Pair<Integer, Integer>, PetCommand>();
        PetDataFactory.petHunger = new HashMap<Integer, Integer>();
    }
}
