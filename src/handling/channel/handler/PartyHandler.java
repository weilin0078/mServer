package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class PartyHandler
{
    public static final void DenyPartyRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int action = slea.readByte();
        final int partyid = slea.readInt();
        if (c.getPlayer().getParty() == null) {
            final MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (action == 27) {
                    if (party.getMembers().size() < 6) {
                        World.Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    }
                    else {
                        c.getSession().write((Object)MaplePacketCreator.partyStatusMessage(17));
                    }
                }
                else if (action != 22) {
                    final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                    if (cfrom != null) {
                        cfrom.getClient().getSession().write((Object)MaplePacketCreator.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
            }
            else {
                c.getPlayer().dropMessage(5, "\u8981\u53c2\u52a0\u7684\u961f\u4f0d\u4e0d\u5b58\u5728\u3002");
            }
        }
        else {
            c.getPlayer().dropMessage(5, "\u60a8\u5df2\u7ecf\u6709\u4e00\u4e2a\u7ec4\u961f\uff0c\u65e0\u6cd5\u52a0\u5165\u5176\u4ed6\u7ec4\u961f!");
        }
    }
    
    public static final void PartyOperatopn(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int operation = slea.readByte();
        MapleParty party = c.getPlayer().getParty();
        final MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
        switch (operation) {
            case 1: {
                if (c.getPlayer().getParty() == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.getSession().write((Object)MaplePacketCreator.partyCreated(party.getId()));
                    break;
                }
                if (partyplayer.equals(party.getLeader()) && party.getMembers().size() == 1) {
                    c.getSession().write((Object)MaplePacketCreator.partyCreated(party.getId()));
                    break;
                }
                c.getPlayer().dropMessage(5, "\u4f60\u4e0d\u80fd\u521b\u5efa\u4e00\u4e2a\u7ec4\u961f,\u56e0\u4e3a\u4f60\u5df2\u7ecf\u5b58\u5728\u4e00\u4e2a\u961f\u4f0d\u4e2d");
                break;
            }
            case 2: {
                if (party != null) {
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                    }
                    else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                    }
                    c.getPlayer().setParty(null);
                    break;
                }
                break;
            }
            case 3: {
                final int partyid = slea.readInt();
                if (c.getPlayer().getParty() != null) {
                    c.getPlayer().dropMessage(5, "\u4f60\u4e0d\u80fd\u521b\u5efa\u4e00\u4e2a\u7ec4\u961f,\u56e0\u4e3a\u4f60\u5df2\u7ecf\u5b58\u5728\u4e00\u4e2a\u961f\u4f0d\u4e2d");
                    break;
                }
                party = World.Party.getParty(partyid);
                if (party == null) {
                    c.getPlayer().dropMessage(5, "\u8981\u52a0\u5165\u7684\u961f\u4f0d\u4e0d\u5b58\u5728");
                    break;
                }
                if (party.getMembers().size() < 6) {
                    World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                    c.getPlayer().receivePartyMemberHP();
                    c.getPlayer().updatePartyMemberHP();
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.partyStatusMessage(17));
                break;
            }
            case 4: {
                final MapleCharacter invited = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (invited == null) {
                    c.getSession().write((Object)MaplePacketCreator.partyStatusMessage(18));
                    break;
                }
                if (invited.getParty() != null || party == null) {
                    c.getSession().write((Object)MaplePacketCreator.partyStatusMessage(16));
                    break;
                }
                if (party.getMembers().size() < 6) {
                    invited.getClient().getSession().write((Object)MaplePacketCreator.partyInvite(c.getPlayer()));
                    break;
                }
                break;
            }
            case 5: {
                if (partyplayer != null && party != null && partyplayer.equals(party.getLeader())) {
                    final MaplePartyCharacter expelled = party.getMemberById(slea.readInt());
                    if (expelled != null) {
                        World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                        if (c.getPlayer().getEventInstance() != null && expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        if (c.getPlayer().getPyramidSubway() != null && expelled.isOnline()) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                    }
                    break;
                }
                break;
            }
            case 6: {
                if (party != null) {
                    final MaplePartyCharacter newleader = party.getMemberById(slea.readInt());
                    if (newleader != null && partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                    }
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    break;
                }
                break;
            }
            default: {
                System.out.println("Unhandled Party function." + operation);
                break;
            }
        }
    }
}
