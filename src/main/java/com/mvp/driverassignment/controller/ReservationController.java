package com.mvp.driverassignment.controller;

import com.mvp.driverassignment.dto.ReservationRequest;
import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.repository.DriverRepository;
import com.mvp.driverassignment.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller — tüm HTTP endpoint'leri buradan yönetilir.
 *
 * Endpoint listesi:
 *   POST   /reservation            → yeni rezervasyon oluştur
 *   POST   /assign/{id}            → rezervasyona şoför ata
 *   GET    /drivers                → tüm şoförleri listele
 *   POST   /driver/{id}/accept     → şoför görevi kabul eder
 *   GET    /reservations           → tüm rezervasyonları listele (bonus)
 */
@RestController
@RequestMapping("/reservations") // base path yok, endpoint'ler direkt root'ta
public class ReservationController {

    private final ReservationService reservationService;
    private final DriverRepository driverRepository;

    public ReservationController(ReservationService reservationService,
                                  DriverRepository driverRepository) {
        this.reservationService = reservationService;
        this.driverRepository = driverRepository;
    }

    // =====================================================
    // POST /reservation
    // Body: { "customerName": "Ali", "pickupLat": 41.01, "pickupLng": 28.97 }
    // Yeni rezervasyon oluşturur, PENDING durumunda döner
    // =====================================================
    @PostMapping("/reservation")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(
                    request.getCustomerName(),
                    request.getPickupLat(),
                    request.getPickupLng()
            );
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    // =====================================================
    // POST /assign/{id}
    // Path: rezervasyon ID'si
    // Otomatik en uygun şoförü atar
    // =====================================================
    @PostMapping("/assign/{id}")
    public ResponseEntity<?> assignDriver(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.assignDriverToReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    // =====================================================
    // GET /drivers
    // Tüm şoförleri döner (müsait ve meşgul hepsini)
    // =====================================================
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverRepository.findAll());
    }

    // =====================================================
    // POST /driver/{id}/accept
    // Path: şoför ID'si
    // Query param: reservationId
    // Örnek: POST /driver/1/accept?reservationId=1
    // Şoför görevi kabul eder → durum COMPLETED olur
    // =====================================================
    @PostMapping("/driver/{id}/accept")
    public ResponseEntity<?> acceptJob(@PathVariable Long id,
                                        @RequestParam Long reservationId) {
        try {
            Reservation reservation = reservationService.driverAcceptsJob(reservationId, id);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    // =====================================================
// POST /reservations/driver/{id}/release
// Şoförü tekrar müsait yapar (İş bittiğinde)
// =====================================================
    @PostMapping("/driver/{id}/release")
    public ResponseEntity<?> releaseDriver(@PathVariable Long id) {
        try {
            Driver driver = driverRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Şoför bulunamadı"));
            driver.setAvailable(true);
            driverRepository.save(driver);
            return ResponseEntity.ok("Şoför tekrar müsait.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    // =====================================================
    // GET /reservations (bonus - debug için)
    // Tüm rezervasyonları listeler
    // =====================================================
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}
