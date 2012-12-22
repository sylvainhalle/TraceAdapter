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

public class MonpolyExecution extends Execution
{
  private final String m_command = "monpoly";
  private final String m_key = "@";
  private final String m_sep = ":";

  @Override
  protected String[] getCommandLines()
  {
    String[] cl = new String[1];
    StringBuilder out = new StringBuilder();
    out.append(m_command).append(" -sig ")
    .append(conditionalQuote(m_signature)).append(" -formula ")
    .append(conditionalQuote(m_property))
    .append(" -log ").append(conditionalQuote(m_trace));
    cl[0] = out.toString();
    return cl;
  }

  @Override
  /* package */ ReturnCode parseReturnValue(String strValue)
  {
    String s_val = "";
    ReturnCode val = ReturnCode.ERROR;
    String[] lines = strValue.split(CRLF);
    for (String line : lines)
    {
      if (line.contains(m_key))
      {
        String[] tab2 = line.split(m_sep);

        if (tab2.length == 1)
        {
          s_val = tab2[0].trim();
        }
        else
        {
          s_val = tab2[1].trim();
        }
        if (!s_val.isEmpty() && s_val.contains("true"))
        {
          val = ReturnCode.TRUE;
        }
        else
        {
          val = ReturnCode.FALSE;
        }
        break;
      }
    }
    return val;
  }

  @Override
  public boolean isReady()
  {
    return !(m_trace.isEmpty() || m_property.isEmpty() ||
        m_signature.isEmpty());
  }

  @Override
  protected String getStdin()
  {
    return null;
  }
  
  @Override
  public String getTraceExtension()
  {
    return "trace";
  }
  
  @Override
  public String getFormulaExtension()
  {
    return "mfotl";
  }
  
  @Override
  public String getSignatureExtension()
  {
    return "sig";
  }
}