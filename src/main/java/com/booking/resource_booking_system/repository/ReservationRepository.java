package com.booking.resource_booking_system.repository;

import com.booking.resource_booking_system.entity.Reservation;
import com.booking.resource_booking_system.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long>,
                JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByUser(User user);
     boolean existsByResourceId(Long resourceId);
}