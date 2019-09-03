var status = 0;
var choice;
var scrolls = Array(1402251,1412177,1422184,1432214,1482216,1442268,1452252,1462239,1472261,1382259,1492231);
/*
*  呦児修改
*/
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，欢迎下次继续光临！.");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            var choices = "\r\n以下是你可以选择的购买物品: ";
            for (var i = 0; i < scrolls.length; i++) {
                    choices += "\r\n#L" + i + "##v" + scrolls[i] + "##t" + scrolls[i] + "##l";
            }
            cm.sendSimple("欢迎来到#r天天冒险岛160级武器商店#k ,你想买我们商店的物品么？？请选择吧，每个需要100000点券：." + choices);
        } else if (status == 1) {
            cm.sendYesNo("你确定需要购买这个物品么？这将花费你100000点券！！" +"\r\n#v" + scrolls[selection] + "##t" + scrolls[selection] + "#");
           choice = selection;
        } else if (status == 2) {
            if (cm.getPlayer().getCSPoints(1) >= 100000) {
                cm.gainNX(-100000);
                //cm.gainItem(scrolls[choice], 1);
		cm.gainGachaponItem(scrolls[choice], 1,"土豪武器");
                cm.sendOk("谢谢你的光顾，你购买的物品已经放入你的背包！.");
                cm.dispose();
            } else {
                    cm.sendOk("抱歉，你没足够的钱！.");
                    cm.dispose();
            }
        }
    }
}