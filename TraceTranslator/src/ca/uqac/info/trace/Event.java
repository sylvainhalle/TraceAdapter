/******************************************************************************
  Event trace translator
  Copyright (C) 2012 Sylvain Halle
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License along
  with this program; if not, write to the Free Software Foundation, Inc.,
  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ca.uqac.info.trace;

import java.io.StringWriter;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import ca.uqac.info.util.Relation;

public class Event
{
  protected Node m_contents = null;
  
  /**
   * Empty constructor. Should not be used outside of package.
   */
  /*package*/ Event()
  {
    super();
  }
  
  /**
   * Builds an event, initializing its content based on a DOM
   * node.
   * @param n The node
   */
  public Event(Node n)
  {
    this();
    parse(n);
  }
  
  /**
   * The current implementation of an event merely keeps internally
   * a reference to the DOM node for this event.
   * @param n
   */
  /*package*/ void parse(Node n)
  {
    m_contents = n;
  }
  
  /**
   * Computes the maximum nesting level of leaves into the event.
   * For example, the nesting of
   * <pre>
   * &lt;Event&gt;
   *   &lt;a&gt;123&lt;/a&gt;
   *   &lt;b&gt;
   *     &lt;c&gt;222&lt;/c&gt;
   *   &lt;/b&gt;
   * &lt;/Event&gt;
   * </pre>
   * is 4 (the root of the event is considered level 1).
   * @return The nesting level
   */
  public int getNesting()
  {
    return getNesting(m_contents);
  }
  
  /**
   * Recursive body of {@link getNesting} 
   * @param n
   * @return
   */
  private int getNesting(Node n)
  {
    int max_nesting = 0;
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      max_nesting = Math.max(max_nesting, getNesting(child));
    }
    return max_nesting + 1;
  }
  
  /**
   * Outputs the event as an instance of DOM node
   * @return The node
   */
  public final Node getDomNode()
  {
    return m_contents;
  }
  
  /**
   * Determines if an event is flat. An event is considered flat
   * if it contains only top-level parameters; for example:
   * <pre>
   * &lt;Event&gt;
   *   &lt;a&gt;123&lt;/a&gt;
   *   &lt;b&gt;345&lt;/b&gt;
   * &lt;/Event&gt;
   * </pre>
   * is flat while 
   * <pre>
   * &lt;Event&gt;
   *   &lt;a&gt;123&lt;/a&gt;
   *   &lt;b&gt;
   *     &lt;c&gt;222&lt;/c&gt;
   *   &lt;/b&gt;
   * &lt;/Event&gt;
   * </pre>
   * is not.
   */
  public boolean isFlat()
  {
    return getNesting() <= 3;
  }
  
  public boolean isMultiValued()
  {
    return isMultiValued(m_contents, new HashSet<String>());
  }
  
  private boolean isMultiValued(Node n, Set<String> names)
  {
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE)
        continue;
      String m_name = child.getNodeName();
      if (names.contains(m_name))
        return true;
      names.add(m_name);
      if (isMultiValued(child, names))
        return true;
    }
    return false;
  }
  
  /**
   * Returns the arity of the event, i.e. the number of first-level
   * parameters it contains 
   * @return The arity
   */
  public int getArity()
  {
    NodeList children = m_contents.getChildNodes();
    return children.getLength();
  }
  
  /**
   * Returns the set of possible values for each parameter
   * found in the event
   * @return A map <i>P</i> &rarr; 2<sup>V</sup> from the set
   * of parameter names <i>P</i> to a subset of values <i>V</i>
   */
  public Relation<String,String> getParameterDomain()
  {
    Relation<String,String> domains = new Relation<String,String>();
    getParameterDomainRecursive(m_contents, domains);
    return domains;
  }
  
  /**
   * Recursive body for {@link getParameterDomain()}.
   * @param n
   * @param domains
   */
  private void getParameterDomainRecursive(Node n, Relation<String,String> domains)
  {
    NodeList list = n.getChildNodes();
    int list_length = list.getLength();
    String node_name = n.getNodeName();
    for (int i = 0; i < list_length; i++)
    {
      Node child = list.item(i);
      short n_type = child.getNodeType();
      if (n_type == Node.TEXT_NODE)
      {
        String value = child.getNodeValue().trim();
        if (value.isEmpty())
          continue;
        Relation<String,String> r = new Relation<String,String>();
        r.put(node_name, value);
        domains.fuseFrom(r);
      }
      else
      {
        getParameterDomainRecursive(child, domains);
      }
    }
    
  }
  
  @Override
  public String toString()
  {
    // Taken from http://tech.chitgoks.com/2010/05/06/convert-org-w3c-dom-node-to-string-using-java/
    try
    {
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      StringWriter sw = new StringWriter();
      t.transform(new DOMSource(m_contents), new StreamResult(sw));
      return sw.toString();
    }
    catch (TransformerException e)
    {
      e.printStackTrace(System.err);
    }
    return "";
  }
}
