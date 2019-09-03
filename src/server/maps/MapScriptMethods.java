package server.maps;

import java.awt.Point;

import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;

public class MapScriptMethods
{
    private static final Point witchTowerPos;
    private static final String[] mulungEffects;
    static {
        witchTowerPos = new Point(-60, 184);
        mulungEffects = new String[] { "\u6211\u4e00\u76f4\u5728\u7b49\u4f60\uff01\u5982\u679c\u4f60\u6709\u4e00\u4e1d\u7684\u52c7\u6c14\uff0c\u4f60\u4f1a\u8d70\u5728\u90a3\u4e2a\u95e8\u7684\u53f3\u8fb9!", "\u5982\u4f55\u52c7\u6562\u7684\u4f60\u91c7\u53d6\u7684\u9053\u573a\u8bad\u7ec3\u5854!", "\u6211\u4f1a\u786e\u4fdd\u4f60\u4f1a\u540e\u6094\u5e26\u4e0a\u4e86\u9053\u573a\u8bad\u7ec3\u5854!", "\u6211\u505a\u4f60\u7684\u5021\u5bfc\u575a\u97e7\uff01\u4f46\u4e0d\u8981\u628a\u4f60\u7684\u52c7\u6c14\u4e0e\u9c81\u83bd!", "\u5982\u679c\u4f60\u60f3\u4e00\u6b65\u4e00\u6b65\u5730\u8d70\u5230\u5931\u8d25\u7684\u9053\u8def\u4e0a\uff0c\u7528\u4e00\u5207\u65b9\u6cd5\u53bb\u505a!" };
    }
    
    private enum onFirstUserEnter
    {
        pepeking_effect, 
        dojang_Eff, 
        PinkBeen_before, 
        onRewordMap, 
        StageMsg_together, 
        StageMsg_davy, 
        party6weatherMsg, 
        StageMsg_juliet, 
        StageMsg_romio, 
        moonrabbit_mapEnter, 
        astaroth_summon, 
        boss_Ravana, 
        killing_BonusSetting, 
        killing_MapSetting, 
        metro_firstSetting, 
        balog_bonusSetting, 
        balog_summon, 
        easy_balog_summon, 
        Sky_TrapFEnter, 
        shammos_Fenter, 
        PRaid_D_Fenter, 
        PRaid_B_Fenter, 
        GhostF, 
        NULL;
        
        private static onFirstUserEnter fromString(final String Str) {
            try {
                return valueOf(Str);
            }
            catch (IllegalArgumentException ex) {
                return onFirstUserEnter.NULL;
            }
        }
    }
    
    private enum onUserEnter
    {
        babyPigMap, 
        crash_Dragon, 
        evanleaveD, 
        getDragonEgg, 
        meetWithDragon, 
        go1010100, 
        go1010200, 
        go1010300, 
        go1010400, 
        evanPromotion, 
        PromiseDragon, 
        evanTogether, 
        incubation_dragon, 
        TD_MC_Openning, 
        TD_MC_gasi, 
        TD_MC_title, 
        cygnusJobTutorial, 
        cygnusTest, 
        startEreb, 
        dojang_Msg, 
        dojang_1st, 
        reundodraco, 
        undomorphdarco, 
        explorationPoint, 
        goAdventure, 
        go10000, 
        go20000, 
        go30000, 
        go40000, 
        go50000, 
        go1000000, 
        go1010000, 
        go1020000, 
        go2000000, 
        go104000000, 
        goArcher, 
        goPirate, 
        goRogue, 
        goMagician, 
        goSwordman, 
        goLith, 
        iceCave, 
        mirrorCave, 
        aranDirection, 
        rienArrow, 
        rien, 
        check_count, 
        Massacre_first, 
        Massacre_result, 
        aranTutorAlone, 
        evanAlone, 
        dojang_QcheckSet, 
        Sky_StageEnter, 
        outCase, 
        balog_buff, 
        balog_dateSet, 
        Sky_BossEnter, 
        Sky_GateMapEnter, 
        shammos_Enter, 
        shammos_Result, 
        shammos_Base, 
        dollCave00, 
        dollCave01, 
        Sky_Quest, 
        enterBlackfrog, 
        onSDI, 
        blackSDI, 
        summonIceWall, 
        metro_firstSetting, 
        start_itemTake, 
        PRaid_D_Enter, 
        PRaid_B_Enter, 
        PRaid_Revive, 
        PRaid_W_Enter, 
        PRaid_WinEnter, 
        PRaid_FailEnter, 
        Ghost, 
        NULL;
        
        private static onUserEnter fromString(final String Str) {
            try {
                return valueOf(Str);
            }
            catch (IllegalArgumentException ex) {
                return onUserEnter.NULL;
            }
        }
    }
    public static void startScript_FirstUser(final MapleClient c, final String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("\u60a8\u5df2\u7ecf\u5efa\u7acb\u4e0e\u5730\u56fe\u811a\u672cScript_FirstUser\u3010" + scriptName + "\u3011\u7684\u8fde\u63a5");
        }
        switch (scriptName) {
            case "summon_pepeking": {
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/W"));
                if (c.getPlayer().getMap().getAllMonster().isEmpty()) {
                    final int rand = Randomizer.rand(0, 2);
                    final MapleMonster mob = MapleLifeFactory.getMonster(3300005 + rand);
                    final OverrideMonsterStats oms = new OverrideMonsterStats();
                    oms.setOExp(7110);
                    oms.setOHp(mob.getMobMaxHp());
                    oms.setOMp(mob.getMobMaxMp());
                    mob.setOverrideStats(oms);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(358, -68));
                    if (rand == 0) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                    }
                    else if (rand == 1) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeG"));
                    }
                    else if (rand == 2) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeW"));
                    }
                }
                else {
                    c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                }
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/chat/nugu"));
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/B"));
                break;
            }
        }
        switch (onFirstUserEnter.fromString(scriptName)) {
            case pepeking_effect: {
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/W"));
                if (c.getPlayer().getMap().getAllMonster().isEmpty()) {
                    final int rand2 = Randomizer.rand(0, 2);
                    final MapleMonster mob2 = MapleLifeFactory.getMonster(3300005 + rand2);
                    final OverrideMonsterStats oms2 = new OverrideMonsterStats();
                    oms2.setOExp(7110);
                    oms2.setOHp(mob2.getMobMaxHp());
                    oms2.setOMp(mob2.getMobMaxMp());
                    mob2.setOverrideStats(oms2);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob2, new Point(358, -68));
                    if (rand2 == 0) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                    }
                    else if (rand2 == 1) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeG"));
                    }
                    else if (rand2 == 2) {
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeW"));
                    }
                }
                else {
                    c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                }
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/chat/nugu"));
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/B"));
                break;
            }
            case dojang_Eff: {
                final int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                final int stage = temp - temp / 100 * 100;
                sendDojoClock(c, getTiming(stage) * 60);
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(c);
                break;
            }
            case GhostF: {
                c.getPlayer().getMap().startMapEffect("\u8fd9\u4e2a\u5730\u56fe\u611f\u89c9\u9634\u68ee\u68ee\u7684..\u6709\u79cd\u83ab\u540d\u7684\u5947\u602a\u611f\u89c9..", 5120025);
                break;
            }
            case moonrabbit_mapEnter: {
                c.getPlayer().getMap().startMapEffect("\u7ca5\u73af\u7ed5\u6708\u7403\u7684\u6708\u89c1\u8349\u79cd\u5b50\u548c\u4fdd\u62a4\u6708\u7403\u5154\u5b50\uff01", 5120016);
                break;
            }
            case StageMsg_together: {
                switch (c.getPlayer().getMapId()) {
                    case 103000800: {
                        c.getPlayer().getMap().startMapEffect("\u89e3\u51b3\u95ee\u9898\u5e76\u6536\u96c6\u901a\u884c\u8bc1\u7684\u6570\u91cf!", 5120017);
                        break;
                    }
                    case 103000801: {
                        c.getPlayer().getMap().startMapEffect("\u4e0a\u7ef3\u7d22\uff0c\u63ed\u5f00\u6b63\u786e\u7684\u7ec4\u5408!", 5120017);
                        break;
                    }
                    case 103000802: {
                        c.getPlayer().getMap().startMapEffect("\u5728\u5e73\u53f0\u4e0a\u63a8\u51fa\u6b63\u786e\u7684\u7ec4\u5408!", 5120017);
                        break;
                    }
                    case 103000803: {
                        c.getPlayer().getMap().startMapEffect("\u5728\u6876\u4e0a\uff0c\u63ed\u5f00\u6b63\u786e\u7684\u7ec4\u5408!", 5120017);
                        break;
                    }
                    case 103000804: {
                        c.getPlayer().getMap().startMapEffect("\u6253\u8d25\u7eff\u6c34\u7075\u738b\u548c\u4ed6\u7684\u722a\u7259!", 5120017);
                        break;
                    }
                }
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926100000: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u627e\u5230\u9690\u85cf\u7684\u95e8\uff0c\u901a\u8fc7\u8c03\u67e5\u5b9e\u9a8c\u5ba4\uff01", 5120021);
                        break;
                    }
                    case 926100001: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u5230\u4f60\u7684\u65b9\u5f0f\u901a\u8fc7\u8fd9\u9ed1\u6697\uff01", 5120021);
                        break;
                    }
                    case 926100100: {
                        c.getPlayer().getMap().startMapEffect("\u5145\u6ee1\u80fd\u91cf\u7684\u70e7\u676f\uff01", 5120021);
                        break;
                    }
                    case 926100200: {
                        c.getPlayer().getMap().startMapEffect("\u83b7\u53d6\u5b9e\u9a8c\u7684\u6587\u4ef6\u901a\u8fc7\u6bcf\u4e2a\u95e8\uff01", 5120021);
                        break;
                    }
                    case 926100203: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u6253\u8d25\u6240\u6709\u7684\u602a\u7269\uff01", 5120021);
                        break;
                    }
                    case 926100300: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u5230\u4f60\u7684\u65b9\u6cd5\u901a\u8fc7\u5b9e\u9a8c\u5ba4\uff01", 5120021);
                        break;
                    }
                    case 926100401: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u4fdd\u62a4\u6211\u7684\u7231\u4eba\uff01", 5120021);
                        break;
                    }
                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926110000: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u627e\u5230\u9690\u85cf\u7684\u95e8\uff0c\u901a\u8fc7\u8c03\u67e5\u5b9e\u9a8c\u5ba4\uff01", 5120022);
                        break;
                    }
                    case 926110001: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u5230\u4f60\u7684\u65b9\u5f0f\u901a\u8fc7\u8fd9\u9ed1\u6697\uff01", 5120022);
                        break;
                    }
                    case 926110100: {
                        c.getPlayer().getMap().startMapEffect("\u5145\u6ee1\u80fd\u91cf\u7684\u70e7\u676f\uff01", 5120022);
                        break;
                    }
                    case 926110200: {
                        c.getPlayer().getMap().startMapEffect("\u83b7\u53d6\u5b9e\u9a8c\u7684\u6587\u4ef6\u901a\u8fc7\u6bcf\u4e2a\u95e8\uff01", 5120022);
                        break;
                    }
                    case 926110203: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u6253\u8d25\u6240\u6709\u7684\u602a\u7269\uff01", 5120022);
                        break;
                    }
                    case 926110300: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u5230\u4f60\u7684\u65b9\u6cd5\u901a\u8fc7\u5b9e\u9a8c\u5ba4\uff01", 5120022);
                        break;
                    }
                    case 926110401: {
                        c.getPlayer().getMap().startMapEffect("\u8bf7\u4fdd\u62a4\u6211\u7684\u7231\u4eba\uff01", 5120022);
                        break;
                    }
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930000000: {
                        c.getPlayer().getMap().startMapEffect("\u8fdb\u5165\u4f20\u9001\u70b9\uff0c\u6211\u8981\u5bf9\u4f60\u4eec\u65bd\u653e\u53d8\u8eab\u9b54\u6cd5\u4e86\uff01", 5120023);
                        break;
                    }
                    case 930000100: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u706d\u6240\u6709\u602a\u7269\uff01", 5120023);
                        break;
                    }
                    case 930000200: {
                        c.getPlayer().getMap().startMapEffect("\u5bf9\u8346\u68d8\u65bd\u653e\u7a00\u91ca\u7684\u6bd2\u6db24\u4e2a\uff01", 5120023);
                        break;
                    }
                    case 930000300: {
                        c.getPlayer().getMap().startMapEffect("\u5988\u5988\u4f60\u5728\u54ea\u91cc\u545c\u545c\u54ed\u54ed\u5594\u6211\u8ff7\u8def\u4e86", 5120023);
                        break;
                    }
                    case 930000400: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u6211\u5bf9\u8bdd\u62ff\u51c0\u5316\u4e4b\u73e0\u5176\u4e2d\u4e00\u4e2a\u961f\u5458\u96c6\u6ee110\u4e2a\u602a\u7269\u73e0\u7ed9\u6211\uff01", 5120023);
                        break;
                    }
                    case 930000500: {
                        c.getPlayer().getMap().startMapEffect("\u4ece\u602a\u4eba\u4e66\u684c\u4e2d\u5bfb\u627e\u7d2b\u8272\u9b54\u529b\u77f3\uff01\uff01", 5120023);
                        break;
                    }
                    case 930000600: {
                        c.getPlayer().getMap().startMapEffect("\u5c06\u7d2b\u8272\u9b54\u529b\u77f3\u653e\u5728\u796d\u575b\u4e0a\uff01", 5120023);
                        break;
                    }
                }
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925100000: {
                        c.getPlayer().getMap().startMapEffect("\u51fb\u8d25\u5916\u7684\u602a\u7269\u7684\u8239\u8236\u63a8\u8fdb!", 5120020);
                        break;
                    }
                    case 925100100: {
                        c.getPlayer().getMap().startMapEffect("\u6211\u4eec\u5fc5\u987b\u8bc1\u660e\u81ea\u5df1\uff01\u7ed9\u6211\u6d77\u76d7\u52cb\u7ae0!", 5120020);
                        break;
                    }
                    case 925100200: {
                        c.getPlayer().getMap().startMapEffect("\u5728\u8fd9\u91cc\u51fb\u8d25\u5b88\u536b!", 5120020);
                        break;
                    }
                    case 925100300: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u706d\u8fd9\u91cc\u7684\u5b88\u536b!", 5120020);
                        break;
                    }
                    case 925100400: {
                        c.getPlayer().getMap().startMapEffect("\u9501\u4e0a\u95e8\uff01\u5bc6\u5c01\u8239\u8236\u52a8\u529b\u7684\u6839!", 5120020);
                        break;
                    }
                    case 925100500: {
                        c.getPlayer().getMap().startMapEffect("\u4e3b\uff0c\u6d88\u706d\u6d77\u76d7!", 5120020);
                        break;
                    }
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9300119 : 9300119;
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1: {
                            mobId = 9300105;
                            break;
                        }
                        case 2: {
                            mobId = 9300106;
                            break;
                        }
                    }
                    final MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                    break;
                }
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26));
                break;
            }
            case boss_Ravana: {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "\u90a3\u5df2\u7ecf\u51fa\u73b0!"));
                break;
            }
            case killing_BonusSetting: {
                c.getPlayer().getMap().resetFully();
                c.getSession().write((Object)MaplePacketCreator.showEffect("killing/bonus/bonus"));
                c.getSession().write((Object)MaplePacketCreator.showEffect("killing/bonus/stage"));
                Point pos1 = null;
                Point pos2 = null;
                Point pos3 = null;
                int spawnPer = 0;
                int mobId2 = 0;
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId2 = 9700020;
                    spawnPer = 10;
                }
                else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId2 = 9700019;
                    spawnPer = 10;
                }
                else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId2 = 9700019;
                    spawnPer = 15;
                }
                else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId2 = 9700019;
                    spawnPer = 20;
                }
                else {
                    if (c.getPlayer().getMapId() < 926010070 || c.getPlayer().getMapId() > 926010089) {
                        break;
                    }
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId2 = 9700029;
                    spawnPer = 20;
                }
                for (int i = 0; i < spawnPer; ++i) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId2), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId2), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId2), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() >= 921120100 && c.getPlayer().getMapId() < 921120500) {
                    final MapleMonster shammos2 = MapleLifeFactory.getMonster(9300275);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos2);
                        if (c.getPlayer().getEventInstance().getProperty("HP") != null) {
                            shammos2.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                        }
                        else {
                            c.getPlayer().getEventInstance().setProperty("HP", "50000");
                        }
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos2, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos2.switchController(c.getPlayer(), false);
                    break;
                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u706d\u6240\u6709\u7684\u602a\u7269!", 5120033);
                        break;
                    }
                    case 1: {
                        c.getPlayer().getMap().startMapEffect("\u6253\u7834\u76d2\u5b50\uff0c\u6d88\u706d\u602a\u7269!", 5120033);
                        break;
                    }
                    case 2: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u9664!", 5120033);
                        break;
                    }
                    case 3: {
                        c.getPlayer().getMap().startMapEffect("\u6d88\u706d\u6240\u6709\u7684\u602a\u7269!", 5120033);
                        break;
                    }
                    case 4: {
                        c.getPlayer().getMap().startMapEffect("\u627e\u5230\u53e6\u4e00\u8fb9\u7684\u8def!", 5120033);
                        break;
                    }
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().startMapEffect("\u6253\u8d25\u5e7d\u7075\u8239\u8239\u957f!", 5120033);
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: {
                c.getPlayer().getMap().resetFully();
                break;
            }
        }
    }
    
    public static void startScript_User(final MapleClient c, final String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("\u60a8\u5df2\u7ecf\u5efa\u7acb\u4e0e\u5730\u56fe\u811a\u672cScript_User\u3010" + scriptName + "\u3011\u7684\u8fde\u63a5");
        }
        String data = "";
        switch (scriptName) {
            case "103000804": {
                if (c.getPlayer().getParty() == null || c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {}
                break;
            }
        }
        switch (onUserEnter.fromString(scriptName)) {
            case cygnusTest:
            case cygnusJobTutorial: {
                showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913040100));
                break;
            }
            case shammos_Enter: {
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 921120500) {
                    NPCScriptManager.getInstance().dispose(c);
                    NPCScriptManager.getInstance().start(c, 2022006);
                    break;
                }
                break;
            }
            case start_itemTake: {
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    NPCScriptManager.getInstance().start(c, 2013001);
                    break;
                }
                break;
            }
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter:
            case PRaid_FailEnter:
            case PRaid_Revive:
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest:
            case dollCave00:
            case dollCave01:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            case aranTutorAlone:
            case Ghost: {
                c.getPlayer().getMap().startMapEffect("\u8fd9\u4e2a\u5730\u56fe\u611f\u89c9\u9634\u68ee\u68ee\u7684..\u6709\u4e00\u79cd\u83ab\u540d\u7684\u5947\u602a\u611f\u89c9..", 5120025);
                break;
            }
            case evanAlone: {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case dojang_Msg: {
                c.getPlayer().getMap().startMapEffect(MapScriptMethods.mulungEffects[Randomizer.nextInt(MapScriptMethods.mulungEffects.length)], 5120024);
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), false, -1L);
                break;
            }
            case goAdventure: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)UIPacket.MapNameDisplay(10000));
                break;
            }
            case crash_Dragon: {
                showIntro(c, "Effect/Direction4.img/crash/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case getDragonEgg: {
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case meetWithDragon: {
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case PromiseDragon: {
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case evanPromotion: {
                switch (c.getPlayer().getMapId()) {
                    case 900090000: {
                        data = "Effect/Direction4.img/promotion/Scene0" + ((c.getPlayer().getGender() == 0) ? "0" : "1");
                        break;
                    }
                    case 900090001: {
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    }
                    case 900090002: {
                        data = "Effect/Direction4.img/promotion/Scene2" + ((c.getPlayer().getGender() == 0) ? "0" : "1");
                        break;
                    }
                    case 900090003: {
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    }
                    case 900090004: {
                        c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                        c.getSession().write((Object)UIPacket.IntroLock(false));
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                    }
                }
                showIntro(c, data);
                break;
            }
            case TD_MC_title: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                c.getSession().write((Object)UIPacket.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104000000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    c.getSession().write((Object)UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                }
                MapleQuest.MedalQuest m = null;
                for (final MapleQuest.MedalQuest mq : MapleQuest.MedalQuest.values()) {
                    for (final int i : mq.maps) {
                        if (c.getPlayer().getMapId() == i) {
                            m = mq;
                            break;
                        }
                    }
                }
                if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != 2) {
                    if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                        MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                    }
                    if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                        MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int j = 0; j < m.maps.length; ++j) {
                            sb.append("0");
                        }
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                    }
                    final String quest = c.getPlayer().getInfoQuest(m.questid - 2005);
                    final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(m.questid - 1995));
                    if (stat.getCustomData() == null) {
                        stat.setCustomData("0");
                    }
                    int number = Integer.parseInt(stat.getCustomData());
                    final StringBuilder sb2 = new StringBuilder("enter=");
                    boolean changedd = false;
                    for (int k = 0; k < m.maps.length; ++k) {
                        boolean changed = false;
                        try {
                            if (c.getPlayer().getMapId() == m.maps[k] && quest.substring(k + 6, k + 7).equals("0")) {
                                sb2.append("1");
                                changed = true;
                                changedd = true;
                            }
                            if (!changed) {
                                sb2.append(quest.substring(k + 6, k + 7));
                            }
                        }
                        catch (Exception ex) {}
                    }
                    if (changedd) {
                        ++number;
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb2.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                        c.getPlayer().dropMessage(5, "\u8bbf\u95ee " + number + "/" + m.maps.length + " \u4e2a\u5730\u533a.");
                        c.getPlayer().dropMessage(5, "\u79f0\u53f7 " + String.valueOf(m) + " \u5df2\u5b8c\u6210\u4e86");
                        c.getSession().write((Object)MaplePacketCreator.showQuestMsg("\u79f0\u53f7 " + String.valueOf(m) + " \u5df2\u5b8c\u6210\u8bbf\u95ee " + number + "/" + m.maps.length + " \u4e2a\u5730\u533a"));
                    }
                    break;
                }
                break;
            }
            case go10000:
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go1020000:
            case go104000000: {
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
            }
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case go1010400: {
                c.getSession().write((Object)UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                break;
            }
            case goArcher: {
                showIntro(c, "Effect/Direction3.img/archer/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(c, "Effect/Direction3.img/pirate/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(c, "Effect/Direction3.img/rogue/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(c, "Effect/Direction3.img/magician/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(c, "Effect/Direction3.img/swordman/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(c, "Effect/Direction3.img/goLith/Scene" + ((c.getPlayer().getGender() == 0) ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: {
                showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914090010: {
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    }
                    case 914090011: {
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + ((c.getPlayer().getGender() == 0) ? "0" : "1");
                        break;
                    }
                    case 914090012: {
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + ((c.getPlayer().getGender() == 0) ? "0" : "1");
                        break;
                    }
                    case 914090013: {
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    }
                    case 914090100: {
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + ((c.getPlayer().getGender() == 0) ? "0" : "1");
                        break;
                    }
                    case 914090200: {
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                    }
                }
                showIntro(c, data);
                break;
            }
            case iceCave: {
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000014), (byte)(-1), (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000015), (byte)(-1), (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000016), (byte)(-1), (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000017), (byte)(-1), (byte)0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000018), (byte)(-1), (byte)0);
                c.getSession().write((Object)UIPacket.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin", -1));
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case rienArrow: {
                if (c.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                    c.getSession().write((Object)UIPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                    break;
                }
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21101) == 2 && c.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                c.getSession().write((Object)UIPacket.IntroLock(false));
                break;
            }
            case check_count: {
                if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) {
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100);
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                    break;
                }
                break;
            }
            case Massacre_first: {
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                    break;
                }
                break;
            }
            case Massacre_result: {
                c.getSession().write((Object)MaplePacketCreator.showEffect("pvp/lose"));
                break;
            }
        }
    }
    
    private static final int getTiming(final int ids) {
        if (ids <= 5) {
            return 5;
        }
        if (ids >= 7 && ids <= 11) {
            return 6;
        }
        if (ids >= 13 && ids <= 17) {
            return 7;
        }
        if (ids >= 19 && ids <= 23) {
            return 8;
        }
        if (ids >= 25 && ids <= 29) {
            return 9;
        }
        if (ids >= 31 && ids <= 35) {
            return 10;
        }
        if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }
    
    private static final int getDojoStageDec(final int ids) {
        if (ids <= 5) {
            return 0;
        }
        if (ids >= 7 && ids <= 11) {
            return 1;
        }
        if (ids >= 13 && ids <= 17) {
            return 2;
        }
        if (ids >= 19 && ids <= 23) {
            return 3;
        }
        if (ids >= 25 && ids <= 29) {
            return 4;
        }
        if (ids >= 31 && ids <= 35) {
            return 5;
        }
        if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }
    
    private static void showIntro(final MapleClient c, final String data) {
        c.getSession().write((Object)UIPacket.IntroDisableUI(true));
        c.getSession().write((Object)UIPacket.IntroLock(true));
        c.getSession().write((Object)UIPacket.ShowWZEffect(data, -1));
    }
    
    private static void sendDojoClock(final MapleClient c, final int time) {
        c.getSession().write((Object)MaplePacketCreator.getClock(time));
    }
    
    private static void sendDojoStart(final MapleClient c, final int stage) {
        c.getSession().write((Object)MaplePacketCreator.environmentChange("Dojang/start", 4));
        c.getSession().write((Object)MaplePacketCreator.environmentChange("dojang/start/stage", 3));
        c.getSession().write((Object)MaplePacketCreator.environmentChange("dojang/start/number/" + stage, 3));
        c.getSession().write((Object)MaplePacketCreator.trembleEffect(0, 1));
    }
    
    private static void handlePinkBeanStart(final MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();
        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }
    
    private static void reloadWitchTower(final MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);
        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        }
        else if (level <= 20) {
            mob = 9300368;
        }
        else if (level <= 30) {
            mob = 9300369;
        }
        else if (level <= 40) {
            mob = 9300370;
        }
        else if (level <= 50) {
            mob = 9300371;
        }
        else if (level <= 60) {
            mob = 9300372;
        }
        else if (level <= 70) {
            mob = 9300373;
        }
        else if (level <= 80) {
            mob = 9300374;
        }
        else if (level <= 90) {
            mob = 9300375;
        }
        else if (level <= 100) {
            mob = 9300376;
        }
        else {
            mob = 9300377;
        }
        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob), MapScriptMethods.witchTowerPos);
    }
    

}
