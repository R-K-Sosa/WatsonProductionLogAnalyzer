package watson.watson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONObject;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.DocumentAccepted;
import com.ibm.watson.developer_cloud.discovery.v1.model.AddDocumentOptions;

public class ReadNSearchFile {

	public static void main(String[] args) throws Exception {
		
        String JTID = "DB5JSKDLZ";
        String rohanaID = "DB5JSKECR";
		
        JSONObject json = new JSONObject();
        
		Path path = Paths.get(System.getProperty("user.dir")).resolve("/Users/James.Thomas.Holden@ibm.com/git/Watson/newestLogData.csv");
		
		BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
		
		LinkedHashMap<String, Integer> frequency = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, Integer> frequencyInOrder = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, LinkedHashSet<String>> mapWithDates = new LinkedHashMap<String, LinkedHashSet<String>>();
		
		//as long as there is a non-null character, read each line and stop reading lines at null
		String line = reader.readLine();
        StringBuffer response = new StringBuffer();
        //creates the stringBuffer by appending each line and adding line breaks
		while(line != null) {
			response.append(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		String responseString = response.toString();
		responseString = responseString.replaceFirst("Date And Time, Log Entry", "");
		String datePattern = "\\[\\d{1,2}/\\d{1,2}/\\d{2}\\s+\\d{1,2}:\\d{2}:\\d{2}:\\d{3}\\sUTC\\]" + ",\" [\\S]{8}";
       //Date and time hashSet
		LinkedHashSet<String> tempHashSet = new LinkedHashSet<String>();
        Pattern r = Pattern.compile(datePattern);
        Matcher m = r.matcher(responseString);
        while(m.find()) {
            String dateAndTime = m.group();
            tempHashSet.add(dateAndTime);
            responseString = responseString.replaceFirst(datePattern, "asdfghjkl");
        }
        
		String [] words2 = responseString.split("asdfghjkl");
		
		for(int i = 0; words2.length > i; i++) {
			words2[i] = words2[i].substring(0, words2[i].length() - 1);
		}
		
		for (String word : words2) {
			//word = word.substring(0, word.length() - 1);
			if(word == null || word.trim().equals("")) {
				continue;
			}
			
			if(frequency.containsKey(word)) {
				frequency.put(word, frequency.get(word) + 1);
			} else {
				frequency.put(word, 1);
			}
		}
		
		
		//System.out.println(frequency);
		
		int mostFrequencyUsed = 0;
		String theEntry = null;
		
		
		//sorting loop for that puts the entries into a new linked hashmap in order
		boolean sorted = false;
		while(!sorted) {
			for(String entry : frequency.keySet()){
				Integer theVal = frequency.get(entry);
				if(theVal > mostFrequencyUsed) {
					mostFrequencyUsed = theVal;
					theEntry = entry;
				}
			}
			frequencyInOrder.put(theEntry, frequency.get(theEntry));
			frequency.remove(theEntry);
			mostFrequencyUsed = 0;
			if(frequency.isEmpty()) {
				sorted = true;
			}
		}
		
		String errorFix = "Fix%20this%20exception%20by";
		
        // file creator
        PrintWriter pw = null;  //dasnjdjaskn
        try {
            pw = new PrintWriter(new File("LogFiles.json"));
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        StringBuilder builder = new StringBuilder();
        //adds labels to the columns
        //String ColumnNamesList = "Frequency, Log Entry";
        
        //builder.append(ColumnNamesList +"\n");
        
        //This integer is to count how many different types of errors there are
        int errorTypeCount = 0;
        
        
        //loop that goes through all exceptions that appear in the log file as well as entries that appear more than once
		for(Entry<String, Integer> mpd:frequencyInOrder.entrySet()){
			if(mpd.getKey().contains("Exception") || mpd.getValue() > 1) {
				errorTypeCount++;
				//LogError currentError = new LogError(mpd.getKey());
				
				//puts errors to the JSON format
		        json.put(mpd.getKey(), mpd.getValue());
		        //this stringbuffer will be the text that is put in the web API call to send back to the user
		        StringBuffer slackResponse = new StringBuffer();
		        //appends the error, dateAndTimes, as well as the invoice number to the slack response, with line breaks inbetween
		        slackResponse.append("Error " + errorTypeCount + " from the log is: " + mpd.getKey() + " \n \n \n Date and Times it appeared: " + GetDateAndTimes.getDateAndTimes(mpd.getKey()) + "\n \n Click here to see fix: " + "https://slack.com/api/chat.postMessage?token=xoxb-377960480037-380083483923-4BFuqRwyFhISCBPHLwcreQEH&channel=" + JTID + "&text=" + errorFix + "\n \n Click here to create/change fix: https://slack.com/api/chat.postMessage?token=xoxb-377960480037-380083483923-4BFuqRwyFhISCBPHLwcreQEH&channel=" + JTID + "&text=Enter%20the%20fix%20below%20for%20error%20" + errorTypeCount + "&pretty=1" + "\n \n_______________________________________________________________________________________________________________________");
		    	//the URI builder allows certain characters from the log entries to be recognized as part of the URL
		        URIBuilder ub = new URIBuilder();
		        //converts the string buffer to a string format
		    	String slackResponseString = slackResponse.toString();
		    	//changes the characters to be able to be read as a URL
		    	ub.addParameter("", slackResponseString);    	
		    	//changes it back to string
		    	slackResponseString = ub.toString();
		    	//removes the first two letters of the string, which aren't helpful
		    	slackResponseString = slackResponseString.substring(2);
		    	//creates the URL with all the api syntax
		    	String postLogResults = "https://slack.com/api/chat.postMessage?token=xoxb-377960480037-380083483923-4BFuqRwyFhISCBPHLwcreQEH&channel=" + JTID + "&text=" + slackResponseString + "&pretty=1";
		    	//This next part opens the connection to the URL thus posting the message to the slack channel
		    	URL postLogResultsURL = new URL(postLogResults);   	
		        URLConnection yc = postLogResultsURL.openConnection();
		        yc.getInputStream();
		        System.out.println(errorTypeCount);
		        //UploadErrorSolution.getErrorSolution(mpd.getKey());
			}
		}
        pw.write(builder.toString());
        pw.close();
        System.out.println("Done!");

        //creates the discovery object which allows us to use the API
    	Discovery discovery = new Discovery(
    		    "2018-03-05",
    		    "668d6291-1fec-4d99-a97b-114f411aaca8",
    		    "4tuIbvKD4i4W");

    	//creates all the variables of the user IDs we need
    	String environmentId = "7e180bb0-da44-4891-ad6f-0bb8fd44ee8a";
    	String collectionId = "eeadead5-9c5d-407c-be29-30857998dc80";
    	String documentJson = "{\"field\":\"value\"}";
    	InputStream documentStream = new ByteArrayInputStream(documentJson.getBytes());

    	AddDocumentOptions.Builder builder1 = new AddDocumentOptions.Builder(environmentId, collectionId);
    	String logFileString;
    	File logFile = new File("logFile.json");
    	builder1.filename("logFile.json");
    	
    	logFileString = json.toJSONString();
    	
    	PrintWriter pwJSON = null;
        try {
            pwJSON = new PrintWriter(new File("logFile.json"));
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        pwJSON.write(logFileString);
        pwJSON.close();
        System.out.println("Done!");
        
        
    	builder1.file(logFile);
    	DocumentAccepted createResponse = discovery.addDocument(builder1.build()).execute();
    	
    	
    	

	}

}



