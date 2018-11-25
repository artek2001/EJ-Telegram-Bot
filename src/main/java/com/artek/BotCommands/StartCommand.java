package com.artek.BotCommands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class StartCommand extends BotCommand{

    private static final String LOGTAG = "START_COMMAND";


    public StartCommand() {
        super("/start", "Command to start using bot");
    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {
        //TODO check if user is already registered using DBManager

        String userName = chatFrom.getUserName();

        if (userName == null || userName.isEmpty()) {
            userName = userFrom.getFirstName() + " " + userFrom.getLastName();
        }

        StringBuilder messageRespone = new StringBuilder("HELLO ").append(userName);

        if (args != null && args.length > 0) {
            messageRespone.append("\n");
            messageRespone.append("Thank you for your kind words: \n");
            messageRespone.append(String.join(" ", args));
        }

        SendMessage sendMessage = new SendMessage(chatFrom.getId(), messageRespone.toString());

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
