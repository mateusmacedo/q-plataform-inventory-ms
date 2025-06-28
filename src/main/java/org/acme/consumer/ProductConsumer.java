package org.acme.consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;


@ApplicationScoped
public class ProductConsumer {
    Logger log = Logger.getLogger(ProductConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    /**
     * Método para processar mensagens de produtos recebidas.
     *
     * @param message Mensagem recebida contendo dados do produto
     */
    @Incoming("products-in")
    public Uni<Void> consume(Message<String> message) {
        var kafkaMetadata = message.getMetadata(IncomingKafkaRecordMetadata.class).orElse(null);
        String traceId = kafkaMetadata != null && kafkaMetadata.getHeaders().lastHeader("X-Trace-Id") != null
                ? new String(kafkaMetadata.getHeaders().lastHeader("X-Trace-Id").value())
                : "N/A";
        try {
            ProductInputDTO payload = objectMapper.readValue(message.getPayload(), ProductInputDTO.class);
            log.infof("[traceId=%s] Mensagem recebida do Kafka: id=%s, sku=%s, nome=%s", traceId, payload.getId(), payload.getSku(), payload.getName());
            message.ack();
        } catch (Exception e) {
            log.errorf("[traceId=%s] Falha ao processar mensagem do Kafka: %s", traceId, e.getMessage());
            return Uni.createFrom().failure(e);
        }
        return Uni.createFrom().voidItem();
    }
}
