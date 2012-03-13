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

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;

public class XmlTranslator implements Translator
{
  
  /**
   * Whether to write the <tt>&lt;?xml ... ?&gt;</tt> preamble in the
   * output trace
   */
  protected boolean m_writePreamble = false;

  @Override
  public String translateTrace(EventTrace trace)
  {
    StringBuffer out = new StringBuffer();
    String CRLF = System.getProperty("line.separator");
    if (m_writePreamble)
      out.append("<?xml version=\"1.0\" ?>").append(CRLF);
    out.append("<Trace>").append(CRLF).append(CRLF);
    for (Event e : trace)
    {
      String contents = e.toString();
      out.append(contents);
      out.append(CRLF);
    }
    out.append("</Trace>");
    return out.toString();
  }

  @Override
  public String translateFormula(Operator o)
  {
    return o.toString();
  }

}
