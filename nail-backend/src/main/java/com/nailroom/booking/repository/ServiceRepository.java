package com.nailroom.booking.repository;

import com.nailroom.booking.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer>, JpaSpecificationExecutor<Service> {

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Service> findAll(Specification<Service> spec, Pageable pageable);

    @Query("select case when count(s) > 0 then true else false end from Service s where s.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("select count(s) from Service s where s.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("select s.category.id, count(s) from Service s where s.category is not null group by s.category.id")
    List<Object[]> countGroupByCategory();
}
