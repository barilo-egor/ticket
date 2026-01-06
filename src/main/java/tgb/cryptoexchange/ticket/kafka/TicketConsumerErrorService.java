package tgb.cryptoexchange.ticket.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketConsumerErrorService {

    public void handle(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        log.error("Ошибка обработки записи: key={}, value={}", consumerRecord.key(), consumerRecord.value(), e);
    }

}
