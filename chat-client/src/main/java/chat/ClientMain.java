package chat;

import java.io.InputStream;
import java.net.InetSocketAddress;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import chat.client.config.ClientConfig;

public class ClientMain {

	public static void main(String[] args) {
		final InputStream is = ClassLoader.getSystemResourceAsStream("client.yaml");
		final ClientConfig conf = (ClientConfig) new Yaml(new Constructor(ClientConfig.class)).load(is);

		final ActorSystem system = ActorSystem.create("chat-client");

		final ActorRef manager = system.actorOf(Props.create(ClientManager.class), "manager");
		
		final InetSocketAddress serverAddress = new InetSocketAddress(conf.getServer().getHost(), conf.getServer().getPort());
		final ActorRef client = system.actorOf(Props.create(Client.class, serverAddress, manager), "client");

		final ActorRef scanner = system.actorOf(Props.create(LineScanner.class, client, manager), "scanner");
		scanner.tell("start", ActorRef.noSender());
	}
}
