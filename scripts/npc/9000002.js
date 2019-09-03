var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1){
		cm.dispose();
	}else{
		if(mode == 0 && status == 0){
			cm.dispose();
			return;
		}
		if(mode == 1){
			status++;
		}else{
			status--;
		}
		if(status == 0){
			//cm.sendYesNo("恭喜你挑战通过,领取你的奖励把!")
			cm.sendYesNo("活动暂未开始!~!")
		}else if(status == 1){
			if(cm.getBossLog('2017-1-4活动') < 1){
				var map = cm.getSavedLocation("EVENT");
				if (map > -1 && map != cm.getMapId()) {
					cm.warp(map, 0);
				} else {
					cm.warp(910000000, 0);
				}
				//cm.gainItem(2049100, 5);//混沌
				//cm.gainItem(2340000, 5);//祝福
				//cm.gainItem(4001226, 1);//勇者心
				//cm.gainNX(1000)//点券
				//cm.setBossLog('2017-1-4活动');
				cm.dispose();
			}else{
				cm.sendOk("你已经参加过一次了,请勿重复领取.");
				cm.dispose();
			}
			
		}else{
			cm.dispose();
		}
	}
}
