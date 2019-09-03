/**
-- Odin JavaScript --------------------------------------------------------------------------------
	Shane - Ellinia (101000000)
-- By ---------------------------------------------------------------------------------------------
	Unknown
-- Version Info -----------------------------------------------------------------------------------
	1.1 - Statement fix [Information]
	1.0 - First Version by Unknown
---------------------------------------------------------------------------------------------------
**/

var status = 0;
var check = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.sendOk("需要的時候再來找我吧。");
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
	if (cm.getPlayerStat("LVL") < 25) {
	    cm.sendOk("你的等級好像不夠高。");
	    cm.dispose();
	    check = 1;
	}
	else {
	    cm.sendYesNo("嗨~我是賽恩，我可讓你進入#m101000100# 費用是 #b5000#k 楓幣 你是否想進去？？");
	}
    } else if (status == 1) {
	if (check != 1) {
	    if (cm.getMeso() < 5000) {
		cm.sendOk("抱歉，你好像沒有足夠楓幣，我不能讓你進去！！")
		cm.dispose();
	    }
	    else {
		if (cm.getQuestStatus(2050) == 1) {
		    cm.warp(101000100, 0);
		}
		else if (cm.getQuestStatus(2051) == 1) {
		    cm.warp(101000102, 0);
		}
		else if (cm.getPlayerStat("LVL") >= 25 && cm.getPlayerStat("LVL") < 50) {
		    cm.warp(101000100, 0);
		}
		else if (cm.getPlayerStat("LVL") >= 50) {
		    cm.warp(101000102, 0);
		}
		cm.gainMeso(-5000);
		cm.dispose();
	    }
	}
    }
}	


