package com.tourism.backend.festivaloccurrence.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "festival_occurrences",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "festival_id",
                        "state_id",
                        "year"
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalOccurrence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Min(1900)
    @Max(3000)
    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Size(max = 150)
    @Column(length = 150)
    private String district;

    @Size(max = 200)
    @Column(length = 200)
    private String venue;

    @Size(max = 2000)
    @Column(length = 2000)
    private String notes;
}