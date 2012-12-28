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
 * Manages the execution of the trace validator using the
 * MySQL database engine
 * @author sylvain
 */
public class MySQLExecution extends Execution
{
  private final String m_commandLocation = "mysql";
  private String m_userName = "root";
  private String m_password = "";

  @Override
  protected String[] getCommandLines()
  {
    String[] cl = new String[2];
    // Specifying a password on the command line is considered
    // insecure. Only do this in isolated environments!
    {
      StringBuilder out = new StringBuilder();
      out.append(m_commandLocation).append(" --user=")
      .append(m_userName).append(" --password=")
      .append(m_password).append(" < ").append(conditionalQuote(m_signature));
      cl[0] = out.toString();
    }
    {
      StringBuilder out = new StringBuilder();
      out.append(m_commandLocation).append(" --user=")
      .append(m_userName).append(" --password=")
      .append(m_password).append(" < ").append(conditionalQuote(m_trace));
      cl[1] = out.toString();
    }
    return cl;
  }

  @Override
  protected String getStdin()
  {
    return m_trace;
  }

  @Override
  /* package */ ReturnVerdict parseReturnString(String strValue)
  {
    String[] lines = strValue.split(CRLF);
    for (String line : lines)
    {
      if (line.contains("Usage:") || line.contains("ERROR"))
        return ReturnVerdict.ERROR;
      if (line.compareTo("0") == 0)
        return ReturnVerdict.TRUE;
    }
    return ReturnVerdict.FALSE;
  }

  @Override
  public boolean isReady()
  {
    return !m_trace.isEmpty();
  }

  @Override
  public String getTraceExtension()
  {
    return "sql";
  }
  
  @Override
  public String getSignatureExtension()
  {
    return "sig.sql";
  }
}
