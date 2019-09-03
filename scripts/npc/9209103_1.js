var status = -1;
var minLevel = 5; // 35
var maxLevel = 200; // 65

var minPartySize = 1;
var maxPartySize = 6;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.dispose();
			return;
		}
		status--;
	}

	if (status == 0) {
		if (cm.getMapId() == 910510000) {
			cm.warp(910000000);
		}
		if (cm.getHour() != 19) {
			cm.sendOk("没有达到规定的入场时间无法为你开启传送大门！");
			cm.dispose();
			return;
		}

		if (cm.getParty() == null) { // No Party
			cm.sendSimple("你愿意和你的组队完成一个任务吗？在这里，你会发现障碍和问题，你将无法击败它，除非与伟大的团队合作。如果你想试试，请告诉我 #b组队队长#k 跟我说话.\r\n\r\n#r要求: " + minPartySize + " 队员 所有级别 " + minLevel + " ~ " + maxLevel + ".#b#l");
		} else if (!cm.isLeader()) { // Not Party Leader
			cm.sendSimple("如果你想尝试，请告诉 #b组队队长#k 跟我说话.#b#l");
		} else if (cm.getPlayer().getClient().getChannel() != 1) {
			cm.sendOk("对不起只有1线可以");
			cm.dispose();
		} else {
			// Check if all party members are within PQ levels
			var party = cm.getParty().getMembers();
			var mapId = cm.getMapId();
			var next = true;
			var levelValid = 0;
			var inMap = 0;
			var it = party.iterator();
			while (it.hasNext()) {
				var cPlayer = it.next();
				if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
					levelValid += 1;
				} else {
					next = false;
				}
				if (cPlayer.getMapid() == mapId) {
					inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
				}
			}
			if (party.size() > maxPartySize || inMap < minPartySize) {
				next = false;
			}
			if (next) {
				var em = cm.getEventManager("wugongPQ");
				if (em == null) {
					cm.sendSimple("PQ遇到了一个错误。请联系GM，与截图.#b#l");
				} else {
					var prop = em.getProperty("state");
					if (prop.equals("0") || prop == null) {
						for (var i = 4001095; i < 4001099; i++) {
							cm.givePartyItems(i, 0, true);
						}
						for (var i = 4001100; i < 4001101; i++) {
							cm.givePartyItems(i, 0, true);
						}

						em.startInstance(cm.getParty(), cm.getMap());
						//   cm.spawnMobOnMap(9600009, 1,260,248, 912010000);
						//cm.喇叭(3,"[" + cm.getName() + "]进入了限时蜈蚣副本,击杀蜈蚣大王获取丰富奖励！");
						cm.dispose();
						return;
					} else {
						cm.sendSimple("另一方已进入 #r蜈蚣副本#k 请等待他们的任务完成.#b#");
						cm.dispose();
					}
				}
			} else {
				cm.sendSimple("申请进入失败。请遵守以下规定:\r\n\r\n#r要求: " + minPartySize + " 队员, 所有级别 " + minLevel + " ~ " + maxLevel + ".#b#l");
				cm.dispose();
			}
		}
	}
}
