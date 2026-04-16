package com.stedfast.meal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.fxlae.typeid.TypeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "meal_dishes")
@Data
@NoArgsConstructor
public class MealDish {

    @Id
    @Column(length = 50)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("mdish").toString();
        }
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    private String name;
    private Integer calories;

    @Column(precision = 5, scale = 2)
    private BigDecimal protein;

    @Column(precision = 5, scale = 2)
    private BigDecimal carbs;

    @Column(precision = 5, scale = 2)
    private BigDecimal fat;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
