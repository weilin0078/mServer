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
            text += "#e#r累积充值达到300礼包，可获得：#n#b\r\n\r\n#r领取礼包必须要有足够的空间哦，否则被系统吞了东西，管理员不负责哦\r\n"//3
            text += "#L1##r#v4310049#领取累计充值300礼包#l\r\n\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(1112793,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if(cm.haveItem(4310049,1)){
				cm.gainItem(4310049, -1);
                                
				cm.gainItem(1082252,1,1,1,1,1,1,10,10,1,1,1,1,1,1);
				
				cm.gainItem(1142218,10,10,10,10,50,50,10,10,5,5,5,5,10,10);//xunzhang
				cm.gainItem(1112405,7,7,7,7,100,100,7,7,7,7,0,0,0,0);
                                cm.gainItem(2049100, 10);
                                cm.gainItem(5150040, 8);
                                cm.gainItem(2340000, 10);
                                       cm.gainItem(4001129, 8);
                                 cm.gainItem(4001226, 3);
                                    cm.gainItem(4251202, 5);
//cm.gainNx(+20000);

            cm.sendOk("领取成功，任选卷轴必须找管理员获得！");

            cm.dispose();
			}else{
            cm.sendOk("你的充值达不到限度，或者你已经领取过了，请勿重复领取！");
            cm.dispose();
			}
		}
    }
}


