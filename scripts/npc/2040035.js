function action(mode, type, selection) {
    cm.removeAll(4001022);
    cm.removeAll(4001023);
    //cm.addTrait("will", 35);
    //cm.addTrait("charisma", 10);
    cm.getPlayer().endPartyQuest(1202); //might be a bad implentation.. incase they dc or something
   // cm.gainNX(100);
if(cm.haveItem(4031326,1)){//有财神的信件的玩家，可以获得斗神证物
	cm.gainItem(4310015,+1);}
	cm.gainItem(4001322,+12);//白雪人法老的蓝宝石
	cm.gainMeso(+30000);//读取变量
	cm.gainExp(+80000);
	cm.gainItem(4170005,+1);
	cm.gainItem(4251200, 1);
    cm.warp(922010000);
    cm.喇叭2(1,"玩具副本","成功通关[组队任务 - 玩具城组队]副本,获得大量奖励.");
    cm.dispose();
}