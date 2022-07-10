package gay.monke.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import gay.monke.account.AccountProfile;

/**
 * Contains data in the struture: {<20:username(string{20})><2:level(int16)><2:xp(int16)><2:streak(int16)>}
 * @author nulcr
 *
 */
public class ProfilePacket extends Packet {
	
	public static final int length = 26;
	
	private static final byte[] prefix = Packet.createPrefix("pp");
	
	private String username;
	private short level;
	private short xp;
	private short streak;
	
	public ProfilePacket(String username, short level, short xp, short streak) {
		this.username = username;
		this.level = level;
		this.xp = xp;
		this.streak = streak;
	}
	
	public ProfilePacket(AccountProfile profile) {
		this.username = profile.username;
		this.level = profile.level;
		this.xp = profile.xp;
		this.streak = profile.streak;
	}
	
	public ProfilePacket(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		byte[] u = new byte[20];
		bb.get(u);
		username = new String(u, StandardCharsets.UTF_8).trim();
		this.level = bb.getShort();
		this.xp = bb.getShort();
		this.streak = bb.getShort();
	}

	public String getUsername() {
		return username;
	}

	public int getLevel() {
		return level;
	}

	public int getXp() {
		return xp;
	}

	public int getStreak() {
		return streak;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer b = ByteBuffer.wrap(dest);
		b.put(prefix);
		byte[] u = username.getBytes(StandardCharsets.UTF_8);
		b.put(u);
		b.position(22);
		b.putShort(level);
		b.putShort(xp);
		b.putShort(streak);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
