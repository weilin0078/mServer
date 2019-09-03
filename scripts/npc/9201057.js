var sw;

function start() {
    status = -1;
    sw = cm.getEventManager("Subway");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if(mode == 0) {
	cm.sendNext("你有一些經濟的負擔而無法搭地鐵對吧?");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if(sw == null) {
	    cm.sendNext("找不到此腳本請回報GM");
	    cm.dispose();
	} else if(sw.getProperty("entry").equals("true")) {
	    cm.sendYesNo("你要搭地鐵??");
	} else if(sw.getProperty("entry").equals("false") && sw.getProperty("docked").equals("true")) {
	    cm.sendNext("很抱歉本班地鐵準備開走,乘坐時間表可以通過售票展台查看.");
	    cm.dispose();
	} else {
	    cm.sendNext("請耐心等待幾分鐘，正在整理裡面中！");
	    cm.dispose();
	}
    } else if(status == 1 && cm.getMapId() == 103000100) {
	if(!cm.haveItem(4031711)) {
		cm.sendNext("不! 你沒有#b#t4031711##k 所以我不能放你走!");
		cm.dispose();
	} else {
	    cm.gainItem(4031711,-1);
	    cm.warp(600010004);
		cm.dispose();
	}
	} else if(status == 1 && cm.getMapId() == 600010001) {
	if(!cm.haveItem(4031713)) {
		cm.sendNext("不! 你沒有#b#t4031713##k 所以我不能放你走!");
		cm.dispose();
	} else {
	    cm.gainItem(4031713,-1);
	    cm.warp(600010002);
		cm.dispose();
	}
	cm.dispose();
    }
}
