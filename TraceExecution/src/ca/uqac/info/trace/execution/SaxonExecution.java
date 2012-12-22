/******************************************************************************
  Execution of trace validation tools
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
package ca.uqac.info.trace.execution;

/**
 * Manages the execution of the XQuery validator using Saxon
 * @author sylvain
 */
public class SaxonExecution extends Execution
{
  private final String m_jarLocation = "/usr/local/lib/SaxonValidator.jar";

  @Override
  protected String[] getCommandLines()
  {
    String[] cl = new String[1];
    StringBuilder out = new StringBuilder();
    out.append("java -jar ").append(m_jarLocation)
    .append(" -m saxon -i ").append(conditionalQuote(m_trace)).append(" -f ").append(conditionalQuote(m_property));
    cl[0] = out.toString();
    return cl;
  }

  @Override
  public boolean isReady()
  {
    return !(m_trace.isEmpty() || m_property.isEmpty());
  }

  @Override
  /* package */ ReturnCode parseReturnValue(String strValue)
  {
    String[] lines = strValue.split(CRLF);
    for (String line : lines)
    {
      if (line.contains("INCONCLUSIVE"))
        return ReturnCode.INCONCLUSIVE;
      if (line.contains("TRUE"))
        return ReturnCode.TRUE;
      if (line.contains("FALSE"))
        return ReturnCode.FALSE;
      if (line.contains("Error"))
        return ReturnCode.ERROR;
    }
    return ReturnCode.ERROR;
  }

  @Override
  protected String getStdin()
  {
    return null;
  }
  
  @Override
  public String getTraceExtension()
  {
    return "xml";
  }
  
  @Override
  public String getFormulaExtension()
  {
    return "txt";
  }
}
