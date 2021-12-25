import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private ActorRef storageActor;
    private Flow<HttpRequest, HttpResponse, NotUsed> flow;

    private static final String URL_PARAM = "url";
    //private final Flow<HttpRequest, HttpResponse, NotUsed> routes;

    public Server(ActorSystem system, Http http, Materializer materializer, ActorRef storageActor, int port) {
        this.http = http;
        this.materializer = materializer;
        this.storageActor = storageActor;
        //this.routes = routes;
        flow = createRoute().flow(system, materializer);
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> getFlow() {
        return flow;
    }

    public Route createRoute() {
        return get(() ->
                parameter())
    }
}
