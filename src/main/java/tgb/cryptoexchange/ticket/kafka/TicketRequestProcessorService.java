package tgb.cryptoexchange.ticket.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.ticket.service.TicketService;

@Service
@Slf4j
public class TicketRequestProcessorService {

    private final TicketService ticketService;

    private final ThreadPoolTaskExecutor ticketRequestSaveExecutor;

    public TicketRequestProcessorService(TicketService ticketService,
                                         ThreadPoolTaskExecutor ticketRequestSaveExecutor) {
        this.ticketService = ticketService;
        this.ticketRequestSaveExecutor = ticketRequestSaveExecutor;

    }

    public void process(TicketReceive ticketRequest) {
        ticketRequestSaveExecutor.submit(() -> {
            try {
                ticketService.save(ticketRequest);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
