package com.nailroom.booking.controller;

import com.nailroom.booking.repository.LocationRepository;
import com.nailroom.booking.repository.ServiceCategoryRepository;
import com.nailroom.booking.repository.TechnicianRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/options", "/api/admin/options"})
public class MetaController {

    private final LocationRepository locationRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final TechnicianRepository technicianRepository;

    public MetaController(LocationRepository locationRepository,
                          ServiceCategoryRepository categoryRepository,
                          TechnicianRepository technicianRepository) {
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.technicianRepository = technicianRepository;
    }

    public record OptionItem(Integer id, String name, String extra, Integer parentId) {}

    @GetMapping
    public Map<String, List<OptionItem>> getOptions() {
        Map<String, List<OptionItem>> map = new LinkedHashMap<>();

        map.put("locations", locationRepository.findAll(Sort.by("name")).stream()
                .map(l -> new OptionItem(l.getId(), l.getName(), l.getCity(), null))
                .toList());

        map.put("categories", categoryRepository.findAll(Sort.by("sortOrder", "id")).stream()
                .map(c -> new OptionItem(c.getId(), c.getName(), null, null))
                .toList());

        map.put("technicians", technicianRepository.findAll((r, q, cb) -> null, Sort.by("fullName")).stream()
                .map(t -> new OptionItem(t.getId(), t.getFullName(), t.getStatus(),
                        t.getLocation() != null ? t.getLocation().getId() : null))
                .toList());

        return map;
    }
}
