//cherry_MS
importPackage(net.sf.cherry.client);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("为了执行秘密任务，我送你进入通道。。。");
		}
		else if (status == 1) {
			
				if(cm.haveItem(4000194,20)){
					cm.gainItem(4000194, -20);
					cm.warp(701010322, 0);
					cm.dispose();
				}else{
				cm.sendNextPrev("你没有20个黑羊毛无法进入！");
				cm.dispose();
				}
		}
		else{
				cm.sendOk("就这样吧。");
				cm.dispose();
		}
	}
}	
