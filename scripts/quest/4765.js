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
			if(qm.getQuestStatus(4765)==2){
			qm.sendOk("���Ѿ���ȡ������������Ŭ����40�����Ի�ø��ཱ���");
			qm.forceCompleteQuest(4765);
			qm.dispose();
			}else{
			qm.sendNext("��ϲ�㵱ǰ�ȼ��Ѿ�����#b30#k����");
			}
		} else if (status == 1) {
			qm.sendOk("��ϲ��ȼ��ﵽ��#r30#k�������Ѿ���ʽ��������ð�յ���һԱ�ˣ���\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v4001126# 500��#v4001215# 1��");
			qm.gainItem(4001126, 500);
			
			qm.forceCompleteQuest(4765);
			qm.dispose();
		} 
	}
}