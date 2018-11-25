package com.artek;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.function.BiConsumer;

public interface ICommandRegister {
    void registerDefaultAction(BiConsumer<AbsSender, Message> var1);

    boolean registerCommand(ICommand command);

}
