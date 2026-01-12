package tgb.cryptoexchange.ticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.entity.TicketReply;
import tgb.cryptoexchange.ticket.exception.TicketReplyException;
import tgb.cryptoexchange.ticket.kafka.TicketReplyReceive;
import tgb.cryptoexchange.ticket.repository.TickerReplyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketReplyServiceTest {

    @Mock
    private TickerReplyRepository tickerReplyRepository;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketReplyService ticketReplyService;

    @Test
    @DisplayName("Успешное сохранение ответа, если тикет существует")
    void save_Success() {
        Long ticketId = 100L;
        TicketReplyReceive request = createRequest(ticketId);

        when(ticketService.findById(ticketId)).thenReturn(new TicketDTO());
        ticketReplyService.save(request);

        verify(tickerReplyRepository, times(1)).save(any(TicketReply.class));
        verify(ticketService, times(1)).findById(ticketId);
    }

    @Test
    @DisplayName("Если тикет не найден - сохранение не происходит")
    void save_TicketNotFound_ThrowsException() {
        Long ticketId = 200L;
        TicketReplyReceive request = createRequest(ticketId);

        when(ticketService.findById(ticketId)).thenReturn(null);

        assertThatCode(() -> ticketReplyService.save(request))
                .doesNotThrowAnyException();
        verify(tickerReplyRepository, never()).save(any());
    }

    private TicketReplyReceive createRequest(Long ticketId) {
        TicketReplyReceive request = new TicketReplyReceive();
        request.setTicketId(ticketId);
        request.setReply("Test reply");
        request.setAuthorId(1L);
        request.setFileIds(List.of("file1"));
        return request;
    }
}