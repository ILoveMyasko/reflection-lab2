package org.example;

import org.example.classes.*;
import org.example.generator.GenerationException;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenerateExample {

    public static void main(String[] args) {
        var gen = new Generator();
        // 1. Примитивы и строки
        Integer number = gen.generateValueOfType(Integer.class);
        Double floating = gen.generateValueOfType(Double.class);
        String text = gen.generateValueOfType(String.class);
        System.out.println("1. Базовые типы:");
        System.out.println("Integer: " + number);
        System.out.println("Double:  " + floating);
        System.out.println("String:  \"" + text + "\"\n");

        // 2. Массивы (одномерные и двумерные)
        int[] numbers = gen.generateValueOfType(int[].class);
        int[][] matrix = gen.generateValueOfType(int[][].class);
        System.out.println("2. Массивы:");
        System.out.println("int[]:   " + Arrays.toString(numbers));
        System.out.println("int[][]: " + Arrays.deepToString(matrix) + "\n");

        // 3. Коллекции
        List<?> items = gen.generateValueOfType(List.class);
        Map<?, ?> entries = gen.generateValueOfType(Map.class);
        System.out.println("3. Коллекции:");
        System.out.println("List<?> (размер " + items.size() + "): " + items);
        System.out.println("Map<?, ?> (размер " + entries.size() + "): " + entries + "\n");

        // 4. Простые пользовательские объекты
        Example example = gen.generateValueOfType(Example.class);
        Product product = gen.generateValueOfType(Product.class);
        Rectangle rectangle = gen.generateValueOfType(Rectangle.class);
        System.out.println("4. Пользовательские объекты:");
        System.out.println("Example:   " + example);
        System.out.println("Product:   Product(name=\"" + product.getName() + "\", price=" + product.getPrice() + ")");
        System.out.println("Rectangle: Rectangle(area=" + rectangle.getArea() + ", perimeter=" + rectangle.getPerimeter() + ")\n");

        // 5. Треугольник с предметными ограничениями (@Generatable + createValid)
        Triangle triangle = gen.generateValueOfType(Triangle.class);
        System.out.println("5. Треугольник с валидацией геометрических ограничений:");
        System.out.println("Triangle:  Triangle(area=" + triangle.getArea() + ", perimeter=" + triangle.getPerimeter() + ")");
        System.out.println("Валиден?   " + (!Double.isNaN(triangle.getArea()) && triangle.getArea() > 0) + "\n");

        // 6. Генерация интерфейса через @Generatable
        Shape shape = gen.generateValueOfType(Shape.class);
        System.out.println("6. Интерфейс Shape (полиморфная генерация):");
        System.out.println("Сгенерированный класс: " + shape.getClass().getSimpleName());
        System.out.println("Площадь фигуры:        " + shape.getArea() + "\n");

        // 7. Сложный объект с дженериком List<Product>
        Cart cart = gen.generateValueOfType(Cart.class);
        System.out.println("7. Корзина Cart со списком List<Product>:");
        System.out.println("Количество товаров в корзине: " + cart.getItems().size());
        for (int i = 0; i < cart.getItems().size(); i++) {
            Product item = cart.getItems().get(i);
            System.out.println("     [" + (i + 1) + "] " + item.getName() + " -> " + item.getPrice());
        }
        System.out.println();

        // 8. Рекурсивное бинарное дерево
        BinaryTreeNode root = gen.generateValueOfType(BinaryTreeNode.class);
        System.out.println("8. Рекурсивная структура BinaryTreeNode:");
        System.out.println("Корень: data = " + root.getData());
        System.out.println("Левый потомок:  " + (root.getLeft() != null ? "Node(data=" + root.getLeft().getData() + ")" : "null (лист)"));
        System.out.println("Правый потомок: " + (root.getRight() != null ? "Node(data=" + root.getRight().getData() + ")" : "null (лист)"));
        System.out.println();

        // =================================================================
        // 9. ДЕМОНСТРАЦИЯ ИНФОРМАТИВНЫХ ИСКЛЮЧЕНИЙ ПРИ ОШИБКАХ
        // =================================================================
        System.out.println("=================================================================");
        System.out.println("       ПРОВЕРКА ОСМЫСЛЕННОСТИ ИСКЛЮЧЕНИЙ ПРИ СБОЯХ               ");
        System.out.println("=================================================================\n");

        // Случай А: Интерфейс без аннотации @Generatable
        demonstrateFailure(gen, Runnable.class, "Интерфейс Runnable без аннотации @Generatable");

        // Случай Б: Абстрактный класс без аннотации
        demonstrateFailure(gen, Number.class, "Абстрактный класс Number без аннотации @Generatable");

        // Случай В: Передача null
        demonstrateFailure(gen, null, "Передача null вместо типа");
    }

    private static void demonstrateFailure(Generator gen, Class<?> targetClass, String scenarioDescription) {
        System.out.println("Сценарий: " + scenarioDescription);
        try {
            gen.generateValueOfType(targetClass);
            System.out.println("   [ОШИБКА]: Генерация должна была упасть, но вернула значение!");
        } catch (GenerationException e) {
            System.out.println("   [УСПЕХ]: Выброшено понятное исключение GenerationException:");
            System.out.println("   Причина: \"" + e.getMessage() + "\"");
        }
        System.out.println();
    }
}
