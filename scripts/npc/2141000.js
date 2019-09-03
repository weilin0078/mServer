/*
 * Time Temple - Kirston
 * Twilight of the Gods
 */

function start() {
    cm.askAcceptDecline("你确定要把这可怕的恶魔召唤出来吗\r\n我真的不知道你在想什么还在犹豫什么\r\n\重要的事说三遍了。召唤请点 是 观光请点 拒绝 \r\n这怪物真的很可怕\r\n好吧我容你想想，想清楚在告诉我吧\r\n");
}

function action(mode, type, selection) {
    if (mode == 1) {
	cm.removeNpc(270050100, 2141000);
	cm.forceStartReactor(270050100, 2709000);
    }
    cm.dispose();

// If accepted, = summon PB + Kriston Disappear + 1 hour timer
// If deny = NoTHING HAPPEN
}
