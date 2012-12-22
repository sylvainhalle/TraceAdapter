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
 * Spin model checker
 * @author sylvain
 */
public class SpinExecution extends Execution
{
  private final String m_commandLocation = "spin";
  private final String m_compilerName = "gcc";
  private final String m_tempDir = "/tmp/"; // Must be absolute
  private final String m_tempFilename = "temp.pml";
  
  @Override
  protected String getWorkingDirectory()
  {
    return m_tempDir;
  }
  
  @Override
  protected String[] getCommandLines()
  {
    int i = 0;
    String tmpfn = conditionalQuote(m_tempDir + m_tempFilename);
    String[] cl = new String[5];
    { // cat trace.pml > /tmp/temp.pml
      StringBuilder out = new StringBuilder();
      out.append("cat ").append(conditionalQuote(m_trace)).append(" > ").append(tmpfn).append("; ");
      cl[i++] = out.toString();
    }
    { // spin -F property.pml >> /tmp/temp.pml
      StringBuilder out = new StringBuilder();
      out.append(m_commandLocation).append(" -F ").append(conditionalQuote(m_property)).append(" >> ").append(tmpfn).append("; ");
      cl[i++] = out.toString();
    }
    { // spin -a /tmp/temp.pml
      StringBuilder out = new StringBuilder();
      out.append(m_commandLocation).append(" -a ").append(tmpfn).append("; ");
      cl[i++] = out.toString();
    }
    { // gcc /tmp/pan.c
      StringBuilder out = new StringBuilder();
      out.append(m_compilerName).append(" ").append(m_tempDir).append("pan.c");
      cl[i++] = out.toString();
    }
    { // /tmp/a.out
      StringBuilder out = new StringBuilder();
      out.append(m_tempDir).append("a.out");
      cl[i++] = conditionalQuote(out.toString());
    }
    return cl;
  }

  @Override
  /* package */ ReturnCode parseReturnValue(String strValue)
  {
    // Unreached final state for never claim: negation of
    // the property is true, hence property is false
    if (strValue.contains("unreached in claim"))
      return ReturnCode.FALSE;
    // File not found, meaning Spin could not compile it
    if (strValue.contains("no such"))
      return ReturnCode.ERROR;
    return ReturnCode.TRUE;
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
    return "pml";
  }
  
  @Override
  public String getFormulaExtension()
  {
    return "pml";
  }
}
