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

public class Ltlfo2monExecution extends Execution
{
  private final String m_jarLocation = "/usr/local/lib/ltlfo2mon.jar";

  @Override
  public boolean isReady()
  {
    return !m_trace.isEmpty() && !m_property.isEmpty();
  }

  @Override
  protected String[] getCommandLines()
  {
    String[] s = new String[1];
    StringBuilder out = new StringBuilder();
    out.append("java -jar ").append(m_jarLocation)
    .append(" \"$(cat ").append(m_property).append(")\" \"$(cat ").append(m_trace).append(")\"");
    s[0] = out.toString();
    return s;
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
    return "ltlfo";
  }

  @Override
  ReturnVerdict parseReturnString(String strValue)
  {
    if (strValue.contains("⊥"))
      return ReturnVerdict.FALSE;
    if (strValue.contains("⊤"))
      return ReturnVerdict.TRUE;
    if (strValue.contains("?"))
      return ReturnVerdict.INCONCLUSIVE;
    return ReturnVerdict.ERROR;
  }

}
