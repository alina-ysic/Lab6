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
import org.apache.zookeeper.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Server extends AllDirectives {
    private final Http http;
    private final Materializer materializer;
    private ActorRef storageActor;
    private Flow<HttpRequest, HttpResponse, NotUsed> flow;
    private static final Duration TIMEOUT = Duration.ofMillis(5000);
    private static final int TIMEOUT_INT = 5000;
    private static final String SERVERS_PATH = "/servers";
    private static final String NODE_PATH = "/servers/node";
    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";

    private static final String ZOOKEEPER_SERVER = "127.0.0.1:2181";
    private final ZooKeeper zookeeper;

    public Server(ActorSystem system, Http http, Materializer materializer, ActorRef storageActor, int port) throws IOException, InterruptedException, KeeperException {
        this.http = http;
        this.materializer = materializer;
        this.storageActor = storageActor;
        this.zookeeper = new ZooKeeper(ZOOKEEPER_SERVER, TIMEOUT_INT, null);
        watchServers();
        zookeeper.create(
                NODE_PATH,
                ("http://localhost" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
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
                        if (count == 0) return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                        return completeWithFuture(Patterns.ask(storageActor, new RandomRequest(), TIMEOUT)
                                .thenCompose(newUrl -> {
                                    String link = String.valueOf(Uri.create(url)
                                            .query(Query.create(
                                                    Pair.create(URL_PARAM, url),
                                                    Pair.create(COUNT_PARAM, Integer.toString(count - 1)))));
                                    return http.singleRequest(HttpRequest.create(link));
                                }));

                    })
                )
        );

    }

    public void watchServers() throws InterruptedException, KeeperException {
        final List<String> servers = new ArrayList<>();
        final List<String> serverNames = zookeeper.getChildren(SERVERS_PATH, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                watchServers();
            }
        });
        for (String serverName : serverNames) {
            byte[] url = zookeeper.getData(SERVERS_PATH + "/" + serverName, null, null);
            
        }
    }
}
