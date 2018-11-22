package com.artek.HtmlParser;

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
import java.util.ArrayList;
import java.util.List;

public class EjBot {

    public String password;
    public String login;

    public EjBot(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String parseMessage(String login, String password) throws IOException {
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
        if (htmlString.length() > 400) {
            System.out.println("TRUE FOR " + password);
            Parser.trueFor = password;
            Parser.isFound = true;
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
}
