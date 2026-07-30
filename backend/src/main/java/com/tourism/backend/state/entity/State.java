package com.tourism.backend.state.entity;

import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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