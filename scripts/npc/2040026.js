/*
WNMS
*/
var status = 0;
var map;
var portal;

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
			cm.sendSimple("你可以使用 #b#z4001020##k 激活 #b#z4001020#k. 这些石头要传送到?#b\r\n#L0##b#z4001020##k (71楼)#l\r\n#L1##z4001020# (1楼)#l");
		} else {
			cm.sendOk("你没有指定物品.");
			cm.dispose();
		}
	} else if (status == 1) {
		if (selection == 0) {
			cm.sendYesNo("你可以使用 #b#z4001020##k 激活 #b#z4001020##k. 将你传送到 #b#z4001020##k第七十一楼吗？");
			map = 221022900;
			portal = 3;
		} else {
			cm.sendYesNo("你可以使用 #b#z4001020##k激活 #b#z4001020##k. 将你传送到 #b第四EOS岩#k 在第一层?");
			map = 221020000;
			portal = 4;
		}
	} else if (status == 2) {
		cm.gainItem(4001020, -1);
		cm.warp(map, portal);
		cm.dispose();
		}
	}
}
