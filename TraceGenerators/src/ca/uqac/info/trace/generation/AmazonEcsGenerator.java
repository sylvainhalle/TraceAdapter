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

import ca.uqac.info.trace.EventTrace;

/**
 * Generate a random trace of Amazon ECS shopping cart requests and responses.
 * The constraints followed by the sequence of messages are detailed in:
 * <blockquote>
 * S. Hallé, R. Villemaire. (2012). Constraint-Based Invocation of Stateful Web
 * Services: the Beep Store (Case Study). In
 * <cite>4th International Workshop on
 * Principles of Engineering Service-Oriented Systems (PESOS 2012)</cite>.
 * </blockquote>
 * @author sylvain
 *
 */
public class AmazonEcsGenerator extends TraceGenerator
{

	/**
	 * The seed used to initialize the generator
	 */
	protected long m_seed = 0;

	/**
	 * The random number generator
	 */
	protected Random m_random;

	/**
	 * Intervals for all the aforementioned parameters
	 */
	protected int m_minMessages = 1;
	protected int m_maxMessages = 10;
	protected int m_maxCartSize = 10;
	protected int m_catalogSize = 10;
	protected int m_maxCarts = 1;

	/**
	 * Whether to use the system clock as the seed
	 */
	protected boolean m_clockAsSeed = false;

	/**
	 * Creates a trace generator with default settings
	 */
	public AmazonEcsGenerator()
	{
		m_random = new Random();
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

	@Override
	public EventTrace generate()
	{
		if (m_clockAsSeed)
			setSeed(System.currentTimeMillis());
		EventTrace trace = new EventTrace();
		Vector<Cart> carts = new Vector<Cart>();
    // We choose the number of messages to produce
    int n_messages = m_random.nextInt(m_maxMessages - m_minMessages) + m_minMessages;
    for (int i = 0; i < n_messages; i++)
    {
    	Vector<String> available_operations = new Vector<String>();
    	// Step 1: determine which operations are available
    	available_operations.add("create_item_search");
    	if (carts.size() < m_maxCarts)
    	{
    		available_operations.add("create_cart_create");
    	}
    }
		return trace;
	}

	@Override
	public String getAppName()
	{
		return "Amazon ECS Shopping Cart Generator";
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getCommandLineOptions()
	{
		Options options = new Options();
		Option opt;
		options.addOption("t", false, "Use system clock as random generator's seed (default: no)");
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set random generator's seed to x (default: 0)").create("s");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set maximum number of simultaneous shopping carts to x (default: 1)").create("C");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set store's catalog size to x (default: 10)").create("i");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set each cart's maximum size to x (default: 10)").create("k");
		options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Maximum number of messages to produce").create("N");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Minimum number of messages to produce").create("n");
    options.addOption(opt);
		return options;
	}

	@Override
	public void initialize(CommandLine c_line)
	{
		if (c_line.hasOption("s"))
			setSeed(new Integer(c_line.getOptionValue("s")).intValue());
		if (c_line.hasOption("t"))
			m_clockAsSeed = true;
		if (c_line.hasOption("C"))
			m_maxCarts = new Integer(c_line.getOptionValue("C")).intValue();
		if (c_line.hasOption("n"))
			m_minMessages = new Integer(c_line.getOptionValue("n")).intValue();
		if (c_line.hasOption("N"))
			m_maxMessages = new Integer(c_line.getOptionValue("N")).intValue();
		if (c_line.hasOption("k"))
			m_maxCartSize = new Integer(c_line.getOptionValue("k")).intValue();
		if (c_line.hasOption("i"))
			m_catalogSize = new Integer(c_line.getOptionValue("i")).intValue();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		process(args, new AmazonEcsGenerator());
	}
	
	private int cart_id_count = 0;
	
	/**
	 * Basic implementation of an Amazon shopping cart
	 * @author sylvain
	 *
	 */
	private class Cart extends HashMap<Item,Integer>
	{
		private int m_id = 0;
		
		private Random m_randomGen;
		
		public Cart()
		{
			super();
			m_id = cart_id_count++;
			m_randomGen = AmazonEcsGenerator.this.m_random;
		}
		
		/**
		 * Randomly pick an item from the cart.
		 * This method uses the random number generator from the container
		 * classe (AmazonEcsGenerator).
		 * @return
		 */
		public Item pick()
		{
			assert size() > 0;
			Vector<Item> items = new Vector<Item>();
			items.addAll(super.keySet());
			int index = m_randomGen.nextInt(items.size());
			return items.elementAt(index);
		}
		
		public int getId()
		{
			return m_id;
		}
		
		public int hashCode()
		{
			return m_id;
		}
		
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof Cart))
				return false;
			return equals((Cart) o);
		}
		
		public boolean equals(Cart c)
		{
			if (c == null)
				return false;
			return m_id == c.m_id;
		}
	}
	
	/**
	 * Basic implementation of a cart item
	 * @author sylvain
	 *
	 */
	private class Item
	{
		protected int m_id = 0;
		protected int m_price =0 ;
		
		public Item(int id, int price)
		{
			super();
			m_id = id;
			m_price = price;
		}
		
		public int getId()
		{
			return m_id;
		}
		
		public int getPrice()
		{
			return m_price;
		}
		
		public int hashCode()
		{
			return m_id;
		}
		
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof Item))
				return false;
			return equals((Item) o);
		}
		
		public boolean equals(Item c)
		{
			if (c == null)
				return false;
			return m_id == c.m_id;
		}
	}

}
