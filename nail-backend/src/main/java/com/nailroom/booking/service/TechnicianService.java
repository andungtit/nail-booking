package com.nailroom.booking.service;

import com.nailroom.booking.dto.ListTechniciansRequest;
import com.nailroom.booking.dto.TechnicianRequest;
import com.nailroom.booking.entity.Location;
import com.nailroom.booking.entity.Technician;
import com.nailroom.booking.repository.AppointmentRepository;
import com.nailroom.booking.repository.LocationRepository;
import com.nailroom.booking.repository.TechnicianRepository;
import com.nailroom.booking.repository.TechnicianScheduleRepository;
import com.nailroom.booking.service.base.BaseListService;
import com.nailroom.booking.util.FilterUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TechnicianService extends BaseListService<Technician, ListTechniciansRequest> {

    private final TechnicianRepository technicianRepository;
    private final LocationRepository locationRepository;
    private final TechnicianScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public TechnicianService(TechnicianRepository technicianRepository,
                             LocationRepository locationRepository,
                             TechnicianScheduleRepository scheduleRepository,
                             AppointmentRepository appointmentRepository) {
        this.technicianRepository = technicianRepository;
        this.locationRepository = locationRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Technician> executor() {
        return technicianRepository;
    }

    @Override
    protected Specification<Technician> buildSpecification(ListTechniciansRequest r) {
        return FilterUtils.allOf(
                FilterUtils.anyLike(r.getQ(), "fullName", "phone"),
                FilterUtils.eq("location.id", r.getLocationId()),
                FilterUtils.eq("status", r.getStatus())
        );
    }

    @Transactional(readOnly = true)
    public List<Technician> getAll(Integer locationId) {
        Specification<Technician> spec = FilterUtils.eq("location.id", locationId);
        return technicianRepository.findAll(spec, Sort.by("fullName"));
    }

    @Transactional(readOnly = true)
    public Technician getById(Integer id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ #" + id));
    }

    public Technician create(TechnicianRequest request) {
        Technician technician = new Technician();
        copyFields(technician, request);
        return technicianRepository.save(technician);
    }

    public Technician update(Integer id, TechnicianRequest request) {
        Technician technician = getById(id);
        copyFields(technician, request);
        return technicianRepository.save(technician);
    }

    public void delete(Integer id) {
        Technician technician = getById(id);
        long n = appointmentRepository.countByTechnicianId(id);
        if (n > 0) {
            throw new IllegalStateException("Thợ đã có " + n + " lịch hẹn, hãy đổi trạng thái sang 'off'.");
        }
        scheduleRepository.deleteByTechnicianId(id);
        technicianRepository.delete(technician);
    }

    private void copyFields(Technician t, TechnicianRequest r) {
        Location location = locationRepository.findById(r.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Chi nhánh #" + r.getLocationId() + " không tồn tại"));
        t.setLocation(location);
        t.setFullName(r.getFullName().trim());
        t.setAvatarUrl(blankToNull(r.getAvatarUrl()));
        t.setPhone(blankToNull(r.getPhone()));
        t.setStatus(r.getStatus() == null || r.getStatus().isBlank() ? "active" : r.getStatus());
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
