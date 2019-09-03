var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.getPlayer().getLevel() < 255) {
            cm.sendYesNo("你确定你要进去吗？？");
        } else {
            cm.sendOk("你想要进去完成职业头的任务啊？？？.");
            cm.dispose();
        }
    } else {
        cm.warp(677000004, 0);
		cm.spawnMobOnMap(9400609,1,424,-435,677000004);
        cm.dispose();
    }
}