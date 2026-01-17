package tgb.cryptoexchange.ticket.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.dto.TicketRequest;
import tgb.cryptoexchange.ticket.service.TicketService;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketController extends ApiController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketDTO>>> findAll(@Valid @ModelAttribute TicketRequest ticketRequest, @PageableDefault(size = 20) Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.findAll(pageable, ticketRequest);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tickets.getTotalElements()))
                .body(ApiResponse.success(tickets.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDTO>> findById(@PathVariable Long id) {
        return ticketService.findById(id)
                .map(ticket -> new ResponseEntity<>(ApiResponse.success(
                        TicketDTO.fromEntity(ticket)),
                        HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        ticketService.deleteById(id);
    }

}
