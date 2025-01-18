package me.rejomy.chest.util;

import java.util.Random;

public class RandomUtil {
    public final static Random RANDOM = new Random();

    public static int getRandom(int max, int min) {
        return max == min? max : RANDOM.nextInt(max + 1 - min) + min;
    }

    // Weight give priority for values weight
    // If weight larger than one, increase value of middle to max.
    // If weight lower than one, decrease value of middle to min
    public static int getRandom(int max, int min, double weight) {
        // Проверка корректности значений
        if (min > max) {
            throw new IllegalArgumentException("Минимальное значение не может быть больше максимального");
        }

        if (weight <= 0) {
            throw new IllegalArgumentException("Вес должен быть больше 0");
        }

        // Генерация случайного числа в диапазоне от 0 до 1
        double random = Math.random();

        // Корректировка случайного числа с учетом веса
        if (weight > 1) {
            random = Math.pow(random, 1 / weight);
        } else {
            random = 1 - Math.pow(1 - random, 1 * weight);
        }
        // Вычисление и округление случайного числа в заданном диапазоне
        return (int) Math.round(min + random * (max - min + 1));
    }
}
