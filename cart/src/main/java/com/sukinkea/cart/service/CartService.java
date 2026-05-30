package com.sukinkea.cart.service;

import com.sukinkea.cart.model.Cart;
import com.sukinkea.cart.model.CartItem;
import com.sukinkea.cart.model.ShippingAndPlatformFee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.sukinkea.cart.constants.Constants.*;


@Service
@Slf4j
public class CartService {

    private final Map<String, CartItem> cartItems = new ConcurrentHashMap<>();
    
    private final String currentCartId = UUID.randomUUID().toString();
    private final LocalDateTime createdDate = LocalDateTime.now();

    public Mono<Cart> getCart() {
        log.info("Retrieving cart: {}", currentCartId);
        return Mono.just(buildCart());
    }

    public Mono<Cart> addBookToCart(CartItem newItem) {
        return Mono.defer(() -> {
            String skuId = newItem.getSkuDetails().getSkuId();
            log.info("Adding item to cart. SKU: {}, Quantity: {}", skuId, newItem.getQuantity());
            
            CartItem existing = cartItems.get(skuId);
            int currentQty = (existing != null) ? existing.getQuantity() : 0;
            int newQty = currentQty + newItem.getQuantity();

            if (newQty > MAX_QUANTITY) {
                log.warn("Exceeded max quantity for SKU: {}. Current: {}, Attempted: {}", skuId, currentQty, newItem.getQuantity());
                return Mono.error(new IllegalArgumentException("Cannot add more than " + MAX_QUANTITY + " quantities for SKU: " + skuId));
            }

            CartItem updatedItem = CartItem.builder()
                    .skuDetails(newItem.getSkuDetails())
                    .quantity(newQty)
                    .offerDetails(newItem.getOfferDetails())
                    .build();
            
            cartItems.put(skuId, updatedItem);
            return Mono.just(buildCart());
        });
    }

    public Mono<Cart> updateQuantityOfBooksIntheCart(String skuId, int quantity) {
        return Mono.defer(() -> {
            log.info("Updating quantity for SKU: {} to {}", skuId, quantity);
            if (quantity > MAX_QUANTITY) {
                return Mono.error(new IllegalArgumentException("Quantity cannot exceed " + MAX_QUANTITY));
            }
            CartItem existing = cartItems.get(skuId);
            if (existing == null) {
                return Mono.error(new NoSuchElementException("Item not found in cart: " + skuId));
            }

            if (quantity <= 0) {
                cartItems.remove(skuId);
            } else {
                existing.setQuantity(quantity);
                cartItems.put(skuId, existing);
            }
            return Mono.just(buildCart());
        });
    }

    public Mono<Cart> deleteBooksFromCart(String skuId) {
        return Mono.fromRunnable(() -> cartItems.remove(skuId))
                .then(Mono.fromCallable(this::buildCart));
    }

    private Cart buildCart() {
        List<CartItem> items = new ArrayList<>(cartItems.values());
        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        
        // Base price of 100.0 per item for demonstration
        double totalValueWithoutTax = items.stream()
                .mapToDouble(item -> 100.0 * item.getQuantity())
                .sum();

        ShippingAndPlatformFee fees = calculateFees(totalValueWithoutTax);

        return Cart.builder()
                .cartId(currentCartId)
                .createdDate(createdDate)
                .books(items)
                .totalValueWithoutTax(totalValueWithoutTax)
                .shippingAndPlatformFee(fees)
                .deliveryMethod(fees.getShippingMethodName())
                .totalQuantity(totalQuantity)
                .totalItemsSelectedForCheckout(items.size())
                .build();
    }

    private ShippingAndPlatformFee calculateFees(double totalValue) {
        boolean isFreeShipping = totalValue >= FREE_SHIPPING_THRESHOLD;
        double shippingFee = isFreeShipping ? 0.0 : STANDARD_SHIPPING_FEE;
        double remainingForFree = isFreeShipping ? 0.0 : FREE_SHIPPING_THRESHOLD - totalValue;
        
        String feeMessage = isFreeShipping 
            ? "You've unlocked FREE shipping!" 
            : String.format("Add ₹%.2f more for FREE shipping", remainingForFree);

        return ShippingAndPlatformFee.builder()
                .shippingFee(shippingFee)
                .platformFee(PLATFORM_FEE)
                .totalAdditionalFees(shippingFee + PLATFORM_FEE)
                .freeShippingThreshold(FREE_SHIPPING_THRESHOLD)
                .isFreeShippingEligible(isFreeShipping)
                .amountRemainingForFreeShipping(remainingForFree)
                .shippingMethodName("Standard Delivery")
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .feeMessage(feeMessage)
                .build();
    }
}
