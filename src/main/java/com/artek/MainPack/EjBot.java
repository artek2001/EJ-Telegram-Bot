package com.artek.MainPack;

import com.artek.BotCommands.*;
import com.artek.Database.Config;
import com.artek.Dao.ManagerDAO;
import com.artek.BotCommands.ICommand;
import com.artek.BotCommands.ICommandRegister;
import com.artek.SessionFactory.SessionFactoryUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;

public class EjBot extends TelegramLongPollingBot implements ICommandRegister {

    public ArrayList<ICommand> commands = new ArrayList<>();
    public static final String LOGTAG = "EJBOT";
    private final CommandRegistry defaultCommandRegistry;

    //states
    private static volatile EjBot instance;
    private static final int START_STATE = 0;
    private static final int MAIN_MENU = 1;
    private static final int ALL_SUBJECTS = 2;
    private static final int ALL_MARKS = 3;
    private static final int TIMETABLE = 4;

    public static EjBot getInstance() {
        return instance;
    }

    public static void setInstance(EjBot ejBot) {
        EjBot.instance = ejBot;
    }

    public EjBot() {
        new Parser();

        this.defaultCommandRegistry = new CommandRegistry(false, getBotUsername());

        registerCommand(new HackCommand());
        registerCommand(new StartCommand());
        registerCommand(new MarksCommand());
        registerCommand(new LogoutCommand());
        SessionFactoryUtil.build();

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


        try {
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

                } else {
                    handleIncomingMessage(message);
                }
            } else if (update.hasCallbackQuery()) {
                handleCallBackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
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

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        final int state = ManagerDAO.getInstance().getUserState(message.getFrom().getId());
        //TODO final String language
        if (message.isCommand()) {
            defaultCommandRegistry.executeCommand(this, message);
        }

        else if (state == START_STATE) {
            return;
        }

        else {
            SendMessage sendMessageRequest = new SendMessage();
            switch (state) {
                case START_STATE:
                    break;

                case MAIN_MENU:
                    sendMessageRequest = messageOnMainMenu(message);
                    break;

                case ALL_SUBJECTS:
                    //sendMessageRequest = messageOnAllSubjects();
                    break;

                case TIMETABLE:
                    //sendMessageRequest = messageOnTimetable();
                    break;

            }
            execute(sendMessageRequest);
        }
    }

    private static SendMessage messageOnMainMenu(Message message) {
        SendMessage sendMessageRequest = new SendMessage();
        if (message.hasText()) {
            if (message.getText().equals(getAllSubjectsCommand())) {
                sendMessageRequest = onAllSubjectsChoosen(message);
            }
            else if (message.getText().equals(getAllMarksCommand())) {
                sendMessageRequest = onAllMarksChoosen(message);
            }
            else if (message.getText().equals(getTimetableCommand())) {
                //sendMessageRequest = onTimetableChoosen();
            }
            else if (message.getText().equals(getLogoutCommand())) {
                sendMessageRequest = onLogoutChoosen(message);
            }
            else {
                sendMessageRequest = sendChooseOption(message, getMainMenuKeyboard());
            }
        } else {
            sendMessageRequest = sendChooseOption(message, getMainMenuKeyboard());
        }
        return sendMessageRequest;
    }

    private static String getAllSubjectsCommand() {
        return "All subjects";
    }

    private static String getAllMarksCommand() {
        return "All marks";
    }

    private static String getTimetableCommand() {
        return "Timetable";
    }

    private static String getLogoutCommand() {
        return "Logout";
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(getAllSubjectsCommand());
        firstRow.add(getAllMarksCommand());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(getTimetableCommand());
        secondRow.add(getLogoutCommand());

        rows.add(firstRow);
        rows.add(secondRow);

        replyKeyboardMarkup.setKeyboard(rows);

        return replyKeyboardMarkup;
    }

    private static SendMessage sendChooseOption(Message messageFromUser, ReplyKeyboard replyKeyboard) {
        SendMessage sendChooseOptionMessage = new SendMessage();
        sendChooseOptionMessage.enableMarkdown(true);
        sendChooseOptionMessage.setChatId(messageFromUser.getChatId().toString());
        sendChooseOptionMessage.setReplyToMessageId(messageFromUser.getMessageId());
        sendChooseOptionMessage.setText("Choose an option from menu");
        sendChooseOptionMessage.setReplyMarkup(replyKeyboard);

        return sendChooseOptionMessage;
    }

    private static SendMessage onAllMarksChoosen(Message message) {
        SendMessage messageRespone = new SendMessage();
        messageRespone.setChatId(message.getChatId().toString());
        messageRespone.setText(MarksCommand.makeAllMarksRespond(message.getFrom()).toString());
        messageRespone.enableMarkdown(true);

        return messageRespone;
    }

    private static SendMessage onAllSubjectsChoosen(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setText("Choose your subject:");

        InlineKeyboardMarkup inlineKeyboardMarkup = getAllSubjectsKeyboard(message.getFrom().getId());

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());

        //ManagerDAO.getInstance().setUserState(message.getFrom().getId(), ALL_SUBJECTS);

        return sendMessage;
    }

    private static InlineKeyboardMarkup getAllSubjectsKeyboard(Integer userId) {

        ArrayList<String> allSubjects = ManagerDAO.getInstance().getAllSubjects(userId);
        int subjectsCount = allSubjects.size();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String subj : allSubjects) {

            List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();
            //TODO CallbackQuery
            keyboardButtonList.add(new InlineKeyboardButton().setText(subj).setCallbackData(String.valueOf(allSubjects.indexOf(subj))));
            rows.add(keyboardButtonList);
        }

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }


    //TODO
    private static EditMessageReplyMarkup getAllSubjectsByPage(Integer userId, int page) {
        ArrayList<String> allSubjectsList = ManagerDAO.getInstance().getAllSubjects(userId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String subj : allSubjectsList) {
            List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();
            keyboardButtonList.add(new InlineKeyboardButton().setText(subj).setCallbackData(String.valueOf(allSubjectsList.indexOf(subj))));
            rows.add(keyboardButtonList);
        }

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        return editMessageReplyMarkup;
    }

    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        try {
            if (callbackQuery.getData().equals("back_to_allsubjects")) {
                EditMessageText editMessageText = editMessageWithInlineKeyboard(callbackQuery, "All subjects", null, getAllSubjectsKeyboard(callbackQuery.getFrom().getId()));
                execute(editMessageText);

            } else if (callbackQuery.getData().equals("BACK")) {

            } else {
                handleOneSingleSubject(callbackQuery);
            }
        }

        catch (TelegramApiException e) {

        }

    }

    private void handleOneSingleSubject(CallbackQuery query) {
        try {
            String json = ManagerDAO.getInstance().getRecentMarks(query.getFrom().getId());
            Map<String, ArrayList<String>> map = new ObjectMapper().readValue(json, new TypeReference<Map<String, ArrayList<String>>>(){});

            Collection<String> keys = map.keySet();
            String[] keysArray = keys.toArray(new String[keys.size()]);

            String chosenSubject = keysArray[Integer.valueOf(query.getData())];
            ArrayList<String> chosenSubjectList = map.get(chosenSubject);


            EditMessageText editMessageText = editMessageWithInlineKeyboard(query, chosenSubject + ":", map.get(chosenSubject), getOneInlineButton(query));
            execute(editMessageText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static InlineKeyboardMarkup getOneInlineButton(CallbackQuery query) {

        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(query.getMessage().getChatId().toString());
        editMessageText.setMessageId(query.getMessage().getMessageId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> line = new ArrayList<>();
        line.add(new InlineKeyboardButton().setText("Back to all").setCallbackData("back_to_allsubjects"));
        markup.getKeyboard().add(line);

        return markup;
    }

    private static EditMessageText editMessageWithInlineKeyboard(CallbackQuery query, String newText, ArrayList<String> marks, InlineKeyboardMarkup newMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(query.getMessage().getChatId().toString());
        editMessageText.setMessageId(query.getMessage().getMessageId());
        editMessageText.setReplyMarkup(newMarkup);

        editMessageText = (marks == null) ? editMessageText.setText(newText) : editMessageText.setText(newText + "\n\n" + String.join(",", marks) + "\n\n" + "Average mark: ");

        return editMessageText;
    }

    private static SendMessage onLogoutChoosen(Message message) {

        SendMessage sendMessage = null;
        try {
            //instance.defaultCommandRegistry.getRegisteredCommand("logout").processMessage(instance, message, null);
            sendMessage = LogoutCommand.logout(message.getFrom(), message.getChat());

            sendMessage.setText("Successfully logged out");
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sendMessage;
    }


}
