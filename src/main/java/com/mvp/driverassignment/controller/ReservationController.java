package com.mvp.driverassignment.controller;

import com.mvp.driverassignment.dto.ReservationRequest;
import com.mvp.driverassignment.entity.Driver;
import com.mvp.driverassignment.entity.Reservation;
import com.mvp.driverassignment.repository.DriverRepository;
import com.mvp.driverassignment.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final DriverRepository driverRepository;

    public ReservationController(ReservationService reservationService,
                                 DriverRepository driverRepository) {
        this.reservationService = reservationService;
        this.driverRepository = driverRepository;
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            // DTO'dan gelen yeni alanları (passengerCount ve preferredVehicle) Service'e gönderiyoruz
            Reservation reservation = reservationService.createReservation(
                    request.getCustomerName(),
                    request.getPickupLat(),
                    request.getPickupLng(),
                    request.getPassengerCount(),
                    request.getPreferredVehicle()
            );
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<?> assignDriver(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.assignDriverToReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverRepository.findAll());
    }

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

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}