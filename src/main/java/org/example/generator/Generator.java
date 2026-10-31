package org.example.generator;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class Generator {
    private static final int MAX_DEPTH = 7;
    private int depth = 0;
    private final Map<Class<?>, Supplier<?>> primitiveGenerators = new HashMap<>();
    private final Random random = new Random();

    public Generator(){

        primitiveGenerators.put(int.class, () -> random.nextInt(-100, 101));
        primitiveGenerators.put(Integer.class, () -> random.nextInt(-100, 101));
        primitiveGenerators.put(double.class, () -> random.nextDouble(-100.0, 100.0));
        primitiveGenerators.put(Double.class, () -> random.nextDouble(-100.0, 100.0));
        primitiveGenerators.put(short.class, () -> (short) random.nextInt(-100, 101));
        primitiveGenerators.put(Short.class, () -> (short) random.nextInt(-100, 101));
        primitiveGenerators.put(long.class, () -> random.nextLong(-100L, 101L));
        primitiveGenerators.put(Long.class, () -> random.nextLong(-100L, 101L));
        primitiveGenerators.put(float.class, () -> random.nextFloat(-100.0f, 100.0f));
        primitiveGenerators.put(Float.class, () -> random.nextFloat(-100.0f, 100.0f));
        primitiveGenerators.put(boolean.class, random::nextBoolean);
        primitiveGenerators.put(Boolean.class, random::nextBoolean);
        primitiveGenerators.put(char.class, () -> (char) ('a' + random.nextInt(26)));
        primitiveGenerators.put(Character.class, () -> (char) ('a' + random.nextInt(26)));
        primitiveGenerators.put(byte.class, () -> (byte) random.nextInt(-100, 101));
        primitiveGenerators.put(Byte.class, () -> (byte) random.nextInt(-100, 101));
        primitiveGenerators.put(String.class, this::generateRandomString);
    }


    @SuppressWarnings("unchecked")
    public <T> T generateValueOfType(Class<T> clazz){
        return (T) generate((Type) clazz);
    }


    private Object generate(Type type) {
        if (type == null) {
            throw new GenerationException("class cannot be null");
        }
        depth++;
        try {
            if (depth > MAX_DEPTH) {
                if (type instanceof Class<?> c && c.isPrimitive()) {
                    return primitiveGenerators.get(c).get();
                }
                return null;
            }

            //generics
            if (type instanceof ParameterizedType paramType) {
                Class<?> clazz = (Class<?>) paramType.getRawType();
                Type[] types = paramType.getActualTypeArguments();
                if (List.class.isAssignableFrom(clazz)){
                    Class<?> elementClazz = (Class<?>) types[0];
                    return generateList(clazz, elementClazz);
                }
                if (Map.class.isAssignableFrom(clazz)) {
                    Class<?> keyClazz = (Class<?>) types[0];
                    Class<?> valueClazz = (Class<?>) types[1];
                    return generateMap(clazz, keyClazz, valueClazz);
                }

            }

            //classes
            if (type instanceof Class<?> clazz) {
                if (clazz.isAnnotation()) {
                    throw new IllegalArgumentException("Cannot generate annotation");
                }
                if (primitiveGenerators.containsKey(clazz)) {
                    return primitiveGenerators.get(clazz).get(); // cast to T?
                }
                if (clazz.isEnum()) {
                    return generateEnum(clazz);
                }
                if (clazz.isArray()) {
                    return generateArray(clazz);
                }
                //random List<Integer>
                if (List.class.isAssignableFrom(clazz)) {
                    return generateList(clazz, Integer.class);
                }
                //random Map<Integer, String>
                if (Map.class.isAssignableFrom(clazz)) {
                    return generateMap(clazz, Integer.class, String.class);
                }
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                    return generateFromInterface(clazz);
                }
                return generateObject(clazz);
            }
        } catch (IllegalArgumentException e) {
            throw new GenerationException("Generation exception: " + e.getMessage());
        } finally {
            depth--;
        }
        throw new GenerationException("Unsupported type: " + type.getTypeName());
    }

    private Object generateObject(Class<?> clazz) {
        Generatable annotation = clazz.getAnnotation(Generatable.class);
        try {
            if (annotation != null && !annotation.factoryMethod().isEmpty()) {
                String factoryMethodName = annotation.factoryMethod();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(factoryMethodName)) {
                        method.setAccessible(true);
                        return method.invoke(null, random);
                    }
                }
                throw new GenerationException("Factory method '" + factoryMethodName
                        + "' not found in class " + clazz.getName());
            }
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                throw new GenerationException("No constructors found in class " + clazz.getName());
            }
            Constructor<?> ctor = constructors[random.nextInt(constructors.length)];
            ctor.setAccessible(true);
            Type[] paramTypes = ctor.getGenericParameterTypes();
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < args.length; i++){
                args[i] = generate(paramTypes[i]);
            }
            return ctor.newInstance(args);
        }
        catch (ReflectiveOperationException e){
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new GenerationException("Failed to generate object of class " + clazz.getName()
                    + " because: " + cause.getMessage());
        }

    }

    private Object generateFromInterface(Class<?> clazz){
        Generatable annotation = clazz.getAnnotation(Generatable.class);
        if (annotation == null) {
            throw new GenerationException("Cannot generate object from interface or abstract class " +
                    "if its not annotated with @Generatable: " + clazz.getName());
        }
        Class<?>[] implementations = annotation.value();
        if (implementations.length == 0){
            throw new GenerationException("No specified implementations for @Generatable annotation for interface"
                    + clazz.getName());
        }
        int randIndex = random.nextInt(0, implementations.length);
        return generate(implementations[randIndex]);
    }

    private Object generateEnum(Class<?> clazz){
        Object[] constants = clazz.getEnumConstants();
        if  (constants.length == 0) {
            throw new IllegalArgumentException("Empty enum");
        }
        int randInt = random.nextInt(constants.length);
        return constants[randInt];
    }

    // int[], String[]
    private Object generateArray(Class<?> clazz) {
        Class<?> componentType = clazz.componentType(); //int, String
        int n = random.nextInt(1, 5);
        Object array = Array.newInstance(componentType, n);
        for (int i = 0; i < n; i++){
            Object object = generate(componentType); // recursion
            Array.set(array,i, object);
        }
        return array;
    }

    private List<?> generateList(Class<?> clazz, Class<?> elementType) {
        List<Object> list = generateEmptyList(clazz);
        int n = random.nextInt(1, 5);
        for (int i = 0; i < n; i++){
            Object object = generate(elementType); // recursion
            list.add(object);
        }
        return list;
    }

    private List<Object> generateEmptyList(Class<?> clazz){
         if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return new ArrayList<>();
        }
        try {
            return (List<Object>) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new GenerationException("Failed to create list of given class");
        }
    }

    private Map<?, ?> generateMap(Class<?> clazz, Class<?> keyClazz, Class<?> valueClazz) {
        Map<Object, Object> map = generateEmptyMap(clazz);
        int n = random.nextInt(1, 5);
        for (int i = 0; i < n; i++){
            Object keyObject = generate(keyClazz); // recursion
            Object valueObject = generate(valueClazz);
            map.put(keyObject,valueObject);
        }
        return map;
    }

    private Map<Object, Object> generateEmptyMap(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return new HashMap<>();
        }
        try {
            return (Map<Object, Object>) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new GenerationException("Failed to create Map of given class");
        }
    }

    private String generateRandomString() {
        int len = random.nextInt(3, 10);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }


//    public Object generateValueOfType(Class<?> clazz)
//            throws InvocationTargetException
//            , InstantiationException
//            , IllegalAccessException {
//        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
//
//        int randomConstructorIndex = new Random().nextInt(constructors.length);
//        Constructor<?> randomConstructor = constructors[randomConstructorIndex];
//        return randomConstructor.newInstance(111);
//
//    }



}
