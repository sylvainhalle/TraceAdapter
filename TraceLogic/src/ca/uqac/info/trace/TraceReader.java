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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

/**
 * A TraceFactory takes a file as input and produces an EventTrace as
 * output.
 * @author sylvain
 *
 */
public abstract class TraceReader
{ 
  /**
   * Produces a trace from an input stream
   * @param f The input stream to read from
   * @return The EventTrace
   */
  public abstract EventTrace parseEventTrace(InputStream f);
  
  /**
   * Produces an instance of a DOM Document that child factories can use
   * to build event traces
   * @return An empty DOM Document
   */
  protected Document getEmptyDomDocument()
  {
    Document doc = null;
    try
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      doc = builder.newDocument();
    }
    catch (ParserConfigurationException e)
    {
      e.printStackTrace(System.err);
    }
    return doc;
  }
}
