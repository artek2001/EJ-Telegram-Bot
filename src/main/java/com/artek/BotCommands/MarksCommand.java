package com.artek.BotCommands;

import com.artek.Dao.ManagerDAO;
import com.artek.MainPack.Parser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        long timeExecute = System.currentTimeMillis();
        StringBuilder messageReposonse = new StringBuilder();

        ManagerDAO managerDAO = ManagerDAO.getInstance();

        if (managerDAO.isUserActive(userFrom.getId())) {
            messageReposonse = makeAllMarksRespond(userFrom);
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
        BotLogger.info("TIME EXECUTING", Long.toString((System.currentTimeMillis() - timeExecute)));

    }

    public static StringBuilder makeAllMarksRespond(User userFrom) {
        StringBuilder messageReposonse = new StringBuilder();
        Map<String, ArrayList<String>> allMarks = null;
        try {
            String jsonAllMarks = ManagerDAO.getInstance().getRecentMarks(userFrom.getId());

            allMarks = new ObjectMapper().readValue(jsonAllMarks, new TypeReference<Map<String, ArrayList<String>>>() {});
            for (Map.Entry<String, ArrayList<String>> depEntry : allMarks.entrySet()) {
                messageReposonse.append("***" + depEntry.getKey() + ": ***" + "\n");
                messageReposonse.append(String.join(",", depEntry.getValue()));
                messageReposonse.append("\n");

            }
        } catch (IOException e) {
            BotLogger.error("ERROR", "Exception in MarksCommand class");
        }
        return messageReposonse;
    }
}
