/**
 * 
 */
package ca.uqac.info.trace;
import java.util.*;
import org.w3c.dom.*;

import ca.uqac.info.util.Relation;

/**
 * @author sylvain
 *
 */
public class EventTrace extends Vector<Event>
{
  protected String m_eventTagName = "";
  
  /**
   * Auto-generated UID
   */
  private static final long serialVersionUID = 1L;
  
  public EventTrace()
  {
    super();
  }
  
  public EventTrace(String eventTagName)
  {
    super();
    m_eventTagName = eventTagName;
  }
  
  /*package*/ void parse(Document doc)
  {
    NodeList list = doc.getElementsByTagName(m_eventTagName);
    final int list_length = list.getLength();
    for (int i = 0; i < list_length; i++)
    {
      Node n = list.item(i);
      Event e = new Event(n);
      add(e);
    }    
  }
  
  /*package*/ void setEventTagName(String n)
  {
    m_eventTagName = n;
  }
  
  /**
   * Returns the maximal arity of a message in the
   * trace
   * @return
   */
  public int getMaxArity()
  {
    int max_arity = 0;
    Iterator<Event> it = iterator();
    while (it.hasNext())
    {
      Event e = it.next();
      Math.max(max_arity, e.getArity());
    }
    return max_arity;
  }
  
  /**
   * Returns the set of possible values for each parameter
   * found in the trace
   * @return A map <i>P</i> &rarr; 2<sup>V</sup> from the set
   * of parameter names <i>P</i> to a subset of values <i>V</i>
   */
  public Relation<String,String> getParameterDomain()
  {
    Relation<String,String> domains = new Relation<String,String>();
    Iterator<Event> it = iterator();
    while (it.hasNext())
    {
      Event e = it.next();
      Relation<String,String> d = e.getParameterDomain();
      domains.fuseFrom(d);
    }
    return domains;
  }

}
