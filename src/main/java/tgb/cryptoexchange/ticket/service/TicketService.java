package tgb.cryptoexchange.ticket.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.kafka.TicketRequest;
import tgb.cryptoexchange.ticket.repository.TickerRepository;

import java.time.Instant;

@Service
@Slf4j
public class TicketService {

    private final TickerRepository ticketRepository;

    public TicketService(TickerRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void save(TicketRequest ticketRequest) {
        log.info("Запрос на сохранение тикета от пользователя: {}", ticketRequest.getUserId());
        Ticket ticket = Ticket.builder()
                .creationDate(Instant.now())
                .category(ticketRequest.getCategory())
                .description(ticketRequest.getDescription())
                .appId(ticketRequest.getAppId())
                .userId(ticketRequest.getUserId())
                .fileIds(ticketRequest.getFileIds())
                .build();
        ticketRepository.save(ticket);
        log.info("Тикет сохранен с ID: {}", ticket.getId());
    }

    public Page<TicketDTO> findAllFilteredAndPaged(String appId, Long userId, String category, Pageable pageable) {
        log.debug("Запрос на поиск тикетов с параметрами: appId={}, userId={}, type={}, page={}", appId, userId, category, pageable);
        Specification<Ticket> spec = Specification.unrestricted();
        if (StringUtils.isNotBlank(appId)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("appId"), appId));
        }
        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userId"), userId));
        }
        if (StringUtils.isNotBlank(category)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category));
        }
        Page<Ticket> ticketPage = ticketRepository.findAll(spec, pageable);
        log.debug("Найдено {} тикетов на странице {}, всего страниц {}",
                ticketPage.getNumberOfElements(), ticketPage.getNumber(), ticketPage.getTotalPages());
        return ticketPage.map(ticket ->
                TicketDTO.builder()
                        .id(ticket.getId())
                        .category(ticket.getCategory())
                        .description(ticket.getDescription())
                        .creationDate(ticket.getCreationDate())
                        .userId(ticket.getUserId())
                        .fileIds(ticket.getFileIds())
                        .appId(ticket.getAppId())
                        .replyTicketId(ticket.getReplyTicket()==null ? null : ticket.getReplyTicket().getId())
                        .build());
    }


    public TicketDTO findById(Long id) {
        log.debug("Запрос на поиск тикета по ID: {}", id);
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            log.warn("Тикет с ID: {} не найден", id);
            return null;
        }
        return TicketDTO.builder()
                .id(ticket.getId())
                .category(ticket.getCategory())
                .description(ticket.getDescription())
                .creationDate(ticket.getCreationDate())
                .userId(ticket.getUserId())
                .fileIds(ticket.getFileIds())
                .appId(ticket.getAppId())
                .replyTicketId(ticket.getReplyTicket()==null ? null : ticket.getReplyTicket().getId())
                .build();
    }

    public void deleteById(Long id) {
        log.info("Запрос на удаление тикета с ID: {}", id);
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            log.info("Тикет с ID: {} удален", id);
        } else {
            log.warn("Тикет с ID: {} не существует", id);
        }

    }

}
