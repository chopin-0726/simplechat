package chat;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ServerMain {
		
	public static void main(String[] args){
		ActorSystem system = ActorSystem.create("chat-server");
		final ActorRef server = system.actorOf(Props.create(Server.class), "server");
	}

	
    public static class Manager extends UntypedActor {
        public void onReceive(Object message) {
        }
    }
}
