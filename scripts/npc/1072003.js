/* Thief Job Instructor
	Thief 2nd Job Advancement
	Victoria Road : Construction Site North of Kerning City (102040000)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0 && cm.getQuestStatus(100010) == 1) {
	status = 3;
    }
    if (status == 0) {
	if (cm.getQuestStatus(6141) == 1) {
	    var ddz = cm.getEventManager("DLPracticeField");
	    if (ddz == null) {
		cm.sendOk("Unknown error occured");
		cm.safeDispose();
	    } else {
		ddz.startInstance(cm.getPlayer());
		cm.dispose();
	    }
	} else if (cm.getQuestStatus(100010) == 2) {
	    cm.sendOk("You're truly a hero!");
	    cm.safeDispose();
	} else if (cm.getQuestStatus(100009) >= 1) {
	    cm.completeQuest(100009);

	    if (cm.getQuestStatus(100009) == 2) {
		cm.sendNext("你想要二转吗？？？");
	    }
	} else {
	    cm.sendOk("我能给你一次机会，你准备好了吗？.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	cm.sendNextPrev("你想要二转吗？？.")
    } else if (status == 2) {
	cm.askAcceptDecline("我能给你一次机会，你准备好了吗？.");
    } else if (status == 3) {
	cm.startQuest(100010);
	//cm.warp(910370000, 0);
		   // cm.gainItem(4031011, -1);
	cm.sendOk("进去以后收集#b30 #t4031013##k. 祝你好运.")
    } else if (status == 4) {
		cm.warp(108000400,0);
	cm.dispose();
    }
}	