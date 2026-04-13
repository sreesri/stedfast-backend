package com.stedfast.fasting.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stedfast.user.models.User;
import de.fxlae.typeid.TypeId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "fasting_sessions")
@Data
@NoArgsConstructor
public class FastingSession {

    public enum SessionType {
        FAST, EAT
    }

    public enum SessionStatus {
        ACTIVE, COMPLETED
    }

    @Id
    @Column(length = 50)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("fastsess").toString();
        }
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private FastingSchedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", length = 10, nullable = false)
    private SessionType sessionType;

    @Column(name = "started_at", nullable = false)
    private ZonedDateTime startedAt = ZonedDateTime.now();

    @Column(name = "ended_at")
    private ZonedDateTime endedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;
}
