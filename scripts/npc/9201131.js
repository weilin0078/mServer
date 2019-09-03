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

			cm.sendYesNo("你确定你要进去吗？");

		} else {

			cm.sendOk("你需要拥有一个#v4032492##z4032482#才能进去.");

			cm.dispose();

		}

} else {

	cm.warp(677000002,0);

		cm.spawnMobOnMap(9400610,1,404,-596,677000003);

cm.dispose();
 
   }

}