package com.nailroom.booking.controller;

import com.nailroom.booking.dto.ListServicesRequest;
import com.nailroom.booking.dto.ServiceRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.Service;
import com.nailroom.booking.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/services", "/api/admin/services"})
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public BaseListResponse<Service> getAll(ListServicesRequest request) {
        return BaseListResponse.of(serviceService.list(request));
    }

    @GetMapping("/{id}")
    public Service getById(@PathVariable Integer id) {
        return serviceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Service create(@Valid @RequestBody ServiceRequest request) {
        return serviceService.create(request);
    }

    @PutMapping("/{id}")
    public Service update(@PathVariable Integer id, @Valid @RequestBody ServiceRequest request) {
        return serviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        serviceService.delete(id);
    }
}
