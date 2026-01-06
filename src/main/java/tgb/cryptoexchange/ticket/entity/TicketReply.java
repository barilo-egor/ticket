package tgb.cryptoexchange.ticket.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "reply")
public class TicketReply {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Ticket ticket;

    @Column
    private String reply;

    @Column(nullable = false)
    private Long authorId;

    @ElementCollection
    @CollectionTable(
            name = "reply_files",
            joinColumns = @JoinColumn(name = "reply_id")
    )
    @Column
    private List<String> fileIds = new ArrayList<>();

}
