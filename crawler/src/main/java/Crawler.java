import dto.Node;
import udp.DHTClient;
import udp.DHTServer;
import utilities.Utils;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by wihoho on 20/9/15.
 */
public class Crawler {

    public static void main(String[] args) throws SocketException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("0.0.0.0", 6884);
        DatagramSocket socket = new DatagramSocket(inetSocketAddress);

        String nodeId = Utils.randomId();
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(Node.builder().address("router.bittorrent.com").port(6881).build());
        nodes.add(Node.builder().address("dht.transmissionbt.com").port(6881).build());
        nodes.add(Node.builder().address("router.utorrent.com").port(6881).build());
//        nodes.add(Node.builder().address("localhost").port(6882).build());

        DHTClient dhtClient = DHTClient.builder()
                .id(nodeId)
                .socket(socket)
                .nodes(nodes)
                .build();

        DHTServer dhtServer = DHTServer.builder()
                .id(nodeId)
                .socket(socket)
                .nodes(nodes)
                .build();

        Thread client = new Thread(dhtClient);
        Thread server = new Thread(dhtServer);

        client.start();
        server.start();

    }
}
