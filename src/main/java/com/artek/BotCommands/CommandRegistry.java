package com.artek.BotCommands;

import com.artek.ICommand;
import com.artek.ICommandRegister;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommandRegistry implements ICommandRegister {

    private final Map<String, ICommand> commandRegistryMap = new HashMap<>();
    private final boolean allowCommandsWithUsername;
    private final String botUsername;
    private BiConsumer<AbsSender, Message> defaultConsumer;


    public CommandRegistry(boolean allowCommandsWithUsername, String botUsername) {
        this.allowCommandsWithUsername = allowCommandsWithUsername;
        this.botUsername = botUsername;
    }

    @Override
    public void registerDefaultAction(BiConsumer<AbsSender, Message> consumer) {
        this.defaultConsumer = consumer;
    }

    @Override
    public boolean registerCommand(ICommand command) {
        if (commandRegistryMap.containsKey(command.getCommandIdentifier())) {
            return false;
        }

        else {
            commandRegistryMap.put(command.getCommandIdentifier(), command);
            return true;
        }
    }

    @Override
    public Collection<ICommand> getRegisteredCommands() {
        return commandRegistryMap.values();
    }

    @Override
    public ICommand getRegisteredCommand(String commandIdentifier) {
        return commandRegistryMap.get(commandIdentifier);
    }

    public final boolean executeCommand(AbsSender sender, Message message) {
        if (message.hasText()) {
            String text = message.getText();
            if (text.startsWith(BotCommand.COMMAND_INIT_CHARACTER)) {
                String commandMessage = text.substring(1);
                String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);
                String command = removeUserIfNeeded(commandSplit[0]);

                if (commandRegistryMap.containsKey(command)) {
                    String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
                    commandRegistryMap.get(command).processMessage(sender, message, parameters);
                    return true;
                }
                else if(sender != null) {
                    defaultConsumer.accept(sender, message);
                }
            }
        }

        return false;
    }

    private String removeUserIfNeeded(String command) {
        if (allowCommandsWithUsername) {
            return command.replace("@" + botUsername, "").trim();
        }

        return command;
    }

    //TODO registerAll method and other methods


}
