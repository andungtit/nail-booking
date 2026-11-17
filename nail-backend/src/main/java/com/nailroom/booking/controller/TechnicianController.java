package com.nailroom.booking.controller;

import com.nailroom.booking.dto.ListTechniciansRequest;
import com.nailroom.booking.dto.TechnicianRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.Technician;
import com.nailroom.booking.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/technicians", "/api/admin/technicians"})
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping
    public BaseListResponse<Technician> getAll(ListTechniciansRequest request) {
        return BaseListResponse.of(technicianService.list(request));
    }

    @GetMapping("/{id}")
    public Technician getById(@PathVariable Integer id) {
        return technicianService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Technician create(@Valid @RequestBody TechnicianRequest request) {
        return technicianService.create(request);
    }

    @PutMapping("/{id}")
    public Technician update(@PathVariable Integer id, @Valid @RequestBody TechnicianRequest request) {
        return technicianService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        technicianService.delete(id);
    }
}
