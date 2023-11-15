package me.sk8ingduck.friendsystem.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.sk8ingduck.friendsystem.SpigotAPI;
import me.sk8ingduck.friendsystem.mysql.MySQL;
import me.sk8ingduck.friendsystem.util.Party;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * PartyManager manages party interactions, acting as a bridge between the Spigot plugin and BungeeCord server.
 * <p>
 * Main functions:
 * - Retrieving party data from MySQL.
 * - Sending plugin messages to BungeeCord for party-related actions (e.g., creating parties, inviting members, ...).
 * BungeeCord then executes these changes.
 * <p>
 * PartyManager offers both synchronous and asynchronous methods for handling Party objects.
 * Asynchronous usage is recommended to minimize server lag.
 */
public class PartyManager {

	private final MySQL mySQL;

	public PartyManager(MySQL mySQL) {
		this.mySQL = mySQL;
	}

	public Party getParty(UUID playerUUID) {
		return mySQL.getParty(playerUUID);
	}

	public void getParty(UUID playerUUID, Consumer<Party> party) {
		mySQL.getParty(playerUUID, party);
	}

	public List<Party> getAllParties() {
		return mySQL.getAllParties();
	}

	public void getAllParties(Consumer<List<Party>> parties) {
		mySQL.getAllParties(parties);
	}

	public void createParty(Player leader, String... memberUUIDs) {
		sendPluginMessage(leader, PartyAction.CREATE,
				leader.getUniqueId().toString(), String.join(",", memberUUIDs));
	}

	public void disbandParty(Player leader) {
		sendPluginMessage(leader, PartyAction.DISBAND, leader.getUniqueId().toString());
	}

	public void invitePlayer(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.ADD_INVITE, leader.toString(), player.getUniqueId().toString());
	}

	public void removeInvite(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.REMOVE_INVITE, leader.toString(), player.getUniqueId().toString());
	}

	public void addPlayer(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.ADD_PLAYER, leader.toString(), player.getUniqueId().toString());
	}

	public void kickPlayer(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.KICK_PLAYER, leader.toString(), player.getUniqueId().toString());
	}

	public void promotePlayer(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.PROMOTE_PLAYER, leader.toString(), player.getUniqueId().toString());
	}

	public void demotePlayer(UUID leader, Player player) {
		sendPluginMessage(player, PartyAction.DEMOTE_PLAYER, leader.toString(), player.getUniqueId().toString());
	}

	private void sendPluginMessage(Player player, PartyAction channel, String... args) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(channel.name());
		for (String arg : args) {
			out.writeUTF(arg);
		}
		player.sendPluginMessage(SpigotAPI.getInstance(), SpigotAPI.CHANNEL, out.toByteArray());
	}

	enum PartyAction {
		CREATE,
		ADD_INVITE,
		REMOVE_INVITE,
		ADD_PLAYER,
		KICK_PLAYER,
		PROMOTE_PLAYER,
		DEMOTE_PLAYER,
		DISBAND
	}
}
