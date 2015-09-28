package udp;

import com.dampcake.bencode.Bencode;
import dto.Node;
import lombok.Data;
import lombok.experimental.Builder;
import utilities.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wihoho on 19/9/15.
 */

@Builder
@Data
public class DHTServer implements Runnable {
    private String id;
    private DatagramSocket socket;
    private Map<String, Node> nodeMap;
    private AtomicInteger count;

    @Override
    public void run() {
        System.out.println("Server starts");
        byte[] receiveData = new byte[65536];
        count = new AtomicInteger(0);

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] validData = Utils.getByteArray(receiveData, 0, receivePacket.getLength() - 1);


            Map<String, Object> map = null;
            try {
                map = Utils.deBencode(validData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Node sourceNode = Node.builder()
                    .address(receivePacket.getAddress().toString())
                    .port(receivePacket.getPort())
                    .build();

            try {
                onMessage(map, sourceNode);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMessage(Map<String, Object> map, Node sourceNode) throws IOException {
        // handle find_nodes response
        if (map.get("y").equals("r")) {
            Map<String, String> subMap = (Map<String, String>) map.get("r");
            if (subMap.containsKey("nodes")) {
                onFindNodesResponse(map);
            }

        } else if (Objects.nonNull(map.get("y")) && map.get("y").equals("q")) {
            // handle ping
            switch ((String) map.get("q")) {
                case "ping":
                    onPing(map, sourceNode);
                    break;

                case "get_peers":
                    onGetPeers(map, sourceNode);
                    break;

                case "announce_peer":
                    System.out.println("Announce");
                    break;

                case "find_nodes":
                    onFindNodes(map, sourceNode);
                    break;
            }
        }
    }

    private void onFindNodesResponse(Map<String, Object> map) throws UnknownHostException {
        List<Node> decodedNodes = Utils.decodeNodes(((Map<String, String>) (map.get("r"))).get("nodes"));
        if (decodedNodes.isEmpty())
            return;

        synchronized (nodeMap) {
            decodedNodes.stream()
                    .filter(Node::isValid)
                    .forEach(n -> nodeMap.putIfAbsent(n.getAddress(), n));
        }

    }

    private void onFindNodes(Map<String, Object> map, Node sourceNode) {
        Map<String, String> subMap = (Map<String, String>) map.get("a");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("t", map.get("t"));
        responseMap.put("y", "r");
        Map<String, String> subMap1 = new HashMap<>();
        subMap1.put("id", subMap.get("target"));
//        subMap1.put("nodes", );

    }

    private void onPing(Map<String, Object> map, Node sourceNode) throws IOException {
        Map<String, Object> pong = new HashMap<>();

        pong.put("t", map.get("t"));
        pong.put("y", "r");

        Map<String, String> subMap = new HashMap<>();
        subMap.put("id", this.getId());
        pong.put("r", subMap);

        sendMessage(pong, sourceNode);

    }

    private void sendMessage(Map<String, Object> map, Node targetNode) throws IOException {
        byte[] sendData = Utils.enBencode(map);

        InetAddress destinationIp = InetAddress.getByName(targetNode.getAddress());
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationIp, targetNode.getPort());
        socket.send(sendPacket);

    }

    private void onGetPeers(Map<String, Object> map, Node sourceNode) throws IOException {
        if (Objects.nonNull(map.get("q")) && map.get("q").equals("get_peers")) {
            Map<String, String> subMap = (Map<String, String>) map.get("a");
            String infoHash = subMap.get("info_hash");

            int countNumber = count.incrementAndGet();
            System.out.println(countNumber + ":" + infoHash);

            // response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("t", map.get("t"));
            responseMap.put("y", "r");
            Map<String, String> subMap1 = new HashMap<>();
            subMap1.put("id", Utils.randomId());
            subMap1.put("token", new String(Utils.getByteArray(infoHash.getBytes(Bencode.DEFAULT_CHARSET), 0, 1), Bencode.DEFAULT_CHARSET));
            subMap1.put("nodes", "");
            responseMap.put("r", subMap1);

            sendMessage(responseMap, sourceNode);
        }
    }

}
