var ϣ�� = "#fMob/9700000.img/move/0#";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("��л��Ĺ��٣�");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
			//��ʾ��ƷIDͼƬ�õĴ�����  #v����д��ID#
text += ""+ϣ��+"\r\n"
            text += "#d����Χ�Ĺ��������ҵ���#v1004148#,�Ҿ��ܰ����ٻ�����ϣ��Ŷ~ (^O^).1���ٻ�һ��.#l\r\n#L1##r�ٻ���ŭ��ϣ��.\r\n"//3
            cm.sendSimple(text);
        } else if (selection == 1) {
			//1
			//2
			//3
			//4
			//5
			/*if(!cm.beibao(1,3)){
            cm.sendOk("װ�������಻��3���ո�");
            cm.dispose();
			}else if(!cm.beibao(2,1)){
            cm.sendOk("���������಻��1���ո�");
            cm.dispose();
			}else if(!cm.beibao(3,1)){
            cm.sendOk("���������಻��1���ո�");
            cm.dispose();
			}else if(!cm.beibao(4,1)){
            cm.sendOk("���������಻��1���ո�");
            cm.dispose();
			}else if(!cm.beibao(5,1)){
            cm.sendOk("�ֽ������಻��1���ո�");
            cm.dispose();
			}else */if(cm.haveItem(1004148,1)){
				cm.gainItem(1004148, -1);
				cm.spawnMonster(9700000, 1);
				cm.spawnMonster(4230107, 3);
				cm.gainMeso(20000);
            cm.sendOk("�ٻ��ɹ���");
			cm.worldMessage(6,"��ң�["+cm.getName()+"]�ٻ��˷�ŭ��ϣ��.���С�İ�������");
            cm.dispose();
			}else{
            cm.sendOk("��Ҫ��#v1004148#.�Ҳ��ܰ����ٻ�ϣ��Ŷ~��");
            cm.dispose();
			}
		}
    }
}

