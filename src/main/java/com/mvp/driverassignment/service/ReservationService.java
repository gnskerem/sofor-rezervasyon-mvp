package com.mvp.driverassignment.service;

import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.entity.Reservation.ReservationStatus;
import com.mvp.driverassignment.repository.DriverRepository;
import com.mvp.driverassignment.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
     * Yeni rezervasyon oluşturur (Aynı isimde aktif kayıt varsa ENGELLER).
     */
    public Reservation createReservation(String customerName, Double pickupLat, Double pickupLng,
                                         Integer passengerCount, String preferredVehicle) {

        // 1. AYNI MÜŞTERİNİN AKTİF İŞİ VAR MI KONTROL ET
        // PENDING (Bekliyor) veya ASSIGNED (Yolda) durumunda bir kaydı varsa yeni iş açma
        List<Reservation> activeReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getCustomerName().equalsIgnoreCase(customerName))
                .filter(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.ASSIGNED)
                .toList();

        if (!activeReservations.isEmpty()) {
            throw new RuntimeException("Sayın " + customerName + ", zaten aktif bir yolculuğunuz veya bekleyen talebiniz var!");
        }

        // 2. Eğer aktif kaydı yoksa yeni rezervasyon oluştur
        Reservation reservation = new Reservation(customerName, pickupLat, pickupLng, passengerCount, preferredVehicle);
        return reservationRepository.save(reservation);
    }

    /**
     * Güncellendi: Artık çok daha sıkı bir kontrol mekanizmasına sahip.
     * @Transactional eklendi: Veritabanı işlemlerinin atomik olmasını sağlar.
     */
    @Transactional
    public Reservation assignDriverToReservation(Long reservationId) {
        // 1. Rezervasyonu bul
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        // 2. KISITLAMA 1: Rezervasyon zaten atanmış mı? (Mükerrer atama engeli)
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Bu rezervasyon zaten işleme alınmış (Atanmış veya Tamamlanmış).");
        }

        // 3. En iyi şoförü bul
        Optional<Driver> bestDriver = driverAssignmentService.assignDriver(
                reservation,
                reservation.getPassengerCount(),
                reservation.getPreferredVehicle()
        );

        if (bestDriver.isEmpty()) {
            throw new RuntimeException("Kriterlerinize uygun müsait şoför bulunamadı.");
        }

        Driver driver = bestDriver.get();

        // 4. KISITLAMA 2: Şoförün meşguliyet kontrolü (Ekstra güvenlik katmanı)
        // isAvailable() yerine getAvailable() kullan
        if (!driver.getAvailable()) {
            throw new RuntimeException("Seçilen şoför şu an başka bir görevde.");
        }

        // 5. Şoförü meşgul işaretle
        driver.setAvailable(false);
        driverRepository.save(driver);

        // 6. Rezervasyonu güncelle
        reservation.setAssignedDriverId(driver.getId());
        reservation.setStatus(ReservationStatus.ASSIGNED);

        return reservationRepository.save(reservation);
    }

    /**
     * Şoför görevi kabul eder/bitirir -> durum COMPLETED olur.
     * Burada şoförü tekrar "Available" yapabiliriz (opsiyonel, senin release metodunla da olabilir).
     */
    @Transactional
    public Reservation driverAcceptsJob(Long reservationId, Long driverId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        if (!driverId.equals(reservation.getAssignedDriverId())) {
            throw new RuntimeException("Bu rezervasyon size atanmamış.");
        }

        if (reservation.getStatus() != ReservationStatus.ASSIGNED) {
            throw new RuntimeException("Rezervasyon tamamlanacak durumda değil.");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}