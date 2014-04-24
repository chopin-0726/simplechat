package chat;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

@Slf4j
public class Client extends UntypedActor {

	final InetSocketAddress remote;
	final ActorRef manager;
	final ActorRef tcp;

	public Client(InetSocketAddress remote, ActorRef manager) {
		this.remote = remote;
		this.manager = manager;

		tcp = Tcp.get(getContext().system()).manager();
		tcp.tell(TcpMessage.connect(remote), getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CommandFailed) {
			disconnectRequest();

		} else if (msg instanceof Connected) {
			log.debug("client:Connected");
			getSender().tell(TcpMessage.register(getSelf()), getSelf());
			getContext().become(connected(getSender()));
		}
	}

	private Procedure<Object> connected(final ActorRef connection) {
		return new Procedure<Object>() {
			@Override
			public void apply(Object msg) throws Exception {

				if (msg instanceof String) {
					// input from LineScanner
					final String msgStr = (String) msg;
					log.debug("client:String msg={}", msgStr);
					connection.tell(TcpMessage.write(ByteString.fromString(msgStr, "utf-8")), getSelf());
					
				} else if (msg instanceof CommandFailed) {
					disconnectRequest();

				} else if (msg instanceof Received) {

					final String decoded = ((Received) msg).data().decodeString("utf-8");
					log.debug("client:Received msg={}", decoded);
					System.out.println(decoded);

				} else if (msg instanceof ConnectionClosed) {
					disconnectRequest();

				}
			}
		};
	}		
	private void disconnectRequest(){
		manager.tell(new ClientManager.Disconnect(), getSelf());
		
	}

	@Override
	public void postStop() throws Exception {
		tcp.tell(TcpMessage.close(),getSelf());
		super.postStop();
	}
}