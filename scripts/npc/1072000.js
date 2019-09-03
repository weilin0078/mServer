/* Warrior Job Instructor
	Warrior 2nd Job Advancement
	Victoria Road : West Rocky Mountain IV (102020300)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	if (cm.getQuestStatus(100004) == 1) {
	    cm.sendOk("你要收集 #b30 #t4031013##k.祝你好运.");
	    status = 3;
	} else {
	    if (cm.getQuestStatus(100004) == 2) {
		cm.sendOk("You're truly a hero!");
		cm.safeDispose();
	    } else if (cm.getQuestStatus(100003) >= 1) {
		cm.completeQuest(100003);
		if (cm.getQuestStatus(100003) == 2) {
		    cm.sendNext("哇，你达成二转转职条件了吗?");
		}
	    } else {
		cm.sendOk("如果你准备好了，就来找我吧");
		cm.safeDispose();
	    }
	}
    } else if (status == 1) {
	cm.sendNextPrev("你想要证明你自己吗？？？")
    } else if (status == 2) {
	cm.askAcceptDecline("我会给你一次机会，你准备好了吗.");
    } else if (status == 3) {
	cm.startQuest(100004);
	cm.sendOk("你要收集 #b30 #t4031013##k.祝你好运.")
    } else if (status == 4) {
	//	    cm.gainItem(4031008, -1);
		cm.warp(108000300,0);
	cm.dispose();
    }
}	