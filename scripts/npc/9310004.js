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
			cm.sendNext("看来你在执行秘密任务，祝你好运气。。。");
		}
		else if (status == 1) {
			if(cm.haveItem(4031227,1)){
				cm.sendOk("你已经有赤珠了。快去完成任务吧！\r\n但是完成任务以后就不能杀蜈蚣了哦！");
				cm.dispose();
			} else if(cm.haveItem(4031289,1)){
				cm.warp(701010321, 0);
				cm.dispose();
			}else{
				cm.sendOk("你没有资格挑战蜈蚣！\r\n去完成前置任务拿了资格证明书#v4031289#再来找我！");
				cm.dispose();
			}
		}else{
			cm.sendOk("就这样吧······");
			cm.dispose();
		}
	}
}	
