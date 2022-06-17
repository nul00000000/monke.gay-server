package gay.monke.packet;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * idk where to put this so code 0 = skin, code 1 = name, code 2 = player id setting, code 3 = banana info
 * Contains data in the struture: {<2:id(uint16)><1:infocode(uint8)><32:info(byte[32])>}
 * @author nul00000000
 *
 */
public class EntityInfoPacket extends Packet {
	
	public static final int length = 37;
	
	private static final byte[] prefix = Packet.createPrefix("ei");
	private int id;
	private int code;
	private byte[] info;
	
	public EntityInfoPacket(int id, int code, byte[] info) {
		if(info.length > 32) {
			System.err.println("Data of length longer than 32 (" + info.length + ") added to EntityInfoPacket, ignoring last " + (info.length - 32) + " bytes.");
		}
		this.id = id;
		this.code = code;
		this.info = new byte[32];
		System.arraycopy(info, 0, this.info, 0, 32);
	}
	
	/**
	 * Should only be used with UTF-16 text, honestly im not sure how you could mess that up in java
	 * @param id Entity ID
	 * @param code Data Code
	 * @param info UTF-16 string of less than 17 characters
	 */
	public EntityInfoPacket(int id, int code, String info) {
		this.id = id;
		this.code = code;
		this.info = new byte[32];
		try {
			byte[] a = info.getBytes("UTF-16");
			for(int i = 0; i < a.length && i < this.info.length; i++) {
				this.info[i] = a[i];
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public EntityInfoPacket(byte[] info) {
		ByteBuffer bb = ByteBuffer.wrap(info);
		this.id = bb.getInt();
		this.code = bb.get();
		this.info = new byte[32];
		bb.get(this.info);
	}
	
	public int getID() {
		return id;
	}
	
	public int getCode() {
		return code;
	}
	
	public byte[] getInfo() {
		return info;
	}
	
	public String getString() {
		String r = new String(info, StandardCharsets.UTF_16BE);
		return r;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer b = ByteBuffer.wrap(dest);
		b.put(prefix);
		b.putInt(id);
		b.put((byte)code);
		b.put(info);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
