import dto.Node;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import udp.DHTClient;
import udp.DHTServer;
import utilities.Utils;

/**
 * Created by wihoho on 20/9/15.
 */
public class Crawler {

    public static void main(String[] args) throws SocketException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("0.0.0.0", 6884);
        DatagramSocket socket = new DatagramSocket(inetSocketAddress);

        String nodeId = Utils.randomId();
        Map<String, Node> nodeMap = new HashMap<>();
        Node n1 = Node.builder().address("router.bittorrent.com").port(6881).build();
        Node n2 = Node.builder().address("dht.transmissionbt.com").port(6881).build();
        Node n3 = Node.builder().address("router.utorrent.com").port(6881).build();
        nodeMap.put(n1.getAddress(), n1);
        nodeMap.put(n2.getAddress(), n2);
        nodeMap.put(n3.getAddress(), n3);

        DHTClient dhtClient = DHTClient.builder()
                .id(nodeId)
                .socket(socket)
                .nodeMap(nodeMap)
                .build();

        DHTServer dhtServer = DHTServer.builder()
                .id(nodeId)
                .socket(socket)
                .nodeMap(nodeMap)
                .build();

        Thread client = new Thread(dhtClient);
        Thread server = new Thread(dhtServer);

        client.start();
        server.start();

    }
}
