package me.sk8ingduck.friendsystem.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.sk8ingduck.friendsystem.SpigotAPI;
import me.sk8ingduck.friendsystem.mysql.MySQL;
import me.sk8ingduck.friendsystem.util.FriendPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * FriendManager handles friend interactions, acting as a bridge between the Spigot plugin and BungeeCord server.
 * <p>
 * Main functions:
 * - Fetching friend data from MySQL.
 * - Sending plugin messages to BungeeCord for friend-related actions (e.g. adding friends, toggling settings, ...).
 *   BungeeCord then executes these changes.
 * <p>
 * FriendManager offers both synchronous and asynchronous methods for retrieving FriendPlayer data.
 * Asynchronous usage is recommended to minimize server lag.
 */
public class FriendManager {

	private final MySQL mySQL;

	public FriendManager(MySQL mySQL) {
		this.mySQL = mySQL;
	}

	public FriendPlayer getFriendPlayer(String name) {
		return mySQL.getFriendPlayer(name);
	}

	public void getFriendPlayer(String name, Consumer<FriendPlayer> friendPlayer) {
		mySQL.getFriendPlayer(name, friendPlayer);
	}

	public FriendPlayer getFriendPlayer(UUID uuid) {
		return mySQL.getFriendPlayer(uuid);
	}

	public void getFriendPlayer(UUID uuid, Consumer<FriendPlayer> friendPlayer) {
		mySQL.getFriendPlayer(uuid, friendPlayer);
	}

	public void toggleInvites(Player player) {
		sendPluginMessage(player, FriendAction.TOGGLE_INVITES, player.getUniqueId().toString());
	}

	public void toggleMsgs(Player player) {
		sendPluginMessage(player, FriendAction.TOGGLE_MSGS, player.getUniqueId().toString());
	}

	public void toggleJumping(Player player) {
		sendPluginMessage(player, FriendAction.TOGGLE_JUMPING, player.getUniqueId().toString());
	}

	public void toggleNotifies(Player player) {
		sendPluginMessage(player, FriendAction.TOGGLE_NOTIFIES, player.getUniqueId().toString());
	}

	public void updateStatus(Player player, String status) {
		sendPluginMessage(player, FriendAction.UPDATE_STATUS, player.getUniqueId().toString(), status);
	}

	public void addFriendRequest(Player sender, UUID receiver) {
		sendPluginMessage(sender, FriendAction.ADD_FRIEND_REQUEST,
				sender.getUniqueId().toString(), receiver.toString());
	}

	public void acceptFriendRequest(Player sender, UUID receiver) {
		sendPluginMessage(sender, FriendAction.ACCEPT_FRIEND_REQUEST,
				sender.getUniqueId().toString(), receiver.toString());
	}

	public void denyFriendRequest(Player sender, UUID receiver) {
		sendPluginMessage(sender, FriendAction.DENY_FRIEND_REQUEST,
				sender.getUniqueId().toString(), receiver.toString());
	}

	public void removeFriend(Player sender, UUID receiver) {
		sendPluginMessage(sender, FriendAction.REMOVE_FRIEND,
				sender.getUniqueId().toString(), receiver.toString());
	}

	public void toggleFavouriteFriend(Player sender, UUID receiver) {
		sendPluginMessage(sender, FriendAction.TOGGLE_FAVOURITE_FRIEND,
				sender.getUniqueId().toString(), receiver.toString());
	}

	private void sendPluginMessage(Player player, FriendAction action, String... args) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(action.name());
		for (String arg : args) {
			out.writeUTF(arg);
		}
		player.sendPluginMessage(SpigotAPI.getInstance(), SpigotAPI.CHANNEL, out.toByteArray());
	}

	enum FriendAction {
		TOGGLE_INVITES,
		TOGGLE_MSGS,
		TOGGLE_JUMPING,
		TOGGLE_NOTIFIES,
		UPDATE_STATUS,
		ADD_FRIEND_REQUEST,
		ACCEPT_FRIEND_REQUEST,
		DENY_FRIEND_REQUEST,
		REMOVE_FRIEND,
		TOGGLE_FAVOURITE_FRIEND
	}
}
