package com.nailroom.booking.repository;

import com.nailroom.booking.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("select count(a) from Appointment a where a.location.id = :locationId")
    long countByLocationId(@Param("locationId") Integer locationId);

    @Query("select count(a) from Appointment a where a.service.id = :serviceId")
    long countByServiceId(@Param("serviceId") Integer serviceId);

    @Query("select count(a) from Appointment a where a.technician.id = :technicianId")
    long countByTechnicianId(@Param("technicianId") Integer technicianId);
}
