package gay.monke.world;

public class Banana implements Entity {
	
	public float x;
	public float y;
	public float dx;
	public float dy;
	public int age = 0;
	private int id;
	
	private boolean left = false;
	public float angle = 0;
	private World world;
	public Monke thrower;
	
	public boolean canKill;
	public boolean onGround = false;
	
	public Banana(int id, float x, float y, float speed, float angle, Monke thrower, World world, boolean canKill) {
		this.id = id;
		this.thrower = thrower;
		left = Math.cos(-angle) < 0;
		this.x = x;
		this.y = y;
		this.dx = (float) (Math.cos(-angle) * speed);// + (thrower == null ? 0 : thrower.dx);
		this.dy = (float) (Math.sin(-angle) * speed);// + (thrower == null ? 0 : thrower.dy);
		this.canKill = canKill;
		this.angle = 0;
		this.world = world;
	}
	
	public void update() {
		if(!onGround) {
			angle += left ? -0.1f : 0.1f;
		}
		dy += 1.7f;
		dx *= 0.99f;
		dy *= 0.99f;
		x += dx;
		y += dy;
		onGround = y >= -20;
		if(onGround) {
			age++;
			y = -20;
			dy *= -0.5f;
		}
		while(x > world.worldSize / 2) {
			x -= world.worldSize;
		}
		while(x < -world.worldSize / 2) {
			x += world.worldSize;
		}
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
		return angle;
	}

	@Override
	public int getType() {
		return 1;
	}

}
