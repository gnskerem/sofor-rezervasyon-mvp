package com.mvp.driverassignment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    public enum ReservationStatus {
        PENDING, ASSIGNED, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private Double pickupLat;
    private Double pickupLng;

    // --- Turizm İçin Eklenen Alanlar ---
    private Integer passengerCount;
    private String preferredVehicle;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Long assignedDriverId;
    private LocalDateTime createdAt;
    private Double totalPrice;

    // ===================== Constructors =====================

    public Reservation() {
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    // GÜNCELLENEN KISIM: 5 Parametreli Constructor (Fiyat hariç)
    // Çünkü rezervasyon ilk oluşturulduğunda fiyat henüz hesaplanmamıştır.
    public Reservation(String customerName, Double pickupLat, Double pickupLng, Integer passengerCount, String preferredVehicle) {
        this(); // Parametresiz constructor'ı çağırıp createdAt ve status ayarlarını yapar
        this.customerName = customerName;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.passengerCount = passengerCount;
        this.preferredVehicle = preferredVehicle;
    }

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Double getPickupLat() { return pickupLat; }
    public void setPickupLat(Double pickupLat) { this.pickupLat = pickupLat; }

    public Double getPickupLng() { return pickupLng; }
    public void setPickupLng(Double pickupLng) { this.pickupLng = pickupLng; }

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public String getPreferredVehicle() { return preferredVehicle; }
    public void setPreferredVehicle(String preferredVehicle) { this.preferredVehicle = preferredVehicle; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public Long getAssignedDriverId() { return assignedDriverId; }
    public void setAssignedDriverId(Long assignedDriverId) { this.assignedDriverId = assignedDriverId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}