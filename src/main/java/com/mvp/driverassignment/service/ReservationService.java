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

        // 2. Durum kontrolü
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Bu rezervasyon zaten işleme alınmış.");
        }

        // 3. Akıllı algoritma ile şoförü bul (Eve dönüş desteği DriverAssignmentService içinde)
        Optional<Driver> bestDriver = driverAssignmentService.assignDriver(
                reservation,
                reservation.getPassengerCount(),
                reservation.getPreferredVehicle()
        );

        if (bestDriver.isEmpty()) {
            throw new RuntimeException("Kriterlere uygun müsait şoför bulunamadı.");
        }

        Driver driver = bestDriver.get();

        // 4. Şoför müsaitlik kontrolü ve kilitleme
        if (!Boolean.TRUE.equals(driver.getAvailable())) {
            throw new RuntimeException("Şoför şu an başka bir görevde.");
        }

        // 5. Şoförü meşgul işaretle
        driver.setAvailable(false);
        driverRepository.save(driver);

        // 6. Rezervasyonu güncelle (Atanan Şoför ve Durum)
        reservation.setAssignedDriverId(driver.getId());
        reservation.setStatus(ReservationStatus.ASSIGNED);

        // 7. FİYAT HESAPLAMA (Mesafe Bazlı)
        // Şoförün mevcut konumu ile müşterinin alış noktası arasındaki mesafeyi ölçüyoruz
        double distanceKm = calculateDistance(reservation.getPickupLat(), reservation.getPickupLng(),
                driver.getLatitude(), driver.getLongitude());

        double basePrice = 50.0; // Açılış Ücreti
        double perKm;

        // Araç tipine göre katsayı (VIP stratejisi)
        if (reservation.getPreferredVehicle().contains("Vito")) {
            perKm = 45.0;
        } else if (reservation.getPreferredVehicle().contains("Minibus")) {
            perKm = 60.0;
        } else {
            perKm = 25.0; // Standart Sedan
        }

        double calculatedPrice = basePrice + (distanceKm * perKm);

        // Fiyatı 2 hane yuvarlayıp set ediyoruz
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