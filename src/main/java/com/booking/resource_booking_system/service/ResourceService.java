package com.booking.resource_booking_system.service;

import com.booking.resource_booking_system.dto.ResourceRequest;
import com.booking.resource_booking_system.dto.ResourceResponse;
import com.booking.resource_booking_system.entity.Resource;
import com.booking.resource_booking_system.repository.ReservationRepository;
import com.booking.resource_booking_system.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    

    public ResourceService(ResourceRepository resourceRepository,  
                        ReservationRepository reservationRepository) {
        this.resourceRepository = resourceRepository;
         this.reservationRepository = reservationRepository;
    }

   
    public ResourceResponse createResource(ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.isAvailable());

        Resource savedResource = resourceRepository.save(resource);

        return convertToResponse(savedResource);
    }

    // GET ALL
    public List<ResourceResponse> getAllResources() {

        return resourceRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET BY ID
    public ResourceResponse getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Resource not found with id: " + id));

        return convertToResponse(resource);
    }

    // UPDATE
    public ResourceResponse updateResource(Long id, ResourceRequest request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Resource not found with id: " + id));

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.isAvailable());

        Resource updatedResource = resourceRepository.save(resource);

        return convertToResponse(updatedResource);
    }

  ;

    public void deleteResource(Long id) {

    Resource resource = resourceRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Resource not found with id: " + id));

    if (reservationRepository.existsByResourceId(id)) {
        throw new IllegalStateException(
        "Cannot delete resource because it has existing reservations"
);
    }

    resourceRepository.delete(resource);
}

    
    private ResourceResponse convertToResponse(Resource resource) {

        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .description(resource.getDescription())
                .available(resource.isAvailable())
                .build();
    }
}