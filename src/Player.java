import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class Player implements Serializable{

	private Rectangle rect;
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private boolean leftHeld, rightHeld, upHeld, downHeld, fireHeld;

//	public PlayerData(int x, int y, int width, int height) {
//		this(new Rectangle(x,y,width,height));
//	}
	
	public Player(Rectangle rect) {
		this.rect = rect;
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
		for (int i=0; i< projectiles.size(); i++){
			projectiles.get(i).update();
			if (projectiles.get(i).isDead()){
				say("killed projectile");
				projectiles.remove(i);
				i--;
			}
		}
	}

	public void say( Object o) {
		System.out.println(o);
	}

	public boolean isFireHeld() {
		return fireHeld;
	}

	public void setFireHeld(boolean fireHeld) {
		this.fireHeld = fireHeld;
	}

	public void draw(Graphics2D g, BufferedImage image) {
		g.drawImage(image, rect.x, rect.y, null);
		
		for (Projectile p : new ArrayList<Projectile>(projectiles)){
			p.draw(g);
		}
	}

	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}

//	public void setProjectiles(List<Projectile> list) {
//		projectiles = list;
//	}
//
//	private void setKeysHeld(boolean leftHeld2, boolean rightHeld2,
//			boolean upHeld2, boolean downHeld2, boolean fireHeld2) {
//		leftHeld = leftHeld2;
//		rightHeld = rightHeld2;
//		upHeld = upHeld2;
//		downHeld = downHeld2;
//		fireHeld = fireHeld2;
//	}

	public Player clearProjectiles() {
		projectiles.clear();
		return this;
	}
}

