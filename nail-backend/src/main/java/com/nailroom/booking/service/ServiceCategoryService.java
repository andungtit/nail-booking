package com.nailroom.booking.service;

import com.nailroom.booking.dto.ListCategoriesRequest;
import com.nailroom.booking.dto.ServiceCategoryRequest;
import com.nailroom.booking.entity.ServiceCategory;
import com.nailroom.booking.repository.ServiceCategoryRepository;
import com.nailroom.booking.repository.ServiceRepository;
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
public class ServiceCategoryService extends BaseListService<ServiceCategory, ListCategoriesRequest> {

    private final ServiceCategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public ServiceCategoryService(ServiceCategoryRepository categoryRepository,
                                  ServiceRepository serviceRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    protected JpaSpecificationExecutor<ServiceCategory> executor() {
        return categoryRepository;
    }

    @Override
    protected Specification<ServiceCategory> buildSpecification(ListCategoriesRequest r) {
        return FilterUtils.allOf(
                FilterUtils.like("name", r.getQ())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategory> list(ListCategoriesRequest request) {
        Page<ServiceCategory> page = super.list(request);
        Map<Integer, Long> counts = new HashMap<>();
        for (Object[] row : serviceRepository.countGroupByCategory()) {
            counts.put((Integer) row[0], (Long) row[1]);
        }
        page.getContent().forEach(c -> c.setServiceCount(counts.getOrDefault(c.getId(), 0L)));
        return page;
    }

    @Transactional(readOnly = true)
    public List<ServiceCategory> getAll() {
        List<ServiceCategory> list = categoryRepository.findAll();
        Map<Integer, Long> counts = new HashMap<>();
        for (Object[] row : serviceRepository.countGroupByCategory()) {
            counts.put((Integer) row[0], (Long) row[1]);
        }
        list.forEach(c -> c.setServiceCount(counts.getOrDefault(c.getId(), 0L)));
        return list;
    }

    @Transactional(readOnly = true)
    public ServiceCategory getById(Integer id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm dịch vụ #" + id));
        category.setServiceCount(serviceRepository.countByCategoryId(id));
        return category;
    }

    public ServiceCategory create(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        copyFields(category, request);
        return categoryRepository.save(category);
    }

    public ServiceCategory update(Integer id, ServiceCategoryRequest request) {
        ServiceCategory category = getById(id);
        copyFields(category, request);
        return categoryRepository.save(category);
    }

    public void delete(Integer id) {
        ServiceCategory category = getById(id);
        long n = serviceRepository.countByCategoryId(id);
        if (n > 0) {
            throw new IllegalStateException("Nhóm đang có " + n + " dịch vụ, hãy chuyển/xóa dịch vụ trước.");
        }
        categoryRepository.delete(category);
    }

    private void copyFields(ServiceCategory c, ServiceCategoryRequest r) {
        c.setName(r.getName().trim());
        c.setSortOrder(r.getSortOrder() == null ? 0 : r.getSortOrder());
    }
}
