import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BOW
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{		
		Dictionary<String, Result> results = new Hashtable<String, Result>();
		
		// Read File and tokenize		
		File mainDirectory = new File("20news-18828");
		
		// Iterate directories
		File directories[] = mainDirectory.listFiles();
		for (File directory : directories) 
		{
		   // Get class name
		   String className = directory.getName();
		   
		   // Iterate files
		   File files[] = directory.listFiles();
		   for (File file : files) 
		   {
			   	Long fileName = Long.parseLong(file.getName());
			   	
				ArrayList<String> tokens = Utility.Tokenize(file);
				tokens = Utility.Stemm(tokens);	
				
				// Add Tokens to Result List
				for(String token : tokens)
				{
					Result result = results.get(token);
					
					// Already in Result List?
					if(result == null)
					{
						// No add to dictionary
						List<Entry> newList = new LinkedList<Entry>();
						Entry entry = new Entry();
						entry.setDocId(fileName);
						entry.setFreq(1);
						
						newList.add(entry);
						
						results.put(token, new Result(token, 1, newList));
						
						Result.incrementDocumentLength(fileName);
					}
					else
					{
						// Add document if it's not already in list						
						
						// Add Document Reference to dictionary
						result.addToDocList(fileName);						
					}
				}
		   }
		   
		   
		}
		
	    Enumeration<Result> e = results.elements();
	    
	    //iterate through Hashtable values Enumeration
	    while(e.hasMoreElements()) {
	      Result result = e.nextElement();
	      result.calculateWeights();
	      
	      if(result.performFeatureSelection(20,200))
	      {
	    	  results.remove(result.getFeature());	    	  
	      }
	      else
	      {
	    	  System.out.println(result);
	      }
	      
	      
	  }
		
		// Print Result List
	    //System.out.println(results.toString());
		
		//File file = new File("9150");		

	}
	


}
