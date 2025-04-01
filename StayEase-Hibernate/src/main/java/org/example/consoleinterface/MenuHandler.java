package org.example.consoleinterface;

import java.util.Scanner;

public class MenuHandler {
    private final Scanner scanner;

    public MenuHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu(String title, String[] options) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("---------------------------");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("---------------------------");
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

