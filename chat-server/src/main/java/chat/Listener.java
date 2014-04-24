package chat;

import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.util.ByteString;

@Slf4j
public class Listener extends UntypedActor {

	private final ActorRef writer;
	public Listener(ActorRef writer) {
		this.writer = writer;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Received) {
			final ByteString data = ((Received) msg).data();
			final String decoded = data.decodeString("utf-8");
			log.debug("listener:Received msg=" + decoded);

			writer.tell(new Writer.Write(decoded), getSelf());

		} else if (msg instanceof ConnectionClosed) {
			writer.tell(new Writer.Remove(getSender()), getSelf());
		}
	}
}
