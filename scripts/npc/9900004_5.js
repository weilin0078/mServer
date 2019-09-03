function start() {
	status = -1;
	
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.sendOk("我是土豪，我不和你交朋友！");
			cm.dispose();
			return;                    
        }
		if (mode == 1) {
			status++;
		} else {
			status--;
		} 
	if (status == 0) {
		var text = "你有什么稀奇古怪的好玩的材料吗。。。都可以找我回收哦！我开的价格是抵用卷哦！\r\n\r\n";
			if (cm.haveItem(4001126, 2)) {
			text+= "#L1##v4001126##t4001126##b  2#k个 换购#r  1#k抵用卷#l\r\n";
			} else {
			text+= "   #v4001126##t4001126##b  2#k个 换购#r  1#k抵用卷\r\n";
			}
			if (cm.haveItem(4001126, 20)) {
			text+= "#L2##v4001126##t4001126##b 20#k个 换购#r 10#k抵用卷#l\r\n";
			} else {
			text+= "   #v4001126##t4001126##b 20#k个 换购#r 10#k抵用卷\r\n";
			}
			if (cm.haveItem(4001126, 200)) {
			text+= "#L3##v4001126##t4001126##b200#k个 换购#r100#k抵用卷#l\r\n";
			} else {
			text+= "   #v4001126##t4001126##b200#k个 换购#r100#k抵用卷\r\n";
			}
			if (cm.haveItem(4000313, 1)) {
			text+= "#L4##v4000313##t4000313##b  1#k个 换购#r  5#k点卷#l\r\n";
			} else {
			text+= "   #v4000313##t4000313##b  1#k个 换购#r  5#k点卷\r\n";
			}
			if (cm.haveItem(4000313, 10)) {
			text+= "#L5##v4000313##t4000313##b 10#k个 换购#r 50#k点卷#l\r\n";
			} else {
			text+= "   #v4000313##t4000313##b 10#k个 换购#r 50#k点卷\r\n";
			}
			if (cm.haveItem(4000313, 100)) {
			text+= "#L6##v4000313##t4000313##b100#k个 换购#r500#k点卷#l";
			} else {
			text+= "   #v4000313##t4000313##b100#k个 换购#r500#k点卷";
			}
		cm.sendSimple(text);
	} else if (status == 1) { 
		if (selection == 1) { 
			if (cm.haveItem(4001126, 2)) {
				cm.gainItem(4001126,-2);
				cm.gainDY(1);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4001126# × #c4001126#");
				cm.dispose(); 
			}
		} else if (selection == 2) { 
			if (cm.haveItem(4001126, 20)) {
				cm.gainItem(4001126,-20);
				cm.gainDY(10);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4001126# × #c4001126#");
				cm.dispose(); 
			}
		} else if (selection == 3) { 
			if (cm.haveItem(4001126, 200)) {
				cm.gainItem(4001126,-200);
				cm.gainDY(100);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4001126# × #c4001126#");
				cm.dispose(); 
			}
		} else if (selection == 4) { 
			if (cm.haveItem(4000313, 1)) {
				cm.gainItem(4000313,-1);
				cm.gainNX(5);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4000313# × #c4000313#");
				cm.dispose(); 
			}
		} else if (selection == 5) { 
			if (cm.haveItem(4000313, 10)) {
				cm.gainItem(4000313,-10);
				cm.gainNX(50);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4000313# × #c4000313#");
				cm.dispose(); 
			}
		} else if (selection == 6) { 
			if (cm.haveItem(4000313, 100)) {
				cm.gainItem(4000313,-100);
				cm.gainNX(500);
				cm.sendOk("成功兑换");
				cm.dispose();
			} else {
	           	cm.sendOk("枫叶不足~ \r\n您目前只有#v4000313# × #c4000313#");
				cm.dispose(); 
			}
		}
	}
}
}