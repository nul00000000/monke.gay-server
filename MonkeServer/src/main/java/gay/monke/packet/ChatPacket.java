package gay.monke.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Contains data in the struture: {<4:id(uint16)><1:flags(uint8)><80:msg(string)>}
 * @author nul00000000
 *
 */
public class ChatPacket extends Packet {
	
	public static final int length = 85;
	public static final int P_BROADCAST = 0x1;
	public static final int P_PLAYER_STATUS = 0x2;
	
	private static final byte[] prefix = Packet.createPrefix("cp");
	private String message;
	private int flags;
	private int id;
	
	public ChatPacket(int id, int flags, String message) {
		this.id = id;
		this.flags = flags;
		if(message.length() > 40) {
			this.message = message.substring(0, 40);
		} else {
			this.message = message;
			while(this.message.length() < 40) {
				this.message += '\0';
			}
		}
	}
	
	public ChatPacket(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		this.id = buf.getInt();
		this.flags = Byte.toUnsignedInt(buf.get());
		this.message = StandardCharsets.UTF_16BE.decode(buf).toString();
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public int getID() {
		return id;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer buf = ByteBuffer.wrap(dest);
		buf.put(prefix);
		buf.putInt(id);
		buf.put((byte) flags);
		buf.put(message.getBytes(StandardCharsets.UTF_16BE));
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
