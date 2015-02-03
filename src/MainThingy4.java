import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainThingy4 extends JFrame{

	private static final long serialVersionUID = 1L;
	public static final int gameHeight = 400, gameWidth = 400;
	private int port = 25568;
	HashMap<String, Player> others = new HashMap<String, Player>();
	private Rectangle defaultRect = new Rectangle(50,50,50,50);
	public Player you = new Player((Rectangle) defaultRect.clone());
	private Client client;
	static boolean debug = false;
	public StringBuilder username = new StringBuilder("default");
	private String ip = "0";
	//public Border border = new Border(0,400,0,400);
	
	public MainThingy4() {
		//attempt to set up server
		new DedicatedServer(port);
		setupClient();
		setupKeys();
		setupWindowListener();
		setupFrame();
		setupRepaint();
		setupGameLoop();
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				while (true){
					//Fix location once per second/2
					//Probably put this inside another thread later to save on processor power
					client.sendPacketToServer(new MyPacket(ServerEnums.myLocation, you.getCoords()));
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	UpdatesAndFrames uaf = new UpdatesAndFrames();
	
	private void setupGameLoop() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final int updateTarget = 20;
				final int updateSleep = 1000/updateTarget;//, updateTarget = 1000 / updateSleep;
				final double GAME_HERTZ = updateTarget;
				final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
				double lastUpdateTime = System.nanoTime();

				uaf.setStartTime();

				while (true) {
					double now = System.nanoTime();
					while (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {

						gameUpdate();
						
						lastUpdateTime += TIME_BETWEEN_UPDATES;
						uaf.addGameUpdate();
						uaf.update();
						try {
							Thread.sleep(updateSleep / 2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
						lastUpdateTime = now - TIME_BETWEEN_UPDATES;
					}
				}
			}
		}).start();
	}
	
	protected void gameUpdate() {
		if (you.isLeftHeld()){
			you.setCoords(you.getCoords().x - 5, you.getCoords().y);
		}
		if (you.isRightHeld()){
			you.setCoords(you.getCoords().x + 5, you.getCoords().y);
		}
		if (you.isUpHeld()){
			you.setCoords(you.getCoords().x, you.getCoords().y - 5);
		}
		if (you.isDownHeld()){
			you.setCoords(you.getCoords().x, you.getCoords().y + 5);
		}
		if (you.isFireHeld()){
			you.fire(you.getCoords());
			client.sendPacketToServer(new MyPacket(ServerEnums.newProjectile, you.getProjectiles().get(you.getProjectiles().size()-1)));
		}
		
		for (Entry<String, Player> other : others.entrySet()){
			Player player = other.getValue();
			if (player.isLeftHeld()){
				player.setCoords(player.getCoords().x - 5, player.getCoords().y);
			}
			if (player.isRightHeld()){
				player.setCoords(player.getCoords().x + 5, player.getCoords().y);
			}
			if (player.isUpHeld()){
				player.setCoords(player.getCoords().x, player.getCoords().y - 5);
			}
			if (player.isDownHeld()){
				player.setCoords(player.getCoords().x, player.getCoords().y + 5);
			}
			
			player.updateProjectiles();
		}
		
		you.updateProjectiles();
		
		if (debug) System.out.println(you);
	}

	

	private void setupClient() {
		client = new Client(ip , port){
	
			@Override
			void onNewPacket(MyPacket packet) {
				if (MainThingy4.debug) say(packet);
				
				ServerEnums purpose = packet.getPurpose();
				
				if (purpose == ServerEnums.direction){
					String sender = (String) packet.getObjects()[0];
					ServerEnums direction = (ServerEnums) packet.getObjects()[1];
					boolean held = (boolean) packet.getObjects()[2];
					
					if (direction == ServerEnums.left){
						others.get(sender).setLeftHeld(held);
					}
					else if (direction == ServerEnums.right){
						others.get(sender).setRightHeld(held);
					}
					else if (direction == ServerEnums.up){
						others.get(sender).setUpHeld(held);
					}
					else if (direction == ServerEnums.down){
						others.get(sender).setDownHeld(held);
					}
				}
				else if (purpose == ServerEnums.fixLocation){
					others.get(packet.getObjects()[0]).setCoords((Point) packet.getObjects()[1]);
					if (debug) say(others);
				}
				else if (purpose == ServerEnums.heartbeat){
					if (MainThingy4.debug) say("Server Checked Heartbeat");
				}
				else if (purpose == ServerEnums.newPlayer){
					String name = (String) packet.getObjects()[0];
					say("New player: " + name +  " has joined");
					others.put(name, new Player((Rectangle) defaultRect.clone()));
					if (debug) say(others);
				}
				else if (purpose == ServerEnums.playerQuit){
					String name = (String) packet.getObjects()[0];
					say("Removed player: " + name);
					others.remove(name);
				}
				else if (purpose == ServerEnums.joinInfo){
					say("Welcome");
					say(((Player) packet.getObjects()[1]).getProjectiles());
					others.put((String) packet.getObjects()[0], (Player) packet.getObjects()[1]);
				}
				else if (purpose == ServerEnums.requestUpdate){
//					say("Update requested, sending: " + you.getProjectiles());
					client.sendPacketToServer(new MyPacket(ServerEnums.returnUpdate, you.clearProjectiles()));
				}
				else if (purpose == ServerEnums.requestUsername){
					client.sendPacketToServer(new MyPacket(ServerEnums.returnUsername, username.toString()));
				}
				else if (purpose == ServerEnums.nameTaken){
					username.replace(0, username.length(), JOptionPane.showInputDialog("Username: " + username.toString() + " is taken\nPlease try a new one:"));
					client.sendPacketToServer(new MyPacket(ServerEnums.returnUsername, username.toString()));
				}
				else if (purpose == ServerEnums.newProjectile){
					//say("new proj");
					others.get(packet.getObjects()[0]).addProjectile((Projectile) packet.getObjects()[1]);
				}
			}
		};
	}
	
//	List<Projectile> otherPros = new ArrayList<Projectile>();

	private void setupKeys() {
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e){}
			@Override
			public void keyReleased(KeyEvent e){
				
				int key = e.getKeyCode();
				
				if (key == KeyEvent.VK_LEFT || key == 'A'){
					you.setLeftHeld(false);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.left, you.isLeftHeld()));
				}
				else if (key == KeyEvent.VK_RIGHT || key == 'D'){
					you.setRightHeld(false);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.right, you.isRightHeld()));
				}
				else if (key == KeyEvent.VK_UP || key == 'W'){
					you.setUpHeld(false);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.up, you.isUpHeld()));
				}
				else if (key == KeyEvent.VK_DOWN || key == 'S'){
					you.setDownHeld(false);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.down, you.isDownHeld()));
				}
				if (key == KeyEvent.VK_SPACE){
					you.setFireHeld(false);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				int key = e.getKeyCode();
				
				if (key == KeyEvent.VK_LEFT || key == 'A'){
					you.setLeftHeld(true);
															//Purpose				//direction		//boolean
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.left, you.isLeftHeld()));
				}
				else if (key == KeyEvent.VK_RIGHT || key == 'D'){
					you.setRightHeld(true);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.right, you.isRightHeld()));
				}
				else if (key == KeyEvent.VK_UP || key == 'W'){
					you.setUpHeld(true);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.up, you.isUpHeld()));
				}
				else if (key == KeyEvent.VK_DOWN || key == 'S'){
					you.setDownHeld(true);
					client.sendPacketToServer(new MyPacket(ServerEnums.direction, ServerEnums.down, you.isDownHeld()));
				}
				if (key == KeyEvent.VK_SPACE){
					you.setFireHeld(true);
				}
			}
		});
		

	}

	private void setupWindowListener(){
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.err.println("Closing...");
				client.sendPacketToServer(new MyPacket(ServerEnums.leaveGame));
				System.exit(0);
			}
			
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}

	private void setupFrame() {
		add(new Screen(this));
		setSize(400,400);
		setBackground(Color.black);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setupRepaint(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					repaint();
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void say(Object s){
		System.out.println(s);
	}
	
	public static void main(String[] args) {
		new MainThingy4();
	}
}
