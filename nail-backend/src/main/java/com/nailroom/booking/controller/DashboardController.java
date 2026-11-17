package com.nailroom.booking.controller;

import com.nailroom.booking.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({"/api/dashboard", "/api/admin/dashboard"})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public Map<String, Object> getDashboard() {
        return dashboardService.getCounts();
    }

    @GetMapping("/counts")
    public Map<String, Object> getCounts() {
        return dashboardService.getCounts();
    }
}
