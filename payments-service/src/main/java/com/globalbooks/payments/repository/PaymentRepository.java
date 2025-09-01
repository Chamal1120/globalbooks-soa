package com.globalbooks.payments.repository;

import com.globalbooks.payments.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PaymentRepository {

    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idCounter.incrementAndGet());
        }
        payments.put(payment.getId(), payment);
        return payment;
    }

    public Payment findById(Long id) {
        return payments.get(id);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }
}
