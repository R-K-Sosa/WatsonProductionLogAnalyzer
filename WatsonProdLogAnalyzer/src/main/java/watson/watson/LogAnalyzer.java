package watson.watson;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.logging.HttpLoggingInterceptor.Level;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
public class LogAnalyzer {
    
    public static void main(String args[]) throws Exception {
        
    	Logger logger = Logger.getLogger(LogAnalyzer.class.getName());
        LogAnalyzer logA = new LogAnalyzer();
        
        try {
            logA.test();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        //connect both LogAnalyzer and ReadNSearchFile classes 
        
    }
    public void test() throws Exception
    {
        String JTID = "DB5JSKDLZ";
        String rohanaID = "DB5JSKECR";
        
        
        //authentication token for bot
        String botToken = "xoxb-377960480037-380083483923-4BFuqRwyFhISCBPHLwcreQEH";
        //the string to fetch IM history with WatsonChatBot
        String imHistory = "https://slack.com/api/im.history?token=" + botToken + "&channel=" + rohanaID +"&count=6&pretty=1";
        
        URL imHistoryURL = new URL(imHistory);
        URLConnection yc = imHistoryURL.openConnection();
        
        //buffered reader to get most recent messages and extract the url for the most recent file uploaded
        BufferedReader in1 = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String imResponse = "";
        String inputLine;
        while ((inputLine = in1.readLine()) != null) 
            imResponse = imResponse + inputLine;
        in1.close();
        
        
        int a = 15 + (imResponse.indexOf("url_private"));
        int b = imResponse.indexOf('"', a);
        System.out.println(imResponse.substring(a, b));
        
        String url = imResponse.substring(a, b);
        url = url.replace("\\", "");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        // optional default is GET
        con.setRequestMethod("GET");
        
        //add request header
        con.setRequestProperty("Authorization", "Bearer " + botToken);
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        if(responseCode == 200) {
            System.out.println("Website Reached");
        }
        
        //buffered reader to open log file from private url 
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String logLine;
        StringBuffer response = new StringBuffer();
        
        //creates the stringBuffer by appending each line and adding line breaks
        while ((logLine = in.readLine()) != null) {
            response.append(logLine);
        }
        
        
        in.close();
        
        
        LinkedList<String> dateAndTimes = new LinkedList<String>();
        
        //random string that replaces all dates after they have been extracted thus making it easier to split
        String randomString = "asdfghjkl";
        
        //changes the response from stringBuffer to String type
        String responseString = response.toString();
        
        //puts quotes around commas in the log file
// *****************        //Below is adding too many quotes       ******************** //
        responseString = responseString.replaceAll("\"", "\"\"");
        //responseString = responseString.replaceAll(", ", ",");
        
        
        //the datepattern that appears in the log file
        String datePattern = "\\[\\d{1,2}/\\d{1,2}/\\d{2}\\s+\\d{1,2}:\\d{2}:\\d{2}:\\d{3}\\sUTC\\]";
        
        //compiles the date pattern and creates the matcher
        Pattern r = Pattern.compile(datePattern);
        Matcher m = r.matcher(responseString);
        
        //Extracts the date and times
        while(m.find()) {
            String dateAndTime = m.group();
            dateAndTimes.add(dateAndTime);
            responseString = responseString.replaceFirst(datePattern, randomString);
        }
        
        //splits the string into an array of the log entries
        String[] ops = responseString.split("asdfghjkl");
        
        
        //.csv file creator
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("newestLogData.pdf"));
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        StringBuilder builder = new StringBuilder();
        
        //adds labels to the columns
        String ColumnNamesList = "Date And Time, Log Entry";
        
        builder.append(ColumnNamesList +"\n");
        
        //changes the array to an array list
        ArrayList<String> logEntryList = new ArrayList<String>(Arrays.asList(ops));
        //removes the first element of the arrayList
        logEntryList.remove(0);
        
        //appends both lists to the csv file and formats it with commas and line breaks
        for(int i = 0; i < dateAndTimes.size(); i++) {
            builder.append(dateAndTimes.get(i) + ",");
            builder.append("\"" + logEntryList.get(i) + "\" \n");
            System.out.println(i + " entries added");
            //logger.log(Level.BASIC, i + " entries added");
        }
        
        pw.write(builder.toString());
        pw.close();
        System.out.println("Done!");
        
		//downloaded a library for uploading files to slack via the API
		File uploadFile = new File("newerLogData.csv");

		new Meteoroid.Builder()
		.token(botToken)
		.uploadFile(uploadFile)
		.channels(rohanaID)
		.title("Analyzed Log")
		.initialComment("Watson uploaded this file")
		.build()
		.post();
        
        
    }
    
}






