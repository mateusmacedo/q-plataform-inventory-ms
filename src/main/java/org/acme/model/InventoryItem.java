package org.acme.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity(name = "inventory_item")
public class InventoryItem extends PanacheEntity {

    @Column(length = 12, unique = true, nullable = false)
    public String sku;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Version
    public Long version;

    public InventoryItem() {
    }
    
    public InventoryItem(String sku) {
        this.sku = sku;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=\"" + id +
                "\", \"sku=\"" + sku +
                "\", \"createdAt=" + createdAt +
                "\", \"updatedAt=" + updatedAt +
                "\", \"version=" + version +
                "\"}";
    }
}
