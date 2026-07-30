package com.tourism.backend.guide.entity;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "guides",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phone"),
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "license_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Guide extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String bio;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String email;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "guide_languages",
            joinColumns = @JoinColumn(name = "guide_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Set<Language> languages = new HashSet<>();

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(nullable = false, length = 50)
    private String licenseNumber;

    @Column(nullable = false)
    private Boolean providesTransport = false;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id")
    private Destination destination;
}