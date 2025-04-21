package org.example.consoleinterface;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {
    private final Scanner scanner;

    public MenuHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu(String title, String[] options) {
        // Finding the max length among title and menu options
        int maxOptionLength = title.length();
        for (int i = 0; i < options.length; i++) {
            String optionText = (i + 1) + ". " + options[i];
            if (optionText.length() > maxOptionLength) {
                maxOptionLength = optionText.length();
            }
        }

        // Added padding 4 spaces to left and right.
        int width = maxOptionLength + 8;

        // Build border
        String border = "=".repeat(width);

        // Center the title
        int leftPadding = (width - title.length()) / 2;
        int rightPadding = width - title.length() - leftPadding;
        String paddedTitle = " ".repeat(leftPadding) + title + " ".repeat(rightPadding);

        // Display menu
        System.out.println(border);
        System.out.println(paddedTitle);
        System.out.println(border);

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        System.out.println("-".repeat(width));
        System.out.print("Enter your choice: ");
    }

    public int getUserChoice() {
//        while (!scanner.hasNextInt()) {
//            System.out.println("Invalid input! Please enter a number from range.");
//            scanner.next();
//        }
//        return scanner.nextInt();
        while(true){
            try{
                int x = scanner.nextInt();
                scanner.nextLine();
                return x;
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
                scanner.nextLine();
            }
        }
    }

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

