package com.mvp.driverassignment.entity;

import jakarta.persistence.*;

/**
 * Şoför entity'si.
 * Turizm odaklı araç tipi ve yolcu kapasitesi alanları eklendi.
 */
@Entity
@Table(name = "driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Double rating;

    // true = müsait, false = meşgul veya pasif
    private Boolean available;

    // --- Turizm Sektörü İçin Eklenen Alanlar ---

    /**
     * Araç Tipi: "Sedan", "VIP Vito", "Minibus"
     */
    private String vehicleType;

    /**
     * Maksimum Yolcu Kapasitesi: 4, 7, 16
     */
    private Integer capacity;

    // ===================== Constructors =====================

    public Driver() {}

    // Constructor güncellendi
    public Driver(String name, Double latitude, Double longitude, Double rating,
                  Boolean available, String vehicleType, Integer capacity) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.available = available;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
    }

    // ===================== Getters & Setters =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", capacity=" + capacity +
                ", available=" + available +
                '}';
    }
}