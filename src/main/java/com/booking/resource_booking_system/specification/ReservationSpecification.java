package com.booking.resource_booking_system.specification;

import com.booking.resource_booking_system.entity.Reservation;
import com.booking.resource_booking_system.entity.ReservationStatus;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ReservationSpecification {

    public static Specification<Reservation> hasStatus(
            ReservationStatus status) {

        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("status"),
                                status
                        );
    }

    public static Specification<Reservation> priceGreaterThanOrEqualTo(
            BigDecimal minPrice) {

        return (root, query, criteriaBuilder) ->
                minPrice == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        );
    }

    public static Specification<Reservation> priceLessThanOrEqualTo(
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) ->
                maxPrice == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        );
    }

    public static Specification<Reservation> belongsToUser(
            Long userId) {

        return (root, query, criteriaBuilder) ->
                userId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("user").get("id"),
                                userId
                        );
    }
}

