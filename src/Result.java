import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Result
{
	public static Dictionary<Entry, Long> documentLengths = new Hashtable<Entry, Long>();
	public static Dictionary<String, Long> nrDocsInClass = new Hashtable<String, Long>();
	private String feature;
	
	
	public String getFeature()
	{
		return feature;
	}
	public void setFeature(String feature)
	{
		this.feature = feature;
	}
	
	public Result(String featureName, long freq, List docs)
	{
		super();
		this.setFeature(featureName);
		this.freq = freq;
		this.docs = docs;
	}
	private long freq;
	public long getFreq()
	{
		return freq;
	}
	public void setFreq(long freq)
	{
		this.freq = freq;
	}
	public List<Entry> getDocs()
	{
		return docs;
	}
	public void setDocs(List<Entry> docs)
	{
		this.docs = docs;
	}
	
	public static void incrementNrDocsInClass(String className)
	{
		Long oldLength = nrDocsInClass.get(className);
		if(oldLength != null)
		{
			oldLength++;
			nrDocsInClass.remove(className);
			nrDocsInClass.put(className, oldLength);
		}
		else
		{
			oldLength = 1L;
			nrDocsInClass.put(className, oldLength);
		}		
	}
	
	public void addToDocList(String className, Long docId)
	{
		
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();
	    	if(next.getDocId() == docId && next.getClassName().equals(className))
	    	{
	    		next.setFreq(next.getFreq() + 1);
	    		itr.set(next);
	    		
	    		return;
	    	}
	    }
		Entry entry = new Entry(className, docId);
		entry.setFreq(1);
		
		docs.add(entry);
		setFreq(getFreq() + 1);
		
		incrementDocumentLength(className, docId);
	}
	
	public static void incrementDocumentLength(String className, Long docId)
	{
		Long oldLength = documentLengths.get(new Entry(className, docId));
		if(oldLength != null)
		{
			oldLength++;
			documentLengths.remove(new Entry(className, docId));
			documentLengths.put(new Entry(className, docId), oldLength);
		}
		else
		{
			oldLength = 1L;
			documentLengths.put(new Entry(className, docId), oldLength);
		}		
	}
	
	public void calculateWeightsBoolean()
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	if(frequency > 0)
	    		next.setWeight(1.0);
	    	else
	    		next.setWeight(0.0);
	    	
	    	itr.set(next);
	    }
	}
	
	public void calculateWeightsTF()
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	next.setWeight(frequency.doubleValue());
	    	
	    	itr.set(next);
	    }
	}
	
	public void calculateWeightsNormalizedTF()
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	next.setWeight(frequency.doubleValue()/ documentLengths.get(next));
	    	
	    	itr.set(next);
	    }
	}
	
	public void calculateWeightsTFIDF()
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	next.setWeight((frequency.doubleValue()/ documentLengths.get(next)) * (Math.log10(nrDocsInClass.get(next.getClassName()))/  docs.size()));
	    	
	    	itr.set(next);
	    }
	}
	
	
	
	public boolean performFeatureSelection(int lowerTreshold, int upperTreshold)
	{
		int counter = 0;
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	
	    	if(frequency > 0)
	    	{
	    		counter++;
	    	}	    	
	    }
	    
	    if(counter < lowerTreshold || counter > upperTreshold)
	    {
	    	// Remove Feature
	    	setFreq(0);
	    	return true;
	    }
	    return false;
	}
	
	public String toString()
	{
		return feature + "  " + freq;
	}
	
	
	private List<Entry> docs = new LinkedList<Entry> ();
}
