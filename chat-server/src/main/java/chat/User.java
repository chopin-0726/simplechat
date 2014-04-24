package chat;

import lombok.Data;
import akka.actor.ActorRef;

@Data
public class User {
	private String userName;
	private final ActorRef connection;
	
	public User(ActorRef connection){
		this.connection  = connection;
	}
}
