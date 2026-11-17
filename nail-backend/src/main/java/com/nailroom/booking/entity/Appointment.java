package com.nailroom.booking.entity;

import com.nailroom.booking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_apt_loc_date", columnList = "location_id, appointment_date"),
                @Index(name = "idx_apt_tech_date", columnList = "technician_id, appointment_date"),
                @Index(name = "idx_apt_booking_code", columnList = "booking_code")
        })
@Getter
@Setter
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "booking_code", length = 64)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    /** NULL nếu khách không chọn thợ */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "guest_count")
    private Integer guestCount = 1;

    @Column(columnDefinition = "TEXT")
    private String note;

    /** pending, confirmed, completed, cancelled */
    @Column(length = 50)
    private String status = "pending";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "service_names", length = 500)
    private String serviceNames;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    public Integer getLocationId() {
        return location != null ? location.getId() : null;
    }

    public String getLocationName() {
        return location != null ? location.getName() : null;
    }

    public Integer getTechnicianId() {
        return technician != null ? technician.getId() : null;
    }

    public String getTechnicianName() {
        return technician != null ? technician.getFullName() : null;
    }

    public Integer getServiceId() {
        return service != null ? service.getId() : null;
    }

    public String getServiceName() {
        return service != null ? service.getName() : (serviceNames != null ? serviceNames : null);
    }
}
