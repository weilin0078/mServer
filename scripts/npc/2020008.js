/** 
 转职  三转 战士
 El Nath: Chief's Residence (211000001)
 
 Custom Quest 100100, 100102
 */

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 1) {
        cm.sendOk("记起来，再看一次.");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (!(cm.getJob() == 110 || cm.getJob() == 120 || cm.getJob() == 130 || cm.getJob() == 2110)) {
            if (cm.getQuestStatus(6192) == 1) {
                if (cm.getParty() != null) {
                    var ddz = cm.getEventManager("ProtectTylus");
                    if (ddz == null) {
                        cm.sendOk("发生未知的错误!");
                    } else {
                        var prop = ddz.getProperty("state");
                        if (prop == null || prop.equals("0")) {
                            ddz.startInstance(cm.getParty(), cm.getMap());
                        } else {
                            cm.sendOk("可能现在转职的人比较多，请再试一次.");
                        }
                    }
                } else {
                    cm.sendOk("请形成一方为了保护太拉斯！");
                }
            } else if (cm.getQuestStatus(6192) == 2) {
                cm.sendOk("你保护了我。谢谢你。我会教你立场的技巧.");
                if (cm.getJob() == 112) {
                    if (cm.getPlayer().getMasterLevel(1121002) <= 0) {
                        cm.teachSkill(1121002, 0, 10);
                    }
                } else if (cm.getJob() == 122) {
                    if (cm.getPlayer().getMasterLevel(1221002) <= 0) {
                        cm.teachSkill(1221002, 0, 10);
                    }
                } else if (cm.getJob() == 132) {
                    if (cm.getPlayer().getMasterLevel(1321002) <= 0) {
                        cm.teachSkill(1321002, 0, 10);
                    }
                }
            } else {
                cm.sendOk("愿神明与你在一起！");
            }
            cm.dispose();
            return;
        }
        if ((cm.getJob() == 110 || cm.getJob() == 120 || cm.getJob() == 130 || cm.getJob() == 2110) && cm.getPlayerStat("LVL") >= 70) {
            if (cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 70) * 3) {
                if (cm.getPlayer().getAllSkillLevels() > cm.getPlayerStat("LVL") * 3) { //player used too much SP means they have assigned to their skills.. conflict
                    cm.sendOk("看来你学会了很多技巧，也足够的强大的空间，你的技能已经获得了我的祝福，接下来的路 离不开你 这个世界！");
                    cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 70) * 3);
                } else {
                    cm.sendOk("让我看看。。。你似乎还存在没有加完的技能点，无法这样继续下去。");
                }
                cm.safeDispose();
            } else {
                cm.sendNext("你确实是一个坚强的人。");
            }
        } else {
            cm.sendOk("请确定你有足够的资格进行转职。需要等级70或者以上 : )");
            cm.safeDispose();
        }
    } else if (status == 1) {
        if (cm.haveItem(4031059,1)) { //判断是否有黑符
            if(cm.haveItem(4031058) == false){
                cm.sendOk("\r\n你居然搜集到了1张黑符！！\r\n那么，现在还有一个严峻的考验，事情不是武力就能解决的，你还需要与丰富的知识来证明你有三次转职的能力。\r\n#e#r请你去神秘岛雪原圣地，寻找到黑圣石。给我带来一个#b智慧项链#k\r\n#d#e（为了保证任务能够正常进行，切记黑符不能丢弃。三次转职后会自动扣除）#n");
                cm.dispose();
            }else{
            if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
                if (cm.getJob() == 110) { // 剑客
                    cm.changeJob(111); // 勇士
                    cm.sendOk("恭喜你转职成为 #b勇士#k.");
                    cm.dispose();
                } else if (cm.getJob() == 120) { // 准骑士
                    cm.changeJob(121); // 骑士
                    cm.sendOk("恭喜你转职成为 #b骑士#k.");
                    cm.dispose();
                } else if (cm.getJob() == 130) { // 枪战士
                    cm.changeJob(131); // 龙骑士
                    cm.sendOk("恭喜你转职成为 #b龙骑士#k");
                    cm.dispose();
                } else if (cm.getJob() == 2110) { // 战神2转
                    cm.changeJob(2111); // 战神
                    if (cm.canHold(1142131, 1)) {
                        cm.forceCompleteQuest(29926);
                        cm.gainItem(1142131, 1); //temp fix
                    }
                    cm.sendOk("恭喜你转职成为 #b战神(3转)#k.");
                    cm.dispose();
                }
            } else {
                cm.sendOk("当你七十级的时候再进行回来。.");
                cm.dispose();
            }
            cm.喇叭(3,"成功进行了第三次转职.....OH~YE~YO~YO~切克闹~");
            cm.gainItem(4031058,-1);
            cm.gainItem(4031059,-1);
            cm.dispose();
            }
        } else {
            cm.sendOk("你现在已经到了足够强大的地步了,但是为了证明你的强大,请你回到#b金银岛#k与最初帮助你成为战士的教官进行一次#b决斗#k，然后给我物品#v4031059##z4031059#，我将安排你下一步的转职。");
            cm.gainItem(4031380,1);
            cm.dispose();
        }
    }}