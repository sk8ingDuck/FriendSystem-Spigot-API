package me.sk8ingduck.friendsystem.util;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * This is a helper class to represent a friend.
 * <p>
 * This class provides functionalities to:
 * - UUID and name: Unique identifiers for the friend.
 * - Friends: A map storing UUIDs of other friends and a boolean flag indicating if they are marked as favorites.
 * - Requests: A map of friend request UUIDs along with the time they were made (using LocalDateTime).
 * - Permissions: Booleans indicating whether the friend allows invites, messages, jumping, and notifications.
 * - Last Seen: The last time the friend was seen online, represented using LocalDateTime.
 * - Status: A string representing the current status of the friend.
 */
public class FriendPlayer {

	private final String uuid;
	private final String name;
	private final HashMap<String, Boolean> friends; //the boolean indicates if the friend is marked as favourite
	private final HashMap<String, LocalDateTime> requests;
	private final boolean invitesAllowed;
	private final boolean msgsAllowed;
	private final boolean jumpingAllowed;
	private final boolean notifiesAllowed;
	private final LocalDateTime lastSeen;
	private final String status;

	public FriendPlayer(String uuid, String name,
	                    boolean invitesAllowed, boolean notifiesAllowed, boolean msgsAllowed, boolean jumpingAllowed,
	                    LocalDateTime lastSeen, String status,
	                    HashMap<String, Boolean> friends, HashMap<String, LocalDateTime> requests) {
		this.uuid = uuid;
		this.name = name;
		this.invitesAllowed = invitesAllowed;
		this.notifiesAllowed = notifiesAllowed;
		this.msgsAllowed = msgsAllowed;
		this.jumpingAllowed = jumpingAllowed;
		this.lastSeen = lastSeen;
		this.status = status;
		this.friends = friends;
		this.requests = requests;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Boolean> getFriends() {
		return friends;
	}

	public HashMap<String, LocalDateTime> getRequests() {
		return requests;
	}

	public boolean isInvitesAllowed() {
		return invitesAllowed;
	}

	public boolean isMsgsAllowed() {
		return msgsAllowed;
	}

	public boolean isJumpingAllowed() {
		return jumpingAllowed;
	}

	public boolean isNotifiesAllowed() {
		return notifiesAllowed;
	}

	public LocalDateTime getLastSeen() {
		return lastSeen;
	}

	public String getStatus() {
		return status;
	}
}
