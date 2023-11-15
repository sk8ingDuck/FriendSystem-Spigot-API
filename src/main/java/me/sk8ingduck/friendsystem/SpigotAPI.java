package me.sk8ingduck.friendsystem;

import me.sk8ingduck.friendsystem.command.TestFriend;
import me.sk8ingduck.friendsystem.command.TestParty;
import me.sk8ingduck.friendsystem.config.DBConfig;
import me.sk8ingduck.friendsystem.manager.FriendManager;
import me.sk8ingduck.friendsystem.manager.PartyManager;
import me.sk8ingduck.friendsystem.mysql.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotAPI extends JavaPlugin {

	private static SpigotAPI instance;
	public static final String CHANNEL = "me:friendsystemapi";
	private FriendManager friendManager;
	private PartyManager partyManager;
	private MySQL mysql;

	@Override
	public void onEnable() {
		instance = this;

		DBConfig db = new DBConfig("database.yml", getDataFolder());
		mysql = new MySQL(db.getHost(), db.getPort(), db.getUsername(), db.getPassword(), db.getDatabase());

		friendManager = new FriendManager(mysql);
		partyManager = new PartyManager(mysql);

		getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);

		getCommand("testparty").setExecutor(new TestParty());
		getCommand("testfriend").setExecutor(new TestFriend());
	}

	@Override
	public void onDisable() {
		mysql.close();
		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}

	public static SpigotAPI getInstance() {
		return instance;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public PartyManager getPartyManager() {
		return partyManager;
	}
}
