package handling.channel.handler;

import java.util.List;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import server.maps.FieldLimitType;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.FamilyPacket;

public class FamilyHandler
{
    public static final void RequestFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.getSession().write((Object)FamilyPacket.getFamilyPedigree(chr));
        }
    }
    
    public static final void OpenFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getSession().write((Object)FamilyPacket.getFamilyInfo(c.getPlayer()));
    }
    
    public static final void UseFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int type = slea.readInt();
        final MapleFamilyBuff.MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
        if (entry == null) {
            return;
        }
        boolean success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (type) {
            case 0: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
                    success = false;
                    break;
                }
                if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "\u65e0\u6548\u540d\u79f0\u6216\u60a8\u4e0d\u5728\u540c\u4e00\u9891\u9053.");
                    success = false;
                    break;
                }
                if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                    break;
                }
                c.getPlayer().dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
                success = false;
                break;
            }
            case 1: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
                }
                else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "\u65e0\u6548\u540d\u79f0\u6216\u60a8\u4e0d\u5728\u540c\u4e00\u9891\u9053.");
                }
                else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "\u53e6\u4e00\u4e2a\u89d2\u8272\u8981\u6c42\u4f20\u5524\u8fd9\u4e2a\u89d2\u8272\u3002\u8bf7\u7a0d\u540e\u518d\u8bd5.");
                }
                else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    victim.getClient().getSession().write((Object)FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                }
                else {
                    c.getPlayer().dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
                }
                return;
            }
            case 4: {
                final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
                final List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7) {
                    success = false;
                    break;
                }
                for (final MapleFamilyCharacter chrz : chrs) {
                    final int chr = World.Find.findChannel(chrz.getId());
                    if (chr == -1) {
                        continue;
                    }
                    final MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                    entry.applyTo(chrr);
                }
                break;
            }
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
            case 8: {
                entry.applyTo(c.getPlayer());
                break;
            }
            case 9:
            case 10: {
                entry.applyTo(c.getPlayer());
                if (c.getPlayer().getParty() != null) {
                    for (final MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                        if (mpc.getId() != c.getPlayer().getId()) {
                            final MapleCharacter chr2 = c.getPlayer().getMap().getCharacterById(mpc.getId());
                            if (chr2 == null) {
                                continue;
                            }
                            entry.applyTo(chr2);
                        }
                    }
                    break;
                }
                break;
            }
        }
        if (success) {
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.getSession().write((Object)FamilyPacket.changeRep(-entry.rep));
            c.getPlayer().useFamilyBuff(entry);
        }
        else {
            c.getPlayer().dropMessage(5, "\u53d1\u751f\u9519\u8bef.");
        }
    }
    
    public static final void FamilyOperation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.getPlayer().dropMessage(1, "\u60a8\u9080\u8bf7\u7684\u73a9\u5bb6\u89d2\u8272\u540d\u5b57\u4e0d\u6b63\u786e\u6216\u8005\u5c1a\u672a\u767b\u5165.");
        }
        else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            c.getPlayer().dropMessage(1, "\u5df2\u7ecf\u5728\u76f8\u540c\u7684\u5b66\u9662\u91cc.");
        }
        else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.getPlayer().dropMessage(1, "\u4e0d\u518d\u76f8\u540c\u7684\u5730\u56fe\u91cc.");
        }
        else if (addChr.getSeniorId() != 0) {
            c.getPlayer().dropMessage(1, "\u60a8\u9080\u8bf7\u7684\u73a9\u5bb6\u89d2\u8272\u5df2\u7ecf\u5728\u5225\u7684\u5b66\u9662\u91cc.");
        }
        else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.getPlayer().dropMessage(1, "\u60a8\u9700\u8981\u9080\u8bf7\u6bd4\u60a8\u4f4e\u7b49\u7ea7\u7684\u73a9\u5bb6.");
        }
        else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
            c.getPlayer().dropMessage(1, "\u60a8\u9080\u8bf7\u7684\u73a9\u5bb6\u7b49\u7ea7\u5fc5\u9808\u76f8\u5dee20\u7b49\u7ea7\u4ee5\u5185.");
        }
        else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "\u60a8\u5fc5\u9808\u9080\u8bf710\u7d1a\u4ee5\u4e0a\u7684\u73a9\u5bb6.");
        }
        else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0) {
            c.getPlayer().dropMessage(1, "\u60a8\u5b66\u9662\u5df2\u7ecf\u6709\u4e24\u4e2a\u4eba\u4e86\uff0c\u8bf7\u627e\u60a8\u7684\u540e\u4ee3\u7ee7\u7eed\u9080\u8bf7\u5225\u4eba\u5427.");
        }
        else {
            addChr.getClient().getSession().write((Object)FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public static final void FamilyPrecept(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
        c.getPlayer().dropMessage(1, "\u91cd\u5f00\u5bb6\u65cf\u89c6\u7a97\u5373\u53ef\u5957\u7528.");
    }
    
    public static final void FamilySummon(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int TYPE = 1;
        final MapleFamilyBuff.MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
        final MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && c.getPlayer().isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost) && c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && c.getPlayer().getEventInstance() == null && tt.getEventInstance() == null) {
            final boolean accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().getSession().write((Object)FamilyPacket.changeRep(-cost.rep));
                tt.useFamilyBuff(cost);
            }
            else {
                tt.dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
            }
        }
        else {
            c.getPlayer().dropMessage(5, "\u4f20\u5524\u5931\u8d25\u3002\u60a8\u5f53\u524d\u7684\u4f4d\u7f6e\u6216\u72b6\u6001\u4e0d\u5141\u8bb8\u4f20\u5524.");
        }
        c.getPlayer().setTeleportName("");
    }
    
    public static final void DeleteJunior(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int juniorid = slea.readInt();
        if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid)) {
            return;
        }
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        final MapleFamilyCharacter oth = c.getPlayer().getMFC();
        final boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        }
        else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " \u6211\u505a\u4eba\u5931\u8d25 \u89e3\u6563\u4e86\u5bb6\u65cf", 0);
        if (!fam.splitFamily(juniorid, other)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "\u8e22\u51fa\u4e86 (" + other.getName() + ").");
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public static final void DeleteSenior(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
            return;
        }
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        final MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        final boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        }
        else {
            mgc.setJunior1(0);
        }
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        c.getPlayer().saveFamilyStatus();
        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " \u6211\u5c55\u7fc5\u9ad8\u98de\u4e86 \u79bb\u5f00\u4f60\u7684\u5bb6\u65cf", 0);
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "\u9000\u51fa\u4e86 (" + mgc.getName() + ") \u7684\u5bb6\u65cf.");
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }
    
    public static final void AcceptFamily(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (inviter != null && c.getPlayer().getSeniorId() == 0 && (c.getPlayer().isGM() || !inviter.isHidden()) && inviter.getLevel() - 20 <= c.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(slea.readMapleAsciiString()) && inviter.getNoJuniors() < 2 && c.getPlayer().getLevel() >= 10) {
            final boolean accepted = slea.readByte() > 0;
            inviter.getClient().getSession().write((Object)FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                c.getSession().write((Object)FamilyPacket.getSeniorMessage(inviter.getName()));
                final int old = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getFamilyId();
                final int oldj1 = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getJunior1();
                final int oldj2 = (c.getPlayer().getMFC() == null) ? 0 : c.getPlayer().getMFC().getJunior2();
                if (inviter.getFamilyId() > 0 && World.Family.getFamily(inviter.getFamilyId()) != null) {
                    final MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());
                    c.getPlayer().setFamily((old <= 0) ? inviter.getFamilyId() : old, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                    final MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    }
                    else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old > 0 && World.Family.getFamily(old) != null) {
                        MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                    }
                    else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    if (fam != null) {
                        if (inviter.getNoJuniors() == 1 || old > 0) {
                            fam.resetDescendants();
                        }
                        fam.resetPedigree();
                    }
                }
                else {
                    final int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0);
                        c.getPlayer().setFamily(id, inviter.getId(), (oldj1 <= 0) ? 0 : oldj1, (oldj2 <= 0) ? 0 : oldj2);
                        final MapleFamily fam2 = World.Family.getFamily(id);
                        fam2.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if (old > 0 && World.Family.getFamily(old) != null) {
                            MapleFamily.mergeFamily(fam2, World.Family.getFamily(old));
                        }
                        else {
                            fam2.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam2.resetDescendants();
                        fam2.resetPedigree();
                    }
                }
                c.getSession().write((Object)FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
    }
}
