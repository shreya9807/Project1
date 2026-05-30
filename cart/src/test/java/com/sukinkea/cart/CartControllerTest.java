package com.sukinkea.cart;

import com.sukinkea.cart.controller.CartController;
import com.sukinkea.cart.model.CartItem;
import com.sukinkea.cart.model.SkuDetails;
import com.sukinkea.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(CartController.class)
@Import(CartService.class)
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testFreeShippingLogic() {
        // Add 5 items (each 100.0) to reach 500.0 threshold
        // Note: Max 2 per SKU, so we add 3 different SKUs
        
        addSku("sku1", 2);
        addSku("sku2", 2);
        
        // At 400.0, should NOT have free shipping
        webTestClient.get()
                .uri("/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.shippingAndPlatformFee.isFreeShippingEligible").isEqualTo(false)
                .jsonPath("$.data.shippingAndPlatformFee.shippingFee").isEqualTo(40.0)
                .jsonPath("$.data.shippingAndPlatformFee.amountRemainingForFreeShipping").isEqualTo(100.0);

        // Add 1 more (Total 500.0)
        addSku("sku3", 1);

        // At 500.0, SHOULD have free shipping
        webTestClient.get()
                .uri("/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.shippingAndPlatformFee.isFreeShippingEligible").isEqualTo(true)
                .jsonPath("$.data.shippingAndPlatformFee.shippingFee").isEqualTo(0.0)
                .jsonPath("$.data.shippingAndPlatformFee.feeMessage").isEqualTo("You've unlocked FREE shipping!");
    }

    private void addSku(String skuId, int qty) {
        SkuDetails sku = SkuDetails.builder().skuId(skuId).build();
        CartItem item = CartItem.builder().skuDetails(sku).quantity(qty).build();
        webTestClient.post()
                .uri("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange();
    }
}
