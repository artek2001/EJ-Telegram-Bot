package com.artek.HtmlParser;

import com.artek.BotCommands.BotCommand;
import com.artek.BotCommands.CommandRegistry;
import com.artek.BotCommands.StartCommand;
import com.artek.Config;
import com.artek.Database.DBManager;
import com.artek.ICommand;
import com.artek.ICommandRegister;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class EjBot extends TelegramLongPollingBot implements ICommandRegister{

    public ArrayList<ICommand> commands = new ArrayList<>();
    public static final String LOGTAG = "EJBOT";
    private final CommandRegistry defaultCommandRegistry;

    public EjBot() {
        this.defaultCommandRegistry = new CommandRegistry(false, getBotUsername());

        registerCommand(new StartCommand());

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

    public String parseMessage(Integer userId) throws IOException {
        String resultMessage = null;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://ej.grsmu.by/prosm_ocenki_stud.php");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //nvps.add(new BasicNameValuePair("login", login));
        //nvps.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();

        String htmlString = EntityUtils.toString(entity, "UTF-8");
        if (htmlString.length() > 400) {
            return "END";
        }
        //System.out.println(htmlString);
//        UserAgent userAgent = new UserAgent();
//        userAgent.openContent(htmlString);
//
//
//        Table table = userAgent.doc.getTable("<table>");
//
//        Elements cellsDep = table.getCol(1);
//        Map<String, Integer> map = new HashMap<String, Integer>();
//
//        int entry = 1;
//        for (Element cell: cellsDep) {
//            map.put(cell.getTextContent(), entry);
//            entry+=1;
//        }


//        Elements depRow = table.getRow(Integer.parseInt(messageToParse));
//        List<String> marksByDep = new ArrayList<String>();
//        Elements marksElements = depRow.getChildElements().get(2).findFirst("<table>").getChildElements().get(1).findEvery("<td>");
//        for(Element mark: marksElements) {
//            marksByDep.add(mark.getTextContent());
//        }


//       //String depName = table.getRow(Integer.parseInt(messageToParse)).findFirst("<td>").getTextContent();
//        String subjectName = table.getCell(1, Integer.parseInt(messageToParse)).getTextContent();
//        System.out.println(subjectName);
//        if (marksByDep.size() == 0) {
//            return subjectName + ":" + '\n' +"У вас нет оценок по данному предмету";
//        }
//        marksByDep.removeAll(Collections.singleton(""));
//        StringBuilder sb = new StringBuilder();
//        for (String item: marksByDep) {
//            sb.append(" " + item);
//        }
//        sb.substring(0, sb.length() - 1);
//
//        resultMessage = (subjectName + ":" + '\n' + sb.toString());
//
//        return resultMessage;
        return "END";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!defaultCommandRegistry.executeCommand(this, message)) {

                }

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
}
