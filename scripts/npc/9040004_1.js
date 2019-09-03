..var status = 0;
var choice;
var scrolls = Array(5570000,2044908,2040303,2044815,2040403,2040506,2040508,2040507,2040603,2040709,2040710,2040711,2040806,2040807,2040903,2041024,2041025,2043003,2043103,2043203,2043303,2043703,2043803,2044003,2044103,2044203,2044303,2044403,2044503,2044603,2044703);
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
            cm.sendSimple("欢迎来到#r回归冒险岛卷轴商店#k ,你想买我们商店的物品么？？请选择吧，每个需要2400点券：." + choices);
        } else if (status == 1) {
            cm.sendYesNo("你确定需要购买这个物品么？这将花费你3000点券！！" +"\r\n#v" + scrolls[selection] + "##t" + scrolls[selection] + "#");
           choice = selection;
        } else if (status == 2) {
            if (cm.getPlayer().getCSPoints(1) >= 3000) {
                cm.gainNX(-3000);
                cm.gainItem(scrolls[choice], 1);
                cm.sendOk("谢谢你的光顾，你购买的物品已经放入你的背包！.");
                cm.dispose();
            } else {
                    cm.sendOk("抱歉，你没足够的钱！.");
                    cm.dispose();
            }
        }
    }
}