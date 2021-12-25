import akka.actor.AbstractActor;

import java.util.Random;

public class ConfStorageActor extends AbstractActor {

    private String[] servers;

    public String getServer() {
        return servers[new Random().nextInt(servers.length)];
    }

    @Override
    public Receive createReceive() {
        return 
    }
}
