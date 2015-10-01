package dto;

import com.google.common.base.Strings;
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

    public boolean isValid() {
        if (Strings.isNullOrEmpty(address))
            return false;

        if (port < 1 || port > 65535)
            return false;

        return true;
    }
}
