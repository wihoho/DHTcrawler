package udp;

import dto.Node;
import lombok.Data;
import lombok.experimental.Builder;
import utilities.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by wihoho on 19/9/15.
 */

@Builder
@Data
public class DHTServer implements Runnable {
    private String id;
    private DatagramSocket socket;
    private Queue<Node> nodes;

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
        if (map.get("eQ==").equals("cg==")) {
            Map<String, String> subMap = (Map<String, String>) map.get("cg==");
            if (subMap.containsKey("bm9kZXM=")) {
                List<Node> decodedNodes = Utils.decodeNodes(subMap.get("bm9kZXM="));
                if (decodedNodes.isEmpty())
                    return;

                synchronized (nodes) {
                    decodedNodes.forEach(
                            eachNode -> nodes.add(eachNode)
                    );
                }
            }

        } else if (map.get("y").equals("q")) {
            String operation = (String) map.get("q");
            System.out.println(operation);
        }
    }
}
