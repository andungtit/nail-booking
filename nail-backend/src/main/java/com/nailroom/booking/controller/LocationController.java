package com.nailroom.booking.controller;

import com.nailroom.booking.dto.ListLocationsRequest;
import com.nailroom.booking.dto.LocationRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.Location;
import com.nailroom.booking.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/locations", "/api/admin/locations"})
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public BaseListResponse<Location> getAll(ListLocationsRequest request) {
        return BaseListResponse.of(locationService.list(request));
    }

    @GetMapping("/{id}")
    public Location getById(@PathVariable Integer id) {
        return locationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Location create(@Valid @RequestBody LocationRequest request) {
        return locationService.create(request);
    }

    @PutMapping("/{id}")
    public Location update(@PathVariable Integer id, @Valid @RequestBody LocationRequest request) {
        return locationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        locationService.delete(id);
    }
}
