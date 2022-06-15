package gay.monke.world;

import java.util.Random;

public class Monke implements Entity {
	
	public float x = 0;
	public float y = 0;
	public float dx = 0;
	public float dy = 0;
	public boolean left = true;
	public int ticks = 0;
	public boolean canThrow = false;
	private int id;
		
	public int bananas = 10;
	public World world;
	protected boolean onGround = true;
	public int skin;
	public String name; 
	
	private Random random;

	public Monke(int id, float x, World world, Random random) {
		this.world = world;
		this.x = x;
		this.y = 0;
		this.random = random;
		this.id = id;
		this.name = world.getRandomName();
		if(random != null) {
			this.skin = random.nextInt(14);
		} else {
			this.skin = 0;
		}
	}
	
	public Monke(int id, float x, float y, World world, Random random) {
		this.world = world;
		this.random = random;
		this.x = x;
		this.y = y;
		this.id = id;
		this.name = world.getRandomName();
		if(random != null) {
			this.skin = random.nextInt(14);
		} else {
			this.skin = 0;
		}
	}
	
	public float getWidth() {
		return Math.max(40.0f, bananas * 4.0f);
	}
	
	public float getHeight() {
		return Math.max(20.0f, bananas * 2.0f);
	}
	
	public void update() {
		if(ticks > 150) {
			canThrow = true;
		}
		dy += 3.0f;
		if(random.nextFloat() < 0.006 && onGround) {
			dy -= 5.0f * Math.sqrt(this.getHeight());
		}
		if(random.nextBoolean()) {
			dx -= 1.0f;
		}
		if(random.nextBoolean()) {
			dx += 1.0f;
		}
		if(bananas > 0 && random.nextFloat() < 0.001) {
			bananas--;
			world.addBanana(30.0f, x, y - getHeight() / 2, (float) ((float) random.nextFloat() * Math.PI), this);
			canThrow = true;
		}
		dy *= 0.99f;
		dx *= onGround ? 0.95f : 0.99f;
		left = dx < 0;
		y += dy;
		x += dx;
		onGround = y >= 0;
		if(onGround) {
			y = 0;
			dy = 0;
		}
		while(x > world.worldSize / 2) {
			x -= world.worldSize;
		}
		while(x < -world.worldSize) {
			x += world.worldSize;
		}
		ticks++;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getRot() {
		return 0;
	}

	@Override
	public int getType() {
		return 0;
	}

}
