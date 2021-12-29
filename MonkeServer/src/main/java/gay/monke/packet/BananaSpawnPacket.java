package gay.monke.packet;

import java.nio.ByteBuffer;

/**
 * Contains data in the struture: {<4:id(uint32)><4:angle(float)>}
 * @author nul00000000
 *
 */
public class BananaSpawnPacket extends Packet {
	
	private static final byte[] prefix = Packet.createPrefix("bs");
	public static final int length = 8;
	
	private int id;
	private float angle;
	
	public BananaSpawnPacket(int id, float angle) {
		this.id = id;
		this.angle = angle;
	}
	
	public BananaSpawnPacket(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		this.id = bb.getInt();
		this.angle = bb.getFloat();
	}
	
	public int getID() {
		return id;
	}
	
	public float getAngle() {
		return angle;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer bb = ByteBuffer.wrap(dest);
		bb.put(prefix);
		bb.putInt(id);
		bb.putFloat(angle);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}