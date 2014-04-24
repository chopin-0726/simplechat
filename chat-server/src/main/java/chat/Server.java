package chat;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;

@Slf4j
public class Server extends UntypedActor {

	private static String HOST = "localhost";
	private static int PORT = 9876;
	
	private final ActorRef writer;
	private final ActorRef handler;

	public Server() {
		this.writer = getContext().actorOf(Props.create(Writer.class), "writer");
		this.handler = getContext().actorOf(Props.create(Listener.class, writer),"listener");
	}

	@Override
	public void preStart() throws Exception {
		final ActorRef tcp = Tcp.get(getContext().system()).manager();
		tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress(HOST, PORT), 100), getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Bound) {
			log.debug("server:Bound msg=" + msg);
		} else if (msg instanceof CommandFailed) {
			getContext().stop(getSelf());

		} else if (msg instanceof Connected) {
			log.debug("server:Connected msg=" + msg);
			getSender().tell(TcpMessage.register(handler), getSelf());
			writer.tell(new Writer.Add(new User(getSender())), getSelf());
		}
	}

}