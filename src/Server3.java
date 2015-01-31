import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.EOFException;
import java.util.ArrayList;


public abstract class Server3 {

	ServerSocket server;// = new ServerSocket(255);
	public ArrayList<ServerConnection> serverConnections = new ArrayList<ServerConnection>();
	ServerConnection host;
	
	public Server3(int port) throws IOException {
		
		server = new ServerSocket(port);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					for (ServerConnection connection : getServerConnectionsClone()){
						if (!isConnectionStreamEmpty(connection)){
							onNewPacket(getPacketFromConnection(connection), connection);
						}
					}
				}
			}
		}).start();
	}
	
	boolean userNames = true;
	
	public void startServer() throws IOException{
		//This method should be used on a new Thread
		say("Server Running");
		int connectionNumber = 0;
		
//		host = new ServerConnection(server.accept(), "Connection" + connectionNumber++);
		
		while (true){
			//blocks until new connection
			
			ServerConnection connection = new ServerConnection(server.accept());
			
			if (userNames){
				
				new Thread(new GetUsernameRunnable(connection){

					@Override
					public void run() {
						
						ServerConnection connection = this.getConnection();
						
						sendMyPacketToConnection(connection, new MyPacket(ServerEnums.requestUsername));
						
						while (true){
						
							MyPacket packet = getPacketFromConnection(connection);
							if (packet.getPurpose() == ServerEnums.returnUsername){
								String requestedUsername = (String) packet.getObjects()[0];
								
								boolean nameTaken = false;
								
								for (ServerConnection con : serverConnections){
									if (con.getName().equals(requestedUsername)){
										nameTaken = true;
										break;
									}
								}
								
								if (!nameTaken){
									connection.setName(requestedUsername);
									
									serverConnections.add(connection);
									onPlayerJoin(connection);
									say (serverConnections);
									
									for (ServerConnection con : serverConnections){
										System.out.print(con.getName());
									}
									
									//removeBrokenConnections();
									sendMyPacketToAllExcept(new MyPacket(ServerEnums.requestUpdate), connection);
									
									
									return;
								}
								else{
									sendMyPacketToConnection(connection, new MyPacket(ServerEnums.nameTaken));
								}
							}
							else{
								System.err.println("Wrong packet: " + packet.getPurpose());
							}
						}
						
					}
					
				}).start();
				
			}
			else{
				connection.setName("Connection" + connectionNumber++);
				serverConnections.add(connection);
				onPlayerJoin(connection);
				say (serverConnections);
				
				for (ServerConnection con : serverConnections){
					System.out.print(con.getName() + "  ");
				}
				say("");
				
				//removeBrokenConnections();
			}
		}
	}
	
	Thread heartbeater;
	
	public void heartbeatChecker(int seconds){
		
		int interval = seconds*1000;
		
		if (interval >= 0){
			heartbeater = new Thread(new Runnable() {
					@Override
					public void run() {
						while (true){
							
							removeBrokenConnections();
							try {
								Thread.sleep(interval);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
			heartbeater.start();
		}
		else
			heartbeater = null;
	}
	
//	public ServerConnection getHostConnection() {
//		return host;
//	}
//	
	abstract void onPlayerJoin(ServerConnection connection);

	public ArrayList<ServerConnection> getServerConnections(){
		return serverConnections;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ServerConnection> getServerConnectionsClone() {
		//Use to prevent ConcurrentModificationExceptions
		return (ArrayList<ServerConnection>) serverConnections.clone();
	}
	
	public void removeBrokenConnections(){
		sendMyPacketToAllConnections(new MyPacket(ServerEnums.heartbeat, "Are you still there?"));
	}
	
	public ServerConnection findServerConnectionByName(String name){
		for (int i=0; i < serverConnections.size(); i++)
			if (serverConnections.get(i).getName().equals(name))
				return serverConnections.get(i);
		return null;
	}
	
	private boolean isConnectionStreamEmpty(ServerConnection connection){
		try {
			return connection.getSocket().getInputStream().available() == 0;
		}catch (Exception e) {
			e.printStackTrace();
			removeConnection(connection);
			return true;
		}
	}
	
	private MyPacket getPacketFromConnection(ServerConnection connection){
		
		//This is a blocking method (will block until it receives a message)
		//Should use a new Thread in most cases
		try {
			return (MyPacket) connection.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			removeConnection(connection);
		}
		
		return null;
	}
	
	public void sendMyPacketToConnection(String name, MyPacket packet){
		ServerConnection connection = findServerConnectionByName(name);
		sendMyPacketToConnection(connection, packet);
	}
	
	public void sendMyPacketToAllConnections(MyPacket packet){
		
		//This must be used to prevent ConcurrentModificationExceptions
		//This occurs when you change the array while being iterated
		//So this would remove a closed client and then cause an error
		@SuppressWarnings("unchecked")
		ArrayList<ServerConnection> tempArrayForIteration = (ArrayList<ServerConnection>) serverConnections.clone();
		
		for (ServerConnection connection : tempArrayForIteration){
			sendMyPacketToConnection(connection, packet);
		}
	}
	
	public void sendMyPacketToAllExcept(MyPacket packet, ServerConnection sendingConnection) {
		@SuppressWarnings("unchecked")
		ArrayList<ServerConnection> tempArrayForIteration = (ArrayList<ServerConnection>) serverConnections.clone();
		
		for (ServerConnection connection : tempArrayForIteration){
			if (connection != sendingConnection)
				sendMyPacketToConnection(connection, packet);
			else
				if (MainThingy4.debug) say("Did not send to sender");
		}
	}
	
	public void sendMyPacketToConnection(ServerConnection connection, MyPacket packet){
		try {
			connection.sendMyPacket(packet);
		} 
		catch (SocketException | EOFException e){
			System.err.println(e.getMessage());
			removeConnection(connection);
		}
		catch (NullPointerException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeConnection(ServerConnection connection){
		serverConnections.remove(connection);
		onPlayerExit(connection);
		System.err.println(connection.getName() + " has quit");
		say(serverConnections);
	}
	
	abstract void onPlayerExit(ServerConnection connection);

	public void say (Object s){
		System.out.println("Server: " + s);
	}
	
	abstract void onNewPacket(MyPacket myPacket, ServerConnection connection);

}

abstract class GetUsernameRunnable implements Runnable{
	
	private ServerConnection connection;

	public GetUsernameRunnable(ServerConnection connection){
		this.connection = connection;
	}
	
	public ServerConnection getConnection(){
		return connection;
	}
}

class ServerConnection {

	private String name;
	private Socket sock;
	private ObjectInputStream input;
	private CustomOutputStream output;
	
	public ServerConnection(Socket sock){
		this.sock = sock;
		try {
			input = new ObjectInputStream(sock.getInputStream());
			output = new CustomOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerConnection(Socket sock, String name){
		this(sock);
		this.name = name;
	}
	
	public MyPacket readObject() throws ClassNotFoundException, IOException{
		//Should return MyPacket object
		try{
			return (MyPacket) input.readObject();
		}
		catch(Exception e){
			e.printStackTrace();
			return (new MyPacket(ServerEnums.blank));
		}
	}
	
	public void sendMyPacket(MyPacket packet) throws IOException{
		output.sendObject(packet);
	}
	
	public Socket getSocket(){
		return sock;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
