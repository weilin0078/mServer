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
            text += "#e#d你好！\r\n在我这里可以帮你制作你所想要的五子棋和记忆大考验游戏，以下是我可以为您制作的游戏道具列表.\r\n\r\n"//3
            text += "#L1##e#d#v4080000# 绿水灵/蘑菇  五子棋#l\r\n"//3
            text += "#L2##e#d#v4080001# 绿水灵/三眼章五子棋#l\r\n"//3
            text += "#L3##e#d#v4080002# 绿水灵/猪猪  五子棋#l\r\n"//3
            text += "#L4##e#d#v4080003# 三眼章鱼/蘑菇五子棋#l\r\n"//3
            text += "#L5##e#d#v4080004# 猪猪/三眼章鱼五子棋#l\r\n"//3
            text += "#L6##e#d#v4080005# 猪猪/蘑菇    五子棋#l\r\n"//3
            text += "#L7##e#d#v4080100# 记忆大考验#l\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
		cm.openNpc(1012008, 1);
        } else if (selection == 2) {
		cm.openNpc(1012008, 2);
        } else if (selection == 3) {
		cm.openNpc(1012008, 3);
        } else if (selection == 4) {
		cm.openNpc(1012008, 4);
        } else if (selection == 5) {
		cm.openNpc(1012008, 5);
        } else if (selection == 6) {
		cm.openNpc(1012008, 6);
        } else if (selection == 7) {
		cm.openNpc(1012008, 7);
	}
    }
}


