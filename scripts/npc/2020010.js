/* Rene
	Bowman 3rd job advancement
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
        cm.sendOk("Make up your mind and visit me again.");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (! (cm.getJob() == 310 || cm.getJob() == 320)) {
            cm.sendOk("May the Gods be with you!");
            cm.dispose();
            return;
        }
        if ((cm.getJob() == 310 || cm.getJob() == 320) && cm.getPlayerStat("LVL") >= 70) {
            if (cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 70) * 3) {
                if (cm.getPlayer().getAllSkillLevels() > cm.getPlayerStat("LVL") * 3) { //player used too much SP means they have assigned to their skills.. conflict
                    cm.sendOk("你获得了我的力量。。不信吗。。看看你的技能栏吧");
                    cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 70) * 3);
                } else {
                    cm.sendOk("你没有分配完你的技能点，我没办法给你转职.");
                }
                cm.safeDispose();
            } else {
                cm.sendNext("少侠好眼力。。我如此强大的隐身术你都发现了（根本就是站在上面嘛- -#）");
            }
        } else {
            cm.sendOk("恩。。。。。你可以转职。。。七十级都没有。。。四转好不好？");
            cm.safeDispose();
        }
    } else if (status == 1) {
         if (cm.haveItem(4031059,1)) { //判断是否有黑符
            if(cm.haveItem(4031058) == false){
                cm.sendOk("\r\n你居然搜集到了1张黑符！！\r\n那么，现在还有一个严峻的考验，事情不是武力就能解决的，你还需要与丰富的知识来证明你有三次转职的能力。\r\n#e#r请你去神秘岛雪原圣地，寻找到黑圣石。给我带来一个#b智慧项链#k\r\n#d#e（为了保证任务能够正常进行，切记黑符不能丢弃。三次转职后会自动扣除）#n");
                cm.dispose();
            }else{
        if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
            if (cm.getJob() == 310) { // HUNTER
                cm.changeJob(311); // RANGER
                cm.sendOk("恭喜你成功成为三转 射手");
                cm.dispose();
            } else if (cm.getJob() == 320) { // CROSSBOWMAN
                cm.changeJob(321); // SNIPER
                cm.sendOk("恭喜你成功成为 游侠");
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
            cm.sendOk("你现在已经到了足够强大的地步了,但是为了证明你的强大,请你回到#b金银岛#k与最初帮助你成为本职业的教官进行一次#b决斗#k，然后给我物品#v4031059##z4031059#，我将安排你下一步的转职。");
            cm.gainItem(4031380,1);
            cm.dispose();
        }
    }}