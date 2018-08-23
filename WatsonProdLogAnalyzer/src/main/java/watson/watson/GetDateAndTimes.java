package watson.watson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDateAndTimes {
	public static List getDateAndTimes(String entry) throws Exception {
		
		Path path = Paths.get(System.getProperty("user.dir")).resolve("/Users/James.Thomas.Holden@ibm.com/git/Watson/newestLogData.csv");
		
		BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
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
		List<String> dateList = new ArrayList<String>();
		while(responseString.contains(entry)) {			
			Integer entryIndex = responseString.indexOf(entry);
			String date = responseString.substring(responseString.indexOf("[", entryIndex - 40), entryIndex);
			date = date.replace(",\"", "");
			response.replace(entryIndex, entryIndex + entry.length(), ""); 
			responseString = response.toString();
			dateList.add(date);
		}
		return dateList;
	}
}
