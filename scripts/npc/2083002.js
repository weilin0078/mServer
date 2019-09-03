/*
	Crystal of Roots - Leafre Cave of life
 */

var status = -1;


function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
         cm.sendYesNo("你想离开这里到 #m240040700# 吗?");

    } else if (status == 1) {
        cm.warp(240040700, 0);
	if (cm.getPlayerCount(240060200) == 0) {
	     cm.getMap(240060200).resetReactors();

	     cm.getMap(240060200).killAllMonsters(true);

 	     cm.getPlayer().getMap().removeDrops();
	}
	//cm.getPlayer().setbosslog(0);//BOSS重返
        cm.dispose();
    }else{
         cm.dispose();
	}
}