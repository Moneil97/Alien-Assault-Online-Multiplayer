import java.io.IOException;
import java.net.BindException;


public class DedicatedServer {

	private Server3 server;
	boolean verification = false;
	//private Client client;
	static boolean debug = false;

	public DedicatedServer(int port) {
		setupServer(port);
	}
	
	private void setupServer(int port) {
		try {
			server = new Server3(port){
				@Override
				void onNewPacket(MyPacket packet, ServerConnection connection) {
					
					ServerEnums purpose = packet.getPurpose();
					if (purpose == ServerEnums.direction){
																			//Purpose		//Player				//direction			//boolean
						this.sendMyPacketToAllExcept(new MyPacket(ServerEnums.direction, connection.getName(), packet.getObjects()[0], packet.getObjects()[1]), connection);
					}
					else if (purpose == ServerEnums.myLocation){
																													//Point
						this.sendMyPacketToAllExcept(new MyPacket(ServerEnums.fixLocation, connection.getName(), packet.getObjects()[0]), connection);
					}
					else if (purpose == ServerEnums.leaveGame){
						this.removeConnection(connection);
					}
					else if (purpose == ServerEnums.returnUpdate){
																													//PlayerData
						this.sendMyPacketToAllExcept(new MyPacket(ServerEnums.joinInfo, connection.getName(), packet.getObjects()[0]), connection);
					}
				}

				@Override
				void onPlayerJoin(ServerConnection connection) {
					say("Welcome: " + connection.getName());
//					if (connection != this.getHostConnection())
//						others.put(connection, defaultRect);
					
					//this.sendMyPacketToConnection(connection, new MyPacket(ServerEnums.joinInfo));
					this.sendMyPacketToAllExcept(new MyPacket(ServerEnums.newPlayer, connection.getName()), connection);
					
				}

				@Override
				void onPlayerExit(ServerConnection connection) {
					say(connection.getName() + " has left the game");
					this.sendMyPacketToAllExcept(new MyPacket(ServerEnums.playerQuit, connection.getName()), connection);
					//others.remove(connection);
				}
			};
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						server.startServer();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
//			server.heartbeatChecker(5);
			
		} 
		catch (BindException e){
			System.err.println("Server already running, you are a client");
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void periodicUpdate(){
//		server.sendMyPacketToAllConnections(new MyPacket(ServerEnums.getUpdate));
//	}

	public static void main(String[] args) {
		new DedicatedServer(25568);
	}

}
