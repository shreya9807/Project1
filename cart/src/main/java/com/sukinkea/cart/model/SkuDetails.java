package com.sukinkea.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuDetails {
    private String skuId;
    private String imageUrl;
    private String brandName;
    private String displayName;
    private String productId;
    private String ml;
}
