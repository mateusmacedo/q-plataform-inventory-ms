package org.acme.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.dto.ProductInputDTO;
import org.acme.model.InventoryItem;
import org.acme.repository.ItemRepository;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;

/**
 * Serviço para gerenciar operações relacionadas ao inventário.
 * Este serviço pode incluir métodos para adicionar, remover ou atualizar itens
 * no inventário.
 */
@ApplicationScoped
public class InventoryService {
    Logger log = Logger.getLogger(InventoryService.class);

    @Inject
    ItemRepository itemRepository;

    /**
     * Método para adicionar um item ao inventário.
     * 
     * @param sku O SKU do item a ser adicionado.
     */
    public Uni<InventoryItem> registerItem(ProductInputDTO input) {
        return itemRepository.findBySku(input.getSku())
                // .onItem().ifNotNull().failWith(() -> new RuntimeException("Item com SKU já existe: " + input.getSku()))
                .onItem().ifNull().continueWith(() -> new InventoryItem(input.getSku()))
                .flatMap(itemRepository::persist)
                .onItem().invoke(item -> {
                    log.infof("Item registrado com sucesso: %s", item);
                })
                .onFailure().invoke(throwable -> {
                    log.errorf("Erro ao registrar item: %s, stack: %s", throwable.getMessage(),
                            throwable.getStackTrace());
                });
    }

    /**
     * Método para buscar um item pelo SKU.
     * 
     * @param sku O SKU do item a ser buscado.
     * @return Um Uni contendo o item encontrado ou vazio se não encontrado.
     */
    public Uni<InventoryItem> getItemBySku(String sku) {
        // Busca o item pelo SKU e registra logs de sucesso ou erro
        return itemRepository.findBySku(sku)
                .invoke(() -> log.infof("Buscando item com SKU: %s", sku))
                .onItem().ifNotNull().invoke(item -> {
                    log.infof("Item encontrado: %s", item);
                })
                .onItem().ifNull().failWith(() -> new RuntimeException("Item não encontrado com SKU: " + sku))
                .onFailure().invoke(throwable -> {
                    log.errorf("Erro ao buscar item: %s", throwable.getMessage());
                });
    }
}
