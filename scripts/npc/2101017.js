var status = 0;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            //p=cm.paiMing();
            var a = "#b岛内人气排行榜<前十>：#r#e\r\n"; 
            a+=cm.rq(); 
            cm.sendOk(a);
            cm.dispose();
            } else {                      
                    cm.dispose();
        }
    }
}