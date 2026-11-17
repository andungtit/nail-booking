package com.nailroom.booking.controller;

import com.nailroom.booking.dto.AppointmentResponse;
import com.nailroom.booking.dto.ListAppointmentsRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/appointments", "/api/admin/appointments"})
@RequiredArgsConstructor
public class AppointmentController {

    private final BookingService bookingService;

    @GetMapping
    public BaseListResponse<AppointmentResponse> listAppointments(ListAppointmentsRequest request) {
        return bookingService.listAppointments(request);
    }

    @PutMapping("/{id}/status")
    public AppointmentResponse updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "confirmed");
        return bookingService.updateStatus(id, status);
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse patchStatus(@PathVariable Integer id, @RequestParam("status") String status) {
        return bookingService.updateStatus(id, status);
    }
}
