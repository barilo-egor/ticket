package tgb.cryptoexchange.ticket.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.ticket.dto.TicketDTO;
import tgb.cryptoexchange.ticket.dto.TicketRequest;
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

        when(ticketService.findAll(any(), any())).thenReturn(page);

        mockMvc.perform(get("/ticket")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("findAll должен вернуть 204 No Content, если список пуст")
    void findAll_ShouldReturnNoContent_WhenEmpty() throws Exception {
        when(ticketService.findAll(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/ticket"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-100"}) // 1. Используем несколько некорректных значений
    @DisplayName("findAll должен вернуть 400 Bad Request при некорректном pageSize")
    void findAll_ShouldReturnBadRequest_WhenPageSizeIsInvalid(String invalidPageSize) throws Exception {
        mockMvc.perform(get("/ticket")
                        .param("pageSize", invalidPageSize))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("findById должен вернуть 404, если тикет не найден")
    void findById_ShouldReturnNotFound() throws Exception {
        when(ticketService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/ticket/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deleteById должен вызвать сервис и вернуть 200 OK")
    void deleteById_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/ticket/100"))
                .andExpect(status().isOk());

        verify(ticketService).deleteById(100L);
    }
}