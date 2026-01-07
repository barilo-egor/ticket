package tgb.cryptoexchange.ticket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.entity.Ticket;
import tgb.cryptoexchange.ticket.entity.TicketReply;
import tgb.cryptoexchange.ticket.exception.TicketReplyException;
import tgb.cryptoexchange.ticket.kafka.TicketReplyRequest;
import tgb.cryptoexchange.ticket.repository.TickerReplyRepository;


@Service
@Slf4j
public class TicketReplyService {

    private final TickerReplyRepository tickerReplyRepository;

    private final TicketService ticketService;

    public TicketReplyService(TickerReplyRepository tickerReplyRepository, TicketService ticketService) {
        this.tickerReplyRepository = tickerReplyRepository;
        this.ticketService = ticketService;
    }

    public void save(TicketReplyRequest ticketReplyRequest) {
        log.info("Запрос на сохранение ответа на тикет: {}", ticketReplyRequest.getTicketId());
        TicketDTO ticketDTO = ticketService.findById(ticketReplyRequest.getTicketId());
        if (ticketDTO == null) {
            throw new TicketReplyException(
                    String.format("Тикет с ID %s не существует", ticketReplyRequest.getTicketId()));
        }

        TicketReply ticketReply = TicketReply.builder()
                .ticket(Ticket.builder().id(ticketReplyRequest.getTicketId()).build())
                .reply(ticketReplyRequest.getReply())
                .authorId(ticketReplyRequest.getAuthorId())
                .fileIds(ticketReplyRequest.getFileIds())
                .build();
        tickerReplyRepository.save(ticketReply);
        log.info("Ответ на тикет сохранен с ID: {}", ticketReply.getId());
    }

}
