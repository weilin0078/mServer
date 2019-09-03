/*
	NPC Name: 		Rini
	Map(s): 		Orbis: Station<To Ellinia> (200000111)
	Description: 		Orbis Ticketing Usher
*/
var status = 0;

function start() {
    status = -1;
    boat = cm.getEventManager("Boats");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if(mode == 0) {
	cm.sendNext("You must have some business to take care of here, right?");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if(boat == null) {
	    cm.sendNext("脚本发生错误，请联系管理员解决。");
	    cm.dispose();
	} else if(boat.getProperty("entry").equals("true")) {
	    cm.sendYesNo("非常好，船上还有足够的位置，请准备好你的船票，我们将进入漫长的旅行，你是不是想登船？");
	} else if(boat.getProperty("entry").equals("false") && boat.getProperty("docked").equals("true")) {
	    cm.sendNext("本次航班已经出去，请等待下一次航班。");
	    cm.dispose();
	} else {
	    cm.sendNext("飞船起飞前5分钟内停止剪票，请注意时间。");
	    cm.dispose();
	}
    } else if(status == 1) {
	cm.warp(200000112, 0);
	cm.dispose();
    }
}