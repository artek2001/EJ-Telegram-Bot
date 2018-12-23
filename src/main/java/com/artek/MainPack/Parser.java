package com.artek.MainPack;

import com.artek.Dao.ManagerDAO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jaunt.*;
import com.jaunt.component.Table;
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

    private static CloseableHttpClient client;

    public Parser() {
        client = HttpClients.createDefault();
    }

    @Override
    public Map<String, ArrayList<String>> allDepsMarks(Integer userId) throws IOException, ResponseException, NotFound {

        ManagerDAO managerDAO = ManagerDAO.getInstance();
        String[] credentials = managerDAO.getUserCredentials(userId);

        Table table = getTableFromJournal(credentials);

        //array to put all departments in to INSERT in db and then fetch them in getAllSubjectsKeyboard
        ArrayList<String> allDepartments = new ArrayList<>();

        Map<String, ArrayList<String>> allMarks = new HashMap<>();
        Elements cellsDep = table.getCol(1);


        //Making Map of all deps and their marks
        int rawNumber = 1;
        for (Element dep : cellsDep) {
            ArrayList<String> marks = new ArrayList<>();

            Elements depRow = table.getRow(rawNumber);
            String depName = depRow.getChildElements().get(1).getTextContent();
            Elements marksElements = depRow.getChildElements().get(2).findFirst("<table>").getChildElements().get(1).findEvery("<td>");

            for (Element depMark : marksElements) {
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

            allDepartments.add(depName);

            if (rawNumber < cellsDep.size() - 1) {
                rawNumber++;
            }
        }
        String json = serializeMarks(allMarks);

        managerDAO.setRecentMarks(credentials[0], json);
//        managerDAO.addAllSubjects(credentials[0], String.join(", ", allDepartments).replaceAll(", ", ","));

        return allMarks;
    }


    public static boolean getPasswordByLogin(String login, String password) throws IOException {

        HttpPost post = new HttpPost("http://ej.grsmu.by/prosm_ocenki_stud.php");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login", login));
        nvps.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = client.execute(post);

        HttpEntity entity = response.getEntity();
        String htmlString = EntityUtils.toString(entity, "UTF-8");
        if (htmlString.length() > 400) {
            return true;
        } else {
            return false;
        }
    }

    public static String loop(String login) throws IOException {
        String password = "18-00";
        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < 100; i++) {
                if (i < 10) {

                    password += k + "0" + i;
                    //BotLogger.info("LOOP", password);
                    if (getPasswordByLogin(login, password)) {
                        return "TRUE FOR" + password;
                    }
                    password = "18-00";
                } else {
                    //BotLogger.info("LOOP", password);
                    password += String.valueOf(k) + i;

                    if (getPasswordByLogin(login, password)) {
                        return "TRUE FOR " + password;
                    }
                    password = "18-00";
                }
            }
        }
        return "FAIL";
    }

    public static Table getTableFromJournal(String[] credentials) throws IOException, ResponseException, NotFound {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://ej.grsmu.by/prosm_ocenki_stud.php");


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

        return userAgent.doc.getTable("<table>");
    }

    private static String serializeMarks(Map<String, ArrayList<String>> allMarks) {
        String mapAsJson = null;
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            for (Map.Entry<String, ArrayList<String>> entry : allMarks.entrySet()) {
//                mapper.writeValueAsString()
//            }

            mapAsJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(allMarks);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return mapAsJson;
    }
}
