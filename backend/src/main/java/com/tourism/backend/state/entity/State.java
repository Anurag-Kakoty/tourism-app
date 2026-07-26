package com.tourism.backend.state.entity;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String capital;

    @Column(length = 2000)
    private String description;

    private String thumbnailUrl;

    @OneToMany(
            mappedBy = "state",
            fetch = FetchType.LAZY
    )
    private List<Destination> destinations = new ArrayList<>();
}