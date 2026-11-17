package com.nailroom.booking.service;

import com.nailroom.booking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final LocationRepository locationRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final TechnicianRepository technicianRepository;
    private final TechnicianScheduleRepository scheduleRepository;

    public DashboardService(LocationRepository locationRepository,
                            ServiceCategoryRepository categoryRepository,
                            ServiceRepository serviceRepository,
                            TechnicianRepository technicianRepository,
                            TechnicianScheduleRepository scheduleRepository) {
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
        this.technicianRepository = technicianRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public Map<String, Object> getCounts() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("locations", locationRepository.count());
        map.put("serviceCategories", categoryRepository.count());
        map.put("services", serviceRepository.count());
        map.put("technicians", technicianRepository.count());
        map.put("activeTechnicians", technicianRepository.countByStatus("active"));
        map.put("schedulesToday", scheduleRepository.countByWorkDate(LocalDate.now()));
        return map;
    }
}
