importPackage(net.sf.odinms.client);
var menu = new Array("武陵","天空之城","百草堂","武陵");
var cost = new Array(1000,1000,1000,1000);
var SDtoJ;
var display = "";
var btwmsg;
var method;

function start() {
	status = -1;
	SDtoJ = cm.getEventManager("SDtoJ");
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1) {
		cm.dispose();
		return;
	} else {
		if(mode == 0 && status == 0) {
			cm.dispose();
			return;
		} else if(mode == 0) {
			cm.sendNext("OK. If you ever change your mind, please let me know.");
			cm.dispose();
			return;
		}
		status++;
		if (status == 0) {
			for(var i=0; i < menu.length; i++) {
				if(cm.getChar().getMapId() == 101000400 && i < 1) {
					display += "\r\n#L"+i+"#移动时间大约是#b2分钟#k，费用是#b("+cost[i]+")#k金币。";
				}
			}
			if(cm.getChar().getMapId() == 101000400) {
				btwmsg = "#b这里到圣地#k";
			}
			if(cm.getChar().getMapId() == 101000400) {
				cm.sendYesNo("怎么样？我的速度很快的吧，如果你想返回，那么我们就立刻出发，不过还是得给我一些辛苦钱，价格是 #b"+cost[3]+" 金币#k。");
			} else {
				cm.sendSimple("嗯……你是说……你想离开魔法密林？前往圣地？路程所需的时间大约是#b2分钟#k，费用用1000金币。\r\n" + display);
			}
		} else if(status == 1) {
			if(selection == 2) {
				cm.sendYesNo("你确定要去？ 那么你要付给我 #b"+cost[2]+" 金币#k。");
			} else {
				if(cm.getMeso() < cost[selection]) {
					cm.sendNext("你确定你有足够的金币？");
					cm.dispose();
				} else {
					if(cm.getChar().getMapId() == 101000400) {
						cm.gainMeso(-cost[3]);
						cm.warp(130000210);
						cm.dispose();
					} else {
						if(SDtoJ.getProperty("isRiding").equals("false")) {
							cm.gainMeso(-cost[selection]);
							SDtoJ.newInstance("SDtoJ");
							SDtoJ.setProperty("myRide",selection);
							SDtoJ.getInstance("SDtoJ").registerPlayer(cm.getChar());
							cm.dispose();
						} else {
							cm.gainMeso(-cost[3]);
							cm.warp(130000210);
							cm.dispose();
						}
					}
				}
			}
		} else if(status == 2) {
			if(cm.getMeso() < cost[2]) {
				cm.sendNext("你确定你有足够的金币？");
				cm.dispose();
			} else {
				cm.gainMeso(-cost[2]);
				cm.warp(130000210);
				cm.dispose();
			}
		}
	}
}
