package com.booking.resource_booking_system.controller;

import com.booking.resource_booking_system.dto.ResourceRequest;
import com.booking.resource_booking_system.dto.ResourceResponse;
import com.booking.resource_booking_system.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
@RequestMapping("/resources")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

  
    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse response =
                resourceService.createResource(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {

        List<ResourceResponse> resources =
                resourceService.getAllResources();

        return ResponseEntity.ok(resources);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable Long id) {

        ResourceResponse response =
                resourceService.getResourceById(id);

        return ResponseEntity.ok(response);
    }

  
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse response =
                resourceService.updateResource(id, request);

        return ResponseEntity.ok(response);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);

        return ResponseEntity.noContent().build();
    }
}