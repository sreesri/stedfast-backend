package com.stedfast.meal.repository;

import com.stedfast.meal.models.MealDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealDishRepository extends JpaRepository<MealDish, String> {
    List<MealDish> findAllByMeal_Id(String mealId);
}
