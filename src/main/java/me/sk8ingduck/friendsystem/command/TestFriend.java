package me.sk8ingduck.friendsystem.command;

import me.sk8ingduck.friendsystem.SpigotAPI;
import me.sk8ingduck.friendsystem.manager.FriendManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFriend implements CommandExecutor, TabCompleter {

	private final FriendManager friendManager;

	public TestFriend() {
		this.friendManager = SpigotAPI.getInstance().getFriendManager();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			// Subcommands
			List<String> commands = Arrays.asList("toggleinvites", "togglemsgs", "togglejumping", "togglenotifies",
					"updatestatus", "addfriendrequest", "acceptfriendrequest", "denyfriendrequest", "removefriend",
					"togglefavourite");
			String partialCommand = args[0].toLowerCase();
			for (String cmd : commands) {
				if (cmd.toLowerCase().startsWith(partialCommand)) {
					completions.add(cmd);
				}
			}
		} else if (args.length == 2) {
			if (Arrays.asList("addfriendrequest", "acceptfriendrequest", "denyfriendrequest",
					"removefriend", "togglefavourite").contains(args[0].toLowerCase())) {
				String partialPlayerName = args[1].toLowerCase();
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.getName().toLowerCase().startsWith(partialPlayerName)) {
						completions.add(player.getName());
					}
				});
			}
		}
		return completions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage("Usage: /friend <subcommand> [args]");
			return true;
		}

		String subCommand = args[0].toLowerCase();
		switch (subCommand) {
			case "toggleinvites":
				friendManager.toggleInvites(player);
				player.sendMessage("Friend invites toggled.");
				break;
			case "togglemsgs":
				friendManager.toggleMsgs(player);
				player.sendMessage("Friend messages toggled.");
				break;
			case "togglejumping":
				friendManager.toggleJumping(player);
				player.sendMessage("Friend jumping toggled.");
				break;
			case "togglenotifies":
				friendManager.toggleNotifies(player);
				player.sendMessage("Friend notifications toggled.");
				break;
			case "updatestatus":
				if (args.length < 2) {
					player.sendMessage("Usage: /friend updatestatus <status>");
					return true;
				}
				String status = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				friendManager.updateStatus(player, status);
				player.sendMessage("Status updated to: " + status);
				break;
			case "addfriendrequest":
				handleAddFriendRequest(player, args);
				break;
			case "acceptfriendrequest":
				handleAcceptFriendRequest(player, args);
				break;
			case "denyfriendrequest":
				handleDenyFriendRequest(player, args);
				break;
			case "removefriend":
				handleRemoveFriend(player, args);
				break;
			case "togglefavourite":
				handleToggleFavouriteFriend(player, args);
				break;
			default:
				player.sendMessage("Unknown subcommand.");
				break;
		}

		return true;
	}

	private void handleAddFriendRequest(Player sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Usage: /friend addfriendrequest <player>");
			return;
		}
		Player receiver = Bukkit.getPlayer(args[1]);
		if (receiver == null) {
			sender.sendMessage("Player not found.");
			return;
		}
		friendManager.addFriendRequest(sender, receiver.getUniqueId());
		sender.sendMessage("Friend request sent to " + receiver.getName() + ".");
	}

	private void handleAcceptFriendRequest(Player sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Usage: /friend acceptfriendrequest <player>");
			return;
		}
		Player requester = Bukkit.getPlayer(args[1]);
		if (requester == null) {
			sender.sendMessage("Player not found.");
			return;
		}
		friendManager.acceptFriendRequest(sender, requester.getUniqueId());
		sender.sendMessage("Friend request from " + requester.getName() + " accepted.");
	}

	private void handleDenyFriendRequest(Player sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Usage: /friend denyfriendrequest <player>");
			return;
		}
		Player requester = Bukkit.getPlayer(args[1]);
		if (requester == null) {
			sender.sendMessage("Player not found.");
			return;
		}
		friendManager.denyFriendRequest(sender, requester.getUniqueId());
		sender.sendMessage("Friend request from " + requester.getName() + " denied.");
	}

	private void handleRemoveFriend(Player sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Usage: /friend removefriend <player>");
			return;
		}
		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			sender.sendMessage("Player not found.");
			return;
		}
		friendManager.removeFriend(sender, target.getUniqueId());
		sender.sendMessage("Removed " + target.getName() + " from friends.");
	}

	private void handleToggleFavouriteFriend(Player sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Usage: /friend togglefavourite <player>");
			return;
		}
		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			sender.sendMessage("Player not found.");
			return;
		}
		friendManager.toggleFavouriteFriend(sender, target.getUniqueId());
		sender.sendMessage("Toggled favourite for " + target.getName() + ".");
	}

}