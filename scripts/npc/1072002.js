/* Bowman Job Instructor
	Hunter Job Advancement
	Warning Street : The Road to the Dungeon (106010000)
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0 && cm.getQuestStatus(100001) == 1) {
	status = 3;
    }
    if (status == 0) {
	if (cm.getQuestStatus(100001) == 2) {
	    cm.sendOk("You're truly a hero!");
	    cm.dispose();
	} else if (cm.getQuestStatus(100000) >= 1) {
	    cm.completeQuest(100000);
	    if (cm.getQuestStatus(100000) == 2) {
		cm.sendNext("Oh, isn't this a letter from #bAthena#k?");
	    }
	} else {
	    cm.sendOk("I can show you the way once your ready for it.");
	    cm.dispose();
	}
    } else if (status == 1) {
	cm.sendNextPrev("你想要二转吗？？")
    } else if (status == 2) {
	cm.askAcceptDecline("我会给你一次就会，不知道你准备好了吗.");
    } else if (status == 3) {
	cm.startQuest(100001);
	cm.sendOk("请你收集#b30 #t4031013##k.祝你好运！.")
    } else if (status == 4) {
	// cm.gainItem(4031010, -1);
		cm.warp(108000100,0);
	cm.dispose();
    }
}