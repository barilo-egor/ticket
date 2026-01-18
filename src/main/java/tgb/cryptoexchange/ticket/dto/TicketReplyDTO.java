package tgb.cryptoexchange.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.cryptoexchange.ticket.entity.TicketReply;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketReplyDTO {

    private Long id;

    private Long ticketId;

    private String reply;

    private Long authorId;

    @Builder.Default
    private List<FileDTO> files = new ArrayList<>();

    public static TicketReplyDTO fromEntity(TicketReply ticketReply) {
        return TicketReplyDTO.builder()
                .id(ticketReply.getId())
                .reply(ticketReply.getReply())
                .authorId(ticketReply.getAuthorId())
                .ticketId(ticketReply.getTicket() == null ? null : ticketReply.getTicket().getId())
                .files(ticketReply.getFiles().stream()
                        .map(file ->
                                new FileDTO(file.getFileId(), file.getFormat()))
                        .toList())
                .build();
    }

}
