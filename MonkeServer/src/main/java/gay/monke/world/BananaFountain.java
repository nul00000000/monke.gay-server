package gay.monke.world;

import java.util.Random;

public class BananaFountain {
	
	public int bananas;
	private int timer = 0;
	private World world;
	private Random random;
	private float x;
	private float y;
	
	public BananaFountain(float x, float y, int bananas, World world, Random random) {
		this.bananas = bananas;
		this.world = world;
		this.random = random;
		this.x = x;
		this.y = y;
	}
	
	public void update() {
		if(timer % 10 == 0 && bananas > 0) {
			bananas--;
			Banana b = world.addBanana(45.0f, x, y, (float) (random.nextGaussian() * 0.1 + Math.PI / 2), null, false);
			b.x = x;
			b.y = y;
		}
		timer++;
	}

}
