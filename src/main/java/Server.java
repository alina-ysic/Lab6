import akka.NotUsed;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private final Flow<HttpRequest, HttpResponse, NotUsed> routes;

    public Server(Http http, Materializer materializer, Flow<HttpRequest, HttpResponse) {
        this.http = http;
        this.materializer = materializer;
        //this.routes = routes;
    }
}
