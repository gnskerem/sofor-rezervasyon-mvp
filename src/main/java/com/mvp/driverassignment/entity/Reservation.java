package com.mvp.driverassignment.entity;

import jakarta.persistence.*;

/**
 * Rezervasyon entity'si.
 * Acente tarafından oluşturulur, sistem şoför atar.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private Double pickupLat;

    private Double pickupLng;

    /**
     * Rezervasyon durumu:
     * PENDING   → Şoför henüz atanmadı
     * ASSIGNED  → Şoför atandı, kabul bekleniyor
     * COMPLETED → Şoför kabul etti, görev aktif
     */
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    // Atanan şoförün ID'si (null ise henüz atanmadı)
    private Long assignedDriverId;

    // ===================== Enum =====================

    public enum ReservationStatus {
        PENDING, ASSIGNED, COMPLETED
    }

    // ===================== Constructors =====================

    public Reservation() {}

    public Reservation(String customerName, Double pickupLat, Double pickupLng) {
        this.customerName = customerName;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.status = ReservationStatus.PENDING;
    }

    // ===================== Getters & Setters =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Long getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(Long assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", customer='" + customerName + "', status=" + status + "}";
    }
}
