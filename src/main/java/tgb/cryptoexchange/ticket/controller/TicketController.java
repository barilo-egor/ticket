package tgb.cryptoexchange.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<Page<TicketDTO>> findAll(
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String category,
            @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.findAllFilteredAndPaged(appId, userId, category, pageable);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> findById(@PathVariable Long id) {
       TicketDTO ticket = ticketService.findById(id);
       if(ticket == null) {
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        ticketService.deleteById(id);
    }

}
