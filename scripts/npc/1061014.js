importPackage(net.sf.cherry.tools);
importPackage(net.sf.cherry.client);



var status = 0;  



	
function start() {  
    status = -1;  
    action(1, 0, 0);  
}  

function action(mode, type, selection) {   
    if (mode == -1) {  
        cm.dispose();  
    }  
    else {   
        if (mode == 0) {      
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
		        var pbMap = cm.getC().getChannelServer().getMapFactory().getMap(105100300);
 if (pbMap.getCharacters().size() == 0) {
		    cm.sendYesNo("在当前所在的频道中可以参加#b普通模式蝙蝠怪远征队#k。\r\n#b#i3994116#   40-70级. 2人以上.\r\n\r\n#k[目前已经进入#r" + cm.getBossLog('BF') + "#k次]");

}
                 else {
                    cm.sendOk("#r里面有人?");
                  cm.dispose();}
        } else if (status == 1  && cm.getLevel() >= 40) {
		var party = cm.getPlayer().getParty();	
		if (party == null || party.getLeader().getId() != cm.getPlayer().getId()) {
                    cm.sendOk("你不是队长。请你们队长来说话吧！");
                    cm.dispose();
               }else if (cm.getBossLog('BF') < 5 && cm.haveItem(1092008) < 2) {
                  cm.warpParty(105100300);
                  cm.getC().getChannelServer().getMapFactory().getMap(105100300).killAllMonsters(); 
                  cm.setBossLog('BF');
                  cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,net.sf.cherry.tools.MaplePacketCreator.serverNotice(11,cm.getC().getChannel(),"[Boss公告]" + " : " + "[" + cm.getPlayer().getName() + "]开始挑战巨魔蝙蝠,大家祝福他!",true).getBytes());
                  cm.dispose();
                } else {
                    cm.sendOk("#r每次进入,只能带一个#v1092008#. 或者你每天只能允许进入5次 ! 时间未到.");
                   mode = 1;
                   status = -1;
                }
                
            }
            else{
                cm.sendOk("你的等级没有达到45级，所以不能进入!");
               mode = 1;
               status = -1;
        }
    }
}
