function enter(pi) {
    try {
        var em = pi.getEventManager("Pirate");
		var playerS = pi.isLeader();
        if (playerS == true && em != null && pi.haveItem(4001120,10)&& pi.haveItem(4001121,10)&& pi.haveItem(4001122,10)) {
        //if (playerS == true && em != null && em.getProperty("stage2").equals("3")) {
		pi.givePartyExp(180000);
            pi.warpParty(925100200); //next
        } else {
            pi.playerMessage(5, "The portal is not opened yet.");
        }
    } catch(e) {
        pi.playerMessage(5, "Error: " + e);
    }
}