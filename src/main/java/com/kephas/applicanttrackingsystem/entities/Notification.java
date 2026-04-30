package com.kephas.applicanttrackingsystem.entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private UUID applicationId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    private LocalDateTime sentAt;


    @PrePersist
    public void onCreate() {
        this.status = "PENDING";
    }
}