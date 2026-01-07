package tgb.cryptoexchange.ticket.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import tgb.cryptoexchange.ticket.exception.DeserializeEventException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketReplyRequest {

    @NotNull(message = "ticketId must not be null")
    @JsonProperty(required = true)
    private Long ticketId;

    private String reply;

    @NotNull(message = "authorId must not be null")
    @JsonProperty(required = true)
    private Long authorId;

    private List<String> fileIds;

    public static class KafkaDeserializer implements Deserializer<TicketReplyRequest> {

        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        @Override
        public TicketReplyRequest deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return objectMapper.readValue(data, TicketReplyRequest.class);
            } catch (Exception e) {
                throw new DeserializeEventException("Error occurred while deserializer value: " + new String(data, StandardCharsets.UTF_8), e);
            }
        }
    }

}
