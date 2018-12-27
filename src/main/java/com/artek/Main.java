package com.artek;

import com.artek.MainPack.EjBot;
import com.artek.Models.User;
import com.artek.SessionFactory.SessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Main {

    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) throws InterruptedException {


        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());

        try {
            ApiContextInitializer.init();
            TelegramBotsApi api = new TelegramBotsApi();

            try {
                EjBot ejBot = new EjBot();
                EjBot.setInstance(ejBot);

                api.registerBot(ejBot);
            } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
            }
        }
        catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
