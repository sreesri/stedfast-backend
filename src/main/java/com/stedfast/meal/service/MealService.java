package com.stedfast.meal.service;

import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.repository.DishRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    @Transactional
    public Dish createDish(String userId, DishRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dish dish = new Dish();
        dish.setUser(user);
        dish.setName(request.getName());
        dish.setCalories(request.getCalories());
        dish.setProtein(request.getProtein());
        dish.setCarbs(request.getCarbs());
        dish.setFat(request.getFat());

        return dishRepository.save(dish);
    }

    public List<Dish> getUserDishes(String userId) {
        return dishRepository.findAllByUserId(userId);
    }

    public void deleteDish(String userId, String dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        
        if (!dish.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        dishRepository.delete(dish);
    }
}
