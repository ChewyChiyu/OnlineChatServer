
public class Message {
	public String message;
	public User user;
	public Message(String message, User user ){
		this.message = message;
		this.user = user;
	}
	
	public String toString(){
		return user + " : " + message;
	}
	
	
	
}
