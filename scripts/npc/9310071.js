/* 
 * �ű�����: cm
 * �ű���;: ���þ��н�
 * �ű�����: ����ؼ
 * ����ʱ��: 2014/12/18
 */
 
importPackage(Packages.client);
importPackage(Packages.client.inventory);


var status = -1;
var beauty = 0;
var tosend = 0;
var sl;
var mats;
function start() {
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
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                cm.sendNext("�����Ҫ���þ��н�����������Ұɡ�");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
            var gsjb = "";
            gsjb ="  #e�˴��һ� #b- ��Ҷ - #r1��1�һ���\r\n  #r��Ҷ��÷�ʽ: #v4001126#\r\n  ���κι����м��ʱ�\r\n";
            gsjb +="  ��ǰ���þ�:#r" + cm.getPlayer().getCSPoints(2) + "#k\r\n\r\n#d";
            gsjb +="#L3##b#z4001126#�һ����þ� #fUI/Basic/BtHide3/mouseOver/0# #b���� - (#r1 = 1#b)#l\r\n\r\n";




           gsjb += "#L1##b#z4001126#�һ���Ϸ�� #fUI/Basic/BtHide3/mouseOver/0# #b���� - (#r1 = 250#b)#l#l\r\n\r\n";
           gsjb += "#L5##d��Ϸ�̵�#l#l\r\n";
            cm.sendSimple(gsjb);
        } else if (status == 1) {
            if (cm.getPlayer() >= 1 && cm.getPlayer() <= 5) {
                cm.sendOk("GM���ܲ���һ���");
                cm.dispose();
            }
            if (selection == 0) {
                if (cm.getPlayer().getCSPoints(2) / 100 == 0) {
                    cm.sendNext("�����ʻ����þ������޷��һ�#z4001126#��");
                    status = -1;
                } else {
                    beauty = 1;
                    cm.sendGetNumber("������#r���þ�#k�һ�#b#z4001126##k������:\r\n#b���� - (#r100 = 1#b)\r\n����˻���Ϣ - \r\n    ���þ�����: #r" +
                            cm.gainMeso + " \r\n", 1, 1, cm.getPlayer().getCSPoints(2) / 100);

                }

            
            } else if (selection == 1) {
                var iter = cm.getChar().getInventory(MapleInventoryType.ETC).listById(4001126).iterator();
                if (cm.haveItem(4001126) == 0) {
                    cm.sendNext("�����ʻ�#v4001126#��������һ���Ϸ�ҡ�");
                    status = -1;
                } else {
                    beauty = 4;
                    cm.sendGetNumber("������#b#z4001126##k�һ�#r��Ϸ��#k������:\r\n#b���� - (#r1 = 250#b)\r\n ", 1, 1, iter.next().getQuantity());

                }
        } else if (selection == 5) {//ɾ����Ʒ
             cm.dispose();
            cm.openNpc(9310070, 55);

            } else if (selection == 3) {
                var iter = cm.getChar().getInventory(MapleInventoryType.ETC).listById(4001126).iterator();
                if (cm.haveItem(4001126) == 0) {
                    cm.sendNext("�����ʻ�#v4001126#��������һ���Ӷ����");
                    status = -1;
                } else {
                    beauty = 3;
                    cm.sendGetNumber("������#b#z4001126##k�һ�#r���þ�#k������:\r\n#b���� - (#r1 = 1#b)\r\n����˻���Ϣ - \r\n    ���þ�����: #r" +
                            cm.getPlayer().getCSPoints(2) + "   \r\n", 1, 1, iter.next().getQuantity());

                }

            }


        } else if (status == 2) {
            if (beauty == 1) {
                if (selection <= 0) {
                    cm.sendOk("����Ķһ����ִ���");
                    cm.dispose();
                /*
                } else if (selection >= 200) {
                    sl = (selection / 200) + 1;
                } else {
                    sl = 3;
                }

                //if(cm.getPlayer().getInventory(net.sf.cherry.client.MapleInventoryType.getByType(1)).isFull()){
                if (cm.getSpace(4) < sl) {
                    cm.sendOk("��ı������������ռ䲻��!��������" + sl + "���ռ�����.\r\n��������г���С���Ļ�����λ!\r\n�磺����<������7.5���ռ�����>��ô������Ҫ��8���ռ�!");
                    cm.dispose();
*/
                } else if (cm.getPlayer().getCSPoints(2) >= selection * 100) {
                    cm.gainD(-selection * 100);
                    cm.gainItem(4000463, selection);
                    cm.sendOk("���ɹ��� #r " + (selection * 100) + " #k���þ� �һ��� ��������#v4000463# x #r" + selection + " #k")
                } else {
                    cm.sendNext("�һ�" + selection + "��#z4000463##v4000463# ��Ҫ#r " + (selection * 100) + "#k���þ�����û���㹻�ĵ��þ���");
                    cm.dispose();
                }
            } else if (beauty == 4) {
                if (cm.haveItem(4001126, selection)) {
                    cm.gainItem(4001126, -selection);
                    cm.gainMeso(+Math.floor(selection * 250));
                    cm.sendOk("���ɹ���#z4001126##v4001126# x #r1 #k��Ϊ#r " + Math.floor(selection * 250) + " #k��Ϸ�ҡ�");
                } else {
                    cm.sendNext("������������������޷��һ���Ϸ�ҡ�");
                    cm.dispose();
                }


            } else if (beauty == 3) {
                if (cm.haveItem(4001126, 500)) {
                    cm.gainItem(4001126, -500);
                    cm.gainD(+Math.floor(1 * 500));
                    cm.sendOk("���ɹ���#z4001126##v4001126# x #r #k��Ϊ#r " + Math.floor(1 * 500) + " #k���þ���");
                } else {
                    cm.sendNext("������������������޷��һ����þ���");
                    cm.dispose();
                }
            }
            status = -1;
        } else {
            cm.dispose();
        }
    }
}