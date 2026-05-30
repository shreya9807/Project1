package com.sukinkea.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private SkuDetails skuDetails;
    private Integer quantity;
    private OfferDetails offerDetails;
}
