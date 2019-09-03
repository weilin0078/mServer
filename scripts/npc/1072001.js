/* Magician Job Instructor
	Magician 2nd Job Advancement
	Victoria Road : The Forest North of Ellinia (101020000)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0 && cm.getQuestStatus(100007) == 1) {
	status = 3;
    }
    if (status == 0) {
	if (cm.getQuestStatus(100007) == 2) {
	    cm.sendOk("You're truly a hero!");
	    cm.safeDispose();
	} else if (cm.getQuestStatus(100006) >= 1) {
	    cm.completeQuest(100006);
	    if (cm.getQuestStatus(100006) == 2) {
		cm.sendNext("你来啦?");
	    }
	} else {
	    cm.sendOk("我能给你一次机会哦！！");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	cm.sendNextPrev("你想要二转吗？？.")
    } else if (status == 2) {
	cm.askAcceptDecline("我会给你一次机会，你准备好了吗？？");
    } else if (status == 3) {
	cm.startQuest(100007);
	cm.sendOk("你将要收集 #b30 #t4031013##k.祝你好运！！！！")
    } else if (status == 4) {
	//	    cm.gainItem(4031009, -1);
		cm.warp(108000200,0);
	cm.dispose();
    }
}	