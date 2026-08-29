package com.booking.resource_booking_system.service;

import com.booking.resource_booking_system.dto.ReservationRequest;
import com.booking.resource_booking_system.dto.ReservationResponse;
import com.booking.resource_booking_system.dto.ReservationUpdateRequest;
import com.booking.resource_booking_system.entity.Reservation;
import com.booking.resource_booking_system.entity.ReservationStatus;
import com.booking.resource_booking_system.entity.Resource;
import com.booking.resource_booking_system.entity.User;
import com.booking.resource_booking_system.exception.ReservationNotFoundException;
import com.booking.resource_booking_system.exception.ResourceNotFoundException;
import com.booking.resource_booking_system.exception.UnauthorizedException;
import com.booking.resource_booking_system.repository.ReservationRepository;
import com.booking.resource_booking_system.repository.ResourceRepository;
import com.booking.resource_booking_system.repository.UserRepository;
import com.booking.resource_booking_system.specification.ReservationSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    // CREATE RESERVATION
    public ReservationResponse createReservation(
            ReservationRequest request,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found"));

        if (!resource.isAvailable()) {
            throw new RuntimeException(
                    "Resource is not available");
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(savedReservation);
    }

    // GET RESERVATIONS
    // ADMIN -> all reservations
    // USER -> only own reservations
    public Page<ReservationResponse> getReservations(
            String username,
            boolean isAdmin,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        Specification<Reservation> specification =
                Specification
                        .where(ReservationSpecification.hasStatus(status))
                        .and(ReservationSpecification
                                .priceGreaterThanOrEqualTo(minPrice))
                        .and(ReservationSpecification
                                .priceLessThanOrEqualTo(maxPrice));

        if (!isAdmin) {

            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("user").get("id"),
                                    user.getId()
                            )
            );
        }

        Page<Reservation> reservations =
                reservationRepository.findAll(
                        specification,
                        pageable
                );

        return reservations.map(this::convertToResponse);
    }

    // GET RESERVATION BY ID
    // ADMIN -> any reservation
    // USER -> own reservation only
    public ReservationResponse getReservationById(
            Long id,
            String username,
            boolean isAdmin) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found"));

        if (isAdmin) {
            return convertToResponse(reservation);
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!reservation.getUser().getId()
                .equals(user.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to view this reservation");
        }

        return convertToResponse(reservation);
    }

    // GET USER RESERVATIONS
    public List<ReservationResponse> getUserReservations(
            String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Reservation> reservations =
                reservationRepository.findByUser(user);

        return reservations.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // UPDATE RESERVATION
    public ReservationResponse updateReservation(
            Long id,
            ReservationUpdateRequest request) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found"));

        Resource resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found"));

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());
        reservation.setStatus(request.getStatus());

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(updatedReservation);
    }

    // DELETE RESERVATION
    public void deleteReservation(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found"));

        reservationRepository.delete(reservation);
    }

    // ENTITY -> DTO
    private ReservationResponse convertToResponse(
            Reservation reservation) {

        ReservationResponse response =
                new ReservationResponse();

        response.setId(reservation.getId());

        response.setUserId(
                reservation.getUser().getId());

        response.setUsername(
                reservation.getUser().getUsername());

        response.setResourceId(
                reservation.getResource().getId());

        response.setResourceName(
                reservation.getResource().getName());

        response.setStartTime(
                reservation.getStartTime());

        response.setEndTime(
                reservation.getEndTime());

        response.setPrice(
                reservation.getPrice());

        response.setStatus(
                reservation.getStatus());

        return response;
    }
}


