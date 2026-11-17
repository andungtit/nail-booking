package com.nailroom.booking.service;

import com.nailroom.booking.dto.ListServicesRequest;
import com.nailroom.booking.dto.ServiceRequest;
import com.nailroom.booking.entity.ServiceCategory;
import com.nailroom.booking.repository.AppointmentRepository;
import com.nailroom.booking.repository.ServiceCategoryRepository;
import com.nailroom.booking.repository.ServiceRepository;
import com.nailroom.booking.service.base.BaseListService;
import com.nailroom.booking.util.FilterUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceService extends BaseListService<com.nailroom.booking.entity.Service, ListServicesRequest> {

    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final AppointmentRepository appointmentRepository;

    public ServiceService(ServiceRepository serviceRepository,
                          ServiceCategoryRepository categoryRepository,
                          AppointmentRepository appointmentRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    protected JpaSpecificationExecutor<com.nailroom.booking.entity.Service> executor() {
        return serviceRepository;
    }

    @Override
    protected Specification<com.nailroom.booking.entity.Service> buildSpecification(ListServicesRequest r) {
        return FilterUtils.allOf(
                FilterUtils.anyLike(r.getQ(), "name", "description"),
                FilterUtils.eq("category.id", r.getCategoryId()),
                FilterUtils.eq("isActive", r.getIsActive())
        );
    }

    @Transactional(readOnly = true)
    public List<com.nailroom.booking.entity.Service> getAll(Integer categoryId) {
        if (categoryId != null) {
            return serviceRepository.findAll(FilterUtils.eq("category.id", categoryId));
        }
        return serviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public com.nailroom.booking.entity.Service getById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ #" + id));
    }

    public com.nailroom.booking.entity.Service create(ServiceRequest request) {
        com.nailroom.booking.entity.Service service = new com.nailroom.booking.entity.Service();
        copyFields(service, request);
        return serviceRepository.save(service);
    }

    public com.nailroom.booking.entity.Service update(Integer id, ServiceRequest request) {
        com.nailroom.booking.entity.Service service = getById(id);
        copyFields(service, request);
        return serviceRepository.save(service);
    }

    public void delete(Integer id) {
        com.nailroom.booking.entity.Service service = getById(id);
        long n = appointmentRepository.countByServiceId(id);
        if (n > 0) {
            throw new IllegalStateException("Dịch vụ đã có " + n + " lịch hẹn, chỉ có thể tạm ngừng phục vụ.");
        }
        serviceRepository.delete(service);
    }

    private void copyFields(com.nailroom.booking.entity.Service s, ServiceRequest r) {
        ServiceCategory category = categoryRepository.findById(r.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Nhóm dịch vụ #" + r.getCategoryId() + " không tồn tại"));
        s.setCategory(category);
        s.setName(r.getName().trim());
        s.setDurationMinutes(r.getDurationMinutes());
        s.setPrice(r.getPrice());
        s.setDescription(r.getDescription() == null || r.getDescription().isBlank() ? null : r.getDescription().trim());
        s.setIsActive(r.getIsActive() == null || r.getIsActive());
    }
}
