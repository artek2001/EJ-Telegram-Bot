package com.artek.BotCommands;

import com.artek.Dao.ManagerDAO;
import com.artek.MainPack.EjBot;
import com.artek.MainPack.Parser;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;

public class StartCommand extends BotCommand{

    private static  final String LOGTAG = "START_COMMAND";


    public StartCommand() {
        super("/start", "Command to start using bot");

    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {



        ManagerDAO managerDAO = ManagerDAO.getInstance();
        StringBuilder messageResponse = new StringBuilder();
        SendMessage message = new SendMessage();
        message.setChatId(chatFrom.getId().toString());

        if (args == null || args.length < 2) {
            messageResponse.append("To start the bot, type:" + "\n" + "/start login password");
            message.setText(messageResponse.toString());
        }

        else {
            if (managerDAO.isUserActive(userFrom.getId())) {
                messageResponse.append("You are already registered");
            }
            else {
                try {
                    if (EjBot.checkConnection(args[0], args[1])) {
                        messageResponse.append("Successfully logged in");
                        managerDAO.addUser(userFrom.getId(),args[0], args[1], true, 1);
                        message.setReplyMarkup(EjBot.getMainMenuKeyboard());

                        //TODO temporary stuff(remove in future)
                        new Parser().allDepsMarks(userFrom.getId());
                    }
                    else {
                        messageResponse.append("Wrong login or password");
                    }

                } catch (IOException | ResponseException | NotFound e) {
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
