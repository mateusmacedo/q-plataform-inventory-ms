package org.acme.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInputDTO {
    private Long id;
    private String sku;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}