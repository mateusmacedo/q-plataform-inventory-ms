package org.acme.consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;

import org.acme.dto.ProductInputDTO;
import org.acme.service.InventoryService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ProductConsumer {
    Logger log = Logger.getLogger(ProductConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    InventoryService inventoryService;

    /**
     * Método para processar mensagens de produtos recebidas.
     *
     * @param message Mensagem recebida contendo dados do produto
     */
    @Incoming("products-in")
    @ActivateRequestContext
    public Uni<Void> consume(Message<String> message) {
        log.infof("Mensagem recebida: %s", message.getPayload());

        Uni<ProductInputDTO> payloadUni = Uni.createFrom().item(() -> {
            try {
                return objectMapper.readValue(message.getPayload(), ProductInputDTO.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return payloadUni
                .onItem().transformToUni(inventoryService::registerItem)
                .invoke(message::ack)
                .invoke(item -> log.infof("Mensagem processada com sucesso: %s", item))
                .onFailure()
                .invoke(failure -> log.errorf("Mensagem falhou: %s",
                        failure.getMessage()))
                .replaceWithVoid();
    }
}
