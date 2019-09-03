importPackage(Packages.tools);
var setupTask;

function init() {
	scheduleNew();
}

function scheduleNew() {
	setupTask = em.schedule("start", 1000);
}

function cancelSchedule() {
	setupTask.cancel(true);
}

function start() {
	scheduleNew();
	var date = new Date();
	var hours = date.getHours(); //时
	var minute = date.getMinutes(); //分
	var second = date.getSeconds(); //秒
	if (minute == 0 && second == 0) {
		em.getChannelServer().公告("1线自由傅婆全面夺宝开始啦,全天每个整点开启");
	}
}
