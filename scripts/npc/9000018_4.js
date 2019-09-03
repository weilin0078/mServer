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
            text += "#L1##e#d#v1302312#扎昆泊伊兹尼之剑制作.\r\n"//3
            text += "#L2##e#d#v1332257#扎昆泊伊兹尼短刀制作#l\r\n"//3
            text += "#L3##e#d#v1372204#扎昆泊伊兹尼短杖制作#l\r\n"//3
            text += "#L4##e#d#v1382242#扎昆泊伊兹尼长杖制作#l\r\n"//3
            text += "#L5##e#d#v1402233#扎昆泊伊兹尼双手剑制作#l\r\n"//3
            text += "#L6##e#d#v1442251#扎昆泊伊兹尼矛制作#l\r\n"//3
            text += "#L7##e#d#v1452235#扎昆泊伊兹尼弓制作#l\r\n"//3
            text += "#L8##e#d#v1462222#扎昆泊伊兹尼弩制作#l\r\n"//3
            text += "#L9##e#d#v1472244#扎昆泊伊兹尼斗拳制作#l\r\n"//3
            text += "#L10##e#d#v1432197#扎昆泊伊兹尼枪制作#l\r\n"//3
            text += "#L11##e#d#v1312182#扎昆泊伊兹尼单手斧制作#l\r\n"//3
            text += "#L12##e#d#v1322233#扎昆泊伊兹尼单手锤制作#l\r\n"//3
            text += "#L13##e#d#v1412161#扎昆泊伊兹尼双手战斧制作#l\r\n"//3
            text += "#L14##e#d#v1422168#扎昆泊伊兹尼双手大锤制作#l\r\n"//3
            text += "#L15##e#d#v1482199#扎昆泊伊兹尼拳甲制作#l\r\n"//3
            text += "#L16##e#d#v1492209#扎昆泊伊兹尼短枪制作#l\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
		cm.openNpc(9000018, 41);
        } else if (selection == 2) {
		cm.openNpc(9000018, 42);
        } else if (selection == 3) {
		cm.openNpc(9000018, 43);
        } else if (selection == 4) {
		cm.openNpc(9000018, 44);
        } else if (selection == 5) {
		cm.openNpc(9000018, 45);
        } else if (selection == 6) {
		cm.openNpc(9000018, 46);
        } else if (selection == 7) {
		cm.openNpc(9000018, 47);
        } else if (selection == 8) {
		cm.openNpc(9000018, 48);
        } else if (selection == 9) {
		cm.openNpc(9000018, 49);
        } else if (selection == 10) {
		cm.openNpc(9000018, 410);
        } else if (selection == 11) {
		cm.openNpc(9000018, 411);
        } else if (selection == 12) {
		cm.openNpc(9000018, 412);
        } else if (selection == 13) {
		cm.openNpc(9000018, 413);
        } else if (selection == 14) {
		cm.openNpc(9000018, 414);
        } else if (selection == 15) {
		cm.openNpc(9000018, 415);
        } else if (selection == 16) {
		cm.openNpc(9000018, 416);
	}
    }
}


