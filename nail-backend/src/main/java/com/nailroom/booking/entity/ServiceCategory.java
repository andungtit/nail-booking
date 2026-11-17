package com.nailroom.booking.entity;

import com.nailroom.booking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_categories")
@Getter
@Setter
public class ServiceCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Transient
    private Long serviceCount = 0L;
}
