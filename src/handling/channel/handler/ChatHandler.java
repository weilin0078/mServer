package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.CommandProcessor;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChatHandler
{
    public static final void GeneralChat(final String text, final byte unk, final MapleClient c, final MapleCharacter chr) {
        if (chr != null) {
            try {
                final boolean condition = CommandProcessor.processCommand(c, text, ServerConstants.CommandType.NORMAL);
                if (condition) {
                    return;
                }
            }
            catch (Throwable e) {
                System.err.println(e);
            }
            if (!chr.isGM() && text.length() >= 80) {
                return;
            }
            if (chr.isHidden()) {
                chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
            }
            else {
                chr.getCheatTracker().checkMsg();
                chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getPosition());
            }
        }
    }
    
    public static final void Others(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        final int[] recipients = new int[numRecipients];
        for (byte i = 0; i < numRecipients; ++i) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.getSession().write((Object)MaplePacketCreator.serverNotice(6, "你已经被禁言，因此无法说话."));
            return;
        }
        if (CommandProcessor.processCommand(c, chattext, ServerConstants.CommandType.NORMAL)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0: {
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            }
            case 1: {
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            }
            case 2: {
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            }
            case 3: {
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            }
        }
    }
    
    public static final void Messenger(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        MapleMessenger messenger = c.getPlayer().getMessenger();
        switch (slea.readByte()) {
            case 0: {
                if (messenger == null) {
                    final int messengerid = slea.readInt();
                    if (messengerid == 0) {
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    }
                    else {
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                    break;
                }
                break;
            }
            case 3: {
                if (messenger == null) {
                    break;
                }
                final int position2 = messenger.getLowestPosition();
                if (position2 <= -1 || position2 >= 4) {
                    return;
                }
                final String input = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
                if (target != null) {
                    if (target.getMessenger() == null) {
                        if (!target.isGM() || c.getPlayer().isGM()) {
                            c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 1));
                            target.getClient().getSession().write((Object)MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                        }
                        else {
                            c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 0));
                        }
                    }
                    else {
                        c.getSession().write((Object)MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + "已经是使用枫叶信使."));
                    }
                }
                else if (World.isConnected(input)) {
                    World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGM());
                }
                else {
                    c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 0));
                }
                break;
            }
            case 5: {
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) {
                    if (target.getMessenger() != null) {
                        target.getClient().getSession().write((Object)MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                        break;
                    }
                    break;
                }
                else {
                    if (!c.getPlayer().isGM()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                        break;
                    }
                    break;
                }
                
            }
            case 6: {
                if (messenger != null) {
                    World.Messenger.messengerChat(messenger.getId(), slea.readMapleAsciiString(), c.getPlayer().getName());
                    break;
                }
                break;
            }
        }
    }
    
    public static final void Whisper_Find(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        switch (mode) {
            case 5:
            case 68: {
                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player == null) {
                    final int ch = World.Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }
                        if (player != null) {
                            if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                                c.getSession().write((Object)MaplePacketCreator.getFindReply(recipient, (byte)ch, mode == 68));
                            }
                            else {
                                c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                            }
                            return;
                        }
                    }
                    if (ch == -10) {
                        c.getSession().write((Object)MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                    }
                    else if (ch == -20) {
                        c.getSession().write((Object)MaplePacketCreator.getFindReplyWithMTS(recipient, mode == 68));
                    }
                    else {
                        c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                    }
                    break;
                }
                if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                    c.getSession().write((Object)MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                break;
            }
            case 6: {
                if (!c.getPlayer().getCanTalk()) {
                    c.getSession().write((Object)MaplePacketCreator.serverNotice(6, "你已经被禁言，因此无法说话."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = World.Find.findChannel(recipient);
                if (ch <= 0) {
                    c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                    break;
                }
                final MapleCharacter player2 = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                if (player2 == null) {
                    break;
                }
                player2.getClient().getSession().write((Object)MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                if (!c.getPlayer().isGM() && player2.isGM()) {
                    c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                }
                else {
                    c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)1));
                }
                break;
            }
        }
    }
}
