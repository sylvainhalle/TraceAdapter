package ca.uqac.info.trace.conversion;

import java.util.Set;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;

public class XesTranslator implements Translator {

	@Override
	public String translateTrace(EventTrace m_trace) {

		StringBuffer out = new StringBuffer();
		String CRLF = System.getProperty("line.separator");
	    Relation<String,String> domains = m_trace.getParameterDomain();
	    Set<String> params = domains.keySet();
	    
	    out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CRLF);
	    
	    out.append("<!-- This file has been generated with the OpenXES library. It conforms -->").append(CRLF);
	    out.append("<!-- to the XML serialization of the XES standard for log storage and -->").append(CRLF);
	    out.append("<!-- management. -->").append(CRLF);
	    out.append("<!-- XES standard version: 1.0 -->").append(CRLF);
	    out.append(" <!-- OpenXES library version: 1.0RC7 -->").append(CRLF);
	    out.append(" <!-- OpenXES is available from http://www.openxes.org/ -->").append(CRLF);
	    
	    out.append("<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\" xmlns=\"http://www.xes-standard.org/\">").append(CRLF);
	    out.append("<extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>").append(CRLF);
	    out.append("<extension name=\"Organizational\" prefix=\"org\" uri=\"http://www.xes-standard.org/org.xesext\"/>").append(CRLF);
	    out.append("<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>").append(CRLF);
	    out.append("<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>").append(CRLF);
	    out.append("<extension name=\"Semantic\" prefix=\"semantic\" uri=\"http://www.xes-standard.org/semantic.xesext\"/>").append(CRLF);
	   
	    
	    out.append("<global scope=\"trace\" >").append(CRLF);
	    out.append("<string key=\"concept:name\"  value =\"_INVALID_ ").append(CRLF);
	    out.append("</global>").append(CRLF);
	    
	    out.append("<global scope=\"event\" >").append(CRLF);
	    out.append("<string key=\"concept:name\"  value =\"_INVALID_ ").append(CRLF);
	    out.append("<string key=\"lifecycle:transition\"   value= \"complete\" /> ").append(CRLF);
	    out.append("</global>").append(CRLF);
	    
	    out.append("<!-- This file has been generated with the OpenXES library. It conforms -->").append(CRLF);
	    out.append("<!-- to the XML serialization of the XES standard for log storage and -->").append(CRLF);
	    out.append("<!-- management. -->").append(CRLF);
	    out.append("<!-- XES standard version: 1.0 -->").append(CRLF);
	    out.append(" <!-- OpenXES library version: 1.0RC7 -->").append(CRLF);
	    out.append(" <!-- OpenXES is available from http://www.openxes.org/ -->").append(CRLF);
	    
	    
	    out.append("<classifier name=\"MXML legacy Classifier\" keys=\"concept:name lifecycle:transition\" >").append(CRLF);
	    out.append("<classifier name=\"Event Name\" keys=\"org:resource\" >").append(CRLF);
	    out.append("<classifier name=\"Resource\" keys=\"org:resource\" >").append(CRLF);
	    out.append("<classifier name=\"source\" keys=\"Rapid Synthesizer\" >").append(CRLF);
	    out.append("<classifier name=\"concept:name\" keys=\"trace.mxml\" >").append(CRLF);
	    out.append("<classifier name=\"lifecycle:model\" keys=\"standard\" >").append(CRLF);
	    
	    
	    for (Event e : m_trace)
	    {
	    	 if (!e.isFlat() || e.isMultiValued())
		      {
		        out.append("-- WARNING: this event is not flat or is multi-valued\n");
		      }
		     
		      out.append(toXes(e, params));
		      out.append(CRLF);
	    }
	    
	    out.append("</trace>").append(CRLF);
	    out.append("</log>").append(CRLF);
	    
		return out.toString();
	}


	  private StringBuffer toXes(Event e, Set<String> all_params)
	  {
		  String CRLF = System.getProperty("line.separator");
		  String resource = "<string key= \"org:resource value= \"UNDEFINED\" /> "+CRLF;
		  String timeStamp = "<date key= \"time:timestamp  value=\"";
		  String concept = "<string key= org:\"concept:name\" name =\" "; 
	      String lifeCycle = "<string key=\"lifecycle:transition\"   value= \"complete\" /> "+CRLF;
	    
	      StringBuffer out = new StringBuffer();
	      Relation<String,String> domain = e.getParameterDomain();
	      Set<String> params = domain.keySet();
	    
	    
	    
	    for (String p : all_params)
	    {
	     
	      out.append("<event>").append(CRLF);
	      out.append(resource).append(timeStamp).append(System.currentTimeMillis());
	      out.append("\"/>").append(CRLF);
	      String p_name = concept + p+"\" " ;
	      if (!params.contains(p))
	      {
	    	  out.append(p_name).append(" value =\"NULL\"").append("\"/>").append(CRLF);
	    	  
	      }
	      else
	      {
	        Set<String> values = domain.get(p);
	        String val = "";
	        // We get only the first value
	        for (String v : values)
	        {
	          val = " value =\""+v ;
	          break;
	        }
	        out.append(p_name).append(val).append("\"/>").append(CRLF);
	        }
	      
	      out.append(lifeCycle);
    	  out.append("</event>").append(CRLF).append(CRLF);
	    }
	    return out;
	  }
	@Override
	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		return null;
	}

}
