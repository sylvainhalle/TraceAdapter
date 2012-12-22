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
 * Manages the execution of the trace validator using Maude
 * @author sylvain
 */
public class MaudeExecution extends Execution
{
  private final String m_commandLocation = "maude";

  @Override
  protected String[] getCommandLines()
  {
    String[] cl = new String[1];
    StringBuilder out = new StringBuilder();
    out.append(m_commandLocation)
    .append(" ").append(m_trace);
    cl[0] = out.toString();
    return cl;
  }

  @Override
  /* package */ ReturnCode parseReturnValue(String strValue)
  {
    if (strValue.contains("(true).Bool"))
      return ReturnCode.TRUE;
    if (strValue.contains("(false).Bool"))
      return ReturnCode.FALSE;
    return ReturnCode.ERROR;
  }

  @Override
  public boolean isReady()
  {
    return !m_trace.isEmpty();
  }

  @Override
  protected String getStdin()
  {
    return null;
  }
  
  @Override
  public String getTraceExtension()
  {
    return "maude";
  }
}