package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Şoför atama mantığının tamamı bu serviste.
 *
 * Algoritma:
 *   1. Tüm müsait şoförleri getir
 *   2. Her şoför için score hesapla:
 *      score = distance * 0.5 - rating * 0.3
 *      (düşük score = daha iyi: yakın VE yüksek puanlı şoför kazanır)
 *   3. En düşük score'a sahip şoförü ata
 */
@Service
public class DriverAssignmentService {

    private final DriverRepository driverRepository;

    // Constructor injection — @Autowired yerine bu yöntem daha güvenli
    public DriverAssignmentService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Rezervasyon için en uygun şoförü bulur.
     * Şoför bulunamazsa Optional.empty() döner.
     */
    public Optional<Driver> assignDriver(Reservation reservation) {
        // 1. Müsait şoförleri getir
        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        if (availableDrivers.isEmpty()) {
            System.out.println("⚠ Müsait şoför bulunamadı!");
            return Optional.empty();
        }

        Driver bestDriver = null;
        double bestScore = Double.MAX_VALUE;

        // 2. Her şoför için score hesapla
        for (Driver driver : availableDrivers) {
            double distance = calculateDistance(
                    driver.getLatitude(), driver.getLongitude(),
                    reservation.getPickupLat(), reservation.getPickupLng()
            );

            // score formülü: düşük mesafe ve yüksek rating → düşük score → daha iyi
            double score = distance * 0.5 - driver.getRating() * 0.3;

            System.out.printf("Şoför: %-15s | Mesafe: %.4f | Rating: %.1f | Score: %.4f%n",
                    driver.getName(), distance, driver.getRating(), score);

            if (score < bestScore) {
                bestScore = score;
                bestDriver = driver;
            }
        }

        System.out.println("✓ Seçilen şoför: " + (bestDriver != null ? bestDriver.getName() : "YOK"));
        return Optional.ofNullable(bestDriver);
    }

    /**
     * Basit Euclidean distance hesabı.
     * Gerçek projede Haversine formülü kullanılmalı,
     * MVP için bu yeterli.
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = lat1 - lat2;
        double dLng = lng1 - lng2;
        return Math.sqrt(dLat * dLat + dLng * dLng);
    }
}
