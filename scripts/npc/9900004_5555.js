importPackage(net.sf.odinms.client);
var status = 0;

var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";


	function start() {
		status = -1;
		action(1, 0, 0);
		}
	function action(mode, type, selection) {
		if (mode == -1) {
		cm.dispose();
		} else {
		if (status >= 0 && mode == 0) {
		cm.dispose();
		return;
		}
		if (mode == 1)
		status++;
		else
		status--;


	if (status == 0) {

	    var textz = "\r\n#e欢迎来到#r天天冒险岛每日任务专区.每个任务都可以完成.获取相对应的奖励，建议新人每日都做#l\r\n#b注：材料不够会被系统吞掉，吞掉一律不赔，请注意#l\r\n";

		textz += "#d#L0#收集#v4000015##b#z4000015##r 100 #d个可兑换#r#v4251201#中等五彩\r\n";

		textz += "#d#L1#收集#v4000010##b#z4000010##r 100 #d个可兑换#r#v4030002#俄罗斯方块2个\r\n";

		textz += "#d#L2#收集#v4000053##b#z4000053##r 22 #d个可兑换#r#v4001322#法老蓝宝石10\r\n";

		textz += "#d#L3#收集#v4000180##b#z4000180##r 66 #d个可兑#r#v4251201#中等五彩\r\n";

		textz += "#d#L4#收集#v4000273##b#z4000273##r 88 #d个可兑换#r#v2340000#祝福卷轴#l\r\n";

		textz += "#d#L5#收集#v4001084##b#z4001084##r 1 #d个可兑换#r200万经验#l\r\n";

		textz += "#d#L6#收集#v4001085##b#z4001085##r 1 #d个可兑换#r#v4251201#中等五彩#l\r\n";

		textz += "#d#L7#收集#v4001083##b#z4001083##r 1 #d个可兑换#r#v4001226#勇气之心#l\r\n";

		//textz += "#d#L8#收集#v4001085##b#z4001085##r 1 #d个\r\n  可兑换#r1000点#d卷#l\r\n";

		//textz += "#d#L9#收集#v4001084##b#z4001084##r 1 #d个\r\n  可兑换#r1000点#d卷#l\r\n";

		//textz += "#d#L10#收集#v4001083##b#z4001083##r 1 #d个\r\n  可兑换#r2000点#d卷#l\r\n";
                cm.sendSimple (textz);  

			
	}else if (status == 1) {

	if (selection == 0) {
if (cm.haveItem(4000015,100) && cm.getBossLog('PlayQuest1') < 1) {
cm.dispose();

 			cm.sendOk("请带来#r100W#k金币#k");
      			cm.dispose();
		cm.setBossLog('PlayQuest1');
                cm.gainItem(4251201,1);
                //cm.gainExp( + 50000);
		cm.gainItem(4000015,-100);
		cm.sendOk("任务完成,获得以下奖励:\r\n");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();
	    


}else if (selection == 1) {
	if (cm.haveItem(4000010,100) && cm.getBossLog('PlayQuest2') < 1) {
		cm.gainItem(4000010,-100);
		cm.setBossLog('PlayQuest2');
		cm.gainItem(4030002,2);
		cm.sendOk("任务完成,获得以下奖励:\r\n#20万经验!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();
	    

}else if (selection == 2) {
	if (cm.haveItem(4000053,22) && cm.getBossLog('PlayQuest3') < 1) {
		cm.gainItem(4000053,-22);
		cm.setBossLog('PlayQuest3');
		cm.gainItem(4001322,10);
		cm.sendOk("任务完成,获得以下奖励:\r\n#40万经验!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 3){
	if (cm.haveItem(4000180,66) && cm.getBossLog('PlayQuest4') < 1) {
		cm.gainItem(4000180,-66);
		cm.setBossLog('PlayQuest4');
		cm.gainItem(4251201,1);
		cm.sendOk("任务完成,获得以下奖励:\r\n#150万经验!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 4){
	if (cm.haveItem(4000273,88) && cm.getBossLog('PlayQuest5') < 1) {
		cm.gainItem(4000273,-88);
		cm.setBossLog('PlayQuest5');
		cm.gainItem(2340000,1);
		cm.sendOk("任务完成,获得以下奖励:\r\n#祝福卷轴!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 5){
	if (cm.haveItem(4001084,1) && cm.getBossLog('PlayQuest6') < 1) {
		cm.gainItem(4001084,-1);
		cm.setBossLog('PlayQuest6');
		cm.gainExp( + 2000000);
		cm.sendOk("任务完成,获得以下奖励:\r\n#200万经验!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 6){
	if (cm.haveItem(4001085,1) && cm.getBossLog('PlayQuest7') < 1) {
		cm.gainItem(4001085,-1);
		cm.setBossLog('PlayQuest7');
		cm.gainItem(4251201,1);
		cm.sendOk("任务完成,获得以下奖励:\r\n#中等五彩!");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 7){
	if (cm.haveItem(4001083,1) && cm.getBossLog('PlayQuest8') < 1) {
		cm.gainItem(4001083,-1);
		cm.setBossLog('PlayQuest8');
		cm.gainItem(4001226,1);
		cm.sendOk("任务完成,获得以下奖励:\r\n勇气之心");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 8){
	if (cm.haveItem(4001085,1) && cm.getBossLog('PlayQuest9') < 1) {
		cm.gainItem(4001085,-1);
		cm.setBossLog('PlayQuest9');
		cm.gainNX(1000);
		cm.sendOk("任务完成,获得以下奖励:\r\n1000点卷");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 9){
	if (cm.haveItem(4001084,1) && cm.getBossLog('PlayQuest10') < 1) {
		cm.gainItem(4001084,-1);
		cm.setBossLog('PlayQuest10');
		cm.gainNX(1000);
		cm.sendOk("任务完成,获得以下奖励:\r\n1000点卷");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();

}else if (selection == 10){
	if (cm.haveItem(4001083,1) && cm.getBossLog('PlayQuest11') < 1) {
		cm.gainItem(4001083,-1);
		cm.setBossLog('PlayQuest11');
		cm.gainNX(2000);
		cm.sendOk("任务完成,获得以下奖励:\r\n2000点卷");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();



}else if (selection == 11){
	if (cm.haveItem(4001126,1000) && cm.getBossLog('PlayQuest14') < 1) {
		cm.gainItem(4001126,-1000);
		cm.setBossLog('PlayQuest14');
		cm.gainNX(1000);
		cm.sendOk("任务完成,获得以下奖励:\r\n1000点卷");
		cm.dispose();
	} else 
		cm.sendOk("请检查是否有足够的物品。\r\n#r(注:该任务每天只能完成一次)#k");
		cm.dispose();


}
}
}
}
