// package com.booking.resource_booking_system.controller;

// import com.booking.resource_booking_system.dto.ReservationRequest;
// import com.booking.resource_booking_system.dto.ReservationResponse;
// import com.booking.resource_booking_system.service.ReservationService;

// import jakarta.validation.Valid;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/reservations")
// public class ReservationController {

//     private final ReservationService reservationService;

//     public ReservationController(ReservationService reservationService) {
//         this.reservationService = reservationService;
//     }

//     // CREATE RESERVATION
//     // USER identity comes from JWT
//     @PostMapping
//     public ResponseEntity<ReservationResponse> createReservation(
//             @Valid @RequestBody ReservationRequest request,
//             Authentication authentication) {

//         String username = authentication.getName();

//         ReservationResponse response =
//                 reservationService.createReservation(
//                         request,
//                         username
//                 );

//         return new ResponseEntity<>(
//                 response,
//                 HttpStatus.CREATED
//         );
//     }

//     // GET ALL RESERVATIONS
//     // ADMIN only
//     @GetMapping
//     public ResponseEntity<List<ReservationResponse>> getAllReservations() {

//         List<ReservationResponse> reservations =
//                 reservationService.getAllReservations();

//         return ResponseEntity.ok(reservations);
//     }

//     // GET MY RESERVATIONS
//     @GetMapping("/my")
//     public ResponseEntity<List<ReservationResponse>> getMyReservations(
//             Authentication authentication) {

//         String username = authentication.getName();

//         List<ReservationResponse> reservations =
//                 reservationService.getUserReservations(username);

//         return ResponseEntity.ok(reservations);
//     }

//     // GET RESERVATION BY ID
//     // ADMIN can view any reservation
//     // USER can view only their own reservation
//     @GetMapping("/{id}")
//     public ResponseEntity<ReservationResponse> getReservationById(
//             @PathVariable Long id,
//             Authentication authentication) {

//         String username = authentication.getName();

//         boolean isAdmin =
//                 authentication.getAuthorities()
//                         .stream()
//                         .anyMatch(authority ->
//                                 authority.getAuthority()
//                                         .equals("ROLE_ADMIN"));

//         ReservationResponse response =
//                 reservationService.getReservationById(
//                         id,
//                         username,
//                         isAdmin
//                 );

//         return ResponseEntity.ok(response);
//     }

//     // DELETE RESERVATION
//     // ADMIN only
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteReservation(
//             @PathVariable Long id) {

//         reservationService.deleteReservation(id);

//         return ResponseEntity.noContent().build();
//     }
// }
package com.booking.resource_booking_system.controller;

import com.booking.resource_booking_system.dto.ReservationRequest;
import com.booking.resource_booking_system.dto.ReservationResponse;
import com.booking.resource_booking_system.dto.ReservationUpdateRequest;
import com.booking.resource_booking_system.entity.ReservationStatus;
import com.booking.resource_booking_system.service.ReservationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }

    // CREATE RESERVATION
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        ReservationResponse response =
                reservationService.createReservation(
                        request,
                        username
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

   
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>>
    getAllReservations(

            @RequestParam(required = false)
            ReservationStatus status,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String sort
    ) {

        Pageable pageable;

        if (sort != null && !sort.isBlank()) {

            String[] sortParts = sort.split(",");

            String field = sortParts[0];

            Sort.Direction direction =
                    Sort.Direction.ASC;

            if (sortParts.length > 1 &&
                    sortParts[1].equalsIgnoreCase("desc")) {

                direction = Sort.Direction.DESC;
            }

            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(direction, field)
            );

        } else {

            pageable = PageRequest.of(
                    page,
                    size
            );
        }

        Page<ReservationResponse> reservations =
                reservationService.getAllReservations(
                        status,
                        minPrice,
                        maxPrice,
                        pageable
                );

        return ResponseEntity.ok(reservations);
    }

   
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations(
            Authentication authentication) {

        String username =
                authentication.getName();

        return ResponseEntity.ok(
                reservationService.getUserReservations(
                        username
                )
        );
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse>
    getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        String username =
                authentication.getName();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        ReservationResponse response =
                reservationService.getReservationById(
                        id,
                        username,
                        isAdmin
                );

        return ResponseEntity.ok(response);
    }
    

@PutMapping("/{id}")
public ResponseEntity<ReservationResponse> updateReservation(
        @PathVariable Long id,
        @Valid @RequestBody ReservationUpdateRequest request) {

    ReservationResponse response =
            reservationService.updateReservation(id, request);

    return ResponseEntity.ok(response);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
