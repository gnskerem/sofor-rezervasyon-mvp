package com.mvp.driverassignment.repository;

import com.mvp.driverassignment.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Reservation için veritabanı işlemleri.
 * MVP'de ekstra metod gerekmiyor, JpaRepository yeterli.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Şimdilik standart metodlar yeterli
}
