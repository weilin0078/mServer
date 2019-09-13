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
			System.out.println("加载 自动注册完成 :::");
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
		在线时间(1);
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.RandDrop"))) {
			ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
		try {
			SpeedRunner.getInstance().loadSpeedRuns();
		} catch (SQLException e) {
			System.out.println("SpeedRunner错误:" + e);
		}
		World.registerRespawn();
		LoginServer.setOn();
		System.out.println("\r\n经验倍率:" + Integer.parseInt(ServerProperties.getProperty("tms.Exp"))
				+ "  物品倍率：" + Integer.parseInt(ServerProperties.getProperty("tms.Drop"))
				+ "  金币倍率" + Integer.parseInt(ServerProperties.getProperty("tms.Meso")));
		System.out.println("\r\n加载完成!开端成功! :::");
	}

	public void startServer() throws InterruptedException {
		Start.Check = false;
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"))) {
			System.out.println("[!!! Admin Only Mode Active !!!]");
		}
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister", "true"))) {
			ServerConstants.自动注册 = Boolean
					.parseBoolean(ServerProperties.getProperty("tms.AutoRegister", "true"));
			System.out.println("加载 自动注册完成 :::");
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
		自动存档(5);
		if (Boolean.parseBoolean(ServerProperties.getProperty("tms.RandDrop"))) {
			ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
		try {
			SpeedRunner.getInstance().loadSpeedRuns();
		} catch (SQLException e) {
			System.out.println("SpeedRunner错误:" + e);
		}
		World.registerRespawn();
		LoginServer.setOn();
		MapleMapFactory.loadCustomLife();
		Timer.WorldTimer.getInstance().register(DatabaseConnection.CloseSQLConnections, 1080000L);
		System.out.println("\r\n经验倍率:" + Integer.parseInt(ServerProperties.getProperty("tms.Exp"))
				+ "  物品倍率：" + Integer.parseInt(ServerProperties.getProperty("tms.Drop"))
				+ "  金币倍率" + Integer.parseInt(ServerProperties.getProperty("tms.Meso")));
		System.out.println("\r\n加载完成!开端成功! :::");
	}

	public static void 自动存档(final int time) {
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

	public static void 在线统计(final int time) {
		System.out.println("服务端启用在线统计." + time
				+ "分钟统计一次在线的人数信息.");
		Timer.WorldTimer.getInstance().register(new Runnable() {
			@Override
			public void run() {
				final Map connected = World.getConnected();
				final StringBuilder conStr = new StringBuilder(
						FileoutputUtil.CurrentReadable_Time() + " 在线人数: ");
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
					FileoutputUtil.log("在线统计.txt", conStr.toString() + "\r\n");
				}
			}
		}, 60000 * time);
	}

	public static void 在线时间(final int time) {
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
