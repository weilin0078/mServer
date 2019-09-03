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
	text += "#d合成-- #r★进击的巨人勋章★需要以下物品：\r\n#v4251202##d#z4251202# * 5个\r\n#v1142178##d#z1142178# * 1个\r\n#v4001084##d#z4001084# * 1个\r\n#v4001085##d#z4001085# * 1个\r\n~\r\n"
	text += "\r\n#L1##d我收集了以上物品。确定进击的巨人勋章";//七天
	text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.haveItem(1142178,1)){
		cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if(cm.haveItem(4251202,5) && cm.haveItem(1142178,1) && cm.haveItem(4001084,1) && cm.haveItem(4001085,1)){
				cm.gainItem(4251202, -5);
				cm.gainItem(1142178, -1);
				cm.gainItem(4001084, -1);
				cm.gainItem(4001085, -1);
cm.gainItem(1142788,25,25,25,25,200,200,15,15,10,10,10,0,0,0);
            cm.sendOk("换购成功！");
            cm.dispose();
cm.喇叭(3, "玩家：[" + cm.getPlayer().getName() + "]成功制作进击的巨人勋章，恭喜！！");
			}else{
            cm.sendOk("无法制作，或许你#v4251202#不足5个\r\n#v1142178#不足1个\r\n#v4001084#不足1个\r\n#v4001085#不足1个\r\n");
            cm.dispose();
			}
		}
    }
}




