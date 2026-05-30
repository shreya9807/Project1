package com.sukinkea.cart.controller;

import com.sukinkea.cart.model.ApiResponse;
import com.sukinkea.cart.model.Cart;
import com.sukinkea.cart.model.CartItem;
import com.sukinkea.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<ApiResponse<Cart>> getCart() {
        log.trace("GET /cart request received");
        return cartService.getCart()
                .map(cart -> new ApiResponse<>("success", "Cart retrieved successfully", cart));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<Cart>> addBookToCart(@RequestBody CartItem item) {
        log.trace("POST /cart request received for SKU: {}", item.getSkuDetails().getSkuId());
        return cartService.addBookToCart(item)
                .map(cart -> new ApiResponse<>("success", "Item added to cart", cart));
    }

    @PutMapping("/{skuId}")
    public Mono<ApiResponse<Cart>> updateQuantity(@PathVariable String skuId, @RequestParam int quantity) {
        log.trace("PUT /cart/{} request received with quantity: {}", skuId, quantity);
        return cartService.updateQuantityOfBooksIntheCart(skuId, quantity)
                .map(cart -> new ApiResponse<>("success", "Cart updated successfully", cart));
    }

    @DeleteMapping("/{skuId}")
    public Mono<ApiResponse<Cart>> deleteBook(@PathVariable String skuId) {
        log.trace("DELETE /cart/{} request received", skuId);
        return cartService.deleteBooksFromCart(skuId)
                .map(cart -> new ApiResponse<>("success", "Item removed from cart", cart));
    }
}
