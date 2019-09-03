var minPlayers = 1;

function init() {
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function setup(level, leaderid) {
    em.setProperty("state", "1");
    var mob3 = em.getMonster(8150000);
    em.setProperty("leader", "true");
    var eim = em.newInstance("bianfu" + leaderid);
    em.setProperty("stage", "0");
    var map = eim.setInstanceMap(749020910);
    var map2 = eim.getMapInstance(0);
    map.resetFully(false);
    map.setSpawns(false);
    map.resetSpawnLevel(level);
    eim.startEventTimer(300000); //10 mins
    //map2.spawnMonsterOnGroundBelow(null, new java.awt.Point(22,149));
    return eim;

}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
    player.tryPartyQuest(1200);
}

function playerRevive(eim, player) {}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 749020910) {
        eim.unregisterPlayer(player);

        if (eim.disposeIfPlayerBelow(0, 0)) {
            em.setProperty("state", "0");
            em.setProperty("leader", "true");
        }
    }
}

function playerDisconnected(eim, player) {
    return 0;
}
    //怪物的数据
function monsterValue(eim, mobId) {
    if (mobId == 9300061) {
        eim.broadcastPlayerMsg(5, "蜈蚣大王被干掉了.");
        end(eim);
    }
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
        em.setProperty("leader", "true");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 910000000);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable:
    end(eim);
}
function disbandParty(eim) {
    end(eim);
}
function playerDead(eim, player) {}
function cancelSchedule() {}