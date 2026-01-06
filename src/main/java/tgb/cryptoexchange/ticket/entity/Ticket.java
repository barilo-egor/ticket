package tgb.cryptoexchange.ticket.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String applicationId;

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
    private List<String> fileIds = new ArrayList<>();

}
