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
	text += "#d购买-- #r★属性名片戒指★需要以下物品：\r\n#30000点券# * \r\n~\r\n"
	text += "\r\n#L1##d我要购买。属性名片戒指";//七天
	text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(4001230,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if (cm.getPlayer().getCSPoints(1) >= 30000) {
                cm.gainNX(-30000);
               
cm.gainItem(1112122,10,10,10,10,10,10,10,10,8,8,8,0,0,0);
            cm.sendOk("换购成功！");
            cm.dispose();
cm.喇叭(3, "玩家：[" + cm.getPlayer().getName() + "]成功够买属性名片戒指，恭喜！！");
			}else{
            cm.sendOk("无法够买，点卷不足\r\n");
            cm.dispose();
			}
		}
    }
}




