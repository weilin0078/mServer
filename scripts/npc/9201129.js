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
            cm.sendYesNo("Would you like to move to Marbas's Strolling Place?");
        } else {
            cm.sendOk("You need to be less than level 255 and need the Marbas's Necklace to enter.");
            cm.dispose();
        }
    } else {
        cm.warp(677000000, 0);
		cm.spawnMobOnMap(9400612,1,404,-596,677000001);
        cm.dispose();
    }
}