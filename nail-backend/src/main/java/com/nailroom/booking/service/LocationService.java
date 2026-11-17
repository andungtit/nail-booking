package com.nailroom.booking.service;

import com.nailroom.booking.dto.ListLocationsRequest;
import com.nailroom.booking.dto.LocationRequest;
import com.nailroom.booking.entity.Location;
import com.nailroom.booking.repository.AppointmentRepository;
import com.nailroom.booking.repository.LocationRepository;
import com.nailroom.booking.repository.TechnicianRepository;
import com.nailroom.booking.service.base.BaseListService;
import com.nailroom.booking.util.FilterUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LocationService extends BaseListService<Location, ListLocationsRequest> {

    private final LocationRepository locationRepository;
    private final TechnicianRepository technicianRepository;
    private final AppointmentRepository appointmentRepository;

    public LocationService(LocationRepository locationRepository,
                           TechnicianRepository technicianRepository,
                           AppointmentRepository appointmentRepository) {
        this.locationRepository = locationRepository;
        this.technicianRepository = technicianRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Location> executor() {
        return locationRepository;
    }

    @Override
    protected Specification<Location> buildSpecification(ListLocationsRequest r) {
        return FilterUtils.allOf(
                FilterUtils.anyLike(r.getQ(), "name", "address", "phone"),
                FilterUtils.eq("city", r.getCity()),
                FilterUtils.eq("isActive", r.getIsActive())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Location> list(ListLocationsRequest request) {
        Page<Location> page = super.list(request);
        Map<Integer, Long> counts = new HashMap<>();
        for (Object[] row : technicianRepository.countGroupByLocation()) {
            counts.put((Integer) row[0], (Long) row[1]);
        }
        page.getContent().forEach(l -> l.setTechnicianCount(counts.getOrDefault(l.getId(), 0L)));
        return page;
    }

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        List<Location> list = locationRepository.findAll();
        Map<Integer, Long> counts = new HashMap<>();
        for (Object[] row : technicianRepository.countGroupByLocation()) {
            counts.put((Integer) row[0], (Long) row[1]);
        }
        list.forEach(l -> l.setTechnicianCount(counts.getOrDefault(l.getId(), 0L)));
        return list;
    }

    @Transactional(readOnly = true)
    public Location getById(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh #" + id));
        location.setTechnicianCount(technicianRepository.countByLocationId(id));
        return location;
    }

    public Location create(LocationRequest request) {
        Location location = new Location();
        copyFields(location, request);
        return locationRepository.save(location);
    }

    public Location update(Integer id, LocationRequest request) {
        Location location = getById(id);
        copyFields(location, request);
        return locationRepository.save(location);
    }

    public void delete(Integer id) {
        Location location = getById(id);
        long techs = technicianRepository.countByLocationId(id);
        if (techs > 0) {
            throw new IllegalStateException("Chi nhánh đang có " + techs + " thợ, hãy chuyển/xóa thợ trước.");
        }
        long appts = appointmentRepository.countByLocationId(id);
        if (appts > 0) {
            throw new IllegalStateException("Chi nhánh đã có " + appts + " lịch hẹn, chỉ có thể ngừng hoạt động.");
        }
        locationRepository.delete(location);
    }

    private void copyFields(Location l, LocationRequest r) {
        l.setName(r.getName().trim());
        l.setCity(r.getCity().trim());
        l.setAddress(r.getAddress().trim());
        l.setPhone(r.getPhone().trim());
        l.setIsActive(r.getIsActive() == null || r.getIsActive());
    }
}
