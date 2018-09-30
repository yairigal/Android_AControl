package yairigal.com.homeac;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static yairigal.com.homeac.Globals.IP;
import static yairigal.com.homeac.Globals.PORT;


/**
 * Created by Yair Yigal on 2018-09-26.
 */

public class WebRequests {


    private static final String URL = IP+":"+PORT+"/";

    private static String POST(String url, String postParams) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        // new connection trying to login and get a cookie
        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("Host", "accounts.google.com");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept", "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,**/*//*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
        conn.setDoOutput(true);
        conn.setDoInput(true);

        System.out.println("Sending POST data: "+postParams);
        // Send post request
        if (postParams.length() > 0) {
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
        }

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        Charset set = Charset.forName("UTF-8");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), set));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            //response.append("\n");
        }
        in.close();
        System.out.println("POST response: "+response.toString());
        return response.toString();

    }

    /**
     * HTTP GET
     *
     * @param url
     * @return
     * @throws Exception
     */
    private static String GET(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
/*
        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
*/


        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


/*        // Get the response cookies
        Map<String, List<String>> coo1 = conn.getHeaderFields();
        List<String> coo = coo1.get("Set-Cookie");
        if (coo != null)
            setCookies(coo);*/

        return response.toString();

    }

    private static ACInstance parseData(String data){
        ACInstance instance  = new ACInstance();
        String[] lines = data.split(",");
        String[] firstLine = lines[0].split(" ");
        instance.on = Objects.equals(firstLine[0], "1");
        instance.fanStrength = Integer.parseInt(firstLine[1]);
        instance.temp = Integer.parseInt(firstLine[2]);
        instance.acMode = Integer.parseInt(firstLine[3]);
        instance.acSwing = Integer.parseInt(firstLine[4]);
        instance.actions = new SortedActions();
        for(int i=1;i<lines.length;i++){
            String[] currentLine = lines[i].split(" ");
            FutureAction current = new FutureAction();
            current.on = Objects.equals(currentLine[0], "true");
            current.time = new DateTimeContainer();
            current.time.selectedYear = Integer.parseInt(currentLine[1]);
            current.time.selectedMonth = Integer.parseInt(currentLine[2]);
            current.time.selectedDay = Integer.parseInt(currentLine[3]);
            current.time.selectedHour = Integer.parseInt(currentLine[4]);
            current.time.selectedMin = Integer.parseInt(currentLine[5]);
            instance.actions.add(current);
        }
        return instance;
    }

    public static ACInstance getData() throws Exception {
        String data =  GET(URL+"get");
        return parseData(data);
    }

    public static ACInstance sendData(ACInstance data) throws Exception {
        String newData = POST(URL+"set",data.toString());
        return parseData(newData);
    }

    public static ACInstance updateActions(SortedActions actions) throws Exception {
        ArrayList<String> lst = new ArrayList<>();
        for(int i=0;i<actions.size();i++){
            String dataStr =  "";
            dataStr += (actions.get(i).on);
            dataStr +=(" ");
            dataStr+=(actions.get(i).time.selectedYear);
            dataStr+=(" ");
            dataStr+=(actions.get(i).time.selectedMonth);
            dataStr+=(" ");
            dataStr+=(actions.get(i).time.selectedDay);
            dataStr+=(" ");
            dataStr+=(actions.get(i).time.selectedHour);
            dataStr+=(" ");
            dataStr+=(actions.get(i).time.selectedMin);
            lst.add(dataStr);
        }
        String newData = POST(URL+"update", TextUtils.join(",",lst));
        return parseData(newData);
    }
}
