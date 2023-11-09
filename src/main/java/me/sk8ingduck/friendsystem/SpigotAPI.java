package me.sk8ingduck.friendsystem;

import me.sk8ingduck.friendsystem.config.DBConfig;
import me.sk8ingduck.friendsystem.mysql.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotAPI extends JavaPlugin {

	private static SpigotAPI instance;

	private MySQL mysql;

	@Override
	public void onEnable() {
		instance = this;

		DBConfig dbConfig = new DBConfig("database.yml", getDataFolder());
		mysql = new MySQL(dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUsername(), dbConfig.getPassword(), dbConfig.getDatabase());
	}

	@Override
	public void onDisable() {
		mysql.close();
	}

	public static SpigotAPI getInstance() {
		return instance;
	}

	public MySQL getMysql() {
		return mysql;
	}
}
