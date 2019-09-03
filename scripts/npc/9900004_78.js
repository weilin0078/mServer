var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
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
                text += "精灵吊坠，佩戴即可获得经验加成30%，立刻生效哦！升级必备哦，快快购买吧!\r\n\r\n";
            text += "" + 蓝色箭头 + "#L1##r#v1122017##z1122017#\t使用权：永久\t需要点卷：12000点\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L2##r#v1122017##z1122017#\t使用权：10小时\t需要点卷：600点\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L3##r#v1122017##z1122017#\t使用权：1天\t需要点卷：1200点\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L4##r#v1122017##z1122017#\t使用权：7天\t需要点卷：6000点\r\n\r\n"//3
            text += "" + 蓝色箭头 + "#L5##r#v4310003##z4310003#1个\t需要点卷：800点\r\n\r\n"//3
            text += "" + 蓝色箭头 + "#L6##r#v4310003##z4310003#11个\t需要点卷：8000点\r\n\r\n"//3
            text += "" + 蓝色箭头 + "#L7##r#v2049100##z2049100#10个\t需要点卷：15000点\r\n\r\n"//3
            text += "" + 蓝色箭头 + "#L8##r#v2340000##z2340000#10个\t需要点卷：15000点\r\n\r\n"//3
            cm.sendSimple(text);
            }
        } else if (selection == 1) {
if (cm.getPlayer().getCSPoints(1) >= 1200) {
cm.gainNX(-12000);
cm.gainItem(1122017,1);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买精灵坠子3小时使用权！");
            cm.dispose();
			}else{
            cm.sendOk("道具不足无法换购！");
            cm.dispose();
			}
        } else if (selection == 2) {
if (cm.getPlayer().getCSPoints(1) >= 600) {
cm.gainNX(-600);
cm.gainItem(1122017,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买精灵坠子10小时使用权！");
cm.dispose();
}else{
cm.sendOk("道具不足无法换购！");
cm.dispose();
}
        } else if (selection == 3) {
if (cm.getPlayer().getCSPoints(1) >= 1200) {
cm.gainNX(-1200);
cm.gainItem(1122017,0,0,0,0,0,0,0,0,0,0,0,0,0,0,24);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买精灵坠子1天使用权！");
cm.dispose();
}else{
cm.sendOk("道具不足无法换购！");
cm.dispose();
}
        } else if (selection == 4) {
if (cm.getPlayer().getCSPoints(1) >= 6000) {
cm.gainNX(-6000);
cm.gainItem(1122017,0,0,0,0,0,0,0,0,0,0,0,0,0,0,168);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买精灵坠子7天使用权！");
cm.dispose();
}else{
cm.sendOk("道具不足无法换购！");
cm.dispose();
}
        } else if (selection == 5) {
if (cm.getPlayer().getCSPoints(1) >= 800) {
cm.gainNX(-800);
cm.gainItem(4310003,1);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买(蛋糕馅饼纪念徽章)1个，快去抽奖吧！");
cm.dispose();
}else{
cm.sendOk("点卷不足，无法购买！");
cm.dispose();
}
        } else if (selection == 6) {
if (cm.getPlayer().getCSPoints(1) >= 8000) {
cm.gainNX(-8000);
cm.gainItem(4310003,11);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买(蛋糕馅饼纪念徽章)11个，快去抽奖吧！");
cm.dispose();
}else{
cm.sendOk("点卷不足，无法购买！");
cm.dispose();
}
        } else if (selection == 7) {
if (cm.getPlayer().getCSPoints(1) >= 15000) {
cm.gainNX(-15000);
cm.gainItem(2049100,10);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买混沌卷轴10个，恭喜！");
cm.dispose();
}else{
cm.sendOk("点卷不足，无法购买！");
cm.dispose();
}
        } else if (selection == 8) {
if (cm.getPlayer().getCSPoints(1) >= 15000) {
cm.gainNX(-15000);
cm.gainItem(2340000,10);
cm.worldMessage(6, "[" + cm.getPlayer().getName() + "]成功购买祝福卷轴10个，恭喜！");
cm.dispose();
}else{
cm.sendOk("点卷不足，无法购买！");
cm.dispose();
}
		}
    }
}

