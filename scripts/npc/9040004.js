/*
 * 
 * @wnms
 * @大擂台传送副本npc
 */
var 蓝蜗牛 = "#fMob/0130101.img/move/0#";
function start() {
    status = -1;
    action(1, 0, 0);
}
var 冒险币 = 5000;
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
            cm.sendSimple("#r" + 蓝蜗牛 + 蓝蜗牛 + "请选择你要购买的物品" + 蓝蜗牛 + 蓝蜗牛 + 蓝蜗牛 + "\r\n#b#L2##g透明装备#n#l\r\n#L4##g冲值礼包#n#l\r\n#L1##g土豪武器#n#l\r\n#L5##g快乐夺宝卷#n#l\r\n#L3##g经验专区#n#l\r\n");
        } else if (status == 1) {
            if (selection == 0) {//必成
             cm.openNpc(9040004,1);
            } else if (selection == 1) {//160武器
              cm.openNpc(9040004,2);
            }else if(selection == 2){//戒指
                cm.openNpc(9040004,3);
            }else if(selection == 3){//
                cm.openNpc(9040004,4);
            }else if(selection == 4){
                cm.openNpc(9040004,5);
            }else if(selection == 5){
                cm.openNpc(9040004,8);
            }else if(selection == 6){
                cm.openNpc(9000021,160);
        }
        }
    }
}


