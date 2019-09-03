var random = java.lang.Math.floor(Math.random() * 9 + 1);

function action(mode, type, selection) {
		//cm.setEventCount("冰封雪域保卫");
		if(random == 1){
		//cm.gainPlayerEnergy(1);
		//cm.gainItem(2430781,1);
		//cm.worldSpouseMessage(0x20,"[组队-冰封雪域保卫] 玩家 "+ cm.getChar().getName() +" 通关 "+ cm.getEventCount("冰封雪域保卫") +" 次 系统随机给予大量通关奖励，额外获得少量活力值。");
	   	cm.dispose();
		}else if(random == 2){
		///cm.gainItem(2430781,1);
		//cm.gainItem(4310088,5);
		//cm.worldSpouseMessage(0x20,"[组队-冰封雪域保卫] 玩家 "+ cm.getChar().getName() +" 通关 "+ cm.getEventCount("冰封雪域保卫") +" 次 系统随机给予大量通关奖励，额外获得大量RED币。");
	   	cm.dispose();
		}else if(random == 3){
		//cm.gainItem(2430781,1);
		//cm.gainItem(4033356,1);
		//cm.worldSpouseMessage(0x20,"[组队-冰封雪域保卫] 玩家 "+ cm.getChar().getName() +" 通关 "+ cm.getEventCount("冰封雪域保卫") +" 次 系统随机给予大量通关奖励,额外获得正义火种1。");
	   	cm.dispose();
		}else{
	//	cm.gainItem(2430781,1);
		//cm.worldSpouseMessage(0x20,"[组队-冰封雪域保卫] 玩家 "+ cm.getChar().getName() +" 通关 "+ cm.getEventCount("冰封雪域保卫") +" 次 系统随机给予大量通关奖励。");
	  	 cm.dispose();
		}
            if (cm.getQuestStatus(6192) == 1) {
			cm.completeQuest(6192);
			}
                 cm.warp(211000001);
	  	 cm.dispose();
}