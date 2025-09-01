package com.globalbooks.shipping.repository;

import com.globalbooks.shipping.model.Shipment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ShipmentRepository {

    private final Map<Long, Shipment> shipments = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public Shipment save(Shipment shipment) {
        if (shipment.getId() == null) {
            shipment.setId(idCounter.incrementAndGet());
        }
        shipments.put(shipment.getId(), shipment);
        return shipment;
    }

    public Shipment findById(Long id) {
        return shipments.get(id);
    }

    public List<Shipment> findAll() {
        return new ArrayList<>(shipments.values());
    }
}
