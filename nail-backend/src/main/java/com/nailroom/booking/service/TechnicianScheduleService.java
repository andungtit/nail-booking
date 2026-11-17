package com.nailroom.booking.service;

import com.nailroom.booking.dto.BulkScheduleRequest;
import com.nailroom.booking.dto.ListSchedulesRequest;
import com.nailroom.booking.dto.TechnicianScheduleRequest;
import com.nailroom.booking.entity.Technician;
import com.nailroom.booking.entity.TechnicianSchedule;
import com.nailroom.booking.repository.TechnicianRepository;
import com.nailroom.booking.repository.TechnicianScheduleRepository;
import com.nailroom.booking.service.base.BaseListService;
import com.nailroom.booking.util.FilterUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class TechnicianScheduleService extends BaseListService<TechnicianSchedule, ListSchedulesRequest> {

    private static final int MAX_BULK_DAYS = 93;

    private final TechnicianScheduleRepository scheduleRepository;
    private final TechnicianRepository technicianRepository;

    public TechnicianScheduleService(TechnicianScheduleRepository scheduleRepository,
                                   TechnicianRepository technicianRepository) {
        this.scheduleRepository = scheduleRepository;
        this.technicianRepository = technicianRepository;
    }

    @Override
    protected JpaSpecificationExecutor<TechnicianSchedule> executor() {
        return scheduleRepository;
    }

    @Override
    protected Specification<TechnicianSchedule> buildSpecification(ListSchedulesRequest r) {
        return FilterUtils.allOf(
                FilterUtils.like("technician.fullName", r.getQ()),
                FilterUtils.eq("technician.id", r.getTechnicianId()),
                FilterUtils.eq("technician.location.id", r.getLocationId()),
                FilterUtils.gte("workDate", r.getFromDate()),
                FilterUtils.lte("workDate", r.getToDate()),
                FilterUtils.eq("isOff", r.getIsOff())
        );
    }

    @Transactional(readOnly = true)
    public TechnicianSchedule getById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca trực #" + id));
    }

    public TechnicianSchedule create(TechnicianScheduleRequest request) {
        TechnicianSchedule schedule = new TechnicianSchedule();
        copyFields(schedule, request);
        return scheduleRepository.save(schedule);
    }

    public TechnicianSchedule update(Integer id, TechnicianScheduleRequest request) {
        TechnicianSchedule schedule = getById(id);
        copyFields(schedule, request);
        return scheduleRepository.save(schedule);
    }

    public void delete(Integer id) {
        TechnicianSchedule schedule = getById(id);
        scheduleRepository.delete(schedule);
    }

    public Map<String, Integer> bulkCreate(BulkScheduleRequest r) {
        if (r.getToDate().isBefore(r.getFromDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
        if (ChronoUnit.DAYS.between(r.getFromDate(), r.getToDate()) >= MAX_BULK_DAYS) {
            throw new IllegalArgumentException("Chỉ tạo tối đa " + MAX_BULK_DAYS + " ngày mỗi lần.");
        }

        boolean off = Boolean.TRUE.equals(r.getIsOff());
        validateTimes(r.getStartTime(), r.getEndTime(), off);

        Set<Integer> weekdays = (r.getWeekdays() == null || r.getWeekdays().isEmpty())
                ? Set.of(1, 2, 3, 4, 5, 6, 7) : new HashSet<>(r.getWeekdays());
        boolean overwrite = Boolean.TRUE.equals(r.getOverwrite());

        int created = 0, replaced = 0, skipped = 0;
        for (Integer techId : new HashSet<>(r.getTechnicianIds())) {
            Technician tech = technicianRepository.findById(techId)
                    .orElseThrow(() -> new IllegalArgumentException("Thợ #" + techId + " không tồn tại"));

            for (LocalDate d = r.getFromDate(); !d.isAfter(r.getToDate()); d = d.plusDays(1)) {
                if (!weekdays.contains(d.getDayOfWeek().getValue())) continue;

                List<TechnicianSchedule> existing = scheduleRepository.findByTechnicianIdAndWorkDate(techId, d);
                if (!existing.isEmpty()) {
                    if (!overwrite) {
                        skipped++;
                        continue;
                    }
                    scheduleRepository.deleteAll(existing);
                    scheduleRepository.flush();
                    replaced++;
                } else {
                    created++;
                }

                TechnicianSchedule s = new TechnicianSchedule();
                s.setTechnician(tech);
                s.setWorkDate(d);
                s.setStartTime(r.getStartTime());
                s.setEndTime(r.getEndTime());
                s.setIsOff(off);
                scheduleRepository.save(s);
            }
        }

        return Map.of("created", created, "replaced", replaced, "skipped", skipped);
    }

    private void copyFields(TechnicianSchedule s, TechnicianScheduleRequest r) {
        boolean off = Boolean.TRUE.equals(r.getIsOff());
        validateTimes(r.getStartTime(), r.getEndTime(), off);

        Technician tech = technicianRepository.findById(r.getTechnicianId())
                .orElseThrow(() -> new IllegalArgumentException("Thợ #" + r.getTechnicianId() + " không tồn tại"));

        // Kiểm tra trùng ca
        for (TechnicianSchedule other : scheduleRepository.findByTechnicianIdAndWorkDate(tech.getId(), r.getWorkDate())) {
            if (Objects.equals(other.getId(), s.getId())) continue;
            if (off || Boolean.TRUE.equals(other.getIsOff())) {
                throw new IllegalStateException("Thợ " + tech.getFullName() + " đã có ca/ngày nghỉ vào " + r.getWorkDate());
            }
            boolean overlap = r.getStartTime().isBefore(other.getEndTime()) && other.getStartTime().isBefore(r.getEndTime());
            if (overlap) {
                throw new IllegalStateException("Trùng ca " + other.getStartTime() + " - " + other.getEndTime()
                        + " của thợ " + tech.getFullName() + " ngày " + r.getWorkDate());
            }
        }

        s.setTechnician(tech);
        s.setWorkDate(r.getWorkDate());
        s.setStartTime(r.getStartTime());
        s.setEndTime(r.getEndTime());
        s.setIsOff(off);
    }

    private static void validateTimes(LocalTime start, LocalTime end, boolean off) {
        if (!off && !end.isAfter(start)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu.");
        }
    }
}
