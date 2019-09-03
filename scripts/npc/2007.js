/* 
 * NPC   : Dev Doll
 * Map   : GMMAP
 */
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var status = 0;
var invs = Array(1, 5);
var invv;
var selected;
var slot_1 = Array();
var slot_2 = Array();
var statsSel;
var totalLevel = 0;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        var bbb = false;
	var itemid = 0;
        var selStr = "请选择你要进行增加砸卷次数的装备#n\r\n#b";
        selStr += "#d装备进行觉醒后，会挥发出潜在属性！！每次需要消耗一枚#b勇气之心#d  勇气之心可以通过2个高等五彩水晶兑换噢！另外觉醒是有百分之30的几率会失败的\r\n#e觉醒装备是第一格的!#n\r\n"
        for (var x = 0; x < invs.length; x++) {
            var inv = cm.getInventory(invs[x]);
            for (var i = 0; i <= 1; i++) {
                if (x == 0) {
                    slot_1.push(i);
                } else {
                    slot_2.push(i);
                }
                var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
                itemid = it.getItemId();
		//totalLevel = (it.getUpgradeSlots() + it.getLevel())
                if (cm.isCash(itemid)) {
                    continue;
                }
                bbb = true;
				if(itemid == 1122000){
					cm.sendOk("对不起，该装备无法进行觉醒！");
					cm.dispose();
					return;
				}
                selStr += "#L" + ((invs[x] * 1000) + i) + "#" + 蓝色箭头 + "我要增加:#z" + itemid + "# #v" + itemid + "#的砸卷次数#l\r\n";
            }
        }
        if (!bbb) {
            cm.sendOk("第一个没有装备，请确认您是否第一格没有装备或者为现金装备！");
            cm.dispose();
            return;
        }
         if (itemid >= 1112000 && itemid <= 1113000) {
            cm.sendOk("对不起 戒指无法进行觉醒!");
            cm.dispose();
            return;
        }
        if (itemid >= 1112000 && itemid <= 1113000) {
            cm.sendOk("对不起 戒指无法进行觉醒!");
            cm.dispose();
            return;
        }
        if (itemid >= 1142000 && itemid <= 1142999) {
            cm.sendOk("对不起~勋章也是无法觉醒的！");
            cm.dispose();
            return;
        }
	//if(totalLevel > 20){
	//    cm.sendOk("已达到最高强化等级！");
        //    cm.dispose();
        //    return;
	//}
        cm.sendSimple(selStr + "#k");
    } else if (status == 2) {
        invv = (selection / 1000) | 0;
        selected = (selection % 1000) | 0;
        var inzz = cm.getInventory(invv);
        if (selected >= inzz.getSlotLimit()) {
            cm.sendOk("错误，请再试一次.");
            cm.dispose();
            return;
        }
        if (invv == invs[0]) {
            statsSel = inzz.getItem(slot_1[selected]);
        } else if (invv == invs[1]) {
            statsSel = inzz.getItem(slot_2[selected]);
        }
        if (statsSel == null) {
            cm.sendOk("错误，请再试一次.");
            cm.dispose();
            return;
        }
        if ((inzz.getItem(1) == null)) {
            cm.sendOk("你确定有装备？");
            cm.dispose();
            return;
        }
        // str, dex,luk,Int,hp,mp,watk,matk,wdef,mdef,hb,mz,ty, yd
        Str = statsSel.getStr();//力量
        Dex = statsSel.getDex();//敏捷
        Luk = statsSel.getLuk();//运气
        Int = statsSel.getInt();//智力
        Hp = statsSel.getHp();//生命值
        Mp = statsSel.getMp();//魔法值
        Watk = statsSel.getWatk();//物理攻击
        Matk = statsSel.getMatk();//魔法攻击
        Wdef = statsSel.getWdef();//物理防御
        Mdef = statsSel.getMdef();//魔法防御
        Hb = statsSel.getAvoid();//回避
        mz = statsSel.getAcc();//命中
        ty = statsSel.getJump();//跳跃
        yd = statsSel.getSpeed();//速度
        qh = statsSel.getUpgradeSlots();
        St = "\t\t\t ◆强化次数:" + qh + "#r(+1)#k\r\n\r\n#b每次需要消耗一枚#b勇气之心#d#r\r\n\t\t\t\t是否确认该武器进行觉醒属性?(确认请输入1)";
        cm.sendGetNumber("第一格武器:#v" + statsSel.getItemId() + "##b#z" + statsSel.getItemId() + "# #r#e待升级次数:" + statsSel.getUpgradeSlots() + "#k\r\n#k#n\r\n#k\r\n\t\t\t\t\t#d属性查看\r\n" + St, 0, 1, 1);

    } else if (status == 3) {//开始觉醒
        var jl = Math.floor(Math.random() * 10 + 1);//随机数
        var jl2;
        var jl3;
        var jlname;
        if (jl == 8) {
            //暴击觉醒
            jl3 = 2;
            jl2 = 0.4;
            jlname = "暴击觉醒";
        } else if (jl == 9) {
            //卓越觉醒
            jl3 = 3;
            jl2 = 0.6;
            jlname = "卓越觉醒";
        } else {
            //正常觉醒
            jl3 = 1;
            jl2 = 0.2;
            jlname = "普通觉醒";
        }
        for (var x = 0; x < invs.length; x++) {
            var inv = cm.getInventory(1);
            it1 = inv.getItem(1);
                itemid1 = it1.getItemId();
                Str = it1.getStr();//力量
                Dex = it1.getDex();//敏捷
                Luk = it1.getLuk();//运气
                Int = it1.getInt();//智力
                Hp = it1.getHp();//生命值
                Mp = it1.getMp();//魔法值
                Qh = (it1.getPotential1());
                if (it1.getWatk() != 0) {
                    Watk = it1.getWatk();//物理攻击
                } else {
                    Watk = it1.getWatk();//物理攻击
                }
                if (Matk != 0) {
                    Matk = it1.getMatk();//物理攻击
                } else {
                    Matk = it1.getMatk();//物理攻击
                }
                Wdef = it1.getWdef();//物理防御
                Mdef = it1.getMdef();//魔法防御
                Hb = it1.getAvoid();//回避
                mz = it1.getAcc();//命中
                ty = it1.getJump();//跳跃
                yd = it1.getSpeed();//速度
                qh = it1.getUpgradeSlots()+1;
	        le = it1.getLevel();//升级次数
		fl = statsSel.getFlag();//物品状态
            }
        
        //             //本次强化需要消耗:#v4031875# "+statsSel.getPotential1()*3+",#v4000313#"+statsSel.getPotential1()*2+",冒险币 = "+(statsSel.getPotential1()*123456-7788)*2


        if (cm.haveItem(4001226, 1)) {
			//扣除物品
			cm.gainItem(4001226,-1);
			if(Math.round(Math.random() * 9 + 1) <= 4){
				cm.sendOk("哎呀，手抖了一下觉醒失败。");
				//cm.喇叭2(2,"装备进阶","恭喜 " + cm.getName() + " 进阶失败 +" + it1.getLevel() + cm.getItemName(itemid1) +",是不是很气" );
				cm.dispose();
			}else{
				//判断装备是否一样
                // 扣除装备
                cm.gainItem(itemid1, -1);
                cm.sendOk("恭喜你成功进行觉醒。");
                cm.gainItem(itemid1, Str, Dex, Luk, Int, Hp, Mp, Watk, Matk, Wdef, Mdef, Hb, mz, ty, yd,qh,le,fl);
				//cm.喇叭2(2,"装备进阶","恭喜 " + cm.getName() + " 成功进阶 +" + it1.getLevel() + cm.getItemName(itemid1) );
                cm.dispose();
			}

            
        } else {
            cm.sendOk("不好意思 你需要的物品不足啊！");
            cm.dispose();
        }
    }}