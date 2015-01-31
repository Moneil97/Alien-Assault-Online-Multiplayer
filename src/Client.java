import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public abstract class Client {

	private Socket sock;
	private CustomOutputStream output;
	private ObjectInputStream input;

	public Client(String ip, int port) {
		try {
			sock = new Socket(ip, port);
			output = new CustomOutputStream(sock.getOutputStream());
		} catch (UnknownHostException e1){
			e1.printStackTrace();
		} catch (IOException e1){
			e1.printStackTrace();
		}
		
		new Thread (new Runnable() {
			@Override
			public void run() {
				try {
					input = new ObjectInputStream(sock.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				while (true){
					onNewPacket(getMyPacketFromServer());
				}
			}
		}).start();
	}
	
	abstract void onNewPacket(MyPacket packet);
	
	public MyPacket getMyPacketFromServer(){
		try {
			return (MyPacket) input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			System.err.println("Lost Connection to the Server");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public void say(Object s){
		System.out.println(s);
	}

	public void sendPacketToServer(MyPacket packet) {
		output.sendObject(packet);
	}
}
