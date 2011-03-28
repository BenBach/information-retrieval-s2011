
public class Entry
{
	public String className;
	public String docId;
	public Integer freq;
	public Double weight;
	public Entry(String className, String docId)
	{
		this.className = className;
		this.docId = docId;
	}
	public String getClassName()
	{
		return className;
	}
	public void setClassName(String className)
	{
		this.className = className;
	}
	public String getDocId()
	{
		return docId;
	}
	public void setDocId(String docId)
	{
		this.docId = docId;
	}
	public Integer getFreq()
	{
		return freq;
	}
	public void setFreq(Integer freq)
	{
		this.freq = freq;
	}
	public Double getWeight()
	{
		return weight;
	}
	public void setWeight(Double weight)
	{
		this.weight = weight;
	}
	
	public boolean equals(Object anotherEntry)
	{
		if(this.className == ((Entry)anotherEntry).className && this.docId == ((Entry)anotherEntry).docId)
			return true;
		return false;
	}
    public int hashCode()
    {
    	return this.toString().hashCode();
    }
	
    public String toString()
    {
    	return className+docId;
    }
}
