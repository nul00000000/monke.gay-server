package gay.monke.world;

import java.util.Random;

public class BananaFountain {
	
	public static final float BANANA_SPEED = 45.0f;
	
	public int bananas;
	private int timer = 0;
	private World world;
	private Random random;
	private float x;
	private float y;
	private Monke monke;
	
	public BananaFountain(float x, float y, int bananas, World world, Monke monke, Random random) {
		this.bananas = bananas;
		this.world = world;
		this.random = random;
		this.x = x;
		this.y = y;
		this.monke = monke;
	}
	
	public void update() {
		if(timer % 10 == 0 && bananas > 0) {
			bananas--;
			Banana b = world.addFountainBanana(x, y, (float) (random.nextGaussian() * 0.1 + Math.PI / 2), monke);
			b.x = x;
			b.y = y;
		}
		timer++;
	}

}
