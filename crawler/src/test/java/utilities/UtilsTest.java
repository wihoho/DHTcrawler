package utilities;

import com.google.common.io.Files;
import dto.Node;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by wihoho on 19/9/15.
 */
public class UtilsTest {

    @Test
    public void testRandomId() throws Exception {
        String result = Utils.randomId();
        assertEquals(20, Hex.decodeHex(result.toCharArray()).length);
    }

    @Test
    public void testEncode() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("t", "aa");
        map.put("y", "q");
        map.put("q", "find_node");

        Map<String, Object> subMap = new HashMap<>();
        subMap.put("id", "abcdefghij0123456789");
        subMap.put("target", "mnopqrstuvwxyz123456");
        map.put("a", subMap);

        byte[] bytes = Utils.enBencode(map);

        assertEquals("d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe", new String(bytes));
    }

    @Test
    public void testDecode() throws IOException {
        String s = "d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe";
        Map<String, Object> map = Utils.deBencode(s.getBytes());
        assertEquals(4, map.size());
    }

    @Test
    public void testFindNodes() throws IOException {
        byte[] bytes = Files.toByteArray(new File(getClass().getResource("/findNodes.bin").getFile()));
        Map<String,Object> map = Utils.deBencode(bytes);

        List<Node> decodedNodes = new ArrayList<>();
        if (map.get("eQ==").equals("cg==")) {
            Map<String, String> subMap = (Map<String, String>) map.get("cg==");
            if (subMap.containsKey("bm9kZXM=")) {
                 decodedNodes = Utils.decodeNodes(subMap.get("bm9kZXM="));
            }
        }

        assertEquals(8, decodedNodes.size());
        assertEquals(64808, decodedNodes.get(0).getPort());
        assertEquals("98.113.86.226", decodedNodes.get(0).getAddress());
    }

    @Test
    public void testGetPort() {
        byte[] bytes = new byte[2];
        bytes[0] = -3;
        bytes[1] = 40;

        assertEquals(64808, Utils.getPort(bytes));
    }

}