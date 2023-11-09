package me.sk8ingduck.friendsystem.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.sk8ingduck.friendsystem.util.Party;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQL {

	private final HikariDataSource dataSource;
	private final ExecutorService pool = Executors.newCachedThreadPool();

	public MySQL(String host, int port, String username, String password, String database) {

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true");
		config.setUsername(username);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}

	public void close() {
		dataSource.close();
	}

	public Party getParty(UUID playerUUID) {
		String findPartyIdSQL = "SELECT p.partyID, p.leaderUUID FROM party p LEFT JOIN party_members m ON p.partyID = m.partyID WHERE m.memberUUID = ?";
		String findMembersSQL = "SELECT memberUUID FROM party_members WHERE partyID=?";
		Party party = null;

		try (Connection con = dataSource.getConnection();
		     PreparedStatement findPartyIdStmt = con.prepareStatement(findPartyIdSQL)) {

			findPartyIdStmt.setString(1, playerUUID.toString());
			ResultSet rsPartyId = findPartyIdStmt.executeQuery();

			if (rsPartyId.next()) {
				int partyID = rsPartyId.getInt("partyID");
				UUID leaderUUID = UUID.fromString(rsPartyId.getString("leaderUUID"));
				List<UUID> memberUUIDs = new ArrayList<>();

				try (PreparedStatement findMembersStmt = con.prepareStatement(findMembersSQL)) {
					findMembersStmt.setInt(1, partyID);
					ResultSet rsMembers = findMembersStmt.executeQuery();
					while (rsMembers.next()) {
						memberUUIDs.add(UUID.fromString(rsMembers.getString("memberUUID")));
					}
				}
				party = new Party(partyID, leaderUUID, memberUUIDs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return party;
	}
}
