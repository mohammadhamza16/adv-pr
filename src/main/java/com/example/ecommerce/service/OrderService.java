package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    public Order placeOrder(long userId, String shippingAddress) throws SQLException {
        Cart cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(shippingAddress);
        order.setStatus("CREATED");
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new IllegalStateException("Product is not available");
            }
            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product " + product.getName());
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        Order created = orderRepository.create(order);
        cartService.clearCart(userId);
        return created;
    }

    public List<Order> getOrdersForUser(long userId) throws SQLException {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderRepository.findAll();
    }
}
