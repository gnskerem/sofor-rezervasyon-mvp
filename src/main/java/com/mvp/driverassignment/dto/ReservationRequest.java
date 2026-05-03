package com.mvp.driverassignment.dto;

/**
 * POST /reservation endpoint'i için request body.
 * JSON'dan Java nesnesine otomatik dönüşüm sağlar.
 */
public class ReservationRequest {

    private String customerName;
    private Double pickupLat;
    private Double pickupLng;

    // Jackson için boş constructor şart
    public ReservationRequest() {}

    public ReservationRequest(String customerName, Double pickupLat, Double pickupLng) {
        this.customerName = customerName;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Double getPickupLat() { return pickupLat; }
    public void setPickupLat(Double pickupLat) { this.pickupLat = pickupLat; }

    public Double getPickupLng() { return pickupLng; }
    public void setPickupLng(Double pickupLng) { this.pickupLng = pickupLng; }
}
