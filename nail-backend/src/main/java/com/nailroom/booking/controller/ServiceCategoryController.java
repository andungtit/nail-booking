package com.nailroom.booking.controller;

import com.nailroom.booking.dto.ListCategoriesRequest;
import com.nailroom.booking.dto.ServiceCategoryRequest;
import com.nailroom.booking.dto.response.BaseListResponse;
import com.nailroom.booking.entity.ServiceCategory;
import com.nailroom.booking.service.ServiceCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/service-categories", "/api/admin/service-categories"})
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    public ServiceCategoryController(ServiceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public BaseListResponse<ServiceCategory> getAll(ListCategoriesRequest request) {
        return BaseListResponse.of(categoryService.list(request));
    }

    @GetMapping("/{id}")
    public ServiceCategory getById(@PathVariable Integer id) {
        return categoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceCategory create(@Valid @RequestBody ServiceCategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public ServiceCategory update(@PathVariable Integer id, @Valid @RequestBody ServiceCategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        categoryService.delete(id);
    }
}
