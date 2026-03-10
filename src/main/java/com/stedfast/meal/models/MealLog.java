package com.stedfast.meal.models;

import java.time.ZonedDateTime;

import com.stedfast.user.models.User;

import de.fxlae.typeid.TypeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mealLog")
@Data
@NoArgsConstructor
public class MealLog {

    @Id
    @Column(length = 31)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("mealLog").toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "mealType")
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(name = "mealTime")
    private ZonedDateTime mealTime;

    @Column(name = "dish")
    private String dish;

    @Column(name = "calories")
    private Integer calories;

}
