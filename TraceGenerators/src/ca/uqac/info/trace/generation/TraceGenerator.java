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


import java.util.Random;

import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.conversion.Translator;
import ca.uqac.info.trace.conversion.XmlTranslator;
import org.apache.commons.cli.*;

/**
 * The TraceGenerator defines the basic methods one can use to produce
 * event traces of various kinds. It also provides functionality to
 * read command-line arguments, produce an output trace and initialize
 * a random number generator.
 * @author sylvain
 *
 */
public abstract class TraceGenerator
{  

	/**
	 * Intervals for all the aforementioned parameters
	 */
	protected int m_minMessages = 1;
	protected int m_maxMessages = 10;

	/**
	 * The seed used to initialize the generator
	 */
	protected long m_seed = 0;

	/**
	 * The random number generator
	 */
	protected Random m_random;

	/**
	 * Whether to use the system clock as the seed
	 */
	protected boolean m_clockAsSeed = false;
	
	/**
	 * Generator's level of verbosity; defaults to 0 (that is,
	 * don't display any message apart from the output trace)
	 */
	protected int m_verboseLevel = 0;

	/**
	 * Generate an event trace. There are no basic assumptions on that
	 * trace: it may be the same on every call to generate, or different.
	 * It is up to each trace generator to determine the appropriate
	 * behaviour for this method.
	 * @return The EventTrace produced by the generator
	 */
	public abstract EventTrace generate();

	/**
	 * Initializes the generator, taking its input parameters from the
	 * parsed command line
	 * @param c_line An instance of the parsed CommandLine, which can
	 * be queried for values gathered from the user 
	 */
	public void initialize(CommandLine c_line)
	{
		if (c_line.hasOption("t"))
			m_clockAsSeed = true;
		if (c_line.hasOption("s"))
			setSeed(new Integer(c_line.getOptionValue("s")).intValue());
		if (c_line.hasOption("n"))
		{
			m_minMessages = new Integer(c_line.getOptionValue("n")).intValue();
			if (m_minMessages > m_maxMessages)
				m_maxMessages = m_minMessages + 1;
		}
		if (c_line.hasOption("N"))
		{
			m_maxMessages = new Integer(c_line.getOptionValue("N")).intValue();
			if (m_maxMessages < m_minMessages)
				m_minMessages = Math.max(0, m_maxMessages - 1);
		}
		if (c_line.hasOption("verbose"))
			m_verboseLevel = Integer.parseInt(c_line.getOptionValue("verbose"));
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
	 * Returns an identifying string used by the command-line application
	 * to designate the trace generator
	 * @return The name
	 */
	public abstract String getAppName();

	/**
	 * Creates the proper command line options to gather parameters
	 * relevant to the configuration of the trace generator. This
	 * method must be implemented by all trace generators, in order
	 * for them to declare their command line switches.
	 * @return The Options object containing the options required by
	 * this generator
	 */
	@SuppressWarnings("static-access")
  public Options getCommandLineOptions()
	{
		Options options = new Options();
		Option opt;
		options.addOption("t", false, "Use system clock as random generator's seed (default: no)");
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set random generator's seed to x (default: 0)").create("s");
		options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Maximum number of messages to produce (default: 10)").create("N");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Minimum number of messages to produce (default: 1)").create("n");
    options.addOption(opt);
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set verbosity level to x (default: 0 = no messages)").withLongOpt("verbose").create();
    options.addOption(opt);
    return options;
	}

	/**
	 * Main processing loop for generating traces.
	 * @param args
	 * @param t_gen
	 */
	public static final void process(String[] args, TraceGenerator t_gen)
	{    
		Options options = t_gen.getCommandLineOptions();
		options.addOption("h", "help", false, "Show help");
		CommandLineParser parser = new PosixParser();
		CommandLine c_line = null;
		try
		{
			// parse the command line arguments
			c_line = parser.parse(options, args);
		}
		catch (ParseException exp)
		{
			// oops, something went wrong
			System.err.println("ERROR: " + exp.getMessage() + "\n");
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(t_gen.getAppName() + " [options]", options);
			System.exit(1);
		}
		assert c_line != null;
		if (c_line.hasOption("h"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(t_gen.getAppName() + " [options]", options);
			System.exit(1);
		}
		t_gen.initialize(c_line);
		EventTrace trace = t_gen.generate();
		Translator trans = new XmlTranslator();
		System.out.println(trans.translateTrace(trace));
	}
	
	/**
	 * Overrides Random's {@link Random.nextInt} to add the corner case where
	 * the range is 0
	 * @param val
	 * @return
	 */
	protected int nextInt(int val)
	{
		if (val == 0)
			return 0;
		return m_random.nextInt(val);
	}
}
