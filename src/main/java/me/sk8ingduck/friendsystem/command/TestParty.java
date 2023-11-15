package me.sk8ingduck.friendsystem.command;

import me.sk8ingduck.friendsystem.SpigotAPI;
import me.sk8ingduck.friendsystem.manager.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestParty implements CommandExecutor, TabCompleter {

	private final PartyManager partyManager;
	public TestParty() {
		partyManager = SpigotAPI.getInstance().getPartyManager();
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			// Subcommands
			List<String> commands = Arrays.asList("create", "disband", "invite", "removeinvite", "add", "kick", "promote", "demote");
			String partialCommand = args[0].toLowerCase();
			for (String cmd : commands) {
				if (cmd.toLowerCase().startsWith(partialCommand)) {
					completions.add(cmd);
				}
			}
		} else if (args.length == 2) {
			if (Arrays.asList("create", "invite", "removeinvite", "add", "kick", "promote", "demote").contains(args[0].toLowerCase())) {
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
			player.sendMessage("Usage: /party <subcommand> [args]");
			return true;
		}

		String subCommand = args[0].toLowerCase();
		switch (subCommand) {
			case "create":
				handleCreateParty(player, args);
				break;
			case "disband":
				handleDisbandParty(player);
				break;
			case "invite":
				handleInvitePlayer(player, args);
				break;
			case "removeinvite":
				handleRemoveInvite(player, args);
				break;
			case "add":
				handleAddPlayer(player, args);
				break;
			case "kick":
				handleKickPlayer(player, args);
				break;
			case "promote":
				handlePromotePlayer(player, args);
				break;
			case "demote":
				handleDemotePlayer(player, args);
				break;
			default:
				player.sendMessage("Unknown subcommand.");
				break;
		}

		return true;
	}

	private void handleCreateParty(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage("Usage: /party create <members>");
			return;
		}
		String[] memberNames = Arrays.copyOfRange(args, 1, args.length);
		List<String> memberUUIDs = new ArrayList<>();
		for (String memberName : memberNames) {
			Player memberPlayer = Bukkit.getPlayer(memberName);
			memberUUIDs.add(memberPlayer.getUniqueId().toString());
		}
		partyManager.createParty(player, memberUUIDs.toArray(new String[0]));
		player.sendMessage("Party created with members: " + Arrays.toString(memberUUIDs.toArray(new String[0])));
	}

	private void handleDisbandParty(Player player) {
		partyManager.disbandParty(player);
		player.sendMessage("Party disbanded.");
	}

	private void handleInvitePlayer(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party invite <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.invitePlayer(player.getUniqueId(), targetPlayer);
		player.sendMessage("Invited " + targetPlayer.getName() + " to the party.");
	}

	private void handleRemoveInvite(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party removeinvite <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.removeInvite(player.getUniqueId(), targetPlayer);
		player.sendMessage("Removed invite for " + targetPlayer.getName());
	}

	private void handleAddPlayer(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party add <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.addPlayer(player.getUniqueId(), targetPlayer);
		player.sendMessage("Added " + targetPlayer.getName() + " to the party.");
	}

	private void handleKickPlayer(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party kick <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.kickPlayer(player.getUniqueId(), targetPlayer);
		player.sendMessage("Kicked " + targetPlayer.getName() + " from the party.");
	}

	private void handlePromotePlayer(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party promote <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.promotePlayer(player.getUniqueId(), targetPlayer);
		player.sendMessage("Promoted " + targetPlayer.getName());
	}

	private void handleDemotePlayer(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage("Usage: /party demote <player>");
			return;
		}
		Player targetPlayer = Bukkit.getPlayer(args[1]);
		if (targetPlayer == null) {
			player.sendMessage("Player not found.");
			return;
		}
		partyManager.demotePlayer(player.getUniqueId(), targetPlayer);
		player.sendMessage("Demoted " + targetPlayer.getName());
	}

}
