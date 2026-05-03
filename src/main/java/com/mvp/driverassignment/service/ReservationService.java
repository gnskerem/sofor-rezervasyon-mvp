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
 * - Oluşturma
 * - Şoför atama tetikleme
 * - Şoförün görevi kabul etmesi
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
     */
    public Reservation createReservation(String customerName, Double pickupLat, Double pickupLng) {
        Reservation reservation = new Reservation(customerName, pickupLat, pickupLng);
        return reservationRepository.save(reservation);
    }

    /**
     * Belirtilen rezervasyona otomatik şoför atar.
     * Atama başarılı → durum ASSIGNED olur, şoför müsait değil işaretlenir.
     * Atama başarısız → hata mesajı döner.
     */
    public Reservation assignDriverToReservation(Long reservationId) {
        // 1. Rezervasyonu bul
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        // 2. Zaten atanmış mı?
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Bu rezervasyon zaten işleme alınmış. Mevcut durum: " + reservation.getStatus());
        }

        // 3. En iyi şoförü bul
        Optional<Driver> bestDriver = driverAssignmentService.assignDriver(reservation);

        if (bestDriver.isEmpty()) {
            throw new RuntimeException("Şu an müsait şoför bulunmuyor.");
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
     * Şoför görevi kabul eder → durum COMPLETED olur.
     * MVP'de "red etme" durumu yok, sadece kabul var.
     */
    public Reservation driverAcceptsJob(Long reservationId, Long driverId) {
        // 1. Rezervasyonu bul
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        // 2. Doğru şoför mü?
        if (!driverId.equals(reservation.getAssignedDriverId())) {
            throw new RuntimeException("Bu rezervasyon sana atanmamış. Atanan şoför ID: " + reservation.getAssignedDriverId());
        }

        // 3. Durumu kontrol et
        if (reservation.getStatus() != ReservationStatus.ASSIGNED) {
            throw new RuntimeException("Rezervasyon ASSIGNED durumunda değil. Mevcut: " + reservation.getStatus());
        }

        // 4. Tamamlandı olarak işaretle
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    /**
     * Tüm rezervasyonları listele.
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}
