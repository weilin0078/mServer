package server;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import client.MapleCharacter;
import client.SkillFactory;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamilyBuff;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;

public class Start {
	public static boolean Check;
	public static final Start instance;
	private static int maxUsers;

	public static void main(final String[] args) {
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin"))) {
			System.out.println("[!!! Admin Only Mode Active !!!]");
		}
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister"))) {
			System.out.println("\u52a0\u8f7d \u81ea\u52a8\u6ce8\u518c\u5b8c\u6210 :::");
		}
		try (final PreparedStatement ps = DatabaseConnection.getConnection()
				.prepareStatement("UPDATE accounts SET loggedin = 0")) {
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
		}
		World.init();
		Timer.WorldTimer.getInstance().start();
		Timer.EtcTimer.getInstance().start();
		Timer.MapTimer.getInstance().start();
		Timer.MobTimer.getInstance().start();
		Timer.CloneTimer.getInstance().start();
		Timer.EventTimer.getInstance().start();
		Timer.BuffTimer.getInstance().start();
		LoginInformationProvider.getInstance();
		MapleQuest.initQuests();
		MapleLifeFactory.loadQuestCounts();
		MapleItemInformationProvider.getInstance().load();
		RandomRewards.getInstance();
		SkillFactory.getSkill(99999999);
		MapleOxQuizFactory.getInstance().initialize();
		MapleCarnivalFactory.getInstance();
		MapleGuildRanking.getInstance().RankingUpdate();
		MapleFamilyBuff.getBuffEntry();
		MapleServerHandler.registerMBean();
		CashItemFactory.getInstance().initialize();
		LoginServer.run_startup_configurations();
		ChannelServer.startChannel_Main();
		CashShopServer.run_startup_configurations();
		Timer.CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000L);
		\u5728\u7ebf\u65f6\u95f4(1);
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.RandDrop"))) {
			ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
		try {
			SpeedRunner.getInstance().loadSpeedRuns();
		} catch (SQLException e) {
			System.out.println("SpeedRunner\u9519\u8bef:" + e);
		}
		World.registerRespawn();
		LoginServer.setOn();
		System.out.println("\r\n\u7ecf\u9a8c\u500d\u7387:" + Integer.parseInt(ServerProperties.getProperty("tms.Exp"))
				+ "  \u7269\u54c1\u500d\u7387\uff1a" + Integer.parseInt(ServerProperties.getProperty("tms.Drop"))
				+ "  \u91d1\u5e01\u500d\u7387" + Integer.parseInt(ServerProperties.getProperty("tms.Meso")));
		System.out.println("\r\n\u52a0\u8f7d\u5b8c\u6210!\u5f00\u7aef\u6210\u529f! :::");
	}

	public void startServer() throws InterruptedException {
		Start.Check = false;
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"))) {
			System.out.println("[!!! Admin Only Mode Active !!!]");
		}
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister", "true"))) {
			ServerConstants.\u81ea\u52a8\u6ce8\u518c = Boolean
					.parseBoolean(ServerProperties.getProperty("tms.AutoRegister", "true"));
			System.out.println("\u52a0\u8f7d \u81ea\u52a8\u6ce8\u518c\u5b8c\u6210 :::");
		}
		try {
			try (final PreparedStatement ps = DatabaseConnection.getConnection()
					.prepareStatement("UPDATE accounts SET loggedin = 0")) {
				ps.executeUpdate();
			}
			try (final PreparedStatement ps = DatabaseConnection.getConnection()
					.prepareStatement("UPDATE accounts SET lastGainHM = 0")) {
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
		}
		World.init();
		Timer.WorldTimer.getInstance().start();
		Timer.EtcTimer.getInstance().start();
		Timer.MapTimer.getInstance().start();
		Timer.MobTimer.getInstance().start();
		Timer.CloneTimer.getInstance().start();
		Timer.EventTimer.getInstance().start();
		Timer.BuffTimer.getInstance().start();
		LoginInformationProvider.getInstance();
		MapleQuest.initQuests();
		MapleLifeFactory.loadQuestCounts();
		MapleItemInformationProvider.getInstance().load();
		RandomRewards.getInstance();
		SkillFactory.getSkill(99999999);
		MapleOxQuizFactory.getInstance().initialize();
		MapleCarnivalFactory.getInstance();
		MapleGuildRanking.getInstance().RankingUpdate();
		MapleFamilyBuff.getBuffEntry();
		MapleServerHandler.registerMBean();
		PredictCardFactory.getInstance().initialize();
		CashItemFactory.getInstance().initialize();
		LoginServer.run_startup_configurations();
		ChannelServer.startChannel_Main();
		CashShopServer.run_startup_configurations();
		Timer.CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000L);
		\u81ea\u52a8\u5b58\u6863(5);
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.RandDrop"))) {
			ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
		try {
			SpeedRunner.getInstance().loadSpeedRuns();
		} catch (SQLException e) {
			System.out.println("SpeedRunner\u9519\u8bef:" + e);
		}
		World.registerRespawn();
		LoginServer.setOn();
		MapleMapFactory.loadCustomLife();
		Timer.WorldTimer.getInstance().register(DatabaseConnection.CloseSQLConnections, 1080000L);
		System.out.println("\r\n\u7ecf\u9a8c\u500d\u7387:" + Integer.parseInt(ServerProperties.getProperty("tms.Exp"))
				+ "  \u7269\u54c1\u500d\u7387\uff1a" + Integer.parseInt(ServerProperties.getProperty("tms.Drop"))
				+ "  \u91d1\u5e01\u500d\u7387" + Integer.parseInt(ServerProperties.getProperty("tms.Meso")));
		System.out.println("\r\n\u52a0\u8f7d\u5b8c\u6210!\u5f00\u7aef\u6210\u529f! :::");
	}

	public static void \u81ea\u52a8\u5b58\u6863(final int time) {
		Timer.WorldTimer.getInstance().register(new Runnable() {
			@Override
			public void run() {
				int ppl = 0;
				try {
					for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
						for (final MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
							if (chr == null) {
								continue;
							}
							++ppl;
							chr.saveToDB(false, false);
						}
					}
				} catch (Exception ex) {
				}
			}
		}, 60000 * time);
	}

	public static void \u5728\u7ebf\u7edf\u8ba1(final int time) {
		System.out.println("\u670d\u52a1\u7aef\u542f\u7528\u5728\u7ebf\u7edf\u8ba1." + time
				+ "\u5206\u949f\u7edf\u8ba1\u4e00\u6b21\u5728\u7ebf\u7684\u4eba\u6570\u4fe1\u606f.");
		Timer.WorldTimer.getInstance().register(new Runnable() {
			@Override
			public void run() {
				final Map connected = World.getConnected();
				final StringBuilder conStr = new StringBuilder(
						FileoutputUtil.CurrentReadable_Time() + " \u5728\u7ebf\u4eba\u6570: ");
				for (Iterator i$ = connected.keySet().iterator(); i$.hasNext();) {
					int i = ((Integer) i$.next()).intValue();
					if (i == 0) {
						int users = ((Integer) connected.get(Integer.valueOf(i))).intValue();
						conStr.append(StringUtil.getRightPaddedStr(String.valueOf(users), ' ', 3));
						if (users > Start.maxUsers) {
							Start.maxUsers = users;
						}
						conStr.append(" 最高在线: ");
						conStr.append(Start.maxUsers);
						break;
					}
				}
				System.out.println(conStr.toString());
				if (Start.maxUsers > 0) {
					FileoutputUtil.log("\u5728\u7ebf\u7edf\u8ba1.txt", conStr.toString() + "\r\n");
				}
			}
		}, 60000 * time);
	}

	public static void \u5728\u7ebf\u65f6\u95f4(final int time) {
		Timer.WorldTimer.getInstance().register(new Runnable() {
			@Override
			public void run() {
				try {
					for (final ChannelServer chan : ChannelServer.getAllInstances()) {
						for (final MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
							if (chr == null) {
								continue;
							}
							chr.gainGamePoints(1);
							if (chr.getGamePoints() >= 5) {
								continue;
							}
							chr.resetGamePointsPD();
						}
					}
				} catch (Exception ex) {
				}
			}
		}, 60000 * time);
	}

	static {
		Start.Check = true;
		instance = new Start();
		Start.maxUsers = 0;
	}

	public static class Shutdown implements Runnable {
		@Override
		public void run() {
			new Thread(ShutdownServer.getInstance()).start();
		}
	}
}
