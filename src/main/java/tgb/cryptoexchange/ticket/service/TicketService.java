package tgb.cryptoexchange.ticket.service;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.exception.BadRequestException;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.dto.TicketRequest;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.kafka.TicketReceive;
import tgb.cryptoexchange.ticket.repository.TickerRepository;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class TicketService {

    private final TickerRepository ticketRepository;

    public TicketService(TickerRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void save(TicketReceive ticketReceive) {
        log.info("Запрос на сохранение тикета от пользователя: {}", ticketReceive.getUserId());
        Ticket ticket = Ticket.builder()
                .creationDate(Instant.now())
                .category(ticketReceive.getCategory())
                .description(ticketReceive.getDescription())
                .appId(ticketReceive.getAppId())
                .userId(ticketReceive.getUserId())
                .fileIds(ticketReceive.getFileIds())
                .build();
        ticketRepository.save(ticket);
        log.info("Тикет сохранен с ID: {}", ticket.getId());
    }

    public Page<TicketDTO> findAll(Pageable pageable, TicketRequest ticketRequest) {
        log.debug("Запрос на поиск тикетов с параметрами: {}", ticketRequest);
        Page<Ticket> ticketPage = ticketRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        ticketRequest.toPredicates(root, criteriaBuilder).toArray(new Predicate[0])
                ),
                pageable
        );
        log.debug("Найдено {} тикетов на странице {}, всего страниц {}",
                ticketPage.getNumberOfElements(), ticketPage.getNumber(), ticketPage.getTotalPages());
        return ticketPage.map(TicketDTO::fromEntity);
    }


    public Optional<Ticket> findById(Long id) {
        log.debug("Запрос на поиск тикета по ID: {}", id);
        return ticketRepository.findById(id);
    }

    public void deleteById(Long id) {
        log.info("Запрос на удаление тикета с ID: {}", id);
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            log.info("Тикет с ID={} удален", id);
        } else {
            log.warn("Тикет для удаления с ID={} не существует.", id);
            throw new BadRequestException(String.format("Ticket with id %s not found.", id));
        }
    }

}
