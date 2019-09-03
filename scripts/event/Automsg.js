importPackage(Packages.tools);
var setupTask;

function init() {
	scheduleNew();
}

function scheduleNew() {
	setupTask = em.schedule("start", 1000 * 60 * 3);
}

function cancelSchedule() {
	setupTask.cancel(true);
}

function start() {
	scheduleNew();
var Message = new Array("天天冒险岛欢迎你",
"温馨提醒：天天冒险岛的商城是禁止转物品的，否则背包物品可能失效",
"每周五晚，市场会举行线上活动，唯一QQ群594278543,唯一GMQQ793636201",
"游戏内大家如果遇见什么问题，可以私聊我们的群管理，充值赞助请认准群主",
"天天冒险岛招收给力主播，要求：直播1小时以上且有麦解说,并且全程录像",
"天天冒险岛欢迎你,120级，130级武器，FFN武器分别从不同的BOSS身上出噢",
"温馨提醒：天天冒险岛的商城是禁止转物品的，否则背包物品可能失效");
em.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(0,Message[Math.floor(Math.random() * Message.length)]));
}
