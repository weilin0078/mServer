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
	text += "#d合成-- #r★高级冒险家勋章★需要以下物品：\r\n#v4011002##d#z4011002# * 20个\r\n#v1142108##d#z1142108# * 1个\r\n#v4011003##d#z4011003# * 10个\r\n#v4000176##d#z4000176# * 20个\r\n~\r\n"
	text += "\r\n#L1##d我收集了以上物品。确定高级冒险家勋章";//七天
	text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.haveItem(1142108,1)){
		cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if(cm.haveItem(4011002,20) && cm.haveItem(1142108,1) && cm.haveItem(4011003,10) && cm.haveItem(4000176,20)){
				cm.gainItem(4011002, -20);
				cm.gainItem(1142108, -1);
				cm.gainItem(4011003, -10);
				cm.gainItem(4000176, -20);
cm.gainItem(1142109,12,12,12,12,80,80,10,10,10,10,10,0,0,0);
            cm.sendOk("换购成功！");
            cm.dispose();
cm.喇叭(3, "玩家：[" + cm.getPlayer().getName() + "]成功制作高级冒险家勋章，恭喜！！");
			}else{
            cm.sendOk("无法制作，或许你#v4011002#不足20个\r\n#v1142108#不足1个\r\n#v4011003#不足10个\r\n#v4000176#不足20个\r\n");
            cm.dispose();
			}
		}
    }
}




