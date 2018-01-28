import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ChatServer{

	final int SERVER_IP = 2000;

	public ArrayList<User> users;
	public ArrayList<Message> messageQueue;

	public boolean isRunning;

	//read data
	public Thread serverReader;
	public Runnable reader;
	public DatagramSocket readingSocket;
	public byte[] readArray;

	//send data
	public Thread serverWriter;
	public Runnable writer;
	private  DatagramSocket sendingSocket;
	private  InetAddress ip;

	//Server User
	User server = new User(2000,"Server");
	
	public ChatServer(){
		users = new ArrayList<User>();
		messageQueue = new ArrayList<Message>();
		start();
	}

	synchronized void start(){
		reader = () -> read();
		writer = () -> write();

		serverWriter = new Thread(writer);
		serverReader = new Thread(reader);

		isRunning = true;

		try{
			sendingSocket = new DatagramSocket();  
			ip = InetAddress.getByName("127.0.0.1");
			readingSocket = new DatagramSocket(SERVER_IP);  
			readArray = new byte[1024];  
		}catch(Exception e) {  e.printStackTrace(); }

		serverWriter.start();
		serverReader.start();
	}

	//reading data

	void read(){
		while(isRunning){
			try{
				DatagramPacket readPacket = new DatagramPacket(readArray, 1024);  
				readingSocket.receive(readPacket);  
				String readData = new String(readPacket.getData(), 0, readPacket.getLength()); 
				processRead(readData);
				//sleeping thread
				Thread.sleep(1);

			}catch(Exception e) {  e.printStackTrace(); }


		}
	}

	void processRead(String readData){
		DataPackageTranslator data = new DataPackageTranslator(readData);
		if(isNewConnection(data.port)){
			//is a new user
			users.add(new User(data.port, data.userName));
		}
		//continue with message
		//get user with port
		for(int index = 0; index < users.size(); index++){
			User u = users.get(index);
			if(u.PORT == data.port){
				//found user
				Message message = new Message(data.message,u);
				
				//see if notification
				if(message.message.equals(DataPackageTranslator.exitCode)){
					message = new Message(u.userName + " has logged off", server);
					users.remove(index);
					index--;
				}else if(message.message.equals(DataPackageTranslator.enterCode)){
					message = new Message(u.userName + " has logged on", server);
				}
				
				
				messageQueue.add(message);

				//stop search
				break;
			}
		}
	}

	//checking for new users
	boolean isNewConnection(int port){
		for(int index = 0; index < users.size(); index++){
			User u = users.get(index);
			if(u.PORT == port){
				return false;
			}
		}
		return true;
	}

	//writing data


	void write(){
		while(isRunning){
			try{ 
				//sending messageQueues
			   //relaying messages from server to users

					for(int index  = 0; index < messageQueue.size(); index++){
						Message m = messageQueue.get(0);

						for(int index2 = 0; index2 < users.size(); index2++){
							User u = users.get(index2);

							String builtPackage = m.user.PORT + DataPackageTranslator.nameParseCode + m.user.userName + DataPackageTranslator.messageParseCode + m.message;
							DatagramPacket writePacket = new DatagramPacket( builtPackage.getBytes(), builtPackage.length(), ip,  u.PORT);  
							sendingSocket.send(writePacket); 
						}
						//removing sent message
						messageQueue.remove(index);
						index--;
					}
			


				//adding sleep time
				Thread.sleep(1);
			}catch(Exception e) { e.printStackTrace();}
		}
	}




}

