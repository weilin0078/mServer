/*
 * 
 * @wnms
 * @大擂台传送副本npc
 */
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
            cm.sendSimple("#r这里是天天冒险岛勋章制作处,请选择你制作的勋章\r\n#d#L0##v1142101#热爱冒险岛勋章制作#l\r\n#L1##g#v1142108#中级冒险家勋章制作#n#l\r\n#b#L2##r#v1142109#高级冒险家勋章制作\r\n#L3##g#v1142178#冒险形象大使勋章制作\n\r\n#b#L4##r#v1142788#进击的巨人勋章勋章制作\r\n#b#L5##r#v1142569#联盟统率者勋章制作\r\n#L6##g#v1142574#妹子认证勋章#n#l\r\n");
        } else if (status == 1) {
            if (selection == 0) {//副本传送
             cm.openNpc(9000021,100);
            } else if (selection == 1) {//副本兑换奖励
              cm.openNpc(9000021,110);
            }else if(selection == 2){
                cm.openNpc(9000021,120);
            }else if(selection == 3){
                cm.openNpc(9000021,130);
            }else if(selection == 4){
                cm.openNpc(9000021,140);
            }else if(selection == 5){
                cm.openNpc(9000021,150);
            }else if(selection == 6){
                cm.openNpc(9000021,160);
        }
        }
    }
}


