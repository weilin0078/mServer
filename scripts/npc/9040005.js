var status = -1;

function action(mode, type, selection) {
    if (mode != 1) {
        cm.sendOk("Good luck on completing the Guild Quest!");
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        if (cm.isPlayerInstance()) {
            cm.sendSimple("你想做什么？ \r\n #L0#离开公会任务#l");
        } else {
            cm.sendOk("对不起，我不能为你做任何事！");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.sendYesNo("你确定你想做的吗？你将不能回来！");
    } else if (status == 2) {
        if (cm.isPlayerInstance()) {
            cm.getPlayer().getEventInstance().removePlayer(cm.getPlayer());
        }
        cm.dispose();
        return;
    }
}