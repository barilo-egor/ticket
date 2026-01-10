package tgb.cryptoexchange.ticket.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class TicketReplyRequestTopicListener {

    private final TicketReplyRequestProcessorService requestProcessorService;

    public TicketReplyRequestTopicListener(TicketReplyRequestProcessorService requestProcessorService) {
        this.requestProcessorService = requestProcessorService;
    }

    @KafkaListener(topics = "${kafka.topic.reply.request}", groupId = "${kafka.group-id}",
            containerFactory = "ticketReplyListenerFactory")
    public void receive(@Payload TicketReplyReceive ticketReplyRequest) {
        requestProcessorService.process(ticketReplyRequest);
    }

}
