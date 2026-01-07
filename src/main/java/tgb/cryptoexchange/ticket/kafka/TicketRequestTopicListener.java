package tgb.cryptoexchange.ticket.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class TicketRequestTopicListener {

    private final TicketRequestProcessorService requestProcessorService;

    public TicketRequestTopicListener(TicketRequestProcessorService requestProcessorService) {
        this.requestProcessorService = requestProcessorService;
    }

    @KafkaListener(topics = "${kafka.topic.ticket.request}", groupId = "${kafka.group-id}",
            containerFactory = "ticketListenerFactory")
    public void receive(@Payload TicketRequest ticketRequest) {
        requestProcessorService.process(ticketRequest);
    }

}
