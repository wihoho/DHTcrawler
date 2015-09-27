package udp;

import dto.Node;
import lombok.Data;
import lombok.experimental.Builder;
import utilities.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wihoho on 19/9/15.
 */

@Builder
@Data
public class DHTServer implements Runnable {
    private String id;
    private DatagramSocket socket;
    private Map<String, Node> nodeMap;

    @Override
    public void run() {
        System.out.println("Server starts");
        byte[] receiveData = new byte[65536];

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
            }
        }
    }

    public void onMessage(Map<String, Object> map, Node sourceNode) throws UnknownHostException {
        // handle find_nodes response
        if (Objects.nonNull(map.get("eQ==")) && map.get("eQ==").equals("cg==")) {
            Map<String, String> subMap = (Map<String, String>) map.get("cg==");
            if (subMap.containsKey("bm9kZXM=")) {
                onFindNodes(subMap);
            }

        } else if (Objects.nonNull(map.get("cQ==")) && map.get("cQ==").equals("Z2V0X3BlZXJz")) {
            Map<String, String> subMap = (Map<String, String>) map.get("YQ==");
            String infoHash = subMap.get("aW5mb19oYXNo");
            System.out.println(infoHash);

        }
    }

    private void onFindNodes(Map<String, String> subMap) throws UnknownHostException {
        List<Node> decodedNodes = Utils.decodeNodes(subMap.get("bm9kZXM="));
        if (decodedNodes.isEmpty())
            return;

        synchronized (nodeMap) {
            decodedNodes.stream()
                    .filter(Node::isValid)
                    .forEach(n -> nodeMap.putIfAbsent(n.getAddress(), n));
        }
    }

    private void onPing(Map<String, Object> map, Node sourceNode) {
        String tId = (String) map.get("dA==");
        Map<String, Object> pong = new HashMap<>();

//        pong.put("")


    }

    private void onGetPeers(Map<String, Object> map) {

    }

}
