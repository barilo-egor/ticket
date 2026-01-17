package tgb.cryptoexchange.ticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.exception.BadRequestException;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.kafka.TicketReceive;
import tgb.cryptoexchange.ticket.repository.TickerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TickerRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    @DisplayName("save должен корректно мапить запрос и сохранять тикет")
    void save_ShouldMapAndSave() {
        TicketReceive request = new TicketReceive();
        request.setUserId(123L);
        request.setCategory("SUPPORT");
        request.setDescription("Help me");

        ticketService.save(request);

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("findById должен возвращать тикет при наличии тикета")
    void findById_ShouldReturnTicket_WhenFound() {
        Long id = 1L;
        Ticket ticket = Ticket.builder()
                .id(id)
                .userId(123L)
                .description("Test")
                .build();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.findById(id);

        assertThat(result).isPresent();
        Ticket actual = result.get();
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getUserId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("findById должен возвращать пустой Optional, если тикет не найден")
    void findById_ShouldReturnEmptyOptional_WhenNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteById должен вызывать репозиторий, если тикет существует")
    void deleteById_ShouldExecute_WhenExists() {
        Long id = 1L;
        when(ticketRepository.existsById(id)).thenReturn(true);

        ticketService.deleteById(id);

        verify(ticketRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById должен пробросить исключение BadRequestException, если тикета нет")
    void deleteById_ShouldNotExecute_WhenNotExists() {
        when(ticketRepository.existsById(1L)).thenReturn(false);

        assertThatException().isThrownBy(() -> ticketService.deleteById(1L)).isInstanceOf(BadRequestException.class);
    }
}