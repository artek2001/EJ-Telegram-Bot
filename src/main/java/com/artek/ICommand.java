package com.artek;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface ICommand {
    String getCommandIdentifier();

    String getDescription();

    void processMessage(AbsSender var1, Message var2, String[] var3);
}
