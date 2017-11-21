package ru.happyfat.integrity;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum ConsoleIO {
    I;

    private static String flush = StringUtils.repeat("\b", 100);

    public void printHeader() {
        System.out.println("/**");
        System.out.println("*");
        System.out.println("* IntegrityScanner v0.1");
        System.out.println("*");
        System.out.println("*/");
        System.out.println();
    }

    public void print(String string) {
        System.out.print(flush);
        System.out.print(flush + string);
    }

    public void printok(String string) {
        print(string);
        System.out.println(StringUtils.leftPad("OK", 32 - string.length(), '.'));
    }

    public String input(String caption) {
        try {
            System.out.print(caption);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void pressEnterToContinue() {
        try {
            System.out.println("Press Enter key to exit...");
            System.in.read();
        } catch (IOException ignored) {
        }
    }
}
