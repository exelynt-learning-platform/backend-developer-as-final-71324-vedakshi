package com.booking.resource_booking_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponse {

    private Long id;
    private String name;
    private String type;
    private String description;
    private boolean available;
}