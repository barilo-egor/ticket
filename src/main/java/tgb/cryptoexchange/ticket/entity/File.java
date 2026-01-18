package tgb.cryptoexchange.ticket.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    private String fileId;

    private String format;

}