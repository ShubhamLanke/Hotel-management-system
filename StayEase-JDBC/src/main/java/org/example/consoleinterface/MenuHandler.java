package org.example.consoleinterface;

import org.example.entity.User;

import java.util.Scanner;

public class MenuHandler {
    private final Scanner scanner;

    public MenuHandler(Scanner scanner) {
        this.scanner = scanner;
    }

//    public void displayMenu(User user, String title, String[] options) {
//        System.out.println("\n=========================");
//        System.out.println("\n      " + title + "      ");
//        System.out.println("\n=========================");
//        System.out.println("\nWelcome, " + user.getName());
//        System.out.println("\nRole: " + user.getUserRole());
//        System.out.println("\n=========================");
//        for (int i = 0; i < options.length; i++) {
//            System.out.println((i + 1) + ". " + options[i]);
//        }
//        System.out.println("---------------------------");
//        System.out.print("Enter your choice: ");
//    }
//
//    public int getUserChoice() {
//        while (!scanner.hasNextInt()) {
//            System.out.println("Invalid input! Please enter a number from range.");
//            scanner.next();
//        }
//        return scanner.nextInt();
//    }

    public void displayMenu(User user, String title, String[] options) {
        int width = 30;

        System.out.println("\n" + "=".repeat(width));
        System.out.println(centerText(title, width));
        System.out.println("=".repeat(width));

        System.out.println("\nWelcome, " + user.getName());
        System.out.println("Role: " + user.getUserRole());

        System.out.println("\n" + "=".repeat(width));
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("-".repeat(width));

        System.out.print("Enter your choice: ");
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(padding, 0)) + text;
    }
}

