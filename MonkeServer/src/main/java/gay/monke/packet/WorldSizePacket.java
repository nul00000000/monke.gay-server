package gay.monke.packet;

import java.nio.ByteBuffer;

import gay.monke.world.World;

/**
 * Contains data in the struture: {4:width(float)}
 * @author nul00000000
 *
 */
public class WorldSizePacket extends Packet {
	
	public static final byte[] prefix = Packet.createPrefix("ws");
	public static final int length = 4;
	
	private float width;
	
	public WorldSizePacket() {
		this(0.0f);
	}
	
	public WorldSizePacket(World world) {
		this.width = (float) world.worldSize;
	}
	
	public WorldSizePacket(float width) {
		this.width = width;
	}
	
	public WorldSizePacket(byte[] data) {
		ByteBuffer a = ByteBuffer.wrap(data);
		this.width = a.getFloat();
	}
	
	public float getWidth() {
		return width;
	}

	@Override
	public byte[] encode() {
		return this.encode(new byte[length + 2]);
	}

	@Override
	public byte[] encode(byte[] dest) {
		ByteBuffer a = ByteBuffer.wrap(dest);
		a.put(prefix);
		a.putFloat(width);
		return dest;
	}

	@Override
	public byte[] getPrefix() {
		return prefix;
	}

}
