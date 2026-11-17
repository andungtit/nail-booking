package com.nailroom.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailroom.booking.dto.booking.*;
import com.nailroom.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    // -------------------------------------------------------------
    // 1. GET TIME FRAME (Lấy khung giờ còn trống)
    // -------------------------------------------------------------
    @PostMapping(value = {"/api/get-time-frame", "/get-time-frame"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TimeFrameResponse getTimeFrameJson(@RequestBody TimeFrameRequest request) {
        return bookingService.getTimeFrame(request);
    }

    @PostMapping(value = {"/api/get-time-frame", "/get-time-frame"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TimeFrameResponse getTimeFrameForm(HttpServletRequest request) {
        TimeFrameRequest req = new TimeFrameRequest();
        String locId = request.getParameter("location_id");
        if (locId == null) locId = request.getParameter("locationId");
        if (locId != null && !locId.isBlank()) req.setLocationId(Integer.valueOf(locId.trim()));

        String merId = request.getParameter("merchant_id");
        if (merId == null) merId = request.getParameter("merchantId");
        if (merId != null && !merId.isBlank()) req.setMerchantId(Integer.valueOf(merId.trim()));

        req.setDate(request.getParameter("date"));

        String opId = request.getParameter("operator_id");
        if (opId == null) opId = request.getParameter("operatorId");
        if (opId != null && !opId.isBlank()) req.setOperatorId(Integer.valueOf(opId.trim()));

        return bookingService.getTimeFrame(req);
    }

    @GetMapping(value = {"/api/get-time-frame", "/get-time-frame"})
    public TimeFrameResponse getTimeFrameGet(
            @RequestParam(value = "location_id", required = false) Integer locationId,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "operator_id", required = false) Integer operatorId,
            @RequestParam(value = "merchant_id", required = false) Integer merchantId) {
        TimeFrameRequest req = new TimeFrameRequest();
        req.setLocationId(locationId);
        req.setDate(date);
        req.setOperatorId(operatorId);
        req.setMerchantId(merchantId);
        return bookingService.getTimeFrame(req);
    }

    // -------------------------------------------------------------
    // 2. GET CUSTOMER BY MOBILE (Tra cứu khách hàng theo SĐT/Email)
    // -------------------------------------------------------------
    @PostMapping(value = {"/api/dat-lich/getCustomerByMobile", "/dat-lich/getCustomerByMobile"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomerLookupResponse getCustomerJson(@RequestBody CustomerLookupRequest request) {
        return bookingService.getCustomer(request);
    }

    @PostMapping(value = {"/api/dat-lich/getCustomerByMobile", "/dat-lich/getCustomerByMobile"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public CustomerLookupResponse getCustomerForm(HttpServletRequest request) {
        CustomerLookupRequest req = new CustomerLookupRequest();
        String merId = request.getParameter("merchant_id");
        if (merId != null && !merId.isBlank()) req.setMerchantId(Integer.valueOf(merId.trim()));
        req.setMobile(request.getParameter("mobile"));
        req.setEmail(request.getParameter("email"));
        return bookingService.getCustomer(req);
    }

    @GetMapping(value = {"/api/dat-lich/getCustomerByMobile", "/dat-lich/getCustomerByMobile"})
    public CustomerLookupResponse getCustomerGet(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "merchant_id", required = false) Integer merchantId) {
        CustomerLookupRequest req = new CustomerLookupRequest();
        req.setMobile(mobile);
        req.setEmail(email);
        req.setMerchantId(merchantId);
        return bookingService.getCustomer(req);
    }

    // -------------------------------------------------------------
    // 3. CREATE BOOKING (Tạo lịch hẹn)
    // -------------------------------------------------------------
    @PostMapping(value = {"/api/dat-lich", "/dat-lich", "/api/dat-lich/", "/dat-lich/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingCreateResponse createBookingJson(@RequestBody BookingCreateRequest request) {
        return bookingService.createBooking(request);
    }

    @PostMapping(value = {"/api/dat-lich", "/dat-lich", "/api/dat-lich/", "/dat-lich/"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public BookingCreateResponse createBookingForm(HttpServletRequest request) {
        BookingCreateRequest req = new BookingCreateRequest();

        String locId = getParam(request, "location_id", "locationId");
        if (locId != null) req.setLocationId(Integer.valueOf(locId));

        String merId = getParam(request, "merchant_id", "merchantId");
        if (merId != null) req.setMerchantId(Integer.valueOf(merId));

        String custId = getParam(request, "customer_id", "customerId");
        if (custId != null) req.setCustomerId(Integer.valueOf(custId));

        req.setGender(getParam(request, "gender"));
        req.setBookedAt(getParam(request, "booked_at", "bookedAt"));
        req.setNote(getParam(request, "note"));
        req.setCustomerName(getParam(request, "customer_name", "customerName"));
        req.setMobile(getParam(request, "mobile"));
        req.setPhoneCountryCode(getParam(request, "phone_country_code", "phoneCountryCode"));
        req.setEmail(getParam(request, "email"));
        req.setBookingCode(getParam(request, "booking_code", "bookingCode"));

        String guests = getParam(request, "number_of_guests", "numberOfGuests", "guests");
        if (guests != null) req.setNumberOfGuests(Integer.valueOf(guests));

        String est = getParam(request, "estimated_duration", "estimatedDuration");
        if (est != null) req.setEstimatedDuration(Integer.valueOf(est));

        String city = getParam(request, "city_code", "cityCode");
        if (city != null) req.setCityCode(Integer.valueOf(city));

        String dist = getParam(request, "district_code", "districtCode");
        if (dist != null) req.setDistrictCode(Integer.valueOf(dist));

        req.setExternalUserId(getParam(request, "externalUserId"));
        req.setExternalAppId(getParam(request, "externalAppId"));

        // Parse danh sách product_id và operator_id từ form-urlencoded
        req.setProductId(parseIntegerList(request, "product_id[]", "product_id", "productId"));
        req.setOperatorId(parseIntegerList(request, "operator_id[]", "operator_id", "operatorId"));

        return bookingService.createBooking(req);
    }

    // -------------------------------------------------------------
    // 4. MERCHANT DATA / BOOTSTRAP (Dữ liệu salon đầy đủ từ DB)
    // -------------------------------------------------------------
    @GetMapping(value = {"/api/public/merchant-data", "/api/bootstrap", "/api/merchant"})
    public MerchantDataResponse getMerchantData() {
        return bookingService.getMerchantData();
    }

    // ----------------- Helpers -----------------
    private String getParam(HttpServletRequest req, String... names) {
        for (String name : names) {
            String val = req.getParameter(name);
            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }
        return null;
    }

    private List<Integer> parseIntegerList(HttpServletRequest req, String... names) {
        List<Integer> list = new ArrayList<>();
        for (String name : names) {
            String[] vals = req.getParameterValues(name);
            if (vals != null && vals.length > 0) {
                for (String v : vals) {
                    if (v != null && !v.isBlank()) {
                        try {
                            list.add(Integer.parseInt(v.trim()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                if (!list.isEmpty()) return list;
            }
        }
        return list;
    }
}
