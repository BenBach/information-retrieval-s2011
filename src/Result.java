import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Result
{
	public static Dictionary<Long, Long> documentLengths = new Hashtable<Long, Long>();
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
	
	public void addToDocList(Long docId)
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();
	    	if(next.getDocId() == docId)
	    	{
	    		next.setFreq(next.getFreq() + 1);
	    		itr.set(next);
	    		
	    		return;
	    	}
	    }
		Entry entry = new Entry();
		entry.setDocId(docId);
		entry.setFreq(1);
		
		docs.add(entry);
		setFreq(getFreq() + 1);
		
		incrementDocumentLength(docId);
	}
	
	public static void incrementDocumentLength(Long docId)
	{
		Long oldLength = documentLengths.get(docId);
		if(oldLength != null)
		{
			oldLength++;
			documentLengths.remove(docId);
			documentLengths.put(docId, oldLength);
		}
		else
		{
			oldLength = 1L;
			documentLengths.put(docId, oldLength);
		}
		
	}
	
	public void calculateWeights()
	{
		// Already in List
		ListIterator<Entry> itr = docs.listIterator();
	    while(itr.hasNext())
	    {
	    	Entry next = itr.next();

	    	// Get Frequency
	    	Integer frequency = next.getFreq();
	    	next.setWeight(frequency.doubleValue()/ documentLengths.get(next.getDocId()).doubleValue());
	    	
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
