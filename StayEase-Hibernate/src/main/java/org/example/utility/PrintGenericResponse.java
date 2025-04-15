package org.example.utility;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class PrintGenericResponse {

    public <T> void printTable(List<T> list, List<String> ignoreFields) {
        if (list == null || list.isEmpty()) return;

        Class<?> clazz = list.get(0).getClass();
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(f -> ignoreFields == null || !ignoreFields.contains(f.getName()))
                .peek(f -> f.setAccessible(true))
                .toList();

        int totalWidth = fields.size() * 17;
        String line = new String(new char[totalWidth]).replace('\0', '=');
        String lineEnd = new String(new char[totalWidth]).replace('\0', '-');

        System.out.println(line);
        for (Field field : fields) {
            System.out.printf("%-17s", field.getName());
        }
        System.out.println();
        System.out.println(line);

        for (T item : list) {
            for (Field field : fields) {
                try {
                    Object value = field.get(item);
                    if (field.getName().equalsIgnoreCase("price")) {
                        System.out.printf("Rs.%-14.2f", Double.parseDouble(value.toString()));
                    } else {
                        System.out.printf("%-17s", value);
                    }
                } catch (Exception e) {
                    System.out.printf("%-17s", "N/A");
                }
            }
            System.out.println();
        }

        System.out.println(lineEnd);
    }

}
