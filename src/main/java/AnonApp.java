import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;

public class AnonApp {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        ActorSystem system = ActorSystem.create("Lab6");
        ActorRef storageActor = system.actorOf(Props.create(ConfStorageActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        Server server = new Server(system, http, materializer, storageActor, port);
        Flow<HttpRequest, HttpResponse, NotUsed> flow = server.getFlow();

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                flow,
                ConnectHttp.toHost("localhost", port),
                materializer
        );

        System.out.println("Server started: " + "localhost" + ":" + port);
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbind -> system.terminate());
    }
}
