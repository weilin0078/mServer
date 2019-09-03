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
            text += "#e#r累积充值达到200礼包，可获得：#n#b\r\n1、\r\n#r领取礼包必须要有足够的空间哦，否则被系统吞了东西，管理员不负责哦\r\n"//3
            text += "#L1##r#v4310016#领取累计充值200礼包#l\r\n\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(1112793,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if(cm.haveItem(4310016,1)){
				cm.gainItem(4310016, -1);
				cm.gainItem(3010757, 1);
                                cm.gainItem(4001129, 5)
				
				cm.gainItem(2340000, 1);//祝福
				cm.gainItem(2049100, 1);//混沌
		cm.gainItem(1142216,8,8,8,8,100,100,8,8,0,0,0,0,0,0);
                cm.gainItem(1112405,8,8,8,8,100,100,5,5,5,5,0,0,0,0);
				
                                cm.gainItem(4251202, 2);
                                cm.gainItem(4001226, 2);

            cm.sendOk("领取成功，任选卷轴必须找管理员获得！");
cm.喇叭(2, "恭喜[" + cm.getPlayer().getName() + "]成功领取累积充值200礼包！！");
            cm.dispose();
			}else{
            cm.sendOk("你的充值达不到限度，或者你已经领取过了，请勿重复领取！");
            cm.dispose();
			}
		}
    }
}


