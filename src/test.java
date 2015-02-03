import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class test {

	private ServerSocket server;
	private Socket sock;
	private Point p;
	private Socket servSock;
	List<Point> list = new ArrayList<Point>();

	public test() {
		
		try {
			server = new ServerSocket(200);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			sock = new Socket("0", 200);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			servSock = server.accept();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		list.add(new Point (50,50));
		
		sendReceive();
		
		list.add(new Point(450,450));
		
		sendReceive();
		
		list.get(0).setLocation(100, 100);
		
		sendReceive();
	}
	
	public void sendReceive(){
		try {
			new ObjectOutputStream(sock.getOutputStream()).writeObject(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			say(new ObjectInputStream(servSock.getInputStream()).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new test();
	}

}
