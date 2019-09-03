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
                text += "#k欢迎来到#r我爱冒险岛#k，这里抽奖所得物品哦#v1072747##v1072746##v1072745##v1132175##v1132177##v1082543##v1102481##v1102482##v1082546##v1082547##v1432176##v1492188##v1462202##v1382220##v1452214##v1402204##v1412126##v1452196##v1472205##v1492170##v1432158##v1482159##v1462184##v1322161##v1432137##v1462157##v1372137##v1003540##v3010757##v3015051##v3010041##v3010040##v4001226##v4251202##v2340000##v1402037##v1432049##v1332074##v1382057##v1452057#\r\n";
            //text += "#L1##d#v4001129##z4001129#\t高级武器抽奖#l\r\n\r\n"//
            //text += "#L2##d#v4000313##z4000313#\t玩具,卷轴抽奖#l\r\n\r\n"//
            text += "#L3##d#v4110000##z4110000#\t抽奖#l\r\n\r\n"
             //text += "#L4##d#v4001084##z4001084#\t抽取技能书#l\r\n\r\n"
             //text += "#L5##d#v4310018##z4310018#\t领取累计充值800礼包#l\r\n\r\n"
             //text += "#L6##d#v4310028##z4310028#\t领取累计充值1000礼包#l\r\n\r\n"
            //text += "#L7##d#v4310025##z4310025#\t领取累计充值5000礼包#l\r\n\r\n"
            //text += "#L8##d#v4310010##z4310010#\t领取官方认证老玩家礼包#l\r\n\r\n"
            cm.sendSimple(text);
            }
        } else if (selection == 1) {
		cm.openNpc(9050001, 1);
        } else if (selection == 2) {
		cm.openNpc(9050001, 2);
        } else if (selection == 3) {
		cm.openNpc(9050001, 3);
        } else if (selection == 4) {
		cm.openNpc(2041024, 8);
        } else if (selection == 5) {
		cm.openNpc(9900004, 805);
        } else if (selection == 6) {
		cm.openNpc(9900004, 806);
        } else if (selection == 7) {
		cm.openNpc(9900004, 807);
        } else if (selection == 8) {
		cm.openNpc(9900004, 808);
	}
    }
}


