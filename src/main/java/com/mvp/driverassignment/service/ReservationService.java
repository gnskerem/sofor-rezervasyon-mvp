package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.entity.Reservation.ReservationStatus;
import com.mvp.driverassignment.repository.DriverRepository;
import com.mvp.driverassignment.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Rezervasyon işlemleri:
 * Turizm odaklı kapasite ve araç tipi parametreleri eklendi.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DriverRepository driverRepository;
    private final DriverAssignmentService driverAssignmentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            DriverRepository driverRepository,
            DriverAssignmentService driverAssignmentService) {
        this.reservationRepository = reservationRepository;
        this.driverRepository = driverRepository;
        this.driverAssignmentService = driverAssignmentService;
    }

    /**
     * Yeni rezervasyon oluşturur (PENDING durumunda).
     * Güncellendi: Yolcu sayısı ve araç tercihi parametreleri eklendi.
     */
    public Reservation createReservation(String customerName, Double pickupLat, Double pickupLng,
                                         Integer passengerCount, String preferredVehicle) {
        Reservation reservation = new Reservation(customerName, pickupLat, pickupLng, passengerCount, preferredVehicle);
        return reservationRepository.save(reservation);
    }

    /**
     * Belirtilen rezervasyona otomatik şoför atar.
     * DÜZELTİLDİ: DriverAssignmentService'in yeni 3 parametreli yapısına uygun hale getirildi.
     */
    public Reservation assignDriverToReservation(Long reservationId) {
        // 1. Rezervasyonu bul
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        // 2. Zaten atanmış mı kontrol et
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Bu rezervasyon zaten işleme alınmış. Mevcut durum: " + reservation.getStatus());
        }

        // 3. En iyi şoförü bul (Yeni parametrelerle birlikte)
        // HATA BURADAYDI: Rezervasyon içindeki passengerCount ve preferredVehicle alanları eklendi.
        Optional<Driver> bestDriver = driverAssignmentService.assignDriver(
                reservation,
                reservation.getPassengerCount(),
                reservation.getPreferredVehicle()
        );

        if (bestDriver.isEmpty()) {
            throw new RuntimeException("Kriterlerinize uygun (Kişi sayısı/Araç tipi) müsait şoför bulunamadı.");
        }

        // 4. Şoförü meşgul işaretle
        Driver driver = bestDriver.get();
        driver.setAvailable(false);
        driverRepository.save(driver);

        // 5. Rezervasyonu güncelle
        reservation.setAssignedDriverId(driver.getId());
        reservation.setStatus(ReservationStatus.ASSIGNED);
        return reservationRepository.save(reservation);
    }

    /**
     * Şoför görevi kabul eder -> durum COMPLETED olur.
     */
    public Reservation driverAcceptsJob(Long reservationId, Long driverId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        if (!driverId.equals(reservation.getAssignedDriverId())) {
            throw new RuntimeException("Bu rezervasyon sana atanmamış.");
        }

        if (reservation.getStatus() != ReservationStatus.ASSIGNED) {
            throw new RuntimeException("Rezervasyon uygun durumda değil.");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}