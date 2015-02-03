import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

public class Projectile implements Serializable{

	Point coords;
	public int xSpeed = 0, ySpeed = -5;
	
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
//		System.out.println("updating");
//		System.out.println(coords);
		coords.setLocation(coords.x + xSpeed, coords.y + ySpeed);
//		System.out.println(coords);
	}
	
	public String toString(){
		return "Proj: [" + coords.getX() + ", " + coords.getY() + "]";
	}
	
//	public Projectile clone(){
//		return new Projectile((Point) coords.clone());
//	}

}
