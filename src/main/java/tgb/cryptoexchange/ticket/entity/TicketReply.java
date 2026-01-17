package tgb.cryptoexchange.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reply")
public class TicketReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ticket_id")
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
    @Builder.Default
    private List<String> fileIds = new ArrayList<>();

}
