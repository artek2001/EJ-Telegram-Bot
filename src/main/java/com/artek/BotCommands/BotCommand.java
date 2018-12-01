package com.artek.BotCommands;

import com.artek.ICommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.IOException;

public abstract class BotCommand implements ICommand {

    public static final String COMMAND_INIT_CHARACTER = "/";
    public static final String COMMAND_PARAMETER_SEPARATOR= "\\s+";
    private static final int MAX_COMMAND_LENGTH = 32;
    private final String COMMAND_IDENTIFIER;
    private final String COMMAND_DESCRIPTION;

    public BotCommand(String command_identifier, String command_description) {
        if (command_description != null && !command_identifier.isEmpty()) {
            if (command_identifier.startsWith(COMMAND_INIT_CHARACTER)) {
                command_identifier = command_identifier.substring(1);
            }

            if (command_identifier.length() + 1 > MAX_COMMAND_LENGTH) {
                throw new IllegalArgumentException(String.format("Command is too long to be registered(max size is %s including /)", MAX_COMMAND_LENGTH));
            }

            else {
                this.COMMAND_IDENTIFIER = command_identifier.toLowerCase();
                this.COMMAND_DESCRIPTION = command_description;
            }
        }

        else {
            throw new IllegalArgumentException("Command Identifier cannot be null or empty");
        }
    }


    @Override
    public final String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public final String getDescription() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public void processMessage(AbsSender var1, Message var2, String[] var3) {
        this.execute(var1, var2.getFrom(), var2.getChat(), var3);
    }

    public abstract void execute(AbsSender sender, User userFrom, Chat chatFrom, String[] args);
}
