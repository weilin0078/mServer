/*
WNMS
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status >= 0 && mode == 0) {
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		if (cm.haveItem(4001020)) {
			cm.sendYesNo("你如果有#b#z4001020##k我可以传送你去第#b41层#k。是否想去?");
		} else {
			cm.sendOk("你如果有#b#z4001020##k我可以传送你去第#b41层#k.");
			cm.dispose();
		}
	} else if (status == 1) {
		cm.gainItem(4001020, -1);
		cm.warp(221021700, 3);
		cm.dispose();
		}
	}
}
