package com.stedfast.health.models;

import com.stedfast.user.models.User;

import de.fxlae.typeid.TypeId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_intake_limits")
@Data
@NoArgsConstructor
public class UserIntakeLimit {

    @Id
    @Column(length = 50)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("intklimt").toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "calorie_limit", nullable = false)
    private Integer calorieLimit;

    @Column(name = "protein_limit", nullable = false)
    private Integer proteinLimit;

    @Column(name = "carbs_limit", nullable = false)
    private Integer carbsLimit;

    @Column(name = "fat_limit", nullable = false)
    private Integer fatLimit;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();
}
