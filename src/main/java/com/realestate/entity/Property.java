package com.realestate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "properties")
@Getter @Setter @NoArgsConstructor
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 180) private String title;
    @Column(nullable = false, length = 5000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PropertyType propertyType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ListingType listingType;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal price;
    private Integer bedrooms;
    private Integer bathrooms;
    @Column(nullable = false) private Double area;
    @Column(nullable = false, length = 500) private String address;
    @Column(nullable = false, length = 80) private String city;
    @Column(nullable = false, length = 80) private String state;
    @Column(length = 12) private String pincode;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.AVAILABLE;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    @Column(length = 1000) private String rejectionReason;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}

