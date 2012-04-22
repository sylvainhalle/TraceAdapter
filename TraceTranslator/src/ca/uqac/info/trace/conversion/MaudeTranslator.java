package ca.uqac.info.trace.conversion;

import java.util.Set;
import java.util.Vector;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;
import org.w3c.*;
import org.w3c.dom.Node;

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
	 public String translateTrace(EventTrace t)
	  {
	    StringBuffer out = new StringBuffer();
	    Relation<String,String> param_domains = t.getParameterDomain();
	    Set<String> params = param_domains.keySet();
	    System.out.println("parm_domain"+ params);
	    //Vector<String> o_params = new Vector<String>();
	    // We dump params in a Vector to forced ordered iteration every time
	    for (String p : params)
	      o_params.add(p);
	    // Start writing the Java program
	    out.append("/**\n  Auto-generated program for trace analysis through Java\n */\n");
	    out.append("public class ").append(m_className).append("\n");
	    out.append("{\n");
	    out.append("  public static void main(String[] args)\n");
	    out.append("  {\n");
	    for (Event e : t)
	    {
	      Relation<String,String> e_dom = e.getParameterDomain();
	      out.append("    my_event(");
	      for (int i = 0; i < o_params.size(); i++)
	      {
	        String p_name = o_params.elementAt(i);
	        Set<String> p_values = e_dom.get(p_name);
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
	          if (p_values.size() > 1)
	          {
	            // TODO: trop de valeurs
	          }
	        }
	        if (i < o_params.size() - 1)
	          out.append(",");
	      }
	      out.append(");\n");
	    }
	    out.append("  }\n");
	    out.append("\n");
	    out.append("  public static void my_event(");
	    for (int i = 0; i < o_params.size(); i++)
	    {
	      String p = o_params.elementAt(i);
	      out.append("String ").append(p);
	      if (i < o_params.size() - 1)
	        out.append(",");
	    }
	    out.append(")\n");
	    out.append("  {\n");
	    out.append("    // This function is a mere placeholder to be caught\n");
	    out.append("  }\n");
	    out.append("}\n");
	    return out.toString();
	 
}
	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		return null;
	}

}
