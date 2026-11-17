package com.nailroom.booking.repository;

import com.nailroom.booking.entity.Technician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Integer>, JpaSpecificationExecutor<Technician> {

    @Override
    @EntityGraph(attributePaths = "location")
    Page<Technician> findAll(Specification<Technician> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "location")
    List<Technician> findAll(Specification<Technician> spec, Sort sort);

    @Query("select case when count(t) > 0 then true else false end from Technician t where t.location.id = :locationId")
    boolean existsByLocationId(@Param("locationId") Integer locationId);

    @Query("select count(t) from Technician t where t.location.id = :locationId")
    long countByLocationId(@Param("locationId") Integer locationId);

    long countByStatus(String status);

    @Query("select t.location.id, count(t) from Technician t where t.location is not null group by t.location.id")
    List<Object[]> countGroupByLocation();
}
