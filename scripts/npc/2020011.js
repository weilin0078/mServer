/* Arec
	Thief 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = -1;
var job;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
            cm.sendOk("请再试一次哦~.");
            cm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        if (! (cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432)) {
            cm.sendOk("什么鬼!");
            cm.safeDispose();
            return;
        }
        if ((cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432) && cm.getPlayerStat("LVL") >= 70) {
            if (cm.getJob() != 432 && cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 70) * 3) {
                if (cm.getPlayer().getAllSkillLevels() > cm.getPlayerStat("LVL") * 3) { //player used too much SP means they have assigned to their skills.. conflict
                    cm.sendOk("恭喜你成功了获得了我的祝福，看看你的技能栏。会发现有变化哦！");
                    cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 70) * 3);
                } else {
                    cm.sendOk("恩。。你有太多的#b技能点#k了。你不是金三胖，我没法帮你.");
                }
                cm.safeDispose();
            } else {
                cm.sendNext("你是来获得我的帮助的吗。。。我看看。。。");
            }
        } else {
            cm.sendOk("你并不能获得我的帮助，因为你的等级没有到70级或者以上。无法帮助你。");
            cm.safeDispose();
        }
    } else if (status == 1) {
         if (cm.haveItem(4031059,1)) { //判断是否有黑符
            if(cm.haveItem(4031058) == false){
                cm.sendOk("\r\n你居然搜集到了1张黑符！！\r\n那么，现在还有一个严峻的考验，事情不是武力就能解决的，你还需要与丰富的知识来证明你有三次转职的能力。\r\n#e#r请你去神秘岛雪原圣地，寻找到黑圣石。给我带来一个#b智慧项链#k\r\n#d#e（为了保证任务能够正常进行，切记黑符不能丢弃。三次转职后会自动扣除）#n");
                cm.dispose();
            }else{
        if (cm.getPlayerStat("LVL") >= 70 && (cm.getJob() == 432 || cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3)) {
            if (cm.getJob() == 410) { // ASSASIN
                cm.changeJob(411); // HERMIT
                cm.sendOk("你已经转职为 #b隐士#k.");
                cm.safeDispose();
            } else if (cm.getJob() == 420) { // BANDIT
                cm.changeJob(421); // CDIT
                cm.sendOk("你已经转职为 #b独行客#.你现在是一个牛逼人物了..记得保持低调..不要大声喧哗...这个职业听说有照顾的...不信吗？你仔细看看，正确来说数来数去都这么多个字了，算是GM对你的特殊照顾了吧。。你看打字手会累的啊！");
                cm.safeDispose();
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