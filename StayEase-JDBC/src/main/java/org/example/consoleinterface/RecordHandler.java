package org.example.consoleinterface;

import java.lang.reflect.Field;
import java.util.List;

public class RecordHandler {
    public  <T> void printTable(List<T> items, String title) {
        if (items == null || items.isEmpty()) {
            System.out.println("\nNo data found for " + title);
            return;
        }

        Class<?> clazz = items.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        System.out.println("=".repeat(20 * fields.length));
        // Print headers
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.printf("%-20s", field.getName());
        }
        System.out.println();
        System.out.println("=".repeat(20 * fields.length));

        // Print values
        for (T item : items) {
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(item);
                    System.out.printf("%-20s", String.valueOf(value));
                } catch (IllegalAccessException e) {
                    System.out.printf("%-20s", "N/A");
                }
            }
            System.out.println();
        }
        System.out.println("-".repeat(20 * fields.length));
    }
}

