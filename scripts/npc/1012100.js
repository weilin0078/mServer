/* Athena Pierce
	Bowman Job Advancement
	Victoria Road : Bowman Instructional School (100000201)

	Custom Quest 100000, 100002
*/

importPackage(net.sf.odinms.client);

var status = 0;
var job;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 2) {
			cm.sendOk("Make up your mind and visit me again.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.getJob() == 0) {
				if (cm.getLevel() >= 10){
					cm.sendNext("所以你决定成为一个 #r弓箭手#k?");
				} else {
					cm.sendOk("火车有点多，我可以告诉你的方式 #r暴漫#k.")
					cm.dispose();
				}
			} else {
				if (cm.getLevel() >= 30 && cm.getJob() == 300) {
					if (cm.haveItem(4031012,1)) {
						cm.completeQuest(100002);
						if (cm.getQuestStatus(100002) == 2) {
							status = 20;
							cm.sendNext("我看你做得很好。我会让你在你的漫长道路上迈出下一步.");
						} else {
							cm.sendOk("去找找 #r弓箭手二转教官#k.")
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
						cm.sendOk("嗨, " + cm.getChar().getName() + "! 我需要 #b英雄勋章#k. 去寻找维度的门.");
						cm.startQuest(100101);
					}
					cm.dispose();
				} else {
                                    if(cm.haveItem(4031380,1)){
                                        cm.sendOk("你居然带来了#v4031380#，OK，少侠，我们进入正题吧。现在我的分身已经出现了，和他决斗吧。赢了可以获得#b黑符#k一张\r\n(#v4031380#已经删除，如果这次失败，请再去三转教官获取！)");
                                        cm.spawnMonster(9001002,1);
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
			cm.sendYesNo("你想成为一个 #r弓箭手#k?");
		} else if (status == 3) {
			if (cm.getJob() == 0)
				cm.changeJob(300);
			cm.gainItem(1452002, 1);//长弓
			cm.gainItem(2060000, 1000);//箭失
			cm.sendOk("加油加油.");
			cm.dispose();
		} else if (status == 11) {
			cm.sendNextPrev("你可能准备采取下一步 #r猎人#k or #r弩弓手#k.")
		} else if (status == 12) {
			cm.sendAcceptDecline("但首先我必须测试你的技能。你准备好了吗？");
		} else if (status == 13) {
			if (cm.haveItem(4031010)) {
				cm.sendOk("Please report this bug at = 13");
			} else {
				cm.startQuest(100000);
				cm.sendOk("去找 #b转职教官#k 射手村附近。他会告诉你的方式。");
			}
		} else if (status == 21) {
			cm.sendSimple("你想成为什么？#b\r\n#L0##r猎人#l\r\n#L1##r弩弓手#l#k");
		} else if (status == 22) {
			var jobName;
			if (selection == 0) {
				jobName = "猎手";
				job = 310;
			} else {
				jobName = "弩手";
				job = 320;
			}
			cm.sendYesNo("你想成为一个#r" + jobName + "#k?");
		} else if (status == 23) {
			cm.changeJob(job);
			cm.gainItem(4031012, -1);//长弓
			cm.sendOk("恭喜你成功转职！");
cm.喇叭(3, "恭喜[" + cm.getPlayer().getName() + "]成功2转，哈哈，我要起飞了！");
			cm.dispose();
		}
	}
}	
