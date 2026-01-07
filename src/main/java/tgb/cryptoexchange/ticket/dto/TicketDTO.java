package tgb.cryptoexchange.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketDTO {

    private Long id;

    @JsonSerialize(using = InstantSerializer.class)
    private Instant creationDate;

    private String appId;

    private Long userId;

    private String category;

    private String description;

    private List<String> fileIds = new ArrayList<>();

    private Long replyTicketId;

}
