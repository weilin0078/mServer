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

	    var textz = "#d亲爱的萌新，真心的欢迎你来到冒险岛这个有爱的大家庭，以下是我对你努力升级的一些回赠，祝你在天天冒险岛能天天开心~~也希望你在这里能收获友谊与自信，让我们回忆曾经玩冒险岛的记忆,天天冒险岛因为有你们，将会变得更好！#k\r\n";

  textz += "#L1#1.#v2340000##z2340000# 需要:#v4031688#(20级头顶灯泡领取) \r\n";
  textz += "#L2#2.属性#v1032035##z1032035# 需要:#v4031689#(40级头顶灯泡领取) \r\n";
   textz += "#L3#3.属性#v1112422##z1112422# 需要:#v4031690#(50级头顶灯泡领取) \r\n";
  textz += "#L4#4.属性#v1072005##z1072005# 需要:#v4031691#(60级头顶灯泡领取) \r\n";
   textz += "#L5#5.属性#v1122007##z1122007# 需要:#v4031692#(70级头顶灯泡领取) \r\n";
   //textz += "#L6#6.#v2043801##z2043801# 需要:#v4031687# \r\n";
   //textz += "#L7#7.#v2044501##z2044501# 需要:#v4031686# \r\n";
   //textz += "#L8#8.#v2044601##z2044601# 需要:#v4031685# \r\n";
   //textz += "#L11#11.#v2044701##z2044701# 需要:#v4002001#8张 \r\n";
  //textz += "#L10#3.#v2049100##z2049100# 需要:#v4251201#1个 \r\n";
  //textz += "#L9#9.#v4031227##z4031227# 需要:#v4251200#1个 \r\n";
  //textz += "#L12#4.#v2340000##z2340000# 需要:#v4251201#1个 \r\n";

  //textz += "#L13#5.#v1052647##z1052647# 需要:#v4251202#5个#v4001129#1个  \r\n";
  //textz += "#L14#6.#v1082540##z1082540# 需要:#v4251202#5个#v4001129#1个  \r\n";
  //textz += "#L15#7.#v1102612##z1102612# 需要:#v4251202#5个#v4001129#1个  \r\n";
  //textz += "#L16#8.#v1003946##z1003946# 需要:#v4251202#5个#v4001129#1个  \r\n";
  //textz += "#L17#9.#v1072853##z1072853# 需要:#v4251202#5个#v4001129#1个 \r\n";




		cm.sendSimple (textz);  

	}else if (status == 1) {

	       if (selection == 1){
                   if (!cm.haveItem(4031688,1)) {
 			cm.sendOk("请带来#v4031688##z4031688#*1");
     
			cm.dispose();
		} else{
			cm.gainItem(4031688,-1);
			cm.gainItem(2340000,5);
			cm.sendOk("#b兑换成功");
      			cm.dispose();
			}

       } else if (selection == 2){
                  if (!cm.haveItem(4031689,1)) {
    cm.sendOk("请带来#v4031689##z4031689#*1");
         cm.dispose();
 
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4031689,-1);
   cm.gainItem(1032035,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 3){
                  if (!cm.haveItem(4031690,1)) {
    cm.sendOk("请带来#v4031690##z4031690#*1");
         cm.dispose();
 
  } else{
   cm.gainItem(4031690,-1);
   cm.gainItem(1112422,3,3,3,3,3,3,3,3,3,3,3,3,3,3);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 4){
                  if (!cm.haveItem(4031691,1)) {
    cm.sendOk("请带来#v4031690##z4031690#*1");
         cm.dispose();

  } else{
   cm.gainItem(4031691,-1);
   cm.gainItem(1072005,4,4,4,4,4,4,4,4,4,4,4,4,4,4);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 5){
                  if (!cm.haveItem(4031692,1)) {
    cm.sendOk("请带来#v4031692##z4031692#*1");
         cm.dispose();
 ;
  } else{
   cm.gainItem(4031692,-1);
   cm.gainItem(1122007,5,5,5,5,5,5,5,5,5,5,5,5,5,5);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 6){
                  if (!cm.haveItem(4002001,8)) {
    cm.sendOk("请带来#v4002001##z4002001#*8");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-8);
   cm.gainItem(2043801,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 7){
                  if (!cm.haveItem(4002001,8)) {
    cm.sendOk("请带来#v4002001##z4002001#*8");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-8);
   cm.gainItem(2044501,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 8){
                  if (!cm.haveItem(4002001,8)) {
    cm.sendOk("请带来#v4002001##z4002001#*8");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-8);
   cm.gainItem(2044601,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 9){
                  if (!cm.haveItem(4251200,1)) {
    cm.sendOk("请带来#v4251200##z4251200#*1");
         cm.dispose();
 
  } else{
   cm.gainItem(4251200,-1);
   cm.gainItem(4031127,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 10){
                  if (!cm.haveItem(4251201,1)) {
    cm.sendOk("请带来#v4251201##z4251201#*1");
         cm.dispose();

  } else{
   cm.gainItem(4251201,-1);
    cm.gainItem(2049100,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 12){
                  if (!cm.haveItem(4251202,1)) {
    cm.sendOk("请带来#v4251202##z4251202#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251201,-1	);
   cm.gainItem(2340000,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 11){//合成条件
                  if (!cm.haveItem(4000313,100)) {
    cm.sendOk("需要一个对应封印的冒险之心\r\n20个#v4002001##z4002001#→1000个#v4001126##z4001126#以及500万冒险币\r\n收集以上材料就可以兑换");
         cm.dispose();
                  } else if (!cm.haveItem(1482029,1)) {
    cm.sendOk("请带来#v1482029##z1482029#*1");
         cm.dispose();
                  } else if (cm.getMeso() < 5000000) {
    cm.sendOk("请带来#v4001126#z4001126#*500");
         cm.dispose();
                  } else if (!cm.haveItem(4001126,500)) {
    cm.sendOk("请带来#v4001126##z4001126#*500");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4002001,-20);
   cm.gainItem(1482029,-1);
   cm.gainMeso(-5000000);
   cm.gainItem(4001126,-500);
   cm.gainItem(1482022,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 13){
                  if (!cm.haveItem(4251202,5)) {
    cm.sendOk("请带来#v4251202##z4251202#*5");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-5);
  cm.gainItem(4001129,-1);
   
   cm.gainItem(1052647,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 14){
                  if (!cm.haveItem(4251202,5)) {
    cm.sendOk("请带来#v4251202##z4251202#*5");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-5);
cm.gainItem(4001129,-1);
   cm.gainItem(1082540,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 15){
                  if (!cm.haveItem(4251202,5)) {
    cm.sendOk("请带来#v4251202##z4251202#*5");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-5);
   cm.gainItem(4001129,-1);
   cm.gainItem(1102612,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}

       } else if (selection == 16){
                  if (!cm.haveItem(4251202,5)) {
    cm.sendOk("请带来#v4251202##z4251202#*5");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-5);
   cm.gainItem(4001129,-1);
   cm.gainItem(1003946,1);
   cm.sendOk("#b兑换成功")
   cm.dispose();
}


       } else if (selection == 17){
         
                  if (!cm.haveItem(4251202,5)){
    cm.sendOk("请带来#v4251202##z4251202#*5#v4001129##z4001129#");
         cm.dispose();
                  } else if (!cm.haveItem(4001129,1)) {
    cm.sendOk("请带来#v4001129##z4001129#*1");
         cm.dispose();
  } else{
   cm.gainItem(4251202,-5);
   cm.gainItem(4001129,-1);
   cm.gainItem(1072853,1);
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


       } else if (selection == 21){
                  if (!cm.haveItem(4001126,200)) {
    cm.sendOk("请带来#v4001126##z4001126#*200");
         cm.dispose();
  } else if (cm.getPlayer().getInventory
(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull(3)){
   cm.sendOk("#b请保证装备栏位至少有3个空格,否则无法兑换.");
   cm.dispose();
  } else{
   cm.gainItem(4001126,-200);
   cm.gainItem(1092110,1);
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
