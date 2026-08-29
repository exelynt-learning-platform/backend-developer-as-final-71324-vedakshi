package com.booking.resource_booking_system.service;

import com.booking.resource_booking_system.dto.ReservationRequest;
import com.booking.resource_booking_system.dto.ReservationResponse;
import com.booking.resource_booking_system.dto.ReservationUpdateRequest;
import com.booking.resource_booking_system.entity.Reservation;
import com.booking.resource_booking_system.entity.ReservationStatus;
import com.booking.resource_booking_system.entity.Resource;
import com.booking.resource_booking_system.entity.User;
import com.booking.resource_booking_system.repository.ReservationRepository;
import com.booking.resource_booking_system.repository.ResourceRepository;
import com.booking.resource_booking_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
private ResourceService resourceService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Resource resource;
    private ReservationRequest request;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setPassword("password");
        user.setRole("USER");

        resource = new Resource();

        resource.setId(1L);
        resource.setName("Meeting Room");
        resource.setType("Room");
        resource.setDescription("Meeting room");
        resource.setAvailable(true);

        request = new ReservationRequest();

        request.setResourceId(1L);

        request.setStartTime(
                LocalDateTime.of(2026, 9, 1, 10, 0)
        );

        request.setEndTime(
                LocalDateTime.of(2026, 9, 1, 12, 0)
        );

        request.setPrice(
                new BigDecimal("500.00")
        );
    }

    @Test
    void createReservation_shouldCreateReservationSuccessfully() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {

                    Reservation reservation =
                            invocation.getArgument(0);

                    reservation.setId(1L);

                    return reservation;
                });

        ReservationResponse response =
                reservationService.createReservation(
                        request,
                        "user"
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "user",
                response.getUsername()
        );

        assertEquals(
                "Meeting Room",
                response.getResourceName()
        );

        assertEquals(
                new BigDecimal("500.00"),
                response.getPrice()
        );

        assertEquals(
                ReservationStatus.PENDING,
                response.getStatus()
        );

        verify(
                reservationRepository,
                times(1)
        ).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldFailWhenUserNotFound() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(
                                        request,
                                        "user"
                                )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(
                reservationRepository,
                never()
        ).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldFailWhenResourceNotFound() {

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(
                                        request,
                                        "user"
                                )
                );

        assertEquals(
                "Resource not found",
                exception.getMessage()
        );

        verify(
                reservationRepository,
                never()
        ).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldFailWhenResourceUnavailable() {

        resource.setAvailable(false);

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(
                                        request,
                                        "user"
                                )
                );

        assertEquals(
                "Resource is not available",
                exception.getMessage()
        );

        verify(
                reservationRepository,
                never()
        ).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldFailWhenEndTimeIsBeforeStartTime() {

        request.setEndTime(
                LocalDateTime.of(2026, 9, 1, 9, 0)
        );

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(
                                        request,
                                        "user"
                                )
                );

        assertEquals(
                "End time must be after start time",
                exception.getMessage()
        );

        verify(
                reservationRepository,
                never()
        ).save(any(Reservation.class));
    }
    @Test
void getReservationById_shouldAllowAdminToViewAnyReservation() {

    User anotherUser = new User();
    anotherUser.setId(2L);
    anotherUser.setUsername("anotherUser");

    Reservation reservation = new Reservation();
    reservation.setId(1L);
    reservation.setUser(anotherUser);
    reservation.setResource(resource);
    reservation.setStartTime(
            LocalDateTime.of(2026, 9, 1, 10, 0)
    );
    reservation.setEndTime(
            LocalDateTime.of(2026, 9, 1, 12, 0)
    );
    reservation.setPrice(new BigDecimal("500.00"));
    reservation.setStatus(ReservationStatus.PENDING);

    when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(reservation));

    ReservationResponse response =
            reservationService.getReservationById(
                    1L,
                    "admin",
                    true
            );

    assertNotNull(response);

    assertEquals(1L, response.getId());

    assertEquals(
            "anotherUser",
            response.getUsername()
    );

    verify(reservationRepository, times(1))
            .findById(1L);
}
@Test
void getReservationById_shouldAllowUserToViewOwnReservation() {

    Reservation reservation = new Reservation();

    reservation.setId(1L);
    reservation.setUser(user);
    reservation.setResource(resource);
    reservation.setStartTime(
            LocalDateTime.of(2026, 9, 1, 10, 0)
    );
    reservation.setEndTime(
            LocalDateTime.of(2026, 9, 1, 12, 0)
    );
    reservation.setPrice(new BigDecimal("500.00"));
    reservation.setStatus(ReservationStatus.PENDING);

    when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(reservation));

    when(userRepository.findByUsername("user"))
            .thenReturn(Optional.of(user));

    ReservationResponse response =
            reservationService.getReservationById(
                    1L,
                    "user",
                    false
            );

    assertNotNull(response);

    assertEquals(1L, response.getId());

    assertEquals(
            "user",
            response.getUsername()
    );

    assertEquals(
            ReservationStatus.PENDING,
            response.getStatus()
    );
}
@Test
void getReservationById_shouldRejectUserViewingAnotherUsersReservation() {

    User anotherUser = new User();

    anotherUser.setId(2L);
    anotherUser.setUsername("anotherUser");

    Reservation reservation = new Reservation();

    reservation.setId(1L);
    reservation.setUser(anotherUser);
    reservation.setResource(resource);
    reservation.setStartTime(
            LocalDateTime.of(2026, 9, 1, 10, 0)
    );
    reservation.setEndTime(
            LocalDateTime.of(2026, 9, 1, 12, 0)
    );
    reservation.setPrice(new BigDecimal("500.00"));
    reservation.setStatus(ReservationStatus.PENDING);

    when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(reservation));

    when(userRepository.findByUsername("user"))
            .thenReturn(Optional.of(user));

    RuntimeException exception =
            assertThrows(
                    RuntimeException.class,
                    () -> reservationService.getReservationById(
                            1L,
                            "user",
                            false
                    )
            );

    assertEquals(
            "You are not authorized to view this reservation",
            exception.getMessage()
    );

    verify(reservationRepository, times(1))
            .findById(1L);
}
@Test
void updateReservation_shouldUpdateSuccessfully() {

    Reservation reservation = new Reservation();

    reservation.setId(1L);
    reservation.setUser(user);
    reservation.setResource(resource);
    reservation.setStartTime(
            LocalDateTime.of(2026, 9, 1, 10, 0)
    );
    reservation.setEndTime(
            LocalDateTime.of(2026, 9, 1, 12, 0)
    );
    reservation.setPrice(
            new BigDecimal("500.00")
    );
    reservation.setStatus(
            ReservationStatus.PENDING
    );

    ReservationUpdateRequest updateRequest =
            new ReservationUpdateRequest();

    updateRequest.setResourceId(1L);
    updateRequest.setStartTime(
            LocalDateTime.of(2026, 9, 1, 14, 0)
    );
    updateRequest.setEndTime(
            LocalDateTime.of(2026, 9, 1, 16, 0)
    );
    updateRequest.setPrice(
            new BigDecimal("800.00")
    );
    updateRequest.setStatus(
            ReservationStatus.CONFIRMED
    );

    when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(reservation));

    when(resourceRepository.findById(1L))
            .thenReturn(Optional.of(resource));

    when(reservationRepository.save(any(Reservation.class)))
            .thenReturn(reservation);

    ReservationResponse response =
            reservationService.updateReservation(
                    1L,
                    updateRequest
            );

    assertNotNull(response);

    assertEquals(
            new BigDecimal("800.00"),
            response.getPrice()
    );

    assertEquals(
            ReservationStatus.CONFIRMED,
            response.getStatus()
    );

    assertEquals(
            LocalDateTime.of(2026, 9, 1, 14, 0),
            response.getStartTime()
    );

    assertEquals(
            LocalDateTime.of(2026, 9, 1, 16, 0),
            response.getEndTime()
    );

    verify(reservationRepository, times(1))
            .save(any(Reservation.class));
}
@Test
void updateReservation_shouldFailWhenReservationNotFound() {

    ReservationUpdateRequest updateRequest =
            new ReservationUpdateRequest();

    updateRequest.setResourceId(1L);

    when(reservationRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(
                    RuntimeException.class,
                    () -> reservationService.updateReservation(
                            999L,
                            updateRequest
                    )
            );

    assertEquals(
            "Reservation not found",
            exception.getMessage()
    );

    verify(reservationRepository, never())
            .save(any(Reservation.class));
}
@Test
void updateReservation_shouldFailWhenEndTimeIsBeforeStartTime() {

    Reservation reservation = new Reservation();

    reservation.setId(1L);
    reservation.setUser(user);
    reservation.setResource(resource);

    ReservationUpdateRequest updateRequest =
            new ReservationUpdateRequest();

    updateRequest.setResourceId(1L);

    updateRequest.setStartTime(
            LocalDateTime.of(2026, 9, 1, 16, 0)
    );

    updateRequest.setEndTime(
            LocalDateTime.of(2026, 9, 1, 14, 0)
    );

    updateRequest.setPrice(
            new BigDecimal("800.00")
    );

    updateRequest.setStatus(
            ReservationStatus.CONFIRMED
    );

    when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(reservation));

    when(resourceRepository.findById(1L))
            .thenReturn(Optional.of(resource));

    RuntimeException exception =
            assertThrows(
                    RuntimeException.class,
                    () -> reservationService.updateReservation(
                            1L,
                            updateRequest
                    )
            );

    assertEquals(
            "End time must be after start time",
            exception.getMessage()
    );

    verify(reservationRepository, never())
            .save(any(Reservation.class));
}

@Test
void deleteResource_shouldDeleteSuccessfully() {

    when(resourceRepository.findById(1L))
            .thenReturn(Optional.of(resource));

    when(reservationRepository.existsByResourceId(1L))
            .thenReturn(false);

    resourceService.deleteResource(1L);

    verify(resourceRepository, times(1))
            .findById(1L);

    verify(reservationRepository, times(1))
            .existsByResourceId(1L);

    verify(resourceRepository, times(1))
            .delete(resource);
}

@Test
void deleteReservation_shouldFailWhenReservationNotFound() {

    when(reservationRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(
                    RuntimeException.class,
                    () -> reservationService.deleteReservation(999L)
            );

    assertEquals(
            "Reservation not found",
            exception.getMessage()
    );

    verify(reservationRepository, never())
            .delete(any(Reservation.class));
}
}

