package org.example.classes;

import org.example.generator.Generatable;

import java.util.Random;

@Generatable(factoryMethod = "createValid")
public class Rectangle implements Shape {
    private double length;
    private double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }

    public static Rectangle createValid(Random random) {
        double length = random.nextDouble(1.0, 100.0);
        double width = random.nextDouble(1.0, 100.0);
        return new Rectangle(length, width);
    }
}
