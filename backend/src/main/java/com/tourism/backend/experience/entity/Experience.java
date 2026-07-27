package com.tourism.backend.experience.entity;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "experiences",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
public class Experience extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Size(max = 100)
    @Column(length = 100)
    private String icon;

    @ManyToMany(mappedBy = "experiences")
    private Set<Destination> destinations = new HashSet<>();
}