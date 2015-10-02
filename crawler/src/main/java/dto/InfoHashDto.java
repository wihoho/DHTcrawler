package dto;

import lombok.Data;
import lombok.experimental.Builder;

import java.util.UUID;

/**
 * Created by wihoho on 2/10/15.
 */

@Data
@Builder
public class InfoHashDto {

    private UUID _id;
    private String infoHash;
    private String ipAddress;

}
