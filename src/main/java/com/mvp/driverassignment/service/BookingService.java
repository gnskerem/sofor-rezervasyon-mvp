package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.model.ReservationStatus; // Eğer Enum buradaysa
import com.mvp.driverassignment.repository.DriverRepository;
import com.mvp.driverassignment.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation createBooking(String customerName, double lat, double lng) {
        // 1. Senin yazdığın metod sayesinde sadece müsait şoförleri çekiyoruz
        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();

        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("Şu an müsait şoför bulunmamaktadır.");
        }

        // 2. En yakın şoförü bul
        Driver nearestDriver = availableDrivers.stream()
                .min(Comparator.comparingDouble(d ->
                        Math.sqrt(Math.pow(d.getLatitude() - lat, 2) + Math.pow(d.getLongitude() - lng, 2))))
                .orElseThrow();

        // 3. Rezervasyonu oluştur
        Reservation res = new Reservation();
        res.setCustomerName(customerName);
        res.setPickupLat(lat);
        res.setPickupLng(lng);
        res.setAssignedDriverId(nearestDriver.getId());
        res.setStatus(Reservation.ReservationStatus.ASSIGNED);

        // 4. Şoförü meşgul yap ve kaydet
        nearestDriver.setAvailable(false);
        driverRepository.save(nearestDriver);

        return reservationRepository.save(res);
    }
}