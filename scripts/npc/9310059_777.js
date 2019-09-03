/*
 * 
 * @枫之梦
 * 神器进阶系统 - 魔武双修
 */
importPackage(net.sf.odinms.client);
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var txt1 = "#d#L1兑换##v1472214##z1472214#需要BOOS币#r100\r\n";
            var txt2 = "#d#L2兑换##v1402196##z1402196#需要BOOS币#r100\r\n";
            var txt3 = "#d#L3兑换##v1452205##z1452205#需要BOOS币#r100\r\n";
            var txt4 = "#d#L4兑换##v1482168##z1482168#需要BOOS币#r100\r\n";
            var txt5 = "#d#L5兑换##v1432167##z1432167#需要BOOS币#r100\r\n";
            var txt6 = "#d#L6兑换##v1492179##z1492179#需要BOOS币#r100\r\n";
            var txt7 = "#d#L7兑换##v1372177##z1372177#需要BOOS币#r100\r\n";
            var txt8 = "#d#L8兑换##v1462193##z1462193#需要BOOS币#r100\r\n";
            var txt9 = "#d#L9兑换##v1332225##z1332225#需要BOOS币#r100\r\n";
            

            cm.sendSimple("使用#v4310143#和3E金币可以兑换!(各种BOSS出)\r\n" + txt1 + "" + txt2 +  "" + txt3 + "" + txt4 + "" + txt5 + "" + txt6 + "" + txt7 + "" + txt8 + "" + txt9 + "");
        } else if (status == 1) {
            if (selection == 1) {
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1472214, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 2) { 
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1402196, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 3) {
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1452205, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 4) {
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1482168, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 5) {
               if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1432167, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 6) {
               if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1492179, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 7) {
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1372177, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 8) {
               if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1492179, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 9) {
                if (cm.haveItem(4310143, 100) && cm.getMeso() > 300000000) {
                    cm.gainItem(4310143, -100);
                    cm.gainItem(1332225, 1);
                    cm.gainMeso(-300000000);
                    cm.dispose();
                } else {
                    cm.sendOk("金币和材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 10) {
                if (cm.haveItem(4031645, 1) && cm.getMeso() > 100000) {
                    cm.gainItem(4031645, -1);
                    cm.gainExp(+100000);
                    cm.gainMeso(-100000);
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 11) {
                if (cm.haveItem(4031646, 1) && cm.getMeso() > 60000) {
                    cm.gainItem(4031646, -1);
                    cm.gainExp(+50000);
                    cm.gainMeso(-60000);
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 12) {
                if (cm.haveItem(4031647, 1) && cm.getMeso() > 60000) {
                    cm.gainItem(4031647, -1);
                    cm.gainExp(+50000);
                    cm.gainMeso(-60000);
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 13) {
                if (cm.haveItem(4001013, 100)) {
                    cm.gainItem(4001013, -100);
                    cm.gainItem(1482085, 1);
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 14) {
                if (cm.haveItem(1482029, 1)) {
                    cm.gainItem(1482029, -1);
                    cm.gainItem(神器, 1);
                    cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.cherry.tools.MaplePacketCreator.serverNotice(12, cm.getC().getChannel(), "[合成系统]" + " : " + " [" + cm.getPlayer().getName() + "]合成了神器", true).getBytes());
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            } else if (selection == 15) {
                if (cm.haveItem(1492030, 1)) {
                    cm.gainItem(1492030, -1);
                    cm.gainItem(神器, 1);
                    cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.cherry.tools.MaplePacketCreator.serverNotice(12, cm.getC().getChannel(), "[合成系统]" + " : " + " [" + cm.getPlayer().getName() + "]合成了神器", true).getBytes());
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            }else if(selection == 16){
                 if (cm.haveItem(1442071, 1)) {
                    cm.gainItem(1442071, -1);
                    cm.gainItem(神器, 1);
                    cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.cherry.tools.MaplePacketCreator.serverNotice(12, cm.getC().getChannel(), "[合成系统]" + " : " + " [" + cm.getPlayer().getName() + "]合成了神器", true).getBytes());
                    cm.dispose();
                } else {
                    cm.sendOk("材料不足。无法合成！");
                    cm.dispose();
                }
            }
        }
    }
}
