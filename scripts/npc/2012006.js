var mapid = new Array(200000111,200000121,240000000,250000000,200000151,200000161);
var platform = new Array("魔法密林","玩具城","神木村","武陵","阿里安特","圣地");
var flight = new Array("ship","ship","ship","Hak","Geenie","ship","ship");
var menu;
var select;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if(mode == 0) {
	cm.sendOk("请确保你知道你要去哪里，然后到码头。这是一个愉快的旅程，所以你最好不要错过它！");
	cm.dispose();
	return;
    }
    if(mode == 1)
	status++;
    else
	status--;
    if(status == 0) {
	menu = "该站有很多地方可供选择。你需要选择一个将带你到你选择的目的地的人。你会去那个地方？";
	for(var i=0; i < platform.length; i++) {
	    menu += "\r\n#L"+i+"##b船出发的地方 "+platform[i]+"#k#l";
	}
	cm.sendSimple(menu);
    } else if(status == 1) {
	select = selection;
	cm.sendYesNo("Even if you took the wrong passage you can get back here using the portal, so no worries. Will you move to the #bplatform to the "+flight[select]+" that heads to "+platform[select]+"#k?");
    } else if(status == 2) {
	cm.warp(mapid[select], 0);
	cm.dispose();
    }
}