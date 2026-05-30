package com.sukinkea.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAndPlatformFee {
    private Double shippingFee;
    private Double platformFee;
    private Double totalAdditionalFees;
    private Double freeShippingThreshold;
    private Boolean isFreeShippingEligible;
    private Double amountRemainingForFreeShipping;
    private String shippingMethodName;
    private String estimatedDeliveryDate;
    private String feeMessage;
}
