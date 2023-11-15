package me.sk8ingduck.friendsystem.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.sk8ingduck.friendsystem.util.FriendPlayer;
import me.sk8ingduck.friendsystem.util.Party;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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
		String findPartyIdSQL = "SELECT p.partyID, p.leaderUUID FROM party p " +
				"LEFT JOIN party_members m ON p.partyID = m.partyID WHERE m.memberUUID = ?";
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

	public void getParty(UUID playerUUID, Consumer<Party> party) {
		pool.execute(() -> party.accept(getParty(playerUUID)));
	}

	public List<Party> getAllParties() {
		List<Party> parties = new ArrayList<>();
		String findAllPartiesSQL = "SELECT partyID, leaderUUID FROM party";
		String findMembersSQL = "SELECT memberUUID FROM party_members WHERE partyID=?";

		try (Connection con = dataSource.getConnection();
		     PreparedStatement findAllPartiesStmt = con.prepareStatement(findAllPartiesSQL)) {

			ResultSet rsParties = findAllPartiesStmt.executeQuery();

			while (rsParties.next()) {
				int partyID = rsParties.getInt("partyID");
				UUID leaderUUID = UUID.fromString(rsParties.getString("leaderUUID"));
				List<UUID> memberUUIDs = new ArrayList<>();

				try (PreparedStatement findMembersStmt = con.prepareStatement(findMembersSQL)) {
					findMembersStmt.setInt(1, partyID);
					ResultSet rsMembers = findMembersStmt.executeQuery();
					while (rsMembers.next()) {
						memberUUIDs.add(UUID.fromString(rsMembers.getString("memberUUID")));
					}
				}

				Party party = new Party(partyID, leaderUUID, memberUUIDs);
				parties.add(party);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return parties;
	}

	public void getAllParties(Consumer<List<Party>> parties) {
		pool.execute(() -> parties.accept(getAllParties()));
	}

	public FriendPlayer getFriendPlayer(UUID uuid) {
		return getFriendPlayer(uuid.toString());
	}
	public void getFriendPlayer(UUID uuid, Consumer<FriendPlayer> friendPlayer) {
		pool.execute(() -> friendPlayer.accept(getFriendPlayer(uuid)));
	}


	public FriendPlayer getFriendPlayer(String uuidOrName) {
		try (Connection con = dataSource.getConnection();
		     PreparedStatement stmt = con.prepareStatement("SELECT * FROM player WHERE UUID = ? OR name = ?")) {
			stmt.setString(1, uuidOrName);
			stmt.setString(2, uuidOrName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String uuid = rs.getString("UUID");
					String name = rs.getString("name");
					boolean invites = rs.getBoolean("invites");
					boolean notifies = rs.getBoolean("notifies");
					boolean msgs = rs.getBoolean("msgs");
					boolean jump = rs.getBoolean("jump");
					LocalDateTime lastSeen = rs.getTimestamp("lastSeen").toLocalDateTime();
					String status = rs.getString("status");

					HashMap<String, Boolean> friends = new HashMap<>();
					PreparedStatement friendsStmt =
							con.prepareStatement("SELECT friendUUID, isFavourite FROM friend WHERE UUID=?");
					friendsStmt.setString(1, uuid);
					ResultSet friendsRs = friendsStmt.executeQuery();
					while (friendsRs.next()) {
						friends.put(friendsRs.getString("friendUUID"),
								friendsRs.getBoolean("isFavourite"));
					}
					friendsRs.close();
					friendsStmt.close();

					HashMap<String, LocalDateTime> requests = new HashMap<>();
					PreparedStatement requestsStmt = con.prepareStatement("SELECT requestUUID, requestDate " +
							"FROM request WHERE UUID=?");
					requestsStmt.setString(1, uuid);
					ResultSet requestsRs = requestsStmt.executeQuery();
					while (requestsRs.next()) {
						requests.put(requestsRs.getString("requestUUID"),
								requestsRs.getTimestamp("requestDate").toLocalDateTime());
					}
					requestsRs.close();
					requestsStmt.close();

					return new FriendPlayer(uuid, name, invites, notifies,
							msgs, jump, lastSeen, status, friends, requests);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void getFriendPlayer(String uuidOrName, Consumer<FriendPlayer> friendPlayer) {
		pool.execute(() -> friendPlayer.accept(getFriendPlayer(uuidOrName)));
	}
}
