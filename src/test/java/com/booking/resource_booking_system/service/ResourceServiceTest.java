package com.booking.resource_booking_system.service;

import com.booking.resource_booking_system.dto.ResourceRequest;
import com.booking.resource_booking_system.dto.ResourceResponse;
import com.booking.resource_booking_system.entity.Resource;
import com.booking.resource_booking_system.repository.ResourceRepository;
import com.booking.resource_booking_system.repository.ReservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

   @Mock
private ResourceRepository resourceRepository;

@Mock
private ReservationRepository reservationRepository;

@InjectMocks
private ResourceService resourceService;

    private Resource resource;
    private ResourceRequest request;

    @BeforeEach
    void setUp() {

        resource = new Resource();

        resource.setId(1L);
        resource.setName("Meeting Room");
        resource.setType("Room");
        resource.setDescription("Meeting room for meetings");
        resource.setAvailable(true);

        request = new ResourceRequest();

        request.setName("Meeting Room");
        request.setType("Room");
        request.setDescription("Meeting room for meetings");
        request.setAvailable(true);
    }



    @Test
    void createResource_shouldCreateSuccessfully() {

        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(resource);

        ResourceResponse response =
                resourceService.createResource(request);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Meeting Room",
                response.getName()
        );

        assertEquals(
                "Room",
                response.getType()
        );

        assertEquals(
                "Meeting room for meetings",
                response.getDescription()
        );

        assertTrue(
                response.isAvailable()
        );

        verify(resourceRepository, times(1))
                .save(any(Resource.class));
    }


  

    @Test
    void getAllResources_shouldReturnAllResources() {

        Resource resource2 = new Resource();

        resource2.setId(2L);
        resource2.setName("Conference Room");
        resource2.setType("Room");
        resource2.setDescription("Conference room");
        resource2.setAvailable(true);

        when(resourceRepository.findAll())
                .thenReturn(List.of(resource, resource2));

        List<ResourceResponse> responses =
                resourceService.getAllResources();

        assertNotNull(responses);

        assertEquals(
                2,
                responses.size()
        );

        assertEquals(
                "Meeting Room",
                responses.get(0).getName()
        );

        assertEquals(
                "Conference Room",
                responses.get(1).getName()
        );

        verify(resourceRepository, times(1))
                .findAll();
    }


    

    @Test
    void getResourceById_shouldReturnResource() {

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        ResourceResponse response =
                resourceService.getResourceById(1L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Meeting Room",
                response.getName()
        );

        assertEquals(
                "Room",
                response.getType()
        );

        verify(resourceRepository, times(1))
                .findById(1L);
    }


  

    @Test
    void getResourceById_shouldFailWhenResourceNotFound() {

        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> resourceService.getResourceById(999L)
                );

        assertEquals(
                "Resource not found with id: 999",
                exception.getMessage()
        );

        verify(resourceRepository, times(1))
                .findById(999L);
    }



    @Test
    void updateResource_shouldUpdateSuccessfully() {

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(resource);

        request.setName("Updated Meeting Room");
        request.setType("Conference Room");
        request.setDescription("Updated description");
        request.setAvailable(false);

        ResourceResponse response =
                resourceService.updateResource(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                "Updated Meeting Room",
                response.getName()
        );

        assertEquals(
                "Conference Room",
                response.getType()
        );

        assertEquals(
                "Updated description",
                response.getDescription()
        );

        assertFalse(
                response.isAvailable()
        );

        verify(resourceRepository, times(1))
                .findById(1L);

        verify(resourceRepository, times(1))
                .save(any(Resource.class));
    }




    @Test
    void updateResource_shouldFailWhenResourceNotFound() {

        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> resourceService.updateResource(
                                999L,
                                request
                        )
                );

        assertEquals(
                "Resource not found with id: 999",
                exception.getMessage()
        );

        verify(resourceRepository, never())
                .save(any(Resource.class));
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
    void deleteResource_shouldFailWhenResourceNotFound() {

        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> resourceService.deleteResource(999L)
                );

        assertEquals(
                "Resource not found with id: 999",
                exception.getMessage()
        );

        verify(resourceRepository, never())
                .delete(any(Resource.class));
    }
}

