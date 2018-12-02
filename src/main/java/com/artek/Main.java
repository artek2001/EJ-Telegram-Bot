package com.artek;

import com.artek.HtmlParser.EjBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Main {

    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {

        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());

        try {
            ApiContextInitializer.init();
            TelegramBotsApi api = new TelegramBotsApi();

            try {
                api.registerBot(new EjBot());
            } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
            }
        }
        catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
