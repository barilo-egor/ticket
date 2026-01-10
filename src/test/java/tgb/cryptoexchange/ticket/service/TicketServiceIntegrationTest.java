package tgb.cryptoexchange.ticket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.dto.TicketRequest;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.repository.TickerRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TicketService.class)
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TickerRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        ticketRepository.save(
                Ticket.builder().appId("TG").userId(1L).category("A").description("HELP ME").creationDate(Instant.now())
                        .build());
        ticketRepository.save(Ticket.builder().appId("WEB").userId(2L).category("B").description("CALL ME")
                .creationDate(Instant.now()).build());
        ticketRepository.save(
                Ticket.builder().appId("TG").userId(3L).category("B").description("HATE ME").creationDate(Instant.now())
                        .build());
    }

    @Test
    @DisplayName("Фильтрация по appId должна возвращать только подходящие записи")
    void findAll_FilterByAppId() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setAppId("TG");
        ticketRequest.setUserId(null);
        ticketRequest.setCategory(null);
        Page<TicketDTO> result = ticketService.findAll(PageRequest.of(ticketRequest.getPageNumber(), ticketRequest.getPageSize()), ticketRequest);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(t -> t.getAppId().equals("TG"));
    }

    @Test
    @DisplayName("Фильтрация по appId и category одновременно")
    void findAll_FilterByMultipleParams() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setAppId("TG");
        ticketRequest.setUserId(null);
        ticketRequest.setCategory("B");
        Page<TicketDTO> result = ticketService.findAll(PageRequest.of(ticketRequest.getPageNumber(), ticketRequest.getPageSize()), ticketRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getUserId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Если параметры пусты, возвращаются все записи")
    void findAll_NoFilters() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setAppId(null);
        ticketRequest.setUserId(null);
        ticketRequest.setCategory(null);
        Page<TicketDTO> result = ticketService.findAll(PageRequest.of(ticketRequest.getPageNumber(), ticketRequest.getPageSize()), ticketRequest);

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

}