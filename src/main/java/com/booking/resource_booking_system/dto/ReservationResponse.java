package com.booking.resource_booking_system.dto;

import com.booking.resource_booking_system.entity.ReservationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;

    private Long userId;
    private String username;

    private Long resourceId;
    private String resourceName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal price;

    private ReservationStatus status;
}