package com.sukinkea.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private String cartId;
    private LocalDateTime createdDate;
    private List<CartItem> books;
    private Double totalValueWithoutTax;
    private ShippingAndPlatformFee shippingAndPlatformFee;
    private String deliveryMethod;
    private Integer totalQuantity;
    private Integer totalItemsSelectedForCheckout;
}
