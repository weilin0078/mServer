package server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;

public class MapleItemInformationProvider
{
    private static final MapleItemInformationProvider instance;
    protected Map<Integer, Boolean> onEquipUntradableCache;
    protected final MapleDataProvider etcData;
    protected final MapleDataProvider itemData;
    protected final MapleDataProvider equipData;
    protected final MapleDataProvider stringData;
    protected final MapleData cashStringData;
    protected final MapleData consumeStringData;
    protected final MapleData eqpStringData;
    protected final MapleData etcStringData;
    protected final MapleData insStringData;
    protected final MapleData petStringData;
    protected final Map<Integer, List<Integer>> scrollReqCache;
    protected final Map<Integer, Short> slotMaxCache;
    protected final Map<Integer, Integer> getExpCache;
    protected final Map<Integer, List<StructPotentialItem>> potentialCache;
    protected final Map<Integer, MapleStatEffect> itemEffects;
    protected final Map<Integer, Map<String, Integer>> equipStatsCache;
    protected final Map<Integer, Map<String, Byte>> itemMakeStatsCache;
    protected final Map<Integer, Short> itemMakeLevel;
    protected final Map<Integer, Equip> equipCache;
    protected final Map<Integer, Double> priceCache;
    protected final Map<Integer, Integer> wholePriceCache;
    protected final Map<Integer, Integer> projectileWatkCache;
    protected final Map<Integer, Integer> monsterBookID;
    protected final Map<Integer, String> nameCache;
    protected final Map<Integer, String> descCache;
    protected final Map<Integer, String> msgCache;
    protected final Map<Integer, Map<String, Integer>> SkillStatsCache;
    protected final Map<Integer, Byte> consumeOnPickupCache;
    protected final Map<Integer, Boolean> dropRestrictionCache;
    protected final Map<Integer, Boolean> accCache;
    protected final Map<Integer, Boolean> pickupRestrictionCache;
    protected final Map<Integer, Integer> stateChangeCache;
    protected final Map<Integer, Integer> mesoCache;
    protected final Map<Integer, Boolean> notSaleCache;
    protected final Map<Integer, Integer> karmaEnabledCache;
    protected Map<Integer, Boolean> karmaCache;
    protected final Map<Integer, Boolean> isQuestItemCache;
    protected final Map<Integer, Boolean> blockPickupCache;
    protected final Map<Integer, List<Integer>> petsCanConsumeCache;
    protected final Map<Integer, Boolean> logoutExpireCache;
    protected final Map<Integer, List<Pair<Integer, Integer>>> summonMobCache;
    protected final List<Pair<Integer, String>> itemNameCache;
    protected final Map<Integer, Map<Integer, Map<String, Integer>>> equipIncsCache;
    protected final Map<Integer, Map<Integer, List<Integer>>> equipSkillsCache;
    protected final Map<Integer, Pair<Integer, List<StructRewardItem>>> RewardItem;
    protected final Map<Byte, StructSetItem> setItems;
    protected final Map<Integer, Pair<Integer, List<Integer>>> questItems;
    protected Map<Integer, MapleInventoryType> inventoryTypeCache;
    
    protected MapleItemInformationProvider() {
        this.onEquipUntradableCache = new HashMap<Integer, Boolean>();
        this.etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
        this.itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
        this.equipData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz"));
        this.stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
        this.cashStringData = this.stringData.getData("Cash.img");
        this.consumeStringData = this.stringData.getData("Consume.img");
        this.eqpStringData = this.stringData.getData("Eqp.img");
        this.etcStringData = this.stringData.getData("Etc.img");
        this.insStringData = this.stringData.getData("Ins.img");
        this.petStringData = this.stringData.getData("Pet.img");
        this.scrollReqCache = new HashMap<Integer, List<Integer>>();
        this.slotMaxCache = new HashMap<Integer, Short>();
        this.getExpCache = new HashMap<Integer, Integer>();
        this.potentialCache = new HashMap<Integer, List<StructPotentialItem>>();
        this.itemEffects = new HashMap<Integer, MapleStatEffect>();
        this.equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
        this.itemMakeStatsCache = new HashMap<Integer, Map<String, Byte>>();
        this.itemMakeLevel = new HashMap<Integer, Short>();
        this.equipCache = new HashMap<Integer, Equip>();
        this.priceCache = new HashMap<Integer, Double>();
        this.wholePriceCache = new HashMap<Integer, Integer>();
        this.projectileWatkCache = new HashMap<Integer, Integer>();
        this.monsterBookID = new HashMap<Integer, Integer>();
        this.nameCache = new HashMap<Integer, String>();
        this.descCache = new HashMap<Integer, String>();
        this.msgCache = new HashMap<Integer, String>();
        this.SkillStatsCache = new HashMap<Integer, Map<String, Integer>>();
        this.consumeOnPickupCache = new HashMap<Integer, Byte>();
        this.dropRestrictionCache = new HashMap<Integer, Boolean>();
        this.accCache = new HashMap<Integer, Boolean>();
        this.pickupRestrictionCache = new HashMap<Integer, Boolean>();
        this.stateChangeCache = new HashMap<Integer, Integer>();
        this.mesoCache = new HashMap<Integer, Integer>();
        this.notSaleCache = new HashMap<Integer, Boolean>();
        this.karmaEnabledCache = new HashMap<Integer, Integer>();
        this.karmaCache = new HashMap<Integer, Boolean>();
        this.isQuestItemCache = new HashMap<Integer, Boolean>();
        this.blockPickupCache = new HashMap<Integer, Boolean>();
        this.petsCanConsumeCache = new HashMap<Integer, List<Integer>>();
        this.logoutExpireCache = new HashMap<Integer, Boolean>();
        this.summonMobCache = new HashMap<Integer, List<Pair<Integer, Integer>>>();
        this.itemNameCache = new ArrayList<Pair<Integer, String>>();
        this.equipIncsCache = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
        this.equipSkillsCache = new HashMap<Integer, Map<Integer, List<Integer>>>();
        this.RewardItem = new HashMap<Integer, Pair<Integer, List<StructRewardItem>>>();
        this.setItems = new HashMap<Byte, StructSetItem>();
        this.questItems = new HashMap<Integer, Pair<Integer, List<Integer>>>();
        this.inventoryTypeCache = new HashMap<Integer, MapleInventoryType>();
        System.out.println("\u52a0\u8f7d \u7269\u54c1\u4fe1\u606f :::");
    }
    
    public final void load() {
        if (this.setItems.size() != 0 || this.potentialCache.size() != 0) {
            return;
        }
        this.getAllItems();
    }
    
    public final List<StructPotentialItem> getPotentialInfo(final int potId) {
        return this.potentialCache.get(potId);
    }
    
    public final Map<Integer, List<StructPotentialItem>> getAllPotentialInfo() {
        return this.potentialCache;
    }
    
    public static MapleItemInformationProvider getInstance() {
        return MapleItemInformationProvider.instance;
    }
    
    public final List<Pair<Integer, String>> getAllItems() {
        if (this.itemNameCache.size() != 0) {
            return this.itemNameCache;
        }
        final List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData = this.stringData.getData("Cash.img");
        for (final MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Consume.img");
        for (final MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (final MapleData eqpType : itemsData.getChildren()) {
            for (final MapleData itemFolder2 : eqpType.getChildren()) {
                itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder2.getName()), MapleDataTool.getString("name", itemFolder2, "NO-NAME")));
            }
        }
        itemsData = this.stringData.getData("Etc.img").getChildByPath("Etc");
        for (final MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Ins.img");
        for (final MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Pet.img");
        for (final MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        return itemPairs;
    }
    
    protected final MapleData getStringData(final int itemId) {
        String cat = null;
        MapleData data;
        if (itemId >= 5010000) {
            data = this.cashStringData;
        }
        else if (itemId >= 2000000 && itemId < 3000000) {
            data = this.consumeStringData;
        }
        else if ((itemId >= 1142000 && itemId < 1143000) || (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000)) {
            data = this.eqpStringData;
            cat = "Accessory";
        }
        else if (itemId >= 1000000 && itemId < 1010000) {
            data = this.eqpStringData;
            cat = "Cap";
        }
        else if (itemId >= 1102000 && itemId < 1103000) {
            data = this.eqpStringData;
            cat = "Cape";
        }
        else if (itemId >= 1040000 && itemId < 1050000) {
            data = this.eqpStringData;
            cat = "Coat";
        }
        else if (itemId >= 20000 && itemId < 25000) {
            data = this.eqpStringData;
            cat = "Face";
        }
        else if (itemId >= 1080000 && itemId < 1090000) {
            data = this.eqpStringData;
            cat = "Glove";
        }
        else if (itemId >= 30000 && itemId < 40000) {
            data = this.eqpStringData;
            cat = "Hair";
        }
        else if (itemId >= 1050000 && itemId < 1060000) {
            data = this.eqpStringData;
            cat = "Longcoat";
        }
        else if (itemId >= 1060000 && itemId < 1070000) {
            data = this.eqpStringData;
            cat = "Pants";
        }
        else if (itemId >= 1610000 && itemId < 1660000) {
            data = this.eqpStringData;
            cat = "Mechanic";
        }
        else if (itemId >= 1802000 && itemId < 1810000) {
            data = this.eqpStringData;
            cat = "PetEquip";
        }
        else if (itemId >= 1920000 && itemId < 2000000) {
            data = this.eqpStringData;
            cat = "Dragon";
        }
        else if (itemId >= 1112000 && itemId < 1120000) {
            data = this.eqpStringData;
            cat = "Ring";
        }
        else if (itemId >= 1092000 && itemId < 1100000) {
            data = this.eqpStringData;
            cat = "Shield";
        }
        else if (itemId >= 1070000 && itemId < 1080000) {
            data = this.eqpStringData;
            cat = "Shoes";
        }
        else if (itemId >= 1900000 && itemId < 1920000) {
            data = this.eqpStringData;
            cat = "Taming";
        }
        else if (itemId >= 1300000 && itemId < 1800000) {
            data = this.eqpStringData;
            cat = "Weapon";
        }
        else if (itemId >= 4000000 && itemId < 5000000) {
            data = this.etcStringData;
        }
        else if (itemId >= 3000000 && itemId < 4000000) {
            data = this.insStringData;
        }
        else {
            if (itemId < 5000000 || itemId >= 5010000) {
                return null;
            }
            data = this.petStringData;
        }
        if (cat == null) {
            return data.getChildByPath(String.valueOf(itemId));
        }
        return data.getChildByPath("Eqp/" + cat + "/" + itemId);
    }
    
    protected final MapleData getItemData(final int itemId) {
        MapleData ret = null;
        final String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = this.itemData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                }
                else {
                    if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                        return this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    }
                    continue;
                }
            }
        }
        root = this.equipData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    return this.equipData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        return ret;
    }
    
    public final short getSlotMax(final MapleClient c, final int itemId) {
        if (this.slotMaxCache.containsKey(itemId)) {
            return this.slotMaxCache.get(itemId);
        }
        short ret = 0;
        final MapleData item = this.getItemData(itemId);
        if (item != null) {
            final MapleData smEntry = item.getChildByPath("info/slotMax");
            if (smEntry == null) {
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    ret = 1;
                }
                else {
                    ret = 100;
                }
            }
            else {
                ret = (short)MapleDataTool.getInt(smEntry);
            }
        }
        this.slotMaxCache.put(itemId, ret);
        return ret;
    }
    
    public final int getWholePrice(final int itemId) {
        if (this.wholePriceCache.containsKey(itemId)) {
            return this.wholePriceCache.get(itemId);
        }
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return -1;
        }
        int pEntry = 0;
        final MapleData pData = item.getChildByPath("info/price");
        if (pData == null) {
            return -1;
        }
        pEntry = MapleDataTool.getInt(pData);
        this.wholePriceCache.put(itemId, pEntry);
        return pEntry;
    }
    
    public final double getPrice(final int itemId) {
        if (this.priceCache.containsKey(itemId)) {
            return this.priceCache.get(itemId);
        }
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return -1.0;
        }
        double pEntry = 0.0;
        MapleData pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            try {
                pEntry = MapleDataTool.getDouble(pData);
            }
            catch (Exception e) {
                pEntry = MapleDataTool.getIntConvert(pData);
            }
        }
        else {
            pData = item.getChildByPath("info/price");
            if (pData == null) {
                return -1.0;
            }
            pEntry = MapleDataTool.getIntConvert(pData);
        }
        if (itemId == 2070019 || itemId == 2330007) {
            pEntry = 1.0;
        }
        this.priceCache.put(itemId, pEntry);
        return pEntry;
    }
    
    public final Map<String, Byte> getItemMakeStats(final int itemId) {
        if (this.itemMakeStatsCache.containsKey(itemId)) {
            return this.itemMakeStatsCache.get(itemId);
        }
        if (itemId / 10000 != 425) {
            return null;
        }
        final Map<String, Byte> ret = new LinkedHashMap<String, Byte>();
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ret.put("incPAD", (byte)MapleDataTool.getInt("incPAD", info, 0));
        ret.put("incMAD", (byte)MapleDataTool.getInt("incMAD", info, 0));
        ret.put("incACC", (byte)MapleDataTool.getInt("incACC", info, 0));
        ret.put("incEVA", (byte)MapleDataTool.getInt("incEVA", info, 0));
        ret.put("incSpeed", (byte)MapleDataTool.getInt("incSpeed", info, 0));
        ret.put("incJump", (byte)MapleDataTool.getInt("incJump", info, 0));
        ret.put("incMaxHP", (byte)MapleDataTool.getInt("incMaxHP", info, 0));
        ret.put("incMaxMP", (byte)MapleDataTool.getInt("incMaxMP", info, 0));
        ret.put("incSTR", (byte)MapleDataTool.getInt("incSTR", info, 0));
        ret.put("incINT", (byte)MapleDataTool.getInt("incINT", info, 0));
        ret.put("incLUK", (byte)MapleDataTool.getInt("incLUK", info, 0));
        ret.put("incDEX", (byte)MapleDataTool.getInt("incDEX", info, 0));
        ret.put("randOption", (byte)MapleDataTool.getInt("randOption", info, 0));
        ret.put("randStat", (byte)MapleDataTool.getInt("randStat", info, 0));
        this.itemMakeStatsCache.put(itemId, ret);
        return ret;
    }
    
    private int rand(final int min, final int max) {
        return Math.abs(Randomizer.rand(min, max));
    }
    
    public Equip levelUpEquip(final Equip equip, final Map<String, Integer> sta) {
        final Equip nEquip = (Equip)equip.copy();
        try {
            for (final Map.Entry<String, Integer> stat : sta.entrySet()) {
                if (stat.getKey().equals("STRMin")) {
                    nEquip.setStr((short)(nEquip.getStr() + this.rand(stat.getValue(), sta.get("STRMax"))));
                }
                else if (stat.getKey().equals("DEXMin")) {
                    nEquip.setDex((short)(nEquip.getDex() + this.rand(stat.getValue(), sta.get("DEXMax"))));
                }
                else if (stat.getKey().equals("INTMin")) {
                    nEquip.setInt((short)(nEquip.getInt() + this.rand(stat.getValue(), sta.get("INTMax"))));
                }
                else if (stat.getKey().equals("LUKMin")) {
                    nEquip.setLuk((short)(nEquip.getLuk() + this.rand(stat.getValue(), sta.get("LUKMax"))));
                }
                else if (stat.getKey().equals("PADMin")) {
                    nEquip.setWatk((short)(nEquip.getWatk() + this.rand(stat.getValue(), sta.get("PADMax"))));
                }
                else if (stat.getKey().equals("PDDMin")) {
                    nEquip.setWdef((short)(nEquip.getWdef() + this.rand(stat.getValue(), sta.get("PDDMax"))));
                }
                else if (stat.getKey().equals("MADMin")) {
                    nEquip.setMatk((short)(nEquip.getMatk() + this.rand(stat.getValue(), sta.get("MADMax"))));
                }
                else if (stat.getKey().equals("MDDMin")) {
                    nEquip.setMdef((short)(nEquip.getMdef() + this.rand(stat.getValue(), sta.get("MDDMax"))));
                }
                else if (stat.getKey().equals("ACCMin")) {
                    nEquip.setAcc((short)(nEquip.getAcc() + this.rand(stat.getValue(), sta.get("ACCMax"))));
                }
                else if (stat.getKey().equals("EVAMin")) {
                    nEquip.setAvoid((short)(nEquip.getAvoid() + this.rand(stat.getValue(), sta.get("EVAMax"))));
                }
                else if (stat.getKey().equals("SpeedMin")) {
                    nEquip.setSpeed((short)(nEquip.getSpeed() + this.rand(stat.getValue(), sta.get("SpeedMax"))));
                }
                else if (stat.getKey().equals("JumpMin")) {
                    nEquip.setJump((short)(nEquip.getJump() + this.rand(stat.getValue(), sta.get("JumpMax"))));
                }
                else if (stat.getKey().equals("MHPMin")) {
                    nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MHPMax"))));
                }
                else if (stat.getKey().equals("MMPMin")) {
                    nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MMPMax"))));
                }
                else if (stat.getKey().equals("MaxHPMin")) {
                    nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MaxHPMax"))));
                }
                else {
                    if (!stat.getKey().equals("MaxMPMin")) {
                        continue;
                    }
                    nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MaxMPMax"))));
                }
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return nEquip;
    }
    
    public final Map<Integer, Map<String, Integer>> getEquipIncrements(final int itemId) {
        if (this.equipIncsCache.containsKey(itemId)) {
            return this.equipIncsCache.get(itemId);
        }
        final Map<Integer, Map<String, Integer>> ret = new LinkedHashMap<Integer, Map<String, Integer>>();
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info/level/info");
        if (info == null) {
            return null;
        }
        for (final MapleData dat : info.getChildren()) {
            final Map<String, Integer> incs = new HashMap<String, Integer>();
            for (final MapleData data : dat.getChildren()) {
                if (data.getName().length() > 3) {
                    incs.put(data.getName().substring(3), MapleDataTool.getIntConvert(data.getName(), dat, 0));
                }
            }
            ret.put(Integer.parseInt(dat.getName()), incs);
        }
        this.equipIncsCache.put(itemId, ret);
        return ret;
    }
    
    public final Map<Integer, List<Integer>> getEquipSkills(final int itemId) {
        if (this.equipSkillsCache.containsKey(itemId)) {
            return this.equipSkillsCache.get(itemId);
        }
        final Map<Integer, List<Integer>> ret = new LinkedHashMap<Integer, List<Integer>>();
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info/level/case");
        if (info == null) {
            return null;
        }
        for (final MapleData dat : info.getChildren()) {
            for (final MapleData data : dat.getChildren()) {
                if (data.getName().length() == 1) {
                    final List<Integer> adds = new ArrayList<Integer>();
                    for (final MapleData skil : data.getChildByPath("Skill").getChildren()) {
                        adds.add(MapleDataTool.getIntConvert("id", skil, 0));
                    }
                    ret.put(Integer.parseInt(data.getName()), adds);
                }
            }
        }
        this.equipSkillsCache.put(itemId, ret);
        return ret;
    }
    
    public final Map<String, Integer> getEquipStats(final int itemId) {
        if (this.equipStatsCache.containsKey(itemId)) {
            return this.equipStatsCache.get(itemId);
        }
        final Map<String, Integer> ret = new LinkedHashMap<String, Integer>();
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        for (final MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
            }
        }
        ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
        ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
        ret.put("reqJob", MapleDataTool.getInt("reqJob", info, 0));
        ret.put("reqSTR", MapleDataTool.getInt("reqSTR", info, 0));
        ret.put("reqDEX", MapleDataTool.getInt("reqDEX", info, 0));
        ret.put("reqINT", MapleDataTool.getInt("reqINT", info, 0));
        ret.put("reqLUK", MapleDataTool.getInt("reqLUK", info, 0));
        ret.put("reqPOP", MapleDataTool.getInt("reqPOP", info, 0));
        ret.put("cash", MapleDataTool.getInt("cash", info, 0));
        ret.put("canLevel", (info.getChildByPath("level") != null) ? 1 : 0);
        ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        ret.put("setItemID", MapleDataTool.getInt("setItemID", info, 0));
        ret.put("equipTradeBlock", MapleDataTool.getInt("equipTradeBlock", info, 0));
        ret.put("durability", MapleDataTool.getInt("durability", info, -1));
        if (GameConstants.isMagicWeapon(itemId)) {
            ret.put("elemDefault", MapleDataTool.getInt("elemDefault", info, 100));
            ret.put("incRMAS", MapleDataTool.getInt("incRMAS", info, 100));
            ret.put("incRMAF", MapleDataTool.getInt("incRMAF", info, 100));
            ret.put("incRMAL", MapleDataTool.getInt("incRMAL", info, 100));
            ret.put("incRMAI", MapleDataTool.getInt("incRMAI", info, 100));
        }
        this.equipStatsCache.put(itemId, ret);
        return ret;
    }
    
    public final boolean canEquip(final Map<String, Integer> stats, final int itemid, final int level, final int job, final int fame, final int str, final int dex, final int luk, final int int_, final int supremacy) {
        if (level + supremacy >= stats.get("reqLevel") && str >= stats.get("reqSTR") && dex >= stats.get("reqDEX") && luk >= stats.get("reqLUK") && int_ >= stats.get("reqINT")) {
            final int fameReq = stats.get("reqPOP");
            return fameReq == 0 || fame >= fameReq;
        }
        return false;
    }
    
    public final int getReqLevel(final int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("reqLevel");
    }
    
    public final boolean isCashItem(final int itemId) {
        return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).get("cash") == 1;
    }
    
    public final int getSlots(final int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("tuc");
    }
    
    public final int getSetItemID(final int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("setItemID");
    }
    
    public final StructSetItem getSetItem(final int setItemId) {
        return this.setItems.get((byte)setItemId);
    }
    
    public final List<Integer> getScrollReqs(final int itemId) {
        if (this.scrollReqCache.containsKey(itemId)) {
            return this.scrollReqCache.get(itemId);
        }
        final List<Integer> ret = new ArrayList<Integer>();
        final MapleData data = this.getItemData(itemId).getChildByPath("req");
        if (data == null) {
            return ret;
        }
        for (final MapleData req : data.getChildren()) {
            ret.add(MapleDataTool.getInt(req));
        }
        this.scrollReqCache.put(itemId, ret);
        return ret;
    }
    
    public final IItem scrollEquipWithId(final IItem equip, final IItem scrollId, final boolean ws, final MapleCharacter chr, final int vegas, final boolean checkIfGM) {
        if (equip.getType() == 1) {
            final Equip nEquip = (Equip)equip;
            final Map<String, Integer> stats = this.getEquipStats(scrollId.getItemId());
            final Map<String, Integer> eqstats = this.getEquipStats(equip.getItemId());
            final int succ = GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId())) ? 0 : stats.get("success"));
            final int curse = GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId())) ? 0 : stats.get("cursed"));
            final int success = succ + ((vegas == 5610000 && succ == 10) ? 20 : ((vegas == 5610001 && succ == 60) ? 30 : 0));
            if (GameConstants.isPotentialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || Randomizer.nextInt(100) <= success || checkIfGM) {
                switch (scrollId.getItemId()) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004:
                    case 2049005: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 1));
                            break;
                        }
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 2));
                            break;
                        }
                        break;
                    }
                    case 2040727: {
                        byte flag = nEquip.getFlag();
                        flag |= (byte)ItemFlag.SPIKES.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2041058: {
                        byte flag = nEquip.getFlag();
                        flag |= (byte)ItemFlag.COLD.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    default: {
                        if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                            final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            if (nEquip.getStr() > 0) {
                                nEquip.setStr((short)(nEquip.getStr() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getDex() > 0) {
                                nEquip.setDex((short)(nEquip.getDex() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getInt() > 0) {
                                nEquip.setInt((short)(nEquip.getInt() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getLuk() > 0) {
                                nEquip.setLuk((short)(nEquip.getLuk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWatk() > 0) {
                                nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWdef() > 0) {
                                nEquip.setWdef((short)(nEquip.getWdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMatk() > 0) {
                                nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMdef() > 0) {
                                nEquip.setMdef((short)(nEquip.getMdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAcc() > 0) {
                                nEquip.setAcc((short)(nEquip.getAcc() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAvoid() > 0) {
                                nEquip.setAvoid((short)(nEquip.getAvoid() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getSpeed() > 0) {
                                nEquip.setSpeed((short)(nEquip.getSpeed() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getJump() > 0) {
                                nEquip.setJump((short)(nEquip.getJump() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getHp() > 0) {
                                nEquip.setHp((short)(nEquip.getHp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMp() > 0) {
                                nEquip.setMp((short)(nEquip.getMp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                                break;
                            }
                            break;
                        }
                        else if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                            final int chanc = Math.max(((scrollId.getItemId() == 2049300) ? 100 : 80) - nEquip.getEnhance() * 10, 10);
                            if (Randomizer.nextInt(100) > chanc) {
                                return null;
                            }
                            if (nEquip.getStr() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setStr((short)(nEquip.getStr() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getDex() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setDex((short)(nEquip.getDex() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getInt() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setInt((short)(nEquip.getInt() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getLuk() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setLuk((short)(nEquip.getLuk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWdef() > 0 || Randomizer.nextInt(40) == 1) {
                                nEquip.setWdef((short)(nEquip.getWdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMdef() > 0 || Randomizer.nextInt(40) == 1) {
                                nEquip.setMdef((short)(nEquip.getMdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) {
                                nEquip.setAcc((short)(nEquip.getAcc() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) {
                                nEquip.setAvoid((short)(nEquip.getAvoid() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) {
                                nEquip.setSpeed((short)(nEquip.getSpeed() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) {
                                nEquip.setJump((short)(nEquip.getJump() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) {
                                nEquip.setHp((short)(nEquip.getHp() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) {
                                nEquip.setMp((short)(nEquip.getMp() + Randomizer.nextInt(5)));
                            }
                            nEquip.setEnhance((byte)(nEquip.getEnhance() + 1));
                            break;
                        }
                        else {
                            if (!GameConstants.isPotentialScroll(scrollId.getItemId())) {
                                for (final Map.Entry<String, Integer> stat : stats.entrySet()) {
                                    final String key = stat.getKey();
                                    if (key.equals("STR")) {
                                        nEquip.setStr((short)(nEquip.getStr() + stat.getValue()));
                                    }
                                    else if (key.equals("DEX")) {
                                        nEquip.setDex((short)(nEquip.getDex() + stat.getValue()));
                                    }
                                    else if (key.equals("INT")) {
                                        nEquip.setInt((short)(nEquip.getInt() + stat.getValue()));
                                    }
                                    else if (key.equals("LUK")) {
                                        nEquip.setLuk((short)(nEquip.getLuk() + stat.getValue()));
                                    }
                                    else if (key.equals("PAD")) {
                                        nEquip.setWatk((short)(nEquip.getWatk() + stat.getValue()));
                                    }
                                    else if (key.equals("PDD")) {
                                        nEquip.setWdef((short)(nEquip.getWdef() + stat.getValue()));
                                    }
                                    else if (key.equals("MAD")) {
                                        nEquip.setMatk((short)(nEquip.getMatk() + stat.getValue()));
                                    }
                                    else if (key.equals("MDD")) {
                                        nEquip.setMdef((short)(nEquip.getMdef() + stat.getValue()));
                                    }
                                    else if (key.equals("ACC")) {
                                        nEquip.setAcc((short)(nEquip.getAcc() + stat.getValue()));
                                    }
                                    else if (key.equals("EVA")) {
                                        nEquip.setAvoid((short)(nEquip.getAvoid() + stat.getValue()));
                                    }
                                    else if (key.equals("Speed")) {
                                        nEquip.setSpeed((short)(nEquip.getSpeed() + stat.getValue()));
                                    }
                                    else if (key.equals("Jump")) {
                                        nEquip.setJump((short)(nEquip.getJump() + stat.getValue()));
                                    }
                                    else if (key.equals("MHP")) {
                                        nEquip.setHp((short)(nEquip.getHp() + stat.getValue()));
                                    }
                                    else if (key.equals("MMP")) {
                                        nEquip.setMp((short)(nEquip.getMp() + stat.getValue()));
                                    }
                                    else if (key.equals("MHPr")) {
                                        nEquip.setHpR((short)(nEquip.getHpR() + stat.getValue()));
                                    }
                                    else {
                                        if (!key.equals("MMPr")) {
                                            continue;
                                        }
                                        nEquip.setMpR((short)(nEquip.getMpR() + stat.getValue()));
                                    }
                                }
                                break;
                            }
                            if (nEquip.getState() != 0) {
                                break;
                            }
                            final int chanc = (scrollId.getItemId() == 2049400) ? 90 : 70;
                            if (Randomizer.nextInt(100) > chanc) {
                                return null;
                            }
                            nEquip.resetPotential();
                            break;
                        }
                        
                    }
                }
                if (!GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte)(nEquip.getLevel() + 1));
                }
            }
            else {
                if (!ws && !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() - 1));
                }
                if (Randomizer.nextInt(99) < curse) {
                    return null;
                }
            }
        }
        return equip;
    }
    
    public final IItem getEquipById(final int equipId) {
        return this.getEquipById(equipId, -1);
    }
    
    public final IItem getEquipById(final int equipId, final int ringId) {
        final Equip nEquip = new Equip(equipId, (short)0, ringId, (byte)0);
        nEquip.setQuantity((short)1);
        final Map<String, Integer> stats = this.getEquipStats(equipId);
        if (stats != null) {
            for (final Map.Entry<String, Integer> stat : stats.entrySet()) {
                final String key = stat.getKey();
                if (key.equals("STR")) {
                    nEquip.setStr((short)(int)stat.getValue());
                }
                else if (key.equals("DEX")) {
                    nEquip.setDex((short)(int)stat.getValue());
                }
                else if (key.equals("INT")) {
                    nEquip.setInt((short)(int)stat.getValue());
                }
                else if (key.equals("LUK")) {
                    nEquip.setLuk((short)(int)stat.getValue());
                }
                else if (key.equals("PAD")) {
                    nEquip.setWatk((short)(int)stat.getValue());
                }
                else if (key.equals("PDD")) {
                    nEquip.setWdef((short)(int)stat.getValue());
                }
                else if (key.equals("MAD")) {
                    nEquip.setMatk((short)(int)stat.getValue());
                }
                else if (key.equals("MDD")) {
                    nEquip.setMdef((short)(int)stat.getValue());
                }
                else if (key.equals("ACC")) {
                    nEquip.setAcc((short)(int)stat.getValue());
                }
                else if (key.equals("EVA")) {
                    nEquip.setAvoid((short)(int)stat.getValue());
                }
                else if (key.equals("Speed")) {
                    nEquip.setSpeed((short)(int)stat.getValue());
                }
                else if (key.equals("Jump")) {
                    nEquip.setJump((short)(int)stat.getValue());
                }
                else if (key.equals("MHP")) {
                    nEquip.setHp((short)(int)stat.getValue());
                }
                else if (key.equals("MMP")) {
                    nEquip.setMp((short)(int)stat.getValue());
                }
                else if (key.equals("MHPr")) {
                    nEquip.setHpR((short)(int)stat.getValue());
                }
                else if (key.equals("MMPr")) {
                    nEquip.setMpR((short)(int)stat.getValue());
                }
                else if (key.equals("tuc")) {
                    nEquip.setUpgradeSlots(stat.getValue().byteValue());
                }
                else if (key.equals("Craft")) {
                    nEquip.setHands(stat.getValue().shortValue());
                }
                else {
                    if (!key.equals("durability")) {
                        continue;
                    }
                    nEquip.setDurability(stat.getValue());
                }
            }
        }
        this.equipCache.put(equipId, nEquip);
        return nEquip.copy();
    }
    
    private final short getRandStat(final short defaultValue, final int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        final int lMaxRange = (int)Math.min(Math.ceil(defaultValue * 0.1), maxRange);
        return (short)(defaultValue - lMaxRange + Math.floor(Math.random() * (lMaxRange * 2 + 1)));
    }
    
    public final Equip randomizeStats(final Equip equip) {
        equip.setStr(this.getRandStat(equip.getStr(), 5));
        equip.setDex(this.getRandStat(equip.getDex(), 5));
        equip.setInt(this.getRandStat(equip.getInt(), 5));
        equip.setLuk(this.getRandStat(equip.getLuk(), 5));
        equip.setMatk(this.getRandStat(equip.getMatk(), 5));
        equip.setWatk(this.getRandStat(equip.getWatk(), 5));
        equip.setAcc(this.getRandStat(equip.getAcc(), 5));
        equip.setAvoid(this.getRandStat(equip.getAvoid(), 5));
        equip.setJump(this.getRandStat(equip.getJump(), 5));
        equip.setHands(this.getRandStat(equip.getHands(), 5));
        equip.setSpeed(this.getRandStat(equip.getSpeed(), 5));
        equip.setWdef(this.getRandStat(equip.getWdef(), 10));
        equip.setMdef(this.getRandStat(equip.getMdef(), 10));
        equip.setHp(this.getRandStat(equip.getHp(), 10));
        equip.setMp(this.getRandStat(equip.getMp(), 10));
        return equip;
    }
    
    public final MapleStatEffect getItemEffect(final int itemId) {
        MapleStatEffect ret = this.itemEffects.get(itemId);
        if (ret == null) {
            final MapleData item = this.getItemData(itemId);
            if (item == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            this.itemEffects.put(itemId, ret);
        }
        return ret;
    }
    
    public final List<Pair<Integer, Integer>> getSummonMobs(final int itemId) {
        if (this.summonMobCache.containsKey(itemId)) {
            return this.summonMobCache.get(itemId);
        }
        if (!GameConstants.isSummonSack(itemId)) {
            return null;
        }
        final MapleData data = this.getItemData(itemId).getChildByPath("mob");
        if (data == null) {
            return null;
        }
        final List<Pair<Integer, Integer>> mobPairs = new ArrayList<Pair<Integer, Integer>>();
        for (final MapleData child : data.getChildren()) {
            mobPairs.add(new Pair<Integer, Integer>(MapleDataTool.getIntConvert("id", child), MapleDataTool.getIntConvert("prob", child)));
        }
        this.summonMobCache.put(itemId, mobPairs);
        return mobPairs;
    }
    
    public final int getCardMobId(final int id) {
        if (id == 0) {
            return 0;
        }
        if (this.monsterBookID.containsKey(id)) {
            return this.monsterBookID.get(id);
        }
        final MapleData data = this.getItemData(id);
        final int monsterid = MapleDataTool.getIntConvert("info/mob", data, 0);
        if (monsterid == 0) {
            return 0;
        }
        this.monsterBookID.put(id, monsterid);
        return this.monsterBookID.get(id);
    }
    
    public final int getWatkForProjectile(final int itemId) {
        Integer atk = this.projectileWatkCache.get(itemId);
        if (atk != null) {
            return atk;
        }
        final MapleData data = this.getItemData(itemId);
        atk = MapleDataTool.getInt("info/incPAD", data, 0);
        this.projectileWatkCache.put(itemId, atk);
        return atk;
    }
    
    public final boolean canScroll(final int scrollid, final int itemid) {
        return scrollid / 100 % 100 == itemid / 10000 % 100;
    }
    
    public final String getName(final int itemId) {
        if (this.nameCache.containsKey(itemId)) {
            return this.nameCache.get(itemId);
        }
        final MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        final String ret = MapleDataTool.getString("name", strings, "(null)");
        this.nameCache.put(itemId, ret);
        return ret;
    }
    
    public final String getDesc(final int itemId) {
        if (this.descCache.containsKey(itemId)) {
            return this.descCache.get(itemId);
        }
        final MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        final String ret = MapleDataTool.getString("desc", strings, null);
        this.descCache.put(itemId, ret);
        return ret;
    }
    
    public final String getMsg(final int itemId) {
        if (this.msgCache.containsKey(itemId)) {
            return this.msgCache.get(itemId);
        }
        final MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        final String ret = MapleDataTool.getString("msg", strings, null);
        this.msgCache.put(itemId, ret);
        return ret;
    }
    
    public final short getItemMakeLevel(final int itemId) {
        if (this.itemMakeLevel.containsKey(itemId)) {
            return this.itemMakeLevel.get(itemId);
        }
        if (itemId / 10000 != 400) {
            return 0;
        }
        final short lvl = (short)MapleDataTool.getIntConvert("info/lv", this.getItemData(itemId), 0);
        this.itemMakeLevel.put(itemId, lvl);
        return lvl;
    }
    
    public final byte isConsumeOnPickup(final int itemId) {
        if (this.consumeOnPickupCache.containsKey(itemId)) {
            return this.consumeOnPickupCache.get(itemId);
        }
        final MapleData data = this.getItemData(itemId);
        byte consume = (byte)MapleDataTool.getIntConvert("spec/consumeOnPickup", data, 0);
        if (consume == 0) {
            consume = (byte)MapleDataTool.getIntConvert("specEx/consumeOnPickup", data, 0);
        }
        if (consume == 1 && MapleDataTool.getIntConvert("spec/party", this.getItemData(itemId), 0) > 0) {
            consume = 2;
        }
        this.consumeOnPickupCache.put(itemId, consume);
        return consume;
    }
    
    public final boolean isDropRestricted(final int itemId) {
        if (this.dropRestrictionCache.containsKey(itemId)) {
            return this.dropRestrictionCache.get(itemId);
        }
        final MapleData data = this.getItemData(itemId);
        boolean trade = false;
        if (MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1 || MapleDataTool.getIntConvert("info/quest", data, 0) == 1) {
            trade = true;
        }
        this.dropRestrictionCache.put(itemId, trade);
        return trade;
    }
    
    public final boolean isPickupRestricted(final int itemId) {
        if (this.pickupRestrictionCache.containsKey(itemId)) {
            return this.pickupRestrictionCache.get(itemId);
        }
        final boolean bRestricted = MapleDataTool.getIntConvert("info/only", this.getItemData(itemId), 0) == 1;
        this.pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }
    
    public final boolean isAccountShared(final int itemId) {
        if (this.accCache.containsKey(itemId)) {
            return this.accCache.get(itemId);
        }
        final boolean bRestricted = MapleDataTool.getIntConvert("info/accountSharable", this.getItemData(itemId), 0) == 1;
        this.accCache.put(itemId, bRestricted);
        return bRestricted;
    }
    
    public final int getStateChangeItem(final int itemId) {
        if (this.stateChangeCache.containsKey(itemId)) {
            return this.stateChangeCache.get(itemId);
        }
        final int triggerItem = MapleDataTool.getIntConvert("info/stateChangeItem", this.getItemData(itemId), 0);
        this.stateChangeCache.put(itemId, triggerItem);
        return triggerItem;
    }
    
    public final int getMeso(final int itemId) {
        if (this.mesoCache.containsKey(itemId)) {
            return this.mesoCache.get(itemId);
        }
        final int triggerItem = MapleDataTool.getIntConvert("info/meso", this.getItemData(itemId), 0);
        this.mesoCache.put(itemId, triggerItem);
        return triggerItem;
    }
    
    public final boolean isKarmaEnabled(final int itemId) {
        if (this.karmaEnabledCache.containsKey(itemId)) {
            return this.karmaEnabledCache.get(itemId) == 1;
        }
        final int iRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", this.getItemData(itemId), 0);
        this.karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 1;
    }
    
    public final boolean isPKarmaEnabled(final int itemId) {
        if (this.karmaEnabledCache.containsKey(itemId)) {
            return this.karmaEnabledCache.get(itemId) == 2;
        }
        final int iRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", this.getItemData(itemId), 0);
        this.karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 2;
    }
    
    public final boolean isPickupBlocked(final int itemId) {
        if (this.blockPickupCache.containsKey(itemId)) {
            return this.blockPickupCache.get(itemId);
        }
        final boolean iRestricted = MapleDataTool.getIntConvert("info/pickUpBlock", this.getItemData(itemId), 0) == 1;
        this.blockPickupCache.put(itemId, iRestricted);
        return iRestricted;
    }
    
    public final boolean isLogoutExpire(final int itemId) {
        if (this.logoutExpireCache.containsKey(itemId)) {
            return this.logoutExpireCache.get(itemId);
        }
        final boolean iRestricted = MapleDataTool.getIntConvert("info/expireOnLogout", this.getItemData(itemId), 0) == 1;
        this.logoutExpireCache.put(itemId, iRestricted);
        return iRestricted;
    }
    
    public final boolean cantSell(final int itemId) {
        if (this.notSaleCache.containsKey(itemId)) {
            return this.notSaleCache.get(itemId);
        }
        final boolean bRestricted = MapleDataTool.getIntConvert("info/notSale", this.getItemData(itemId), 0) == 1;
        this.notSaleCache.put(itemId, bRestricted);
        return bRestricted;
    }
    
    public final Pair<Integer, List<StructRewardItem>> getRewardItem(final int itemid) {
        if (this.RewardItem.containsKey(itemid)) {
            return this.RewardItem.get(itemid);
        }
        final MapleData data = this.getItemData(itemid);
        if (data == null) {
            return null;
        }
        final MapleData rewards = data.getChildByPath("reward");
        if (rewards == null) {
            return null;
        }
        int totalprob = 0;
        final List<StructRewardItem> all = new ArrayList<StructRewardItem>();
        for (final MapleData reward : rewards) {
            final StructRewardItem struct = new StructRewardItem();
            struct.itemid = MapleDataTool.getInt("item", reward, 0);
            struct.prob = (byte)MapleDataTool.getInt("prob", reward, 0);
            struct.quantity = (short)MapleDataTool.getInt("count", reward, 0);
            struct.effect = MapleDataTool.getString("Effect", reward, "");
            struct.worldmsg = MapleDataTool.getString("worldMsg", reward, null);
            struct.period = MapleDataTool.getInt("period", reward, -1);
            totalprob += struct.prob;
            all.add(struct);
        }
        final Pair<Integer, List<StructRewardItem>> toreturn = new Pair<Integer, List<StructRewardItem>>(totalprob, all);
        this.RewardItem.put(itemid, toreturn);
        return toreturn;
    }
    
    public final Map<String, Integer> getSkillStats(final int itemId) {
        if (this.SkillStatsCache.containsKey(itemId)) {
            return this.SkillStatsCache.get(itemId);
        }
        if (itemId / 10000 != 228 && itemId / 10000 != 229 && itemId / 10000 != 562) {
            return null;
        }
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        final MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        final Map<String, Integer> ret = new LinkedHashMap<String, Integer>();
        for (final MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
            }
        }
        ret.put("masterLevel", MapleDataTool.getInt("masterLevel", info, 0));
        ret.put("reqSkillLevel", MapleDataTool.getInt("reqSkillLevel", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        final MapleData skill = info.getChildByPath("skill");
        for (int i = 0; i < skill.getChildren().size(); ++i) {
            ret.put("skillid" + i, MapleDataTool.getInt(Integer.toString(i), skill, 0));
        }
        this.SkillStatsCache.put(itemId, ret);
        return ret;
    }
    
    public final List<Integer> petsCanConsume(final int itemId) {
        if (this.petsCanConsumeCache.get(itemId) != null) {
            return this.petsCanConsumeCache.get(itemId);
        }
        final List<Integer> ret = new ArrayList<Integer>();
        final MapleData data = this.getItemData(itemId);
        if (data == null || data.getChildByPath("spec") == null) {
            return ret;
        }
        int curPetId = 0;
        for (final MapleData c : data.getChildByPath("spec")) {
            try {
                Integer.parseInt(c.getName());
            }
            catch (NumberFormatException e) {
                continue;
            }
            curPetId = MapleDataTool.getInt(c, 0);
            if (curPetId == 0) {
                break;
            }
            ret.add(curPetId);
        }
        this.petsCanConsumeCache.put(itemId, ret);
        return ret;
    }
    
    public final boolean isQuestItem(final int itemId) {
        if (this.isQuestItemCache.containsKey(itemId)) {
            return this.isQuestItemCache.get(itemId);
        }
        final boolean questItem = MapleDataTool.getIntConvert("info/quest", this.getItemData(itemId), 0) == 1;
        this.isQuestItemCache.put(itemId, questItem);
        return questItem;
    }
    
    public final Pair<Integer, List<Integer>> questItemInfo(final int itemId) {
        if (this.questItems.containsKey(itemId)) {
            return this.questItems.get(itemId);
        }
        if (itemId / 10000 != 422 || this.getItemData(itemId) == null) {
            return null;
        }
        final MapleData itemD = this.getItemData(itemId).getChildByPath("info");
        if (itemD == null || itemD.getChildByPath("consumeItem") == null) {
            return null;
        }
        final List<Integer> consumeItems = new ArrayList<Integer>();
        for (final MapleData consume : itemD.getChildByPath("consumeItem")) {
            consumeItems.add(MapleDataTool.getInt(consume, 0));
        }
        final Pair<Integer, List<Integer>> questItem = new Pair<Integer, List<Integer>>(MapleDataTool.getIntConvert("questId", itemD, 0), consumeItems);
        this.questItems.put(itemId, questItem);
        return questItem;
    }
    
    public final boolean itemExists(final int itemId) {
        return GameConstants.getInventoryType(itemId) != MapleInventoryType.UNDEFINED && this.getItemData(itemId) != null;
    }
    
    public final boolean isCash(final int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || this.getEquipStats(itemId).get("cash") > 0;
    }
    
    public MapleInventoryType getInventoryType(final int itemId) {
        if (this.inventoryTypeCache.containsKey(itemId)) {
            return this.inventoryTypeCache.get(itemId);
        }
        final String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = this.itemData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    final MapleInventoryType ret = MapleInventoryType.getByWZName(topDir.getName());
                    this.inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
                if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    final MapleInventoryType ret = MapleInventoryType.getByWZName(topDir.getName());
                    this.inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        root = this.equipData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    final MapleInventoryType ret = MapleInventoryType.EQUIP;
                    this.inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        final MapleInventoryType ret = MapleInventoryType.UNDEFINED;
        this.inventoryTypeCache.put(itemId, ret);
        return ret;
    }
    
    public short getPetFlagInfo(final int itemId) {
        short flag = 0;
        if (itemId / 10000 != 500) {
            return flag;
        }
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return flag;
        }
        if (MapleDataTool.getIntConvert("info/pickupItem", item, 0) > 0) {
            flag |= 0x1;
        }
        if (MapleDataTool.getIntConvert("info/longRange", item, 0) > 0) {
            flag |= 0x2;
        }
        if (MapleDataTool.getIntConvert("info/pickupAll", item, 0) > 0) {
            flag |= 0x4;
        }
        if (MapleDataTool.getIntConvert("info/sweepForDrop", item, 0) > 0) {
            flag |= 0x10;
        }
        if (MapleDataTool.getIntConvert("info/consumeHP", item, 0) > 0) {
            flag |= 0x20;
        }
        if (MapleDataTool.getIntConvert("info/consumeMP", item, 0) > 0) {
            flag |= 0x40;
        }
        return flag;
    }
    
    public boolean isKarmaAble(final int itemId) {
        if (this.karmaCache.containsKey(itemId)) {
            return this.karmaCache.get(itemId);
        }
        final MapleData data = this.getItemData(itemId);
        final boolean bRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", data, 0) > 0;
        this.karmaCache.put(itemId, bRestricted);
        return bRestricted;
    }
    
    public List<Pair<String, Integer>> getItemLevelupStats(final int itemId, final int level, final boolean timeless) {
        final List<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
        final MapleData data = this.getItemData(itemId);
        final MapleData data2 = data.getChildByPath("info").getChildByPath("level");
        if (data2 != null) {
            final MapleData data3 = data2.getChildByPath("info").getChildByPath(Integer.toString(level));
            if (data3 != null) {
                for (final MapleData da : data3.getChildren()) {
                    if (Math.random() < 0.9) {
                        if (da.getName().startsWith("incDEXMin")) {
                            list.add(new Pair<String, Integer>("incDEX", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incDEXMax")))));
                        }
                        else if (da.getName().startsWith("incSTRMin")) {
                            list.add(new Pair<String, Integer>("incSTR", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incSTRMax")))));
                        }
                        else if (da.getName().startsWith("incINTMin")) {
                            list.add(new Pair<String, Integer>("incINT", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incINTMax")))));
                        }
                        else if (da.getName().startsWith("incLUKMin")) {
                            list.add(new Pair<String, Integer>("incLUK", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incLUKMax")))));
                        }
                        else if (da.getName().startsWith("incMHPMin")) {
                            list.add(new Pair<String, Integer>("incMHP", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incMHPMax")))));
                        }
                        else if (da.getName().startsWith("incMMPMin")) {
                            list.add(new Pair<String, Integer>("incMMP", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incMMPMax")))));
                        }
                        else if (da.getName().startsWith("incPADMin")) {
                            list.add(new Pair<String, Integer>("incPAD", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incPADMax")))));
                        }
                        else if (da.getName().startsWith("incMADMin")) {
                            list.add(new Pair<String, Integer>("incMAD", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incMADMax")))));
                        }
                        else if (da.getName().startsWith("incPDDMin")) {
                            list.add(new Pair<String, Integer>("incPDD", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incPDDMax")))));
                        }
                        else if (da.getName().startsWith("incMDDMin")) {
                            list.add(new Pair<String, Integer>("incMDD", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incMDDMax")))));
                        }
                        else if (da.getName().startsWith("incACCMin")) {
                            list.add(new Pair<String, Integer>("incACC", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incACCMax")))));
                        }
                        else if (da.getName().startsWith("incEVAMin")) {
                            list.add(new Pair<String, Integer>("incEVA", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incEVAMax")))));
                        }
                        else if (da.getName().startsWith("incSpeedMin")) {
                            list.add(new Pair<String, Integer>("incSpeed", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incSpeedMax")))));
                        }
                        else {
                            if (!da.getName().startsWith("incJumpMin")) {
                                continue;
                            }
                            list.add(new Pair<String, Integer>("incJump", this.rand(MapleDataTool.getInt(da), MapleDataTool.getInt(data3.getChildByPath("incJumpMax")))));
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public boolean isUntradeableOnEquip(final int itemId) {
        if (this.onEquipUntradableCache.containsKey(itemId)) {
            return this.onEquipUntradableCache.get(itemId);
        }
        final boolean untradableOnEquip = MapleDataTool.getIntConvert("info/equipTradeBlock", this.getItemData(itemId), 0) > 0;
        this.onEquipUntradableCache.put(itemId, untradableOnEquip);
        return untradableOnEquip;
    }
    
    public int getExpCache(final int itemId) {
        if (this.getExpCache.containsKey(itemId)) {
            return this.getExpCache.get(itemId);
        }
        final MapleData item = this.getItemData(itemId);
        if (item == null) {
            return 0;
        }
        int pEntry = 0;
        final MapleData pData = item.getChildByPath("spec/exp");
        if (pData == null) {
            return 0;
        }
        pEntry = MapleDataTool.getInt(pData);
        this.getExpCache.put(itemId, pEntry);
        return pEntry;
    }
    
    static {
        instance = new MapleItemInformationProvider();
    }
}
