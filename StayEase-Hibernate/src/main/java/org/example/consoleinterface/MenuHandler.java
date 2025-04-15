package org.example.consoleinterface;

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
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number from range.");
            scanner.next();
        }
        return scanner.nextInt();
    }
}

