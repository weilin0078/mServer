
var status = 0;
var choose = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	 if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
		 if (mode == 1)
            status++;
	 	if (status == 0) {
			var text = "#e你好,用点卷购买东西吗!\r\n";
			
			text += "#b#L1#购买XXX#l \r\n\r\n";
	
			
			 cm.sendSimple(text);
			 
		}else if(status == 1){
			switch(selection){
				case 1:
					var text = "#e你好,请您看看下面的商品!\r\n";
			
					text += "#b#L1##v1042312# 价格800游戏币#l\r\n";
					text += "#b#L2##v1042315# 价格800游戏币#l\r\n";
					
			
					cm.sendSimple(text);
					choose = selection;
					break;
				case 2:
					cm.sendSimple("制作当中");
					cm.dispose();
					break;
					
			}
			
		}else if(status == 2){
			if(choose == 1){
				if(cm.getNX(1)<80000){
					cm.sendSimple("您点卷不足8万");
					cm.dispose();
					return;
				}
				switch(selection){
					case 1:
						cm.gainItem(1042312,1);
						cm.gainMeso(-800);
						break;
					case 2:
						cm.gainItem(1042315,1);
						cm.gainMeso(-800);
						break;
					
				}
				cm.sendSimple("购买成功");
				cm.dispose();
			}
			
		}
	}
}