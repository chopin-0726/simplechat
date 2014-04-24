package chat;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.TcpMessage;
import akka.util.ByteString;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Slf4j
public class Writer extends UntypedActor {

	private List<User> users = new ArrayList<>();

	@Override
	public void onReceive(Object msg) throws Exception {
		if (false == msg instanceof Request) {
			System.out.println("writer:receive unknown message.");
			return;
		}

		if (msg instanceof Add) {
			log.debug("writer:Add");
			users.add(((Add) msg).user);
		} else if (msg instanceof Write) {
			log.debug("writer:Write");
			final String str = ((Write) msg).str;
			final ByteString encoded = ByteString.fromString(str, "utf-8");
			for (User user : users)
				user.getConnection().tell(TcpMessage.write(encoded), getSelf());
		} else if (msg instanceof Remove) {
			log.debug("writer:Remove");
			final ActorRef connection = ((Remove)msg).connection;
//			connection.tell(TcpMessage.close(), getSelf());
			Iterables.removeIf(users, new Predicate<User>() {
				@Override
				public boolean apply(User u) {
					return u.getConnection().equals(connection);
				}
			});
		}
	}

	public interface Request {
	}

	public static class Add implements Request {
		private final User user;

		public Add(User u) {
			user = u;
		}
	}

	public static class Remove implements Request {
		private final ActorRef connection;

		public Remove(ActorRef c) {
			connection = c;
		}
	}

	public static class Write implements Request {
		private final String str;

		public Write(String s) {
			str = s;
		}
	}
}
