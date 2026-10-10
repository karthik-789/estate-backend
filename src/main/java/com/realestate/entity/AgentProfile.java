package com.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class AgentProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(length = 140) private String agencyName;
    @Column(length = 100) private String licenseNumber;
    private Integer experience;
    @Column(length = 2000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
}

