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
     * Yeni rezervasyon oluşturur (Mükerrer kayıt engelli).
     */
    public Reservation createReservation(String customerName, Double pickupLat, Double pickupLng,
                                         Integer passengerCount, String preferredVehicle) {

        List<Reservation> activeReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getCustomerName().equalsIgnoreCase(customerName))
                .filter(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.ASSIGNED)
                .toList();

        if (!activeReservations.isEmpty()) {
            throw new RuntimeException("Sayın " + customerName + ", zaten aktif bir talebiniz var!");
        }

        return reservationRepository.save(new Reservation(customerName, pickupLat, pickupLng, passengerCount, preferredVehicle));
    }

    /**
     * 3. ADIM UYGULANDI: Şoför atama, meşguliyet yönetimi ve Dinamik Fiyatlandırma.
     */
    @Transactional
    public Reservation assignDriverToReservation(Long reservationId) {
        // 1. Rezervasyonu bul
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: ID=" + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Bu rezervasyon zaten işleme alınmış.");
        }

        // 2. Şoförü bul (Müsaitlik ve işaretleme zaten bu servisin içinde yapılıyor)
        Optional<Driver> bestDriver = driverAssignmentService.assignDriver(
                reservation,
                reservation.getPassengerCount(),
                reservation.getPreferredVehicle()
        );

        if (bestDriver.isEmpty()) {
            throw new RuntimeException("Kriterlere uygun müsait şoför bulunamadı.");
        }

        Driver driver = bestDriver.get();

        // --- BURADAKİ EXTRA AVAILABLE KONTROLÜNÜ VE driverRepository.save KISMINI SİLDİK ---
        // Çünkü DriverAssignmentService zaten bu şoförü false yapıp kaydetti.

        // 3. Rezervasyonu güncelle
        reservation.setAssignedDriverId(driver.getId());
        reservation.setStatus(ReservationStatus.ASSIGNED);

        // 4. Fiyat hesaplama (Mevcut kodun aynısı...)
        double distanceKm = calculateDistance(reservation.getPickupLat(), reservation.getPickupLng(),
                driver.getLatitude(), driver.getLongitude());

        double perKm = reservation.getPreferredVehicle().contains("Vito") ? 45.0 : 25.0;
        double calculatedPrice = 50.0 + (distanceKm * perKm);
        reservation.setTotalPrice(Math.round(calculatedPrice * 100.0) / 100.0);

        return reservationRepository.save(reservation);
    }

    /**
     * Şoför görevi tamamladığında çağrılır.
     */
    @Transactional
    public Reservation driverAcceptsJob(Long reservationId, Long driverId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı."));

        if (!driverId.equals(reservation.getAssignedDriverId())) {
            throw new RuntimeException("Bu görev size ait değil.");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Haversine Formülü: İki koordinat arası mesafeyi (KM) hesaplar.
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}