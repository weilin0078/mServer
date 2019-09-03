var 希拉 = "#fMob/9700000.img/move/0#";
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
			//显示物品ID图片用的代码是  #v这里写入ID#
text += ""+希拉+"\r\n"
            text += "#d在周围的怪物，如果你找到了#v1004148#,我就能帮你召唤怪物希拉哦~ (^O^).1个召唤一次.#l\r\n#L1##r召唤愤怒的希拉.\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
			//1
			//2
			//3
			//4
			//5
			/*if(!cm.beibao(1,3)){
            cm.sendOk("装备栏空余不足3个空格！");
            cm.dispose();
			}else if(!cm.beibao(2,1)){
            cm.sendOk("消耗栏空余不足1个空格！");
            cm.dispose();
			}else if(!cm.beibao(3,1)){
            cm.sendOk("设置栏空余不足1个空格！");
            cm.dispose();
			}else if(!cm.beibao(4,1)){
            cm.sendOk("其他栏空余不足1个空格！");
            cm.dispose();
			}else if(!cm.beibao(5,1)){
            cm.sendOk("现金栏空余不足1个空格！");
            cm.dispose();
			}else */if(cm.haveItem(1004148,1)){
				cm.gainItem(1004148, -1);
				cm.spawnMonster(9700000, 1);
				cm.spawnMonster(4230107, 3);
				cm.gainMeso(20000);
            cm.sendOk("召唤成功！");
			cm.worldMessage(6,"玩家：["+cm.getName()+"]召唤了愤怒的希拉.大家小心啊！！！");
            cm.dispose();
			}else{
            cm.sendOk("你要有#v1004148#.我才能帮你召唤希拉哦~！");
            cm.dispose();
			}
		}
    }
}


