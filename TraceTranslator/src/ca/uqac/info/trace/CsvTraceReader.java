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

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * Parses a trace written as a comma-separated values (CSV) file. A
 * CSV file typically has the form:
 * <pre>
 * pname<sub>1</sub>,pname<sub>2</sub>,...,pname<sub><i>n</i></sub>
 * val<sub>1</sub>,val<sub>2</sub>,...,val<sub><i>n</i></sub>
 * val<sub>1</sub>,val<sub>2</sub>,...,val<sub><i>n</i></sub>
 * ...
 * </pre>
 * Where the pnames are the names for each field (column), and the remaining
 * lines give comma-separated list of values for each parameter.
 * The parser is robust, in that it ignores lines made of whitespace only,
 * and accepts the "#" character for comment lines
 * @author sylvain
 *
 */
public class CsvTraceReader extends TraceReader
{
  /**
   * The field separator used in the file
   */
  protected String m_separator = ",";
  
  /**
   * Whether the first line in the file contains the name for each
   * column
   */
  protected boolean m_paramNamesOnFirstLine = true;
  
  /**
   * Whether to ignore lines that do not contain at least one
   * occurrence of a separator. This could be used, e.g. to beautify
   * the input CSV file by putting a string of dashes between lines.
   */
  protected boolean m_ignoreLinesWithoutSeparator = true;
  
  /**
   * Character that denotes a comment line
   */
  protected String m_commentChar = "#";
  
  /**
   * Default constructor
   */
  public CsvTraceReader()
  {
    super();
  }
  
  /**
   * Specifies whether whether the first line in the file contains
   * the name for each column. If set to false, dummy names of the
   * form <tt>p<sub><i>i</i></sub></tt> will be given to each parameter
   * @param b
   */
  public void setParamNamesOnFirstLine(boolean b)
  {
    m_paramNamesOnFirstLine = b;
  }
  
  /**
   * Defines the character to be used as a field separator.
   * By default, this separator is the comma. 
   * @param separator The character
   */
  public void setSeparator(String separator)
  {
    m_separator = separator;
  }

  @Override
  public EventTrace parseEventTrace(File f)
  {
    Scanner scanner = null;
    Vector<String> param_names = new Vector<String>();;
    Document doc = super.getEmptyDomDocument();
    if (doc == null)
      return null;
    EventTrace trace = new EventTrace();
    int num_line = 0;
    try
    {
      scanner = new Scanner(new FileInputStream(f));
      while (scanner.hasNextLine())
      {
        num_line++;
        String line = scanner.nextLine();
        line = line.trim();
        if (line.isEmpty())
          continue;
        if (line.startsWith(m_commentChar))
          continue;
        if (m_ignoreLinesWithoutSeparator && !line.contains(m_separator))
          continue;
        String[] params = line.split(m_separator);
        if (num_line == 1 && m_paramNamesOnFirstLine)
        {
          // If first line contains parameter names, store those names 
          for (String p : params)
            param_names.add(p);
          continue;
        }
        Event e = lineToEvent(doc, params, param_names);
        trace.add(e);
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace(System.err);
    }
    finally
    {
      if (scanner != null)
        scanner.close();
    }
    return trace;
  }
  
  /**
   * Creates an event from a line of the file
   * @param doc The DOM Document used to spawn new nodes
   * @param line The line from the file
   * @param param_names A (possibly empty) list of parameter names 
   * @return The event produced from the line
   */
  protected Event lineToEvent(Document doc, String[] params, Vector<String> param_names)
  {
    Node event = doc.createElement("Event"); // We don't care about the event name
    for (int i = 0; i < params.length; i++)
    {
      String p_name = "p" + i; // Default parameter name if none specified
      if (i < param_names.size())
        p_name = param_names.elementAt(i);
      Node p = doc.createElement(p_name);
      Node v = doc.createTextNode(params[i]);
      p.appendChild(v);
      event.appendChild(p);
    }
    Event e = new Event(event);
    return e;
  }

}
