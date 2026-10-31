package org.example.classes;

import org.example.generator.Generatable;

import java.util.Random;

@Generatable(factoryMethod = "createValid")
public class Triangle implements Shape {
    private double sideA;
    private double sideB;
    private double sideC;

    public Triangle(double sideA, double sideB, double sideC) {
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    @Override
    public double getArea() {
        double s = (sideA + sideB + sideC) / 2;
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    @Override
    public double getPerimeter() {
        return sideA + sideB + sideC;
    }

    public static Triangle createValid(Random random) {
        double a = random.nextDouble(1, 50);
        double b = random.nextDouble(1, 50);
        double c = random.nextDouble(Math.abs(a - b) + 0.1, a + b - 0.1);
        return new Triangle(a, b, c);
    }
}