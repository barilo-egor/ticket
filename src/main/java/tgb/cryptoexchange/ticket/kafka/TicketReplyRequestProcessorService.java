package tgb.cryptoexchange.ticket.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.ticket.service.TicketReplyService;

@Service
@Slf4j
public class TicketReplyRequestProcessorService {

    private final TicketReplyService ticketReplyService;

    private final ThreadPoolTaskExecutor ticketRequestSaveExecutor;

    public TicketReplyRequestProcessorService(TicketReplyService ticketReplyService,
                                              ThreadPoolTaskExecutor ticketRequestSaveExecutor) {
        this.ticketReplyService = ticketReplyService;
        this.ticketRequestSaveExecutor = ticketRequestSaveExecutor;

    }

    public void process(TicketReplyReceive ticketReplyRequest) {
        ticketRequestSaveExecutor.submit(() -> {
            try {
                ticketReplyService.save(ticketReplyRequest);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
