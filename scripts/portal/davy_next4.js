function enter(pi) {

        if (pi.isLeader()) {
		pi.givePartyExp(700000);
            pi.warpParty(925100500); //next
        } else {
            pi.playerMessage(5, "The leader must be here");
        }
    } else {
        pi.playerMessage(5, "The portal is not opened yet.");
    }
}