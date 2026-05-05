package com.mvp.driverassignment.dto;

/**
 * Frontend'den gelen rezervasyon talebini karşılayan DTO.
 */
public class ReservationRequest {
    private String customerName;
    private Double pickupLat;
    private Double pickupLng;
    private Integer passengerCount; // Yeni eklenen alan
    private String preferredVehicle; // Yeni eklenen alan

    // ===================== Getters & Setters =====================
    // Hata buradaki metotların eksik olmasından kaynaklanıyor

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

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getPreferredVehicle() {
        return preferredVehicle;
    }

    public void setPreferredVehicle(String preferredVehicle) {
        this.preferredVehicle = preferredVehicle;
    }
}