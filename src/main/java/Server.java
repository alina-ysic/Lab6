import akka.http.javadsl.server.AllDirectives;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private final Flow<HttpRequest, HttpResponse, NotUsed> routes;
}
