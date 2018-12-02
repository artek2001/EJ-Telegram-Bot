package com.artek.HtmlParser;

import com.artek.Database.DBManager;
import com.artek.IParser;
import com.jaunt.*;
import com.jaunt.component.Table;
import com.mysql.cj.xdevapi.Collection;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

public class Parser implements IParser {
    @Override
    public Map<String, ArrayList<String>> allDepsMarks(Integer userId) throws IOException, ResponseException, NotFound {
        String resultMessage = null;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://ej.grsmu.by/prosm_ocenki_stud.php");

        //user credentials[login, password]
        String[] credentials = DBManager.getInstance().getUserCredentialsById(userId);
        String login = credentials[0];
        String password = credentials[1];

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login", login));
        nvps.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();

        String htmlString = EntityUtils.toString(entity, "UTF-8");

        UserAgent userAgent = new UserAgent();
        userAgent.openContent(htmlString);


        Table table = userAgent.doc.getTable("<table>");

        Elements cellsDep = table.getCol(1);

        Map<String, ArrayList<String>> allMarks = new HashMap<>();

        //Making Map of all deps and their marks
        int rawNumber = 1;
        for (Element dep: cellsDep) {
            ArrayList<String> marks = new ArrayList<>();

            Elements depRow = table.getRow(rawNumber);
            String depName = depRow.getChildElements().get(1).getTextContent();
            Elements marksElements = depRow.getChildElements().get(2).findFirst("<table>").getChildElements().get(1).findEvery("<td>");

            for (Element depMark: marksElements) {
                marks.add(depMark.getTextContent());
            }
            marks.removeAll(Collections.singleton(""));
            if (marks.size() == 0) {
                marks.add("No marks for this subject");
                if (rawNumber < cellsDep.size() - 1) {
                    rawNumber++;
                }
                continue;
            }
            allMarks.put(depName, marks);

            if (rawNumber < cellsDep.size() - 1) {
                rawNumber++;
            }
        }

        return allMarks;
    }


//    public static void main(String[] args) throws IOException {

//        BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
//        String login = stream.readLine();
//
//        EjBot bot = new EjBot(login, password);
//
//        for (int k = 0; k < 5; k++) {
//
//            for (int i = 0; i <= 99; i++) {
//
//                    String pass = bot.password + k + Integer.toString(i);
//                    if (i < 10 && !isFound) {
//                        String pass2 = bot.password + k + "0" + Integer.toString(i);
//                        System.out.println("TRYING FOR " + pass2);
//                        bot.parseMessage(login, pass2);
//                        continue;
//                    }
//                    if (!isFound) {
//                        System.out.println("TRYING FOR " + pass);
//                        bot.parseMessage(login, pass);
//                    }
//
//            }
//
//        }
//
//        System.out.println("=====================================================");
//        System.out.println("RIGHT PASSWORD IS " + trueFor);
//
//    }
}
