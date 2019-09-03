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
                
   cm.sendOk("感谢使用.");
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
	for(i = 0; i < 10; i++){
		text += "";
	}				
	text += "#d合成-- #r★联盟统率者勋章★需要以下物品：\r\n#v4251202##d#z4251202# * 10个\r\n#v1142788##d#z1142788# * 1个\r\n#v4001083##d#z4001083# * 1个\r\n#v4030002##d#z4030002# * 30个\r\n~\r\n"
	text += "\r\n#L1##d我收集了以上物品。确定联盟统率者勋章";//七天
	text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.haveItem(1142788,1)){
		cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if(cm.haveItem(4251202,10) && cm.haveItem(1142788,1) && cm.haveItem(4001083,1) && cm.haveItem(4030002,30)){
				cm.gainItem(4251202, -10);
				cm.gainItem(1142788, -1);
				cm.gainItem(4001083, -1);
				cm.gainItem(4030002, -30);
cm.gainItem(1142569,30,30,30,30,500,500,20,20,30,30,30,30,30,30);
            cm.sendOk("换购成功！");
            cm.dispose();
cm.喇叭(3, "玩家：[" + cm.getPlayer().getName() + "]成功制作联盟统率者勋章，恭喜！！");
			}else{
            cm.sendOk("无法制作，或许你#v4251202#不足10个\r\n#v1142788#不足1个\r\n#v4001083#不足1个\r\n#v4030002#不足30个\r\n");
            cm.dispose();
			}
		}
    }
}




