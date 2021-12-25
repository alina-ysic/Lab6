import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import java.time.Duration;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private ActorRef storageActor;
    private Flow<HttpRequest, HttpResponse, NotUsed> flow;
    private static final Duration TIMEOUT = Duration.ofMillis(5000);
    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";
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
                parameter(URL_PARAM, url ->
                    parameter(COUNT_PARAM, countValue -> {
                        int count = Integer.parseInt(countValue);
                        if (count == 0) http.singleRequest(HttpRequest.create(url));
                        else {
                            Patterns.ask(storageActor, new RandomRequest(), TIMEOUT)
                                    .thenCompose(newUrl -> {
                                        String link = String.valueOf(Uri.create(url)
                                                .query(Query.create(
                                                        Pair.create(URL_PARAM, url),
                                                        Pair.create(COUNT_PARAM, Integer.toString(count - 1)))));

                                                

                                    })
                        }
        })
    }
}
