package ca.uqac.info.trace.generation;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.w3c.dom.Node;

import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.RandomPicker;
/**
 * 
 * @author Samatar
 *
 */
public class CycleGenerator extends TraceGenerator{

	/**
	 * contain the list of attributes of the column of DataBase
	 */	
	protected Vector<Date>vectDate ;
	protected Vector<String > vectContenu ;
	protected Vector<Integer>vectTags = new Vector<Integer>();
	
	protected int t_maxTags ;
	
	/**
	 * Creates a trace generator with default settings
	 */
	public CycleGenerator(){
		m_random = new Random();
		t_maxTags=3;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		process(args, new CycleGenerator());
	}

	
	
	@Override
	public EventTrace generate() {
		if (m_clockAsSeed)
			setSeed(System.currentTimeMillis());
		
		EventTrace trace = new EventTrace();
		
		// Initial vector Tags
		for (int i=0 ; i< t_maxTags ; i++)
		{
			vectTags.add(i);
		}
		
		// We initialize the test vector
		Vector<Boolean> available_choice = new Vector<Boolean>();
		available_choice.add(true);available_choice.add(false);
		
		// We chose the number of message to produce
		int n_message = m_random.nextInt(m_maxMessages + 1 - m_minMessages)
							+ m_minMessages;
		
		// We initialize current vector of  Tags
		Vector<Integer> vectCurrent = new Vector<Integer>();
		int taille = m_random.nextInt(t_maxTags);
		for (int k = 0 ; k < taille ; k ++)
		{
			vectCurrent.add(vectTags.get(k));
		}
		
		for( int j = 0; j < n_message ; j++)
		{
			
			//Do we want to add tags ??
			RandomPicker<Boolean> boolean_picker = new RandomPicker<Boolean>(m_random);
	    	boolean choiceAdd = boolean_picker.pick(available_choice);
	    	
	    	if(choiceAdd)
	    	{
	    		int nbAdd =m_random.nextInt(t_maxTags - 1);
	    		if(nbAdd > 0)
	    			vectCurrent = this.addTag(vectCurrent, nbAdd);
	    	}
			
	    	//Do we want to remove tags ??
	    	boolean choiceRemove = boolean_picker.pick(available_choice);
	    	int nbRemove = 0;
	    	if(choiceRemove)
	    	{
	    		if(vectCurrent.size() > 0)
	    		{
	    			nbRemove =m_random.nextInt(vectCurrent.size());
	    		}
	    		if(nbRemove > 0)
	    			vectCurrent = this.removeTag(vectCurrent, nbRemove);
	    	}
	    	
	    	//Percentage error of number of read
	    	int nbError = m_random.nextInt(10);
	    	
			
			Node event = trace.getNode();
			Node n_items = trace.createElement("Date");
			
			 //create a java calendar instance
			Calendar calendar = Calendar.getInstance();

			// get a java.util.Date from the calendar instance.
			// this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();
			java.sql.Timestamp timestamp= new Timestamp(now.getTime());
			
			n_items.appendChild(trace.createTextNode(timestamp.toString()));
			
			event.appendChild(n_items);
			Node tags = trace.createElement("Tags");
			// Step 2: pick an number
			int nbElement = vectCurrent.size();
			int operation = this.getErrorRead(nbError, nbElement);
	    	
	    	while(operation > 0)
	    	{
	    		Node tag = trace.createElement("Tag");
	    		tag.appendChild(trace.createTextNode(vectCurrent.get(operation-1).toString()));
	    		
	    		tags.appendChild(tag);
	    		
	    		operation--;
	    		
	    	}
	    	event.appendChild(tags);
	    	trace.add(new Event(event));
		}
		
		

		return trace;
	}
/**
 * 
 */
	public void initialize(CommandLine c_line) {
		if (c_line.hasOption("t"))
			m_clockAsSeed = true;
		
		if (c_line.hasOption("n"))
			m_minMessages = new Integer(c_line.getOptionValue("n")).intValue();
		if (c_line.hasOption("N"))
			m_maxMessages = new Integer(c_line.getOptionValue("N")).intValue();
		
		if (c_line.hasOption("M"))
			t_maxTags = new Integer(c_line.getOptionValue("M")).intValue();

	}
	
	@Override
	public String getAppName() {
		return "Cycle Generator";
	}

	/**
	 * Remove number nbRemove the pool of current Tag
	 * @param vectCurrent
	 * @param nbRemove
	 * @return
	 */
	public Vector<Integer> removeTag(Vector<Integer> vectCurrent, int nbRemove) 
	{
		Vector<Integer> temp = new Vector<Integer>();
	
		while (nbRemove > 0) 
		{
			int nbElement = vectCurrent.size();
			int i = m_random.nextInt(nbElement);
			vectCurrent.remove(i);
			nbRemove--;
		}

		temp = vectCurrent;

		return temp;
	}
	
	/**
	 * Add number nbAdd Tags to the pool of current Tag
	 * @param vectCurrent
	 * @param nbAdd
	 * @return
	 */
	public Vector<Integer> addTag (Vector<Integer> vectCurrent, int nbAdd)
	{
		Vector<Integer> temp= vectCurrent;
		int nbElement = vectTags.size();
		
		while (nbAdd > 0)
		{
			int j = m_random.nextInt(nbElement);
			if( (j < vectTags.size()) &&(j>0) )
			{
				temp.add( vectTags.get(j) );
			}
			
			nbAdd--;
		}
		
		return temp;
	}
	
	public int getErrorRead( int nbError, int nbElement)
	{
		double mistake = (nbElement * nbError)/100;
	    int resultat = (int) Math.ceil(nbElement- mistake);
	    
		return resultat;
	}
}
