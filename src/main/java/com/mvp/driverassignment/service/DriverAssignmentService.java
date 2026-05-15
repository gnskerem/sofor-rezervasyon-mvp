package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Önemli!
import java.util.List;
import java.util.Optional;

@Service
public class DriverAssignmentService {
    private final DriverRepository driverRepository;

    public DriverAssignmentService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional // Veritabanı tutarlılığı için ekledik
    public Optional<Driver> assignDriver(Reservation reservation, Integer passengerCount, String preferredVehicle) {
        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        Driver bestDriver = null;
        double bestScore = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            // Kapasite ve Araç Tipi Filtresi
            if (driver.getCapacity() < passengerCount || !driver.getVehicleType().equalsIgnoreCase(preferredVehicle)) {
                continue;
            }

            double score;

            // --- EVE DÖNÜŞ OPTİMİZASYONU ---
            if (Boolean.TRUE.equals(driver.getIsGoingHome()) && driver.getHomeLat() != null) {
                double distToHome = calculateDistance(
                        reservation.getPickupLat(), reservation.getPickupLng(),
                        driver.getHomeLat(), driver.getHomeLng()
                );
                // Evine yakın bir işse skoru çok düşürüp (0.2) öncelik veriyoruz
                score = (distToHome * 0.2) - (driver.getRating() * 0.5);
            } else {
                double distToPickup = calculateDistance(
                        driver.getLatitude(), driver.getLongitude(),
                        reservation.getPickupLat(), reservation.getPickupLng()
                );
                score = (distToPickup * 1.0) - (driver.getRating() * 0.3);
            }

            if (score < bestScore) {
                bestScore = score;
                bestDriver = driver;
            }
        }

        // --- ŞOFÖRÜ MEŞGUL OLARAK İŞARETLE ---
        if (bestDriver != null) {
            bestDriver.setAvailable(false); // Şoför artık müsait değil
            driverRepository.save(bestDriver); // Veritabanında güncelle
        }

        return Optional.ofNullable(bestDriver);
    }

    // Mesafe hesaplama metodu aynı kalıyor...
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }
}