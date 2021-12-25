import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class AnonApp {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        ActorSystem system = ActorSystem.create("Lab6");
        ActorRef storageActor = system.actorOf(Props.create(ConfStorageActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);


        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                flow,
                ConnectHttp.toHost(HOST, port),
                materializer
        );

        System.out.println("Server started: " + HOST + ":" + port);
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbind -> system.terminate());
    }
    }
}
