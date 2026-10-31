package org.example.generator;

import org.example.classes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTest {

    private Generator generator;

    enum TestColor {
        RED, GREEN, BLUE
    }

    enum EmptyEnum {}

    interface UnannotatedInterface {}

    @BeforeEach
    void setUp() {
        generator = new Generator();
    }

    // -------------------------------------------------------------
    // Примитивные типы и их обёртки
    // -------------------------------------------------------------

    @Test
    void testIntPrimitiveAndWrapper() {
        int primitiveInt = generator.generateValueOfType(int.class);
        Integer wrapperInt = generator.generateValueOfType(Integer.class);
        assertNotNull(wrapperInt);
    }

    @Test
    void testDoublePrimitiveAndWrapper() {
        double primitiveDouble = generator.generateValueOfType(double.class);
        Double wrapperDouble = generator.generateValueOfType(Double.class);
        assertNotNull(wrapperDouble);
        assertFalse(Double.isNaN(primitiveDouble));
        assertFalse(Double.isNaN(wrapperDouble));
    }

    @Test
    void testBooleanPrimitiveAndWrapper() {
        boolean primitiveBool = generator.generateValueOfType(boolean.class);
        Boolean wrapperBool = generator.generateValueOfType(Boolean.class);
        assertNotNull(wrapperBool);
    }

    @Test
    void testCharPrimitiveAndWrapper() {
        char primitiveChar = generator.generateValueOfType(char.class);
        Character wrapperChar = generator.generateValueOfType(Character.class);
        assertNotNull(wrapperChar);
        assertTrue(primitiveChar >= 'a' && primitiveChar <= 'z');
        assertTrue(wrapperChar >= 'a' && wrapperChar <= 'z');
    }

    @Test
    void testBytePrimitiveAndWrapper() {
        byte primitiveByte = generator.generateValueOfType(byte.class);
        Byte wrapperByte = generator.generateValueOfType(Byte.class);
        assertNotNull(wrapperByte);
    }

    @Test
    void testShortPrimitiveAndWrapper() {
        short primitiveShort = generator.generateValueOfType(short.class);
        Short wrapperShort = generator.generateValueOfType(Short.class);
        assertNotNull(wrapperShort);
    }

    @Test
    void testLongPrimitiveAndWrapper() {
        long primitiveLong = generator.generateValueOfType(long.class);
        Long wrapperLong = generator.generateValueOfType(Long.class);
        assertNotNull(wrapperLong);
    }

    @Test
    void testFloatPrimitiveAndWrapper() {
        float primitiveFloat = generator.generateValueOfType(float.class);
        Float wrapperFloat = generator.generateValueOfType(Float.class);
        assertNotNull(wrapperFloat);
        assertFalse(Float.isNaN(primitiveFloat));
        assertFalse(Float.isNaN(wrapperFloat));
    }

    // -------------------------------------------------------------
    // Строки
    // -------------------------------------------------------------

    @Test
    void testStringGeneration() {
        String result = generator.generateValueOfType(String.class);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // -------------------------------------------------------------
    // Перечисления (Enum)
    // -------------------------------------------------------------

    @Test
    void testEnumGeneration() {
        TestColor color = generator.generateValueOfType(TestColor.class);
        assertNotNull(color);
        assertTrue(color == TestColor.RED || color == TestColor.GREEN || color == TestColor.BLUE);
    }

    // -------------------------------------------------------------
    // Массивы
    // -------------------------------------------------------------

    @Test
    void testIntArrayGeneration() {
        int[] array = generator.generateValueOfType(int[].class);
        assertNotNull(array);
        assertTrue(array.length >= 0);
    }

    @Test
    void testStringArrayGeneration() {
        String[] array = generator.generateValueOfType(String[].class);
        assertNotNull(array);
        for (String item : array) {
            assertNotNull(item);
        }
    }

    @Test
    void testEnumArrayGeneration() {
        TestColor[] array = generator.generateValueOfType(TestColor[].class);
        assertNotNull(array);
        for (TestColor item : array) {
            assertNotNull(item);
        }
    }

    @Test
    void testObjectArrayGeneration() {
        Rectangle[] array = generator.generateValueOfType(Rectangle[].class);
        assertNotNull(array);
        for (Rectangle item : array) {
            assertNotNull(item);
            assertTrue(item.getArea() > 0);
        }
    }

    @Test
    void testTwoDimensionalIntArrayGeneration() {
        int[][] matrix = generator.generateValueOfType(int[][].class);
        assertNotNull(matrix);
        for (int[] row : matrix) {
            assertNotNull(row);
        }
    }

    @Test
    void testTwoDimensionalStringArrayGeneration() {
        String[][] matrix = generator.generateValueOfType(String[][].class);
        assertNotNull(matrix);
        for (String[] row : matrix) {
            assertNotNull(row);
            for (String item : row) {
                assertNotNull(item);
            }
        }
    }

    // -------------------------------------------------------------
    // List/Map
    // -------------------------------------------------------------

    @Test
    void testListGeneration() {
        List<?> list = generator.generateValueOfType(List.class);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        for (Object item : list) {
            assertNotNull(item);
        }
    }

    @Test
    void testMapGeneration() {
        Map<?, ?> map = generator.generateValueOfType(Map.class);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    // -------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------

    @Test
    void testExampleClass() {
        Example example = generator.generateValueOfType(Example.class);
        assertNotNull(example);
    }

    @Test
    void testProductClass() {
        Product product = generator.generateValueOfType(Product.class);
        assertNotNull(product);
        assertNotNull(product.getName());
        assertFalse(product.getName().isEmpty());
    }

    @Test
    void testRectangleClass() {
        Rectangle rectangle = generator.generateValueOfType(Rectangle.class);
        assertNotNull(rectangle);
        assertTrue(rectangle.getArea() > 0);
        assertTrue(rectangle.getPerimeter() > 0);
    }

    @Test
    void testTriangleClassWithDomainConstraints() {
        Triangle triangle = generator.generateValueOfType(Triangle.class);
        assertNotNull(triangle);
        assertFalse(Double.isNaN(triangle.getArea()));
        assertTrue(triangle.getArea() > 0);
        assertTrue(triangle.getPerimeter() > 0);
    }

    @Test
    void testGeneratedTriangleValidity() throws Exception {
        java.lang.reflect.Field sideAField = Triangle.class.getDeclaredField("sideA");
        java.lang.reflect.Field sideBField = Triangle.class.getDeclaredField("sideB");
        java.lang.reflect.Field sideCField = Triangle.class.getDeclaredField("sideC");
        sideAField.setAccessible(true);
        sideBField.setAccessible(true);
        sideCField.setAccessible(true);

        for (int i = 0; i < 100; i++) {
            Triangle triangle = generator.generateValueOfType(Triangle.class);
            assertNotNull(triangle);

            double a = (double) sideAField.get(triangle);
            double b = (double) sideBField.get(triangle);
            double c = (double) sideCField.get(triangle);

            assertTrue(a > 0);
            assertTrue(b > 0);
            assertTrue(c > 0);

            assertTrue(a + b > c);
            assertTrue(a + c > b);
            assertTrue(b + c > a);

            assertFalse(Double.isNaN(triangle.getArea()));
            assertTrue(triangle.getArea() > 0);
        }
    }

    @Test
    void testShapeInterfaceWithGeneratableAnnotation() {
        Shape shape = generator.generateValueOfType(Shape.class);
        assertNotNull(shape);
        assertTrue(shape instanceof Rectangle || shape instanceof Triangle);
        assertFalse(Double.isNaN(shape.getArea()));
        assertTrue(shape.getArea() > 0);
        assertTrue(shape.getPerimeter() > 0);
    }

    @Test
    void testBinaryTreeNodeRecursiveStructure() {
        BinaryTreeNode root = generator.generateValueOfType(BinaryTreeNode.class);
        assertNotNull(root);
        assertNotNull(root.getData());
        assertNotNull(root.getLeft());
        assertNotNull(root.getRight());
    }

    @Test
    void testCartClassWithGenericListProduct() {
        Cart cart = generator.generateValueOfType(Cart.class);
        assertNotNull(cart);
        assertNotNull(cart.getItems());
        assertFalse(cart.getItems().isEmpty());

        for (Product product : cart.getItems()) {
            assertNotNull(product);
            assertNotNull(product.getName());
            assertFalse(product.getName().isEmpty());
        }

    }

    // -------------------------------------------------------------
    // Negative
    // -------------------------------------------------------------

    @Test
    void testNullClassThrowsGenerationException() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(null));
    }

    @Test
    void testUnannotatedInterfaceThrowsGenerationException() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(UnannotatedInterface.class));
    }

    @Test
    void testEmptyEnumThrowsGenerationException() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(EmptyEnum.class));
    }

    @Test
    void testAnnotationClassThrowsGenerationException() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Deprecated.class));
    }

    static class FaultyClass {
        public FaultyClass(String s) {
            throw new IllegalStateException("Business constraint violation in constructor");
        }
    }

    static class ComplexObjectWithUnsatisfiableDependency {
        public ComplexObjectWithUnsatisfiableDependency(UnannotatedInterface dep) {}
    }

    @Generatable(factoryMethod = "nonExistentMethod")
    static class ClassWithBadFactoryMethod {}

    @Test
    void testExceptionContainsInformativeMessageForUnannotatedInterface() {
        GenerationException e = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(UnannotatedInterface.class));
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("UnannotatedInterface"));
        assertTrue(e.getMessage().contains("@Generatable"));
    }

    @Test
    void testExceptionContainsCauseWhenConstructorFails() {
        GenerationException e = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(FaultyClass.class));
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("FaultyClass"));
        assertTrue(e.getMessage().contains("Business constraint violation in constructor"));
    }

    @Test
    void testExceptionForComplexObjectWithUnsatisfiableDependency() {
        GenerationException e = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(ComplexObjectWithUnsatisfiableDependency.class));
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("UnannotatedInterface"));
    }

    @Test
    void testExceptionWhenFactoryMethodNotFound() {
        GenerationException e = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(ClassWithBadFactoryMethod.class));
        assertNotNull(e.getMessage());
        assertTrue(e.getMessage().contains("nonExistentMethod"));
        assertTrue(e.getMessage().contains("ClassWithBadFactoryMethod"));
    }
}
