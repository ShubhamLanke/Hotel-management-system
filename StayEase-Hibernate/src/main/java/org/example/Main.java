package org.example;

import lombok.extern.log4j.Log4j2;

import org.example.consoleinterface.HomeMenuUI;
import org.example.dependency.DependencyInjector;

@Log4j2
public class Main {
    public static void main(String[] args) {

        HomeMenuUI homeMenuUI = DependencyInjector.initHomeMenuUI();
        homeMenuUI.displayMainMenu();
    }
}