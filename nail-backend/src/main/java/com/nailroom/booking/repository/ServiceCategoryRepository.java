package com.nailroom.booking.repository;

import com.nailroom.booking.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer>, JpaSpecificationExecutor<ServiceCategory> {
    boolean existsByNameIgnoreCase(String name);
}
