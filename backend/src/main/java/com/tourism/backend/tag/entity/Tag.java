package com.tourism.backend.tag.entity;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Tag extends BaseEntity {

    @NotBlank(message = "Tag name is required.")
    @Size(max = 100, message = "Tag name cannot exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "tags")
    private Set<Destination> destinations = new HashSet<>();

}