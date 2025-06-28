package org.acme.repository;

import javax.enterprise.context.ApplicationScoped;

import org.acme.model.InventoryItem;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

/**
 * Repositório para gerenciar operações relacionadas à entidade Item.
 * Utiliza Panache para operações reativas com o banco de dados.
 */
@ApplicationScoped
public class ItemRepository implements PanacheRepository<InventoryItem> {
    
    public Uni<InventoryItem> persist(InventoryItem item) {
        return Panache.withTransaction(item::persist)
        .replaceWith(item);
    }
    
    public Uni<InventoryItem> findBySku(String sku) {
        return find("sku = ?1 and deletedAt is null", sku).firstResult();
    }
}
