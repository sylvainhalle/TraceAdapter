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

import java.io.*;

/**
 * Takes care of the execution of various tools in a uniform way.
 * The intended workflow is as follows:
 * <ol>
 * <li>Instantiate a child of Execution (e.g. SaxonExecution)</li>
 * <li>Feed the instance the trace to monitor using {@link setTrace}</li>
 * <li>Feed the instance the property to monitor using {@link setProperty}
 *   (if required)</li>
 * <li>Feed the instance the signature to use using {@link setSignature}
 *   (if required)</li>
 * <li>Launch the tool using {@link run}</li>
 * <li>Collect the outcome, time and memory consumed using
 * {@link getReturnValue}, {@link getTime}, {@link getMemory}</li>
 * </ol>
 * @author sylvain
 *
 */
public abstract class Execution
{
  /**
   * The system's line separator
   */
  protected static final String CRLF = System.getProperty("line.separator");
  
  /**
   * Tools return values
   */
  public static enum ReturnCode {TRUE, FALSE, INCONCLUSIVE, ERROR};

  /**
   * Time consumed by the tool
   */
  private long m_time = 0;

  /**
   * Memory consumed by the tool
   */
  private long m_memory = 0;

  /**
   * Return value of the execution
   */
  private ReturnCode m_returnValue = ReturnCode.ERROR;
  
  /**
   * Return string of the tool
   */
  private String m_returnString = "";
  
  /**
   * Error string of the tool (if any)
   */
  private String m_errorString = "";

  /**
   * Property contents
   */
  protected String m_property = "";

  /**
   * Signature contents
   */
  protected String m_signature = "";

  /**
   * Trace contents
   */
  protected String m_trace = "";
  

  /**
   * Set the signature used for the tool. Depending on the
   * tool, the parameter will denote a path to a file, or the
   * signature itself; see the executor's documentation.
   * @param s The signature
   */
  public final void setSignature(String s)
  {
    m_signature = s;
  }

  /**
   * Set the trace to be verified by the tool. Depending on the
   * tool, the parameter will denote a path to a file, or the
   * trace itself; see the executor's documentation.
   * @param s The trace
   */
  public final void setTrace(String s)
  {
    m_trace = s;
  }

  /**
   * Set the property to be verified by the tool. Depending on the
   * tool, the parameter will denote a path to a file, or the
   * property itself; see the executor's documentation.
   * @param s The signature
   */
  public final void setProperty(String s)
  {
    m_property = s;
  }

  /**
   * Determines if the executor is ready to launch the tool.
   * A value of false indicates that the executor is still
   * waiting for some data before launching.
   * @return True or false depending on the executor
   */
  public abstract boolean isReady();

  public final long getTime()
  {
    return m_time;
  }

  public final long getMemory()
  {
    return m_memory;
  }

  public final ReturnCode getReturnValue()
  {
    return m_returnValue;
  }
  
  public final String getReturnString()
  {
    return m_returnString;
  }
  
  /**
   * Get the extension to be given to trace filenames
   * @return
   */
  public String getTraceExtension()
  {
    return "";
  }
  
  /**
   * Get the extension to be given to signature filenames
   * @return
   */
  public String getSignatureExtension()
  {
    return "";
  }
  
  /**
   * Get the extension to be given to formula filenames
   * @return
   */
  public String getFormulaExtension()
  {
    return "";
  }
  
  protected String getWorkingDirectory()
  {
    return "";
  }

  /**
   * Run the tool with the parameters passed, and count the time
   * and memory consumed.
   */
  public final void run()
  {
    long start_time = 0, end_time = 0;
    String[] command_list = getCommandLines();
    String s_cwd = getWorkingDirectory();
    File cwd = null;
    if (!s_cwd.isEmpty())
    {
      cwd = new File(s_cwd);
    }
    for (int i = 0; i < command_list.length; i++)
    {
      String[] command_to_run = {"bash", "-c", command_list[i]};
      StringBuilder strReponse = new StringBuilder();
      StringBuilder strErr = new StringBuilder();
      Runtime rt = Runtime.getRuntime();
      try
      {
        if (i != command_list.length - 1) // Not the last command to run
        {
          // Just run it and move on to the next
          rt.exec(command_to_run, null, cwd);
          continue;
        }
        // Last command: compute running time and process its output
        start_time = System.nanoTime();
        Process process = rt.exec(command_to_run, null, cwd);
        /*BufferedWriter stdIn = new BufferedWriter(new
            OutputStreamWriter(process.getOutputStream()));*/
        BufferedReader stdOut = new BufferedReader(new
            InputStreamReader(process.getInputStream()));
        BufferedReader stdErr = new BufferedReader(new
            InputStreamReader(process.getErrorStream()));
        // Read data from stdout, line by line
        String in_line = "", err_line = "";
        while (((in_line = stdOut.readLine()) != null)
            || ((err_line = stdErr.readLine()) != null)) 
        {
          if (in_line != null)
            strReponse.append(in_line).append(CRLF);
          if (err_line != null)
            strErr.append(err_line).append(CRLF);
        }
        end_time = System.nanoTime();

        //Execution time
        m_time = end_time - start_time;

        // Get the memory consumption
        rt.gc();
        m_memory = rt.totalMemory() - rt.freeMemory();

        // Return value
        String stReponse = strReponse.toString();
        String stErr = strErr.toString();
        m_errorString = stErr;
        if (!stReponse.toString().isEmpty())
          m_returnValue = parseReturnValue(stReponse);
        else
          m_returnValue = parseReturnValue(stErr);
        // For debugging: System.err.println("Response:\n" + strReponse);

        // close
        process.destroy();
        stdOut.close();
        stdErr.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * Get the command line(s) to run the tool. Each command will be
   * executed in sequence; if more than one
   * command is given, only the <em>last</em> will be checked for
   * stdin/stdout and measured for elapsed time.
   */
  protected abstract String[] getCommandLines();

  /**
   * Get the input file to send to the command's standard input,
   * if any
   * @return The filename, null if nothing must be passed to stdin
   */
  protected abstract String getStdin();

  /** 
   * Parses the return string from the tool (collected from stdout)
   * and converts it into an appropriate outcome (true, false, etc.)
   * @param strValue The string returned by the tool (complete with
   *     carriage returns if any)
   * @return The outcome
   */
  /* package */ abstract ReturnCode parseReturnValue(String strValue) ;

  /**
   * Surrounds a filename by double quotes
   * if it contains spaces, otherwise returns it as is.
   * @param s The filename to handle
   * @return The (potentially escaped) filename
   */
  protected static String conditionalQuote(String s)
  {
    if (s.contains(" "))
      return "\"" + s + "\"";
    return s;
  }
  
  public String getErrorString()
  {
    return m_errorString;
  }
}
