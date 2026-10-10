package com.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class PropertyImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 1000) private String imageUrl;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "property_id")
    private Property property;
}

