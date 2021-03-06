import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.Random;

public class ConfStorageActor extends AbstractActor {

    private String[] servers;

    public String getServer() {
        String url = servers[new Random().nextInt(servers.length)];
        System.out.println("REQUEST REDIRECTED: " + url);
        return url;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(RandomRequest.class, m -> sender().tell(getServer(), self()))
                .match(ServersChangeMessage.class, msg -> this.servers = msg.getServers())
                .build();
    }
}
