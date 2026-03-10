package com.stedfast.fasting.models;

import com.stedfast.user.models.User;
import jakarta.persistence.*;
import de.fxlae.typeid.TypeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "fastingLog")
@Data
@NoArgsConstructor
public class FastingLog {

    @Id
    @Column(length = 31)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("fastlog").toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FastingStatus status;

    @Column(name = "startTime", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "endTime")
    private ZonedDateTime endTime;
}
