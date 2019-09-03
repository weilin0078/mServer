function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
 if (pi.getMap().getReactorByName("sMob1").getState() >= 1 && pi.getMap().getReactorByName("sMob2").getState() >= 1 && pi.getMap().getReactorByName("sMob3").getState() >= 1 && pi.getMap().getReactorByName("sMob4").getState() >= 1) {
		  if (pi.isLeader()) {
		pi.givePartyExp(400000);
		  pi.warpParty(925100400); //next
        } else {
            pi.playerMessage(5, "not");
		  }
    } else {
        pi.playerMessage(5, "The portal is not opened yet.");
    }
}