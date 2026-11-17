package com.nailroom.booking.controller;

import com.nailroom.booking.dto.BulkScheduleRequest;
import com.nailroom.booking.dto.ListSchedulesRequest;
import com.nailroom.booking.dto.TechnicianScheduleRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.TechnicianSchedule;
import com.nailroom.booking.service.TechnicianScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/technician-schedules", "/api/admin/technician-schedules"})
public class TechnicianScheduleController {

    private final TechnicianScheduleService scheduleService;

    public TechnicianScheduleController(TechnicianScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public BaseListResponse<TechnicianSchedule> getAll(ListSchedulesRequest request) {
        return BaseListResponse.of(scheduleService.list(request));
    }

    @GetMapping("/{id}")
    public TechnicianSchedule getById(@PathVariable Integer id) {
        return scheduleService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TechnicianSchedule create(@Valid @RequestBody TechnicianScheduleRequest request) {
        return scheduleService.create(request);
    }

    @PostMapping("/bulk")
    public Map<String, Integer> bulkCreate(@Valid @RequestBody BulkScheduleRequest request) {
        return scheduleService.bulkCreate(request);
    }

    @PutMapping("/{id}")
    public TechnicianSchedule update(@PathVariable Integer id, @Valid @RequestBody TechnicianScheduleRequest request) {
        return scheduleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        scheduleService.delete(id);
    }
}
