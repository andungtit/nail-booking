package com.nailroom.booking.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailroom.booking.entity.Location;
import com.nailroom.booking.entity.Service;
import com.nailroom.booking.entity.ServiceCategory;
import com.nailroom.booking.entity.Technician;
import com.nailroom.booking.entity.TechnicianSchedule;
import com.nailroom.booking.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final boolean enabled;
    private final ObjectMapper mapper;
    private final LocationRepository locationRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final TechnicianRepository technicianRepository;
    private final TechnicianScheduleRepository scheduleRepository;

    public DataInitializer(@Value("${app.seed.enabled:true}") boolean enabled,
                           ObjectMapper mapper,
                           LocationRepository locationRepository,
                           ServiceCategoryRepository categoryRepository,
                           ServiceRepository serviceRepository,
                           TechnicianRepository technicianRepository,
                           TechnicianScheduleRepository scheduleRepository) {
        this.enabled = enabled;
        this.mapper = mapper;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
        this.technicianRepository = technicianRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!enabled || locationRepository.count() > 0) {
            return;
        }

        JsonNode root;
        try (InputStream in = new ClassPathResource("seed/nailroom-seed.json").getInputStream()) {
            root = mapper.readTree(in);
        }

        Map<Integer, Location> locs = new HashMap<>();
        for (JsonNode n : root.get("locations")) {
            Location l = new Location();
            l.setName(n.get("name").asText());
            l.setCity(n.get("city").asText());
            l.setAddress(n.get("address").asText());
            l.setPhone(n.get("phone").asText());
            l.setIsActive(true);
            locs.put(n.get("ref").asInt(), locationRepository.save(l));
        }

        Map<Integer, ServiceCategory> cats = new HashMap<>();
        for (JsonNode n : root.get("categories")) {
            ServiceCategory c = new ServiceCategory();
            c.setName(n.get("name").asText());
            c.setSortOrder(n.get("sortOrder").asInt());
            cats.put(n.get("ref").asInt(), categoryRepository.save(c));
        }

        for (JsonNode n : root.get("services")) {
            Service s = new Service();
            s.setCategory(cats.get(n.get("categoryRef").asInt()));
            s.setName(n.get("name").asText());
            s.setDurationMinutes(n.get("durationMinutes").asInt());
            s.setPrice(BigDecimal.valueOf(n.get("price").asLong()));
            String desc = n.path("description").asText("");
            s.setDescription(desc.isBlank() ? null : desc);
            s.setIsActive(true);
            serviceRepository.save(s);
        }

        JsonNode wh = root.get("workingHours");
        LocalDate today = LocalDate.now();
        int techCount = 0;
        int scheduleCount = 0;

        for (JsonNode n : root.get("technicians")) {
            int locRef = n.get("locationRef").asInt();
            Technician t = new Technician();
            t.setLocation(locs.get(locRef));
            t.setFullName(n.get("fullName").asText());
            t.setAvatarUrl(n.hasNonNull("avatarUrl") ? n.get("avatarUrl").asText() : null);
            t.setPhone(n.hasNonNull("phone") ? n.get("phone").asText() : null);
            t.setStatus("active");
            t = technicianRepository.save(t);
            techCount++;

            for (int i = 0; i < 7; i++) {
                LocalDate d = today.plusDays(i);
                JsonNode day = wh.path(String.valueOf(locRef)).path(String.valueOf(d.getDayOfWeek().getValue() % 7));
                TechnicianSchedule s = new TechnicianSchedule();
                s.setTechnician(t);
                s.setWorkDate(d);
                s.setStartTime(LocalTime.parse(day.path("start").asText("09:00:00")));
                s.setEndTime(LocalTime.parse(day.path("end").asText("19:30:00")));
                s.setIsOff(!day.path("isWorking").asBoolean(true));
                scheduleRepository.save(s);
                scheduleCount++;
            }
        }

        log.info("Seeded {} locations, {} categories, {} services, {} technicians, {} schedules",
                locs.size(), cats.size(), root.get("services").size(), techCount, scheduleCount);
    }
}
