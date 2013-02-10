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
 * @author Sylvain Hallé
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
  public static enum ReturnVerdict {TRUE, FALSE, INCONCLUSIVE, ERROR};

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
  private ReturnVerdict m_returnValue = ReturnVerdict.ERROR;
  
  /**
   * Return code of the executable
   */
  private int m_returnCode = 0;
  
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
   * Whether or not to compute running time only on the
   * last command called, in case there are many commands to call
   * in sequence
   */
  protected boolean m_timeOnlyLastCommand = false;

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

  /**
   * Get the time taken by the tool to run
   * @return The running time (in nanoseconds)
   */
  public final long getTime()
  {
    return m_time;
  }

  /**
   * Get the memory consumed by the tool
   * @return The memory consumed, in bytes
   */
  public final long getMemory()
  {
    return m_memory;
  }

  /**
   * Get the verdict deduced from the tool's return string
   * (i.e. whether the trace fulfills the property, or not)
   * @return
   */
  public final ReturnVerdict getReturnVerdict()
  {
    return m_returnValue;
  }
  
  /**
   * Get the return code of the executable launched
   * @return The return code
   */
  public final int getReturnCode()
  {
    return m_returnCode;
  }
  
  /**
   * Get the string returned by the tool when run
   * @return The return string
   */
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
  public final void run() throws CommandLineException
  {
    long start_time = 0, end_time = 0;
    String[] command_list = getCommandLines();
    String s_cwd = getWorkingDirectory();
    File cwd = null;
    if (!s_cwd.isEmpty())
    {
      cwd = new File(s_cwd);
    }
    if (!m_timeOnlyLastCommand)
    {
    	// Start stopwatch here if we count running time for all commands
    	start_time = System.nanoTime();
    }
    for (int i = 0; i < command_list.length; i++)
    {
      String[] command_to_run = {"bash", "-c", command_list[i]};
      Runtime rt = Runtime.getRuntime();
      try
      {
        if (i != command_list.length - 1) // Not the last command to run
        {
          // Just run it and move on to the next
          Process p = rt.exec(command_to_run, null, cwd);
          m_returnCode = p.waitFor(); // Must wait till command is finished
          if (m_returnCode != 0)
          {
            // Throw an exception in case the command does not
            // exit with return code 0 (indicating an error)
          	throw new CommandLineException(m_returnCode, command_list[i]);
          }
          continue;
        }
        // Last command
        if (m_timeOnlyLastCommand)
        {
        	// Start stopwatch here if we count running time for last command only
        	start_time = System.nanoTime();
        }
        Process p = rt.exec(command_to_run, null, cwd);
        StreamGobbler errorGobbler = new 
            StreamGobbler(p.getErrorStream());            
        // any output?
        StreamGobbler outputGobbler = new 
            StreamGobbler(p.getInputStream());
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
        // any error???
        m_returnCode = p.waitFor();
        // Stop gobblers
        //errorGobbler.
        end_time = System.nanoTime();

        //Execution time
        m_time = end_time - start_time;

        // Get the memory consumption
        rt.gc();
        m_memory = rt.totalMemory() - rt.freeMemory();

        // Return value
        m_returnString = outputGobbler.getString();
        m_errorString = errorGobbler.getString();
        if (!m_returnString.isEmpty())
          m_returnValue = parseReturnString(m_returnString);
        else
          m_returnValue = parseReturnString(m_errorString);
        // For debugging: System.err.println("Response:\n" + strReponse);

        // close
        p.destroy();
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
   * stdin/stdout (and possibly measured for elapsed time; see
   * the {@link Execution#m_timeOnlyLastCommand} field).
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
  /* package */ abstract ReturnVerdict parseReturnString(String strValue) ;

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
  
  /*
   * Taken (and adapted) from:
   * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
   */
  protected class StreamGobbler extends Thread
  {
      InputStream is;
      StringBuilder m_contents = new StringBuilder();
      
      StreamGobbler(InputStream is)
      {
          this.is = is;
      }
      
      public void run()
      {
        try
        {
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr);
          String line=null;
          while ( (line = br.readLine()) != null)
          {
            m_contents.append(line).append("\n");
          }
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();  
        }
      }
      
      public String getString()
      {
        return m_contents.toString();
      }
  }
  
  public class CommandLineException extends Exception
  {
    private static final long serialVersionUID = 1L;
		protected int m_returnCode = 0;
  	protected String m_commandLine = "";
  	
  	public CommandLineException(int returnCode, String commandLine)
  	{
  		m_returnCode = returnCode;
  		m_commandLine = commandLine;
  	}
  }
}
