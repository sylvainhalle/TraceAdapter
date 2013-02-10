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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.w3c.dom.Node;

import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.RandomPicker;
import ca.uqac.info.util.Relation;

/**
 * Generates a trace of events based on the definition
 * of a finite-state machine.
 * @author Sylvain Hallé
 *
 */
public class FsmGenerator extends TraceGenerator
{
	/**
	 * The relation containing all triplets of the FSM
	 */
	protected Relation<String,FsmTriplet> m_triplets;
		
	/**
	 * Initial state label
	 */
	protected String m_initialStateLabel = "";
	
	public FsmGenerator()
	{
		super();
		super.m_random = new Random();
		m_triplets = new Relation<String,FsmTriplet>();
	}

	@Override
	public EventTrace generate()
	{
		if (super.m_clockAsSeed)
			setSeed(System.currentTimeMillis());
		EventTrace trace = new EventTrace();
		String current_state = m_initialStateLabel;
		RandomPicker<FsmTriplet> triplet_picker = new RandomPicker<FsmTriplet>(m_random);
    // We choose the number of messages to produce
    int n_messages = super.m_random.nextInt(super.m_maxMessages + 1 - super.m_minMessages) + super.m_minMessages;
    for (int i = 0; i < n_messages; i++)
    {
    	// Gather possible next states
    	Set<FsmTriplet> triplets = m_triplets.get(current_state);
    	if (triplets == null || triplets.size() == 0)
    	{
    		// No next state: we are in a dead end
    		break;
    	}
    	// Pick one next state
    	FsmTriplet t = triplet_picker.pick(triplets);
    	// Emit event
    	Node n = trace.getNode();
    	n.appendChild(trace.createTextNode(t.m_label));
    	Event e = new Event(n);
    	trace.add(e);
    	// Set new current state
    	current_state = t.m_to;
    }
    return trace;
	}

	@Override
	public String getAppName()
	{
		return "FSM Generator";
	}

	@SuppressWarnings("static-access")
  @Override
	public Options getCommandLineOptions()
	{
		Options options = super.getCommandLineOptions();
		Option opt;
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set finite-state machine to x (surrounded by quotes)").create("f");
    options.addOption(opt);
    return options;
	}

	@Override
	public void initialize(CommandLine c_line)
	{
		super.initialize(c_line);
		if (c_line.hasOption("f"))
		{
			String fsm_string = c_line.getOptionValue("f");
			parseFromString(fsm_string);
		}
	}
	
	/**
	 * Defines the finite-state machine from a structured string.
	 * The string itself is just a succession of triplets separated
	 * by spaces. See {@link FsmGenerator.FsmTriplet(String)} for a definition
	 * of each triplet's format.
	 * <p>
	 * By definition, the FSM's initial state label is the "from" label
	 * of the first triplet. It may occur in more than one triplet (that is,
	 * the FSM need not be deterministic).
	 * @param fsmString The structured string
	 */
	protected void parseFromString(String fsmString)
	{
		String[] elements = fsmString.split("\\s+");
		for (int i = 0; i < elements.length - 2; i += 3)
		{
			String from = elements[i];
			String label = elements[i + 1];
			String to = elements[i + 2];
			FsmTriplet t = new FsmTriplet(from, label, to);
			m_triplets.add(from, t);
			if (i == 0)
				m_initialStateLabel = from;
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		process(args, new FsmGenerator());
	}
	
	/**
	 * Representation of a finite-state machine edge between
	 * two states. A triplet contains:
	 * <ol>
	 * <li>The source state label</li>
	 * <li>The edge label</li>
	 * <li>The destination state label</li>
	 * </ol>
	 * A set of such triplets defines a finite-state machine.
	 * @author Sylvain Hallé
	 *
	 */
	protected class FsmTriplet
	{
		protected String m_from;
		protected String m_to;
		protected String m_label;
		
		/**
		 * Builds a triplet from a structured string. The string format
		 * is:
		 * <pre>
		 * s_label e_label d_label
		 * </pre>
		 * where each element represents the source, edge and destination
		 * label, respectively, separated by any number of whitespace. 
		 * @param s
		 */
		public FsmTriplet(String s)
		{
			String[] parts = s.split("\\s+");
			assert parts.length == 3;
			m_from = parts[0];
			m_label = parts[1];
			m_to = parts[2];
		}
		
		/**
		 * Builds a triplet by specifying its three elements.
		 * @param from Source label
		 * @param label Edge label
		 * @param to Destination label
		 */
		public FsmTriplet(String from, String label, String to)
		{
			super();
			assert to != null && label != null & from != null;
			m_from = from;
			m_to = to;
			m_label = label;
		}
		
		@Override
		public int hashCode()
		{
			return m_from.length() + m_to.length() + m_label.length();
		}
		
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof FsmTriplet))
				return false;
			return equals((FsmTriplet) o);
		}
		
		public boolean equals(FsmTriplet t)
		{
			if (t == null)
				return false;
			return (m_from.compareTo(t.m_from) == 0) &&
			(m_to.compareTo(t.m_to) == 0) && (m_label.compareTo(t.m_label) == 0);
		}
	}

}
