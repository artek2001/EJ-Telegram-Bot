package com.artek.BotCommands;

import com.artek.Dao.ManagerDAO;
import com.artek.MainPack.Parser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.util.Arrays;

public class HackCommand extends BotCommand{

    private final String LOGTAG = "HACK_COMMAND";

    public HackCommand() {
        super("/hack", "Hack password of a user");
    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {
        String password = null;
        SendMessage message = new SendMessage();
        message.setChatId(chatFrom.getId().toString());
        String login = args[0];
        try {
            password = Parser.loop(login);
        } catch (IOException e) {
            BotLogger.error(LOGTAG, "Error while hacking password");
        }

        message.setText(password);

//        message.setText(Arrays.toString(ManagerDAO.getInstance().getUserCredentials(userFrom.getId())));
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, "Error in executing HACK COMMAND");
        }
    }
}
