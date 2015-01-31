import java.awt.Graphics2D;
import java.awt.Point;

public class Projectile {

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

}
