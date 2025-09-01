package com.globalbooks.orders.repository;

import com.globalbooks.orders.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idCounter.incrementAndGet());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Order findById(Long id) {
        return orders.get(id);
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
