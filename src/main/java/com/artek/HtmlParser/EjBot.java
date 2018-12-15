package com.artek.HtmlParser;

import com.artek.BotCommands.*;
import com.artek.Config;
import com.artek.Database.DBManager;
import com.artek.ICommand;
import com.artek.ICommandRegister;
import com.artek.Models.User;
import com.artek.Models.UserDAO;
import com.artek.SessionFactory.SessionFactoryUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hibernate.SessionFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class EjBot extends TelegramLongPollingBot implements ICommandRegister{

    public ArrayList<ICommand> commands = new ArrayList<>();
    public static final String LOGTAG = "EJBOT";
    private final CommandRegistry defaultCommandRegistry;

    public EjBot() {
        new Parser();
        this.defaultCommandRegistry = new CommandRegistry(false, getBotUsername());

        SessionFactory sessionFactory = SessionFactoryUtil.build();

        registerCommand(new HackCommand());
        registerCommand(new StartCommand());
        registerCommand(new MarksCommand());
        registerCommand(new LogoutCommand());
    }

    public static boolean checkConnection(String login, String password) throws IOException {
        String resultMessage = null;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://ej.grsmu.by/prosm_ocenki_stud.php");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login", login));
        nvps.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();

        String htmlString = EntityUtils.toString(entity, "UTF-8");
        if (htmlString.length() > 200) {
            return true;
        }

        return false;
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!defaultCommandRegistry.executeCommand(this, message)) {

                }



//                SendMessage message1 = new SendMessage();
//                message1.setReplyToMessageId(message.getMessageId());
//                message1.setChatId(message.getChatId().toString());
//                message1.setText("Write your login");
//                message1.setReplyMarkup(getForceReply());
//                try {
//                    execute(message1);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }

            }
        }
    }

    private boolean filter(Message message) {
        return false;
    }

    @Override
    public String getBotUsername() {
        return Config.BOTUSERNAME;
    }

    @Override
    public String getBotToken() {
        return Config.TOKEN;
    }

    @Override
    public void registerDefaultAction(BiConsumer<AbsSender, Message> consumer) {

    }

    @Override
    public boolean registerCommand(ICommand command) {
        return defaultCommandRegistry.registerCommand(command);
    }

    @Override
    public Collection<ICommand> getRegisteredCommands() {
        return defaultCommandRegistry.getRegisteredCommands();
    }

    @Override
    public ICommand getRegisteredCommand(String commandIdentifier) {
        return defaultCommandRegistry.getRegisteredCommand(commandIdentifier);
    }

    private static ForceReplyKeyboard getForceReply() {
        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
        forceReplyKeyboard.setSelective(true);
        return forceReplyKeyboard;
    }
}
