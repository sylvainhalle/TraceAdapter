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

/**
 * Generates a trace of events based on the definition
 * of a Petri net. For more information about Petri nets,
 * see <a href="http://en.wikipedia.org/wiki/Petri_net">Wikipedia's entry</a>.
 * <p>
 * See {@link parseFromString} for a precise definition of the
 * input format to specify the Petri net.
 * @author Sylvain Hallé
 *
 */
public class PetriNetGenerator extends TraceGenerator
{
	/**
	 * The list of places in the Petri net
	 */
	protected List<Place> m_places;

	
	/**
	 * The list of transitions in the Petri net
	 */
	protected List<Transition> m_transitions;
	
	/**
	 * Creates an empty Petri Net Generator
	 */
	public PetriNetGenerator()
	{
		super();
		super.m_random = new Random();
		m_places = new ArrayList<Place>();
		m_transitions = new ArrayList<Transition>();
	}
	
	@Override
	public EventTrace generate()
	{
		if (super.m_clockAsSeed)
			setSeed(System.currentTimeMillis());
		EventTrace trace = new EventTrace();

		RandomPicker<Transition> transition_picker = new RandomPicker<Transition>(super.m_random);
	
    // We choose the number of messages to produce
    int n_messages = super.m_random.nextInt(super.m_maxMessages + 1 - super.m_minMessages) + super.m_minMessages;
    for (int i = 0; i < n_messages; i++)
		{
			if (super.m_verboseLevel > 0)
				System.out.println("Generating message " + i);
		  		// Get enabled transitions
			Vector<Transition> enabled_trans = new Vector<Transition>();
 
			for (Transition trans : m_transitions)
			{
			
				if (trans.isEnabled())
				{

					enabled_trans.add(trans);
				}

			}
    	if (enabled_trans.isEmpty())
    	{
    		// No next state: we are in a dead end
    		break;
    	}
			// Pick one such transition
			Transition t = transition_picker.pick(enabled_trans);
			// Emit event
			Node n = trace.getNode();
			Node n2 = trace.createElement("name");
			Node n3 = trace.createTextNode(t.m_label);
			n2.appendChild(n3);
			n.appendChild(n2);
			
			Event e = new Event(n);
			trace.add(e);
			// Fire transition
			t.fire();
		}
		return trace;
	}

	@Override
	public String getAppName()
	{
		return "Petri Net Generator";
	}

	@SuppressWarnings("static-access")
  @Override
	public Options getCommandLineOptions()
	{
		Options options = super.getCommandLineOptions();
		Option opt;
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set Petri net to x (surrounded by quotes)").create("f");
    options.addOption(opt);
    return options;
	}

	@Override
	public void initialize(CommandLine c_line)
	{
		super.initialize(c_line);
		if (c_line.hasOption("f"))
		{
			String pn_string = c_line.getOptionValue("f");
			try
			{
				parseFromString(pn_string);
			}
			catch (java.text.ParseException e)
			{
				e.printStackTrace(System.err);
			}
		}
	}
	
	/**
	 * Given a place p: finds and returns the element in the list of places
	 * if it exists, or adds it to the list and returns p if it doesn't.
	 * @param p The place to look for
	 * @return
	 */
	private Place getAddPlace(Place p)
	{
		int i = m_places.lastIndexOf(p);
		if (i == -1)
			m_places.add(p);

		else
			p = m_places.get(i);

		return p;
	}
	
	/**
	 * Given a transition t: finds and returns the element in the list of transitions
	 * if it exists, or adds it to the list and returns t if it doesn't.
	 * @param t The transition to look for
	 * @return
	 */
	private Transition getAddTransition(Transition t)
	{
		int i = m_transitions.lastIndexOf(t);
		if (i == -1)
			m_transitions.add(t);
		else
			t = m_transitions.get(i);
		return t;
	}
	
	/**
	 * Defines the Petri net from a structured string.
	 * The string itself is just a succession of "words" separated
	 * by whitespace with the following structure:
	 * <dl>
	 * <dt><tt>T label<sub>1</sub> label<sub>2</sub></tt></dt>
	 * <dd>Defines an arrow from a transition named label<sub>1</sub> to a
	 * place named label<sub>2</sub></dd>
	 * <dt><tt>P label<sub>1</sub> label<sub>2</sub></tt></dt>
	 * <dd>Defines an arrow from a place named label<sub>1</sub> to a
	 * transition named label<sub>2</sub></dd>
	 * <dt><tt>M label<sub>1</sub> x</tt></dt>
	 * <dd>Defines an initial marking by putting <i>x</i> tokens into
	 * a place named label<sub>1</sub></dd>
	 * </dl>
	 * For example, Petri net <i>N</i> shown on
	 * <a href="http://en.wikipedia.org/wiki/Petri_net">Wikipedia's entry</a>
	 * would be represented as:
	 * <pre>
	 * T T1 p1
	 * P p1 T2
	 * T T2 p2
	 * P p2 T3
	 * M p1 0
	 * M p2 0
	 * </pre>
	 * Carriage returns are optional, the whole text could have been
	 * put on a single line. The order in which lines are input does not
	 * matter: transitions and places are created the first time their
	 * label is seen by the parser.
	 * <p>
	 * <em>Caveat emptor</em>: the parser assumes the string is well-formed
	 * but does not check it. A malformed string will likely cause a runtime
	 * error of some kind.
	 * @param s The structured string
	 */
	protected void parseFromString(String s) throws java.text.ParseException
	{
		String[] elements = s.split("\\s+");
		int i = 0;
		while (i < elements.length)
		{
			String word = elements[i];
			if (word.compareTo("P") == 0)
			{
				// Definition of a place -> transition arrow
				assert i + 2 < elements.length;
				String place_label = elements[i + 1];
				String trans_label = elements[i + 2];
				Place p = getAddPlace(new Place(place_label));
				Transition t = getAddTransition(new Transition(trans_label));
				p.addOutgoingTransition(t);
				t.addIncomingPlace(p);
				i += 3;
			}
			else if (word.compareTo("T") == 0)
			{
				// Definition of a transition -> place arrow
				assert i + 2 < elements.length;
				String place_label = elements[i + 2];
				String trans_label = elements[i + 1];
				Place p = getAddPlace(new Place(place_label));
				Transition t = getAddTransition(new Transition(trans_label));
				p.addIncomingTransition(t);
				t.addOutgoingPlace(p);
				i += 3;
			}
			else if (word.compareTo("M") == 0)
			{
				// Definition of an initial marking
				assert i + 2 < elements.length;
				String place_label = elements[i + 1];
				int value = Integer.parseInt(elements[i + 2]);
				Place p = getAddPlace(new Place(place_label));
				p.setMarking(value);
				i += 3;
			}
			else
			{
				throw new java.text.ParseException("Unknown word " + word, i);
			}
		}
	}
	
	/**
	 * A place in a Petri net is a graph node that can contain zero or more
	 * <em>tokens</em>. It is linked via incoming and outgoing arrows to
	 * transitions.
	 * @author Sylvain Hallé
	 *
	 */
	protected class Place
	{
		protected Set<Transition> m_incoming;
		protected Set<Transition> m_outgoing;
		protected int m_marking = 0;
		protected String m_label;

		
		public Place()
		{
			super();
			m_label = "";
			m_incoming = new HashSet<Transition>();
			m_outgoing = new HashSet<Transition>();
		}
		
		public Place(String label)
		{
			this();
			assert label != null;
			m_label = label;
		}
		
		public void addIncomingTransition(Transition t)
		{
			m_incoming.add(t);
		}
		
		public void addOutgoingTransition(Transition t)
		{
			m_outgoing.add(t);
		}
		
		public void setMarking(int value)
		{
			m_marking = value;
		}
		
		public void put()
		{
			m_marking++;
		}
		
		public void consume()
		{
			m_marking--;
		}
		
		public boolean isEmpty()
		{
			return m_marking == 0;
		}
		
		@Override
		public int hashCode()
		{
			return m_label.hashCode();  
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof Place))
				return false;
			return equals((Place) o);
		}
		
		public boolean equals(Place p)
		{
			if (p == null)
				return false;
			return m_label.compareTo(p.m_label) == 0;
		}
	}
	
	/**
	 * A transition in a Petri net is a graph node that is linked via
	 * incoming and outgoing arrows to places.
	 * A transition of a Petri net may <em>fire</em> whenever there are sufficient
	 * tokens in the places from all incoming arcs; when it fires, it consumes
	 * these tokens, and places tokens at the end of all output arcs.
	 * @author Sylvain Hallé
	 *
	 */
	protected class Transition
	{
		protected String m_label;
		protected Set<Place> m_incoming;
		protected Set<Place> m_outgoing;
		
		public Transition()
		{
			super();
			m_label = "";
			m_incoming = new HashSet<Place>();
			m_outgoing = new HashSet<Place>();
		}
		
		public Transition(String label)
		{
			this();
			assert label != null;
			m_label = label;
		}
		
		public void addIncomingPlace(Place p)
		{
			m_incoming.add(p);
		}
		
		public void addOutgoingPlace(Place p)
		{
			m_outgoing.add(p);
		}
		
		public boolean isEnabled()
		{
			for (Place p : m_incoming)
				if (p.isEmpty())
					return false;
			return true;
		}
		
		public void fire()
		{
			assert isEnabled();
			for (Place p : m_incoming)
				p.consume();
			for (Place p : m_outgoing)
				p.put();
		}
		
		@Override
		public int hashCode()
		{
			return m_label.hashCode();  
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof Transition))
				return false;
			return equals((Transition) o);
		}
		
		public boolean equals(Transition t)
		{
			if (t == null)
				return false;
			return m_label.compareTo(t.m_label) == 0;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		process(args, new PetriNetGenerator());
	}

}
