/* Dark Lord
	Thief Job Advancement
	Victoria Road : Thieves' Hideout (103000003)

	Custom Quest 100009, 100011
*/

var status = 0;
var job;

importPackage(net.sf.odinms.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 2) {
			cm.sendOk("你知道你没有其他选择了的.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.getJob() == 0) {
				if (cm.getLevel() >= 10)
					cm.sendNext("所以你决定成为一个#r飞侠#k?");
				else {
					cm.sendOk("选择获取还有。。。但是你想好了记得来找我")
					cm.dispose();
				}
			} else {
				if (cm.getLevel() >= 30 && cm.getJob() == 400) {
					if (cm.haveItem(4031012,1)) {
						cm.completeQuest(100011);
						if (cm.getQuestStatus(100011) == 2) {
							status = 20;
							cm.sendNext("我看你做得很好。我会让你在你的漫长道路上迈出下一步.");
						} else {
							cm.sendOk("去看看 #r二转教官#k.")
							cm.dispose();
						}
					} else {
						status = 10;
						cm.sendNext("你所取得的进步是惊人的.");
					}
				} else if (cm.getQuestStatus(100100) == 1) {
					cm.completeQuest(100101);
					if (cm.getQuestStatus(100101) == 2) {
						cm.sendOk("好吧，现在把这个带到 #b二转教官所在地#k.");
					} else {
						cm.sendOk("Hey, " + cm.getChar().getName() + "!我需要英雄证明。");
						cm.startQuest(100101);
					}
					cm.dispose();
				} else {
                                    if(cm.haveItem(4031380,1)){
                                        cm.sendOk("你居然带来了#v4031380#，OK，少侠，我们进入正题吧。现在我的分身已经出现了，和他决斗吧。赢了可以获得#b黑符#k一张\r\n(#v4031380#已经删除，如果这次失败，请再去三转教官获取！)");
                                        cm.spawnMonster(9001003,1);
                                        cm.gainItem(4031380,-1);
                                        cm.dispose();
                                    }else{
					cm.sendOk("你好，再见.");
					cm.dispose();
				}
                            }
			}
		} else if (status == 1) {
			cm.sendNextPrev("这是一个重要的和最后的选择。你将无法回头。");
		} else if (status == 2) {
			cm.sendYesNo("你想成为一个 #r飞侠#k?");
		} else if (status == 3) {
			if (cm.getJob() == 0)
				cm.changeJob(400);
			cm.gainItem(1472000,1);
			cm.gainItem(2070015,500);
			cm.sendOk("因此，它！现在去吧，带着骄傲。");
			cm.dispose();
		} else if (status == 11) {
			cm.sendNextPrev("你可能准备采取下一步 #r刺客#k or #r侠客#k.");
		} else if (status == 12) {
			cm.sendAcceptDecline("但首先我必须测试你的技能。你准备好了吗？");
		} else if (status == 13) {
			if (cm.haveItem(4031011)) {
				cm.sendOk("Please report this bug at = 13");
			} else {
				cm.startQuest(100009);
				cm.sendOk("去找 #b转职教官#k 废弃都市附近。他会告诉你的方式。");
			}
		} else if (status == 21) {
			cm.sendSimple("你想成为什么？#b\r\n#L0##r刺客#l\r\n#L1##r侠客#l#k");
		} else if (status == 22) {
			var jobName;
			if (selection == 0) {
				jobName = "刺客";
				job = 410;
			} else {
				jobName = "侠客";
				job = 420;
			}
			cm.sendYesNo("你确定要成为 #r" + jobName + "#k?");
		} else if (status == 23) {
			cm.changeJob(job);
			cm.gainItem(4031012, -1);//长弓
			cm.sendOk("恭喜你成功转职！");
cm.喇叭(3, "恭喜[" + cm.getPlayer().getName() + "]成功2转，哈哈，我要起飞了！");
			cm.dispose();
		}
	}
}	
