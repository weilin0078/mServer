var status = -1;

function start(mode, type, selection) {
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if(qm.getQuestStatus(4761)==2){
			qm.sendOk("���Ѿ���ȡ������������Ŭ����10�����Ի���½����");
			qm.forceCompleteQuest(4761);
			qm.dispose();
			}else{
			qm.sendNext("��ϲ�㵱ǰ�ȼ��Ѿ�����#b8#k����");
			}
		} else if (status == 1) {
			qm.sendOk("��ϲ����ϵͳ������\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v2000003# x 50��"); 
			qm.gainItem(2000003, 50);//5390003
                        qm.gainItem(1142358,3,3,3,3,18,18,2,2,2,2,2,2,2,2);
                        //qm.gainMeso(+500000);
                        //qm.gainNx(+10000);
			qm.forceCompleteQuest(4761);
			qm.dispose();
		} 
	}
}