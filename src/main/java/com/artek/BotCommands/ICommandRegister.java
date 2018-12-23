package com.artek.BotCommands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;
import java.util.function.BiConsumer;

public interface ICommandRegister {
    void registerDefaultAction(BiConsumer<AbsSender, Message> consumer);

    boolean registerCommand(ICommand command);

    Collection<ICommand> getRegisteredCommands();

    ICommand getRegisteredCommand(String commandIdentifier);

}
