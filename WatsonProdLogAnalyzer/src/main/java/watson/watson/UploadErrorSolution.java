package watson.watson;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UploadErrorSolution {
	public static LogError getErrorSolution() throws Exception {
		String JTID = "DB5JSKDLZ";
        String rohanaID = "DB5JSKECR";
        String botToken = "xoxb-377960480037-380083483923-4BFuqRwyFhISCBPHLwcreQEH";
        String imHistory = "https://slack.com/api/im.history?token=" + botToken + "&channel=" + JTID +"&pretty=1";
        
        
        URL imHistoryURL = new URL(imHistory);
        URLConnection yc = imHistoryURL.openConnection();
        
        //buffered reader to get most recent messages and extract the url for the most recent file uploaded
        BufferedReader in1 = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String imResponse = "";
        String inputLine;
        while ((inputLine = in1.readLine()) != null) 
            imResponse = imResponse + inputLine;
        in1.close();
        //System.out.println(imResponse);
        System.out.println(imHistoryURL);
        
        String errorNumberString;
        
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(imResponse);
        
        //String messageString = json.get("messages").toString();
        //System.out.println(messageString);
        JSONArray jsonMessages = (JSONArray) json.get("messages");
        JSONObject jsonMessage = (JSONObject) jsonMessages.get(2);
        
        errorNumberString = jsonMessage.get("text").toString();
        errorNumberString = errorNumberString.substring(errorNumberString.length() - 1);
        
        
        
        String solution = imResponse.substring(0, imResponse.indexOf("Enter the fix below for error " + errorNumberString));
        solution = solution.substring(0, solution.lastIndexOf("}"));
        solution = solution.substring(solution.lastIndexOf("\"text\": "));
        solution = solution.substring(9, solution.indexOf("\","));
        //System.out.println("to fix error " + errorNumberString + " follow this solution: \n" + solution);
        
        String finalError = "";
        for(int i = 0; i < jsonMessages.size(); i++) {
        	JSONObject currentObject = (JSONObject) jsonMessages.get(i);
        	
        	if(currentObject.get("text").toString().contains("Error " + errorNumberString + " from the log is:")) {
        		String currentError = (currentObject.get("text").toString());
        		currentError = currentError.substring(0, currentError.indexOf("\n \n"));
        		currentError = currentError.replace("Error " + errorNumberString + " from the log is:  ", "");
        		//System.out.println(currentError);
        		finalError = currentError;
        		break;
        		
        	}

        	
        }
        LogError errorObjectFinal = new LogError(finalError, solution);
        
        return errorObjectFinal;
	}
	
	public static void uploadErrorSolution(/*JSONObject errorObject*/) throws Exception {
		
		String urlString = "https://472687cd-80c0-4c73-b172-4351b0f9eea8-bluemix:ae40ff6ba824282dd9e9558843aa2acd71524ee6dd6ad07bd60b61a0f73e04b1@472687cd-80c0-4c73-b172-4351b0f9eea8-bluemix.cloudant.com";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String password = null;
        // optional default is GET
        con.setRequestMethod("GET");
        
        //add request header
        //con.setRequestProperty("Authorization", "Basic yWyaDMghT8fg-0FiF31nEHRARZQWUubXuwYqcIQTbqU0");
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
        System.out.println(response);
	}
	
	public static void main(String[] args) throws Exception {
		//getErrorSolution();
		System.out.println(getErrorSolution().solution);
		uploadErrorSolution();
	}
}
