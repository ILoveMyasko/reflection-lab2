package org.example.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated interface should specify it's implementations that can be auto-generated using Reflections.
 * <p>
 * Annotated class should implement method {@code factoryMethod} for specific creation logic rather than random field values
 * produced by the generator.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Generatable {
    Class<?>[] value() default {};
    String factoryMethod() default "";
}
