package com.nailroom.booking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "technician_schedules",
        indexes = @Index(name = "idx_schedule_tech_date", columnList = "technician_id, work_date"))
@Getter
@Setter
public class TechnicianSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Column(name = "is_off")
    private Boolean isOff = false;

    public Integer getTechnicianId() {
        return technician != null ? technician.getId() : null;
    }

    public String getTechnicianName() {
        return technician != null ? technician.getFullName() : null;
    }

    public Integer getLocationId() {
        return (technician != null && technician.getLocation() != null)
                ? technician.getLocation().getId() : null;
    }

    public String getLocationName() {
        return (technician != null && technician.getLocation() != null)
                ? technician.getLocation().getName() : null;
    }
}
