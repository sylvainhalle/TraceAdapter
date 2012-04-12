package ca.uqac.info.trace.conversion;

import java.util.Set;
import java.util.Vector;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;

public class MaudeTranslator implements Translator
{

	Vector<String> o_params;
	  
	  /**
	   * Constructor
	   */
	  public MaudeTranslator()
	  {
	    super();
	    o_params = new Vector<String>();
	  }

	@Override
	public String translateTrace(EventTrace t) {
		StringBuffer out = new StringBuffer();
	    Relation<String,String> param_domains = t.getParameterDomain();
	    Set<String> params = param_domains.keySet();
	    
	    for (String p : params)
	    {
	    	o_params.add(p);
	    	System.out.println("\n params : "+p);
	    }
	    
	    for (Event e : t)
	    {
	      Relation<String,String> e_dom = e.getParameterDomain();
	     
	      String p_name = o_params.lastElement();
	      Set<String> p_values = e_dom.get(p_name);
	      
	      System.out.println("\n p_name : "+p_name);
	      if (p_values == null || p_values.isEmpty())
	          out.append("null");
	        else
	        {
	          String p_val = "";
	          for (String v : p_values)
	          {
	            p_val = v;
	            break;
	          }
	          out.append(p_val);
	          System.out.println("\n p_val : "+p_val);
	          
	          if (t.lastElement()!= e)
		          out.append(" , ");
	        }
	    }

	    return out.toString();
	 
}
	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		return null;
	}

}
