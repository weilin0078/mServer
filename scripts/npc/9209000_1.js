var ����ţ = "#fMob/9001000.img/move/0#";
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
                
   cm.sendOk("��лʹ��.");
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
	for(i = 0; i < 10; i++){
		text += "";
	}				
	text += "#d�ٻ� #r" + ����ţ + "��սʿ�̹١���Ҫ������Ʒ��\r\n2000��ȯ \r\n~\r\n"
	text += "\r\n#L1##d��Ҫ�ٻ�սʿ�̹�";//����
	text += "     \r\n"
        cm.sendSimple(text);
        } else if (selection == 1) {
                      if(!cm.canHold(4001230,1)){
			cm.sendOk("��������ı��������ٿճ�2��λ�ã�");
            cm.dispose();
        } else if (cm.getPlayer().getCSPoints(1) >= 2000) {
                cm.gainNX(-2000);
               
cm.spawnMonster(9001000, 1);
            cm.sendOk("�ٻ�սʿ�̹ٳɹ���");
            cm.dispose();
cm.worldMessage(6,"��ң�["+cm.getName()+"]�ڻ��ͼ[���Ԫ����]�ٻ��� ���͵�սʿ�̹�.���С�İ�������");
			}else{
            cm.sendOk("�޷����򣬵������\r\n");
            cm.dispose();
			}
		}
    }
}



