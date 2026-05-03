package com.mvp.driverassignment.repository;

import com.mvp.driverassignment.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Driver için veritabanı işlemleri.
 * JpaRepository sayesinde findAll, findById, save, delete
 * metodları otomatik olarak gelir — yazmana gerek yok.
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Sadece müsait şoförleri getir
    List<Driver> findByAvailableTrue();
}
