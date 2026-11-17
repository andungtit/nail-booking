package com.nailroom.booking.repository;

import com.nailroom.booking.entity.TechnicianSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TechnicianScheduleRepository extends JpaRepository<TechnicianSchedule, Integer>, JpaSpecificationExecutor<TechnicianSchedule> {

    @Override
    @EntityGraph(attributePaths = {"technician", "technician.location"})
    Page<TechnicianSchedule> findAll(Specification<TechnicianSchedule> spec, Pageable pageable);

    @Query("select s from TechnicianSchedule s where s.technician.id = :technicianId and s.workDate = :workDate")
    List<TechnicianSchedule> findByTechnicianIdAndWorkDate(@Param("technicianId") Integer technicianId, @Param("workDate") LocalDate workDate);

    @Query("select count(s) from TechnicianSchedule s where s.technician.id = :technicianId")
    long countByTechnicianId(@Param("technicianId") Integer technicianId);

    long countByWorkDate(LocalDate workDate);

    @Modifying
    @Query("delete from TechnicianSchedule s where s.technician.id = :technicianId")
    int deleteByTechnicianId(@Param("technicianId") Integer technicianId);
}
