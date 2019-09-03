importPackage(java.lang);
importPackage(Packages.tools);
importPackage(Packages.client);
importPackage(Packages.server);

var status = 0;
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";

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

	    var textz = "精灵吊坠，佩戴即可获得经验加成30%，立刻生效哦！升级必备哦，快快购买吧!\r\n\r\n";

		textz += "" + 蓝色箭头 + "#L1##r#v1122017##z1122017#\t使用权：3小时\t需要点卷：500点\r\n\r\n";

		textz += "" + 蓝色箭头 + "#L2##r#v1122017##z1122017#\t使用权：10小时\t需要点卷：1000点\r\n\r\n";

		textz += "" + 蓝色箭头 + "#L3##r#v1122017##z1122017#\t使用权：1天\t需要点卷：2000点\r\n\r\n";

		textz += "" + 蓝色箭头 + "#L4##r#v1122017##z1122017#\t使用权：7天\t需要点卷：7000点\r\n\r\n";

		//textz += "#r#L4##v1462019##z1462019#   弩手 - 远  弩#l\r\n\r\n";

		//textz += "#r#L5##v1472032##z1472032#   飞侠 - 拳  套#l\r\n\r\n";
		
		//textz += "#r#L6##v1482020##z1482020#   海盗 - 拳  甲#l\r\n\r\n";
		
		//textz += "#r#L7##v1492020##z1492020#   海盗 - 手  枪#l\r\n\r\n";
		
		//textz += "#r#L8##v1432012##z1432012#   战士 - 枪  器#l\r\n\r\n";
		
		//textz += "#r#L9##v1442024##z1442024#   海盗 - 矛  器#l\r\n\r\n";

	

		cm.sendSimple (textz);  

	}else if (status == 1) {

	if (selection == 0) {
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r1亿#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else {
        var ii = MapleItemInformationProvider.getInstance();		                
		var type = ii.getInventoryType(1302208); //获得装备的类形/////////////////////////////////////////////////////////////////////
		var toDrop = ii.randomizeStats(ii.getEquipById(1302208)).copy(); // 生成一个Equip类
		var temptime = (System.currentTimeMillis() + 3 * 60 * 60 * 1000); //时间
		toDrop.setExpiration(temptime); 
		cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
		cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
		cm.getChar().saveToDB(false, false);
		cm.gainItem(5220007,-1);
		cm.sendOk("兑换成功!")
		cm.dispose();
		}
	} else if (selection == 1) {
		if (cm.getMeso(1) < 50000) {
 			cm.sendOk("点卷不足无法换购！");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(1)){
			cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
		} else {
			var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1122017); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1122017)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 3 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			  cm.gainMeso(-500000);
			cm.sendOk("成功购买精灵坠子3小时使用权！")
      			cm.dispose();
			}

	}else if (selection == 2){
		if (cm.getNX(1) < 1000) {
 			cm.sendOk("点卷不足无法换购！");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(1)){
			cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
		} else {
			var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1122017); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1122017)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 10 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainNX(-1000);
			cm.sendOk("成功购买精灵坠子10小时使用权！")
      			cm.dispose();
			}

	}else if (selection == 3){
		if (cm.getNX(1) < 2000) {
 			cm.sendOk("点卷不足无法换购！");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(1)){
			cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
		} else {
			var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1122017); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1122017)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000);  //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainNX(-2000);
			cm.sendOk("成功购买精灵坠子1天使用权！")
      			cm.dispose();
			}

	}else if (selection == 4){
		if (cm.getNX(1) < 7000) {
 			cm.sendOk("点卷不足无法换购！");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(1)){
			cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
			cm.dispose();
		} else {
			var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1122017); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1122017)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);  //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainNX(-7000);
			cm.sendOk("成功购买精灵坠子7天使用权！")
      			cm.dispose();
			}

	}else if (selection == 5){
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r5000W#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧#k");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else{
         var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1472032); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1472032)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainItem(5220007,-1);
			cm.sendOk("兑换成功!")
      			cm.dispose();
			}

	}else if (selection == 6){
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r5000W#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧#k");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else{
         var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1482041); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1482041)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainItem(5220007,-1);
			cm.sendOk("兑换成功!")
      			cm.dispose();
			}
	}else if (selection == 7){
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r5000W#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧#k");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else {
         var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1492042); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1492042)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainItem(5220007,-1);
			cm.sendOk("兑换成功!")
      			cm.dispose();
			}
   }else if (selection == 8){
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r5000W#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧#k");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else {
         var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1432012); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1432012)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainItem(5220007,-1);
			cm.sendOk("兑换成功!")
      			cm.dispose();
	       }
	 }else if (selection == 9){
		if (cm.getMeso() < 0) {
 			cm.sendOk("请带来#r5000W#k金币#k");
      			cm.dispose();
		} else if (!cm.haveItem(5220007,1)) {
 			cm.sendOk("#v5220007##z5220007#您没有该物品 需要在商城购买后在来吧#k");
      			cm.dispose();
		} else if (cm.getInventory(1).isFull(3)){
			cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
			cm.dispose();
		} else {
         var ii = MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1442024); //获得装备的类形/////////////////////////////////////////////////////////////////////
            var toDrop = ii.randomizeStats(ii.getEquipById(1442024)).copy(); // 生成一个Equip类
            var temptime = (System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000); //时间
			toDrop.setExpiration(temptime); 
			cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
			cm.getC().getSession().write(MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包	
			cm.getChar().saveToDB(false, false);
			cm.gainItem(5220007,-1);
			cm.sendOk("兑换成功!")
      			cm.dispose();
}       }
}
}
}
