package tgb.cryptoexchange.ticket.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.service.TicketService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @Test
    @DisplayName("findAll должен вернуть 200 OK и список тикетов")
    void findAll_ShouldReturnOk_WhenTicketsExist() throws Exception {
        TicketDTO ticket = new TicketDTO();
        ticket.setId(1L);
        Page<TicketDTO> page = new PageImpl<>(Collections.singletonList(ticket));

        when(ticketService.findAllFilteredAndPaged(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("findAll должен вернуть 204 No Content, если список пуст")
    void findAll_ShouldReturnNoContent_WhenEmpty() throws Exception {
        when(ticketService.findAllFilteredAndPaged(any(), any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("findById должен вернуть 404, если тикет не найден")
    void findById_ShouldReturnNotFound() throws Exception {
        when(ticketService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deleteById должен вызвать сервис и вернуть 200 OK")
    void deleteById_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/tickets/100"))
                .andExpect(status().isOk());

        verify(ticketService).deleteById(100L);
    }
}