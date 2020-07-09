package com.shaphan;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Program output on my machine:
 * <pre>
 *  1. Starting test: Reflection (String.value.length)
 * 589969586
 * Calculated 20000000 strings size in 201 ms
 *
 * 2. Starting test: String.length()
 * 590002921
 * Calculated 20000000 strings size in 246 ms
 *
 * 3. Starting test: String.getBytes().length
 * 590002972
 * Calculated 20000000 strings size in 864 ms
 *
 * 4. Starting test: Reflection (String.value.length)
 * 589980841
 * Calculated 20000000 strings size in 285 ms
 *
 * 5. Starting test: String.length()
 * 589981050
 * Calculated 20000000 strings size in 191 ms
 *
 * 6. Starting test: String.getBytes().length
 * 589930404
 * Calculated 20000000 strings size in 688 ms
 *
 * 7. Starting test: Reflection (String.value.length)
 * 590021459
 * Calculated 20000000 strings size in 303 ms
 *
 * 8. Starting test: String.length()
 * 589980730
 * Calculated 20000000 strings size in 266 ms
 *
 * 9. Starting test: String.getBytes().length
 * 590010051
 * Calculated 20000000 strings size in 707 ms
 * ...
 * </pre>
 */
public class TestApp {
    private static final int STRINGS_COUNT = 20_000_000;
    private static final Class<String> STRING_CLASS = String.class;
    private static final Field VALUE_FIELD;
    private static final int  NANOS_PER_MILLI    = 1000_000;
    private static int num = 1;

    static {
        try {
            VALUE_FIELD = STRING_CLASS.getDeclaredField("value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        VALUE_FIELD.setAccessible(true);
    }

    public static void main(String[] args) {
        IntStream.range(0, 10).forEach(i -> {
            runTest("Reflection (String.value.length)", TestApp::getSizeWithReflection);
            runTest("String.length()", String::length);
            runTest("String.getBytes().length", s -> s.getBytes().length);
        });
    }

    private static void runTest(String testName, Function<String, Integer> sizeCalc) {
        System.out.println(num++ + ". Starting test: " + testName);
        List<String> strings = generateRandomStrings();

        long startNanos = System.nanoTime();
        try {
            long sum = strings.stream()
                    .mapToLong(sizeCalc::apply)
                    .sum();
            System.out.println(sum);
        } finally {
            long endNanos = System.nanoTime();
            System.out.println(String.format("Calculated %d strings size in %d ms\n",
                    STRINGS_COUNT, (endNanos - startNanos) / NANOS_PER_MILLI));
        }
    }

    private static int getSizeWithReflection(String s) {
        try {
            byte[] value = (byte[]) VALUE_FIELD.get(s);
            return value.length;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> generateRandomStrings() {
        return IntStream.range(0, STRINGS_COUNT)
                .mapToObj(i -> RandomStringUtils.randomAlphanumeric(20, 40))
                .collect(Collectors.toList());
    }
}
