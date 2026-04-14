package com.stedfast.meal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stedfast.user.models.User;
import de.fxlae.typeid.TypeId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_intake_summary")
@Data
@NoArgsConstructor
public class UserIntakeSummary {

    @Id
    @Column(length = 50)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("intksum").toString();
        }
    }

    @JsonIgnore
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

    @Column(name = "logged_date", nullable = false)
    private ZonedDateTime loggedDate;

    @Column(name = "consumed_calories", nullable = false)
    private Integer consumedCalories = 0;

    @Column(name = "consumed_protein", nullable = false)
    private Integer consumedProtein = 0;

    @Column(name = "consumed_carbs", nullable = false)
    private Integer consumedCarbs = 0;

    @Column(name = "consumed_fat", nullable = false)
    private Integer consumedFat = 0;
}
