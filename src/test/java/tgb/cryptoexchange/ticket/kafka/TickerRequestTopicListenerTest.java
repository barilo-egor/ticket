package tgb.cryptoexchange.ticket.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TickerRequestTopicListenerTest {

    @Mock
    private TicketRequestProcessorService ticketRequestProcessorService;

    @InjectMocks
    private TicketRequestTopicListener ticketRequestTopicListener;

    @Test
    void receiveShouldCallServiceMethodWithAllMerchants() {
        TicketReceive detailsRequest = new TicketReceive();
        detailsRequest.setDescription("HELP ME");
        ticketRequestTopicListener.receive(detailsRequest);
        verify(ticketRequestProcessorService).process(detailsRequest);
    }

}