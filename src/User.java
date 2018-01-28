
public class User {
	
	public final int PORT;
	public boolean isConnected;
	public String userName;
	
	public User(int port, String userName){
		PORT = port;
		this.userName = userName;
		isConnected = true;
	}
	
	public String toString(){
		return userName;
	}
	
}
