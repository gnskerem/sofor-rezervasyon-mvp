package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Turizm/VIP Transfer Odaklı Şoför Atama Servisi.
 */
@Service
public class DriverAssignmentService {

    private final DriverRepository driverRepository;

    public DriverAssignmentService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Rezervasyon için en uygun şoförü; yolcu sayısı, araç tipi ve mesafeye göre bulur.
     */
    public Optional<Driver> assignDriver(Reservation reservation, Integer passengerCount, String preferredVehicle) {
        // 1. Tüm müsait şoförleri getir
        List<Driver> allAvailable = driverRepository.findByAvailableTrue();

        // 2. Turizm Odaklı Sert Filtreleme (Kapasite ve Araç Tipi)
        List<Driver> suitableDrivers = allAvailable.stream()
                .filter(d -> d.getCapacity() >= passengerCount) // Yolcu sayısı sığmalı
                .filter(d -> d.getVehicleType().equalsIgnoreCase(preferredVehicle)) // İstenen segment olmalı
                .collect(Collectors.toList());

        if (suitableDrivers.isEmpty()) {
            System.out.println("⚠ Kriterlere uygun (kapasite/araç tipi) müsait şoför bulunamadı!");
            return Optional.empty();
        }

        Driver bestDriver = null;
        double bestScore = Double.MAX_VALUE;

        // 3. Uygun şoförler arasında en iyi skoru hesapla
        for (Driver driver : suitableDrivers) {
            double distance = calculateHaversineDistance(
                    driver.getLatitude(), driver.getLongitude(),
                    reservation.getPickupLat(), reservation.getPickupLng()
            );

            // VIP Servis Puanı: Mesafe önemli ama yüksek rating (şoför kalitesi) turizmde çok kritiktir.
            // Formül: Mesafe ağırlığı 0.7, Puan ağırlığı 0.3
            double score = (distance * 0.7) - (driver.getRating() * 0.3);

            System.out.printf("Filtre Uygun: %-12s | Araç: %-8s | Mesafe: %.2f km | Score: %.4f%n",
                    driver.getName(), driver.getVehicleType(), distance, score);

            if (score < bestScore) {
                bestScore = score;
                bestDriver = driver;
            }
        }

        return Optional.ofNullable(bestDriver);
    }

    /**
     * Haversine Formülü: Dünya üzerindeki iki koordinat arasındaki gerçek KM mesafesini hesaplar.
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Dünya yarıçapı (KM)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}