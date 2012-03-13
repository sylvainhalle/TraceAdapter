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
package ca.uqac.info.trace.conversion;
import org.w3c.dom.*;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;

/**
 * Translates an event trace into JSON notation.
 * @author sylvain
 */
public class JsonTranslator implements Translator
{

  /**
   * Table name for storing the trace
   */
  protected final String m_tableName = "trace";

  public String translateTrace(EventTrace m_trace)
  {
    StringBuffer out = new StringBuffer();
    out.append("{\n  \"trace\" :\n  [\n");
    int trace_length = m_trace.size();
    for (int i = 0; i < trace_length; i++)
    {
      Event e = m_trace.elementAt(i);
      out.append("    {\n");
      Node n = e.getDomNode();
      NodeList children = n.getChildNodes();
      boolean added_one = false;
      for (int j = 0; j < children.getLength(); j++)
      {
        Node child = children.item(j);
        String val = child.getTextContent();
        val = val.trim();
        if (val.isEmpty())
          continue;
        if (added_one)
          out.append(",\n");
        added_one = true;
        out.append(toJson(child, "      "));    
      }
      if (added_one)
        out.append("\n");
      out.append("    }");
      if (i < trace_length - 1)
        out.append(",");
      out.append("\n");
    }
    out.append("  ]\n");
    out.append("}");
    return out.toString();
  }
  
  private StringBuffer toJson(Node n, String indent)
  {
    StringBuffer out = new StringBuffer();
    NodeList children = n.getChildNodes();
    int num_children = children.getLength();
    out.append(indent).append("\"").append(n.getNodeName()).append("\": ");
    if (num_children == 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
    {
      Node child = children.item(0);
      String val = child.getNodeValue();
      val = val.trim();
      if (val.isEmpty())
      {
        out = new StringBuffer();
        return out;
      }
      out.append("\"").append(val).append("\"");
      return out;
    }
    out.append("\n").append(indent).append("{\n");
    boolean added_one = false;
    for (int i = 0; i < num_children; i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE)
      {
         String val = child.getNodeValue();
         val = val.trim();
         if (val.isEmpty()) // We ignore whitespace
           continue;
      }
      if (added_one)
        out.append(",");
      added_one = true;
      int num_nonempty_children = countNonEmptyChildren(child);
      if (num_nonempty_children > 1)
        out.append(indent).append("{\n");
      out.append(toJson(child, indent + "  "));
      if (num_nonempty_children > 1)
        out.append(indent).append("}");
      if (num_nonempty_children > 1)
        out.append("\n");
    }
    out.append("\n").append(indent).append("}\n");
    return out;
  }
  
  private int countNonEmptyChildren(Node n)
  {
    int value = 0;
    NodeList children = n.getChildNodes();
    int num_children = children.getLength();
    for (int i = 0; i < num_children; i++)
    {
      Node child = children.item(i);
      String val = child.getNodeValue();
      val = val.trim();
      if (val.isEmpty()) // We ignore whitespace
        continue;
      value++;
    }
    return value;
  }

  @Override
  public String translateFormula(Operator o)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
