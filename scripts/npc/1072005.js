/**
	Magician Job Instructor - Magician's Tree Dungeon (108000200)
**/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
if(!cm.canHold(4031012,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
	} else if (cm.haveItem(4031013,30)) {
	    cm.removeAll(4031013);
	    cm.completeQuest(100007);
	    cm.startQuest(100008);
	cm.gainItem(4031012, 1);
	cm.warp(101000000, 0);
	    cm.sendOk("恭喜你收集成功，你可以去找1转教官完成转职了！！！.");
	cm.dispose();
	} else {
	    cm.sendOk("你要收集 #b30 #t4031013##k.祝你好运.")
	    cm.dispose();
	}
    } else if (status == 1) {
	cm.dispose();
    }
}	