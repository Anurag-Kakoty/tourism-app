package com.tourism.backend.festival.entity;

import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

//import java.util.HashSet;
//import java.util.Set;

@Entity
@Table(
        name = "festivals",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Festival extends BaseEntity {

    @Column(nullable = false, length = 150)
    @Size(max = 150)
    private String name;

    @Column(length = 2000)
    @Size(max = 2000)
    private String description;

    @Column(length = 100)
    @Size(max = 100)
    private String category;

    @Column(length = 500)
    @Size(max = 500)
    private String imageUrl;

    @Column(length = 500)
    @Size(max = 500)
    private String officialWebsite;
/*
    @OneToMany(
            mappedBy = "festival",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<FestivalOccurrence> occurrences = new HashSet<>();

 */
}