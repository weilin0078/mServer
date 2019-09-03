/* Robeira
	Magician 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = -1;
var job;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 1) {
        cm.sendOk("什么。。.");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (! (cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230)) { // CLERIC
            cm.sendOk("我是不会告诉你这里有十五个字的");
            cm.dispose();
            return;
        }
        if ((cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230) && cm.getPlayerStat("LVL") >= 70) {
            if (cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 70) * 3) {
                if (cm.getPlayer().getAllSkillLevels() > cm.getPlayerStat("LVL") * 3) { //player used too much SP means they have assigned to their skills.. conflict
                    cm.sendOk("你成功获取了我的力量。。看看你的技能栏是否有变化吧！");
                    cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 70) * 3);
                } else {
                    cm.sendOk("恩。。。我检测到你有没有分别完的#b技能点#k所以我现在没办法帮你转职.");
                }
                cm.safeDispose();
            } else {
                cm.sendNext("你。。是来获取我的帮助的吗。。让我一览你的容颜。。");
            }
        } else {
            cm.sendOk("你无法获得我的帮助，因为你的等级没有达到70级或者以上。");
            cm.safeDispose();
        }
    } else if (status == 1) {
        if (cm.haveItem(4031059,1)) { //判断是否有黑符
            if(cm.haveItem(4031058) == false){
                cm.sendOk("\r\n你居然搜集到了1张黑符！！\r\n那么，现在还有一个严峻的考验，事情不是武力就能解决的，你还需要与丰富的知识来证明你有三次转职的能力。\r\n#e#r请你去神秘岛雪原圣地，寻找到黑圣石。给我带来一个#b智慧项链#k\r\n#d#e（为了保证任务能够正常进行，切记黑符不能丢弃。三次转职后会自动扣除）#n");
                cm.dispose();
            }else{
        if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
            if (cm.getJob() == 210) { // FP
                cm.changeJob(211); // FP MAGE
                cm.sendOk("恭喜你成功转职为 #b火毒巫师#k");
                cm.dispose();
            } else if (cm.getJob() == 220) { // IL
                cm.changeJob(221); // IL MAGE
                cm.sendOk("恭喜你成功转职为 冰雷巫师.");
                cm.dispose();
            } else if (cm.getJob() == 230) { // CLERIC
                cm.changeJob(231); // PRIEST
                cm.sendOk("现在开始。。你是一名祭祀了。。");
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