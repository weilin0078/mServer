/* Author: Xterminator
	NPC Name: 		Trainer Bartos
	Map(s): 		Victoria Road : Pet-Walking Road (100000202)
	Description: 		Pet Trainer
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
	cm.dispose();
	return;
    } else if (status >= 1 && mode == 0) {
	cm.sendNext("Hmmm ... too busy to do it right now? If you feel like doing it, though, come back and find me.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendSimple("Do you have any business with me?\r\n#L0##bPlease tell me about this place.#l\r\n#L1#I'm here through a word from Mar the Fairy...#k#l\r\n#L2##r群宠技能学习#k#l");
    } else if (status == 1) {
	if (selection == 0) {
	    if (cm.haveItem(4031035)) {
		cm.sendNext("Get that letter, jump over obstacles with your pet, and take that letter to my brother Trainer Frod. Give him the letter and something good is going to happen to your pet.");
		cm.dispose();
	    } else {
		cm.sendYesNo("This is the road where you can go take a walk with your pet. You can just walk around with it, or you can train your pet to go through the obstacles here. If you aren't too close with your pet yet, that may present a problem and he will not follow your command as much... So, what do you think? Wanna train your pet?");
	    }
	}else if (selection == 2) {
	    if (cm.haveItem(5460000, 1)) {
            if(cm.getJob() >= 1000 && cm.getJob() <= 1512){
    cm.teachSkill(10000018,1,1);
			} else if(cm.getJob() >= 2000){
    cm.teachSkill(20000024,1,1);
			} else {
    cm.teachSkill(8,1,1);
			}
			cm.gainItem(5460000, -1);
            cm.sendOk("成功学习群宠技能！");
            cm.dispose();
		}else{
            cm.sendOk("你没有宠物的点心#v5460000#无法学习！请到商城购买！");
            cm.dispose();
		}
	} else {
	    cm.sendOk("Hey, are you sure you've met #bMar the Fairy#k? Don't lie to me if you've never met her before because it's obvious. That wasn't even a good lie!!");
	    cm.dispose();
	}
    } else if (status == 2) {
	cm.gainItem(4031035, 1);
	cm.sendNext("Ok, here's the letter. He wouldn't know I sent you if you just went there straight, so go through the obstacles with your pet, go to the very top, and then talk to Trainer Frod to give him the letter. It won't be hard if you pay attention to your pet while going through obstacles. Good luck!");
	cm.dispose();
    }
}