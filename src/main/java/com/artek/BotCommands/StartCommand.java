package com.artek.BotCommands;

import com.artek.Database.DBManager;
import com.artek.HtmlParser.EjBot;
import com.artek.HtmlParser.Parser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.sql.SQLException;

public class StartCommand extends BotCommand{

    private static final String LOGTAG = "START_COMMAND";


    public StartCommand() {
        super("/start", "Command to start using bot");
    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {
        DBManager dbManager = DBManager.getInstance();
        StringBuilder messageResponse = new StringBuilder();
        SendMessage message = new SendMessage();
        message.setChatId(chatFrom.getId().toString());


        if (args == null || args.length < 2) {
            messageResponse.append("To start the bot, type:" + "\n" + "/start login password");
            message.setText(messageResponse.toString());
        }

        else {
            if (dbManager.getUserStateForBot(userFrom.getId())) {
                messageResponse.append("You are already registered");
            }
            else {
                try {
                    if (EjBot.checkConnection(args[0], args[1])) {
                        messageResponse.append("Successfully logged in");
                        dbManager.setUserStateForBot(userFrom.getId(),args[0], args[1], true);
                    }
                    else {
                        messageResponse.append("Wrong login or password");
                    }

                } catch (IOException e) {
                    BotLogger.error(LOGTAG, "ERROR is executeStartCommand method");
                }
            }

            message.setText(messageResponse.toString());


        }
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, "Message was not sent");
        }
    }
}
