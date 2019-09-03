/*
    WNMS - T079
*/

var status = -1;
var rank = "D";
var exp = 0;

function start() {
    if (cm.getCarnivalParty() != null) {
        status = 99;
    }
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (status == 0) {
        selStr = cm.getChar().getName() + "你好,当前时间是#b" + cm.getHour() + "时:" + cm.getMin() + "分:" + cm.getSec() + "秒\r\n#k点券:#r" + cm.getNX(1) + ". \r\n#k当时间达到#r20:00-20:05#k之间,请拿起你的鼠标疯狂点击吧~\r\n";
        selStr += "你可以选择要点卷还是经验。点卷每次可获得1-5点卷。经验根据当前等级来的！\r\n";
        selStr += "#L0#我要点卷#l\r\n\r\n";
        selStr += "#L1#我要经验#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 0://nx
                if (cm.getHour() == 20 && cm.getMin() <= 5) {
                    sl = Math.random() * 5 + 1;
                    cm.gainNX(+sl);
                    cm.sendOk("获得了:" + sl + "点卷");
                    cm.dispose();
                } else {

                    cm.sendOk("活动时间还没有到");
                    cm.dispose();
                }
                break;
            case 1://exp
                 if (cm.getHour() == 20 && cm.getMin() <= 5) {
                    sl = (cm.getLevel()*5)+Math.random() * cm.getLevel() + 1;
                    cm.gainExp(+sl);
                    cm.sendOk("获得了:" + sl + "经验");
                    cm.dispose();
                } else {

                    cm.sendOk("活动时间还没有到");
                    cm.dispose();
                }
                break;
           
        }
    }
}
