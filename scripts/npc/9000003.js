function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			//显示物品ID图片用的代码是  #v这里写入ID#
            text += "#e#d#l大家好：想出去的话 可以找我哦.\r\n\r\n"//3
            text += "#L6##e#r城镇传送#l\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
		cm.openNpc(9900004, 70);
        } else if (selection == 6) {
		cm.openNpc(9900004, 1);
        } else if (selection == 5) {
		cm.openNpc(9310059, 4);
        } else if (selection == 2) {//
           // cm.openNpc(9900004, 7);
            cm.openShop(30);
			cm.dispose();
        } else if (selection == 3) {
		cm.openNpc(9310059, 2);
        } else if (selection == 4) {
		cm.openNpc(9310059, 3);
	}
    }
}


