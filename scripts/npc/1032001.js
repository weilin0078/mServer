/* Grendel the Really Old
	Magician Job Advancement
	Victoria Road : Magic Library (101000003)

	Custom Quest 100006, 100008, 100100, 100101
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
				if (cm.getLevel() >= 8)
					cm.sendNext("所以你决定成为一个 #r魔法师#k?");
				else {
					cm.sendOk("你妹的，想好再来找我.")
					cm.dispose();
				}
			} else {
				if (cm.getLevel() >= 30 && cm.getJob() == 200) {
					if(cm.haveItem(4031012,1)){
						cm.completeQuest(100008);
						if (cm.getQuestStatus(100008) == 2) {
							status = 20;
							cm.sendNext("我看你做得很好。我会让你在你的漫长道路上迈出下一步。");
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
						cm.sendOk("Hey, " + cm.getChar().getName() + "! 我需要英雄证明.");
						cm.startQuest(100101);
					}
					cm.dispose();
				} else {
                                    if(cm.haveItem(4031380,1)){
                                        cm.sendOk("你居然带来了#v4031380#，OK，少侠，我们进入正题吧。现在我的分身已经出现了，和他决斗吧。赢了可以获得#b黑符#k一张\r\n(#v4031380#已经删除，如果这次失败，请再去三转教官获取！)");
                                        cm.spawnMonster(9001001,1);
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
			cm.sendYesNo("你想成为一个 #r魔法师#k?");
		} else if (status == 3) {
			if (cm.getJob() == 0)
				cm.changeJob(200);
			cm.gainItem(1372005, 1);
			cm.sendOk("So be it! Now go, and go with pride.");
			cm.dispose();
		} else if (status == 11) {
			cm.sendNextPrev("你可能准备采取下一步 #r火毒法师#k, #r冰雷法师#k or #r牧师#k.");
		} else if (status == 12) {
			cm.sendAcceptDecline("但首先我必须测试你的技能。你准备好了吗？");
		} else if (status == 13) {
			if (cm.haveItem(4031009)) {
				cm.sendOk("Please report this bug at = 13");
			} else {
				cm.startQuest(100006);
				cm.sendOk("去找 #b转职教官#k 魔法密林附近。他会告诉你的方式。");
			}
		} else if (status == 21) {
			cm.sendSimple("你想成为什么？#b\r\n#L0#r火毒法师#l\r\n#L1#r冰雷法师#l\r\n#L2#r牧师#l#k");
		} else if (status == 22) {
			var jobName;
			if (selection == 0) {
				jobName = "火毒法师";
				job = 210;
			} else if (selection == 1) {
				jobName = "冰雷法师";
				job = 220;
			} else {
				jobName = "牧师";
				job = 230;
			}
			cm.sendYesNo("你确定要成为 #r" + jobName + "#k?");
		} else if (status == 23) {
			cm.changeJob(job);
			cm.gainItem(4031012, -1);
			cm.sendOk("恭喜，你转职了！！");
cm.喇叭(3, "恭喜[" + cm.getPlayer().getName() + "]成功2转，哈哈，我要起飞了！！");
					cm.dispose();
		}
	}
}	
