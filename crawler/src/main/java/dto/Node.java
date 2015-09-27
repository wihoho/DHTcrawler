package dto;

import lombok.Data;
import lombok.experimental.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wihoho on 19/9/15.
 */

@Data
@Builder
public class Node {
    private String id;
    private String address;
    private int port;

    public boolean isValid() {
        if (StringUtils.isEmpty(address))
            return false;

        if (port < 1 || port > 65535)
            return false;

        return true;
    }
}
