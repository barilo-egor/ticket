package tgb.cryptoexchange.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String appId;

    @Column(nullable = false)
    private Long userId;

    private String category;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "ticket_files",
            joinColumns = @JoinColumn(name = "ticket_id")
    )
    @Column
    @Builder.Default
    private List<String> fileIds = new ArrayList<>();

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private TicketReply replyTicket;

}
