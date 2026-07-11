package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.*;

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
}