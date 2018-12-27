package com.artek.BotCommands;

import com.artek.Dao.ManagerDAO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LogoutCommand extends BotCommand {

    private final String LOGTAG = "LOGOUT_COMMAND";
    public LogoutCommand() {
        super("/logout", "Log out from bot");
    }

    @Override
    public void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args) {
        SendMessage sendMessage = logout(userFrom, chatFrom);

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static SendMessage logout(User userFrom, Chat chatFrom) {
        ManagerDAO managerDAO = ManagerDAO.getInstance();

        StringBuilder messageResponse = new StringBuilder();

        if (managerDAO.isUserActive(userFrom.getId())) {
            managerDAO.setIsActiveForUser(0, userFrom.getId());
            managerDAO.setUserState(userFrom.getId(), 0);
            messageResponse.append("You are successfully logged out");
        }

        else {
            messageResponse.append("You are not using bot to use this command" + "\n" + "Use /start to start using the bot");
        }

        SendMessage message = new SendMessage().setChatId(chatFrom.getId().toString()).setText(messageResponse.toString());
        return message;
    }
}
