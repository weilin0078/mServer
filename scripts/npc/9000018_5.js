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
            text += "#e#r你好！在我这里可以帮你制作你所需要的武器，以下是我可以为您制作的武器列表.\r\n\r\n"//3
            text += "#L1##e#d#v1302213#死灵法师单手剑制作.\r\n"//3
            text += "#L2##e#d#v1332188#死灵法师狂鲨锯制作#l\r\n"//3
            text += "#L3##e#d#v1372132#死灵法师权杖剑制作#l\r\n"//3
            text += "#L4##e#d#v1382159#死灵法师长杖制作#l\r\n"//3
            text += "#L5##e#d#v1402143#死灵法师双手剑制作#l\r\n"//3
            text += "#L6##e#d#v1432133#死灵法师枪制作#l\r\n"//3
            text += "#L7##e#d#v1442171#死灵法师矛制作#l\r\n"//3
            text += "#L8##e#d#v1452163#死灵法师长弓制作#l\r\n"//3
            text += "#L9##e#d#v1462153#死灵法师弩制作#l\r\n"//3
            text += "#L10##e#d#v1472175#死灵法师血色拳套制作#l\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
		cm.openNpc(9000018, 51);
        } else if (selection == 2) {
		cm.openNpc(9000018, 52);
        } else if (selection == 3) {
		cm.openNpc(9000018, 53);
        } else if (selection == 4) {
		cm.openNpc(9000018, 54);
        } else if (selection == 5) {
		cm.openNpc(9000018, 55);
        } else if (selection == 6) {
		cm.openNpc(9000018, 56);
        } else if (selection == 7) {
		cm.openNpc(9000018, 57);
        } else if (selection == 8) {
		cm.openNpc(9000018, 58);
        } else if (selection == 9) {
		cm.openNpc(9000018, 59);
        } else if (selection == 10) {
		cm.openNpc(9000018, 510);
	}
    }
}


