package chat;

import akka.actor.UntypedActor;

public class ClientManager extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Disconnect) {
			getContext().system().shutdown();
		}
	}

	interface ClientManagerRequest {
	}

	static class Disconnect implements ClientManagerRequest {
	}
}
