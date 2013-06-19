/******************************************************************************
  Event trace generator
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
package ca.uqac.info.trace.generation;

import java.util.*;

import org.apache.commons.cli.*;
import org.w3c.dom.*;

import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;

/**
 * Randomly produces traces of events based on a number of parameters.
 * More precisely, the RandomTraceGenerator takes as parameters:
 * <ol>
 * <li>The interval for the number of messages to produce</li>
 * <li>The maximum arity of each message</li>
 * <li>The number of available parameter names</li>
 * <li>The maximum domain size for all parameters</li>
 * <li>Whether each event can be multi-valued or not</li>
 * </ol>
 * Calls to {@link generate} produce message traces where values
 * for each parameter are randomly chosen from a uniform distribution
 * in the interval provided.
 * @author Sylvain Hallé
 *
 */
public class RandomTraceGenerator extends TraceGenerator
{    
  /**
   * Intervals for all the aforementioned parameters
   */
  protected int m_minArity = 1;
  protected int m_maxArity = 5;
  protected int m_numParameters = 5;
  protected int m_domainSize = 5;
  
  /**
   * Determines whether the events should be multi-valued
   */
  protected boolean m_isMultiValued = false;
  
  /**
   * Whether to remove parameters from events in the case where only
   * one parameter is required. If this is set to true, a message like
   * this:
   * <pre>
   * &lt;Event&gt;
   *   &lt;p0&gt;value&lt;/p0&gt;
   * &lt;/Event&gt;
   * </pre>
   * will be rather written as: 
   * <pre>
   * &lt;Event&gt;value&lt;/Event&gt;
   * </pre>
   */
  protected boolean m_flatten = false;
  
  /**
   * Main loop for generator
   * @param args
   */
  public static void main(String args[])
  {
    process(args, new RandomTraceGenerator());
  }
  
  /**
   * Creates a trace generator with default settings
   */
  public RandomTraceGenerator()
  {
    m_random = new Random();
    m_minMessages = 1;
    m_maxMessages = 10;
  }
  
  /**
   * Sets the seed used by the internal random number generator.
   * Starting from the same seed and the same input parameters, successive
   * calls to generate will always generate the same sequence of
   * output traces.
   * @param s
   */
  public void setSeed(long s)
  {
    m_seed = s;
    m_random.setSeed(s);
  }
  
  /**
   * Specifies the interval in which the generator will choose the
   * number of messages to produce in the output trace. 
   * @param min Minimum value
   * @param max Maximum value
   */
  public void setNumMessages(int min, int max)
  {
    assert min <= max;
    assert min >= 1;
    m_minMessages = min;
    m_maxMessages = max;
  }
  
  /**
   * Specifies whether the events to produce can be multi-valued.
   * @see {@link ca.uqac.info.trace.Event.isMultiValued}
   * @param b True if events can be multi-valued, false otherwise 
   */
  public void setMultiValued(boolean b)
  {
    m_isMultiValued = b;
  }
  
  /**
   * Specifies the interval in which the generator will choose the
   * arity for each message to produce in the output trace. 
   * @param min Minimum value
   * @param max Maximum value
   */
  public void setArity(int min, int max)
  {
    assert min <= max;
    assert min >= 1;
    m_minArity = min;
    m_maxArity = max;
  }
  
  /**
   * Specifies the number of available parameters to choose from when
   * building an event in the output trace.
   * <p> 
   * <i>Caveat emptor</i>: you will run into a problem if num_parameters &lt; max_arity,
   * and multivalued = false. This effectively tells the generator to
   * produce messages with a number of param-value pairs greater than
   * the number of available parameters, yet forbids it to repeat
   * parameters. No check is made about that in the code. 
   * @param n Number of parameters
   */
  public void setNumParameters(int n)
  {
    assert n >= 1;
    m_numParameters = n;
  }

  @Override
  public EventTrace generate()
  {
    EventTrace trace = new EventTrace();
    // We choose the number of messages to produce
    int n_messages = super.nextInt(m_maxMessages - m_minMessages) + m_minMessages;
    for (int i = 0; i < n_messages; i++)
    {
      Node n = trace.getNode();
      Vector<String> available_params = new Vector<String>();
      // We produce the list of available parameters for this message
      for (int j = 0; j < m_numParameters; j++)
        available_params.add("p" + j);
      // We choose the number of param-value pairs to generate
      int arity = super.nextInt(m_maxArity - m_minArity) + m_minArity;
      for (int j = 0; j < arity; j++)
      {
        // We generate as many param-value pairs as required
        int index = super.nextInt(available_params.size());
        int value = super.nextInt(m_domainSize);
        if (m_minArity == 1 && m_maxArity == 1 && m_flatten)
        {
        	// For traces of messages with fixed arity = 1, we
        	// simply put the value as the text child of the "Event"
        	// element
        	n.appendChild(trace.createTextNode(new Integer(value).toString()));
        }
        else
        {
        	Node el = trace.createElement("p" + index);
        	el.appendChild(trace.createTextNode(new Integer(value).toString()));
        	n.appendChild(el);
        	if (!m_isMultiValued)
        	{
        		// If event should not be multi-valued, then we remove
        		// the chosen parameter from the list of available choices
        		available_params.removeElementAt(index);
        	}
        }
      }
      Event e = new Event(n);
      trace.add(e);
    }
    return trace;
  }

  @SuppressWarnings("static-access")
  @Override
  public Options getCommandLineOptions()
  {
    Options options = new Options();
    Option opt;
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Maximum arity").create("A");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Minimum arity").create("a");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Maximum number of messages to produce").create("N");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Minimum number of messages to produce").create("n");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Number of available parameters").create("p");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set random seed").create("s");
    options.addOption(opt);
    options.addOption("M", false, "Messages can be multi-valued");
    options.addOption("m", false, "Messages cannot be multi-valued");
    return options;
  }

  @Override
  public void initialize(CommandLine c_line)
  {
    if (c_line.hasOption("a"))
      m_minArity = new Integer(c_line.getOptionValue("a")).intValue();
    if (c_line.hasOption("A"))
      m_maxArity = new Integer(c_line.getOptionValue("A")).intValue();
    if (c_line.hasOption("n"))
      m_minMessages = new Integer(c_line.getOptionValue("n")).intValue();
    if (c_line.hasOption("N"))
      m_maxMessages = new Integer(c_line.getOptionValue("N")).intValue();
    if (c_line.hasOption("p"))
      setNumParameters(new Integer(c_line.getOptionValue("p")).intValue());
    if (c_line.hasOption("s"))
      setSeed(new Integer(c_line.getOptionValue("s")).intValue());
    if (c_line.hasOption("m"))
      m_isMultiValued = false;
    if (c_line.hasOption("M"))
      m_isMultiValued = true;
  }

  @Override
  public String getAppName()
  {
    return "Random trace generator";
  }

}
