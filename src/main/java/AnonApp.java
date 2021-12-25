import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class AnonApp {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        ActorSystem system = ActorSystem.create("Lab6");
        ActorRef storageActor = system.actorOf(Props.create(ConfStorageActor.class));
    }
}
