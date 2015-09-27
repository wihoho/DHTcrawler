package dto;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Created by wihoho on 19/9/15.
 */

@Data
@Builder
public class Node {
    private String id;
    private String address;
    private int port;
}
