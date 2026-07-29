package com.tourism.backend.festivaloccurrence.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Builder.Default
    @Column(nullable = false)
    private Boolean confirmed = false;

    @Column(length = 2000)
    private String notes;
}