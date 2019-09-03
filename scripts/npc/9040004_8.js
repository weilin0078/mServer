importPackage(net.sf.cherry.tools);
importPackage(net.sf.cherry.client);

var status = 0;

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

	    var textz = "#d欢迎来到我爱冒险岛。#k\r\n";
 //textz += "#L1#1.#v4001226##z4001226# 需要:#v4251202#2个 \r\n";
  //textz += "#L1#1.#v5570000##z5570000# 需要:#v4000235#30个 \r\n";
   textz += "#L2#2.#v4110000##z4110000# 1个需要:500点券 \r\n";
   textz += "#L3#3.#v4110000##z4110000# 50个需要:20000点券\r\n";
  //textz += "#L4#4.#v1122134##z1122134# 需要:#v4005001#25个 \r\n";
   //textz += "#L5#5.#v1122135##z1122135# 需要:#v4005002#25个 \r\n";
   //textz += "#L6#6.#v1122136##z1122136# 需要:#v4005003#25个 \r\n";
   //textz += "#L7#7.#v1122137##z1122137# 需要:#v4021004#25个 \r\n";
   //textz += "#L8#8.#v1122029##z1122029# 需要:#v1122133#1个 \r\n";
   //textz += "#L11#9.#v1122030##z1122030# 需要:#v1122134#1个 \r\n";
  //textz += "#L10#10.#v1122031##z1122031# 需要:#v1122135#1个 \r\n";
  //textz += "#L9#11.#v1122032##z1122032# 需要:#v1122136#1个 \r\n";
  //textz += "#L12#12.#v1122033##z1122033# 需要:#v1122137#1个 \r\n";

  //textz += "#L13#9.#v2041145##z2041145# 需要:#v4005001#100个#v4001129#7个  \r\n";
 // textz += "#L14#10.#v2041139##z2041139# 需要:#v4005002#100个#v4001129#7个  \r\n";
  //textz += "#L15#6.#v1102612##z1102612# 需要:#v4251202#7个#v4001129#1个  \r\n";
  //textz += "#L16#7.#v1003946##z1003946# 需要:#v4251202#7个#v4001129#1个  \r\n";
  //textz += "#L17#8.#v1072853##z1072853# 需要:#v4251202#7个#v4001129#1个 \r\n";




		cm.sendSimple (textz);  

	}else if (status == 1) {

	       if (selection == 1){
                   if (!cm.haveItem(4251202,2)) {
 			cm.sendOk("请带来#v4251202##z4251202#*2");
     
			cm.dispose();
		} else{
			cm.gainItem(4251202,-2);
			cm.gainItem(4001226,1);
			cm.sendOk("#b兑换成功");
      			cm.dispose();
			}

       } else if (selection == 2){
      if(!cm.canHold(4001230,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if (cm.getPlayer().getCSPoints(1) >= 500) {
                //cm.gainNX(-500);
   cm.gainNX(-500);
   cm.gainItem(4110000,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 3){
                 if(!cm.canHold(4001230,1)){
			cm.sendOk("请清理你的背包，至少空出2个位置！");
            cm.dispose();
        } else if (cm.getPlayer().getCSPoints(1) >= 20000) {
                //cm.gainNX(-500);
   cm.gainNX(-20000);
   cm.gainItem(4110000,50);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}
       } else if (selection == 4){
                  if (!cm.haveItem(4005001,25)) {
    cm.sendOk("请带来#v4005001##z4005001#*25");
   cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
  } else{
   cm.gainItem(4005001,-25);
   cm.gainItem(1122134,1);
 cm.gainMeso(-20000000);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 5){
                  if (!cm.haveItem(4005002,25)) {
    cm.sendOk("请带来#v4005002##z4005002#*25");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
  } else{
   cm.gainItem(4005002,-25);
   cm.gainItem(1122135,1);
 cm.gainMeso(-20000000);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 6){
                  if (!cm.haveItem(4005003,25)) {
    cm.sendOk("请带来#v4005003##z4005003#*25");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
  } else{
   cm.gainItem(4005003,-25);
   cm.gainItem(1122136,1);
cm.gainMeso(-20000000);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 7){
                  if (!cm.haveItem(4021004,25)) {
    cm.sendOk("请带来#v4021004##z4021004#*25");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
 
  } else{
   cm.gainItem(4021004,-25);
   cm.gainItem(1122137,1);
 cm.gainMeso(-20000000);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 8){
                  if (!cm.haveItem(1122133,1)) {
    cm.sendOk("请带来#v1122133##z1122133#*1");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();

  } else{
  cm.gainMeso(-20000000);
  cm.gainItem(1122133,-1);
   cm.gainItem(1122029,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

         
 } else if (selection == 9){
                  if (!cm.haveItem(1122136,1)) {
    cm.sendOk("请带来#v1122136##z1122136#*1");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();

  } else{
  cm.gainMeso(-20000000);
  cm.gainItem(1122136,-1);
   cm.gainItem(1122032,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}
  
    
 } else if (selection == 10){
                  if (!cm.haveItem(1122135,1)) {
    cm.sendOk("请带来#v1122135##z1122135#*1");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();

  } else{
  cm.gainMeso(-20000000);
  cm.gainItem(1122135,-1);
   cm.gainItem(1122031,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


     } else if (selection == 12){
                  if (!cm.haveItem(1122137,1)) {
    cm.sendOk("请带来#v1122137##z1122137#*1");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();

  } else{
  cm.gainMeso(-20000000);
  cm.gainItem(1122137,-1);
   cm.gainItem(1122033,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

 } else if (selection == 11){
                  if (!cm.haveItem(1122134,1)) {
    cm.sendOk("请带来#v1122134##z1122134#*1");
         cm.dispose();
} else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();

  } else{
  cm.gainMeso(-20000000);
  cm.gainItem(1122134,-1);
   cm.gainItem(1122030,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 13){
                  if (!cm.haveItem(4005001,100)) {
    cm.sendOk("请带来#v4005001##z4005001#*100");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,7)) {
    cm.sendOk("请带来#v4001129##z4001129#*7");
         cm.dispose();
  } else{
   cm.gainItem(4005001,-100);
  cm.gainItem(4001129,-7);
   
   cm.gainItem(2041145,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 14){
                  if (!cm.haveItem(4005002,100)) {
    cm.sendOk("请带来#v4005002##z4005002#*100");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,7)) {
    cm.sendOk("请带来#v4001129##z4001129#*7");
         cm.dispose();
  } else{
   cm.gainItem(4005002,-100);
cm.gainItem(4001129,-7);
   cm.gainItem(2041139,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 15){
                  if (!cm.haveItem(4251202,7)) {
    cm.sendOk("请带来#v4251202##z4251202#*7");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-7);
   cm.gainItem(4001129,-1);
   cm.gainItem(1102612,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 16){
                  if (!cm.haveItem(4251202,7)) {
    cm.sendOk("请带来#v4251202##z4251202#*7");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-7);
   cm.gainItem(4001129,-1);
   cm.gainItem(1003946,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


  
       } else if (selection == 18){
                  if (!cm.haveItem(4002001,30)) {
    cm.sendOk("请带来#v4002001##z4002001#*30");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-30);
   cm.gainItem(1072853,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 19){
                 if (!cm.haveItem(4002001,30)) {
    cm.sendOk("请带来#v4002001##z4002001#*30");
         cm.dispose();
                  } else if (!cm.haveItem(4001126,1000)) {
    cm.sendOk("请带来#v4001126##z4001126#*1000");
         cm.dispose();
		  } else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
                  } else if (!cm.haveItem(1122031,1)) {
    cm.sendOk("请带来#v1122031##z1122031#*1");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-30);
   cm.gainItem(4001126,-1000);
   cm.gainMeso(-20000000);
   cm.gainItem(1122031,-1);
   cm.gainItem(1122036,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 20){
                 if (!cm.haveItem(4002001,30)) {
    cm.sendOk("请带来#v4002001##z4002001#*30");
         cm.dispose();
                  } else if (!cm.haveItem(4001126,1000)) {
    cm.sendOk("请带来#v4001126##z4001126#*1000");
         cm.dispose();
		  } else  if (cm.getMeso() < 20000000) {
 			cm.sendOk("请带来#r20000000W#k金币#k");
      			cm.dispose();
                  } else if (!cm.haveItem(1122032,1)) {
    cm.sendOk("请带来#v1122032##z1122032#*1");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-30);
   cm.gainItem(4001126,-1000);
   cm.gainMeso(-20000000);
   cm.gainItem(1122032,-1);
   cm.gainItem(1122037,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 210){
                  if (!cm.haveItem(4021000,2)) {
    cm.sendOk("请带来#v4021000##z4021000#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021001,2)) {
    cm.sendOk("请带来#v4021001##z4021001#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021002,2)) {
    cm.sendOk("请带来#v4021002##z4021002#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021003,2)) {
    cm.sendOk("请带来#v4021003##z4021003#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021004,2)) {
    cm.sendOk("请带来#v4021004##z4021004#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021005,2)) {
    cm.sendOk("请带来#v4021005##z4021005#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021006,2)) {
    cm.sendOk("请带来#v4021006##z4021006#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021007,2)) {
    cm.sendOk("请带来#v4021007##z4021007#*2");
         cm.dispose();
                  } else if (!cm.haveItem(4021008,2)) {
    cm.sendOk("请带来#v4021008##z4021008#*2");
         cm.dispose();
                  } else if (!cm.haveItem(1132205,1)) {
    cm.sendOk("请带来#v1132205##z1132205#*1");
         cm.dispose();
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4021000,-2);
   cm.gainItem(4021001,-2);
   cm.gainItem(4021002,-2);
   cm.gainItem(4021003,-2);
   cm.gainItem(4021004,-2);
   cm.gainItem(4021005,-2);
   cm.gainItem(4021006,-2);
   cm.gainItem(4021007,-2);
   cm.gainItem(4021008,-2);
   cm.gainItem(1132205,-1);
   cm.gainItem(1132204,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}



       } else if (selection == 22){
                  if (!cm.haveItem(4001126,200)) {
    cm.sendOk("请带来#v4001126##z4001126#*200");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4001126,-200);
   cm.gainItem(1092111,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


}
}
}
}
