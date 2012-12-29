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

import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Parses a trace written as an XML file file. The XML file is expected
 * to have the form:
 * <pre>
 * &lt;Trace&gt;
 *   &lt;Event&gt;
 *   ...
 *   &lt;/Event&gt;
 *   &lt;Event&gt;
 *   ...
 *   &lt;/Event&gt;
 *   ...
 * &lt;/Trace&gt;
 * </pre>
 * where each <tt>Event</tt> element itself nests elements that are
 * taken as the parameters for that event. The parser uses Java's
 * own DOM parser and hence should accept any well-formed XML file.
 * @author Sylvain Hallé
 *
 */
public class XmlTraceReader extends TraceReader
{
  protected String m_eventTagName = "message";//"message"
  
  public void setEventTagName(String n)
  {
    m_eventTagName = n;
  }
  
  public XmlTraceReader()
  {
    super();
  }
  
  public EventTrace eventTrace()
  {
    EventTrace t = new EventTrace(m_eventTagName);
    return t;
  }
  
  public EventTrace eventTrace(Document doc)
  {
    if (doc == null)
      return null;
    EventTrace t = new EventTrace(m_eventTagName);
    t.parse(doc);
    return t;
  }

  @Override
  public EventTrace parseEventTrace(InputStream f)
  {
    Document doc = null;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse(f);
      doc.getDocumentElement().normalize();
    }
    catch (ParserConfigurationException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (SAXException e)
    {
      e.getMessage();
    }
    return eventTrace(doc);
  }
}
