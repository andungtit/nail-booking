package com.nailroom.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nailroom.booking.entity.Appointment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentResponse {

    private Integer id;
    private String bookingCode;
    private Integer locationId;
    private String locationName;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Integer technicianId;
    private String technicianName;
    private Integer serviceId;
    private String serviceName;
    private String serviceNames;
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Integer guestCount;
    private String note;
    private String status;
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static AppointmentResponse fromEntity(Appointment a) {
        AppointmentResponse res = new AppointmentResponse();
        res.setId(a.getId());
        res.setBookingCode(a.getBookingCode());
        res.setLocationId(a.getLocationId());
        res.setLocationName(a.getLocationName());
        res.setCustomerId(a.getCustomer() != null ? a.getCustomer().getId() : null);
        res.setCustomerName(a.getCustomerName() != null ? a.getCustomerName() : (a.getCustomer() != null ? a.getCustomer().getFullName() : null));
        res.setCustomerPhone(a.getCustomerPhone() != null ? a.getCustomerPhone() : (a.getCustomer() != null ? a.getCustomer().getPhone() : null));
        res.setCustomerEmail(a.getCustomerEmail() != null ? a.getCustomerEmail() : (a.getCustomer() != null ? a.getCustomer().getEmail() : null));
        res.setTechnicianId(a.getTechnicianId());
        res.setTechnicianName(a.getTechnicianName());
        res.setServiceId(a.getServiceId());
        res.setServiceName(a.getServiceName());
        res.setServiceNames(a.getServiceNames());
        res.setAppointmentDate(a.getAppointmentDate());
        res.setStartTime(a.getStartTime());
        res.setEndTime(a.getEndTime());
        res.setGuestCount(a.getGuestCount());
        res.setNote(a.getNote());
        res.setStatus(a.getStatus());
        res.setPrice(a.getPrice());
        res.setCreatedAt(a.getCreatedAt());
        return res;
    }
}
