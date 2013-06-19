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

//import ca.uqac.info.trace.execution.Execution.ReturnVerdict;

/**
 * Manages the execution of the trace validator using the
 * ProM LTLChecker plug-in
 * @author Jason Vallet
 */
public class JavaMOPExecution extends Execution
{

  @Override
  protected String[] getCommandLines()
  {
    String[] cl = new String[2];
    StringBuilder out = new StringBuilder();
    /*out.append(m_commandLocation)
    .append(" ").append(conditionalQuote(m_trace));*/
    out.append("/usr/bin/java ")
    .append("-classpath ")
    .append("/usr/local/lib/LTLChecker.jar:")
    .append("/usr/local/lib/OpenXES.jar:")
    .append("/usr/local/lib/ProM62.jar:")
    .append("/usr/local/lib/Spex.jar:")
    .append("./PromCliLTLChecker.jar")
    .append(" ")
    .append("MainChecker ")
    .append(m_property).append(" ")
    .append(m_trace).append(" ")
    .append("current_formula"); 

    cl[0] = out.toString();

    out.append("/usr/bin/java ")
    .append("-classpath ")
    .append("/usr/local/lib/LTLChecker.jar:")
    .append("/usr/local/lib/OpenXES.jar:")
    .append("/usr/local/lib/ProM62.jar:")
    .append("/usr/local/lib/Spex.jar:")
    .append("./PromCliLTLChecker.jar")
    .append(" ")
    .append("MainChecker ")
    .append(m_property).append(" ")
    .append(m_trace).append(" ")
    .append("current_formula"); 

    cl[0] = out.toString();
    return cl;
  }

  @Override
  /* package */ ReturnVerdict parseReturnString(String strValue)
  {
    ReturnVerdict val = ReturnVerdict.INCONCLUSIVE;
    String[] lines = strValue.split(CRLF);
    for (String line : lines)
    {
      if (line.contains("TRUE"))
      {
        val = ReturnVerdict.TRUE;
      }
      else if (line.contains("FALSE"))
      {
        val = ReturnVerdict.FALSE;
      }
      else if (line.contains("ERROR"))
      {
        val = ReturnVerdict.ERROR;
      }
    }
    return val;
  }

  @Override
  public boolean isReady()
  {
    return !m_trace.isEmpty() && !m_property.isEmpty();
  }

  @Override
  protected String getStdin()
  {
    return null;
  }

  @Override
  public String getTraceExtension()
  {
    return "java";
  }

  @Override
  public String getFormulaExtension()
  {
    return "mop";
  }
}
