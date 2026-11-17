package com.nailroom.booking.entity;

import com.nailroom.booking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "technicians")
@Getter
@Setter
public class Technician extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 50)
    private String phone;

    /** active, off, busy */
    @Column(length = 50)
    private String status = "active";

    public Integer getLocationId() {
        return location != null ? location.getId() : null;
    }

    public String getLocationName() {
        return location != null ? location.getName() : null;
    }
}
