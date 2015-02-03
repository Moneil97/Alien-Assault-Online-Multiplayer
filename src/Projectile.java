import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Projectile implements Serializable{

	private Point coords;
	public final int xSpeed = 0, ySpeed = -5;
	private boolean dead = false;
	private final int height = 10, width = 10;
	
	public Projectile(Point p) {
		coords = p;
	}

	public Point getCoords() {
		return coords;
	}

	public void draw(Graphics2D g) {
		g.drawRect(coords.x, coords.y, 10, 10);
	}

	public void update() {
		coords.setLocation(coords.x + xSpeed, coords.y + ySpeed);
		
		if (coords.y + height < 0){
			destoy();
		}
	}
	
	private void destoy() {
		dead = true;
	}
	
	public boolean isDead(){
		return dead;
	}

	public String toString(){
		return "Proj: [" + coords.getX() + ", " + coords.getY() + "]";
	}
	
//	public Projectile clone(){
//		return new Projectile((Point) coords.clone());
//	}

}
