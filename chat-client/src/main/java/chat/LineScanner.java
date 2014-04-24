package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class LineScanner extends UntypedActor {

	private final ActorRef client;
	private final ActorRef manager;
	private BufferedReader stdReader;

	public LineScanner(ActorRef client, ActorRef manager) {
		this.client = client;
		this.manager = manager;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		 stdReader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			while ((line = stdReader.readLine()) != null) {
				if (line.equals("exit")) {
					manager.tell(new ClientManager.Disconnect(), getSelf());
					break;
				}
				client.tell(line, getSelf());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void postStop() throws Exception {
		stdReader.close();
		super.postStop();
	}
}
