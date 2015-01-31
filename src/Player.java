import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Player implements Serializable{

	private Rectangle rect;
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private boolean leftHeld, rightHeld, upHeld, downHeld, fireHeld;
	private BufferedImage image;

//	public PlayerData(int x, int y, int width, int height) {
//		this(new Rectangle(x,y,width,height));
//	}
	
	public Player(Rectangle rect) {
		this.rect = rect;
		
		try {
			image = ImageIO.read(new File("Images/You/Player.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fire(Point p){
		projectiles.add(new Projectile(p));
	}

	public Point getCoords(){
		return rect.getLocation();
	}
	
	public Rectangle getRect(){
		return rect;
	}
	
	public boolean isLeftHeld() {
		return leftHeld;
	}

	public boolean isRightHeld() {
		return rightHeld;
	}

	public boolean isUpHeld() {
		return upHeld;
	}

	public boolean isDownHeld() {
		return downHeld;
	}
	
	public void setCoords(Point p){
		rect.setLocation(p);
	}
	
	public void setCoords(int x, int y){
		rect.setLocation(x,y);
	}
	
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public void setLeftHeld(boolean leftHeld) {
		this.leftHeld = leftHeld;
	}

	public void setRightHeld(boolean rightHeld) {
		this.rightHeld = rightHeld;
	}

	public void setUpHeld(boolean upHeld) {
		this.upHeld = upHeld;
	}

	public void setDownHeld(boolean downHeld) {
		this.downHeld = downHeld;
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	public void updateProjectiles() {
		for (Projectile p : projectiles)
			p.coords.setLocation(p.coords.x + p.xSpeed, p.coords.y + p.ySpeed);
	}

	public boolean isFireHeld() {
		return fireHeld;
	}

	public void setFireHeld(boolean fireHeld) {
		this.fireHeld = fireHeld;
	}

	public void draw(Graphics2D g) {
//		g.drawImage(image, rect.x, rect.y, null);
//		g.draw(rect);
	}
}

//class 
