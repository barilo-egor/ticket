package tgb.cryptoexchange.ticket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tgb.cryptoexchange.ticket.entity.File;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.entity.TicketReply;
import tgb.cryptoexchange.ticket.kafka.TicketReplyReceive;
import tgb.cryptoexchange.ticket.repository.TickerReplyRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class TicketReplyService {

    private final TickerReplyRepository tickerReplyRepository;

    private final TicketService ticketService;

    public TicketReplyService(TickerReplyRepository tickerReplyRepository, TicketService ticketService) {
        this.tickerReplyRepository = tickerReplyRepository;
        this.ticketService = ticketService;
    }

    public void save(TicketReplyReceive ticketReplyRequest) {
        log.info("Запрос на сохранение ответа на тикет: {}", ticketReplyRequest.getTicketId());
        Optional<Ticket> maybeTicket = ticketService.findById(ticketReplyRequest.getTicketId());
        if (maybeTicket.isEmpty()) {
            log.warn("На сохранение поступил тикет с ID {}, которого не существует: {}",
                    ticketReplyRequest.getTicketId(), ticketReplyRequest);
            return;
        } else if (maybeTicket.get().getReplyTicket() != null) {
            log.warn("На сохранение поступил тикет {} с уже обработанным ответом: {}",
                    ticketReplyRequest.getTicketId(), ticketReplyRequest);
            return;
        }
        TicketReply ticketReply = TicketReply.builder()
                .ticket(Ticket.builder().id(ticketReplyRequest.getTicketId()).build())
                .reply(ticketReplyRequest.getReply())
                .authorId(ticketReplyRequest.getAuthorId())
                .files(CollectionUtils.isEmpty(ticketReplyRequest.getFiles()) ? new ArrayList<>() :
                        ticketReplyRequest.getFiles().stream()
                                .map(dto -> File.builder().fileId(dto.getFileId()).format(dto.getFormat()).build())
                                .toList())
                .build();
        tickerReplyRepository.save(ticketReply);
        log.info("Ответ на тикет сохранен с ID: {}", ticketReply.getId());
    }

}
