package com.nailroom.booking.service;

import com.nailroom.booking.dto.AppointmentResponse;
import com.nailroom.booking.dto.ListAppointmentsRequest;
import com.nailroom.booking.dto.booking.*;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.*;
import com.nailroom.booking.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final int DEFAULT_SLOT_MINUTES = 30;
    private static final int DEFAULT_LIMIT_SLOT = 6;

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final TechnicianRepository technicianRepository;
    private final TechnicianScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;

    /**
     * Lấy khung giờ còn trống (time frame / slots) cho ngày và chi nhánh đã chọn
     */
    @Transactional(readOnly = true)
    public TimeFrameResponse getTimeFrame(TimeFrameRequest req) {
        LocalDate workDate;
        try {
            workDate = (req.getDate() != null && !req.getDate().isBlank())
                    ? LocalDate.parse(req.getDate())
                    : LocalDate.now(VN_ZONE);
        } catch (Exception e) {
            workDate = LocalDate.now(VN_ZONE);
        }

        // JS day of week: Sunday = 0, Monday = 1, ..., Saturday = 6
        int dayOfWeekJs = workDate.getDayOfWeek().getValue() % 7;

        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        TimeFrameResponse.WorkingHours wh = TimeFrameResponse.WorkingHours.builder()
                .dayOfWeek(dayOfWeekJs)
                .isWorking(1)
                .startTime("08:00:00")
                .endTime("22:00:00")
                .isBreak(0)
                .build();

        Integer locationId = req.getLocationId() != null ? req.getLocationId() : 1;
        Integer operatorId = (req.getOperatorId() != null && req.getOperatorId() > 0) ? req.getOperatorId() : null;

        // Lấy danh sách lịch hẹn trong ngày tại chi nhánh (trừ đã hủy)
        List<Appointment> dayAppointments = appointmentRepository
                .findByLocationIdAndAppointmentDateAndStatusNot(locationId, workDate, "cancelled");

        // Nếu có chọn thợ cụ thể, kiểm tra ca làm việc của thợ
        List<TechnicianSchedule> techSchedules = Collections.emptyList();
        boolean techIsOff = false;
        if (operatorId != null) {
            techSchedules = scheduleRepository.findByTechnicianIdAndWorkDate(operatorId, workDate);
            if (!techSchedules.isEmpty()) {
                techIsOff = techSchedules.stream().anyMatch(TechnicianSchedule::getIsOff);
            }
        }

        List<TimeFrameResponse.TimeSlot> slots = new ArrayList<>();
        LocalTime current = openTime;

        while (!current.plusMinutes(DEFAULT_SLOT_MINUTES).isAfter(closeTime)) {
            LocalTime slotStart = current;
            LocalTime slotEnd = current.plusMinutes(DEFAULT_SLOT_MINUTES);

            // Đếm số lượng booking ở slot này
            long count = dayAppointments.stream().filter(a -> {
                if (operatorId != null) {
                    if (a.getTechnician() == null || !operatorId.equals(a.getTechnician().getId())) {
                        return false;
                    }
                }
                // Trùng khung giờ
                return a.getStartTime().equals(slotStart) ||
                        (a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart));
            }).count();

            int isSlotFull = 0;
            if (operatorId != null) {
                if (techIsOff) {
                    isSlotFull = 1;
                } else if (!techSchedules.isEmpty()) {
                    boolean insideAnyShift = techSchedules.stream().anyMatch(s ->
                            !s.getIsOff() && !slotStart.isBefore(s.getStartTime()) && !slotEnd.isAfter(s.getEndTime()));
                    if (!insideAnyShift) {
                        isSlotFull = 1;
                    }
                }
                if (count > 0) {
                    isSlotFull = 1;
                }
            } else {
                if (count >= DEFAULT_LIMIT_SLOT) {
                    isSlotFull = 1;
                }
            }

            ZonedDateTime zdt = workDate.atTime(slotStart).atZone(VN_ZONE);
            String isoTime = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            slots.add(TimeFrameResponse.TimeSlot.builder()
                    .time(isoTime)
                    .count((int) count)
                    .isSlotFull(isSlotFull)
                    .build());

            current = current.plusMinutes(DEFAULT_SLOT_MINUTES);
        }

        return TimeFrameResponse.builder()
                .status(1)
                .timeFrame(TimeFrameResponse.TimeFrameData.builder()
                        .workingHours(wh)
                        .timeFrame(slots)
                        .build())
                .build();
    }

    /**
     * Tra cứu thông tin khách hàng cũ theo số điện thoại hoặc email
     */
    @Transactional(readOnly = true)
    public CustomerLookupResponse getCustomer(CustomerLookupRequest req) {
        String mobile = req.getMobile() != null ? req.getMobile().replaceAll("[\\s\\-\\(\\)]", "") : null;
        String email = req.getEmail() != null ? req.getEmail().trim() : null;

        Optional<Customer> customerOpt = Optional.empty();

        if (mobile != null && !mobile.isBlank()) {
            customerOpt = customerRepository.findByPhone(mobile);
            if (customerOpt.isEmpty()) {
                // Thử chuyển đổi 84xxxx -> 0xxxx hoặc ngược lại
                if (mobile.startsWith("+84")) {
                    customerOpt = customerRepository.findByPhone("0" + mobile.substring(3));
                } else if (mobile.startsWith("84")) {
                    customerOpt = customerRepository.findByPhone("0" + mobile.substring(2));
                } else if (mobile.startsWith("0")) {
                    customerOpt = customerRepository.findByPhone("84" + mobile.substring(1));
                }
            }
        }

        if (customerOpt.isEmpty() && email != null && !email.isBlank()) {
            customerOpt = customerRepository.findByEmail(email);
        }

        if (customerOpt.isEmpty()) {
            return CustomerLookupResponse.builder()
                    .status(1)
                    .data(CustomerLookupResponse.CustomerData.builder().build())
                    .build();
        }

        Customer c = customerOpt.get();
        return CustomerLookupResponse.builder()
                .status(1)
                .data(CustomerLookupResponse.CustomerData.builder()
                        .id(c.getId())
                        .fullName(c.getFullName())
                        .gender(c.getGender() != null ? c.getGender() : "F")
                        .email(c.getEmail())
                        .mobile(c.getPhone())
                        .phoneCountryCode("84")
                        .build())
                .build();
    }

    /**
     * Đặt lịch hẹn mới từ form booking
     */
    @Transactional
    public BookingCreateResponse createBooking(BookingCreateRequest req) {
        if (req.getLocationId() == null) {
            return BookingCreateResponse.builder()
                    .status(0)
                    .message("Chi nhánh không được để trống")
                    .build();
        }
        if ((req.getMobile() == null || req.getMobile().isBlank()) && (req.getEmail() == null || req.getEmail().isBlank())) {
            return BookingCreateResponse.builder()
                    .status(0)
                    .message("Vui lòng cung cấp số điện thoại hoặc email")
                    .build();
        }
        if (req.getBookedAt() == null || req.getBookedAt().isBlank()) {
            return BookingCreateResponse.builder()
                    .status(0)
                    .message("Thời gian đặt lịch không hợp lệ")
                    .build();
        }

        String phone = req.getMobile() != null ? req.getMobile().replaceAll("[\\s\\-\\(\\)]", "") : "";
        String email = req.getEmail() != null ? req.getEmail().trim() : null;
        String customerName = req.getCustomerName() != null && !req.getCustomerName().isBlank()
                ? req.getCustomerName().trim()
                : "Khách đặt hẹn";

        // Tìm hoặc tạo mới Customer
        Customer customer = null;
        if (!phone.isBlank()) {
            customer = customerRepository.findByPhone(phone).orElse(null);
        }
        if (customer == null && email != null && !email.isBlank()) {
            customer = customerRepository.findByEmail(email).orElse(null);
        }
        if (customer == null) {
            customer = new Customer();
            customer.setPhone(phone.isBlank() ? "EML_" + System.currentTimeMillis() : phone);
            customer.setFullName(customerName);
            customer.setEmail(email);
            customer.setGender(req.getGender() != null ? req.getGender() : "F");
            customer = customerRepository.save(customer);
        } else {
            // Cập nhật thông tin nếu có mới
            if (req.getCustomerName() != null && !req.getCustomerName().isBlank()) {
                customer.setFullName(customerName);
            }
            if (email != null && !email.isBlank() && (customer.getEmail() == null || customer.getEmail().isBlank())) {
                customer.setEmail(email);
            }
            if (req.getGender() != null && !req.getGender().isBlank()) {
                customer.setGender(req.getGender());
            }
            customerRepository.save(customer);
        }

        // Parse booked_at
        LocalDate appointmentDate;
        LocalTime startTime;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(req.getBookedAt());
            appointmentDate = odt.atZoneSameInstant(VN_ZONE).toLocalDate();
            startTime = odt.atZoneSameInstant(VN_ZONE).toLocalTime();
        } catch (Exception e) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(req.getBookedAt());
                appointmentDate = ldt.toLocalDate();
                startTime = ldt.toLocalTime();
            } catch (Exception ex) {
                appointmentDate = LocalDate.now(VN_ZONE);
                startTime = LocalTime.of(9, 0);
            }
        }

        int duration = (req.getEstimatedDuration() != null && req.getEstimatedDuration() > 0)
                ? req.getEstimatedDuration()
                : 60;
        LocalTime endTime = startTime.plusMinutes(duration);

        // Chi nhánh
        Location location = locationRepository.findById(req.getLocationId()).orElse(null);

        // Thợ (kỹ thuật viên)
        Technician technician = null;
        if (req.getOperatorId() != null && !req.getOperatorId().isEmpty()) {
            Integer techId = req.getOperatorId().get(0);
            if (techId != null && techId > 0) {
                technician = technicianRepository.findById(techId).orElse(null);
            }
        }

        // Dịch vụ
        com.nailroom.booking.entity.Service mainService = null;
        List<String> serviceNameList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (req.getProductId() != null && !req.getProductId().isEmpty()) {
            List<com.nailroom.booking.entity.Service> services = serviceRepository.findAllById(req.getProductId());
            for (com.nailroom.booking.entity.Service s : services) {
                serviceNameList.add(s.getName());
                if (s.getPrice() != null) {
                    totalPrice = totalPrice.add(s.getPrice());
                }
            }
            if (!services.isEmpty()) {
                mainService = services.get(0);
            }
        }

        String serviceNamesStr = String.join(", ", serviceNameList);

        // Mã booking
        String bookingCode = req.getBookingCode();
        if (bookingCode == null || bookingCode.isBlank()) {
            bookingCode = "NR" + (System.currentTimeMillis() % 10000000);
        }

        Appointment apt = new Appointment();
        apt.setBookingCode(bookingCode);
        apt.setLocation(location);
        apt.setCustomer(customer);
        apt.setCustomerName(customerName);
        apt.setCustomerPhone(phone);
        apt.setCustomerEmail(email);
        apt.setTechnician(technician);
        apt.setService(mainService);
        apt.setServiceNames(serviceNamesStr);
        apt.setPrice(totalPrice);
        apt.setAppointmentDate(appointmentDate);
        apt.setStartTime(startTime);
        apt.setEndTime(endTime);
        apt.setGuestCount(req.getNumberOfGuests() != null ? req.getNumberOfGuests() : 1);
        apt.setNote(req.getNote());
        apt.setStatus("pending");

        Appointment saved = appointmentRepository.save(apt);

        log.info("Booking created: code={}, id={}, customer={}, date={}, time={}",
                bookingCode, saved.getId(), customerName, appointmentDate, startTime);

        return BookingCreateResponse.builder()
                .status(1)
                .data(BookingCreateResponse.BookingData.builder()
                        .bookingCode(bookingCode)
                        .appointmentId(saved.getId())
                        .build())
                .message("Đặt lịch thành công")
                .build();
    }

    /**
     * Dữ liệu khởi tạo (Bootstrap / Merchant Data) cho Web Booking client
     */
    @Transactional(readOnly = true)
    public MerchantDataResponse getMerchantData() {
        MerchantDataResponse res = new MerchantDataResponse();

        // Locations
        List<Map<String, Object>> locations = locationRepository.findAll().stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("name", l.getName());
            m.put("phone", l.getPhone());
            m.put("address", l.getAddress());
            m.put("fullAddress", l.getAddress() + ", " + l.getCity());
            m.put("city_name", l.getCity());
            m.put("city_code", 1);
            m.put("district_code", 1);
            m.put("district_name", "");
            m.put("country_code", "VN");
            m.put("lat", 21.0285);
            m.put("long", 105.8542);
            m.put("sort_order", l.getId());
            return m;
        }).collect(Collectors.toList());
        res.setLocation(locations);

        // Product Groups (Categories)
        List<Map<String, Object>> groups = categoryRepository.findAll().stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("is_active", 1);
            m.put("show_type", 1);
            return m;
        }).collect(Collectors.toList());
        res.setProductGroups(groups);

        // Products (Services)
        List<Map<String, Object>> products = serviceRepository.findAll().stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("sku", "SV_" + s.getId());
            m.put("type", "service");
            m.put("group_id", s.getCategory() != null ? s.getCategory().getId() : 1);
            m.put("group_name", s.getCategory() != null ? s.getCategory().getName() : "");
            m.put("default_price", s.getPrice() != null ? s.getPrice().intValue() : 0);
            m.put("service_duration", s.getDurationMinutes() != null ? s.getDurationMinutes() : 60);
            m.put("description", s.getDescription() != null ? s.getDescription() : "");
            m.put("edit_price_in_order", 0);
            m.put("location_ids", locations.stream().map(l -> l.get("id")).collect(Collectors.toList()));
            return m;
        }).collect(Collectors.toList());
        res.setProduct(products);

        // Operators (Technicians)
        List<Map<String, Object>> operators = technicianRepository.findAll().stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getFullName());
            m.put("title", "Kỹ thuật viên");
            m.put("location_id", t.getLocation() != null ? t.getLocation().getId() : 1);
            m.put("avatar_url", t.getAvatarUrl());
            m.put("phone", t.getPhone());
            return m;
        }).collect(Collectors.toList());
        res.setOperator(operators);

        // Settings
        Map<String, Object> setting = new HashMap<>();
        setting.put("id", 1);
        setting.put("merchant_id", 1);
        setting.put("booking_before_min", 30);
        setting.put("language", "vi");
        setting.put("web_color", "#e11d48");
        setting.put("show_section_of_day", 0);
        setting.put("default_booking_duration", 60);
        setting.put("lock_web_booking", 0);
        setting.put("showOperatorInfo", 1);
        setting.put("showProductInfo", 1);
        setting.put("showProductPrice", 1);
        setting.put("limitSlotBooking", DEFAULT_LIMIT_SLOT);
        setting.put("limitBookingDate", 30);
        setting.put("customerGender", "all");
        setting.put("slotMinutes", DEFAULT_SLOT_MINUTES);
        res.setSetting(setting);

        // Working hours mẫu cho 7 ngày trong tuần cho mỗi chi nhánh
        Map<String, Map<String, Object>> whMap = new HashMap<>();
        for (Map<String, Object> loc : locations) {
            String locIdStr = String.valueOf(loc.get("id"));
            Map<String, Object> days = new HashMap<>();
            for (int d = 0; d < 7; d++) {
                Map<String, Object> dayWh = new HashMap<>();
                dayWh.put("day_of_week", d);
                dayWh.put("is_working", 1);
                dayWh.put("start_time", "08:00:00");
                dayWh.put("end_time", "22:00:00");
                dayWh.put("is_break", 0);
                days.put(String.valueOf(d), dayWh);
            }
            whMap.put(locIdStr, days);
        }
        res.setWorkingHours(whMap);

        return res;
    }

    /**
     * Danh sách lịch hẹn cho Admin
     */
    @Transactional(readOnly = true)
    public BaseListResponse<AppointmentResponse> listAppointments(ListAppointmentsRequest req) {
        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.getLocationId() != null) {
                predicates.add(cb.equal(root.get("location").get("id"), req.getLocationId()));
            }
            if (req.getTechnicianId() != null) {
                predicates.add(cb.equal(root.get("technician").get("id"), req.getTechnicianId()));
            }
            if (req.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), req.getCustomerId()));
            }
            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), req.getStatus().trim()));
            }
            if (req.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("appointmentDate"), req.getFromDate()));
            }
            if (req.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("appointmentDate"), req.getToDate()));
            }
            if (req.getQ() != null && !req.getQ().isBlank()) {
                String pattern = "%" + req.getQ().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("bookingCode")), pattern),
                        cb.like(cb.lower(root.get("customerName")), pattern),
                        cb.like(cb.lower(root.get("customerPhone")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = req.toPageable();
        Page<Appointment> page = appointmentRepository.findAll(spec, pageable);
        Page<AppointmentResponse> dtoPage = page.map(AppointmentResponse::fromEntity);

        return BaseListResponse.of(dtoPage);
    }

    /**
     * Cập nhật trạng thái lịch hẹn
     */
    @Transactional
    public AppointmentResponse updateStatus(Integer id, String status) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn ID " + id));
        apt.setStatus(status);
        return AppointmentResponse.fromEntity(appointmentRepository.save(apt));
    }
}
