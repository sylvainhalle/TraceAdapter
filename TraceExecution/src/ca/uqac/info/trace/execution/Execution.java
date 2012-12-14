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

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
	private int m_returnValue = 0;
	
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

	public final int getReturnValue()
	{
		return m_returnValue;
	}

	/**
	 * Run the tool with the parameters passed, and count the time
	 * and memory consumed.
	 */
	public final void run()
	{
		long start_time = 0, end_time = 0;
		String cmd = getCommandLine();
		StringBuilder strReponse = new StringBuilder();
		Runtime rt = Runtime.getRuntime();
		try
		{
			start_time = System.nanoTime();
			Process process = rt.exec(cmd);
			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(process.getInputStream()));
			BufferedReader bufError = new BufferedReader(new
					InputStreamReader(process.getErrorStream()));
			String line;
			while (((line = stdInput.readLine()) != null)
					|| ((line = bufError.readLine()) != null)) 
			{
				strReponse.append(line);
			}
			end_time = System.nanoTime();

			//Execution time
			m_time = end_time - start_time;

			// Get the memory consumption
			rt.gc();
			m_memory = rt.totalMemory() - rt.freeMemory();

			// Return value
			m_returnValue = parseReturnValue(strReponse.toString());

			// close
			process.destroy();
			stdInput.close();
			bufError.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	/**
	 * Get the command line to run the tool
	 */
	protected abstract String getCommandLine();

	/** 
	 * Parses the return string from the tool (collected from stdout)
	 * and converts it into an appropriate outcome (true, false, etc.)
	 * The outcome is encoded as:
	 * <ul>
	 * <li>0: property is false</li>
	 * <li>1: property is true</li>
	 * <li>-1: property is inconclusive</li>
	 * <li>-99: error during execution</li>
	 * </ul>
	 * @param strValue The string returned by the tool (complete with
	 *     carriage returns if any)
	 * @return The outcome, encoded as an integer
	 */
	/* package */ abstract int parseReturnValue(String strValue) ;

}
