var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红蜗牛 = "#fMob/0130101.img/move/0#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
           
			text += "\t\t   #e欢迎来到#b天天冒险岛#k!这里是五大职业教官挑战区,每个教官均出本职业卷轴. #n\r\n"
            text += ""+爱心+爱心+爱心+红蜗牛+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+红蜗牛+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
            text += "\t\t\t#L1##e#d" + 蓝色箭头 + "战士教官\t\t#l#L2##d" + 蓝色箭头 + "法师教官#l\r\n\r\n"//3
            text += "\t\t#L3##e#d" + 蓝色箭头 + "弓箭手教官\t\t#l#L4##d" + 蓝色箭头 + "飞侠教官#l\r\n\r\n"//3
            //text += "#L5##d" + 蓝色箭头 + "毒物组队副本#l#L6##d" + 蓝色箭头 + "海盗组队副本#l\r\n\r\n"//3
            //text += "#L11##d" + 蓝色箭头 + "树精经验副本#L7##d" + 蓝色箭头 + "罗密欧与朱丽叶组队副本#l\r\n\r\n"//3
           text += "\t\t\t\t#L9##d" + 蓝色箭头 + "海盗教官#l\r\n\r\n"//3
            //text += "#L10##d" + 蓝色箭头 + "武陵道场挑战赛#l\r\n\r\n"//3
            //text += "#L8##d" + 蓝色箭头 + "遗址公会对抗战(家族副本)#l\r\n\r\n"//3
            text += ""+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+爱心+"\r\n"
            //text += "#L11##dLv120.千年树精王遗迹Ⅱ#l\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L12##dLv130.日本御姐挑战#l\r\n\r\n"//3
            //text += "" + 蓝色箭头 + "#L13##rLv120级以上.绯红副本挑战#l\r\n\r\n"//3
            ////text += "" + 蓝色箭头 + "#L14##rLv140级以上.御姐副本挑战#l\r\n\r\n"//3
            text += ""+星星+星星+星星+红蜗牛+星星+星星+星星+星星+星星+星星+星星+星星+红蜗牛+星星+星星+星星+星星+"\r\n"
            cm.sendSimple(text);
        } else if (selection == 1) { //月妙组队副本
            cm.openNpc(9209000, 1);
        } else if (selection == 2) {  //废弃组队副本
            cm.openNpc(9209000, 2);
        } else if (selection == 3) { //玩具组队副本
           cm.openNpc(9209000, 3);
        } else if (selection == 4) {//天空组队副本
            cm.openNpc(9209000, 4);
        } else if (selection == 5) {//毒物组队副本
            cm.warp(300030100);
            cm.dispose();
        } else if (selection == 6) {//海盗组队副本
            cm.openNpc(2094000, 0);
        } else if (selection == 7) {//罗密欧与朱丽叶组队副本
			cm.warp(261000011);
            cm.dispose();
        } else if (selection == 8) {//遗址公会对抗战
			cm.warp(101030104);
            cm.dispose();
        } else if (selection == 9) {//英语学院副本
            cm.openNpc(9209000, 5);
        } else if (selection == 11) {//千年树精王遗迹
            cm.warp(541020700);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 12) {//人偶师BOSS挑战
            cm.warp(910510001);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 13) {//绯红
            if (cm.getLevel() < 120 ) {  
            cm.sendOk("本地图限制等级120级。您的能力没有资格挑战绯红副本");
                cm.dispose();
              }else{
			cm.warp(803001200);  
				cm.dispose();
                return;
	      } 
        } else if (selection == 14) {//御姐
            if (cm.getLevel() < 140 ) {  
            cm.sendOk("本地图限制等级140级。您的能力没有资格挑战御姐副本");
                cm.dispose();
              }else{
			cm.warp(803000505);  
                cm.dispose();
                return;
	      } 
        } else if (selection == 10) {//.怪物嘉年华
            cm.warp(925020001);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        }
    }
}


