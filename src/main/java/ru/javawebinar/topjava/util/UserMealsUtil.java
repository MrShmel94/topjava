package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesInDay = new HashMap<>();
        List <UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal us : meals){
            if (TimeUtil.isBetweenHalfOpen(LocalTime.of(us.getDateTime().getHour(), us.getDateTime().getMinute()),startTime, endTime)) {
                caloriesInDay.merge(us.getDateTime().toLocalDate(), us.getCalories(), Integer::sum);
            }
        }
        for (UserMeal us: meals){
            if (TimeUtil.isBetweenHalfOpen(LocalTime.of(us.getDateTime().getHour(), us.getDateTime().getMinute()),startTime, endTime)){
                result.add(new UserMealWithExcess(us.getDateTime(), us.getDescription(), us.getCalories(), caloriesInDay.get(us.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map <LocalDate, Integer> caloriesInDay = meals.stream().filter(a -> TimeUtil.isBetweenHalfOpen(LocalTime.of(a.getDateTime().getHour(), a.getDateTime().getMinute()), startTime, endTime)).
                collect(Collectors.groupingBy(a -> (a.getDateTime().toLocalDate()), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream().filter(a -> TimeUtil.isBetweenHalfOpen(LocalTime.of(a.getDateTime().getHour(), a.getDateTime().getMinute()), startTime, endTime)).
                map(a -> new UserMealWithExcess(a.getDateTime(), a.getDescription(), a.getCalories(), caloriesInDay.get(a.getDateTime().toLocalDate()) > caloriesPerDay)).collect(Collectors.toList());
    }
}
