package com.artek.BotCommands;

import com.artek.Database.DBManager;
import com.artek.HtmlParser.Parser;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MarksCommand extends BotCommand {

    private final String LOGTAG = "MARKS_COMMAND";

    public MarksCommand() {
        super("/allmarks", "Get all marks");
    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {
        StringBuilder messageReposonse = new StringBuilder();
//        DBManager dbManager = DBManager.getInstance();
        DBManager dbManager = DBManager.getInstance();

        if (dbManager.getUserStateForBot(userFrom.getId())) {
            Map<String, ArrayList<String>> allMarks = null;
            try {
                allMarks = new Parser().allDepsMarks(userFrom.getId());
                for (Map.Entry<String, ArrayList<String>> depEntry : allMarks.entrySet()) {
                    messageReposonse.append("***" + depEntry.getKey() + ": ***" + "\n");
                    messageReposonse.append(String.join(",", depEntry.getValue()));
                    messageReposonse.append("\n");

                }
            } catch (IOException | ResponseException | NotFound e) {
                BotLogger.error(LOGTAG, "Exception in MarksCommand class");
            }



        } else {
            messageReposonse.append("You are not using bot to use this command" + "\n" + "Use /start to start using the bot");

        }

        SendMessage message = new SendMessage();
        message.setChatId(chatFrom.getId().toString());
        message.setText(messageReposonse.toString());
        message.enableMarkdown(true);
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, "Exception in MarksCommand. Message wasn't sent");
        }


    }
}
