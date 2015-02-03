import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Screen extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Player you;
	private HashMap<String,Player> others;
	private StringBuilder username;
	private MainThingy4 parent;
	private BufferedImage image;
	
	
	public Screen(MainThingy4 parent) {
		this.parent = parent;
		this.you = parent.you;
		this.others = parent.others;
		this.username = parent.username;
		
		try {
			image = ImageIO.read(new File("Images/You/Player.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        draw(g2);
        
        g2.dispose();
	}

	private void draw(Graphics2D g) {
		
		//Draw other players
		g.setColor(Color.green);
		for (Entry<String, Player> entry : others.entrySet()){
			Player other = entry.getValue();
			other.draw(g, image);
		}
		
		//Draw you
		g.setColor(Color.blue);
		you.draw(g, image);
		
		g.setColor(Color.magenta);
		
		//Draw Players List
		int y = 0;
		g.drawString(username.toString(), 300, y+=20);
		
		for (String s : others.keySet()){
			g.drawString(s, 300, y+=20);
		}
		
		g.drawString(parent.uaf.getUPS() + "", 20, 20);
	}
	
	public void say(Object s){
		System.out.println(s);
	}

}
