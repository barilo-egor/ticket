package tgb.cryptoexchange.ticket.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tgb.cryptoexchange.ticket.service.TicketService;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TicketRequestProcessorService.class)
@Import(TicketRequestProcessorServiceTest.Config.class)
public class TicketRequestProcessorServiceTest {

    @Autowired
    private TicketRequestProcessorService processorService;

    @MockitoBean
    private TicketService ticketService;

    @TestConfiguration
    static class Config {
        @Bean
        public ThreadPoolTaskExecutor ticketRequestSaveExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.initialize();
            return executor;
        }
    }

    @Test
    @DisplayName("process должен асинхронно сохранить тикет")
    void process_ShouldSaveAsynchronously() {
        TicketRequest request = new TicketRequest();

        processorService.process(request);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(ticketService).save(request);
        });
    }

}
