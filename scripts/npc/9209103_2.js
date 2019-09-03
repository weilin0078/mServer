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
            text += "#e#r你好！大王蜈蚣出的赤珠兑换，可兑换珍贵物品.\r\n\r\n"//3
            text += "#L1##e#d#v4031227#x 25 兑换 #v4251201#x1\r\n"//3
            text += "#L2##e#d#v4031227#x 60 兑换 #v2340000#x1#l\r\n"//3
            text += "#L3##e#d#v4031227#x 100 兑换 #v5150040#x1#l\r\n"//3
            text += "#L4##e#d#v4031227#x 150 兑换属性#v1082149#x1#l\r\n"//3
            text += "#L5##e#d#v4031227#x 200 兑换属性#v1102041#x1#l\r\n"//3
            text += "#L6##e#d#v4031227#x 250 兑换 #v1003540#x1#l\r\n"//3
            text += "#L7##e#d#v4031227#x 300 兑换 #v3010070#x1#l\r\n"//3
            //text += "#L8##e#d#v1452057#永恒惊电弓制作#l\r\n"//3
            //text += "#L9##e#d#v1462050#永恒冥雷弩制作#l\r\n"//3
            //text += "#L10##e#d#v1472068#永恒大悲赋制作#l\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
		cm.openNpc(9000018, 641);
        } else if (selection == 2) {
		cm.openNpc(9000018, 642);
        } else if (selection == 3) {
		cm.openNpc(9000018, 643);
        } else if (selection == 4) {
		cm.openNpc(9000018, 644);
        } else if (selection == 5) {
		cm.openNpc(9000018, 645);
        } else if (selection == 6) {
		cm.openNpc(9000018, 646);
        } else if (selection == 7) {
		cm.openNpc(9000018, 647);
        } else if (selection == 8) {
		cm.openNpc(9000018, 68);
        } else if (selection == 9) {
		cm.openNpc(9000018, 69);
        } else if (selection == 10) {
		cm.openNpc(9000018, 610);
	}
    }
}


