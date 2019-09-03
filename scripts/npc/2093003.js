/*
自由市场牌子
*/


var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
            cm.sendNext("#d亲爱的#r#h ##d你好，我可以送你进去天空之城!!!");
        } else if (status == 1) {            
			cm.warp(200000000);
		cm.dispose();
	}}
}
