import akka.NotUsed;
import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private ActorRef storageActor;
    //private final Flow<HttpRequest, HttpResponse, NotUsed> routes;

    public Server(Http http, Materializer materializer, ActorRef storageActor) {
        this.http = http;
        this.materializer = materializer;
        this.storageActor = storageActor;
        //this.routes = routes;
    }
}
