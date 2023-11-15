package me.sk8ingduck.friendsystem.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is a helper class to represent a party.
 * <p>
 * This class provides functionalities to:
 * - Retrieve the party's unique identifier.
 * - Get the UUID of the party leader.
 * - List of all member UUIDs in the party.
 * - Obtain a combined list of the leader and all members.
 */
public class Party {
	private final int partyId;
	private final UUID leaderUUID;
	private final List<UUID> memberUUIDs;

	public Party(int partyId, UUID leaderUUID, List<UUID> memberUUIDs) {
		this.partyId = partyId;
		this.leaderUUID = leaderUUID;
		this.memberUUIDs = memberUUIDs;
	}

	public int getPartyId() {
		return partyId;
	}

	public UUID getLeaderUUID() {
		return leaderUUID;
	}

	public List<UUID> getMemberUUIDs() {
		return memberUUIDs;
	}

	public List<UUID> getAllMembers() {
		List<UUID> allMembers = new ArrayList<>();
		allMembers.add(leaderUUID);
		allMembers.addAll(memberUUIDs);
		return allMembers;
	}
}
