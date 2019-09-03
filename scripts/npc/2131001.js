
importPackage(net.sf.cherry.server);
importPackage(java.util);
importPackage(net.sf.cherry.client);

var needap = 0; //需要AP的数量

var needsj = 5000;//升级次数需要点卷的数量

var lglp = 500;//升级物理魔法攻击需要的元宝

var mk1 = 4251200;//需要的物品 力量母矿
var mk2 = 4004001;
var mk3 = 4004002;
var mk4 = 4004003;
var item2 = 4001126;//需要的物品 锂矿石母矿
var item1sl = 1;//需要物品1的数量
var item2sl = 5;//需要物品2的数量
var item3sl = 5;//需要物品2的数量
var item4sl = 5;//需要物品2的数量
var item5sl = 100;//需要物品2的数量
var x = 5;//强化的属性值
var slot;
var item;
var qty;
var display;

function start() {
    status = -1;
    action(1, 0, -1);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else if (mode == 0 && type == 0) {
        status--;
        
    } else {
        cm.sendOk("好的,如果你想好了要做什么,我会很乐意的为你服务的..");
        cm.dispose();
        return;
    }
if (status == 0) {
var text = "#fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1#\r\n你好,我的朋友#h #.我的ID:"+cm.getNpc()+"\r\n";
text += "#r五彩水晶指定的野外BOSS都出.请去拍卖查询BOSS传送.#k\r\n";
//text += "#b#L1#★增加装备 物理攻击力 ★#l#l\r\n#L2#★增加装备 魔法攻击力 ★#l\r\n";
//text += "#b#L3#★增加装备 力量属性值 ★#l  \r\n#L4#★增加装备 敏捷属性值 ★#l\r\n";
//text += "#b#L5#★增加装备 智力属性值 ★#l \r\n"; 
//text += "#b#L6#★增加装备 运气属性值 ★#l \r\n\r\n";
text += "#b#L7#★增加装备 可升级次数 ★#l\r\n\r\n";
//text += "#k#n你目前杀了#r" + cm.getChar().getPvpKills() +"#k人  被杀次数:#r" + cm.getChar().getPvpDeaths() +"#k次 点卷:#r" + cm.getNX() + "#k #fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1##fEffect/CharacterEff/1112905/0/1#";
cm.sendSimple(text);

//==========================
} else if (status == 1) {
if (selection == 1) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备物理攻击力#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk1 + "# + "+item2sl+"个#v" + item2 + "# + "+item3sl+"个#v" + item3 + "# + "+item4sl+"个#v" + item4 + "# + #r"+needap+"#k 属性值\r\n\r\n★效果★：装备#r物理攻击力增加:#b"+x+"#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#L0#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  
}
} else if (status == 1) {
if (selection == 2) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备魔法攻击力#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk2 + "# + "+item2sl+"个#v" + item2 + "# + "+item3sl+"个#v" + item3 + "# + "+item4sl+"个#v" + item4 + "# + #r"+needap+"#k 属性值\r\n\r\n★效果★：装备#r魔法攻击力增加:#b"+x+"#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L1#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text); }
} else if (status == 1) {
if (selection == 3) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备力量属性值#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk1 + "# + "+item5sl+"个#v" + item2 + "\r\n\r\n★效果★：装备#r力量属性值增加:#b"+x+"#r.加卷次数:#b-1#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L2#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }
} else if (status == 1) {
if (selection == 4) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备敏捷属性值#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk3 + "# + "+item5sl+"个#v" + item2 + "\r\n\r\n★效果★：装备#r敏捷属性值增加:#b"+x+"#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L3#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }
} else if (status == 1) {
if (selection == 5) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备智力属性值#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk2 + "# + "+item5sl+"个#v" + item2 + "\r\n\r\n★效果★：装备#r智力属性值增加:#b"+x+"#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L4#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }
} else if (status == 1) {
if (selection == 6) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");
cm.dispose();
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备运气属性值#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk4 + "# + "+item5sl+"个#v" + item2 + "\r\n\r\n★效果★：装备#r运气属性值增加:#b"+x+"#r.\r\n\r\n#k★注意★：①该强化需保证可升级次数>0,方可进行.\r\n";
text += "       ②#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L5#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }
} else if (status == 1) {
if (selection == 7) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#\r\n进行#r增加装备可升级次数#k强化需要如下要求：\r\n★消耗★："+item1sl+"个#v" + mk1 + "\r\n\r\n★效果★：装备#r可升级次数增加:#b1#r..\r\n\r\n#k★注意★：";
text += "①#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L6#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }






} else if (status == 1) {
if (selection == 11) {
if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1) ==null) {
cm.sendOk("第一格没有装备,无法使用.");
}else if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getExpiration() !=null) {
cm.sendOk("限时装备不能使用该功能.");cm.dispose();
cm.dispose();
}else if(cm.getChar().getRemainingAp() < "+needap+")  {
cm.sendOk("#b#b武器强化需要#r "+needap+" 点属性值#k#b,您剩余的属性值不足!#k");
cm.dispose();
} else{
var text = "";
text += "您要升级的装备为:#v"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getItemId()+"#,可升级次数为：#r"+cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).getUpgradeSlots()+"次#k\r\n进行#r增加装备可升级次数#k强化需要如下要求：\r\n★消耗★："+lglp+" 个 元宝\r\n\r\n★效果★：戒指#r魔法攻击和物理攻击增加:#b5#r..\r\n\r\n#k★注意★：";
text += "①#b#e装备栏第一个物品为强化物品#n#K.\r\n#k若不遵守上面的注意事项,造成损失由玩家自行负责.\r\n";
text += "#r#L7#★★★★★开始强化装备★★★★★#l";
text += "\r\n\r\n";
text += "";
cm.sendSimple(text);  }






} else if (status == 1) {
if (selection == 8) {
cm.openNpc( 1063004);
} else if (status == 1) {
if (selection == 10) {
cm.openNpc( 2131006);
} else if (status == 1) {
if (selection == 9) {
var a ="#r请注意:此功能清理垃圾为不可挽回清理,所以请在清理前把重要的东西保存在仓库里:\r\n#k谨记把重要东西存进仓库里.别乱点.管理不负责~\r\n#b";
a+= "\r\n#L7#↖(^ω^)↗装备栏"; 
a+= "\r\n#L8#↖(^ω^)↗消耗栏"; 
a+= "\r\n#L9#↖(^ω^)↗设置栏"; 
a+= "\r\n#L10#↖(^ω^)↗其他栏"; 
a+= "\r\n#L11#↖(^ω^)↗特殊栏"; 
} 
cm.sendSimple(a);
} else if (status == 1) {

//请在之前插入
}}}}
}
}
}
}
}
}
//结束status == 1
} else if (status == 2) {
if (selection == 0) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备物理攻击力需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk1,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk1 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item2sl)) {
cm.sendOk("你的背包里没有"+item2sl+"个#v"+  item2 +"#");
cm.dispose();
}
else if (!cm.haveItem(item3,item3sl)) {
cm.sendOk("你的背包里没有"+item3sl+"个#v"+  item3 +"#");
cm.dispose();
}
else if (!cm.haveItem(item4,item4sl)) {
cm.sendOk("你的背包里没有"+item4sl+"个#v"+  item4 +"#");
cm.dispose();

}
else if (cm.haveItem(mk1,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk1,-item1sl);
cm.gainItem(item2,-item2sl);
cm.gainItem(item3,-item3sl);
cm.gainItem(item4,-item4sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setWatk(item.getWatk() + 5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加物理攻击成功.\r\n物理攻击加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}


} else if (status == 2) {
if (selection == 1) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备魔法攻击力需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk2,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk2 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item2sl)) {
cm.sendOk("你的背包里没有"+item2sl+"个#v"+  item2 +"#");
cm.dispose();
}
else if (!cm.haveItem(item3,item3sl)) {
cm.sendOk("你的背包里没有"+item3sl+"个#v"+  item3 +"#");
cm.dispose();
}
else if (!cm.haveItem(item4,item4sl)) {
cm.sendOk("你的背包里没有"+item4sl+"个#v"+  item4 +"#");
cm.dispose();

}else if (cm.haveItem(mk2,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    
                   
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk2,-item1sl);
cm.gainItem(item2,-item2sl);
cm.gainItem(item3,-item3sl);
cm.gainItem(item4,-item4sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setMatk(item.getMatk() + x);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加魔法攻击成功.\r\n魔法攻击加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}

} else if (status == 2) {
if (selection == 2) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备力量属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk1,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk1 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item5sl)) {
cm.sendOk("你的背包里没有"+item5sl+"个#v"+  item2 +"#");
cm.dispose();

}else if (cm.haveItem(mk1,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    
                   
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk1,-item1sl);
cm.gainItem(item2,-item5sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setUpgradeSlots(item.getUpgradeSlots()-1);
item.setStr(item.getStr()+5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加力量成功.r\n砸卷次数-1.\r\n力量加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}

} else if (status == 2) {
if (selection == 3) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备敏捷属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk3,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk3 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item5sl)) {
cm.sendOk("你的背包里没有"+item5sl+"个#v"+  item2 +"#");
cm.dispose();

}else if (cm.haveItem(mk3,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    
                   
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk3,-item1sl);
cm.gainItem(item2,-item5sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setUpgradeSlots(item.getUpgradeSlots()-1);
item.setDex(item.getDex() + 5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加敏捷属性值成功.r\n砸卷次数-1.\r\n敏捷属性值加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}
} else if (status == 2) {
if (selection == 4) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备智力属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk2,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk2 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item5sl)) {
cm.sendOk("你的背包里没有"+item5sl+"个#v"+  item2 +"#");
cm.dispose();

}else if (cm.haveItem(mk2,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    
                   
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk2,-item1sl);
cm.gainItem(item2,-item5sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setUpgradeSlots(item.getUpgradeSlots()-1);
item.setInt(item.getInt() + 5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加智力属性值成功.r\n砸卷次数-1.\r\n智力属性值加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}
} else if (status == 2) {
if (selection == 5) {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备运气属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk4,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk4 +"#");
cm.dispose();
}
else if (!cm.haveItem(item2,item5sl)) {
cm.sendOk("你的背包里没有"+item5sl+"个#v"+  item2 +"#");
cm.dispose();

}else if (cm.haveItem(mk4,item1sl) && cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy().getUpgradeSlots() >=1) {

var statup = new java.util.ArrayList();
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                    statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                    
                   
                    cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
cm.gainItem(mk4,-item1sl);
cm.gainItem(item2,-item5sl);
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
item.setUpgradeSlots(item.getUpgradeSlots()-1);
item.setLuk(item.getLuk() + 5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加运气属性值成功.r\n砸卷次数-1.\r\n运气属性值加"+x+"!");
cm.dispose();
}
else{
cm.sendOk("强化要求不足.");
cm.dispose();
}

} else if (status == 2) {
if (selection == 6) {
if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备运气属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}
else if (!cm.haveItem(mk1,item1sl)) {
cm.sendOk("你的背包里没有"+item1sl+"个#v"+  mk1 +"#可以到点卷商城购买.也可以击杀野外BOSS掉落");
cm.dispose();
}else {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
var statup = new java.util.ArrayList();
cm.gainItem(mk1,-item1sl);
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
item.setUpgradeSlots((item.getUpgradeSlots() + 1));
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加可升级次数成功.\r\n可升级次数+1!");
cm.dispose();
}



} else if (status == 2) {
if (selection == 7) {
if (cm.getzb() < lglp ) {
cm.sendOk("#b增加装备物、魔需要#r"+lglp+"#b个元宝,你剩余的元宝不足!");
cm.dispose();
}
else if (!cm.haveItem(4001430)) {
cm.sendOk("你的背包里没有黑龙之角#v4001430#");
cm.dispose();
}
else if (cm.getChar().getRemainingAp() < needap) {
cm.sendOk("#b增加装备运气属性值需要#r"+needap+"#b点属性值,你剩余的属性值不足!");
cm.dispose();
}else {
var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
var statup = new java.util.ArrayList();
cm.setzb(-lglp);
cm.gainItem(4001430,-1);
cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - needap);
                statup.add (new net.sf.cherry.tools.Pair(net.sf.cherry.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                cm.getChar().getClient().getSession().write(net.sf.cherry.tools.MaplePacketCreator.updatePlayerStats(statup));
item.setWatk(item.getWatk() + 5);
item.setMatk(item.getMatk() + 5);
MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1,1, true);
MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "Edit by Kevin");
cm.sendOk("恭喜你,增加物、魔攻击成功.\r\n各加+5!");
cm.dispose();
}

} else if (status == 2) {
if (selection == 7) {
cm.deleteItem(1);
cm.sendOk("恭喜,已经为你清理完毕!");  
cm.dispose();
} else if (status == 2) {
if (selection == 8) {
cm.deleteItem(2);
cm.sendOk("恭喜,已经为你清理完毕!");  
cm.dispose();
} else if (status == 2) {
if (selection == 9) {
cm.deleteItem(3);
cm.sendOk("恭喜,已经为你清理完毕!");  
cm.dispose();
} else if (status == 2) {
if (selection == 10) {
cm.deleteItem(4);
cm.sendOk("恭喜,已经为你清理完毕!");  
cm.dispose();
} else if (status == 2) {
if (selection == 11) {
cm.deleteItem(5);
cm.sendOk("恭喜,已经为你清理完毕!");  
cm.dispose();
}}}}}}}}}}}}}

//结束status == 2
}
}