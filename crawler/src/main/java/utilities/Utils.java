package utilities;

import dto.Node;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.BencodeInputStream;
import com.dampcake.bencode.BencodeOutputStream;
import com.dampcake.bencode.Type;
import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created by wihoho on 19/9/15.
 */
public class Utils {
    private final static Random random = new Random();
    private final static HashFunction sha1 = Hashing.sha1();

    public static List<Node> fixedNodes = new ArrayList<>();

    static {
        fixedNodes.add(Node.builder().address("router.bittorrent.com").port(6881).build());
        fixedNodes.add(Node.builder().address("dht.transmissionbt.com").port(6881).build());
        fixedNodes.add(Node.builder().address("router.utorrent.com").port(6881).build());
    }

    public static String getNeighbour(String s1, String s2) {
        if (Strings.isNullOrEmpty(s1))
            return s2;

        byte[] bytes1 = s1.getBytes(Bencode.DEFAULT_CHARSET);
        byte[] bytes2 = s2.getBytes(Bencode.DEFAULT_CHARSET);

        byte[] newBytes = new byte[20];
        for(int i = 0; i < 10; i ++) {
            newBytes[i] = bytes1[i];
        }

        for(int i = 10; i < 20; i ++) {
            newBytes[i] = bytes2[i-10];
        }

        return new String(newBytes, Bencode.DEFAULT_CHARSET);

    }

    public static String randomId() {
        String s = getRandomString(10);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(s.getBytes());
        return new String(messageDigest.digest(), Bencode.DEFAULT_CHARSET);
    }

    public static String getRandomString(int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int randomNum = random.nextInt(256);
            sb.append(Character.toChars(randomNum));
        }

        return sb.toString();
    }

    public static byte[] enBencode(Map<String, Object> map) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BencodeOutputStream bencoder = new BencodeOutputStream(out);
        bencoder.writeDictionary(map);
        return out.toByteArray();
    }

    public static Map<String, Object> deBencode(byte[] data) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        BencodeInputStream bencode = new BencodeInputStream(in);

        Type type = bencode.nextType(); // Returns Type.DICTIONARY

        try {
            Map<String, Object> dict = bencode.readDictionary();
            return dict;
        } catch (Exception e) {
            return new HashMap<>();
        }

    }

    public static List<Node> decodeNodes(String encodeNodes) throws UnknownHostException {
        List<Node> result = new ArrayList<>();
        if (Strings.isNullOrEmpty(encodeNodes)) {
            return result;
        }

        byte[] bytes = encodeNodes.getBytes(Bencode.DEFAULT_CHARSET);
        int size = bytes.length;

        if (size % 26 != 0) {
            return result;
        }

        for (int i = 0; i < size; i += 26) {
            byte[] currentNodeId = getByteArray(bytes, i, i + 19);
            byte[] currentNodeIp = getByteArray(bytes, i+20, i+23);
            byte[] currentNodePort = getByteArray(bytes, i+24, i+25);

            int port = getPort(currentNodePort);
            String ip = InetAddress.getByAddress(currentNodeIp).getHostAddress();

            Node n = Node.builder()
                    .id(new String(currentNodeId, Bencode.DEFAULT_CHARSET))
                    .address(ip)
                    .port(port)
                    .build();

            result.add(n);

        }

        return result;
    }

    public static byte[] getByteArray(byte[] bytes, int start, int end) {
        byte[] newByteArray = new byte[end - start + 1];

        for(int i = start; i <= end; i ++) {
            newByteArray[i-start] = bytes[i];
        }

        return newByteArray;
    }

    public static int getPort(byte[] bytes) {
        return ((bytes[0] << 8) & 0x0000ff00) | (bytes[1] & 0x000000ff);
    }
}
