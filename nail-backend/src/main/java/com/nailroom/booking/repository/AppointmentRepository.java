package com.nailroom.booking.repository;

import com.nailroom.booking.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {

    @Override
    @EntityGraph(attributePaths = {"location", "customer", "technician", "service"})
    Page<Appointment> findAll(Specification<Appointment> spec, Pageable pageable);

    Optional<Appointment> findByBookingCode(String bookingCode);

    @Query("select a from Appointment a where a.location.id = :locationId and a.appointmentDate = :appointmentDate and a.status != :status")
    List<Appointment> findByLocationIdAndAppointmentDateAndStatusNot(@Param("locationId") Integer locationId,
                                                                   @Param("appointmentDate") LocalDate appointmentDate,
                                                                   @Param("status") String status);

    @Query("select a from Appointment a where a.technician.id = :technicianId and a.appointmentDate = :appointmentDate and a.status != :status")
    List<Appointment> findByTechnicianIdAndAppointmentDateAndStatusNot(@Param("technicianId") Integer technicianId,
                                                                     @Param("appointmentDate") LocalDate appointmentDate,
                                                                     @Param("status") String status);

    @Query("select count(a) from Appointment a where a.location.id = :locationId")
    long countByLocationId(@Param("locationId") Integer locationId);

    @Query("select count(a) from Appointment a where a.service.id = :serviceId")
    long countByServiceId(@Param("serviceId") Integer serviceId);

    @Query("select count(a) from Appointment a where a.technician.id = :technicianId")
    long countByTechnicianId(@Param("technicianId") Integer technicianId);

    long countByAppointmentDate(LocalDate appointmentDate);
}
