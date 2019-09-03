function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMapId() == 677000011) { //warp to another astaroth map.
        cm.warp(677000013, 0);
        cm.dispose();
    } else if (cm.getPlayer().getMapId() == 677000013) { //warp to another astaroth map.
        if (cm.getParty() == null) {
            cm.warpParty(677000010);
            cm.sendOk("被传送出去了。因为你没有队伍.");
        } else {
            var party = cm.getParty().getMembers();
            var mapId = cm.getMapId();
            var next = true;
            var levelValid = 0;
            var inMap = 0;
            var it = party.iterator();
            while (it.hasNext()) {
                var cPlayer = it.next();
                if (cPlayer.getMapid() == mapId) {
                    inMap += 1;
                }
            }
            if (party.size() < 1 || inMap < 1) {
                next = false;
            }
            if (next) {
                if (cm.getMap(677000012).getCharactersSize() > 0) {
                    cm.sendOk("有人试图打败亚斯塔罗斯已经.");
                } else {
                    cm.warpParty(677000012);
                }
            } else {
                cm.sendOk("你需要一个至少三个人在同一张地图上.");
            }
        }
        cm.dispose();
    } else {
        if (cm.getParty() != null) {
            cm.warpParty(677000011);
        } else {
            cm.warp(677000011, 0);
        }
        cm.dispose();
    }
}