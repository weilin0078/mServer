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
            text += "#e#r累积充值达到2000礼包，可获得：#n#b\r\n\r\n#r领取礼包必须要有足够的空间哦，否则被系统吞了东西，管理员不负责哦\r\n"//3
            text += "#L1##r#v4310025#领取累计充值2000礼包#l\r\n\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(4310025,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
      }   else if(cm.haveItem(4310025,1)){
				cm.gainItem(4310025, -1);
                               cm.gainItem(4251202, 20);//五彩水晶
				cm.gainItem(2340000, 30);//祝福卷轴
				 cm.gainItem(5211060, 1);//三倍
			cm.gainItem(2049100, 30);//混沌
				cm.gainItem(1122017, 1);//吊坠
       				cm.gainItem(4001226, 15);//勇气之心_
                               cm.gainItem(3010070, 1);//椅子
cm.gainItem(1112405,10,10,10,10,50,50,20,20,0,0,0,0,0,0);//戒指
				cm.gainItem(1082149,8,8,8,8,100,100,8,8,10,10,10,10,0,0);
//腰带
cm.gainItem(1102071,30,30,30,30,1000,1000,20,20,10,10,10,10,0,0);//枫叶披风
				cm.gainItem(1142208,35,35,35,35,100,100,35,35,20,20,20,20,20,20);//勋章
 cm.gainItem(1032035,10,10,10,10,0,0,10,10,5,5,5,5,5,5);//枫叶耳环
 cm.gainItem(1132036,10,10,10,10,100,100,8,8,5,5,5,5,5,5);//腰带
//cm.gainDY(100000);
cm.gainNX(200000);

            cm.sendOk("领取成功，任选卷轴、任选坐骑必须找管理员获得！");
//cm.喇叭(2, "恭喜[" + cm.getPlayer().getName() + "]成功领取累积充值2000礼包！！");
            cm.dispose();
			}else{
            cm.sendOk("你的充值达不到限度，或者你已经领取过了，请勿重复领取！");
            cm.dispose();
			}
		}
    }
}


