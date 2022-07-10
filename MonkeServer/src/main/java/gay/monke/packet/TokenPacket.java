package gay.monke.packet;

import java.nio.ByteBuffer;

/**
 * Contains data in the struture: {4:token<int32>,4:id<int32>}
 * @author nulcr
 *
 */
public class TokenPacket extends Packet {//TODO add id into this packet just in case two logins have the same token
	
public static final int length = 8;
	
	private static final byte[] prefix = Packet.createPrefix("tp");
	private int token;
	private int id;
	
	public TokenPacket(int token, int id) {
		this.token = token;
		this.id = id;
	}
	
	public TokenPacket(byte[] info) {
		ByteBuffer bb = ByteBuffer.wrap(info);
		this.token = bb.getInt();
		this.id = bb.getInt();
	}
	
	public int getToken() {
		return token;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer b = ByteBuffer.wrap(dest);
		b.put(prefix);
		b.putInt(token);
		b.putInt(id);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
